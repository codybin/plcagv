package com.xintai.vehicle.comadpter;

import com.google.inject.assistedinject.Assisted;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.xintai.kecong.mesaage.adapter.OpentcsPointToKeCongPoint;
import com.xintai.kecong.message.ConnectionEventListener;
import com.xintai.kecong.message.DataConvertUtl;
import com.xintai.kecong.message.KeCongActionVar;
import com.xintai.kecong.message.rqst.KeCongComandNavigateControl;
import com.xintai.kecong.message.rqst.KeCongComandRead;
import com.xintai.kecong.message.KeCongComandReadResponse;
import com.xintai.kecong.message.rqst.KeCongComandSerachRobotStatue;
import com.xintai.kecong.message.rqst.KeCongComandSwitchAutoOrManul;
import com.xintai.kecong.message.rqst.KeCongComandWrite;
import com.xintai.kecong.message.KeCongCommandResponse;
import com.xintai.kecong.message.KeCongRequestMessage;
import com.xintai.kecong.message.KeCongRobotStatuSearchResponse;
import com.xintai.kecong.modbusvar.TruckData;
import com.xintai.kecong.model.ReadVarModel;
import com.xintai.kecong.model.RobotStatuResponseModel;
import com.xintai.kecong.robotutl.RobotUtl;
import com.xintai.kecong.task.VehicleActuralTask;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.util.ExplainedBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.xintai.kecong.udp.UdpClientManager;
import com.xintai.modbus.ModbusProcolCharge;
import static com.xintai.vehicle.comadpter.BoundedCounter.UINT16_MAX_VALUE;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.data.TCSObject;
import org.opentcs.data.TCSObjectEvent;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.drivers.vehicle.LoadHandlingDevice;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.opentcs.util.CyclicTask;
import org.opentcs.util.event.EventBus;
import org.opentcs.util.event.EventHandler;

public class KeCongCommAdapter
    extends BasicVehicleCommAdapter
    implements EventHandler,
               ConnectionEventListener<KeCongCommandResponse>,
               TelegramSender {

  private static final Logger LOG = LoggerFactory.getLogger(KeCongCommAdapter.class);
  private final KeCongAdapterComponentsFactory componentsFactory;
  private final ExecutorService kernelExecutor;
  private StateRequesterTask stateRequesterTask;
  private RequestResponseMatcher requestResponseMatcher;
  private static final Set<String> ACTION_SET = new HashSet<>();
  private boolean initialized;
  public static final String LHD_NAME = "default";
  private UdpClientManager<KeCongRequestMessage> udpclientmanager;
  private LoadState loadState = LoadState.EMPTY;
  private final Map<MovementCommand, Integer> orderIds = new ConcurrentHashMap<>();

  public Map<MovementCommand, Integer> GetOrderIds() {
    return orderIds;
  }
  private CyclicTask vehicleActuralCyclicTask;
  private ModbusProcolCharge modbusProcolCharge;//充电机
  private static final String LOAD_OPERATION_CONFLICT = "cannotLoadWhenLoaded";
  private static final String UNLOAD_OPERATION_CONFLICT = "cannotUnloadWhenNotLoaded";
  private final Vehicle vehicle;
  private final EventBus eventBus;

  @Inject

  public KeCongCommAdapter(@Assisted Vehicle vehicle,
                           KeCongAdapterComponentsFactory componentsFactory,
                           @KernelExecutor ExecutorService kernelExecutor,
                           @Nonnull @ApplicationEventBus EventBus eventBus) {
    super(new KeCongProcessModel(vehicle), 3, 2, LoadAction.CHARGE, kernelExecutor);
    this.vehicle = requireNonNull(vehicle, "vehicle");
    this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");
    this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
    this.eventBus = requireNonNull(eventBus, "eventBus");
  }
  private Byte count = 0;
  private boolean connected = false;
  private boolean isconnected = false;

  private void processObjectEvent(TCSObjectEvent event) {
    TCSObject<?> object = event.getCurrentOrPreviousObjectState();
    if (object instanceof TransportOrder) {
      processOrderEvent(event);
    }
    if (object instanceof Vehicle) {
      processVehicleEvent(event);
    }
  }

  @Override
  protected synchronized boolean canSendNextCommand() {
    /*  if(getProcessModel().isIscharging())
    {
    getProcessModel().setVehicleState(Vehicle.State.IDLE);
    while(getProcessModel().isIscharging())
    {
    try {
    Thread.sleep(1000);
    }
    catch (InterruptedException ex) {
    java.util.logging.Logger.getLogger(KeCongCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
    }*/
    System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.canSendNextCommand()" + String.valueOf(getProcessModel().isIscharging()));
    return super.canSendNextCommand() & !getProcessModel().isSingleStepModeEnabled();
  }

  private void processOrderEvent(TCSObjectEvent event) {
    if (event.getPreviousObjectState() == null || event.getCurrentObjectState() == null) {
      // We cannot compare two states to find out what happened - ignore.
      return;
    }

    TransportOrder orderOld = (TransportOrder) event.getPreviousObjectState();
    TransportOrder orderNow = (TransportOrder) event.getCurrentObjectState();

    // Has the order been activated?
    if (orderNow.hasState(TransportOrder.State.ACTIVE)
        && !orderOld.hasState(TransportOrder.State.ACTIVE)) {
      //    writeEvent(StatisticsEvent.ORDER_ACTIVATED, orderNow.getName());
    }
    // Has the order been assigned to a vehicle?
    if (orderNow.hasState(TransportOrder.State.BEING_PROCESSED)
        && !orderOld.hasState(TransportOrder.State.BEING_PROCESSED)) {
      //writeEvent(StatisticsEvent.ORDER_ASSIGNED, orderNow.getName());
    }
    // Has the order been finished?
    if (orderNow.hasState(TransportOrder.State.FINISHED)
        && !orderOld.hasState(TransportOrder.State.FINISHED)) {
      //writeEvent(StatisticsEvent.ORDER_FINISHED_SUCC, orderNow.getName());
      // Check the order's deadline. Has it been crossed?
      if (orderNow.getFinishedTime().isAfter(orderNow.getDeadline())) {
        //writeEvent(StatisticsEvent.ORDER_CROSSED_DEADLINE, orderNow.getName());
      }
    }
    // Has the order failed?
    if (orderNow.hasState(TransportOrder.State.FAILED)
        && !orderOld.hasState(TransportOrder.State.FAILED)) {
      //  writeEvent(StatisticsEvent.ORDER_FINISHED_FAIL, orderNow.getName());
    }
  }

  private void processVehicleEvent(TCSObjectEvent event) {
    if (event.getPreviousObjectState() == null || event.getCurrentObjectState() == null) {
      // We cannot compare two states to find out what happened - ignore.
      return;
    }

    Vehicle vehicleOld = (Vehicle) event.getPreviousObjectState();
    Vehicle vehicleNow = (Vehicle) event.getCurrentObjectState();

    // Did the vehicle get a transport order?
    if (vehicleNow.getTransportOrder() != null && vehicleOld.getTransportOrder() == null) {
      // writeEvent(StatisticsEvent.VEHICLE_STARTS_PROCESSING, vehicleNow.getName());
    }
    // Did the vehicle finish a transport order?
    if (vehicleNow.getTransportOrder() == null && vehicleOld.getTransportOrder() != null) {
      //  writeEvent(StatisticsEvent.VEHICLE_STOPS_PROCESSING, vehicleNow.getName());
    }
    // Did the vehicle start charging?
    if (vehicleNow.hasState(Vehicle.State.CHARGING)
        && !vehicleOld.hasState(Vehicle.State.CHARGING)) {
      //补充开始充电的逻辑
      startcharge();
      // writeEvent(StatisticsEvent.VEHICLE_STARTS_CHARGING, vehicleNow.getName());
    }
    // Did the vehicle start charging?
    if (!vehicleNow.hasState(Vehicle.State.CHARGING)
        && vehicleOld.hasState(Vehicle.State.CHARGING)) {
      stopcharge();
    }
    // If the vehicle is processing an order AND is not in state EXECUTING AND
    // it was either EXECUTING before or not processing, yet, consider it being
    // blocked.
    if (vehicleNow.hasProcState(Vehicle.ProcState.PROCESSING_ORDER)
        && !vehicleNow.hasState(Vehicle.State.EXECUTING)
        && (vehicleOld.hasState(Vehicle.State.EXECUTING)
            || !vehicleOld.hasProcState(Vehicle.ProcState.PROCESSING_ORDER))) {
      // writeEvent(StatisticsEvent.VEHICLE_STARTS_WAITING, vehicleNow.getName());
    }
    // Is the vehicle processing an order AND has its state changed from
    // something else to EXECUTING? - Consider it not blocked any more, then.
    if (vehicleNow.hasProcState(Vehicle.ProcState.PROCESSING_ORDER)
        && vehicleNow.hasState(Vehicle.State.EXECUTING)
        && !vehicleOld.hasState(Vehicle.State.EXECUTING)) {
      // writeEvent(StatisticsEvent.VEHICLE_STOPS_WAITING, vehicleNow.getName());
    }
  }

  private void startcharge() {
    Thread startchargeThread = new Thread() {
      @Override
      public void run() {
        try {
          modbusProcolCharge.StartCharge(8, true);
          byte data = 0;
          while (data != 2)//等待充电头伸出
          {
            data = modbusProcolCharge.GetChargingHeadState(23, 4);
          }
          getProcessModel().setIscharging(true);
          //补充停止充电的逻辑
          // 1.首先判断叉车的当前点，
          //2.获得当前点连接的位置。
          //3.判断是否是充电位置，如果是，继续
          //4.充电机停止充电，并且伸出的充电头撤回。
          //5.执行完该操作后，再进行下面的工作。
          // writeEvent(StatisticsEvent.VEHICLE_STOPS_CHARGING, vehicleNow.getName());
        }
        catch (ModbusTransportException ex) {
          java.util.logging.Logger.getLogger(KeCongCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };
    startchargeThread.start();
  }

  private void stopcharge() {
    Thread stopchargeThread = new Thread() {
      @Override
      public void run() {
        try {
          modbusProcolCharge.StopCharge(7, true);
          byte data = 0;
          while (data != 4)//等待充电头缩回
          {
            data = modbusProcolCharge.GetChargingHeadState(23, 4);
          }
          getProcessModel().setIscharging(false);
          //补充停止充电的逻辑
          // 1.首先判断叉车的当前点，
          //2.获得当前点连接的位置。
          //3.判断是否是充电位置，如果是，继续
          //4.充电机停止充电，并且伸出的充电头撤回。
          //5.执行完该操作后，再进行下面的工作。
          // writeEvent(StatisticsEvent.VEHICLE_STOPS_CHARGING, vehicleNow.getName());
        }
        catch (ModbusTransportException ex) {
          java.util.logging.Logger.getLogger(KeCongCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };
    stopchargeThread.start();
  }

  @Override
  public void onEvent(Object event) {
    if (!(event instanceof TCSObjectEvent)) {
      return;
    }
    TCSObjectEvent objectEvent = (TCSObjectEvent) event;
    if (objectEvent.getType() != TCSObjectEvent.Type.OBJECT_MODIFIED) {
      return;
    }
    if (!(objectEvent.getCurrentOrPreviousObjectState() instanceof Vehicle)) {
      return;
    }
    if (!(Objects.equals(objectEvent.getCurrentOrPreviousObjectState().getName(),
                         vehicle.getName()))) {
      return;
    }
    processObjectEvent(objectEvent);
  }

  private List<Point> lsPoints;

  public void setListPoints(List<Point> lsPoints) {
    this.lsPoints = lsPoints;
  }

  public List<Point> GetListPoints() {
    return lsPoints;
  }

  private void JudgeConnect() {
    count++;
    if (count == 3) {
      if (connected) {
        count = 0;
        isconnected = true;
        connected = false;
      }
      else if (!connected) {
        isconnected = false;
        count = 0;
      }
      getProcessModel().setIsConnected(isconnected);
    }
  }
  private int processindex = 0;

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }
    super.initialize();
    this.requestResponseMatcher = componentsFactory.createRequestResponseMatcher(this);
    this.stateRequesterTask = componentsFactory.createStateRequesterTask(e -> {
      requestResponseMatcher.enqueueRequest(new KeCongComandSerachRobotStatue());
      requestResponseMatcher.checkForSendingNextRequest();
      JudgeConnect();
    });
    eventBus.subscribe(this);
    getProcessModel().setVehicleState(Vehicle.State.IDLE);
    initialized = true;
  }

  public synchronized void initVehiclePosition(String newPos) {
    kernelExecutor.submit(() -> {
      int destinationid = new OpentcsPointToKeCongPoint(newPos).getIntPoint();
      KeCongComandNavigateControl kgcComandNavigateControl = new KeCongComandNavigateControl(String.valueOf(destinationid), (byte) 0, (byte) 0);
      getRequestResponseMatcher().enqueueRequest(kgcComandNavigateControl);
      getProcessModel().setVehiclePosition(newPos);
    });
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public synchronized void enable() {
    if (isEnabled()) {
      return;
    }
    try {
      // stateRequesterTask.enable();
      udpclientmanager = new UdpClientManager<>(this, getProcessModel().getVehicleHost(), getProcessModel().getVehiclePort());
      udpclientmanager.initial();
      modbusProcolCharge = new ModbusProcolCharge(5, "COM6", 9600, 8, 0, 1, 0);
      vehicleActuralCyclicTask = new VehicleActuralTask(this);
      Thread acturalThread = new Thread(vehicleActuralCyclicTask, getName() + "-VechicleactrualThread");
      acturalThread.start();
    }
    catch (Exception ex) {
      java.util.logging.Logger.getLogger(KeCongCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
    }
    super.enable();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    super.propertyChange(evt);
    //  System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.propertyChange()");
    if (getProcessModel().getPeriodicEnable()) {
      stateRequesterTask.enable();
    }
    else {
      stateRequesterTask.disable();

    }
    if (Objects.equals(evt.getPropertyName(),
                       VehicleProcessModel.Attribute.LOAD_HANDLING_DEVICES.name())) {
      if (!getProcessModel().getVehicleLoadHandlingDevices().isEmpty()
          && getProcessModel().getVehicleLoadHandlingDevices().get(0).isFull()) {
        loadState = LoadState.FULL;
      }
      else {
        loadState = LoadState.EMPTY;
      }
    }
    if (Objects.equals(evt.getPropertyName(),
                       KeCongProcessModel.Attribute.RobotStatu.name())) {
      getProcessModel().setVehicleEnergyLevel((int) Math.floor(100 * getProcessModel().getRobotStatu().getBatterypower()));
    }
  }

  @Override
  public synchronized void disable() {
    if (!isEnabled()) {
      return;
    }
    super.disable();
    modbusProcolCharge.destroy();
    udpclientmanager.terminate();
    udpclientmanager = null;
    vehicleActuralCyclicTask.terminate();
    vehicleActuralCyclicTask = null;
    stateRequesterTask.disable();
  }

  @Override
  public void terminate() {
    super.terminate();
    eventBus.unsubscribe(this);
    initialized = false;
  }

  @Override
  public void sendCommand(MovementCommand cmd)
      throws IllegalArgumentException {
    int destinationid = RobotUtl.pointmaptoint(cmd);
    orderIds.put(cmd, destinationid);
    getComandMovementRequestQueue().add(cmd);
  }
//后期这个逻辑要补充，需要根据当前的负载状态，去做出判断，这个逻辑关联到订单的分配状态
  //如果叉车 当前状态是有货的状态，如果下个订单是去取货，则会延迟订单的执行，先执行后续放货的操作，后面接着执行

  @Override
  public synchronized ExplainedBoolean canProcess(List<String> operations) {
    requireNonNull(operations, "operations");

    //   LOG.debug("{}: Checking processability of {}...", getName(), operations);
    boolean canProcess = true;
    String reason = "";

    // Do NOT require the vehicle to be IDLE or CHARGING here!
    // That would mean a vehicle moving to a parking position or recharging location would always
    // have to finish that order first, which would render a transport order's dispensable flag
    // useless.
    boolean loaded = loadState == LoadState.FULL;
    Iterator<String> opIter = operations.iterator();
    while (canProcess && opIter.hasNext()) {
      final String nextOp = opIter.next();
      // If we're loaded, we cannot load another piece, but could unload.
      if (loaded) {
        if (nextOp.startsWith(getProcessModel().getLoadOperation())) {
          canProcess = false;
          reason = LOAD_OPERATION_CONFLICT;
        }
        else if (nextOp.startsWith(getProcessModel().getUnloadOperation())) {
          loaded = false;
        }
      } // If we're not loaded, we could load, but not unload.
      else if (nextOp.startsWith(getProcessModel().getLoadOperation())) {
        loaded = true;
      }
      else if (nextOp.startsWith(getProcessModel().getUnloadOperation())) {
        canProcess = false;
        reason = UNLOAD_OPERATION_CONFLICT;
      }
    }
    if (!canProcess) {
      LOG.debug("{}: Cannot process {}, reason: '{}'", getName(), operations, reason);
    }
    return new ExplainedBoolean(true, reason);
  }

  public RequestResponseMatcher getRequestResponseMatcher() {
    return requestResponseMatcher;
  }

  @Override
  public void processMessage(Object message) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void onIncomingTelegram(KeCongCommandResponse telegram) {
    requireNonNull(telegram);
    connected = true;
    RobotStatuResponseModel keResponseModel;
    if (!requestResponseMatcher.tryMatchWithCurrentRequest(telegram)) {
      //System.out.println(String.valueOf(telegram.getcqs()));
      //requestResponseMatcher.checkForSendingNextRequest();
      return;
    }
    try {
      if (telegram instanceof KeCongRobotStatuSearchResponse)//叉车运行状态得到
      {
        KeCongRobotStatuSearchResponse kcrssr = (KeCongRobotStatuSearchResponse) telegram;
        keResponseModel = kcrssr.getRobotStatuResponseModel();
        onStateComsume(keResponseModel);
        if (getProcessModel().getVehiclePosition() == null) {
          getProcessModel().setVehiclePosition(getInitialPosition(keResponseModel));
        }
      }
      else if (telegram instanceof KeCongComandReadResponse) {
        updateReadVarModle((KeCongComandReadResponse) telegram);
      }
      requestResponseMatcher.checkForSendingNextRequest();
    }
    catch (Exception e) {
      System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.onIncomingTelegram() " + e.getMessage());
      //  System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.onIncomingTelegram()"+e.getMessage());
    }
  }

  private void onStateComsume(RobotStatuResponseModel currentModel) {
    kernelExecutor.submit(() -> {
      RobotStatuResponseModel preRobotStatuResponseModel = getProcessModel().getRobotStatu();
      getProcessModel().setKeCongRobotPreStatuSearchResponse(preRobotStatuResponseModel);
      getProcessModel().setRobotStatu(currentModel);
      if (currentModel != null && preRobotStatuResponseModel != null) {
        upDatePostion(currentModel, preRobotStatuResponseModel);
        upDateVehicleState(currentModel, preRobotStatuResponseModel);
        upDateOrder(currentModel, preRobotStatuResponseModel);
      }
    });
  }

  private void upDatePostion(RobotStatuResponseModel kcrssr, RobotStatuResponseModel preResponse) {
    int currentposition = getPosition(kcrssr);
    if (currentposition == 0) {
      return;
    }
    //if (currentposition!= getPosition(preResponse))
    {
      String currentPosition = String.valueOf(currentposition);
      LOG.info("{}: Vehicle is now at point {}", getName(), currentPosition);
      getProcessModel().setVehiclePosition("Point-" + String.format("%04d", currentposition));
    }
  }

  private void upDateVehicleState(RobotStatuResponseModel kcrssr,
                                  RobotStatuResponseModel preResponse) {
    if (getProcessModel().getVehicleState() != Vehicle.State.CHARGING) {
      if (kcrssr.getCurrenttaskstatue() != preResponse.getCurrenttaskstatue()) {
        getProcessModel().setVehicleState(translatevehicleState(kcrssr));
      }
    }
  }

  private Vehicle.State translatevehicleState(RobotStatuResponseModel kcrssr) {
    Vehicle.State state;
    switch (kcrssr.getCurrenttaskstatue()) {
      case 0://无任务
      case 1://等待
      case 3://暂停
      case 4://完成
        state = Vehicle.State.IDLE;
        break;
      case 2://正在执行
        state = Vehicle.State.EXECUTING;
        break;
      case 5://失败
        state = Vehicle.State.ERROR;
        break;
      default://未知数据
        state = Vehicle.State.UNKNOWN;
    }
    return state;
  }

  private int getPosition(RobotStatuResponseModel kcrssr) {
    Point currentPoint;
    if (kcrssr == null) {
      return 0;
    }
    double y = kcrssr.getPostiony();
    double x = kcrssr.getPositionx();
    Triple precisePosition = new Triple((long) (1000 * x), (long) (1000 * y), 0);
    //  newKeCongComAdapter.getProcessModel().setVehiclePrecisePosition(precisePosition);
    // newKeCongComAdapter.getProcessModel().setVehicleState(Vehicle.State.IDLE);
    List<Point> PointList = GetListPoints().stream().filter(point -> Math.abs(point.getPosition().getX() - precisePosition.getX()) < 100).filter(point -> Math.abs(point.getPosition().getY() - precisePosition.getY()) < 100).collect(Collectors.toList());
    switch (PointList.size()) {
      case 0:
        return 0;
      case 1:
        currentPoint = PointList.get(0);
        // LOG.info("PointList: {}", PointList);
        return new OpentcsPointToKeCongPoint(currentPoint.getName()).getIntPoint();
      default:
        currentPoint = PointList.get(0);
        return new OpentcsPointToKeCongPoint(currentPoint.getName()).getIntPoint();
    }
  }

  private void upDateOrder(RobotStatuResponseModel kcrssr, RobotStatuResponseModel preResponse) {
    int id = getPosition(kcrssr);
    int preid = getPosition(preResponse);
    if (id == 0) {
      return;
    }
    /*   if (id== preid) {
      return;
      }*/
    if (!GetOrderIds().containsValue(id)) {
      LOG.debug("{}: Ignored finished order ID {} (reported by vehicle, not found in sent queue).",
                getName(),
                id);
      return;
    }
    Iterator<MovementCommand> cmdIter = getSentQueue().iterator();
    boolean finishedAll = false;
    while (!finishedAll && cmdIter.hasNext()) {
      MovementCommand cmd = cmdIter.next();
      if (cmd.isFinalMovement() && !cmd.isWithoutOperation()) {
        excutefinalaction(cmd);
        break;
      }
      cmdIter.remove();
      int orderId = GetOrderIds().remove(cmd);
      if (orderId == id) {
        finishedAll = true;
      }
      LOG.info("{}: Reporting command with order ID {} as executed: {}", getName(), orderId, cmd);
      getProcessModel().commandExecuted(cmd);
    }
  }

  private void excutefinalaction(MovementCommand movementCommand) {
    String operation = movementCommand.getOperation();
    if (ACTION_SET.contains(operation)) {
      //LOG.info("action has been done ,cant repeat");
      return;
    }
    if (getProcessModel().getRobotStatu().getCurrenttaskstatue() != 4) {
      return;
    }
    getProcessModel().setSingleStepModeEnabled(true);
    getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
    if (operation.equals(getProcessModel().getLoadOperation())) {
      String liftlevel = movementCommand.getOpLocation().getProperty("liftlevel");
      float lift = Float.valueOf(liftlevel);
      ACTION_SET.add(operation);
      liftorlowerfork(lift);
    }
    else if (operation.equals(getProcessModel().getUnloadOperation())) {
      String lowlevel = movementCommand.getOpLocation().getProperty("lowlevel");
      float low = Float.valueOf(lowlevel);
      ACTION_SET.add(operation);
      liftorlowerfork(low);
    }
    else if (operation.equals(getRechargeOperation())) {
      ACTION_SET.add(operation);
      getProcessModel().setVehicleState(Vehicle.State.CHARGING);
      ExcuteAfterFinalAction();
    }
    if (operation.equals(getProcessModel().getLoadOperation())) {
      // Update load handling devices as defined by this operation
      getProcessModel().setVehicleLoadHandlingDevices(
          Arrays.asList(new LoadHandlingDevice(LHD_NAME, true)));
    }
    else if (operation.equals(getProcessModel().getUnloadOperation())) {
      getProcessModel().setVehicleLoadHandlingDevices(
          Arrays.asList(new LoadHandlingDevice(LHD_NAME, false)));
    }

  }

  private void ExcuteAfterFinalAction() {
    getProcessModel().setVehicleState(Vehicle.State.IDLE);
    getProcessModel().setSingleStepModeEnabled(false);
    MovementCommand cmd = getSentQueue().poll();
    if (cmd == null) {
      return;
    }
    String action = cmd.getOperation();
    if (ACTION_SET.contains(action)) {
      ACTION_SET.remove(action);
    }
    GetOrderIds().remove(cmd);
    getProcessModel().commandExecuted(cmd);
  }

  private void liftorlowerfork(float f) {
    byte[] varvalue = DataConvertUtl.getBytes(f);
    KeCongComandWrite keCongComandWrite1 = new KeCongComandWrite(KeCongActionVar.LIFT_SV, varvalue);
    KeCongComandWrite keCongComandWrite = new KeCongComandWrite(KeCongActionVar.PID_ENABLE_STRING, new byte[]{Byte.parseByte("1")});
    KeCongComandWrite keCongComandWrite2 = new KeCongComandWrite(KeCongActionVar.FINSHI_TASK, new byte[]{Byte.parseByte("0")});
    getRequestResponseMatcher().enqueueRequest(keCongComandWrite);
    getRequestResponseMatcher().enqueueRequest(keCongComandWrite1);
    getRequestResponseMatcher().enqueueRequest(keCongComandWrite2);
    /* sendTelegram(keCongComandWrite);
     sendTelegram(keCongComandWrite1);
     sendTelegram(keCongComandWrite2);*/
    KeCongComandRead kccr = new KeCongComandRead(KeCongActionVar.FINSHI_TASK);
    getRequestResponseMatcher().enqueueRequest(kccr);

  }

  private String getInitialPosition(RobotStatuResponseModel keResponseModel) {
    Point currentPoint;
    if (keResponseModel == null) {
      return null;
    }
    double y = keResponseModel.getPostiony();
    double x = keResponseModel.getPositionx();
    Triple precisePosition = new Triple((long) (1000 * x), (long) (1000 * y), 0);
    getProcessModel().setVehiclePrecisePosition(precisePosition);
    getProcessModel().setVehicleState(Vehicle.State.IDLE);
    List<Point> PointList = lsPoints.stream().filter(point -> Math.abs(point.getPosition().getX() - precisePosition.getX()) < 500).filter(point -> Math.abs(point.getPosition().getY() - precisePosition.getY()) < 500).collect(Collectors.toList());
    switch (PointList.size()) {
      case 0:
        return null;
      case 1:
        currentPoint = PointList.get(0);
        getProcessModel().setVehiclePosition(currentPoint.getName());
        //LOG.info("PointList: {}", PointList);
        return currentPoint.getName();
      default:
        currentPoint = PointList.get(0);
        getProcessModel().setVehiclePosition(currentPoint.getName());
        return PointList.get(0).getName();
    }
  }
  private final Object oblockObject = new Object();

  //等待充电开始
  private void waitforchargestart() {
    if (getProcessModel().getVehicleState() != Vehicle.State.CHARGING) {
      return;
    }
    while (getProcessModel().isIscharging()) {
      try {
        Thread.sleep(1000);
        System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.waitforchargestart()" + "exit");
      }
      catch (InterruptedException ex) {
        java.util.logging.Logger.getLogger(KeCongCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private final TruckData truckdatafromfork = new TruckData();

  ;

  public synchronized TruckData getTruckdatafromfork() {
    return truckdatafromfork;
  }

  public synchronized void setTruckdatafromfork(byte[] var) {
    truckdatafromfork.decodebytes(var);
  }

  //变量读取
  private void updateReadVarModle(KeCongComandReadResponse keCongComandReadResponse) {

    String name = keCongComandReadResponse.getName().replaceAll(" ", "");
    byte[] var = null;
    if (name.contains(KeCongActionVar.FINSHI_TASK)) {
      var = keCongComandReadResponse.getValue();
      byte value = var[0];
      if (value == 1) {
        ExcuteAfterFinalAction();
      }
      else {
        KeCongComandRead kccr = new KeCongComandRead(KeCongActionVar.FINSHI_TASK);
        getRequestResponseMatcher().enqueueRequest(kccr);
      }
      ReadVarModel readVarModel = new ReadVarModel();
      readVarModel.setName(name);
      readVarModel.setValue(var);
      getProcessModel().setReadVarModel(readVarModel);
    }

  }
//起降货架，此处可能存在的问题是命令并不一定是按照命令队列里面的内容去发送

  @Override
  public void onConnect() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void onFailedConnectionAttempt() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void onDisconnect() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void onIdle() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public synchronized void sendTelegram(KeCongRequestMessage request) {
    requireNonNull(request);
    //  byte[] rawcontent = request.creatMessage();//要发送的数据
    kernelExecutor.submit(() -> {
      udpclientmanager.send(request);
    });
    // String string=DataConvertUtl.toHexString(rawcontent);
    //System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.sendTelegram()"+string);
  }

  @Override
  protected void connectVehicle() {
    //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  protected void disconnectVehicle() {
    // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  protected boolean isVehicleConnected() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public final KeCongProcessModel getProcessModel() {
    return (KeCongProcessModel) super.getProcessModel();
  }

  @Override
  protected VehicleProcessModelTO createCustomTransferableProcessModel() {
    return new KeCongProcessModelTO()
        .setVehicleHost(getProcessModel().getVehicleHost())
        .setVehiclePort(getProcessModel().getVehiclePort())
        .SetRobotStatu(getProcessModel().getRobotStatu())
        .setVehicleRef(getProcessModel().getVehicleReference())
        .setAutoRunMark(getProcessModel().getAutoRunMark())
        .setPeriodicEnable(getProcessModel().getPeriodicEnable())
        .setReadVarModel(getProcessModel().getReadVarModel())
        .setNavigateStatuResponseModel(getProcessModel().getNavigateStatuResponseModel())
        .setLoadOperation(getProcessModel().getLoadOperation())
        .setUnloadOperation(getProcessModel().getUnloadOperation())
        .setIsConnected(getProcessModel().getIsConnected());
  }

  private enum LoadState {
    EMPTY,
    FULL;
  }

  private final Queue<MovementCommand> comandmovement = new LinkedBlockingQueue<>();

  public Queue<MovementCommand> getComandMovementRequestQueue() {
    return comandmovement;
  }
}

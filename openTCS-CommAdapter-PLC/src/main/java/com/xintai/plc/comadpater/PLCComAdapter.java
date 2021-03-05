/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.comadpater;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.xinta.plc.model.VehicleParameterSetWithPLCMode;
import com.xinta.plc.model.VehicleStateModel;
import com.xintai.adapter.OpentcsPointToKeCongPoint;
import static com.xintai.agv.charger.device.taitan.ChargeUtilConfiguration.PROPKEY__RECHARGE_HOST;
import static com.xintai.agv.charger.device.taitan.ChargeUtilConfiguration.PROPKEY__RECHARGE_SLAVEID;
import com.xintai.charger.wrapper.ChargerUtl;
import com.xintai.erp.ReportCarPostionTOERP;
import com.xintai.erp.ReportPostionERPService;
import com.xintai.messageserviceinterface.IPParameter;
import com.xintai.messageserviceinterface.InterfaceMessageService;
import com.xintai.messageserviceinterface.VehicleMessageService;
import com.xintai.plc.filterpoint.FilterPoint;
import com.xintai.plc.message.VehicleParameterSetWithPLC;
import com.xintai.plc.message.VehicleStatePLC;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.opentcs.components.kernel.services.InternalPlantModelService;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.TCSObject;
import org.opentcs.data.TCSObjectEvent;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.Route.Step;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.LoadHandlingDevice;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.drivers.vehicle.messages.SetFinshMarkFromMes;
import org.opentcs.drivers.vehicle.messages.SetSpeedMultiplier;
import org.opentcs.util.CyclicTask;
import org.opentcs.util.ExplainedBoolean;
import org.opentcs.util.event.EventBus;
import org.opentcs.util.event.EventHandler;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class PLCComAdapter
    extends BasicVehicleCommAdapter
    implements EventHandler {

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(PLCComAdapter.class);
  private final Vehicle vehicle;
  private final PLCAdapterComponentsFactory componentsFactory;
  private final ExecutorService kernelExecutor;
  private final EventBus eventBus;
  private boolean initialized;
  private StateRequesterTask stateRequesterTask;
  private VehicleActuralTask vehicleActuralCyclicTask;
  private VehicleMessageSendTask vehicleMessageSendTask;
  private final InterfaceMessageService IVehicleMessageService;
  private final Map<MovementCommand, Integer> orderIds = new ConcurrentHashMap<>();
  private ChargerUtl chargerUtl;

  private final PLCCommAdapterConfiguration pLCCommAdapterConfiguration;
  private final FilterPoint filterpoint;
  private static final ResourceBundle BUNDLE_T = ResourceBundle.getBundle("com/xintai/opentcs/commadapter/vehicle/Bundle_zn");

  @Inject
  public PLCComAdapter(@Assisted Vehicle vehicle,
                       PLCAdapterComponentsFactory componentsFactory,
                       @KernelExecutor ExecutorService kernelExecutor,
                       @Nonnull @ApplicationEventBus EventBus eventBus,
                       VehicleMessageService vehicleMessageService,
                       PLCCommAdapterConfiguration pLCCommAdapterConfiguration,
                       InternalPlantModelService plantModelService,
                       FilterPoint filterPoint) {
    super(new PLCProcessModel(vehicle), 4, 2, LoadAction.CHARGE, kernelExecutor);
    this.vehicle = requireNonNull(vehicle, "vehicle");
    this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");
    this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
    this.eventBus = requireNonNull(eventBus, "eventBus");
    this.filterpoint = requireNonNull(filterPoint, "filterPoint");
    IVehicleMessageService = requireNonNull(vehicleMessageService, "vehicleMessageService");
    this.pLCCommAdapterConfiguration = requireNonNull(pLCCommAdapterConfiguration, "pLCCommAdapterConfiguration");
  }

  private int processindex = 0;

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }
    super.initialize();
    this.stateRequesterTask = componentsFactory.createStateRequesterTask(e -> {
      switch (processindex) {
        case 0:
          VehicleStatePLC vehicleStatePLC = IVehicleMessageService.SendStateRequest();
          if (vehicleStatePLC != null) {
            responsesQueue.add(vehicleStatePLC);
            getProcessModel().setVehicleTaskState(vehicleStatePLC.getVehicleTaskState());
          }
          processindex++;
          break;
        case 1:
          IVehicleMessageService.HeartBeat();
          processindex = 0;
          break;
      }
      if (!getProcessModel().isCommAdapterConnected()) {
        IVehicleMessageService.DisConnect();
        IVehicleMessageService.Init(new IPParameter(getProcessModel().getVehicleHost(), getProcessModel().getVehiclePort(), getProcessModel().getSlaveid()));
        LOG.info("���������泵");
      }
      //  System.out.println("com.xintai.plc.comadpater.PLCComAdapter.propertyChange()");
    });
    eventBus.subscribe(this);
    getProcessModel().setVehicleState(Vehicle.State.IDLE);
    initialized = true;
      }

    
        

 
  private void processObjectEvent(TCSObjectEvent event) {
    TCSObject<?> object = event.getCurrentOrPreviousObjectState();
    if (object instanceof TransportOrder) {
      processOrderEvent(event);
    }
    else if (object instanceof Vehicle) {
      processVehicleEvent(event);
    }
  }

  private void processVehicleEvent(TCSObjectEvent objectEvent) {
    if (objectEvent.getPreviousObjectState() == null || objectEvent.getCurrentObjectState() == null) {
      // We cannot compare two states to find out what happened - ignore.
      return;
    }

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

    Vehicle prevVehicleState = (Vehicle) objectEvent.getPreviousObjectState();
    Vehicle currVehicleState = (Vehicle) objectEvent.getCurrentObjectState();
    // Did the vehicle get a transport order?
    if (currVehicleState.getTransportOrder() != null && prevVehicleState.getTransportOrder() == null) {
      TCSObjectReference<TransportOrder> tCSObjectReference = currVehicleState.getTransportOrder();

    }
    // Did the vehicle finish a transport order?
    if (currVehicleState.getTransportOrder() == null && prevVehicleState.getTransportOrder() != null) {

    }
    // Did the vehicle start charging?
    if (currVehicleState.hasState(Vehicle.State.CHARGING)
        && !prevVehicleState.hasState(Vehicle.State.CHARGING)) {
      if (chargerUtl != null) {
        chargerUtl.StartCharge();
      }
      System.out.println("plcadapter start charging");
    }
    // Did the vehicle start charging?
    if (!currVehicleState.hasState(Vehicle.State.CHARGING)
        && prevVehicleState.hasState(Vehicle.State.CHARGING)) {
      if (chargerUtl != null) {
        chargerUtl.StopCharge();
      }
      System.out.println("plcadapter end charging");
    }
    // If the vehicle is processing an order AND is not in state EXECUTING AND
    // it was either EXECUTING before or not processing, yet, consider it being
    // blocked.
    if (currVehicleState.hasProcState(Vehicle.ProcState.PROCESSING_ORDER)
        && !currVehicleState.hasState(Vehicle.State.EXECUTING)
        && (prevVehicleState.hasState(Vehicle.State.EXECUTING)
            || !prevVehicleState.hasProcState(Vehicle.ProcState.PROCESSING_ORDER))) {

    }
    // Is the vehicle processing an order AND has its state changed from
    // something else to EXECUTING? - Consider it not blocked any more, then.
    if (currVehicleState.hasProcState(Vehicle.ProcState.PROCESSING_ORDER)
        && currVehicleState.hasState(Vehicle.State.EXECUTING)
        && !prevVehicleState.hasState(Vehicle.State.EXECUTING)) {

    }

  }

  //���ź�����������
  private void processOrderEvent(TCSObjectEvent event) {
    if (event.getPreviousObjectState() == null || event.getCurrentObjectState() == null) {
      // We cannot compare two states to find out what happened - ignore.
      return;
    }

    TransportOrder orderOld = (TransportOrder) event.getPreviousObjectState();
    TransportOrder orderNow = (TransportOrder) event.getCurrentObjectState();
    if (orderNow.getProcessingVehicle() != null && !orderNow.getProcessingVehicle().getName().equals(vehicle.getName())) {
      return;
    }
    // Has the order been activated?
    if (orderNow.hasState(TransportOrder.State.ACTIVE)
        && !orderOld.hasState(TransportOrder.State.ACTIVE)) {
    }
    // Has the order been assigned to a vehicle?
    if (orderNow.hasState(TransportOrder.State.BEING_PROCESSED)
        && !orderOld.hasState(TransportOrder.State.BEING_PROCESSED)) {
    }
    // Has the order been finished?
    if (orderNow.hasState(TransportOrder.State.FINISHED)
        && !orderOld.hasState(TransportOrder.State.FINISHED)) {
      // Check the order's deadline. Has it been crossed?
      if (orderNow.getFinishedTime().isAfter(orderNow.getDeadline())) {
      }
    }
    // Has the order failed?
    if (orderNow.hasState(TransportOrder.State.FAILED)
        && !orderOld.hasState(TransportOrder.State.FAILED)) {
    }
  }

  @Override
  public void onEvent(Object event) {
    if (!(event instanceof TCSObjectEvent)) {
      return;
    }
    TCSObjectEvent objectEvent = (TCSObjectEvent) event;
    processObjectEvent(objectEvent);
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
    IVehicleMessageService.SetConnectEvent(() -> {
      getProcessModel().setCommAdapterConnected(IVehicleMessageService.IsConnected());
      System.out.println("com.xintai.plc.comadpater.PLCComAdapter.enable()+connect");
    });
    IVehicleMessageService.SetDisConnectEvent(() -> {
      getProcessModel().setCommAdapterConnected(IVehicleMessageService.IsConnected());
      System.out.println("com.xintai.plc.comadpater.PLCComAdapter.enable()+disconnect");
    });
    IVehicleMessageService.Init(new IPParameter(getProcessModel().getVehicleHost(), getProcessModel().getVehiclePort(), getProcessModel().getSlaveid()));
    if (!IVehicleMessageService.IsConnected()) {
      LOG.info("�泵û������");
      getProcessModel().setCommAdapterConnected(false);
      return;
    }
    else {
      LOG.info("�泵������");
      getProcessModel().setCommAdapterConnected(true);
    }
    try {
      vehicleMessageSendTask = new VehicleMessageSendTask();
      vehicleActuralCyclicTask = new VehicleActuralTask();
      new Thread(vehicleMessageSendTask, "messagesendtask").start();
      Thread acturalThread = new Thread(vehicleActuralCyclicTask, getName() + "-VechicleactrualThread");
      acturalThread.start();
    }
    catch (Exception ex) {
      java.util.logging.Logger.getLogger(PLCComAdapter.class.getName()).log(Level.SEVERE, null, ex);
    }
    super.enable();
    stateRequesterTask.enable();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    super.propertyChange(evt);
    if (Objects.equals(evt.getPropertyName(),
                       PLCProcessModel.Attribute.VEHICLE_SETPARAMETERS.name())) {
      VehicleParameterSetWithPLCMode vst = getProcessModel().getVehicleParameterSet();
      if (!vst.isIswrite()) {
        return;
      }
      VehicleParameterSetWithPLC vstp = new VehicleParameterSetWithPLC(vst.getHeartbeatsignal(), vst.getAgvvspeed(),
                                                                       vst.getAgvaspeed(), 0, 0, 0, vst.getRemotestart(), vst.getNavigationtask(),
                                                                       vst.getNextsite(), vst.getNexttwosite(), vst.getTargetsitecardirection(),
                                                                       vst.getTargetsite(), vst.getCurrentschedulingtask(), vst.getMaterialcode(),
                                                                       vst.getChargingpilestate());
      IVehicleMessageService.SendSettingTOPLC(vstp);
    }
  }

  @Override
  public synchronized void disable() {
    if (!isEnabled()) {
      return;
    }
    super.disable();
    IVehicleMessageService.DisConnect();
    vehicleActuralCyclicTask.terminate();
    vehicleActuralCyclicTask = null;
    vehicleMessageSendTask.terminate();
    vehicleMessageSendTask = null;
    stateRequesterTask.disable();
  }

  @Override
  public void terminate() {
    super.terminate();
    eventBus.unsubscribe(this);
    initialized = false;
  }
  private final LinkedBlockingQueue<MovementCommand> movementcomandbufferQueue = new LinkedBlockingQueue<>();

  private LinkedBlockingQueue<MovementCommand> getMovementCommandsBufferQueue() {
    return movementcomandbufferQueue;
  }

  @Override
  public synchronized void sendCommand(MovementCommand cmd)
      throws IllegalArgumentException {
    try {
      getMovementCommandsBufferQueue().put(cmd);
    }
    catch (InterruptedException ex) {
      Logger.getLogger(PLCComAdapter.class.getName()).log(Level.SEVERE, null, ex);
    }
    System.out.println(cmd.toString());
  }

  //������������
  @Override
  protected boolean canSendNextCommand() {
    return super.canSendNextCommand() & !getProcessModel().isSingleStepModeEnabled();
  }

  @Override
  public synchronized void clearCommandQueue() {
    super.clearCommandQueue();
    orderIds.clear();
    getMovementCommandsBufferQueue().clear();
  }

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
    /*  boolean loaded = loadState == LoadState.FULL;
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
    }*/
    return new ExplainedBoolean(true, reason);
  }

  public String Operation = "";

  @Override
  public void processMessage(Object message) {
    if (message instanceof SetSpeedMultiplier) {
      SetSpeedMultiplier lsMessage = (SetSpeedMultiplier) message;
      int multiplier = lsMessage.getMultiplier();
      //  getProcessModel().setVehiclePaused(multiplier == 0);
    }
    else if (message instanceof SetFinshMarkFromMes) {
      SetFinshMarkFromMes setfinshmarkfrommes = (SetFinshMarkFromMes) message;
      //�ж�mes���ز��������Ƿ�һ��
      if (setfinshmarkfrommes.getOpString().equals(Operation)) {
        //����һ��
        String finshmark = setfinshmarkfrommes.getFinshMark();
        Boolean resultBoolean = Boolean.valueOf(finshmark);
        getProcessModel().setFinshmarkfromes(resultBoolean);
        System.out.println(resultBoolean.booleanValue());
      }
      {
        //��һ��
        System.out.println("com.xintai.plc.comadpater.PLCComAdapter.SetFinshMarkFromMes()���������Ͳ�һ��");
      }
    }
  }

  @Override
  protected void connectVehicle() {

  }

  @Override
  protected void disconnectVehicle() {
    // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates
  }

  @Override
  protected boolean isVehicleConnected() {
    //To change body of generated methods, choose Tools | Templates.
    //return  master.isConnected();
    return IVehicleMessageService.IsConnected();
  }

  @Override
  public final PLCProcessModel getProcessModel() {
    return (PLCProcessModel) super.getProcessModel();
  }

  @Override
  protected VehicleProcessModelTO createCustomTransferableProcessModel() {
    return new PLCProcessModelTO().
        setVehicleHost(getProcessModel().getVehicleHost())
        .setVehiclePort(getProcessModel().getVehiclePort()).
        setPreviousVehicleStateModel(getProcessModel().getPreviousVehicleStateModel())
        .setVehicleParameterSet(getProcessModel().getVehicleParameterSet())
        .setVehicleRef(getProcessModel().getVehicleReference());
  }

  private enum LoadState {
    EMPTY,
    FULL;
  }

  private final Queue<VehicleStatePLC> responsesQueue = new LinkedBlockingQueue<>();

  private Queue<VehicleStatePLC> getComandVehicleStateResponsesQueue() {
    return responsesQueue;
  }

  private class VehicleActuralTask
      extends CyclicTask {

    private volatile boolean lastmcdmark = false;

    private VehicleActuralTask() {
      super(100);
    }

    @Override
    protected void runActualTask() {
      VehicleStatePLC vehicleStatePLC;
      synchronized (PLCComAdapter.this) {
        vehicleStatePLC = getComandVehicleStateResponsesQueue().poll();
      }
      if (vehicleStatePLC != null) {
        onStateResponse(vehicleStatePLC);
      }
    }

    private void onStateResponse(VehicleStatePLC vehiclestateplc) {

      VehicleStateModel previousVehicleStateModel = getProcessModel().getPreviousVehicleStateModel();
      VehicleStateModel currentVehicleStateModel = vehiclestateplc.GetVehicleStateModel();
      getProcessModel().setPreviousVehicleStateModel(currentVehicleStateModel);
      //��ѹ�ı�
      getProcessModel().setVehicleEnergyLevel((int) Math.floor(currentVehicleStateModel.getBatteryPower()));
      updatepostion(currentVehicleStateModel, previousVehicleStateModel);
      checkresponseisright(currentVehicleStateModel, previousVehicleStateModel);
      if (lastmcdmark)//���ִ�е����һ���߼��������²��費��ִ�С�
      {
        return;
      }
      updatestate(currentVehicleStateModel, previousVehicleStateModel);
      updateorder(currentVehicleStateModel, previousVehicleStateModel);
    }

    private boolean checkresponseisright(VehicleStateModel curVehicleStateModel,
                                         VehicleStateModel previousStateModel) {
      if (curVehicleStateModel.getCurrentSite() == 0 || curVehicleStateModel.getAgvRunState() != 2) {
        return false;
      }
      /*   if(curVehicleStateModel.getCurrentSite()==previousStateModel.getCurrentSite())
    return false;*/
      int next = curVehicleStateModel.getNextSite();
      int nexttwo = curVehicleStateModel.getNextTwoSite();
      int storenext = getProcessModel().getCurrentnavigationpoint();
      int storenexttwo = getProcessModel().getNextcurrentnavigationpoint();
      if (next == storenext & storenexttwo == nexttwo) {
        MovementCommand movementCommand1 = getMovementCommandsBufferQueue().peek();
        if (movementCommand1 != null) {
          //�����ǰ����ĵ�ͷ��͸��豸�����µ㲻һ�£�˵����ǰ�㻹û�з��͸��豸�����Բ�����ǰ�����Ƴ�����
          if (new OpentcsPointToKeCongPoint(movementCommand1.getStep().getDestinationPoint().getName()).getIntPoint() != nexttwo) {
            return false;
          }
        }
        MovementCommand movementCommand = getMovementCommandsBufferQueue().poll();
        if (movementCommand != null) {
          orderIds.putIfAbsent(movementCommand, nexttwo);
          return true;
        }
      }
      return false;
    }

    private void updatepostion(VehicleStateModel curVehicleStateModel,
                               VehicleStateModel previousStateModel) {
      if (curVehicleStateModel.getCurrentSite() == previousStateModel.getCurrentSite()) {
        return;
      }
      getProcessModel().setVehiclePosition("Point-" + String.format("%04d", curVehicleStateModel.getCurrentSite()));
    }

    private void updatestate(VehicleStateModel curVehicleStateModel,
                             VehicleStateModel previousStateModel) {
      /* if(curVehicleStateModel.getNavigationalState()==previousStateModel.getNavigationalState())
     return;*/
      if (getProcessModel().getVehicleState() != Vehicle.State.CHARGING) {
        getProcessModel().setVehicleState(translateState(curVehicleStateModel.getNavigationalState()));
      }
    }

    private Vehicle.State translateState(int data) {
      switch (data) {
        case 1:
        case 4:
          return Vehicle.State.IDLE;
        case 2:
        case 3:
          return Vehicle.State.EXECUTING;
        case 5:
          getProcessModel().commandFailed(null);//ʧЧ����
          return Vehicle.State.ERROR;
        default:
          return Vehicle.State.UNAVAILABLE;
      }
    }

    private void excutefinalaction(MovementCommand cmd) {
      System.out.println("1");

      if (cmd.isFinalMovement() & cmd.isWithoutOperation()) {
        //���һ��ִ�в���
        System.out.println("2");
        System.out.println("3");
        System.out.println("com.xintai.plc.comadpater.PLCComAdapter.VehicleActuralTask.excutefinalaction()");
        excuteActionAfterFinalOperation(cmd);
        System.out.println("4");
      }
      else if (!cmd.isWithoutOperation() & cmd.isFinalMovement()) {
        //
        System.out.println("5");
        if (cmd.getOperation().equals(getRechargeOperation())) {
          System.out.println("before set state to charging");
//          chargerUtl = new ChargerUtl(3, "COM6", getProcessModel());

//          if (cmd.getOpLocation().getProperty(BUNDLE_T.getString("ChargerUtl.PROPKEY_SLAVEID")) == null || 
//              cmd.getOpLocation().getProperty(BUNDLE_T.getString("ChargerUtl.PROPKEY_HOST")) == null) {
//            System.out.println("The communication parameters of the charging device are abnormal");
//            return;
//          }
          chargerUtl = new ChargerUtl(Integer.parseInt(cmd.getOpLocation().getProperty(PROPKEY__RECHARGE_SLAVEID)),
                                      cmd.getOpLocation().getProperty(PROPKEY__RECHARGE_HOST), getProcessModel());
          getProcessModel().setVehicleState(Vehicle.State.CHARGING);
          excuteActionAfterFinalOperation(cmd);
          System.out.println("end set state to charging");
          return;
        }
        getProcessModel().setSingleStepModeEnabled(true);
        getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
        Thread t = new Thread(new ExcuteFinalAction(cmd), "excutefinalaction");
        t.start();
        Operation = cmd.getOperation();
      }
    }

    private void excuteActionAfterFinalOperation(MovementCommand cmd) {
      if (cmd != getSentQueue().peek()) {
        return;
      }
      getSentQueue().poll();
      orderIds.remove(cmd);
      getProcessModel().commandExecuted(cmd);
      getProcessModel().setSingleStepModeEnabled(false);
      if (getProcessModel().getVehicleState() != Vehicle.State.CHARGING) {
        getProcessModel().setVehicleState(Vehicle.State.IDLE);
      }
      getProcessModel().setCurrentnavigationpoint(0);
      getProcessModel().setNextcurrentnavigationpoint(0);
      lastmcdmark = false;
    }

    private class ExcuteFinalAction
        implements Runnable {

      private final MovementCommand cmd;

      public ExcuteFinalAction(MovementCommand cmd) {
        this.cmd = cmd;
      }

      @Override
      public void run() {
        synchronized (getProcessModel().getObjectForMesFinshWork()) {
          while (!getProcessModel().isFinshmarkfromes()) {
            try {
              System.out.println("before");
              getProcessModel().getObjectForMesFinshWork().wait();
            }
            catch (InterruptedException ex) {
              Logger.getLogger(PLCComAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
        System.out.println("end");
        excuteActionAfterFinalOperation(cmd);
        System.out.println("after");
        //
        if (cmd.getOperation().equals(getProcessModel().getLoadOperation())) {
        // Update load handling devices as defined by this operation
        getProcessModel().setVehicleLoadHandlingDevices(
            Arrays.asList(new LoadHandlingDevice("default", true)));
      }
      else if (cmd.getOperation().equals(getProcessModel().getUnloadOperation())) {
        getProcessModel().setVehicleLoadHandlingDevices(
            Arrays.asList(new LoadHandlingDevice("default", false)));
      }
        getProcessModel().setFinshmarkfromes(false);
      }

    }

    private void updateorder(VehicleStateModel curVehicleStateModel,
                             VehicleStateModel previousStateModel) {
      if (curVehicleStateModel.getCurrentSite() == 0) {
        return;
      }
      // If the last finished order ID hasn't changed, don't bother.
      if (curVehicleStateModel.getCurrentSite() == previousStateModel.getCurrentSite()) {
        return;
      }
      // Check if the new finished order ID is in the queue of sent orders.
      // If yes, report all orders up to that one as finished.
      //����µ���ɶ���ID�Ƿ����ѷ��Ͷ����Ķ����С�
      //����ǣ��������еĶ�����ֱ��һ����ɡ�
      if (!orderIds.containsValue(curVehicleStateModel.getCurrentSite())) {
        LOG.debug("{}: Ignored finished order ID {} (reported by vehicle, not found in sent queue).",
                  getName(),
                  curVehicleStateModel.getCurrentSite());
        return;
      }
      Iterator<MovementCommand> cmdIter = getSentQueue().iterator();
      boolean finishedAll = false;
      while (!finishedAll && cmdIter.hasNext()) {
        MovementCommand cmd = cmdIter.next();
        if (cmd.isFinalMovement() & ("Point-" + String.format("%04d", curVehicleStateModel.getCurrentSite())) == null ? cmd.getFinalDestination().getName() == null : ("Point-" + String.format("%04d", curVehicleStateModel.getCurrentSite())).equals(cmd.getFinalDestination().getName())) {
          lastmcdmark = true;
          if (pLCCommAdapterConfiguration.noticeposition_enable()) {
            noticeerppostion(curVehicleStateModel, cmd);
          }
          excutefinalaction(cmd);
          break;
        }
        cmdIter.remove();
        int orderId = orderIds.remove(cmd);
        if (orderId == curVehicleStateModel.getCurrentSite()) {
          finishedAll = true;
        }
        LOG.info("{}: Reporting command with order ID {} as executed: {}", getName(), orderId, cmd);
        getProcessModel().commandExecuted(cmd);
      }
    }

    private void noticeerppostion(VehicleStateModel curVehicleStateModel, MovementCommand cmd) {
      ReportCarPostionTOERP rcptoerp = new ReportCarPostionTOERP(getProcessModel().getOrderInfor().getOrdername(),
                                                                 getProcessModel().getOrderInfor().getOrdertype(),
                                                                 getProcessModel().getVehicleTaskState().getMaterialState().toString(),
                                                                 String.valueOf(curVehicleStateModel.getCurrentSite()),
                                                                 cmd.getFinalOperation());
      ReportPostionERPService reportPostionERPService = new ReportPostionERPService(pLCCommAdapterConfiguration.reportpostionurl_url());
      reportPostionERPService.SendPostionTOERP(rcptoerp);
    }
  }

  private class VehicleMessageSendTask
      extends CyclicTask {

    public VehicleMessageSendTask() {
      super(200);
    }

    @Override
    protected void runActualTask() {
      if (!isVehicleConnected()) {
        return;
      }
      if (getProcessModel().getPreviousVehicleStateModel().getAgvRunState() == 2) {
        MovementCommand movementCommand = getMovementCommandsBufferQueue().peek();
        if (movementCommand != null) {
          if (getProcessModel().isIscharging()) {
            getProcessModel().setVehicleState(Vehicle.State.EXECUTING);//����߼���Ҫ�������λ״̬Ϊ����״̬��������״̬�޷�ȡ����
            return;
          }
          if (pLCCommAdapterConfiguration.stopvehicle_enable() && filterpoint.verifycanrun(movementCommand, vehicle)) {
            return;
          }
          IVehicleMessageService.SendNavigateComand(movementCommand, getProcessModel());
        }
      }
    }
  }

}

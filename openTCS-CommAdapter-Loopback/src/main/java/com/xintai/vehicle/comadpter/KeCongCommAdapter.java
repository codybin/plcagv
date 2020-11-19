/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.vehicle.comadpter;
import com.alibaba.fastjson.JSON;
import com.google.inject.assistedinject.Assisted;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.xintai.json.VehicleJson;
import com.xintai.kecong.mesaage.adapter.OpentcsPointToKeCongPoint;
import com.xintai.kecong.message.ConnectionEventListener;
import com.xintai.kecong.message.DataConvertUtl;
import com.xintai.kecong.message.KeCongActionVar;
import com.xintai.kecong.message.KeCongComandNavigateControl;
import com.xintai.kecong.message.KeCongComandNavigateSearch;
import com.xintai.kecong.message.KeCongComandNavigateSearchResponse;
import com.xintai.kecong.message.KeCongComandNavigationResponse;
import com.xintai.kecong.message.KeCongComandRead;
import com.xintai.kecong.message.KeCongComandReadResponse;
import com.xintai.kecong.message.KeCongComandSerachRobotStatue;
import com.xintai.kecong.message.KeCongComandWrite;
import com.xintai.kecong.message.KeCongCommandResponse;
import com.xintai.kecong.message.KeCongRequestMessage;
import com.xintai.kecong.message.KeCongReturnCode;
import com.xintai.kecong.message.KeCongRobotStatuSearchResponse;
import com.xintai.kecong.modbusvar.TruckData;
import com.xintai.kecong.model.NavigateStatuResponseModel;
import com.xintai.kecong.model.ReadVarModel;
import com.xintai.kecong.model.RobotStatuResponseModel;

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
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.data.TCSObject;
import org.opentcs.data.TCSObjectEvent;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.drivers.vehicle.LoadHandlingDevice;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.opentcs.util.CyclicTask;
import org.opentcs.util.event.EventBus;
import org.opentcs.util.event.EventHandler;
import redis.clients.jedis.Jedis;

public class KeCongCommAdapter    extends BasicVehicleCommAdapter implements EventHandler, ConnectionEventListener<KeCongCommandResponse>,TelegramSender{
 private static final Logger LOG = LoggerFactory.getLogger(KeCongCommAdapter.class);
  private final KeCongAdapterComponentsFactory componentsFactory;
  private final ExecutorService kernelExecutor;
  private StateRequesterTask stateRequesterTask;
  private RequestResponseMatcher requestResponseMatcher;
  private boolean  initialized;
 public static final String LHD_NAME = "default";
 private UdpClientManager<KeCongRequestMessage> udpclientmanager;
  private  LoadState loadState = LoadState.EMPTY;
  private final Map<MovementCommand, Integer> orderIds = new ConcurrentHashMap<>();
     private CyclicTask vehicleActuralCyclicTask;
 private  ModbusProcolCharge modbusProcolCharge;//充电机
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
    this.componentsFactory = requireNonNull( componentsFactory, "componentsFactory");
    this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
    this.eventBus = requireNonNull(eventBus, "eventBus");
  }
  
  private Byte count=0;
private   boolean connected=false;
private  boolean  isconnected=false;
 private void processObjectEvent(TCSObjectEvent event) {
    TCSObject<?> object = event.getCurrentOrPreviousObjectState();
    /*   if (object instanceof TransportOrder) {
    processOrderEvent(event);
    }*/
     if (object instanceof Vehicle) {
      processVehicleEvent(event);
    }
  }
    @Override   
  protected synchronized boolean canSendNextCommand() {
    if(getProcessModel().isIscharging())
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
    }
      System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.canSendNextCommand()"+ String.valueOf(getProcessModel().isIscharging()));
    return super.canSendNextCommand();
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
   
   private void startcharge()
   {
    Thread startchargeThread=new Thread()
       {
       @Override
       public void run()    
       {
        try {
        modbusProcolCharge.StartCharge(8, true);
        byte data=0;
        while(data!=2)//等待充电头伸出
        {
      data= modbusProcolCharge.GetChargingHeadState(23, 4);     
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
   
   private void stopcharge()
   {
      Thread stopchargeThread=new Thread()
       {
       @Override
       public void run()    
       {
        try {
        modbusProcolCharge.StopCharge(7, true);
        byte data=0;
        while(data!=4)//等待充电头缩回
        {
      data= modbusProcolCharge.GetChargingHeadState(23, 4);     
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
     /*     Vehicle currVehicleState = (Vehicle) objectEvent.getCurrentObjectState();
     System.out.println( currVehicleState.getCurrentPosition());
     Jedis jedis=new Jedis();
     // jedis.flushDB();
     VehicleJson vehicleJson=new VehicleJson();
     vehicleJson.setName(currVehicleState.getName());
     vehicleJson.setCurrentPosition( currVehicleState.getCurrentPosition()!=null?currVehicleState.getCurrentPosition().getName():"");
     vehicleJson.setLength(currVehicleState.getLength());
     vehicleJson.setEnergyLevelCritical(currVehicleState.getEnergyLevelCritical());
     vehicleJson.setEnergyLevelGood(currVehicleState.getEnergyLevelGood());
     vehicleJson.setEnergyLevel(currVehicleState.getEnergyLevel());
     vehicleJson.setProcState(currVehicleState.getProcState().name());
     vehicleJson.setState(currVehicleState.getState().name());
     vehicleJson.setTransportOrder(currVehicleState.getTransportOrder()!=null?currVehicleState.getTransportOrder().getName():"");
     jedis.set(currVehicleState.getName(),JSON.toJSONString(vehicleJson));
     String  vehicleString=   jedis.get(currVehicleState.getName());
     VehicleJson vehicle11=JSON.parseObject(vehicleString, VehicleJson.class);
     System.out.println(vehicle11);*/
  }
  
  private  List<Point> lsPoints;
 public void setListPoints(List<Point> lsPoints)
 {
 this.lsPoints=lsPoints; 
 }
private  void JudgeConnect()
{
      count++;
      if(count==3)
      {
      if(connected)
      {
      count=0;
      isconnected=true;
      connected=false;
      }else if(!connected)
      {
      isconnected=false;
      count=0;
      }
       getProcessModel().setIsConnected(isconnected);
}
}
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
     int destinationid=new  OpentcsPointToKeCongPoint(newPos).getIntPoint(); 
   KeCongComandNavigateControl kgcComandNavigateControl=  new KeCongComandNavigateControl(String.valueOf( destinationid),(byte)0,(byte)0);
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
     udpclientmanager=new UdpClientManager<>(this, getProcessModel().getVehicleHost(), getProcessModel().getVehiclePort());
     udpclientmanager.initial();
     modbusProcolCharge=new ModbusProcolCharge(5, "COM6", 9600, 8,0, 1, 0);
       vehicleActuralCyclicTask = new VehicleActuralTask();
    Thread acturalThread = new Thread(vehicleActuralCyclicTask, getName() + "-VechicleactrualThread");
    acturalThread.start();
   }
   catch (Exception ex) {
     java.util.logging.Logger.getLogger(KeCongCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
   }
    super.enable();
    /*  String initialPos
    = vehicle.getProperties().get(VehicleProperties.PROPKRY_VEHICLE_INITIALPOSITIONS);
    if (initialPos != null) {
    initVehiclePosition(initialPos);
    }*/
  
  
  
  }
   @Override
  public void propertyChange(PropertyChangeEvent evt) {
    super.propertyChange(evt);
     System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.propertyChange()");
         if ( getProcessModel().getPeriodicEnable()) {
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
                     KeCongProcessModel .Attribute.RobotStatu.name())) 
       {
   getProcessModel().setVehicleEnergyLevel((int) Math.floor(100*getProcessModel().getRobotStatu().getBatterypower()));
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
    initialized=false;
  }
  @Override
  public void sendCommand(MovementCommand cmd)
      throws IllegalArgumentException {
   Object selectedItem = cmd.getStep().getDestinationPoint().getName();
    String destinationIdString = selectedItem instanceof Point
        ? ((Point) selectedItem).getName() : selectedItem.toString();
    int destinationid=new  OpentcsPointToKeCongPoint(destinationIdString).getIntPoint(); 
    System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.sendCommand()"+destinationid);
     orderIds.put(cmd, destinationid);
   KeCongComandNavigateControl kgcComandNavigateControl=  new KeCongComandNavigateControl(String.valueOf( destinationid),(byte)0,(byte)0);
   getRequestResponseMatcher().enqueueRequest(kgcComandNavigateControl); 
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
    return new ExplainedBoolean(canProcess, reason);
  }
 public RequestResponseMatcher getRequestResponseMatcher() {
    return requestResponseMatcher;
  }
  @Override
  public void processMessage(Object message) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
private  RobotStatuResponseModel keResponseModel;
  @Override
  public void onIncomingTelegram(KeCongCommandResponse telegram) {
   // requireNonNull(telegram);
    //To change body of generated methods, choose Tools | Templates.
    //补充接收到的逻辑，如果是运行状态报文则更新已经连接。
     int [] currenthaspassedpoint=new int[126];
    int [] previoushaspassedpoint=new int[126];
    connected=true;
     if (!requestResponseMatcher.tryMatchWithCurrentRequest(telegram)) {
       System.out.println(String.valueOf(telegram.getcqs()));
      return;
    }
    
    System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.onIncomingTelegram()");
    try {
    if(telegram instanceof KeCongRobotStatuSearchResponse)//叉车运行状态得到
    {
      keResponseModel=((KeCongRobotStatuSearchResponse) telegram).getRobotStatuResponseModel();
       if(getProcessModel().getVehiclePosition()==null)
       getProcessModel().setVehiclePosition(getInitialPosition());
     getProcessModel().setRobotStatu(((KeCongRobotStatuSearchResponse) telegram).getRobotStatuResponseModel());
    }else if(telegram instanceof KeCongComandReadResponse)
    {
      updateReadVarModle((KeCongComandReadResponse)telegram );
    }
    else if(telegram instanceof KeCongComandNavigationResponse)//叉车导航控制回复
    {
    if(((KeCongComandNavigationResponse) telegram).getReturnCode()==KeCongReturnCode.Sucees)
    {    
    getRequestResponseMatcher().enqueueRequest(new KeCongComandNavigateSearch());
    }   
    }else if(telegram instanceof KeCongComandNavigateSearchResponse)//叉车导航状态回复
    { 
      KeCongComandNavigateSearchResponse  kccnsr=((KeCongComandNavigateSearchResponse) telegram);
      // onStateResponse(kccnsr);
        getComandNavigateSearchResponsesQueue().add(kccnsr);   
    }
    requestResponseMatcher.checkForSendingNextRequest();
    }
    catch (Exception e) {
      System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.onIncomingTelegram()"+e.getMessage());
    }
  }
    
   private String getInitialPosition() {
     Point currentPoint;
      if(keResponseModel==null)
      return null ;
     double y=keResponseModel.getPostiony();
     double x=   keResponseModel.getPositionx();
        Triple precisePosition = new  Triple((long)(1000*x), (long)(1000*y), 0);
        getProcessModel().setVehiclePrecisePosition(precisePosition);
        getProcessModel().setVehicleState(Vehicle.State.IDLE);
        List<Point> PointList = lsPoints.stream().filter(point -> Math.abs(point.getPosition().getX() - precisePosition.getX()) < 500).filter(point -> Math.abs(point.getPosition().getY() - precisePosition.getY()) < 500).collect(Collectors.toList());
        switch( PointList.size() ) {
            case 0:
                return null;
            case 1:
                currentPoint = PointList.get(0);
                getProcessModel().setVehiclePosition(currentPoint.getName());
                LOG.info("PointList: {}", PointList);
                return currentPoint.getName();
            default:
                currentPoint = PointList.get(0);
                getProcessModel().setVehiclePosition(currentPoint.getName());
                return PointList.get(0).getName();
        }
    }
  
   private void onStateResponse(NavigateStatuResponseModel stateResponse) {
    requireNonNull(stateResponse, "stateResponse");

    final NavigateStatuResponseModel previousState = getProcessModel().getCurrentStateModel();
    final NavigateStatuResponseModel currentState = stateResponse;

    kernelExecutor.submit(() -> {
      // Update the vehicle's current state and remember the old one.
      getProcessModel().setPreviesStatuResponseModel(previousState);
      getProcessModel().setCurrentStateModel(currentState);
      checkForVehiclePositionUpdate(previousState, currentState);
      checkForVehicleStateUpdate(previousState, currentState);
      checkOrderFinished(previousState, currentState);
    
      // XXX Process further state updates extracted from the telegram here.
    });
  }
 
 private void onStateResponse(  KeCongComandNavigateSearchResponse  kccnsr) {
    requireNonNull(kccnsr, "stateResponse");

    final NavigateStatuResponseModel previousState = getProcessModel().getCurrentStateModel();
    final NavigateStatuResponseModel currentState = kccnsr.getNavigateStatuResponseModel();
      getProcessModel().setNavigateStatuResponseModel(kccnsr.getNavigateStatuResponseModel());
      getProcessModel().setPreviesStatuResponseModel(previousState);
      getProcessModel().setCurrentStateModel(currentState);
      checkForVehiclePositionUpdate(previousState, currentState);
      checkForOperation(currentState);
      checkForVehicleStateUpdate(previousState, currentState);
      checkOrderFinished(previousState, currentState);
     if(kccnsr.getStatu()==2)
    { 
       getRequestResponseMatcher().enqueueRequest(new KeCongComandNavigateSearch());
    }
      
  }
 private final  Object oblockObject=new Object();
 private void checkForOperation( NavigateStatuResponseModel currentState)
 {
  if(currentState.getStatu()==4)  
   {  Point destPoint;
     
      final MovementCommand curCommand; 
    synchronized (KeCongCommAdapter.this) {
        curCommand = getSentQueue().peek();
        if(curCommand!=null)
        destPoint=curCommand.getFinalDestination();
        else
          destPoint=null;
    }
    if(destPoint!=null&&new OpentcsPointToKeCongPoint(destPoint.getName()).getIntPoint()==(currentState.getPostionId()))
    {
      kecongOperation(curCommand);    
      waitforliftevent();
      waitforchargestart();
    }
   }
 
 }
 //等待充电开始
 private void waitforchargestart() 
 {
   if(getProcessModel().getVehicleState()!=Vehicle.State.CHARGING)
     return;
while(getProcessModel().isIscharging())
{
     try {
       Thread.sleep(1000);
       System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.waitforchargestart()"+"exit");
     }
     catch (InterruptedException ex) {
       java.util.logging.Logger.getLogger(KeCongCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
     }
}
   }
 
 private void waitforliftevent()
 { if(!getProcessModel().isIsLift())
 {
 return; 
 }
     if(getProcessModel().isIsLift())
    {
    getRequestResponseMatcher().enqueueRequest(new KeCongComandRead(KeCongActionVar.TRUCKLiftData));}
    
    synchronized(oblockObject)
    {  
      while(getProcessModel().isIsLift())
    {
    try
    {
    oblockObject.wait();     
    }
    catch (Exception e) {
    }
    }
    }
 
 
 }
  private void checkForVehiclePositionUpdate(NavigateStatuResponseModel previousState,
                                             NavigateStatuResponseModel currentState) {
     if (previousState.getPostionId()!= currentState.getPostionId()) {
    String currentPosition = String.valueOf(currentState.getPostionId());
    LOG.info("{}: Vehicle is now at point {}", getName(), currentPosition);
    if (currentState.getPostionId() != 0) {
           getProcessModel().setVehiclePosition("Point-"+String.format("%04d",currentState.getPostionId()));
    }
     }
  }

  private void checkForVehicleStateUpdate(NavigateStatuResponseModel previousState,
                                          NavigateStatuResponseModel currentState) {
    /*  if (previousState.getStatu()== currentState.getStatu()||!isLift()) {
    return;
    }*/
      if(getProcessModel().getVehicleState()!=Vehicle.State.CHARGING)
         getProcessModel().setVehicleState(translateVehicleState(currentState.getStatu()));
  }
  private Vehicle.State translateVehicleState(byte operationState) {
    switch (operationState) {
      case 4:
        if(!getProcessModel().isIsLift())
        return Vehicle.State.IDLE;
        else 
          return Vehicle.State.EXECUTING; 
      case 2:
        return Vehicle.State.EXECUTING;
      default:
        return Vehicle.State.UNKNOWN;
    }
  }
  private void checkOrderFinished(NavigateStatuResponseModel previousState, NavigateStatuResponseModel currentState) {
  if(currentState.getPostionId()== 0) {
      return;
    }
    // If the last finished order ID hasn't changed, don't bother.
    /*  if (previousState.getPostionId()== currentState.getPostionId()) {
    return;
    }*/
    // Check if the new finished order ID is in the queue of sent orders.
    // If yes, report all orders up to that one as finished.
    if (!orderIds.containsValue(currentState.getPostionId())) {
      LOG.debug("{}: Ignored finished order ID {} (reported by vehicle, not found in sent queue).",
                getName(),
                currentState.getTargetid());
      return;
    }
//可能存在的问题是orderIds里面包含了被发送给车辆但是没有被执行的运行命令，在下面的循环中可能删掉这个命令
    Iterator<MovementCommand> cmdIter = getSentQueue().iterator();
    boolean finishedAll = false;
    while (!finishedAll && cmdIter.hasNext()) {
      MovementCommand cmd = cmdIter.next();
      cmdIter.remove();
      int orderId = orderIds.remove(cmd);
      if (orderId == currentState.getPostionId()) {
      finishedAll = true;
      }
      LOG.info("{}: Reporting command with order ID {} as executed: {}", getName(), orderId, cmd);
      getProcessModel().commandExecuted(cmd);
    }
  }
 
  //变量读取
  private void updateReadVarModle(KeCongComandReadResponse keCongComandReadResponse)
  {
      String name =keCongComandReadResponse.getName().replaceAll(" ", "");
     byte[] var=keCongComandReadResponse.getValue();
     if(getProcessModel().isIsLift())
     {if(name.contains(KeCongActionVar.TRUCKLiftData))
     {
       System.out.println("TRUCKLiftData"+new String(var));
        TruckData  truckData1=new TruckData();
        truckData1.decodebytes(var);
      byte  f =  truckData1.getFinish();
     if(f==2&&truckData.equal(truckData1))
     {
    synchronized(oblockObject)
    {getProcessModel().setIsLift(false);
    oblockObject.notifyAll();
    } 
    truckData1.setEnbalepid((byte)0);
    truckData1.setLiftsv(0);
    truckData1.setFinish((byte)3);
    truckData1.setFinshtask((byte)0);
     KeCongComandWrite keCongComandWrite=  new KeCongComandWrite( KeCongActionVar.TRUCKLiftData,truckData1.getbytes());
    getRequestResponseMatcher().enqueueRequest(keCongComandWrite);
     }else 
     {
      getRequestResponseMatcher().enqueueRequest(new KeCongComandRead(KeCongActionVar.TRUCKLiftData));
     }
     }  
     }
     ReadVarModel readVarModel=new ReadVarModel();
     readVarModel.setName(name);
     readVarModel.setValue(var);
     getProcessModel().setReadVarModel(readVarModel);
  }
//起降货架，此处可能存在的问题是命令并不一定是按照命令队列里面的内容去发送
  private void kecongOperation(MovementCommand movementCommand)
  {
    System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.kecongOperation()");
  if(movementCommand.isFinalMovement()&&!movementCommand.isWithoutOperation())
  {
    //stateRequesterTask.disable();
    if(movementCommand.getOperation().equals(getProcessModel().getLoadOperation()))
      {
        liftorlowerfork(0.3f);
      }else if(movementCommand.getOperation().equals(getProcessModel().getUnloadOperation()))
      { 
        liftorlowerfork(0);
      }else if(movementCommand.getOperation().equals(getRechargeOperation()))
      {
         getProcessModel().setVehicleState(Vehicle.State.CHARGING);
        
      }
      String operation=movementCommand.getOperation();
      if (operation.equals(getProcessModel().getLoadOperation())) {
        // Update load handling devices as defined by this operation
        getProcessModel().setVehicleLoadHandlingDevices(
            Arrays.asList(new LoadHandlingDevice(LHD_NAME, true)));
      }
      else if (operation.equals(getProcessModel().getUnloadOperation())) {
        getProcessModel().setVehicleLoadHandlingDevices(
            Arrays.asList(new LoadHandlingDevice(LHD_NAME, false)));
      }
   // stateRequesterTask.enable();
   //   stateRequesterTask.restart();
  }
  }
  private float liftfork=0;
  private volatile boolean  lift=false;
 private TruckData  truckData;
  private void liftorlowerfork(float f)
  {
    liftfork=f;
   getProcessModel().setIsLift(true);
    truckData=new TruckData();
    truckData.setEnbalepid((byte)1);
    truckData.setLiftsv(f);
    truckData.setFinish((byte)0);
    truckData.setFinshtask((byte)0);
     KeCongComandWrite keCongComandWrite=  new KeCongComandWrite( KeCongActionVar.TRUCKLiftData,truckData.getbytes());
    getRequestResponseMatcher().enqueueRequest(keCongComandWrite);   
  }
 
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
    byte[] rawcontent = request.creatMessage();//要发送的数据
    kernelExecutor.submit(() -> {
      udpclientmanager.send(request);
   });
    String string=DataConvertUtl.toHexString(rawcontent);
  System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.sendTelegram()"+string);
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
  protected VehicleProcessModelTO createCustomTransferableProcessModel() 
  {
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
    private  final Queue<KeCongComandNavigateSearchResponse> responsesQueue = new LinkedBlockingQueue<>();
    private Queue<KeCongComandNavigateSearchResponse> getComandNavigateSearchResponsesQueue()
    {
    return  responsesQueue;
    
    }
private class VehicleActuralTask
      extends CyclicTask {
  
    private VehicleActuralTask() {
      super(0);
    }

    @Override
    protected void runActualTask()
    {
      KeCongComandNavigateSearchResponse kkcnsrComandNavigateSearchResponse;
    synchronized(KeCongCommAdapter.this)
    {
     kkcnsrComandNavigateSearchResponse=getComandNavigateSearchResponsesQueue().poll();
    }
    if(kkcnsrComandNavigateSearchResponse!=null)
    onStateResponse(kkcnsrComandNavigateSearchResponse);      
    }
  }
}

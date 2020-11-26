/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.xintai.vehicle.comadpter;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.assistedinject.Assisted;
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
import com.xintai.kecong.model.ReadVarModel;
import com.xintai.kecong.model.RobotStatuResponseModel;
import com.xintai.kecong.udp.UdpClientManager;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.model.Vehicle.Orientation;
import org.opentcs.data.order.Route.Step;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.LoadHandlingDevice;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.SimVehicleCommAdapter;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.drivers.vehicle.messages.SetSpeedMultiplier;
import org.opentcs.util.CyclicTask;
import org.opentcs.util.ExplainedBoolean;
import org.opentcs.util.event.EventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link VehicleCommAdapter} that does not really communicate with a physical vehicle but roughly
 * simulates one.
 *一个车辆适配器，该适配器并不和真实的车辆交流而是粗略的模拟的一个
 * @author Stefan Walter (Fraunhofer IML)
 */
public class LoopbackCommunicationAdapter
    extends BasicVehicleCommAdapter
    implements EventHandler,ConnectionEventListener<KeCongCommandResponse>,TelegramSender {

  /**
   * The name of the load handling device set by this adapter.
   */
  public static final String LHD_NAME = "default";
  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(LoopbackCommunicationAdapter.class);
  /**
   * An error code indicating that there's a conflict between a load operation and the vehicle's
   * current load state.
   */
  private static final String LOAD_OPERATION_CONFLICT = "cannotLoadWhenLoaded";
  /**
   * An error code indicating that there's a conflict between an unload operation and the vehicle's
   * current load state.
   */
  private static final String UNLOAD_OPERATION_CONFLICT = "cannotUnloadWhenNotLoaded";
  /**
   * The time by which to advance the velocity controller per step (in ms).
   */
  private static final int ADVANCE_TIME = 100;
 private RequestResponseMatcher requestResponseMatcher;
  /**
   * The adapter components factory.
   */
  private final KeCongAdapterComponentsFactory componentsFactory;
  /**
   * The kernel's executor.
   */
  private final ExecutorService kernelExecutor;
  /**
   * The task simulating the virtual vehicle's behaviour.
   */
  private CyclicTask vehicleacturalTask;
  /**
   * The boolean flag to check if execution of the next command is allowed.
   */
  /**
   * The vehicle to this comm adapter instance.
   */
  private final Vehicle vehicle;
  /**
   * The vehicle's load state.
   */
  private LoadState loadState = LoadState.EMPTY;
  /**
   * Whether the loopback adapter is initialized or not.
   */
  private boolean initialized;

  private StateRequesterTask stateRequesterTask;
 private UdpClientManager<KeCongRequestMessage> udpclientmanager;
  private boolean updatapostion;
  private final Map<MovementCommand, Integer> orderIds = new ConcurrentHashMap<>();
  
  
  
  /**
   * Creates a new instance.
   *
   * @param componentsFactory The factory providing additional components for this adapter.
   * @param vehicle The vehicle this adapter is associated with.
   * @param kernelExecutor The kernel's executor.
   */
  @Inject
  public LoopbackCommunicationAdapter(KeCongAdapterComponentsFactory componentsFactory,
                                    
                                      @Assisted Vehicle vehicle,
                                      @KernelExecutor ExecutorService kernelExecutor) {
    super(new KeCongProcessModel(vehicle),
         4,
          1,
         "CHARGE",
          kernelExecutor);
    this.vehicle = requireNonNull(vehicle, "vehicle");
    this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");
    this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
  }

  @Override
  public void initialize() {
       super.initialize();
   this.requestResponseMatcher = componentsFactory.createRequestResponseMatcher(this);
    this.stateRequesterTask = componentsFactory.createStateRequesterTask(e -> {
      requestResponseMatcher.enqueueRequest(new KeCongComandSerachRobotStatue());
      requestResponseMatcher.checkForSendingNextRequest();
     JudgeConnect();
    });
  
    getProcessModel().setVehicleState(Vehicle.State.IDLE);
    initialized = true;
  }
    
  private Byte count=0;
private   boolean connected=false;
private  boolean  isconnected=false;
void JudgeConnect()
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
  private  List<Point> lsPoints;
 public void setListPoints(List<Point> lsPoints)
 {
 this.lsPoints=lsPoints; 
 }
  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void terminate() {
    if (!isInitialized()) {
      return;
    }
    super.terminate();
    initialized = false;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    super.propertyChange(evt);

    if (!((evt.getSource()) instanceof KeCongProcessModel)) {
      return;
    }
     super.propertyChange(evt);
     System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.propertyChange()");
      // Handling of events from the vehicle gui panels start here
         if ( getProcessModel().getPeriodicEnable()) {
        stateRequesterTask.enable();
      }
      else {
        stateRequesterTask.disable();
       
      }
           
    if (Objects.equals(evt.getPropertyName(),
                     KeCongProcessModel .Attribute.RobotStatu.name())) 
       {
   getProcessModel().setVehicleEnergyLevel((int) Math.floor(100*getProcessModel().getRobotStatu().getBatterypower()));
       }
   
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
   }
   catch (Exception ex) {
     java.util.logging.Logger.getLogger(KeCongCommAdapter.class.getName()).log(Level.SEVERE, null, ex);
   }
    // Create task for vehicle simulation.
    vehicleacturalTask = new VehicleSimulationTask();
    Thread simThread = new Thread(vehicleacturalTask, getName() + "-simulationTask");
    simThread.start();
    super.enable();
  }

  @Override
  public synchronized void disable() {
    if (!isEnabled()) {
      return;
    }
    // Disable vehicle simulation.
    vehicleacturalTask.terminate();
    vehicleacturalTask = null;
     udpclientmanager.terminate();
    udpclientmanager = null;
    stateRequesterTask.disable();
    super.disable();
  }

  @Override
  public KeCongProcessModel getProcessModel() {
    return (KeCongProcessModel) super.getProcessModel();
  }

  @Override
  public synchronized void sendCommand(MovementCommand cmd) {
    requireNonNull(cmd, "cmd");
    // Reset the execution flag for single-step mode.
    // Don't do anything else - the command will be put into the sentQueue
    // automatically, where it will be picked up by the simulation task.
  }

  @Override
  public void processMessage(Object message) {
    // Process LimitSpeeed message which might pause the vehicle.
    if (message instanceof SetSpeedMultiplier) {
      SetSpeedMultiplier lsMessage = (SetSpeedMultiplier) message;
      int multiplier = lsMessage.getMultiplier();
      /* getProcessModel().setVehiclePaused(multiplier == 0);*/
    }
  }


  public synchronized void initVehiclePosition(String newPos) {
    kernelExecutor.submit(() -> {
      getProcessModel().setVehiclePosition(newPos);
    });
  }

  @Override
  public synchronized ExplainedBoolean canProcess(List<String> operations) {
    requireNonNull(operations, "operations");

    LOG.debug("{}: Checking processability of {}...", getName(), operations);
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

  @Override
  public void onEvent(Object event) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
//        Triple precisePosition = new Triple((long)currentPosition[0], (long)currentPosition[1], 0);
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
  private  RobotStatuResponseModel keResponseModel;
  @Override
  public void onIncomingTelegram(KeCongCommandResponse telegram) {
    // requireNonNull(telegram);
    //To change body of generated methods, choose Tools | Templates.
    //补充接收到的逻辑，如果是运行状态报文则更新已经连接。
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
      /*  final MovementCommand curCommand;
      synchronized (KeCongCommAdapter.this) {
      curCommand = getSentQueue().peek();
      }
      KeCongComandNavigateSearchResponse  kccnsr=((KeCongComandNavigateSearchResponse) telegram);
      onStateResponse(kccnsr.getNavigateStatuResponseModel());
      getProcessModel().setNavigateStatuResponseModel(kccnsr.getNavigateStatuResponseModel());
      if(kccnsr.getStatu()==2)
      {
      //导航中2
      getRequestResponseMatcher().enqueueRequest(new KeCongComandNavigateSearch());
      //getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
      System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.KeCongComandNavigateSearchResponse()+2");
      }*/
    /*else if (kccnsr.getStatu()==4)
    {
    //导航完成状态4
    int id=  ((KeCongComandNavigateSearchResponse) telegram).getTargetid();
    getProcessModel().setVehiclePosition("Point-"+String.format("%04d",id));
    if(curCommand!=null)
    {  int orderId = orderIds.remove(curCommand);
    if (orderId == id) {
    synchronized (KeCongCommAdapter.this) {
    MovementCommand sentCmd = getSentQueue().poll();
    if (sentCmd != null && sentCmd.equals(curCommand))
    {
    getProcessModel().commandExecuted(curCommand);
    KeCongCommAdapter.this.notify();
    }
    }
    kecongOperation(curCommand);
    }
    }
    if(getProcessModel().getVehicleState()!=Vehicle.State.CHARGING&&!lift)
    getProcessModel().setVehicleState(Vehicle.State.IDLE);
    else if(lift)
    {
    getRequestResponseMatcher().enqueueRequest(new KeCongComandRead(KeCongActionVar.FINSHI_TASK));
    }
    
    System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.KeCongComandNavigateSearchResponse()  +4");
    } */
    
    }
    requestResponseMatcher.checkForSendingNextRequest();
    }
    catch (Exception e) {
      System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.onIncomingTelegram()"+e.getMessage());
    }
  }
 private void updateReadVarModle(KeCongComandReadResponse keCongComandReadResponse)
  {
      String name =keCongComandReadResponse.getName().replaceAll(" ", "");
     byte[] var=keCongComandReadResponse.getValue();
     if(lift)
     {if(name.contains(KeCongActionVar.TRUCKLiftData))
     {
       System.out.println("TRUCKLiftData"+new String(var));
        TruckData  truckData1=new TruckData();
        truckData1.decodebytes(var);
      byte  f =  truckData1.getFinish();
     if(f==2&&truckData1.equal(truckData))
     {
    lift=false;
    truckData=new TruckData();
    truckData.setEnbalepid((byte)0);
    truckData.setLiftsv(0);
    truckData.setFinish((byte)3);
    truckData.setFinshtask((byte)0);
     KeCongComandWrite keCongComandWrite=  new KeCongComandWrite( KeCongActionVar.TRUCKLiftData,truckData.getbytes());
    getRequestResponseMatcher().enqueueRequest(keCongComandWrite);
    getProcessModel().setVehicleState(Vehicle.State.IDLE); 
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
    if(movementCommand.getOperation().equals(getProcessModel().getLoadOperation()))
      {
        liftorlowerfork(0.3f);
      }else if(movementCommand.getOperation().equals(getProcessModel().getUnloadOperation()))
      { 
        liftorlowerfork(0);
      }else if(movementCommand.getOperation().equals(getRechargeOperation()))
      {
         getProcessModel().setVehicleState(Vehicle.State.CHARGING);
        System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.kecongOperation().charge");     
      }
  }
  }
  private float liftfork=0;
  private boolean  lift=false;
 private TruckData  truckData;
  private void liftorlowerfork(float f)
  {
    liftfork=f;
    lift=true;
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
 public RequestResponseMatcher getRequestResponseMatcher() {
    return requestResponseMatcher;
  }
  @Override
  public void sendTelegram(KeCongRequestMessage request) {
    /* Object selectedItem = cmd.getStep().getDestinationPoint().getName();
    String destinationIdString = selectedItem instanceof Point
    ? ((Point) selectedItem).getName() : selectedItem.toString();
    int destinationid=new  OpentcsPointToKeCongPoint(destinationIdString).getIntPoint();
    System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.sendCommand()"+destinationid);
    orderIds.put(cmd, destinationid);
    KeCongComandNavigateControl kgcComandNavigateControl=  new KeCongComandNavigateControl(String.valueOf( destinationid),(byte)0,(byte)0);
    getRequestResponseMatcher().enqueueRequest(kgcComandNavigateControl); */
     requireNonNull(request);
    byte[] rawcontent = request.creatMessage();//要发送的数据
    kernelExecutor.submit(() -> {
      //  uDPUtils.sendImpl(request);
      udpclientmanager.send(request);
   });
    
    String string=DataConvertUtl.toHexString(rawcontent);
    
  }

  @Override
  protected synchronized boolean canSendNextCommand() {
    return super.canSendNextCommand();//后面这个可以添加判断逻辑限制发送的次数
        
  }

  @Override
  protected synchronized void connectVehicle() {
  }

  @Override
  protected synchronized void disconnectVehicle() {
  }

  @Override
  protected synchronized boolean isVehicleConnected() {
    return true;
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
  /**
   * Triggers a step in single step mode.
   */


  /**
   * A task simulating a vehicle's behaviour.
   */
  private class VehicleSimulationTask
      extends CyclicTask {

    /**
     * The time that has passed for the velocity controller whenever
     * <em>advanceTime</em> has passed for real.
     */
    private int simAdvanceTime;

    /**
     * Creates a new VehicleSimluationTask.
     */
    private VehicleSimulationTask() {
      super(0);
    }

    @Override
    protected void runActualTask() {
      final MovementCommand curCommand;
      synchronized (LoopbackCommunicationAdapter.this) {
        curCommand = getSentQueue().peek();
      }
    
     
        // If we were told to move somewhere, simulate the journey.
        LOG.debug("Processing MovementCommand...");
        final Step curStep = curCommand.getStep();
        // Simulate the movement.
        acturalMovement(curStep);
        // Simulate processing of an operation.
        if (!curCommand.isWithoutOperation()) {
          acturalOperation(curCommand.getOperation());
        }
        LOG.debug("Processed MovementCommand.");
        if (!isTerminated()) {
          // Set the vehicle's state back to IDLE, but only if there aren't 
          // any more movements to be processed.
          if (getSentQueue().size() <= 1 && getCommandQueue().isEmpty()) {
            getProcessModel().setVehicleState(Vehicle.State.IDLE);
          }
          // Update GUI.
          synchronized (LoopbackCommunicationAdapter.this) {
            MovementCommand sentCmd = getSentQueue().poll();
            // If the command queue was cleared in the meantime, the kernel
            // might be surprised to hear we executed a command we shouldn't
            // have, so we only peek() at the beginning of this method and
            // poll() here. If sentCmd is null, the queue was probably cleared
            // and we shouldn't report anything back.
            if (sentCmd != null && sentCmd.equals(curCommand)) {
              // Let the vehicle manager know we've finished this command.
              getProcessModel().commandExecuted(curCommand);
              LoopbackCommunicationAdapter.this.notify();
            
          
        
      }}}}
    }

    /**
     * Simulates the vehicle's movement. If the method parameter is null,
     * then the vehicle's state is failure and some false movement
     * must be simulated. In the other case normal step
     * movement will be simulated.
     *
     * @param step A step
     * @throws InterruptedException If an exception occured while sumulating
     */
    private void acturalMovement(Step step) {
      if (step.getPath() == null) {
      return;
      }
      Orientation orientation = step.getVehicleOrientation();
      String pointName = step.getDestinationPoint().getName();
      getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
    int destinationid=new  OpentcsPointToKeCongPoint(pointName).getIntPoint();
    System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.sendCommand()"+destinationid);
    KeCongComandNavigateControl kgcComandNavigateControl=  new KeCongComandNavigateControl(String.valueOf( destinationid),(byte)0,(byte)0);
    getRequestResponseMatcher().enqueueRequest(kgcComandNavigateControl); 
      
      
      /*  if (step.getPath() == null) {
      return;
      }
      
      Orientation orientation = step.getVehicleOrientation();
      long pathLength = step.getPath().getLength();
      int maxVelocity;
      switch (orientation) {
      case BACKWARD:
      maxVelocity = step.getPath().getMaxReverseVelocity();
      break;
      default:
      maxVelocity = step.getPath().getMaxVelocity();
      break;
      }
      String pointName = step.getDestinationPoint().getName();
      
      getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
      getProcessModel().getVelocityController().addWayEntry(new WayEntry(pathLength,
      maxVelocity,
      pointName,
      orientation));
      // Advance the velocity controller by small steps until the
      // controller has processed all way entries.
      while (getProcessModel().getVelocityController().hasWayEntries() && !isTerminated()) {
      WayEntry wayEntry = getProcessModel().getVelocityController().getCurrentWayEntry();
      Uninterruptibles.sleepUninterruptibly(ADVANCE_TIME, TimeUnit.MILLISECONDS);
      getProcessModel().getVelocityController().advanceTime(simAdvanceTime);
      WayEntry nextWayEntry = getProcessModel().getVelocityController().getCurrentWayEntry();
      if (wayEntry != nextWayEntry) {
      // Let the vehicle manager know that the vehicle has reached
      // the way entry's destination point.
      getProcessModel().setVehiclePosition(wayEntry.getDestPointName());
      }
      }*/
    }

    /**
     * Simulates an operation.
     *
     * @param operation A operation
     * @throws InterruptedException If an exception occured while simulating
     */
    private void acturalOperation(String operation) {
      /*   requireNonNull(operation, "operation");
      
      if (isTerminated()) {
      return;
      }
      
      LOG.debug("Operating...");
      final int operatingTime = getProcessModel().getOperatingTime();
      getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
      for (int timePassed = 0; timePassed < operatingTime && !isTerminated();
      timePassed += simAdvanceTime) {
      Uninterruptibles.sleepUninterruptibly(ADVANCE_TIME, TimeUnit.MILLISECONDS);
      getProcessModel().getVelocityController().advanceTime(simAdvanceTime);
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
      }*/
  }

  /**
   * The vehicle's possible load states.
   * 车辆的可能的负载状态
   */
  private enum LoadState {
    EMPTY,
    FULL;
  }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.comadpater;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.xinta.plc.model.CancelTransportModel;
import com.xinta.plc.model.VehicleParameterSetWithPLCMode;
import com.xinta.plc.model.VehicleStateModel;
import com.xintai.messageserviceinterface.InterfaceMessageService;
import com.xintai.messageserviceinterface.MessageService;
import com.xintai.plc.message.VehicleParameterSetWithPLC;
import com.xintai.plc.message.VehicleStatePLC;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.swing.Action;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.model.Vehicle;

import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.drivers.vehicle.messages.SetFinshMarkFromMes;
import org.opentcs.drivers.vehicle.messages.SetSpeedMultiplier;
import org.opentcs.util.CyclicTask;
import org.opentcs.util.ExplainedBoolean;
import org.opentcs.util.event.EventBus;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class PLCComAdapter  extends BasicVehicleCommAdapter {
 private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(PLCComAdapter.class);
  private final Vehicle vehicle;
  private final PLCAdapterComponentsFactory componentsFactory;
  private final ExecutorService kernelExecutor;
  private final EventBus eventBus;
  private  boolean  initialized;
  private StateRequesterTask stateRequesterTask;
  private VehicleActuralTask vehicleActuralCyclicTask;
 private  VehicleMessageSendTask vehicleMessageSendTask;
 private final  InterfaceMessageService interfaceMessageService;
   private final Map<MovementCommand, Integer> orderIds = new ConcurrentHashMap<>();
  
@Inject
    public PLCComAdapter(@Assisted Vehicle vehicle,
                            PLCAdapterComponentsFactory componentsFactory,
                            @KernelExecutor ExecutorService kernelExecutor,
                             @Nonnull @ApplicationEventBus EventBus eventBus) {
    super(new PLCProcessModel(vehicle), 3, 2, LoadAction.CHARGE, kernelExecutor);
    this.vehicle = requireNonNull(vehicle, "vehicle");
    this.componentsFactory = requireNonNull( componentsFactory, "componentsFactory");
    this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
    this.eventBus = requireNonNull(eventBus, "eventBus");
    interfaceMessageService=new MessageService("127.0.0.1",502);
  }
  
    private int processindex=0;
  @Override
   public void initialize() {
         if (isInitialized()) {
      return;
    }
    super.initialize();
     this.stateRequesterTask = componentsFactory.createStateRequesterTask(e -> {
         // if(master.isConnected())
          {
            switch(processindex)
         {  
              case 0:
             VehicleStatePLC vehicleStatePLC= interfaceMessageService.SendStateRequest();
              if(vehicleStatePLC!=null)
              {
               // System.out.println(vehicleStatePLC.GetVehicleStateModel().getErrorErrorCode());
                if(vehicleStatePLC.GetVehicleStateModel().getErrorErrorCode()==0)
                {
                  responsesQueue.add(vehicleStatePLC);
                }else 
                { 
                  VehicleStateModel currentVehicleStateModel= vehicleStatePLC.GetVehicleStateModel(); 
                    getProcessModel().setPreviousVehicleStateModel(currentVehicleStateModel);
                   CancelTransportModel cancelTransportModel=new CancelTransportModel(true, false);
                   getProcessModel().setCancelTransportModel(cancelTransportModel);
                }
              }
             processindex++;
              break;
         case 1:
           /*  ReadHoldingRegistersRequest vst=new ReadHoldingRegistersRequest(5,52,10);
           ReadHoldingRegistersResponse rvst=(ReadHoldingRegistersResponse) master.send(vst);
           parsevehiclesetting(rvst.getData());
           */
           /*  PLCProcessModelTO processModel=new PLCProcessModelTO();
           VehicleParameterSetWithPLCMode vps =processModel.getVehicleParameterSet();
           VehicleParameterSetWithPLC vpswplc= new  VehicleParameterSetWithPLC(vps.getAutorun(), vps.getVspeed(), vps.getAspeed());
           byte[]data= vpswplc.getdata();
           
           
           //1.读取设置变量的信息
           parsevehiclesetting(data);*/
           processindex=0;
           break;

           
            }    
          }    
    });
    getProcessModel().setVehicleState(Vehicle.State.IDLE);
    initialized = true;
  }
  private void parsevehiclesetting(byte[] data)
  {
    /*VehicleParameterSetWithPLC v=new VehicleParameterSetWithPLC();
    VehicleParameterSetWithPLCMode vp=v.decode(data);
    getProcessModel().setVehicleParameterSet(vp);*/
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
    interfaceMessageService.Init();
    try {
      vehicleMessageSendTask=new VehicleMessageSendTask();
     vehicleActuralCyclicTask = new VehicleActuralTask();
     new Thread(vehicleMessageSendTask,"messagesendtask").start();
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
                 PLCProcessModel .Attribute.VEHICLE_SETPARAMETERS.name())) 
       {
         VehicleParameterSetWithPLCMode vst= getProcessModel().getVehicleParameterSet();
         if(!vst.isIswrite()) return;
         VehicleParameterSetWithPLC vstp=new VehicleParameterSetWithPLC(vst.getHeartbeatsignal(),vst.getAgvvspeed(),
         vst.getAgvaspeed(),vst.getRemotestart(),vst.getNavigationtask(),
         vst.getNextsite(),vst.getNexttwosite(),vst.getTargetsitecardirection(),
         vst.getTargetsite(),vst.getCurrentschedulingtask(),vst.getMaterialcode(),
         vst.getChargingpilestate());
      interfaceMessageService.SendSettingTOPLC(vstp);   
       }
  }
 @Override
 public synchronized void disable() {
    if (!isEnabled()) {
      return;
    }
    super.disable();
   interfaceMessageService.DisConnect();
    vehicleActuralCyclicTask.terminate();
    vehicleActuralCyclicTask = null;
    vehicleMessageSendTask.terminate();
    vehicleMessageSendTask=null;
    stateRequesterTask.disable();
  }
@Override
  public void terminate() {
      super.terminate();
      //eventBus.unsubscribe(this);
    initialized=false;
  }
  private  final Queue<MovementCommand> movementcomandbufferQueue = new LinkedBlockingQueue<>();
    private Queue<MovementCommand> getMovementCommandsBufferQueue()
    {
    return  movementcomandbufferQueue;
    }
   
  @Override
  public void sendCommand(MovementCommand cmd)
      throws IllegalArgumentException {
    getMovementCommandsBufferQueue().add(cmd);
    System.out.println(cmd.toString());
  }
  //仅允许单步运行
  @Override
  protected  boolean  canSendNextCommand()
  {
  return super.canSendNextCommand()&!getProcessModel().isSingleStepModeEnabled();
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
  @Override
  public void processMessage(Object message) {
  if (message instanceof SetSpeedMultiplier) {
      SetSpeedMultiplier lsMessage = (SetSpeedMultiplier) message;
      int multiplier = lsMessage.getMultiplier();
     //  getProcessModel().setVehiclePaused(multiplier == 0);
    }else if(message instanceof SetFinshMarkFromMes )
    {
      SetFinshMarkFromMes setfinshmarkfrommes=(SetFinshMarkFromMes)message;
    String finshmark =  setfinshmarkfrommes.getFinshMark();
    Boolean resultBoolean=Boolean.valueOf(finshmark).booleanValue();
    getProcessModel().setFinshmarkfromes(resultBoolean);
      System.out.println(resultBoolean.booleanValue());
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
  return  true;
  }
      @Override
  public final PLCProcessModel getProcessModel() {
    return (PLCProcessModel) super.getProcessModel();
  }
   @Override
  protected VehicleProcessModelTO createCustomTransferableProcessModel() 
  {
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
  
    private  final Queue<VehicleStatePLC> responsesQueue = new LinkedBlockingQueue<>();
    private Queue<VehicleStatePLC> getComandVehicleStateResponsesQueue()
    {
    return  responsesQueue;
    }
  
  private class VehicleActuralTask
      extends CyclicTask {
  private volatile boolean  lastmcdmark=false;
    private VehicleActuralTask() {
      super(0);
    }

    @Override
    protected void runActualTask()
    {
    VehicleStatePLC vehicleStatePLC;
    synchronized(PLCComAdapter.this)
    {
      vehicleStatePLC= getComandVehicleStateResponsesQueue().poll();
    }
   if(vehicleStatePLC!=null)
    onStateResponse(vehicleStatePLC);      
    }

    private void onStateResponse(VehicleStatePLC vehiclestateplc ) {
   
   VehicleStateModel previousVehicleStateModel=getProcessModel().getPreviousVehicleStateModel();
     VehicleStateModel currentVehicleStateModel= vehiclestateplc.GetVehicleStateModel(); 
    getProcessModel().setPreviousVehicleStateModel(currentVehicleStateModel);
    if(lastmcdmark)//如果执行到最后一步逻辑，则以下步骤不再执行。
      return;
    updatepostion(currentVehicleStateModel,previousVehicleStateModel);
    checkresponseisright(currentVehicleStateModel,previousVehicleStateModel);
    updatestate(currentVehicleStateModel, previousVehicleStateModel);
    updateorder(currentVehicleStateModel, previousVehicleStateModel);
    }
    private boolean checkresponseisright(VehicleStateModel curVehicleStateModel,VehicleStateModel previousStateModel)
    { 
      /*  if(curVehicleStateModel.getCurrentSite()==previousStateModel.getCurrentSite())
      return false;*/
    int next= curVehicleStateModel.getNextSite();
    int nexttwo=curVehicleStateModel.getNextTwoSite();
   int storenext= getProcessModel().getCurrentnavigationpoint();
   int storenexttwo=getProcessModel().getNextcurrentnavigationpoint();
    if(next==storenext&storenexttwo==nexttwo)
    {
      MovementCommand movementCommand= getMovementCommandsBufferQueue().poll();
    if(movementCommand!=null)
    { orderIds.putIfAbsent(movementCommand, nexttwo);
    return true;
    }
    }
    return false;
    }
    private void updatepostion(VehicleStateModel curVehicleStateModel,VehicleStateModel previousStateModel)
    {
      if(curVehicleStateModel.getCurrentSite()==previousStateModel.getCurrentSite())
      return;
       getProcessModel().setVehiclePosition("Point-"+String.format("%04d",curVehicleStateModel.getCurrentSite()));
    }
    private  void  updatestate(VehicleStateModel curVehicleStateModel,VehicleStateModel previousStateModel)
   {
     if(curVehicleStateModel.getNavigationalState()==previousStateModel.getNavigationalState())
     return;
      getProcessModel().setVehicleState(translateState(curVehicleStateModel.getNavigationalState()));
     }
     private Vehicle.State  translateState(int data)
     {
     switch(data)
     {
     case 1:
     return Vehicle.State.IDLE;
     case 2:
     return Vehicle.State.EXECUTING;
     case 3:
     return Vehicle.State.CHARGING;
     default:
     return  Vehicle.State.UNAVAILABLE;
     }
    }
     private void excutefinalaction(MovementCommand cmd)
     {System.out.println("1");
       if(cmd.isFinalMovement()&cmd.isWithoutOperation())
       {
      System.out.println("2");
    System.out.println("3");
     System.out.println("com.xintai.plc.comadpater.PLCComAdapter.VehicleActuralTask.excutefinalaction()");
     excuteActionAfterFinalOperation(cmd);
     System.out.println("4");   
       }else if(!cmd.isWithoutOperation()&cmd.isFinalMovement())
       {    
     System.out.println("5");
     getProcessModel().setSingleStepModeEnabled(true);
     getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
     Thread t=new Thread(new ExcuteFinalAction(cmd),"excutefinalaction");
     t.start();
       }    
     }
     private void excuteActionAfterFinalOperation(MovementCommand cmd)
     {
        if(cmd!= getSentQueue().peek())
       {
       return;
       }
     getSentQueue().poll();
     orderIds.remove(cmd);
     getProcessModel().commandExecuted(cmd);
     getProcessModel().setSingleStepModeEnabled(false);
     getProcessModel().setVehicleState(Vehicle.State.IDLE);
     getProcessModel().setCurrentnavigationpoint(0);
     getProcessModel().setNextcurrentnavigationpoint(0);
     lastmcdmark=false;
     }
     private  class ExcuteFinalAction implements Runnable
     {

     private final MovementCommand cmd;

     public ExcuteFinalAction(MovementCommand cmd)
     {
       this.cmd=cmd;
     }

     @Override
     public void run() 
     {
       synchronized(getProcessModel().getObjectForMesFinshWork())
       {  
         while (!getProcessModel().isFinshmarkfromes()) 
       {
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
       getProcessModel().setFinshmarkfromes(false);
     }
     
     }
    private  void  updateorder(VehicleStateModel curVehicleStateModel,VehicleStateModel previousStateModel)
   {
     if(curVehicleStateModel.getCurrentSite()== 0) {
     return;
     }
     // If the last finished order ID hasn't changed, don't bother.
     if (curVehicleStateModel.getCurrentSite()== previousStateModel.getCurrentSite()) {
     return;
     }
     // Check if the new finished order ID is in the queue of sent orders.
     // If yes, report all orders up to that one as finished.
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
     if( cmd.isFinalMovement()&("Point-"+String.format("%04d",curVehicleStateModel.getCurrentSite())) == null ? cmd.getFinalDestination().getName() == null : ("Point-"+String.format("%04d",curVehicleStateModel.getCurrentSite())).equals(cmd.getFinalDestination().getName()))
     {
     lastmcdmark=true;
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
    
  }
  
  private  class VehicleMessageSendTask extends CyclicTask
  {

    public VehicleMessageSendTask() {
      super(0);
    }
    
    @Override
    protected void runActualTask() {
    //先不用加同步
      MovementCommand movementCommand= getMovementCommandsBufferQueue().peek();
      if(movementCommand!=null)
      {
        if(getProcessModel().getPreviousVehicleStateModel().getAgvRunState()==2)
      interfaceMessageService.SendNavigateComand(movementCommand,getProcessModel());
      }
    }
  }
     
}

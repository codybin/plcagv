/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.comadpater;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;
import com.xinta.plc.model.VehicleParameterSetWithPLCMode;
import com.xinta.plc.model.VehicleStateModel;
import com.xintai.adapter.OpentcsPointToKeCongPoint;
import com.xintai.plc.message.NavigateControl;
import com.xintai.plc.message.VehicleParameterSetWithPLC;
import com.xintai.plc.message.VehicleStatePLC;
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
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;

import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
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
  private  ModbusMaster master;
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
  }
  
    private  void initplc () throws ModbusInitException
    {
     IpParameters ipParameters = new IpParameters();
        ipParameters.setHost("127.0.0.1");//后续可以用getprocemodle传进来
        ipParameters.setPort(502);
        ipParameters.setEncapsulated(false);
        ModbusFactory modbusFactory = new ModbusFactory();
        master = modbusFactory.createTcpMaster(ipParameters, true);
        master.setTimeout(8000);
        master.setRetries(0);
        master.init();
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
            try {
            switch(processindex)
         {  
              case 0:
              ReadHoldingRegistersRequest readholdingregisters=new ReadHoldingRegistersRequest(5,0,50);
              ReadHoldingRegistersResponse readHoldingRegistersResponse=(ReadHoldingRegistersResponse) master.send(readholdingregisters);
              responsesQueue.add(readHoldingRegistersResponse);
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
            catch (ModbusTransportException ex) {
              
              Logger.getLogger(PLCComAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }    
          }    
    });
     
   
    getProcessModel().setVehicleState(Vehicle.State.IDLE);
    initialized = true;
  }
  private void parsevehiclesetting(byte[] data)
  {
  VehicleParameterSetWithPLC v=new VehicleParameterSetWithPLC();
  VehicleParameterSetWithPLCMode vp=v.decode(data);
  getProcessModel().setVehicleParameterSet(vp);
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
     initplc();
   }
   catch (ModbusInitException ex) {
     Logger.getLogger(PLCComAdapter.class.getName()).log(Level.SEVERE, null, ex);
   }
    try {
     vehicleActuralCyclicTask = new VehicleActuralTask();
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
         
      try {
        VehicleParameterSetWithPLCMode vst= getProcessModel().getVehicleParameterSet();
        if(!vst.isIswrite()) return;
        VehicleParameterSetWithPLC vstp=new VehicleParameterSetWithPLC(vst.getAutorun(),vst.getVspeed(),vst.getAspeed());
        WriteRegistersRequest writeRegistersRequest=new WriteRegistersRequest(5, 52, vstp.getdata());
         master.send(writeRegistersRequest);
         System.out.println("com.xintai.plc.comadpater.PLCComAdapter.propertyChange()"+vstp.toString());
      }
      catch (ModbusTransportException ex) {
        Logger.getLogger(PLCComAdapter.class.getName()).log(Level.SEVERE, null, ex);
      }
       }
  }
 @Override
 public synchronized void disable() {
    if (!isEnabled()) {
      return;
    }
    super.disable();
    master.destroy();
    vehicleActuralCyclicTask.terminate();
    vehicleActuralCyclicTask = null;
    stateRequesterTask.disable();
  }
@Override
  public void terminate() {
      super.terminate();
      //eventBus.unsubscribe(this);
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
    
    try {
      getProcessModel().setNextcurrentnavigationpoint(destinationid);
      NavigateControl navigateControl =new NavigateControl().setCurrentstation(getProcessModel().getCurrentnavigationpoint())
                                            .setNextstation(getProcessModel().getNextcurrentnavigationpoint())
                                            .setOperation(0)
                                            .setTargetstation(1);
         System.out.println("com.xintai.plc.comadpater.PLCComAdapter.sendCommand()"+navigateControl.toString());
          WriteRegistersRequest writeRegistersRequest=new WriteRegistersRequest(5,60,navigateControl.encodedata());
          WriteRegistersResponse writeRegistersResponse=(WriteRegistersResponse)master.send(writeRegistersRequest);
          orderIds.put(cmd, destinationid);
    }
    catch (ModbusTransportException ex) {
      Logger.getLogger(PLCComAdapter.class.getName()).log(Level.SEVERE, null, ex);
    }
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
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
  return  master.isConnected();
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
  
    private  final Queue<ReadHoldingRegistersResponse> responsesQueue = new LinkedBlockingQueue<>();
    private Queue<ReadHoldingRegistersResponse> getComandVehicleStateResponsesQueue()
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
    ReadHoldingRegistersResponse readHoldingRegistersResponse;
    synchronized(PLCComAdapter.this)
    {
      readHoldingRegistersResponse= getComandVehicleStateResponsesQueue().poll();
    }
   if(readHoldingRegistersResponse!=null)
    onStateResponse(readHoldingRegistersResponse);      
    }

    private void onStateResponse(ReadHoldingRegistersResponse readHoldingRegistersResponse ) {
    byte[] data= readHoldingRegistersResponse.getData();
    VehicleStatePLC vehiclestateplc=new VehicleStatePLC(data);
   VehicleStateModel previousVehicleStateModel=getProcessModel().getPreviousVehicleStateModel();
     VehicleStateModel currentVehicleStateModel= vehiclestateplc.GetVehicleStateModel(); 
    getProcessModel().setPreviousVehicleStateModel(currentVehicleStateModel);
    updatepostion(currentVehicleStateModel,previousVehicleStateModel);
    updatestate(currentVehicleStateModel, previousVehicleStateModel);
    updateorder(currentVehicleStateModel, previousVehicleStateModel);
    }
    private void updatepostion(VehicleStateModel curVehicleStateModel,VehicleStateModel previousStateModel)
    {
    if(curVehicleStateModel.getCurrentposition()==previousStateModel.getCurrentposition())
      return;
    getProcessModel().setVehiclePosition("Point-"+String.format("%04d",curVehicleStateModel.getCurrentposition()));
    }
    private  void  updatestate(VehicleStateModel curVehicleStateModel,VehicleStateModel previousStateModel)
   {
    if(curVehicleStateModel.getNavigatestate()==previousStateModel.getNavigatestate())
      return;
    getProcessModel().setVehicleState(translateState(curVehicleStateModel.getNavigatestate()));
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
    private  void  updateorder(VehicleStateModel curVehicleStateModel,VehicleStateModel previousStateModel)
   {
      if(curVehicleStateModel.getCurrentposition()== 0) {
      return;
    }
    // If the last finished order ID hasn't changed, don't bother.
    if (curVehicleStateModel.getCurrentposition()== previousStateModel.getCurrentposition()) {
    return;
    }
    // Check if the new finished order ID is in the queue of sent orders.
    // If yes, report all orders up to that one as finished.
    if (!orderIds.containsValue(curVehicleStateModel.getCurrentposition())) {
      LOG.debug("{}: Ignored finished order ID {} (reported by vehicle, not found in sent queue).",
                getName(),
                curVehicleStateModel.getCurrentposition());
      return;
    }
    Iterator<MovementCommand> cmdIter = getSentQueue().iterator();
    boolean finishedAll = false;
    while (!finishedAll && cmdIter.hasNext()) {
      MovementCommand cmd = cmdIter.next();
      cmdIter.remove();
      int orderId = orderIds.remove(cmd);
      if (orderId == curVehicleStateModel.getCurrentposition()) {
      finishedAll = true;
      }
      LOG.info("{}: Reporting command with order ID {} as executed: {}", getName(), orderId, cmd);
      getProcessModel().commandExecuted(cmd);
    }
    }
    
  }
}

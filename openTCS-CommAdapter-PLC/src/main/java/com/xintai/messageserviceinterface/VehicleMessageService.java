
package com.xintai.messageserviceinterface;

import com.google.inject.Inject;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;
import com.xintai.adapter.OpentcsPointToKeCongPoint;
import com.xintai.plc.comadpater.PLCComAdapter;
import com.xintai.plc.comadpater.PLCCommAdapterConfiguration;
import com.xintai.plc.comadpater.PLCProcessModel;
import com.xintai.plc.message.NavigateControl;
import com.xintai.plc.message.VehicleParameterSetWithPLC;
import com.xintai.plc.message.VehicleStatePLC;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opentcs.data.model.Point;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.VehicleProcessModel;

/**
 *
 * @author Lenovo
 */
public class VehicleMessageService implements InterfaceMessageService{
  private final PLCCommAdapterConfiguration pLCCommAdapterConfiguration;
 @Inject
  public VehicleMessageService( PLCCommAdapterConfiguration pLCCommAdapterConfiguration) {
    this.pLCCommAdapterConfiguration = requireNonNull(pLCCommAdapterConfiguration, "configuration");
  }

 private  ModbusMaster master;

  @Override
  public void SendNavigateComand(MovementCommand movementCommand,VehicleProcessModel  ProcessModel) {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
 if(!(ProcessModel instanceof  PLCProcessModel))
 return;
   PLCProcessModel PLCProcessModel=  (PLCProcessModel) ProcessModel;
  String operationString= movementCommand.getFinalOperation();
    System.out.println("com.xintai.messageserviceinterface.VehicleMessageService.SendNavigateComand()"+movementCommand.toString());
  DispacherTaskState dispacherTaskState= operationtodispacherTaskState(operationString);
   if(PLCProcessModel.getVehicleTaskState().getDispacherTaskState()!=dispacherTaskState)
   {
   TaskInteractionInformation taskInteractionInformation=new TaskInteractionInformation();
   taskInteractionInformation.setDispacherTaskState(dispacherTaskState);
   taskInteractionInformation.setMaterialnum(10);
   taskInteractionInformation.setChargerStaionState(ChargerStaionState.ChargerTail_out);
   sendtaskinformation(taskInteractionInformation);
   }
  Object finalPoint= movementCommand.getFinalDestination().getName();
   int finalpoint=PointTOINT(finalPoint); 
     boolean result=false;
       Object selectedItem = movementCommand.getStep().getDestinationPoint().getName();
    int destinationid = PointTOINT(selectedItem); 
    System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.sendCommand()"+destinationid);
    try {
      if( PLCProcessModel.getNextcurrentnavigationpoint()!=destinationid)
         PLCProcessModel.setNextcurrentnavigationpoint(destinationid);
      int finaldirection=0;
      if(PLCProcessModel.getFinaldirection()!=null)
        finaldirection= Integer.parseInt(PLCProcessModel.getFinaldirection());
    NavigateControl navigateControl =new NavigateControl().setNextsite(PLCProcessModel.getCurrentnavigationpoint())
                                            .setNexttwosite(PLCProcessModel.getNextcurrentnavigationpoint())
                                            .setRemotestart(0)
                                            .setNavigationtask(1)
                                            .setTargetsitecardirection(finaldirection)
                                            .setTargetsite(finalpoint);
         System.out.println("com.xintai.plc.comadpater.PLCComAdapter.sendCommand()"+navigateControl.toString());
          WriteRegistersRequest writeRegistersRequest=new WriteRegistersRequest(slaveid,pLCCommAdapterConfiguration.navigateoffset(),navigateControl.encodedata());
          WriteRegistersResponse writeRegistersResponse=(WriteRegistersResponse)master.send(writeRegistersRequest);
          if(writeRegistersResponse!=null)
            result=true;
       //   orderIds.put(cmd, destinationid);
    }
    catch (ModbusTransportException ex) {
      result=false;
      Logger.getLogger(PLCComAdapter.class.getName()).log(Level.SEVERE, null, ex);
    } 
   setConnected(result);
  }
private DispacherTaskState operationtodispacherTaskState(String opertaion)
{
  switch(opertaion)
  {
    case "Charge":
      return  DispacherTaskState.Charging_TASK;
    case "Unload":
      return  DispacherTaskState.UNLoadEmpty_TASK;
     case "Load cargo":
       return DispacherTaskState.LoadEmpty_TASK;
  default:
      return null;
  }
}
private void sendtaskinformation(TaskInteractionInformation taskInteractionInformation)
{
EncodeTaskInterActionInformation encodeTaskInterActionInformation=new EncodeTaskInterActionInformation(taskInteractionInformation);
   short[] data= encodeTaskInterActionInformation.EncodeMessage();
       WriteRegistersRequest writeRegistersRequest;
    try {
      writeRegistersRequest = new WriteRegistersRequest(slaveid,2016,data);
        WriteRegistersResponse writeRegistersResponse=(WriteRegistersResponse)master.send(writeRegistersRequest);
    }
    catch (ModbusTransportException ex) {
      Logger.getLogger(VehicleMessageService.class.getName()).log(Level.SEVERE, null, ex);
    }
        
//

}

  private int PointTOINT(Object selectedItem) {
      String destinationIdString = selectedItem instanceof Point
              ? ((Point) selectedItem).getName() : selectedItem.toString();
      int destinationid=new  OpentcsPointToKeCongPoint(destinationIdString).getIntPoint();
    return destinationid;
  }
  @Override
  public VehicleStatePLC SendStateRequest() {
       ReadHoldingRegistersRequest readholdingregisters;
   VehicleStatePLC vehicleStatePLC;
   try {
     readholdingregisters = new ReadHoldingRegistersRequest(slaveid,pLCCommAdapterConfiguration.stateoffset(),pLCCommAdapterConfiguration.statelength());
     ReadHoldingRegistersResponse readHoldingRegistersResponse=(ReadHoldingRegistersResponse) master.send(readholdingregisters);
     if(readHoldingRegistersResponse!=null)
    vehicleStatePLC=new VehicleStatePLC(readHoldingRegistersResponse.getData());
     else
       vehicleStatePLC=null;
   
   }
   catch (ModbusTransportException ex) {
     Logger.getLogger(VehicleMessageService.class.getName()).log(Level.SEVERE, null, ex);
     vehicleStatePLC=null;
   }
   setConnected(vehicleStatePLC!=null); 
    return  vehicleStatePLC;
  }

  @Override
  public boolean Init(IPParameter iPParameter) {
        boolean resut=false;
        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost(iPParameter.getIp());//后续可以用getprocemodle传进来
        ipParameters.setPort(iPParameter.getPort());
        slaveid=iPParameter.getSlaveid();
        ipParameters.setEncapsulated(false);
        try { 
        ModbusFactory modbusFactory = new ModbusFactory();
       if(master==null)
          master = modbusFactory.createTcpMaster(ipParameters, true);
        master.setTimeout(3000);
        master.setRetries(2);
        master.init();
        resut=true;
    }
    catch (ModbusInitException ex) {
     resut=false;
    }
        System.out.println("com.xintai.messageserviceinterface.VehicleMessageService  result"+resut);
       setConnected(resut); 
      return  resut;
    
  }

  @Override
  public boolean Connect() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean DisConnect() {
    if(master!=null)
   master.destroy();
  return true;
  }

  @Override
  public void SendSettingTOPLC(VehicleParameterSetWithPLC vehicleParameterSetWithPLC) {
   boolean result=false;
   try {
     WriteRegistersRequest writeRegistersRequest=new WriteRegistersRequest(slaveid,pLCCommAdapterConfiguration.settingoffset(), vehicleParameterSetWithPLC.getdata());
      WriteRegistersResponse writeRegistersResponse=(WriteRegistersResponse) master.send(writeRegistersRequest);
      if(writeRegistersResponse!=null)
      result=true;
     System.out.println("com.xintai.plc.comadpater.PLCComAdapter.propertyChange()"+vehicleParameterSetWithPLC.toString());
   }
   catch (ModbusTransportException ex) {
     Logger.getLogger(VehicleMessageService.class.getName()).log(Level.SEVERE, null, ex);
   }
      setConnected(result); 
  }

 
  private void OnConnect() {
   ConnectListenner.Action();
  }


  private void OnDisConnect() {
   disCConnectListenner.Action();
  }
private  PLCConnectListenner disCConnectListenner;
  @Override
  public void SetDisConnectEvent(PLCConnectListenner pLCConnectListenner) {
  disCConnectListenner=pLCConnectListenner;
  }
private  PLCConnectListenner ConnectListenner;
  @Override
  public void SetConnectEvent(PLCConnectListenner plccl) {
   ConnectListenner=plccl;
  }


  private void setConnected(boolean  isconnect)
  {
    if(isconnect!=master.isConnected())
    {
      master.setConnected(isconnect);
      if(!master.isConnected())
        OnDisConnect();
      else
        OnConnect();
    }
  }

private int slaveid;
  @Override
  public boolean HeartBeat() {
    boolean result=false;
   try {
     WriteRegistersRequest writeRegistersRequest=new WriteRegistersRequest(slaveid, pLCCommAdapterConfiguration.heartbeat(),new short[]{1} );
   WriteRegistersResponse writeRegistersResponse=(WriteRegistersResponse)master.send(writeRegistersRequest);
     if(writeRegistersResponse!=null)
     result=true;
   }
   catch (ModbusTransportException ex) {
 
     Logger.getLogger(VehicleMessageService.class.getName()).log(Level.SEVERE, null, ex);
   }
    setConnected(result); 
   return result;
  }

  @Override
  public boolean IsInitial() {
 return master.isInitialized();
  }

  @Override
  public boolean IsConnected() {
   return  master.isConnected();
  }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.messageserviceinterface;

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
import com.xintai.plc.comadpater.PLCProcessModel;
import com.xintai.plc.message.NavigateControl;
import com.xintai.plc.message.VehicleParameterSetWithPLC;
import com.xintai.plc.message.VehicleStatePLC;
import java.awt.event.ActionListener;
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

 private  ModbusMaster master;
 
  @Override
  public void SendNavigateComand(MovementCommand movementCommand,VehicleProcessModel  ProcessModel) {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
 if(!(ProcessModel instanceof  PLCProcessModel))
 return;
   boolean result=false;
  PLCProcessModel PLCProcessModel=  (PLCProcessModel) ProcessModel;
       Object selectedItem = movementCommand.getStep().getDestinationPoint().getName();
    String destinationIdString = selectedItem instanceof Point
        ? ((Point) selectedItem).getName() : selectedItem.toString();
    int destinationid=new  OpentcsPointToKeCongPoint(destinationIdString).getIntPoint(); 
    System.out.println("com.xintai.vehicle.comadpter.KeCongCommAdapter.sendCommand()"+destinationid);
    
    try {
      if( PLCProcessModel.getNextcurrentnavigationpoint()!=destinationid)
         PLCProcessModel.setNextcurrentnavigationpoint(destinationid);
    NavigateControl navigateControl =new NavigateControl().setNextsite(PLCProcessModel.getCurrentnavigationpoint())
                                            .setNexttwosite(PLCProcessModel.getNextcurrentnavigationpoint())
                                            .setRemotestart(0)
                                            .setNavigationtask(1)
                                            .setTargetsitecardirection(0)
                                            .setTargetsite(0);
         System.out.println("com.xintai.plc.comadpater.PLCComAdapter.sendCommand()"+navigateControl.toString());
          WriteRegistersRequest writeRegistersRequest=new WriteRegistersRequest(slaveid,1999+59,navigateControl.encodedata());
          WriteRegistersResponse writeRegistersResponse=(WriteRegistersResponse)master.send(writeRegistersRequest);
          result=true;
       //   orderIds.put(cmd, destinationid);
    }
    catch (ModbusTransportException ex) {
      result=false;
      Logger.getLogger(PLCComAdapter.class.getName()).log(Level.SEVERE, null, ex);
    } 
   setConnected(result);
  }

  @Override
  public VehicleStatePLC SendStateRequest() {
       ReadHoldingRegistersRequest readholdingregisters;
   VehicleStatePLC vehicleStatePLC;
   try {
     readholdingregisters = new ReadHoldingRegistersRequest(slaveid,999,50);
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
          System.out.println("com.xintai.messageserviceinterface.VehicleMessageService.Init()"+master.isInitialized());
    }
    catch (ModbusInitException ex) {
     resut=false;
    }
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
   try {
     WriteRegistersRequest writeRegistersRequest=new WriteRegistersRequest(slaveid,1999+52, vehicleParameterSetWithPLC.getdata());
     master.send(writeRegistersRequest);
     setConnected(true); 
     System.out.println("com.xintai.plc.comadpater.PLCComAdapter.propertyChange()"+vehicleParameterSetWithPLC.toString());
   }
   catch (ModbusTransportException ex) {
       setConnected(false); 
     Logger.getLogger(VehicleMessageService.class.getName()).log(Level.SEVERE, null, ex);
   }
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
     WriteRegistersRequest writeRegistersRequest=new WriteRegistersRequest(slaveid, 1999,new short[]{0,1} );
     master.send(writeRegistersRequest);
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

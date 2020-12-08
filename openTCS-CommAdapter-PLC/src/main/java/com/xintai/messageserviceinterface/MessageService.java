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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opentcs.data.model.Point;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.VehicleProcessModel;

/**
 *
 * @author Lenovo
 */
public class MessageService implements InterfaceMessageService{
 private String ip;

  public MessageService(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
 private int port;
 private  ModbusMaster master;
 
  @Override
  public void SendNavigateComand(MovementCommand movementCommand,VehicleProcessModel  ProcessModel) {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
 if(!(ProcessModel instanceof  PLCProcessModel))
 return;
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
          WriteRegistersRequest writeRegistersRequest=new WriteRegistersRequest(5,59,navigateControl.encodedata());
          WriteRegistersResponse writeRegistersResponse=(WriteRegistersResponse)master.send(writeRegistersRequest);
       //   orderIds.put(cmd, destinationid);
    }
    catch (ModbusTransportException ex) {
      Logger.getLogger(PLCComAdapter.class.getName()).log(Level.SEVERE, null, ex);
    } 
  }

  @Override
  public VehicleStatePLC SendStateRequest() {
       ReadHoldingRegistersRequest readholdingregisters;
   VehicleStatePLC vehicleStatePLC;
   try {
     readholdingregisters = new ReadHoldingRegistersRequest(5,0,50);
     ReadHoldingRegistersResponse readHoldingRegistersResponse=(ReadHoldingRegistersResponse) master.send(readholdingregisters);
     if(readHoldingRegistersResponse!=null)
  vehicleStatePLC=new VehicleStatePLC(readHoldingRegistersResponse.getData());
     else
       vehicleStatePLC=null;
   
   }
   catch (ModbusTransportException ex) {
     Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
     vehicleStatePLC=null;
   }
    return  vehicleStatePLC;
  }

  @Override
  public boolean Init() {
  boolean resut=false;
     IpParameters ipParameters = new IpParameters();
        ipParameters.setHost(ip);//后续可以用getprocemodle传进来
        ipParameters.setPort(port);
        ipParameters.setEncapsulated(false);
        try { 
          ModbusFactory modbusFactory = new ModbusFactory();
        master = modbusFactory.createTcpMaster(ipParameters, true);
        master.setTimeout(8000);
        master.setRetries(0);
        master.init();
        resut=true;
    }
    catch (ModbusInitException e) {
      resut=false;
    }
       
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
     WriteRegistersRequest writeRegistersRequest=new WriteRegistersRequest(5, 52, vehicleParameterSetWithPLC.getdata());
     master.send(writeRegistersRequest);
     System.out.println("com.xintai.plc.comadpater.PLCComAdapter.propertyChange()"+vehicleParameterSetWithPLC.toString());
   }
   catch (ModbusTransportException ex) {
     Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
   }
  }
  
}

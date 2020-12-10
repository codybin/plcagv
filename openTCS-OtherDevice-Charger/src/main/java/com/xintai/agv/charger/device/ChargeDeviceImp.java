/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.agv.charger.device;

import com.serotonin.io.serial.SerialParameters;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.ReadCoilsRequest;
import com.serotonin.modbus4j.msg.ReadCoilsResponse;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.WriteCoilRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lenovo
 */
public class ChargeDeviceImp  implements ChargerDevice_Interface{

  private final SerialParameters serialParameters;
  private final int slaveid;
  private ModbusMaster master;
  private boolean init;

  public ChargeDeviceImp(
       int slavid,
  String comport,
  int baudrate,
  int datbit, 
  int parity,
  int stopbit,
 int flowcontrolin)
{     this.slaveid=slavid;
       serialParameters = new SerialParameters();
        serialParameters.setCommPortId(comport);
        serialParameters.setBaudRate(baudrate);
        serialParameters.setDataBits(datbit);
        serialParameters.setParity(parity);
        serialParameters.setStopBits(stopbit);
        serialParameters.setFlowControlIn(flowcontrolin);
}
  
   private void init() throws ModbusInitException
  {
        master = new ModbusFactory().createRtuMaster(serialParameters);
        master.setTimeout(200);
        master.setRetries(3);
        master.init();
  }
   @Override
   public boolean InitDevice()
   { init=false;
    try {
      init();
      init=true;
    }
    catch (ModbusInitException ex) {
      Logger.getLogger(ChargeDeviceImp.class.getName()).log(Level.SEVERE, null, ex);
      init=false;
    }
   return init;
    }
@Override
  public boolean isInit() {
    return master.isInitialized();
  }

  public void setInit(boolean init) {
    this.init = init;
  }
   
  @Override
  public void StopCharger() {
    try {
      master.send(new WriteCoilRequest(slaveid, 7, true));
    }
    catch (ModbusTransportException ex) {
      Logger.getLogger(ChargeDeviceImp.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void StartCharger() {
   try {
      master.send(new WriteCoilRequest(slaveid, 8, true));
    }
    catch (ModbusTransportException ex) {
      Logger.getLogger(ChargeDeviceImp.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void ReadCurrent() {
   
  }

  @Override
  public void ReadVoltage() {
   
  }

  @Override
  public void ReadCurrentCapacity() {
   
  }

  @Override
  public void ReadTime() {
  
  }

  @Override
  public byte[] GetCurrentChargerTailState() 
  {
    byte [] result;
    try {
    ReadCoilsRequest readCoilsRequest=new ReadCoilsRequest(slaveid, 23, 4);
    ReadCoilsResponse readCoilsResponse;
    readCoilsResponse =(ReadCoilsResponse)master.send(readCoilsRequest);
    result =readCoilsResponse.getData();
    }
    catch (ModbusTransportException ex) {
    Logger.getLogger(ChargeDeviceImp.class.getName()).log(Level.SEVERE, null, ex);
    result=null;
    }
    return result;
  }

  @Override
  public byte[] GetCurrentState() {
   byte [] result;
    try {
      ReadHoldingRegistersRequest readHoldingRegistersRequest=new ReadHoldingRegistersRequest(slaveid, 255, 1);
    ReadHoldingRegistersResponse readHoldingRegistersResponse;
    readHoldingRegistersResponse =(ReadHoldingRegistersResponse)master.send(readHoldingRegistersRequest);
    result =readHoldingRegistersResponse.getData();
    }
    catch (ModbusTransportException ex) {
    Logger.getLogger(ChargeDeviceImp.class.getName()).log(Level.SEVERE, null, ex);
    result=null;
    }
    return result;
  }
  
}

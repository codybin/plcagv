/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.modbus;

import com.serotonin.io.serial.SerialParameters;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.ReadCoilsRequest;
import com.serotonin.modbus4j.msg.ReadCoilsResponse;
import com.serotonin.modbus4j.msg.WriteCoilRequest;

/**
 *
 * @author Lenovo
 */
public class ModbusProcolCharge {
 private  ModbusMaster master;
 private final int slaveid;
public ModbusProcolCharge(int slavid,
  String comport,
  int baudrate,
  int datbit, 
  int parity,
  int stopbit,
 int flowcontrolin)
{
    this.slaveid=slavid;
       serialParameters = new SerialParameters();
        serialParameters.setCommPortId(comport);
        serialParameters.setBaudRate(baudrate);
        serialParameters.setDataBits(datbit);
        serialParameters.setParity(parity);
        serialParameters.setStopBits(stopbit);
        serialParameters.setFlowControlIn(flowcontrolin);

  try {
    init();
  }
  catch (Exception e) 
  {
    System.out.println("com.xintai.modbus.ModbusProcolCharge.<init>()");
  }

}

public void destroy()
{
  if(master!=null)
master.destroy();

}
public byte GetChargingHeadState(int offset,int num) throws ModbusTransportException
{
 ReadCoilsResponse readCoilsResponse=(ReadCoilsResponse) master.send(new ReadCoilsRequest(slaveid, offset, num));
  byte[]  data=  readCoilsResponse.getData();
  return  data[0];
 // System.out.println(new String(data));
  
}
public void StartCharge(int addr,boolean data) throws ModbusTransportException
{
master.send(new WriteCoilRequest(slaveid, addr, data));
}
public  void StopCharge(int addr,boolean data) throws ModbusTransportException
{
master.send(new WriteCoilRequest(slaveid, addr, true));
}
private SerialParameters serialParameters;
  private void init() throws ModbusInitException
  {
        master = new ModbusFactory().createRtuMaster(serialParameters);
        master.setTimeout(200);
        master.setRetries(1);
        master.init();
  }
  
}

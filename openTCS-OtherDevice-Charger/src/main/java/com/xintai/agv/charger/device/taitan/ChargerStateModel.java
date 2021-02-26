/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.agv.charger.device.taitan;

import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.locator.NumericLocator;

/**
 *
 * @author Lenovo
 */
public class ChargerStateModel {
private  float volatage;
private  float current;

  @Override
  public String toString() {
    return "ChargerStateModle{" + "volatage=" + volatage + ", current=" + current + ", chargerState=" + chargerState + ", chargerNumber=" + chargerNumber + '}';
  }

  public float getVolatage() {
    return volatage;
  }

  public float getCurrent() {
    return current;
  }

  public int getChargerState() {
    return chargerState;
  }

  public int getChargerNumber() {
    return chargerNumber;
  }
  private int chargerState;
  private  int chargerNumber;
  private final ChargeStatuesModel chargeStatuesModel;

  public ChargeStatuesModel getChargeStatuesModel() {
    return chargeStatuesModel;
  }
  public ChargerStateModel(byte [] data) {
   decode(data);
   chargeStatuesModel=new ChargeStatuesModel(chargerState);
  }
  private void decode(byte[] data)
  {
   volatage=getdata(ChargerDeviceVar.VoltageAdd, data)*0.1f;
    current=getdata(ChargerDeviceVar.CurrentAdd, data)*0.1f;
    chargerState=getdata(ChargerDeviceVar.StateAdd, data);
    chargerNumber=getdata(ChargerDeviceVar.IdAdd, data);
  }
  
 
  
  private int  getdata(int offset,byte [] data)
  {
  NumericLocator numericLocator=   new NumericLocator(3, RegisterRange.HOLDING_REGISTER,offset,DataType.TWO_BYTE_INT_SIGNED);
  Number number= numericLocator.bytesToValue(data, 0);
 return number.intValue(); 
  }
}

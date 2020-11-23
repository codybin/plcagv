/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.message;


import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.locator.NumericLocator;
import com.xinta.plc.model.VehicleParameterSetWithPLCMode;
import com.xintai.kecong.message.ByteQueue;
import com.xintai.kecong.message.DataConvertUtl;



/**
 *
 * @author Administrator
 */
public class VehicleParameterSetWithPLC {

  /**
   * @return the autorun
   */
  public int getAutorun() {
    return autorun;
  }

  /**
   * @param autorun the autorun to set
   */
  public void setAutorun(int autorun) {
    this.autorun = autorun;
  }

  /**
   * @return the vspeed
   */
  public float getVspeed() {
    return vspeed;
  }

  /**
   * @param vspeed the vspeed to set
   */
  public void setVspeed(float vspeed) {
    this.vspeed = vspeed;
  }

  /**
   * @return the aspeed
   */
  public float getAspeed() {
    return aspeed;
  }

  /**
   * @param aspeed the aspeed to set
   */
  public void setAspeed(float aspeed) {
    this.aspeed = aspeed;
  }
  private int autorun;
  private float vspeed;
   private float aspeed;
     public VehicleParameterSetWithPLC()
     {
     
     
     }
    
  public VehicleParameterSetWithPLC(  int autorun,
   float vspeed,
   float aspeed)
  {
  this.aspeed=aspeed;
  this.autorun=autorun;
  this.vspeed=vspeed;
  }
  public VehicleParameterSetWithPLCMode decode(byte [] data)
  {
NumericLocator num=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,52,DataType.TWO_BYTE_INT_SIGNED);
autorun= num.bytesToValueRealOffset(data,0).intValue();
  NumericLocator num1=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,54,DataType.FOUR_BYTE_FLOAT);
 vspeed=  num1.bytesToValueRealOffset(data,2).floatValue();
   NumericLocator num2=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,58,DataType.FOUR_BYTE_FLOAT);
 aspeed=  num2.bytesToValueRealOffset(data,6).floatValue();
    VehicleParameterSetWithPLCMode vs=new VehicleParameterSetWithPLCMode(autorun,vspeed,aspeed,false);
  return vs;
  }
  public short [] getdata()
  { 
  NumericLocator num=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,52,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers= num.valueToShorts(autorun);
  NumericLocator num1=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,54,DataType.FOUR_BYTE_FLOAT);
 short[]numbers1=  num1.valueToShorts(vspeed);
   NumericLocator num2=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,58,DataType.FOUR_BYTE_FLOAT);
 short[]numbers2=  num2.valueToShorts(aspeed);
 return DataConvertUtl.arrayCopy(numbers,numbers1,numbers2);
  
  }
}

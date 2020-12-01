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
import com.xintai.data.util.DataConvertUtl;




/**
 *
 * @author Administrator
 */
public class VehicleParameterSetWithPLC {

  /**
   * @return the autorun
   */

  private int heartbeatsignal;
  private float agvvspeed;
  private float agvaspeed;
  private int remotestart;
  private int navigationtask;
  private int nextsite;
  private int nexttwosite;
  private int targetsitecardirection;
  private int targetsite;
  private int currentschedulingtask;
  private int materialcode;
  private int chargingpilestate;
     public VehicleParameterSetWithPLC()
     {
     
     
     }

  public VehicleParameterSetWithPLC(int heartbeatsignal, float agvvspeed, float agvaspeed,
                                    int remotestart, int navigationtask, int nextsite,
                                    int nexttwosite, int targetsitecardirection, int targetsite,
                                    int currentschedulingtask, int materialcode,
                                    int chargingpilestate) {
    this.heartbeatsignal = heartbeatsignal;
    this.agvvspeed = agvvspeed;
    this.agvaspeed = agvaspeed;
    this.remotestart = remotestart;
    this.navigationtask = navigationtask;
    this.nextsite = nextsite;
    this.nexttwosite = nexttwosite;
    this.targetsitecardirection = targetsitecardirection;
    this.targetsite = targetsite;
    this.currentschedulingtask = currentschedulingtask;
    this.materialcode = materialcode;
    this.chargingpilestate = chargingpilestate;
  }
    

  /*  public VehicleParameterSetWithPLCMode decode(byte [] data)
  {
  NumericLocator num=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,52,DataType.TWO_BYTE_INT_SIGNED);
  heartbeatsignal= num.bytesToValueRealOffset(data,0).intValue();
  NumericLocator num1=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,54,DataType.FOUR_BYTE_FLOAT);
  agvvspeed=  num1.bytesToValueRealOffset(data,2).floatValue();
  NumericLocator num2=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,58,DataType.FOUR_BYTE_FLOAT);
  agvaspeed=  num2.bytesToValueRealOffset(data,6).floatValue();
  VehicleParameterSetWithPLCMode vs=new VehicleParameterSetWithPLCMode(heartbeatsignal,agvvspeed,agvaspeed,false);
  return vs;
  }*/
  public short [] getdata()
  { 
 NumericLocator num=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,52,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers= num.valueToShorts(heartbeatsignal);
 NumericLocator num1=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,54,DataType.FOUR_BYTE_FLOAT);
 short[]numbers1=  num1.valueToShorts(agvvspeed);
 NumericLocator num2=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,58,DataType.FOUR_BYTE_FLOAT);
 short[]numbers2=  num2.valueToShorts(agvaspeed);
 NumericLocator num3=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,62,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers3= num3.valueToShorts(remotestart);
 NumericLocator num4=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,64,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers4= num4.valueToShorts(navigationtask);
 NumericLocator num5=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,66,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers5= num5.valueToShorts(nextsite);
 NumericLocator num6=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,68,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers6= num6.valueToShorts(nexttwosite);
 NumericLocator num7=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,70,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers7= num7.valueToShorts(targetsitecardirection);
 NumericLocator num8=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,72,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers8= num8.valueToShorts(targetsite);
 NumericLocator num9=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,74,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers9= num9.valueToShorts(currentschedulingtask);
 NumericLocator num10=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,76,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers10= num10.valueToShorts(materialcode);
 NumericLocator num11=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,78,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers11= num11.valueToShorts(chargingpilestate);
 return DataConvertUtl.arrayCopy(numbers,numbers1,numbers2,numbers3,numbers4,numbers5,numbers6,numbers7,numbers8,numbers9,numbers10,numbers11);
  
  }

  /**
   * @return the heartbeatsignal
   */
  public int getHeartbeatsignal() {
    return heartbeatsignal;
  }

  /**
   * @param heartbeatsignal the heartbeatsignal to set
   */
  public void setHeartbeatsignal(int heartbeatsignal) {
    this.heartbeatsignal = heartbeatsignal;
  }

  /**
   * @return the agvvspeed
   */
  public float getAgvvspeed() {
    return agvvspeed;
  }

  /**
   * @param agvvspeed the agvvspeed to set
   */
  public void setAgvvspeed(float agvvspeed) {
    this.agvvspeed = agvvspeed;
  }

  /**
   * @return the agvaspeed
   */
  public float getAgvaspeed() {
    return agvaspeed;
  }

  /**
   * @param agvaspeed the agvaspeed to set
   */
  public void setAgvaspeed(float agvaspeed) {
    this.agvaspeed = agvaspeed;
  }

  /**
   * @return the remotestart
   */
  public int getRemotestart() {
    return remotestart;
  }

  /**
   * @param remotestart the remotestart to set
   */
  public void setRemotestart(int remotestart) {
    this.remotestart = remotestart;
  }

  /**
   * @return the navigationtask
   */
  public int getNavigationtask() {
    return navigationtask;
  }

  /**
   * @param navigationtask the navigationtask to set
   */
  public void setNavigationtask(int navigationtask) {
    this.navigationtask = navigationtask;
  }

  /**
   * @return the nextsite
   */
  public int getNextsite() {
    return nextsite;
  }

  /**
   * @param nextsite the nextsite to set
   */
  public void setNextsite(int nextsite) {
    this.nextsite = nextsite;
  }

  /**
   * @return the nexttwosite
   */
  public int getNexttwosite() {
    return nexttwosite;
  }

  /**
   * @param nexttwosite the nexttwosite to set
   */
  public void setNexttwosite(int nexttwosite) {
    this.nexttwosite = nexttwosite;
  }

  /**
   * @return the targetsitecardirection
   */
  public int getTargetsitecardirection() {
    return targetsitecardirection;
  }

  /**
   * @param targetsitecardirection the targetsitecardirection to set
   */
  public void setTargetsitecardirection(int targetsitecardirection) {
    this.targetsitecardirection = targetsitecardirection;
  }

  /**
   * @return the targetsite
   */
  public int getTargetsite() {
    return targetsite;
  }

  /**
   * @param targetsite the targetsite to set
   */
  public void setTargetsite(int targetsite) {
    this.targetsite = targetsite;
  }

  /**
   * @return the currentschedulingtask
   */
  public int getCurrentschedulingtask() {
    return currentschedulingtask;
  }

  /**
   * @param currentschedulingtask the currentschedulingtask to set
   */
  public void setCurrentschedulingtask(int currentschedulingtask) {
    this.currentschedulingtask = currentschedulingtask;
  }

  /**
   * @return the materialcode
   */
  public int getMaterialcode() {
    return materialcode;
  }

  /**
   * @param materialcode the materialcode to set
   */
  public void setMaterialcode(int materialcode) {
    this.materialcode = materialcode;
  }

  /**
   * @return the chargingpilestate
   */
  public int getChargingpilestate() {
    return chargingpilestate;
  }

  /**
   * @param chargingpilestate the chargingpilestate to set
   */
  public void setChargingpilestate(int chargingpilestate) {
    this.chargingpilestate = chargingpilestate;
  }
}

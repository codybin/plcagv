/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.message;

import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.locator.NumericLocator;
import com.serotonin.util.queue.ByteQueue;
import com.xintai.data.util.DataConvertUtl;

/**
 *
 * @author Lenovo
 */
public class NavigateControl {

  @Override
  public String toString() {
    return "NavigateControl{" + "remotestart=" + remotestart + ", navigationtask=" + navigationtask + ", nextsite=" + nextsite + ", nexttwosite=" + nexttwosite + ", targetsitecardirection=" + targetsitecardirection + ", targetsite=" + targetsite + '}';
  }

 

  public int getRemotestart() {
    return remotestart;
  }

  public NavigateControl setRemotestart(int remotestart) {
    this.remotestart = remotestart;
    return  this;
  }

  public int getNavigationtask() {
    return navigationtask;
  }

  public NavigateControl setNavigationtask(int navigationtask) {
    this.navigationtask = navigationtask;
    return  this;
  }

  public int getNextsite() {
    return nextsite;
  }

  public NavigateControl setNextsite(int nextsite) {
    this.nextsite = nextsite;
    return  this;
  }

  public int getNexttwosite() {
    return nexttwosite;
  }

  public NavigateControl setNexttwosite(int nexttwosite) {
    this.nexttwosite = nexttwosite;
    return  this;
  }

  public int getTargetsitecardirection() {
    return targetsitecardirection;
  }

  public NavigateControl setTargetsitecardirection(int targetsitecardirection) {
    this.targetsitecardirection = targetsitecardirection;
    return  this;
  }

  public int getTargetsite() {
    return targetsite;
  }

  public NavigateControl setTargetsite(int targetsite) {
    this.targetsite = targetsite;
    return  this;
  }
    private int remotestart;
  private int navigationtask;
  private int nextsite;
  private int nexttwosite;
  private int targetsitecardirection;
  private int targetsite;
  public short [] encodedata()
  {
     NumericLocator num=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,62,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers1= num.valueToShorts(remotestart);
 short[]numbers2= num.valueToShorts(navigationtask);
 short[]numbers3= num.valueToShorts(nextsite);
 short[]numbers4= num.valueToShorts(nexttwosite);
 short[]numbers5= num.valueToShorts(targetsitecardirection);
 short[]numbers6= num.valueToShorts(targetsite);
  return DataConvertUtl.arrayCopy(numbers1,numbers2,numbers3,numbers4,numbers5,numbers6);
  }
}

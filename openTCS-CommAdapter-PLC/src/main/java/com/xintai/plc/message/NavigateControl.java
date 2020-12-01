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
   private  int operation;

  @Override
  public String toString() {
    return "NavigateControl{" + "operation=" + operation + ", targetstation=" + targetstation + ", byteQueue=" + byteQueue + ", currenttask=" + currenttask + ", currentstation=" + currentstation + ", nextstation=" + nextstation + '}';
  }

  public int getOperation() {
    return operation;
  }

  public NavigateControl setOperation(int operation) {
    this.operation = operation;
    return  this;
  }

  public int getTargetstation() {
    return targetstation;
  }

  public NavigateControl setTargetstation(int targetstation) {
    this.targetstation = targetstation;
        return  this;
  }


  public int getCurrenttask() {
    return currenttask;
  }

  public NavigateControl setCurrenttask(int currenttask) {
    this.currenttask = currenttask;
        return  this;
  }

  public int getCurrentstation() {
    return currentstation;
  }

  public NavigateControl setCurrentstation(int currentstation) {
    this.currentstation = currentstation;
        return  this;
  }

  public int getNextstation() {
    return nextstation;
  }

  public NavigateControl setNextstation(int nextstation) {
    this.nextstation = nextstation;
        return  this;
  }
   private int targetstation;
   private  ByteQueue byteQueue;
   private int  currenttask;
   private int currentstation;
   private int  nextstation;
  public short [] encodedata()
  {
     NumericLocator num=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,60,DataType.TWO_BYTE_INT_SIGNED);
      short[]operationv= num.valueToShorts(operation);
      short[]currenttaskv=num.valueToShorts(currenttask);
      short[]currentstationv= num.valueToShorts(currentstation);
      short[]nextstationv=num.valueToShorts(nextstation);
      short[]targetstationv= num.valueToShorts(targetstation);
       return DataConvertUtl.arrayCopy(operationv,currenttaskv,currentstationv,nextstationv,targetstationv);
  }
}

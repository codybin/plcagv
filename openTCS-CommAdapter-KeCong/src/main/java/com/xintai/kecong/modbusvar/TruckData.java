/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.modbusvar;

import com.xintai.kecong.message.ByteQueue;
import com.xintai.kecong.message.DataConvertUtl;

/**
 *
 * @author Lenovo
 */
public class TruckData {

  /**
   * @return the enbalepid
   */
  public byte getEnbalepid() {
    return enbalepid;
  }

  /**
   * @param enbalepid the enbalepid to set
   */
  public void setEnbalepid(byte enbalepid) {
    this.enbalepid = enbalepid;
  }

  /**
   * @return the liftsv
   */
  public float getLiftsv() {
    return liftsv;
  }

  /**
   * @param liftsv the liftsv to set
   */
  public void setLiftsv(float liftsv) {
    this.liftsv = liftsv;
  }

  /**
   * @return the finish
   */
  public byte getFinish() {
    return finish;
  }

  /**
   * @param finish the finish to set
   */
  public void setFinish(byte finish) {
    this.finish = finish;
  }

  /**
   * @return the finshtask
   */
  public byte getFinshtask() {
    return finshtask;
  }
//比较两个truckdata是否相等
  
  public boolean  equal(TruckData truckData)
  {
  return  this.enbalepid==truckData.enbalepid
      &&this.finshtask==truckData.finshtask
      &&this.liftsv==truckData.liftsv;
  }
  /**
   * @param finshtask the finshtask to set
   */
  public void setFinshtask(byte finshtask) {
    this.finshtask = finshtask;
  }
  public byte [] getbytes()
  {
  return encodebytes();
  }
  private byte [] encodebytes()
  {
    byteQueue.push(enbalepid);
     byteQueue.push(finshtask);
    byteQueue.push(finish);
    byteQueue.push(0);
    byteQueue.pushFloat(liftsv);
   
  return byteQueue.popAll();
  }
  public  void decodebytes(byte []data)
  {
    byteQueue=new ByteQueue(data);
    enbalepid=byteQueue.pop();
    finshtask=byteQueue.pop();
    finish=byteQueue.pop();
    byteQueue.pop();
    byte[]temp=new byte[4];
    byteQueue.pop(temp, 0, 4);
    liftsv=DataConvertUtl.getFloat(temp);
  }
  private String name;
  private  byte[] dataBytes;
  private  byte enbalepid;
  private  float liftsv;
  private  byte finish;
  private  byte finshtask;
  private ByteQueue byteQueue=new ByteQueue();
  
}

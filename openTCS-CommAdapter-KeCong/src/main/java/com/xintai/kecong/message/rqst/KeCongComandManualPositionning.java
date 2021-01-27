/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.message.rqst;

import com.xintai.kecong.message.ByteQueue;
import com.xintai.kecong.message.KeCongComandCode;
import com.xintai.kecong.message.KeCongRequestMessage;

/**
 *
 * @author Lenovo
 */
public class KeCongComandManualPositionning   extends KeCongRequestMessage{
double x,y,theta;
  public KeCongComandManualPositionning(double x,double y, double theta) {
    super();
    this.x=x;
    this.y=y;
    this.theta=theta;
        //expectedresponse=false;
  }

  @Override
  public void setComandCode() {
   comandCode=KeCongComandCode.comandManulPositionning;
  }

  @Override
  public void setDataLength() {
   dataLength=24;
  }

  @Override
  public void addData() {
 ByteQueue tempque=new ByteQueue();
 tempque.pushDouble(x);
 tempque.pushDouble(y);
 tempque.pushDouble(theta);
 dataValue=tempque.popAll();
  }
  
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.message;


/**
 *
 * @author Lenovo
 */
public class KeCongComandMakeSurePosition  extends KeCongRequestMessage {

  @Override
  public void setComandCode() {
    comandCode=KeCongComandCode.comandInitialPostionMakeSure;   
    //expectedresponse=false;    
  }

  @Override
  public void setDataLength() {
    dataLength=0;
  }

  @Override
  public void addData() {
    dataValue=new byte[0];
  }
  
}

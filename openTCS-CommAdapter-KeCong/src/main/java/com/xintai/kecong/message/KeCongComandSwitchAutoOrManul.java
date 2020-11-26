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
public class KeCongComandSwitchAutoOrManul  extends KeCongRequestMessage {
private  byte value;

  public KeCongComandSwitchAutoOrManul(byte value) {
    this.value=value;
   // expectedresponse=false;
  }
public byte get()
{
return value;
}
  @Override
  public void setComandCode() {
    comandCode=KeCongComandCode.comandCodeSwitchManulOrAuto;
  }

  @Override
  public void setDataLength() {
    dataLength=4;
  }

  @Override
  public void addData() {
   dataValue=new byte[]{value,0,0,0};
  }
  
}

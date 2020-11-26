/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.message;


import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Lenovo
 */
public class KeCongComandRead  extends KeCongRequestMessage{
   private String name;
  public KeCongComandRead(String name) {
    this.name=name;
  }

 
  @Override
  public void setComandCode() {
   comandCode=KeCongComandCode.comandCodereadVar;
  }


  @Override
  public void setDataLength() {
 dataLength=16;  
  }

  @Override
  public void addData() {
    byte []temp1=new byte[16];
     byte []temp= name.getBytes(StandardCharsets.US_ASCII);
     System.arraycopy(temp,0, temp1,0, temp.length>16?16:temp.length);
     dataValue=temp1;
  }

  
}

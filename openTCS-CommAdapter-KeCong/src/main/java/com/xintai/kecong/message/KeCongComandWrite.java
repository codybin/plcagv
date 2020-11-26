/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.message;;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author Lenovo
 */
public class KeCongComandWrite  extends KeCongRequestMessage {
byte[] varvalue;
String name;
  public KeCongComandWrite(String name,byte[] varvalue) {
 this.varvalue=varvalue;
 this.name=name;
  }

  @Override
  public void setComandCode() {
   comandCode=KeCongComandCode.comandCodewirteVar;
  }

  @Override
  public void setDataLength() {
  dataLength=(short)(16+varvalue.length);
  }

  @Override
  public void addData() {
   byte []temp1=new byte[16];
     byte []temp= name.getBytes(StandardCharsets.US_ASCII);
     System.arraycopy(temp,0, temp1,0, temp.length>16?15:temp.length);
     byte[] temp2=new byte[16+varvalue.length];
     System.arraycopy(temp1,0, temp2,0,16);
     System.arraycopy(varvalue,0, temp2,16,varvalue.length);
     dataValue=temp2;
  }
  
}

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
public class KeCongComandReadResponse  extends KeCongCommandResponse{
private String name;

  public String getName() {
    return name;
  }

  public byte[] getValue() {
    return value1;
  }
private byte value1[];
  public KeCongComandReadResponse(ByteQueue bytequeque) {
    super(bytequeque);
    byte temp[]=new byte[16];
    if(value.length>16)
    { System.arraycopy(value, 0,temp, 0, 16);
    name=new String(temp) ;
    value1=new byte[length-16];
    System.arraycopy(value, 16,value1, 0, value1.length);
    }
  }
  
}

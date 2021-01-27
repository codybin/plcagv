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
public  abstract class KeCongCommandResponse extends KeCongMessage {
protected ByteQueue bytequeque;
private byte[] buffer=new byte[28];
protected int length;
 protected byte[] value;
 protected int cqs=0;
  public KeCongCommandResponse(ByteQueue bytequeque) {  
    this.bytequeque=bytequeque;
    if(bytequeque.peekAll().length>=28){
    this.bytequeque.pop(buffer,0,28);
     parse();
    }
  }
 public boolean isResponseTo(KeCongRequestMessage keCongRequestMessage)
 {
 return cqs==keCongRequestMessage.getcqs();
 
 }
 public int getcqs()
 {
 return  cqs; 
 }
 private void parse()
 {cqs=buffer[18]&0xff|(buffer[19]&0xff)<<8;
 //System.out.println(String.valueOf(cqs));
 comandCode=buffer[21];
 returnCode=buffer[22];
length=buffer[24]&0xff|(buffer[25]&0xff)<<8;
if(length!=0)
{
  value=new byte[length];
  bytequeque.pop(value,0,length);
}
 }
  
  
  
  
}

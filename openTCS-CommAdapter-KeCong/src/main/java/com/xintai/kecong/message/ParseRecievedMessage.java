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
public class ParseRecievedMessage    {
private final ByteQueue bytequeque;
private int consquence=0;


  public int getConsquence() {
    return consquence;
  }
  public ParseRecievedMessage(ByteQueue bytequeque) {
    this.bytequeque=bytequeque;
     bytequeque.peek(buffer,0,28);
     consquence=buffer[18]&0xff|(buffer[19]&0xff)<<8;
  }
  byte [] buffer=new byte[28];
  public KeCongCommandResponse ParseResponseMessage()
  {
    KeCongCommandResponse response=null;
  int [] bufferint=new int[28];
  for(int i=0;i<28;i++)
    bufferint[i]=buffer[i]&0Xff;
  if(buffer[22]!=KeCongReturnCode.Sucees)
    return  new ErroRespone(bytequeque);
  switch(bufferint[21])
  {case KeCongComandCode.comandCodereadVar:
   response=new KeCongComandReadResponse(bytequeque);
    break;
  case KeCongComandCode.comandRobotNavigationSearch: 
    response=new KeCongComandNavigateSearchResponse(bytequeque);
    break;
  case KeCongComandCode.comandRobotStatuSearch:
    response=new KeCongRobotStatuSearchResponse(bytequeque);
    break; 
  case KeCongComandCode.comandNavigateControl:
    response=new KeCongComandNavigationResponse(bytequeque);
   break;
    default:
      response=new NullComand(bytequeque);
      break;
  
  } 
  return response;
  }
}

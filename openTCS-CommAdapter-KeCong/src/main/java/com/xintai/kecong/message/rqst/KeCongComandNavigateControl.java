/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.message.rqst;

import com.xintai.kecong.message.ByteQueue;
import com.xintai.kecong.message.DataConvertUtl;
import com.xintai.kecong.message.KeCongComandCode;
import com.xintai.kecong.message.KeCongRequestMessage;

/**
 *
 * @author Lenovo
 */
public class KeCongComandNavigateControl extends KeCongRequestMessage {

  public KeCongComandNavigateControl() {
    super();
  }


  public byte getOperation() {
    return operation;
  }

  public void setOperation(byte operation) {
    this.operation = operation;
  }

  public byte getNavigateway() {
    return navigateway;
  }

  public void setNavigateway(byte navigateway) {
    this.navigateway = navigateway;
  }

  public byte getIsgivepth() {
    return isgivepth;
  }

  public void setIsgivepth(byte isgivepth) {
    this.isgivepth = isgivepth;
  }

  public byte getIsusetrafficmanage() {
    return isusetrafficmanage;
  }

  public void setIsusetrafficmanage(byte isusetrafficmanage) {
    this.isusetrafficmanage = isusetrafficmanage;
  }

  public byte[] getPathid() {
    return pathid;
  }

  public void setPathid(byte[] pathid) {
    this.pathid = pathid;
  }

  public int getTargetstartpoint() {
    return targetstartpoint;
  }

  public void setTargetstartpoint(int targetstartpoint) {
    this.targetstartpoint = targetstartpoint;
  }

  public int getTargetendpoint() {
    return targetendpoint;
  }

  public void setTargetendpoint(int targetendpoint) {
    this.targetendpoint = targetendpoint;
  }

  public float getTargetx() {
    return targetx;
  }

  public void setTargetx(float targetx) {
    this.targetx = targetx;
  }

  public float getTargety() {
    return targety;
  }

  public void setTargety(float targety) {
    this.targety = targety;
  }

  public float getTargettheta() {
    return targettheta;
  }

  public void setTargettheta(float targettheta) {
    this.targettheta = targettheta;
  }

  public byte[] getReserved() {
    return reserved;
  }

  public void setReserved(byte[] reserved) {
    this.reserved = reserved;
  }

  public int getPathcount() {
    return pathcount;
  }

  public void setPathcount(int pathcount) {
    this.pathcount = pathcount;
  }

  public int[] getPathpointsid() {
    return pathpointsid;
  }

  public void setPathpointsid(int[] pathpointsid) {
    this.pathpointsid = pathpointsid;
  }
  private byte operation;
private byte navigateway=0;
private  byte isgivepth=0;
private byte isusetrafficmanage=0;
private byte [] pathid=new byte[8];
private int targetstartpoint=0;
private int targetendpoint=0;
private float targetx=0;
private float targety=0;
private float targettheta=0;
private byte [] reserved=new byte[]{0,0};
private int pathcount;
private int [] pathpointsid=new int[200];


  public KeCongComandNavigateControl(String pathid,byte operation,byte usetraficornot) {
    this.pathid=DataConvertUtl.stringtobyte(8, pathid);
    this.operation=operation;
    this.isusetrafficmanage=usetraficornot;
  }

  @Override
  public void setComandCode() {
   comandCode=KeCongComandCode.comandNavigateControl;
       
  }

  @Override
  public void setDataLength() {
dataLength=432;
  }

  @Override
  public void addData() {
   ByteQueue bytequeque=new ByteQueue();
   bytequeque.push(operation);
   bytequeque.push(navigateway);
   bytequeque.push(isgivepth);
   bytequeque.push(isusetrafficmanage);
   bytequeque.push(pathid);
   bytequeque.pushmU2B(targetstartpoint);
   bytequeque.pushmU2B(targetendpoint);
   bytequeque.pushFloat(targetx);
   bytequeque.pushFloat(targety);
   bytequeque.pushFloat(targettheta);
   bytequeque.push(reserved);
   bytequeque.pushmU2B(pathcount);
   for(int i=0;i<pathpointsid.length;i++)
   {
     bytequeque.pushU2B(pathpointsid[i]);}
dataValue= bytequeque.popAll();
   
   
  }
  
}

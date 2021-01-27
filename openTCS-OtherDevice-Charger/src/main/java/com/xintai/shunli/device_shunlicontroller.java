/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.shunli;

import com.google.inject.Inject;

/**
 *
 * @author Lenovo
 */
public class device_shunlicontroller {
  @Inject
  public device_shunlicontroller(deviceservice_shunliimp deviceservice_shunliimp){
  deviceservice=deviceservice_shunliimp;
  }
 public DB readdata(int offset, int DBnum)
 {   
  DB  db= deviceservice.readdata(DB.class, DBnum, offset,3);
  return  db;
 }
 public void writedata(DB db,int offset, int DBnum)
 {
     deviceservice.writedata(db, DBnum, offset,3);
 }
 public void close()
 {
    deviceservice.close();
 }
private final  deviceservice_shunliimp deviceservice;
}

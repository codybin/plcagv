/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.shunli;

import com.github.s7connector.api.S7Connector;
import com.github.s7connector.api.S7Serializer;
import com.github.s7connector.api.factory.S7ConnectorFactory;
import com.github.s7connector.api.factory.S7SerializerFactory;
import com.github.s7connector.exception.S7Exception;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lenovo
 */

public class deviceservice_shunliimp implements  device_shunli{
  @Override
  public String toString() {
    return "deviceservice_shunliimp{" + "port=" + port + ", host=" + host + '}';
  }
  private final int port;
  /*  @Inject
  public deviceservice_shunliimp(OtherDeviceConfiguration otherDeviceConfiguration) {
  this.host = otherDeviceConfiguration.host();
  this.port=otherDeviceConfiguration.port();
  init();
  }*/
  
  public deviceservice_shunliimp() {
  this.host = "192.168.99.88";
  this.port=520;
  init();
  }
 private S7Connector connector;
 private final String host;
  private  S7Serializer serializer;
  private boolean  isinitialed=false;
  @Override
  public  void init() {
    if(isinitialed)
      return;
   if(connector==null)
     try { 
     connector= S7ConnectorFactory.buildTCPConnector().withRack(0).withSlot(2).withPort(102).withHost(host).build();
       System.out.println("com.xintai.shunli.deviceservice_shunliimp.init() success");
     }
   catch (Exception e) {
     isinitialed=false;
     System.out.println("com.xintai.shunli.deviceservice_shunliimp.init()"+e.getMessage().toString());
     connector=null;
   }
   if(connector!=null)
   {
   serializer = S7SerializerFactory.buildSerializer(connector);
   isinitialed=true;
   }
  }

  @Override
  public  void  writedata(Object bean, int dbNum, int byteOffset,int tries) {
    
    try {
      init();
      if(serializer!=null)
       serializer.store(bean, dbNum, byteOffset);
    }
    catch (S7Exception e) {
      tries=tries-1;
      if(tries<0)
        throw  e;
       isinitialed=false;
       this.writedata(bean,  dbNum, byteOffset, tries);
    }
    
  }
  @Override
  public  <T>  T  readdata(Class<T> beanClass, int dbNum, int byteOffset,int tries) {
    try {
      init();
   if(serializer!=null)
    return serializer.dispense(beanClass, dbNum, byteOffset);
    else return null;
    }
    catch (S7Exception e) {
      isinitialed=false;
      tries=tries-1;
       if(tries<0)
        throw e;
      this.readdata(beanClass, dbNum, byteOffset, tries);
      return null;
    } 
  }
   @Override
  public void close()
  {
    try {
      connector.close();
      isinitialed=false;
      connector=null;
    }
    catch (IOException ex) {
      Logger.getLogger(deviceservice_shunliimp.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
}

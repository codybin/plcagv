/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.device;

/**
 *
 * @author Lenovo
 */
public class jingBianLocation {
  private int locationid;
  private int locationindex;
  private int iscapture;
/***
 * 
 * @param locationid
 * @param locationindex
 * @param iscapture
 * @param deviceid 
 */
  public jingBianLocation(int locationid, int locationindex, int iscapture, int deviceid) {
    this.locationid = locationid;
    this.locationindex = locationindex;
    this.iscapture = iscapture;
    this.deviceid = deviceid;
  }

  @Override
  public String toString() {
    return "jingBianLocation{" + "locationid=" + locationid + ", locationindex=" + locationindex + ", iscapture=" + iscapture + ", deviceid=" + deviceid + '}';
  }

  public int getIscapture() {
    return iscapture;
  }

  public void setIscapture(int iscapture) {
    this.iscapture = iscapture;
  }
  private int deviceid;


  
  
  public int getLocationid() {
    return locationid;
  }

  public void setLocationid(int locationid) {
    this.locationid = locationid;
  }

  public int getLocationindex() {
    return locationindex;
  }

  public void setLocationindex(int locationindex) {
    this.locationindex = locationindex;
  }


  public int getDeviceid() {
    return deviceid;
  }

  public void setDeviceid(int deviceid) {
    this.deviceid = deviceid;
  }
 
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.device;

import com.xintai.device.jingBianLocation;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class jingBian_Device {
  private int deviceid;

  @Override
  public String toString() {
    return "jingBian_Device{" + "deviceid=" + deviceid + ", jianbBianLocations=" + jianbBianLocations + '}';
  }

 

  public int getDeviceid() {
    return deviceid;
  }

  public void setDeviceid(int deviceid) {
    this.deviceid = deviceid;
  }

  public List<jingBianLocation> getJianbBianLocations() {
    return jianbBianLocations;
  }

  public void setJianbBianLocations(List<jingBianLocation> jianbBianLocations) {
    this.jianbBianLocations = jianbBianLocations;
  }
 private List<jingBianLocation> jianbBianLocations;
     
}

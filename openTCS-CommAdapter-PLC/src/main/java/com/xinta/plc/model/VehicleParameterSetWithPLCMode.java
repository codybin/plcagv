/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xinta.plc.model;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class VehicleParameterSetWithPLCMode implements Serializable{

  /**
   * @return the iswrite
   */
  public boolean isIswrite() {
    return iswrite;
  }

  /**
   * @param iswrite the iswrite to set
   */
  public void setIswrite(boolean iswrite) {
    this.iswrite = iswrite;
  }

  @Override
  public String toString() {
    return "VehicleParameterSetWithPLCMode{" + "autorun=" + autorun + ", vspeed=" + vspeed + ", aspeed=" + aspeed + '}';
  }

  /**
   * @return the autorun
   */
  public int getAutorun() {
    return autorun;
  }

  /**
   * @param autorun the autorun to set
   */
  public void setAutorun(int autorun) {
    this.autorun = autorun;
  }

  /**
   * @return the vspeed
   */
  public float getVspeed() {
    return vspeed;
  }

  /**
   * @param vspeed the vspeed to set
   */
  public void setVspeed(float vspeed) {
    this.vspeed = vspeed;
  }

  /**
   * @return the aspeed
   */
  public float getAspeed() {
    return aspeed;
  }

  /**
   * @param aspeed the aspeed to set
   */
  public void setAspeed(float aspeed) {
    this.aspeed = aspeed;
  }
    private int autorun;
  private float vspeed;
   private float aspeed;
  private  boolean iswrite;
  public VehicleParameterSetWithPLCMode(  int autorun,
   float vspeed,
   float aspeed,boolean iswrite)
  {
    this.iswrite=iswrite;
  this.aspeed=aspeed;
  this.autorun=autorun;
  this.vspeed=vspeed;
  }
}

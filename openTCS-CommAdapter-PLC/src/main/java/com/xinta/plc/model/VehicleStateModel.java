/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xinta.plc.model;

import java.io.Serializable;

/**
 *
 * @author Lenovo
 */
public class VehicleStateModel implements  Serializable {

  /**
   * @return the postionx
   */
  public float getPostionx() {
    return postionx;
  }

  /**
   * @param postionx the postionx to set
   */
  public void setPostionx(float postionx) {
    this.postionx = postionx;
  }

  /**
   * @return the positiony
   */
  public float getPositiony() {
    return positiony;
  }

  /**
   * @param positiony the positiony to set
   */
  public void setPositiony(float positiony) {
    this.positiony = positiony;
  }

  /**
   * @return the positionangle
   */
  public float getPositionangle() {
    return positionangle;
  }

  /**
   * @param positionangle the positionangle to set
   */
  public void setPositionangle(float positionangle) {
    this.positionangle = positionangle;
  }

  /**
   * @return the currentnavigationstation
   */
  public int getCurrentnavigationstation() {
    return currentnavigationstation;
  }

  /**
   * @param currentnavigationstation the currentnavigationstation to set
   */
  public void setCurrentnavigationstation(int currentnavigationstation) {
    this.currentnavigationstation = currentnavigationstation;
  }

  /**
   * @return the postionstate
   */
  public int getPostionstate() {
    return postionstate;
  }

  /**
   * @param postionstate the postionstate to set
   */
  public void setPostionstate(int postionstate) {
    this.postionstate = postionstate;
  }

  /**
   * @return the navigatestate
   */
  public int getNavigatestate() {
    return navigatestate;
  }

  /**
   * @param navigatestate the navigatestate to set
   */
  public void setNavigatestate(int navigatestate) {
    this.navigatestate = navigatestate;
  }

  /**
   * @return the navigatetype
   */
  public int getNavigatetype() {
    return navigatetype;
  }

  /**
   * @param navigatetype the navigatetype to set
   */
  public void setNavigatetype(int navigatetype) {
    this.navigatetype = navigatetype;
  }

  /**
   * @return the postionlevel
   */
  public float getPostionlevel() {
    return postionlevel;
  }

  /**
   * @param postionlevel the postionlevel to set
   */
  public void setPostionlevel(float postionlevel) {
    this.postionlevel = postionlevel;
  }

  /**
   * @return the batterypower
   */
  public int getBatterypower() {
    return batterypower;
  }

  /**
   * @param batterypower the batterypower to set
   */
  public void setBatterypower(int batterypower) {
    this.batterypower = batterypower;
  }

  /**
   * @return the batterytemprature
   */
  public float getBatterytemprature() {
    return batterytemprature;
  }

  /**
   * @param batterytemprature the batterytemprature to set
   */
  public void setBatterytemprature(float batterytemprature) {
    this.batterytemprature = batterytemprature;
  }

  /**
   * @return the batterycurrent
   */
  public float getBatterycurrent() {
    return batterycurrent;
  }

  /**
   * @param batterycurrent the batterycurrent to set
   */
  public void setBatterycurrent(float batterycurrent) {
    this.batterycurrent = batterycurrent;
  }

  /**
   * @return the batteryvoltage
   */
  public float getBatteryvoltage() {
    return batteryvoltage;
  }

  /**
   * @param batteryvoltage the batteryvoltage to set
   */
  public void setBatteryvoltage(float batteryvoltage) {
    this.batteryvoltage = batteryvoltage;
  }

  /**
   * @return the kilometerintotal
   */
  public float getKilometerintotal() {
    return kilometerintotal;
  }

  /**
   * @param kilometerintotal the kilometerintotal to set
   */
  public void setKilometerintotal(float kilometerintotal) {
    this.kilometerintotal = kilometerintotal;
  }

  /**
   * @return the timeintotal
   */
  public float getTimeintotal() {
    return timeintotal;
  }

  /**
   * @param timeintotal the timeintotal to set
   */
  public void setTimeintotal(float timeintotal) {
    this.timeintotal = timeintotal;
  }

  /**
   * @return the currentposition
   */
  public int getCurrentposition() {
    return currentposition;
  }

  /**
   * @param currentposition the currentposition to set
   */
  public void setCurrentposition(int currentposition) {
    this.currentposition = currentposition;
  }

  /**
   * @return the mapname
   */
  public int getMapname() {
    return mapname;
  }

  /**
   * @param mapname the mapname to set
   */
  public void setMapname(int mapname) {
    this.mapname = mapname;
  }

  /**
   * @return the dispaterstate
   */
  public int getDispaterstate() {
    return dispaterstate;
  }

  /**
   * @param dispaterstate the dispaterstate to set
   */
  public void setDispaterstate(int dispaterstate) {
    this.dispaterstate = dispaterstate;
  }

  /**
   * @return the kilometertoday
   */
  public int getKilometertoday() {
    return kilometertoday;
  }

  /**
   * @param kilometertoday the kilometertoday to set
   */
  public void setKilometertoday(int kilometertoday) {
    this.kilometertoday = kilometertoday;
  }

  /**
   * @return the loadstate
   */
  public int getLoadstate() {
    return loadstate;
  }

  /**
   * @param loadstate the loadstate to set
   */
  public void setLoadstate(int loadstate) {
    this.loadstate = loadstate;
  }
  
     private float postionx;
   private float positiony;
   private float positionangle;
   private int currentnavigationstation;
   private int postionstate;
   private int navigatestate;
   private int navigatetype;
   private float postionlevel;
   private int batterypower;
   private float batterytemprature;
   private float batterycurrent;
   private float batteryvoltage;
   private float kilometerintotal;
   private float timeintotal;
   private int currentposition;
   private int mapname;
   private int dispaterstate;
   private int kilometertoday;
   private int loadstate;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.model;

import java.io.Serializable;

/**
 *
 * @author Lenovo
 */
public class RobotStatuResponseModel implements  Serializable{


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final RobotStatuResponseModel other = (RobotStatuResponseModel) obj;
    if (Double.doubleToLongBits(this.positionx) != Double.doubleToLongBits(other.positionx)) {
      return false;
    }
    if (Double.doubleToLongBits(this.postiony) != Double.doubleToLongBits(other.postiony)) {
      return false;
    }
    if (Double.doubleToLongBits(this.postiontheta) != Double.doubleToLongBits(other.postiontheta)) {
      return false;
    }
    if (Double.doubleToLongBits(this.batterypower) != Double.doubleToLongBits(other.batterypower)) {
      return false;
    }
    if (this.runmode != other.runmode) {
      return false;
    }
     if (this.currenttaskstatue != other.currenttaskstatue) {
      return false;
    }
    if (this.currenttargetid != other.currenttargetid) {
      return false;
    }
    if (Double.doubleToLongBits(this.batteryvoltage) != Double.doubleToLongBits(other.batteryvoltage)) {
      return false;
    }
    if (Double.doubleToLongBits(this.current) != Double.doubleToLongBits(other.current)) {
      return false;
    }
    return true;
  }

  /**
   * @param bodytemprature the bodytemprature to set
   */
  public void setBodytemprature(double bodytemprature) {
    this.bodytemprature = bodytemprature;
  }

  /**
   * @param positionx the positionx to set
   */
  public void setPositionx(double positionx) {
    this.positionx = positionx;
  }

  /**
   * @param postiony the postiony to set
   */
  public void setPostiony(double postiony) {
    this.postiony = postiony;
  }

  /**
   * @param postiontheta the postiontheta to set
   */
  public void setPostiontheta(double postiontheta) {
    this.postiontheta = postiontheta;
  }

  /**
   * @param batterypower the batterypower to set
   */
  public void setBatterypower(double batterypower) {
    this.batterypower = batterypower;
  }

  /**
   * @param isblokingornot the isblokingornot to set
   */
  public void setIsblokingornot(byte isblokingornot) {
    this.isblokingornot = isblokingornot;
  }

  /**
   * @param ischargingornot the ischargingornot to set
   */
  public void setIschargingornot(byte ischargingornot) {
    this.ischargingornot = ischargingornot;
  }

  /**
   * @param runmode the runmode to set
   */
  public void setRunmode(byte runmode) {
    this.runmode = runmode;
  }

  /**
   * @param maploadstate the maploadstate to set
   */
  public void setMaploadstate(byte maploadstate) {
    this.maploadstate = maploadstate;
  }

  /**
   * @param currenttargetid the currenttargetid to set
   */
  public void setCurrenttargetid(int currenttargetid) {
    this.currenttargetid = currenttargetid;
  }

  /**
   * @param forwordspeed the forwordspeed to set
   */
  public void setForwordspeed(double forwordspeed) {
    this.forwordspeed = forwordspeed;
  }

  /**
   * @param curvespeed the curvespeed to set
   */
  public void setCurvespeed(double curvespeed) {
    this.curvespeed = curvespeed;
  }

  /**
   * @param batteryvoltage the batteryvoltage to set
   */
  public void setBatteryvoltage(double batteryvoltage) {
    this.batteryvoltage = batteryvoltage;
  }

  /**
   * @param current the current to set
   */
  public void setCurrent(double current) {
    this.current = current;
  }

  /**
   * @param currenttaskstatue the currenttaskstatue to set
   */
  public void setCurrenttaskstatue(byte currenttaskstatue) {
    this.currenttaskstatue = currenttaskstatue;
  }

  /**
   * @param resrved the resrved to set
   */
  public void setResrved(byte resrved) {
    this.resrved = resrved;
  }

  /**
   * @param mapversion the mapversion to set
   */
  public void setMapversion(int mapversion) {
    this.mapversion = mapversion;
  }

  /**
   * @param reserved the reserved to set
   */
  public void setReserved(byte[] reserved) {
    this.reserved = reserved;
  }

  /**
   * @param totalkilometer the totalkilometer to set
   */
  public void setTotalkilometer(double totalkilometer) {
    this.totalkilometer = totalkilometer;
  }

  /**
   * @param currentrunningtime the currentrunningtime to set
   */
  public void setCurrentrunningtime(double currentrunningtime) {
    this.currentrunningtime = currentrunningtime;
  }

  /**
   * @param totalrunningtime the totalrunningtime to set
   */
  public void setTotalrunningtime(double totalrunningtime) {
    this.totalrunningtime = totalrunningtime;
  }

  /**
   * @param robotpositionstatue the robotpositionstatue to set
   */
  public void setRobotpositionstatue(byte robotpositionstatue) {
    this.robotpositionstatue = robotpositionstatue;
  }

  /**
   * @param reserved1 the reserved1 to set
   */
  public void setReserved1(byte[] reserved1) {
    this.reserved1 = reserved1;
  }

  /**
   * @param mapnumber the mapnumber to set
   */
  public void setMapnumber(int mapnumber) {
    this.mapnumber = mapnumber;
  }

  /**
   * @param currentmapname the currentmapname to set
   */
  public void setCurrentmapname(byte[] currentmapname) {
    this.currentmapname = currentmapname;
  }

  /**
   * @param confidenceinterval the confidenceinterval to set
   */
  public void setConfidenceinterval(float confidenceinterval) {
    this.confidenceinterval = confidenceinterval;
  }

  /**
   * @param reserved2 the reserved2 to set
   */
  public void setReserved2(byte[] reserved2) {
    this.reserved2 = reserved2;
  }
    private double bodytemprature=0;

  public double getBodytemprature() {
    return bodytemprature;
  }

  public double getPositionx() {
    return positionx;
  }

  public double getPostiony() {
    return postiony;
  }

  public double getPostiontheta() {
    return postiontheta;
  }

  public double getBatterypower() {
    return batterypower;
  }

  public byte getIsblokingornot() {
    return isblokingornot;
  }

  public byte getIschargingornot() {
    return ischargingornot;
  }

  public byte getRunmode() {
    return runmode;
  }

  public byte getMaploadstate() {
    return maploadstate;
  }

  public int getCurrenttargetid() {
    return currenttargetid;
  }

  public double getForwordspeed() {
    return forwordspeed;
  }

  public double getCurvespeed() {
    return curvespeed;
  }

  public double getBatteryvoltage() {
    return batteryvoltage;
  }

  public double getCurrent() {
    return current;
  }

  public byte getCurrenttaskstatue() {
    return currenttaskstatue;
  }

  public byte getResrved() {
    return resrved;
  }

  public int getMapversion() {
    return mapversion;
  }

  public byte[] getReserved() {
    return reserved;
  }

  public double getTotalkilometer() {
    return totalkilometer;
  }

  public double getCurrentrunningtime() {
    return currentrunningtime;
  }

  public double getTotalrunningtime() {
    return totalrunningtime;
  }

  public byte getRobotpositionstatue() {
    return robotpositionstatue;
  }

  public byte[] getReserved1() {
    return reserved1;
  }

  public int getMapnumber() {
    return mapnumber;
  }

  public byte[] getCurrentmapname() {
    return currentmapname;
  }

  public float getConfidenceinterval() {
    return confidenceinterval;
  }

  public byte[] getReserved2() {
    return reserved2;
  }
  private double positionx;
  private double postiony;
  private double postiontheta;
  private double batterypower;
  private byte isblokingornot;
  private byte ischargingornot;
  private byte runmode;
  private byte maploadstate;
  private int currenttargetid;
  private double forwordspeed;
  private double curvespeed;
  private double batteryvoltage;
  private double current;
  private byte currenttaskstatue;
  private byte resrved=0;
  private int mapversion;
  private byte reserved[]={0,0,0,0};
  private double totalkilometer;
  private double currentrunningtime;
  private double totalrunningtime;
  private byte robotpositionstatue;
  private byte reserved1[]={0,0,0};
  private int mapnumber;
  private byte currentmapname[]=new byte[64];
  private float confidenceinterval;
  private byte reserved2[]={0,0,0,0};

  
}

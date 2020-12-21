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

  @Override
  public String toString() {
    return "VehicleParameterSetWithPLCMode{" + "heartbeatsignal=" + heartbeatsignal + ", agvvspeed=" + agvvspeed + ", agvaspeed=" + agvaspeed + ", standby1=" + getStandby1() + ", standby2=" + getStandby2() + ", standby3=" + getStandby3() + ", remotestart=" + remotestart + ", navigationtask=" + navigationtask + ", nextsite=" + nextsite + ", nexttwosite=" + nexttwosite + ", targetsitecardirection=" + targetsitecardirection + ", targetsite=" + targetsite + ", currentschedulingtask=" + currentschedulingtask + ", materialcode=" + materialcode + ", chargingpilestate=" + chargingpilestate + ", iswrite=" + iswrite + '}';
  }



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

 

  /**
   * @return the autorun
   */
  
  private int heartbeatsignal;
  private float agvvspeed;
  private float agvaspeed;
  private float standby1;
  private float standby2;
  private float standby3;
  private int remotestart;
  private int navigationtask;
  private int nextsite;
  private int nexttwosite;
  private int targetsitecardirection;
  private int targetsite;
  private int currentschedulingtask;
  private int materialcode;
  private int chargingpilestate;
  private  boolean iswrite;

  public VehicleParameterSetWithPLCMode(int heartbeatsignal, float agvvspeed, float agvaspeed,
                                        float standby1, float standby2, float standby3,
                                        int remotestart, int navigationtask, int nextsite,
                                        int nexttwosite, int targetsitecardirection, int targetsite,
                                        int currentschedulingtask, int materialcode,
                                        int chargingpilestate, boolean iswrite) {
    this.heartbeatsignal = heartbeatsignal;
    this.agvvspeed = agvvspeed;
    this.agvaspeed = agvaspeed;
    this.standby1 = standby1;
    this.standby2 = standby2;
    this.standby3 = standby3;
    this.remotestart = remotestart;
    this.navigationtask = navigationtask;
    this.nextsite = nextsite;
    this.nexttwosite = nexttwosite;
    this.targetsitecardirection = targetsitecardirection;
    this.targetsite = targetsite;
    this.currentschedulingtask = currentschedulingtask;
    this.materialcode = materialcode;
    this.chargingpilestate = chargingpilestate;
    this.iswrite = iswrite;
  }

 
  
 

  /**
   * @return the heartbeatsignal
   */
  public int getHeartbeatsignal() {
    return heartbeatsignal;
  }

  /**
   * @param heartbeatsignal the heartbeatsignal to set
   */
  public void setHeartbeatsignal(int heartbeatsignal) {
    this.heartbeatsignal = heartbeatsignal;
  }

  /**
   * @return the agvvspeed
   */
  public float getAgvvspeed() {
    return agvvspeed;
  }

  /**
   * @param agvvspeed the agvvspeed to set
   */
  public void setAgvvspeed(float agvvspeed) {
    this.agvvspeed = agvvspeed;
  }

  /**
   * @return the agvaspeed
   */
  public float getAgvaspeed() {
    return agvaspeed;
  }

  /**
   * @param agvaspeed the agvaspeed to set
   */
  public void setAgvaspeed(float agvaspeed) {
    this.agvaspeed = agvaspeed;
  }

  /**
   * @return the remotestart
   */
  public int getRemotestart() {
    return remotestart;
  }

  /**
   * @param remotestart the remotestart to set
   */
  public void setRemotestart(int remotestart) {
    this.remotestart = remotestart;
  }

  /**
   * @return the navigationtask
   */
  public int getNavigationtask() {
    return navigationtask;
  }

  /**
   * @param navigationtask the navigationtask to set
   */
  public void setNavigationtask(int navigationtask) {
    this.navigationtask = navigationtask;
  }

  /**
   * @return the nextsite
   */
  public int getNextsite() {
    return nextsite;
  }

  /**
   * @param nextsite the nextsite to set
   */
  public void setNextsite(int nextsite) {
    this.nextsite = nextsite;
  }

  /**
   * @return the nexttwosite
   */
  public int getNexttwosite() {
    return nexttwosite;
  }

  /**
   * @param nexttwosite the nexttwosite to set
   */
  public void setNexttwosite(int nexttwosite) {
    this.nexttwosite = nexttwosite;
  }

  /**
   * @return the targetsitecardirection
   */
  public int getTargetsitecardirection() {
    return targetsitecardirection;
  }

  /**
   * @param targetsitecardirection the targetsitecardirection to set
   */
  public void setTargetsitecardirection(int targetsitecardirection) {
    this.targetsitecardirection = targetsitecardirection;
  }

  /**
   * @return the targetsite
   */
  public int getTargetsite() {
    return targetsite;
  }

  /**
   * @param targetsite the targetsite to set
   */
  public void setTargetsite(int targetsite) {
    this.targetsite = targetsite;
  }

  /**
   * @return the currentschedulingtask
   */
  public int getCurrentschedulingtask() {
    return currentschedulingtask;
  }

  /**
   * @param currentschedulingtask the currentschedulingtask to set
   */
  public void setCurrentschedulingtask(int currentschedulingtask) {
    this.currentschedulingtask = currentschedulingtask;
  }

  /**
   * @return the materialcode
   */
  public int getMaterialcode() {
    return materialcode;
  }

  /**
   * @param materialcode the materialcode to set
   */
  public void setMaterialcode(int materialcode) {
    this.materialcode = materialcode;
  }

  /**
   * @return the chargingpilestate
   */
  public int getChargingpilestate() {
    return chargingpilestate;
  }

  /**
   * @param chargingpilestate the chargingpilestate to set
   */
  public void setChargingpilestate(int chargingpilestate) {
    this.chargingpilestate = chargingpilestate;
  }

  /**
   * @return the standby1
   */
  public float getStandby1() {
    return standby1;
  }

  /**
   * @param standby1 the standby1 to set
   */
  public void setStandby1(float standby1) {
    this.standby1 = standby1;
  }

  /**
   * @return the standby2
   */
  public float getStandby2() {
    return standby2;
  }

  /**
   * @param standby2 the standby2 to set
   */
  public void setStandby2(float standby2) {
    this.standby2 = standby2;
  }

  /**
   * @return the standby3
   */
  public float getStandby3() {
    return standby3;
  }

  /**
   * @param standby3 the standby3 to set
   */
  public void setStandby3(float standby3) {
    this.standby3 = standby3;
  }

 
}

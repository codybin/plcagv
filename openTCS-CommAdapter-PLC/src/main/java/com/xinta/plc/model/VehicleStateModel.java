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


   private int chargingSwitch;
   private float batteryPower;
   private float batteryVoltage;
   private float batteryCurrent;
   private float batteryTemprature;
   private int agvNumber;
   private int IP1;
   private int IP2;
   private int IP3;
   private int IP4;
   private int masterScheduling;
   private float agvVspeed;
   private float agvAspeed;
   private float totalMileage;
   private float runTime;
   private int agvRunState;
   private int errorErrorCode;
   private int warningErrorCode;
   private int lastSite;
   private int currentSite;
   private int nextSite;
   private int nextTwoSite;
   private int targetSite;
   private int targetSiteCarDirection;
   private int positioningState;
   private float betweenSiteMileage;
   private int navigationalState;
   private int currentSchedulingTask;
   private int materialStatus;
   private int taskStatus;

  /**
   * @return the chargingSwitch
   */
  public int getChargingSwitch() {
    return chargingSwitch;
  }

  /**
   * @param chargingSwitch the chargingSwitch to set
   */
  public void setChargingSwitch(int chargingSwitch) {
    this.chargingSwitch = chargingSwitch;
  }

  /**
   * @return the batteryPower
   */
  public float getBatteryPower() {
    return batteryPower;
  }

  /**
   * @param batteryPower the batteryPower to set
   */
  public void setBatteryPower(float batteryPower) {
    this.batteryPower = batteryPower;
  }

  /**
   * @return the batteryVoltage
   */
  public float getBatteryVoltage() {
    return batteryVoltage;
  }

  /**
   * @param batteryVoltage the batteryVoltage to set
   */
  public void setBatteryVoltage(float batteryVoltage) {
    this.batteryVoltage = batteryVoltage;
  }

  /**
   * @return the batteryCurrent
   */
  public float getBatteryCurrent() {
    return batteryCurrent;
  }

  /**
   * @param batteryCurrent the batteryCurrent to set
   */
  public void setBatteryCurrent(float batteryCurrent) {
    this.batteryCurrent = batteryCurrent;
  }

  /**
   * @return the batteryTemprature
   */
  public float getBatteryTemprature() {
    return batteryTemprature;
  }

  /**
   * @param batteryTemprature the batteryTemprature to set
   */
  public void setBatteryTemprature(float batteryTemprature) {
    this.batteryTemprature = batteryTemprature;
  }

  /**
   * @return the agvNumber
   */
  public int getAgvNumber() {
    return agvNumber;
  }

  /**
   * @param agvNumber the agvNumber to set
   */
  public void setAgvNumber(int agvNumber) {
    this.agvNumber = agvNumber;
  }

  /**
   * @return the IP1
   */
  public int getIP1() {
    return IP1;
  }

  /**
   * @param IP1 the IP1 to set
   */
  public void setIP1(int IP1) {
    this.IP1 = IP1;
  }

  /**
   * @return the IP2
   */
  public int getIP2() {
    return IP2;
  }

  /**
   * @param IP2 the IP2 to set
   */
  public void setIP2(int IP2) {
    this.IP2 = IP2;
  }

  /**
   * @return the IP3
   */
  public int getIP3() {
    return IP3;
  }

  /**
   * @param IP3 the IP3 to set
   */
  public void setIP3(int IP3) {
    this.IP3 = IP3;
  }

  /**
   * @return the IP4
   */
  public int getIP4() {
    return IP4;
  }

  /**
   * @param IP4 the IP4 to set
   */
  public void setIP4(int IP4) {
    this.IP4 = IP4;
  }

  /**
   * @return the masterScheduling
   */
  public int getMasterScheduling() {
    return masterScheduling;
  }

  /**
   * @param masterScheduling the masterScheduling to set
   */
  public void setMasterScheduling(int masterScheduling) {
    this.masterScheduling = masterScheduling;
  }

  /**
   * @return the agvVspeed
   */
  public float getAgvVspeed() {
    return agvVspeed;
  }

  /**
   * @param agvVspeed the agvVspeed to set
   */
  public void setAgvVspeed(float agvVspeed) {
    this.agvVspeed = agvVspeed;
  }

  /**
   * @return the agvAspeed
   */
  public float getAgvAspeed() {
    return agvAspeed;
  }

  /**
   * @param agvAspeed the agvAspeed to set
   */
  public void setAgvAspeed(float agvAspeed) {
    this.agvAspeed = agvAspeed;
  }

  /**
   * @return the totalMileage
   */
  public float getTotalMileage() {
    return totalMileage;
  }

  /**
   * @param totalMileage the totalMileage to set
   */
  public void setTotalMileage(float totalMileage) {
    this.totalMileage = totalMileage;
  }

  /**
   * @return the runTime
   */
  public float getRunTime() {
    return runTime;
  }

  /**
   * @param runTime the runTime to set
   */
  public void setRunTime(float runTime) {
    this.runTime = runTime;
  }

  /**
   * @return the agvRunState
   */
  public int getAgvRunState() {
    return agvRunState;
  }

  /**
   * @param agvRunState the agvRunState to set
   */
  public void setAgvRunState(int agvRunState) {
    this.agvRunState = agvRunState;
  }

  /**
   * @return the errorErrorCode
   */
  public int getErrorErrorCode() {
    return errorErrorCode;
  }

  /**
   * @param errorErrorCode the errorErrorCode to set
   */
  public void setErrorErrorCode(int errorErrorCode) {
    this.errorErrorCode = errorErrorCode;
  }

  /**
   * @return the warningErrorCode
   */
  public int getWarningErrorCode() {
    return warningErrorCode;
  }

  /**
   * @param warningErrorCode the warningErrorCode to set
   */
  public void setWarningErrorCode(int warningErrorCode) {
    this.warningErrorCode = warningErrorCode;
  }

  /**
   * @return the lastSite
   */
  public int getLastSite() {
    return lastSite;
  }

  /**
   * @param lastSite the lastSite to set
   */
  public void setLastSite(int lastSite) {
    this.lastSite = lastSite;
  }

  /**
   * @return the currentSite
   */
  public int getCurrentSite() {
    return currentSite;
  }

  /**
   * @param currentSite the currentSite to set
   */
  public void setCurrentSite(int currentSite) {
    this.currentSite = currentSite;
  }

  /**
   * @return the nextSite
   */
  public int getNextSite() {
    return nextSite;
  }

  /**
   * @param nextSite the nextSite to set
   */
  public void setNextSite(int nextSite) {
    this.nextSite = nextSite;
  }

  /**
   * @return the nextTwoSite
   */
  public int getNextTwoSite() {
    return nextTwoSite;
  }

  /**
   * @param nextTwoSite the nextTwoSite to set
   */
  public void setNextTwoSite(int nextTwoSite) {
    this.nextTwoSite = nextTwoSite;
  }

  /**
   * @return the targetSite
   */
  public int getTargetSite() {
    return targetSite;
  }

  /**
   * @param targetSite the targetSite to set
   */
  public void setTargetSite(int targetSite) {
    this.targetSite = targetSite;
  }

  /**
   * @return the targetSiteCarDirection
   */
  public int getTargetSiteCarDirection() {
    return targetSiteCarDirection;
  }

  /**
   * @param targetSiteCarDirection the targetSiteCarDirection to set
   */
  public void setTargetSiteCarDirection(int targetSiteCarDirection) {
    this.targetSiteCarDirection = targetSiteCarDirection;
  }

  /**
   * @return the positioningState
   */
  public int getPositioningState() {
    return positioningState;
  }

  /**
   * @param positioningState the positioningState to set
   */
  public void setPositioningState(int positioningState) {
    this.positioningState = positioningState;
  }

  /**
   * @return the betweenSiteMileage
   */
  public float getBetweenSiteMileage() {
    return betweenSiteMileage;
  }

  /**
   * @param betweenSiteMileage the betweenSiteMileage to set
   */
  public void setBetweenSiteMileage(float betweenSiteMileage) {
    this.betweenSiteMileage = betweenSiteMileage;
  }

  /**
   * @return the navigationalState
   */
  public int getNavigationalState() {
    return navigationalState;
  }

  /**
   * @param navigationalState the navigationalState to set
   */
  public void setNavigationalState(int navigationalState) {
    this.navigationalState = navigationalState;
  }

  /**
   * @return the currentSchedulingTask
   */
  public int getCurrentSchedulingTask() {
    return currentSchedulingTask;
  }

  /**
   * @param currentSchedulingTask the currentSchedulingTask to set
   */
  public void setCurrentSchedulingTask(int currentSchedulingTask) {
    this.currentSchedulingTask = currentSchedulingTask;
  }

  /**
   * @return the materialStatus
   */
  public int getMaterialStatus() {
    return materialStatus;
  }

  /**
   * @param materialStatus the materialStatus to set
   */
  public void setMaterialStatus(int materialStatus) {
    this.materialStatus = materialStatus;
  }

  /**
   * @return the taskStatus
   */
  public int getTaskStatus() {
    return taskStatus;
  }

  /**
   * @param taskStatus the taskStatus to set
   */
  public void setTaskStatus(int taskStatus) {
    this.taskStatus = taskStatus;
  }

  
 
}

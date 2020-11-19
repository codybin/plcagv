/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tryvehicle;

/***********************************************************************
 * Module:  Vehiclemode.java
 * Author:  Lenovo
 * Purpose: Defines the Class Vehiclemode
 ***********************************************************************/

import java.util.*;
import javax.inject.Inject;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleProcessModel;

public class Vehiclemode   extends  VehicleProcessModel{
   private int power;
   private int position;
   private int ip;
   private int port;
   private int enablePeriod;
   private int intervalTime;
   private int actionState;
   private int currentOderID;
   private int startPoint;
   private int endPoint;
   private int speed;
@Inject
  public Vehiclemode(Vehicle attachedVehicle) {
    super(attachedVehicle);
  }
  
  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    int oldvalue=this.position;
    this.position = position;
    getPropertyChangeSupport().firePropertyChange(Attribute.Position.name(), oldvalue, this.position);
  }

  public int getIp() {
    return ip;
  }

  public void setIp(int ip) {
   
     int oldvalue=this.ip;
     this.ip = ip; 
    getPropertyChangeSupport().firePropertyChange(Attribute.IP.name(), oldvalue, power);
  }

  public int getEnablePeriod() {
    return enablePeriod;
  }

  public void setEnablePeriod(int enablePeriod) {
    this.enablePeriod = enablePeriod;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getIntervalTime() {
    return intervalTime;
  }

  public void setIntervalTime(int intervalTime) {
    this.intervalTime = intervalTime;
  }

  public int getActionState() {
    return actionState;
  }

  public void setActionState(int actionState) {
    this.actionState = actionState;
  }

  public int getCurrentOderID() {
    return currentOderID;
  }

  public void setCurrentOderID(int currentOderID) {
    this.currentOderID = currentOderID;
  }

  public int getStartPoint() {
    return startPoint;
  }

  public void setStartPoint(int startPoint) {
    this.startPoint = startPoint;
  }

  public int getEndPoint() {
    return endPoint;
  }

  public void setEndPoint(int endPoint) {
    this.endPoint = endPoint;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }
  
  public static enum Attribute {
   
Position,
IP,
Port,
EnablePeridRequestState,
IntervalTime,
ActionState,
CurrentOrderID,
StartPoint,
EndPoint,
Speed;
  }
  
  
}
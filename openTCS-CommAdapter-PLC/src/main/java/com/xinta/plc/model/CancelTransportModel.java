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
public class CancelTransportModel implements Serializable{ 
  private boolean immediate;

  @Override
  public String toString() {
    return "CancelTransportModel{" + "immediate=" + immediate + ", disableVehicle=" + disableVehicle + '}';
  }

  public boolean isImmediate() {
    return immediate;
  }

  public void setImmediate(boolean immediate) {
    this.immediate = immediate;
  }

  public boolean isDisableVehicle() {
    return disableVehicle;
  }

  public void setDisableVehicle(boolean disableVehicle) {
    this.disableVehicle = disableVehicle;
  }
  private boolean disableVehicle;

  public CancelTransportModel(boolean immediate, boolean disableVehicle) {
    this.immediate = immediate;
    this.disableVehicle = disableVehicle;
  }
  
  
}

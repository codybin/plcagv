/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xinta.plc.model;

import com.xintai.messageserviceinterface.DispacherTaskState;
import com.xintai.messageserviceinterface.MaterialState;
import com.xintai.messageserviceinterface.PLCTaskState;

/**
 *
 * @author Lenovo
 */
public class VehicleTaskState {

  public MaterialState getMaterialState() {
    return materialState;
  }

  public void setMaterialState(MaterialState materialState) {
    this.materialState = materialState;
  }

  public PLCTaskState getpLCTaskState() {
    return pLCTaskState;
  }

  public void setpLCTaskState(PLCTaskState pLCTaskState) {
    this.pLCTaskState = pLCTaskState;
  }

  public DispacherTaskState getDispacherTaskState() {
    return dispacherTaskState;
  }

  public void setDispacherTaskState(DispacherTaskState dispacherTaskState) {
    this.dispacherTaskState = dispacherTaskState;
  }

  public VehicleTaskState() {
  }
  private MaterialState materialState;
  private PLCTaskState pLCTaskState;
  private DispacherTaskState dispacherTaskState;

  public VehicleTaskState(MaterialState materialState, PLCTaskState pLCTaskState,
                          DispacherTaskState dispacherTaskState) {
    this.materialState = materialState;
    this.pLCTaskState = pLCTaskState;
    this.dispacherTaskState = dispacherTaskState;
  }

}

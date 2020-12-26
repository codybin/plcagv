/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.messageserviceinterface;

/**
 *
 * @author Lenovo
 */

public class TaskInteractionInformation {

  public TaskInteractionInformation() {
  }

  public int getMaterialnum() {
    return materialnum;
  }

  public void setMaterialnum(int materialnum) {
    this.materialnum = materialnum;
  }

  public DispacherTaskState getDispacherTaskState() {
    return dispacherTaskState;
  }

  public void setDispacherTaskState(DispacherTaskState dispacherTaskState) {
    this.dispacherTaskState = dispacherTaskState;
  }

  public ChargerStaionState getChargerStaionState() {
    return chargerStaionState;
  }

  public void setChargerStaionState(ChargerStaionState chargerStaionState) {
    this.chargerStaionState = chargerStaionState;
  }

  /**
   *
   * @param materialnum 物流号码
   * @param dispacherTaskState 任务状态
   * @param chargerStaionState 充电站状态
   */
  public TaskInteractionInformation(int materialnum, DispacherTaskState dispacherTaskState,
                                    ChargerStaionState chargerStaionState) {
    this.materialnum = materialnum;
    this.dispacherTaskState = dispacherTaskState;
    this.chargerStaionState = chargerStaionState;
  }
  private int materialnum;
  private DispacherTaskState dispacherTaskState; 
  private ChargerStaionState chargerStaionState;
}

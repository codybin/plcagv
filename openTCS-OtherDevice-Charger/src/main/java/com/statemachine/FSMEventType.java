/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.statemachine;

/**
 *
 * @author Lenovo
 */
public enum FSMEventType {
  //盘头入库状态
  TOB,
  toIdelState,
  toDeviceMiddlePT,
  toBrigeZonePTRK,
  toWaitForUnLoadPT,
  toUnLoadStatePT,
  toFinshedUnLoadPT,
  //盘头出库 状态
  toBrigeZonePTCK,
  toWaitForLoadCK,
  toLoadStateCK,
  toFinshedLoadCK,
  toDeviceZoneCK,
  //原料入库状态
  toDeviceMiddleZoneCK,
  toBrigeZoneYLRK,
  toWaitForUnLoadYL,
  toUnLoadStateYL,
  toFinshedUnLoadYL,
  //原料出库
  toBrigeZoneYLCK,
  toWaitForLoadYL,
  toLoadStateYL,
  toFinshedLoadYL,
  toDeviceZoneYL,
}




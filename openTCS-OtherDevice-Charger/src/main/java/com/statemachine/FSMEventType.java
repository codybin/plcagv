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
  //��ͷ���״̬
  TOB,
  toIdelState,
  toDeviceMiddlePT,
  toBrigeZonePTRK,
  toWaitForUnLoadPT,
  toUnLoadStatePT,
  toFinshedUnLoadPT,
  //��ͷ���� ״̬
  toBrigeZonePTCK,
  toWaitForLoadCK,
  toLoadStateCK,
  toFinshedLoadCK,
  toDeviceZoneCK,
  //ԭ�����״̬
  toDeviceMiddleZoneCK,
  toBrigeZoneYLRK,
  toWaitForUnLoadYL,
  toUnLoadStateYL,
  toFinshedUnLoadYL,
  //ԭ�ϳ���
  toBrigeZoneYLCK,
  toWaitForLoadYL,
  toLoadStateYL,
  toFinshedLoadYL,
  toDeviceZoneYL,
}




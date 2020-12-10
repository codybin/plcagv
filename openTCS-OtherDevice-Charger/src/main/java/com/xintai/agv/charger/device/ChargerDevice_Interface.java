/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.agv.charger.device;

/**
 *
 * @author Lenovo
 */
public interface ChargerDevice_Interface {
  
  void StopCharger();
  void StartCharger();
  void ReadCurrent();
  void ReadVoltage();
  void ReadCurrentCapacity();
  void ReadTime();
  byte[] GetCurrentChargerTailState();
  byte[] GetCurrentState();
    boolean InitDevice();
  boolean isInit();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.agv.charger.device.taitan;

/**
 *
 * @author Lenovo
 */
public class ChargeStatuesModel {

  @Override
  public String toString() {
    return "ChargeStatuesModel{" + "hardwareerro=" + hardwareerro + ", chargetemprature=" + chargetemprature + ", voltagestatue=" + voltagestatue + ", batteryinput=" + batteryinput + ", communictationstatu=" + communictationstatu + ", workestate=" + workestate + ", tailestateout=" + tailestateout + ", tailestatein=" + tailestatein + ", taileouterro=" + taileouterro + ", taileinerro=" + taileinerro + ", chargerfinishstate=" + chargerfinishstate + '}';
  }

  public byte getHardwareerro() {
    return hardwareerro;
  }

  public byte getChargetemprature() {
    return chargetemprature;
  }

  public byte getVoltagestatue() {
    return voltagestatue;
  }

  public byte getBatteryinput() {
    return batteryinput;
  }

  public byte getCommunictationstatu() {
    return communictationstatu;
  }

  public byte getWorkestate() {
    return workestate;
  }

  public byte getTailestateout() {
    return tailestateout;
  }

  public byte getTailestatein() {
    return tailestatein;
  }

  public byte getTaileouterro() {
    return taileouterro;
  }

  public byte getTaileinerro() {
    return taileinerro;
  }

  public byte getChargerfinishstate() {
    return chargerfinishstate;
  }
  private byte hardwareerro;
  private byte chargetemprature;
  private byte voltagestatue;
  private byte batteryinput;
  private byte communictationstatu;
  private byte workestate;
  private byte tailestateout;
  private byte tailestatein;
  private byte taileouterro;
  private byte taileinerro;
  private byte chargerfinishstate; 

  public ChargeStatuesModel(int data) {
    decode(data);
  }
  
  private void decode(int data)
  {
   hardwareerro=getbit(data, 0);
   chargetemprature=getbit(data, 1);;
   voltagestatue=getbit(data, 2);;
    batteryinput=getbit(data, 3);;
   communictationstatu=getbit(data, 4);
   workestate=getbit(data, 5);
   tailestateout=getbit(data, 6);;
  tailestatein=getbit(data, 7);;
  taileouterro=getbit(data, 8);;
   taileinerro=getbit(data, 9);;
   chargerfinishstate=getbit(data, 10);; 
  
  
  
  
  
  
  
  
  
  
  
  }
 private byte getbit(int data,int i)
  {
  return (byte)((data>> i) & 0x01);
 
  }
}

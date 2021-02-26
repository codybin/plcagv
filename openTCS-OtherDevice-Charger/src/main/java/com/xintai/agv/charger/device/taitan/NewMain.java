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
public class NewMain {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    ChargerDeviceService chargerDeviceService=new ChargerDeviceService("127.0.0.1", 3);
    ChargerStateModel chargerStateModel= chargerDeviceService.getChargerStateModel();
    System.out.println(chargerStateModel.toString());
    System.out.println(chargerStateModel.getChargeStatuesModel().toString());
    System.out.println(chargerDeviceService.WriteChargeSet(1, 6));
  }
  
}

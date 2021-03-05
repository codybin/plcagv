/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.charger.wrapper;

import com.xintai.agv.charger.device.taitan.ChargerDeviceService;
import com.xintai.plc.comadpater.PLCProcessModel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Lenovo
 */
public class Charger_DeviceTaiTan {
  
  ChargerDeviceService  chargerDeviceService;
  
   ExecutorService poolExecutorServic;
  private final PLCProcessModel plcmode;
  public Charger_DeviceTaiTan(int slaveid,String hostString,PLCProcessModel pLCProcessModel) {
        chargerDeviceService=new ChargerDeviceService(hostString,slaveid);
        poolExecutorServic=Executors.newFixedThreadPool(2);
        this.plcmode=pLCProcessModel;
  }

  public  void StartCharge() 
  {
    Runnable callable=() -> {
      System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+before init");
      boolean result=false;
      
      chargerDeviceService.WriteChargeSet(1, 2);
     //plcmode.setIscharging(2==chargerDevice_Interface.GetCurrentChargerTailState()[0]);
        System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+启动成功");
        System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+set mark"
            + "sucess");
    };
    call(callable);
  }

  private void call(Runnable callable) {
    poolExecutorServic.submit(callable);
  }
  public void StopCharge()
  {
     Runnable callable=() -> {
     // System.out.println("com.xintai.charger.wrapper.ChargerUtl.StopCharge()+停止成功");
  // plcmode.setIscharging(!(4==chargerDevice_Interface.GetCurrentChargerTailState()[0]));
    };
 call(callable); 
  }
  
  
  
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.charger.wrapper;

import com.xintai.agv.charger.device.ChargeDeviceImp;
import com.xintai.agv.charger.device.ChargerDevice_Interface;
import com.xintai.plc.comadpater.PLCProcessModel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lenovo
 */
public class ChargerUtl {
  ExecutorService poolExecutorServic;
  private final PLCProcessModel plcmode;
  public ChargerUtl(int slaveid,String hostString,PLCProcessModel pLCProcessModel) {
        chargerDevice_Interface=new ChargeDeviceImp(slaveid, hostString, 9600, 8,0, 1, 0);
        poolExecutorServic=Executors.newFixedThreadPool(2);
        this.plcmode=pLCProcessModel;
  }
  ChargerDevice_Interface chargerDevice_Interface;

  public  void StartCharge() 
  {
    Runnable callable=() -> {
      System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+before init");
    if(!chargerDevice_Interface.isInit())
     chargerDevice_Interface.InitDevice();
      if(chargerDevice_Interface.isInit())
      {
         System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+init sucess");
        while( chargerDevice_Interface.GetCurrentChargerTailState()[0]!=2)  {
      //  chargerDevice_Interface.StartCharger();
          System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+start the process of charging");
        }
     plcmode.setIscharging(2==chargerDevice_Interface.GetCurrentChargerTailState()[0]);
        System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+启动成功");
        System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+set mark"
            + "sucess");
      }
     
    };
    call(callable);
  }

  private void call(Runnable callable) {
    poolExecutorServic.submit(callable);
  }
  public void StopCharge()
  {
     Runnable callable=() -> {
      chargerDevice_Interface.InitDevice();
    if(chargerDevice_Interface.isInit())
    {
   while(chargerDevice_Interface.GetCurrentChargerTailState()[0]!=4)
   {
     System.out.println("com.xintai.charger.wrapper.ChargerUtl.StopCharge()+正在停止充电机");
     //chargerDevice_Interface.StopCharger();
   }
      System.out.println("com.xintai.charger.wrapper.ChargerUtl.StopCharge()+停止成功");
   plcmode.setIscharging(!(4==chargerDevice_Interface.GetCurrentChargerTailState()[0]));
    }
    };
 call(callable); 
  }
}

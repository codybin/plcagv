/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.charger.wrapper;

import com.xintai.agv.charger.device.ChargeDeviceImp;
import com.xintai.agv.charger.device.ChargerDevice_Interface;
import com.xintai.agv.charger.device.taitan.ChargerDeviceService;
import com.xintai.agv.charger.device.taitan.ChargerStateModel;
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

  public ChargerUtl(int slaveid, String hostString, PLCProcessModel pLCProcessModel) {
    chargerDeviceService = new ChargerDeviceService(hostString, slaveid);
    poolExecutorServic = Executors.newFixedThreadPool(2);
    this.plcmode = pLCProcessModel;
  }
  ChargerDeviceService chargerDeviceService;

  public void StartCharge() {
    Runnable callable = () -> {
//      System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+before init");
//      if (!chargerDevice_Interface.isInit()) {
//        chargerDevice_Interface.InitDevice();
//      }
//      if (chargerDevice_Interface.isInit()) {
//      System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+init sucess");
      boolean ChargerDeviceReceived = false;
      while (chargerDeviceService.getChargerStateModel().getChargeStatuesModel().getChargerfinishstate() == 1
          || chargerDeviceService.getChargerStateModel().getChargeStatuesModel().getTaileouterro() == 1) {
        System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+����ϴγ������ź�");
        if (!ChargerDeviceReceived) {
          ChargerDeviceReceived = chargerDeviceService.WriteChargeSet(0, 1);
        }
      }
      ChargerDeviceReceived = false;

      while (chargerDeviceService.getChargerStateModel().getChargeStatuesModel().getWorkestate() == 0) {
        //  chargerDevice_Interface.StartCharger();
        if (!ChargerDeviceReceived) {
          ChargerDeviceReceived = chargerDeviceService.WriteChargeSet(1, 1);
        }
        if (chargerDeviceService.getChargerStateModel().getChargeStatuesModel().getTaileouterro() == 1) {
          System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+��ˢ����쳣");
          break;
        }
        System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+���������");
      }
//        plcmode.setIscharging(2 == chargerDevice_Interface.GetCurrentChargerTailState()[0]);
      plcmode.setIscharging(chargerDeviceService.getChargerStateModel().getChargeStatuesModel().getWorkestate() == 0);
      System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+�����ɹ�");
      System.out.println("com.xintai.charger.wrapper.ChargerUtl.StartCharge()+set mark"
          + "sucess");
      plcmode.setIscharging(true);
//      }

    };
    call(callable);
  }

  private void call(Runnable callable) {
    poolExecutorServic.submit(callable);
  }

  public void StopCharge() {
    Runnable callable = () -> {
//      chargerDevice_Interface.InitDevice();
//      if (chargerDevice_Interface.isInit()) {
//        while (chargerDevice_Interface.GetCurrentChargerTailState()[0] != 4) {
//          System.out.println("com.xintai.charger.wrapper.ChargerUtl.StopCharge()+����ֹͣ����");
//          //chargerDevice_Interface.StopCharger();
//        }
//        System.out.println("com.xintai.charger.wrapper.ChargerUtl.StopCharge()+ֹͣ�ɹ�");
//        plcmode.setIscharging(!(4 == chargerDevice_Interface.GetCurrentChargerTailState()[0]));
//      }
      boolean ChargerDeviceReceived = false;
      while (chargerDeviceService.getChargerStateModel().getChargeStatuesModel().getTailestatein() == 0) {
        System.out.println("com.xintai.charger.wrapper.ChargerUtl.StopCharge()+��ˢ������");
        if (!ChargerDeviceReceived) {
          ChargerDeviceReceived = chargerDeviceService.WriteChargeSet(0, 1);
        }
        if (chargerDeviceService.getChargerStateModel().getChargeStatuesModel().getTaileinerro() == 1) {
          System.out.println("com.xintai.charger.wrapper.ChargerUtl.StopCharge()+��ˢ�����쳣");
          break;
        }
      }
      System.out.println("com.xintai.charger.wrapper.ChargerUtl.StopCharge()+ֹͣ�ɹ�");
      plcmode.setIscharging(!(chargerDeviceService.getChargerStateModel().getChargeStatuesModel().getTailestatein() == 0));
      plcmode.setIscharging(false);
    };
    call(callable);
  }
}

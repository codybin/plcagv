/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.comand;

import com.xintai.vehicle.comadpter.KeCongCommAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;

/**
 *
 * @author Lenovo
 */
public class SendComandPeriodicStateRequestEnabled  implements AdapterCommand{

  private final boolean enable;

   public SendComandPeriodicStateRequestEnabled(boolean  enable)
   {
   this.enable=enable;
   
   }
       @Override
  public void execute(VehicleCommAdapter adapter) {
      if (!(adapter instanceof KeCongCommAdapter)) {
      return;
    }
    KeCongCommAdapter exampleAdapter = (KeCongCommAdapter) adapter;
      exampleAdapter.getProcessModel().setPeriodicEnable(enable);
  }
  
}

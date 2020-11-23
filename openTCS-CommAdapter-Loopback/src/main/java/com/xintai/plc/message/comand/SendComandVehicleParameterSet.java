/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.message.comand;

/**
 *
 * @author Administrator
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.xinta.plc.model.VehicleParameterSetWithPLCMode;
import com.xintai.plc.comadpater.PLCComAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;

import com.xintai.plc.comadpater.PLCComAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;

import org.opentcs.drivers.vehicle.AdapterCommand;



public class SendComandVehicleParameterSet  implements AdapterCommand{

  private final VehicleParameterSetWithPLCMode  vst;

   public SendComandVehicleParameterSet(VehicleParameterSetWithPLCMode  vst)
   {
   this.vst=vst;
   
   }
       @Override
  public void execute(VehicleCommAdapter adapter) {
      if (!(adapter instanceof PLCComAdapter)) {
      return;
    }
    PLCComAdapter plcAdapter = (PLCComAdapter) adapter;
    plcAdapter.getProcessModel().setVehicleParameterSet(vst);
  }
  
}


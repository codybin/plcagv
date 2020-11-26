/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.message.comand;

import com.xintai.plc.comadpater.PLCComAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;


public class SendComandPostion  implements AdapterCommand{

  private final String  postionString;

   public SendComandPostion(String  postionString)
   {
   this.postionString=postionString;
   
   }
       @Override
  public void execute(VehicleCommAdapter adapter) {
      if (!(adapter instanceof PLCComAdapter)) {
      return;
    }
    PLCComAdapter exampleAdapter = (PLCComAdapter) adapter;
      exampleAdapter.getProcessModel().setVehiclePosition(postionString);
  }
  
}

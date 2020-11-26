/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.comand;
import com.xintai.kecong.message.KeCongComandMakeSurePosition;
import com.xintai.vehicle.comadpter.KeCongCommAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
/**
 *
 * @author Lenovo
 */
public class SendComandMakeSurePoint implements AdapterCommand {

  @Override
  public void execute(VehicleCommAdapter adapter) {
    if (!(adapter instanceof KeCongCommAdapter)) {
      return;
    }

    KeCongCommAdapter exampleAdapter = (KeCongCommAdapter) adapter;
    KeCongComandMakeSurePosition kecongauto=new KeCongComandMakeSurePosition();
    exampleAdapter.getRequestResponseMatcher().enqueueRequest(kecongauto);
    System.out.println("com.xintai.kecong.comand.SendComandAutoRequest.execute()");
  }
  
}

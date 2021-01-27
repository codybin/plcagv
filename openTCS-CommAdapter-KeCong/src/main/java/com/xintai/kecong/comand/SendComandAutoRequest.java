/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.comand;

import com.xintai.kecong.message.rqst.KeCongComandSwitchAutoOrManul;
import com.xintai.vehicle.comadpter.KeCongCommAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;

/**
 *
 * @author Lenovo
 */
public class SendComandAutoRequest implements AdapterCommand{

  private final byte auto;
  public SendComandAutoRequest(byte auto)
  {this.auto=auto;
  }
  @Override
  public void execute(VehicleCommAdapter adapter) {
      if (!(adapter instanceof KeCongCommAdapter)) {
      return;
    }

    KeCongCommAdapter exampleAdapter = (KeCongCommAdapter) adapter;
    KeCongComandSwitchAutoOrManul kecongauto=new KeCongComandSwitchAutoOrManul(auto);
    exampleAdapter.getRequestResponseMatcher().enqueueRequest(kecongauto);
      exampleAdapter.getProcessModel().setAutoRunMark(auto==1);
    System.out.println("com.xintai.kecong.comand.SendComandAutoRequest.execute()");
  }
  
}

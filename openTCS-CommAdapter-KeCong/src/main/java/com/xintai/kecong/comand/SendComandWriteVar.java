/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.comand;

import com.xintai.kecong.message.rqst.KeCongComandWrite;
import com.xintai.vehicle.comadpter.KeCongCommAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;

/**
 *
 * @author Lenovo
 */
public class SendComandWriteVar implements AdapterCommand{

  private final String name;
  private final byte[] varvalue;
public SendComandWriteVar(String name,byte[] varvalue)
{
this.name=name;
this.varvalue=varvalue;
}
  @Override
  public void execute(VehicleCommAdapter adapter) {
     KeCongComandWrite keCongComandWrite=  new KeCongComandWrite( name, varvalue);
     if (!(adapter instanceof KeCongCommAdapter)) {
      return;
    }

    KeCongCommAdapter exampleAdapter = (KeCongCommAdapter) adapter;
    exampleAdapter.getRequestResponseMatcher().enqueueRequest(keCongComandWrite); 
    System.out.println("com.xintai.kecong.comand.SendComandWriteVar.execute()");
  }
  
}

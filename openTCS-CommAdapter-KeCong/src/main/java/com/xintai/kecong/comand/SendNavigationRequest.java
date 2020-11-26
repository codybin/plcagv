/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.comand;

import com.xintai.kecong.message.KeCongComandNavigateControl;
import com.xintai.vehicle.comadpter.KeCongCommAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
/**
 *
 * @author Lenovo
 */
public class SendNavigationRequest implements AdapterCommand{
 private final String pathid;
private  final byte operation;
private  final byte usetraficornot;

  
  public SendNavigationRequest(String pathid,byte operation,byte usetraficornot)
  {
  
  this.operation=operation;
    System.err.println(pathid);
  this.pathid=pathid;
  this.usetraficornot=usetraficornot;
  }
  
  @Override
  public void execute(VehicleCommAdapter adapter) {
   KeCongComandNavigateControl kgcComandNavigateControl=  new KeCongComandNavigateControl(pathid,operation,usetraficornot);
     if (!(adapter instanceof KeCongCommAdapter)) {
      return;
    }

    KeCongCommAdapter exampleAdapter = (KeCongCommAdapter) adapter;
    exampleAdapter.getRequestResponseMatcher().enqueueRequest(kgcComandNavigateControl); 
    System.out.println("com.xintai.kecong.comand.SendNavigationRequest.execute()");
  }
  
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.comand;
import com.xintai.kecong.message.KeCongComandRead;
import com.xintai.vehicle.comadpter.KeCongCommAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;

/**
 *
 * @author Lenovo
 */
public class SendComanReadVar implements AdapterCommand{

  private final String name;
public SendComanReadVar(String name)
{
 this.name=name;
}
  @Override
  public void execute(VehicleCommAdapter adapter) {
    //To change body of generated methods, choose Tools | Templates.
    KeCongComandRead keCongComandRead=new KeCongComandRead(name);
     if (!(adapter instanceof KeCongCommAdapter)) {
      return;
    }
    KeCongCommAdapter exampleAdapter = (KeCongCommAdapter) adapter;
    exampleAdapter.getRequestResponseMatcher().enqueueRequest(keCongComandRead); 
    System.out.println("com.xintai.kecong.comand.SendComanReadVar.execute()");
    
  }
  
}

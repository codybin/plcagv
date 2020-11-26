/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.vehicle.comadpter;

import java.util.ResourceBundle;
import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;

/**
 *
 * @author Lenovo
 */
public class KeCongCommAdapterDescription
    extends VehicleCommAdapterDescription {

  @Override
  public String getDescription() {
    return  ResourceBundle.getBundle("com/xintai/opentcs/commadapter/vehicle/Bundle_zn").
  getString("KeCongAdapterFactoryDescription");
  }/* ResourceBundle.getBundle("de/fraunhofer/iml/opentcs/example/commadapter/vehicle/Bundle").
  getString("ExampleAdapterFactoryDescription");*/

  @Override
  public boolean isSimVehicleCommAdapter() {
    return false;
  }
}

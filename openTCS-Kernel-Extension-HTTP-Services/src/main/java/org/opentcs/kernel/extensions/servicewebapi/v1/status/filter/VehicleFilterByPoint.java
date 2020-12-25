/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opentcs.kernel.extensions.servicewebapi.v1.status.filter;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.opentcs.data.model.Vehicle;

/**
 *
 * @author Lenovo
 */
public class VehicleFilterByPoint
    implements Predicate<Vehicle> {

  /**
   * The processing state of the requested vehicles.
   */
  @Nullable
  private final String pointname;

  public VehicleFilterByPoint(String pointname) {
    this.pointname = pointname;
  }

  @Override
  public boolean test(Vehicle vehicle) {
    if(vehicle.getCurrentPosition()==null)
      return  false;
    boolean accept = true;
    if (pointname != null && Integer.parseInt(pointname)!=new OpentcsPointToKeCongPoint(vehicle.getCurrentPosition().getName()).getIntPoint())
    {
      accept = false;
    }
    return accept;
  }

}
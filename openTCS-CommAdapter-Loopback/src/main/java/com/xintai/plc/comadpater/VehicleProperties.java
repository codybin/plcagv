/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.comadpater;

import com.xintai.vehicle.comadpter.*;

/**
 *
 * @author Lenovo
 */
public interface VehicleProperties {

  /**
   * The key of the vehicle property containing the vehicle's host name/IP address.
   */
  String PROPKEY_VEHICLE_HOST = "example:vehicleHost";
  /**
   * The key of the vehicle property containing the vehicle's TCP port.
   */
  String PROPKEY_VEHICLE_PORT = "example:vehiclePort";
  
  String PROPKRY_VEHICLE_INITIALPOSITIONS="initialpostion";
}

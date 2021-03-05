/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.device;

/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */

/**
 * A {@link org.opentcs.data.order.DriveOrder DriveOrder}'s destination.
 */
public class Destination {

  @Override
  public String toString() {
    return "Destination{" + "locationName=" + locationName + ", operation=" + operation + '}';
  }


  private String locationName ;

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  private String operation;


}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.vehicle.comadpter;

import org.opentcs.data.order.DriveOrder;

/**
 *
 * @author Lenovo
 */
public interface LoadAction {

  public static final String NONE = DriveOrder.Destination.OP_NOP;
  /**
   * A constant for adding load.
   */
  public static final String LOAD = "Load cargo";
  /**
   * A constant for removing load.
   */
  public static final String UNLOAD = "Unload cargo";
  /**
   * A constant for charging the battery.
   */
  public static final String CHARGE = "CHARGE";
}

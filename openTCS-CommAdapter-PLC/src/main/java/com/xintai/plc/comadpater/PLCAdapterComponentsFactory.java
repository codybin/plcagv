/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.comadpater;

/**
 *
 * @author Lenovo
 */

import java.awt.event.ActionListener;
import org.opentcs.data.model.Vehicle;

/**
 *
 * @author Lenovo
 */
public interface PLCAdapterComponentsFactory {

  /**
   * Creates a new ExampleCommAdapter for the given vehicle.
   *为给定的车辆创建新的适配器
   * @param vehicle The vehicle
   * @return A new ExampleCommAdapter for the given vehicle
   */
  PLCComAdapter createExampleCommAdapter(Vehicle vehicle);



  /**
   * Creates a new {@link StateRequesterTask}.
   *创建新的需求任务
   * @param stateRequestAction The actual action to be performed to enqueue requests.
   * @return The created {@link StateRequesterTask}.
   */
  StateRequesterTask createStateRequesterTask(ActionListener stateRequestAction);
}


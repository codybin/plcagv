/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.comand;

/**
 *
 * @author Lenovo
 */
/**
 * Copyright (c) Fraunhofer IML
 */


import com.xintai.kecong.message.KeCongRequestMessage;
import com.xintai.vehicle.comadpter.KeCongCommAdapter;
import static java.util.Objects.requireNonNull;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;

/**
 * A command for sending a telegram to the actual vehicle.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class SendRequestCommand
    implements AdapterCommand {

  /**
   * The request to send.
   */
 

  /**
   * Creates a new instance.
   *
   * @param request The request to send.
   */
  public SendRequestCommand() {
   // this.request = requireNonNull(request, "request");
  }

  @Override
  public void execute(VehicleCommAdapter adapter) {
    if (!(adapter instanceof KeCongCommAdapter)) {
      return;
    }

    KeCongCommAdapter exampleAdapter = (KeCongCommAdapter) adapter;
    exampleAdapter.getProcessModel().setVehicleEnergyLevel(100);
   // exampleAdapter.getRequestResponseMatcher().enqueueRequest(request);
  }
}


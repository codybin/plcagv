/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.task;

import com.xintai.kecong.message.rqst.KeCongComandNavigateControl;
import com.xintai.kecong.message.rqst.KeCongComandSwitchAutoOrManul;
import com.xintai.kecong.message.KeCongRobotStatuSearchResponse;
import com.xintai.kecong.model.RobotStatuResponseModel;
import com.xintai.kecong.robotutl.RobotUtl;
import com.xintai.vehicle.comadpter.KeCongCommAdapter;
import jdk.internal.org.jline.utils.Log;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.util.CyclicTask;

/**
 *
 * @author Lenovo
 */
public class VehicleActuralTask
    extends CyclicTask {

  private final KeCongCommAdapter newKeCongComAdapter;

  public VehicleActuralTask(KeCongCommAdapter newKeCongComAdapter) {
    super(100);
    this.newKeCongComAdapter = newKeCongComAdapter;
  }

  @Override
  protected void runActualTask() {
    MovementCommand movementCommand;
    synchronized (VehicleActuralTask.this) {
      movementCommand = newKeCongComAdapter.getComandMovementRequestQueue().peek();
    }
    if (movementCommand != null) {
      int destinationid = RobotUtl.pointmaptoint(movementCommand);
      KeCongComandNavigateControl kgcComandNavigateControl = new KeCongComandNavigateControl(String.valueOf(destinationid), (byte) 0, (byte) 0);
      RobotStatuResponseModel kcrssr = newKeCongComAdapter.getProcessModel().getRobotStatu();
      if (kcrssr.getCurrenttargetid() != destinationid) {
        if (kcrssr.getRunmode() != 1) {
          newKeCongComAdapter.getRequestResponseMatcher().enqueueRequest(new KeCongComandSwitchAutoOrManul((byte) 1));
        }
        else {
          newKeCongComAdapter.getRequestResponseMatcher().enqueueRequest(kgcComandNavigateControl);
          newKeCongComAdapter.getComandMovementRequestQueue().poll();
        }
      }
      else {
        //   newKeCongComAdapter.getComandMovementRequestQueue().poll();
      }
    }
  }
}

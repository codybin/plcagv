/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.messageserviceinterface;

import com.xintai.plc.message.VehicleParameterSetWithPLC;
import com.xintai.plc.message.VehicleStatePLC;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.VehicleProcessModel;

/**
 *
 * @author Lenovo
 */
public interface InterfaceMessageService {
  public void SendNavigateComand(MovementCommand movementCommand,VehicleProcessModel pLCProcessModel);
  public  VehicleStatePLC SendStateRequest();
  public void SendSettingTOPLC(VehicleParameterSetWithPLC  vehicleParameterSetWithPLC);
  public boolean  Init();
  public boolean Connect();
  public boolean DisConnect();    
}

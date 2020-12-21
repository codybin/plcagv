/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.messageserviceinterface;

import com.xintai.plc.message.VehicleParameterSetWithPLC;
import com.xintai.plc.message.VehicleStatePLC;
import java.awt.event.ActionListener;
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
  public boolean  IsInitial();
  public boolean  Init(IPParameter iPParameter);
  public boolean Connect();
  public boolean DisConnect(); 
  public boolean  HeartBeat();
  public boolean  IsConnected();
  public void SetDisConnectEvent(PLCConnectListenner pLCConnectListenner);
  public void SetConnectEvent(PLCConnectListenner plccl);
}

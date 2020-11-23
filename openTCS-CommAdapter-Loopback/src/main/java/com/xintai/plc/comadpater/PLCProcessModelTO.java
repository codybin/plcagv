/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.comadpater;

import com.xinta.plc.model.VehicleParameterSetWithPLCMode;
import com.xinta.plc.model.VehicleStateModel;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;

/**
 *
 * @author Lenovo
 */
public class PLCProcessModelTO extends  VehicleProcessModelTO{

  private VehicleStateModel previousVehicleStateModel;
  private String vehicleHost;
  private int vehiclePort;
  private TCSObjectReference<Vehicle> vehicleRef;
  public VehicleStateModel getPreviousVehicleStateModel() {
    return previousVehicleStateModel;
  }

  /**
   * @param previousVehicleStateModel the previousVehicleStateModel to set
   */
  public PLCProcessModelTO setPreviousVehicleStateModel(VehicleStateModel previousVehicleStateModel) {

    this.previousVehicleStateModel = previousVehicleStateModel;
   return  this;
  }
    public synchronized String getVehicleHost() {
    return vehicleHost;
  }
    public PLCProcessModelTO setVehicleHost(@Nonnull String vehicleHost) {
    
    this.vehicleHost = vehicleHost;
   return  this;
  
  }
     public synchronized int getVehiclePort() {
    return vehiclePort;
  }

  /**
   * Sets the TCP port number the vehicle is listening on.
   *设置车辆正在监听的端口号码
   * @param vehiclePort The TCP port number.
   */
  public PLCProcessModelTO setVehiclePort(int vehiclePort) {

    this.vehiclePort = vehiclePort;
     return  this;
  }
   public TCSObjectReference<Vehicle> getVehicleRef() {
    return vehicleRef;
  }
  
    public PLCProcessModelTO setVehicleRef(TCSObjectReference<Vehicle> vehicleRef) {
    this.vehicleRef = vehicleRef;
    return this;
  } private  VehicleParameterSetWithPLCMode vehicleset;
     public  VehicleParameterSetWithPLCMode getVehicleParameterSet() {
    return vehicleset;
  }

  /**
   * Sets the TCP port number the vehicle is listening on.
   *设置车辆正在监听的端口号码
   * @param vehiclePort The TCP port number.
   */
  public PLCProcessModelTO setVehicleParameterSet(VehicleParameterSetWithPLCMode vehicleset) {
    this.vehicleset = vehicleset;
     return this;
    }

}

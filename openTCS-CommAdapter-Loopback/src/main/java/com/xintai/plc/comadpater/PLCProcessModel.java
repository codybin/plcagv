/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.comadpater;

import com.xinta.plc.model.VehicleStateModel;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import static org.opentcs.util.Assertions.checkInRange;

/**
 *
 * @author Lenovo
 */
public class PLCProcessModel extends VehicleProcessModel  {

  /**
   * @return the previousVehicleStateModel
   */
  public VehicleStateModel getPreviousVehicleStateModel() {
    return previousVehicleStateModel;
  }

  /**
   * @param previousVehicleStateModel the previousVehicleStateModel to set
   */
  public void setPreviousVehicleStateModel(VehicleStateModel previousVehicleStateModel) {
     VehicleStateModel oldValue = this.previousVehicleStateModel;
    this.previousVehicleStateModel = requireNonNull(previousVehicleStateModel, "previousVehicleStateModel");

    getPropertyChangeSupport().firePropertyChange(PLCProcessModel.Attribute.VEHILCE_STATE.name(),
                                                  oldValue,
                                                  previousVehicleStateModel);
  }

  private String vehicleHost;
  private int vehiclePort;
   private VehicleStateModel previousVehicleStateModel;
  public PLCProcessModel(Vehicle attachedVehicle) {
    super(attachedVehicle);
    previousVehicleStateModel=new VehicleStateModel();
  }
    @Nonnull
  public synchronized String getVehicleHost() {
    return vehicleHost;
  }
    public synchronized void setVehicleHost(@Nonnull String vehicleHost) {
    String oldValue = this.vehicleHost;
    this.vehicleHost = requireNonNull(vehicleHost, "vehicleHost");

    getPropertyChangeSupport().firePropertyChange(PLCProcessModel.Attribute.VEHICLE_HOST.name(),
                                                  oldValue,
                                                  vehicleHost);
  }
     public synchronized int getVehiclePort() {
    return vehiclePort;
  }

  /**
   * Sets the TCP port number the vehicle is listening on.
   *设置车辆正在监听的端口号码
   * @param vehiclePort The TCP port number.
   */
  public synchronized void setVehiclePort(int vehiclePort) {
    int oldValue = this.vehiclePort;
    this.vehiclePort = checkInRange(vehiclePort, 1, 65535, "vehiclePort");

    getPropertyChangeSupport().firePropertyChange(PLCProcessModel.Attribute.VEHICLE_PORT.name(),
                                                  oldValue,
                                                  vehiclePort);}
    
    
      public static enum Attribute {   
    VEHICLE_HOST,//车辆主站
    VEHICLE_PORT,//车辆端口
    VEHILCE_STATE,
    
      }
}

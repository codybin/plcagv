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
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import static org.opentcs.util.Assertions.checkInRange;

/**
 *
 * @author Lenovo
 */
public class PLCProcessModel extends VehicleProcessModel  {
 private volatile boolean  ischarging=false;

  public boolean isIscharging() {
    return ischarging;
  }

  public void setIscharging(boolean ischarging) {
    this.ischarging = ischarging;
  }
  
  private final Object object=new Object();

  public Object getObjectForMesFinshWork() {
    return object;
  }

  private int currentnavigationpoint;
  private int nextcurrentnavigationpoint;
  private boolean singleStepModeEnabled;
  private boolean  finshmarkfromes;

  public synchronized boolean isFinshmarkfromes() {
    return finshmarkfromes;
  }

  public  void setFinshmarkfromes(boolean finshmarkfromes) {
    this.finshmarkfromes = finshmarkfromes;
    synchronized(object)
    {
      object.notify();
    }
  }
  
   public synchronized void setSingleStepModeEnabled(final boolean mode) {
    boolean oldValue = singleStepModeEnabled;
    singleStepModeEnabled = mode;

    getPropertyChangeSupport().firePropertyChange(Attribute.SINGLE_STEP_MODE.name(),
                                                  oldValue,
                                                  mode);
  }

  /**
   * Returns this communication adapter's <em>single step mode</em> flag.
   *
   * @return <code>true</code> if, and only if, this adapter is currently in
   * single step mode.
   */
  public synchronized boolean isSingleStepModeEnabled() {
    return singleStepModeEnabled;
  }
  
  public int getCurrentnavigationpoint() {
    return currentnavigationpoint;
  }

  public void setCurrentnavigationpoint(int currentnavigationpoint) {
    this.currentnavigationpoint = currentnavigationpoint;
  }

  public int getNextcurrentnavigationpoint() {
    return nextcurrentnavigationpoint;
  }

  public void setNextcurrentnavigationpoint(int nextcurrentnavigationpoint) {
    int oldvalue=this.nextcurrentnavigationpoint;
    this.nextcurrentnavigationpoint = nextcurrentnavigationpoint;
    if(oldvalue!=0)
    {
      setCurrentnavigationpoint(oldvalue);    
    }else
    {    
    setCurrentnavigationpoint(nextcurrentnavigationpoint);
    }
  }
  
  
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
   private volatile VehicleStateModel previousVehicleStateModel;
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
private  int slaveid;

  public int getSlaveid() {
    return slaveid;
  }

  public void setSlaveid(int slaveid) {
    this.slaveid = slaveid;
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
    private  VehicleParameterSetWithPLCMode vehicleset;
     public synchronized VehicleParameterSetWithPLCMode getVehicleParameterSet() {
    return vehicleset;
  }

  /**
   * Sets the TCP port number the vehicle is listening on.设置车辆正在监听的端口号码
   * @param vehicleset
   * @param vehiclePort The TCP port number.
   */
  public synchronized void setVehicleParameterSet(VehicleParameterSetWithPLCMode vehicleset) {
    VehicleParameterSetWithPLCMode oldValue = this.vehicleset;
    this.vehicleset = vehicleset;

    getPropertyChangeSupport().firePropertyChange(PLCProcessModel.Attribute.VEHICLE_SETPARAMETERS.name(),
                                                  oldValue,
                                                  vehicleset);}
      public static enum Attribute {   
    VEHICLE_HOST,//车辆主站
    VEHICLE_PORT,//车辆端口
    VEHILCE_STATE,
    VEHICLE_SETPARAMETERS,
    SINGLE_STEP_MODE,
      }
}

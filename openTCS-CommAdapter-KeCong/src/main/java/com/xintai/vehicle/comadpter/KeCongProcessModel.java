/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.vehicle.comadpter;


import com.xintai.kecong.message.KeCongRobotStatuSearchResponse;
import com.xintai.kecong.model.NavigateStatuResponseModel;
import com.xintai.kecong.model.ReadVarModel;
import com.xintai.kecong.model.RobotStatuResponseModel;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import org.opentcs.data.model.Triple;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import static org.opentcs.util.Assertions.checkInRange;

/**
 *
 * @author Lenovo
 */
public class KeCongProcessModel   extends VehicleProcessModel {

 private volatile RobotStatuResponseModel currentrobotstatuResponse;

  public RobotStatuResponseModel getCurrentrobotstatuResponse() {
    return currentrobotstatuResponse;
  }

  public void setCurrentrobotstatuResponse(RobotStatuResponseModel currentrobotstatuResponse) {
    this.currentrobotstatuResponse = currentrobotstatuResponse;
  }
  
  private boolean singleStepModeEnabled;

  
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
  /**
   * @return the ischarging
   */
  public boolean isIscharging() {
    return ischarging;
  }

  /**
   * @param ischarging the ischarging to set
   */
  public void setIscharging(boolean ischarging) {
    this.ischarging = ischarging;
  }

  private int vehiclePort;
  private  boolean autorun;
  private String vehicleHost;
 private  RobotStatuResponseModel keCongRobotStatuSearchResponse;
  private  RobotStatuResponseModel keCongRobotPreStatuSearchResponse;

  public RobotStatuResponseModel getKeCongRobotPreStatuSearchResponse() {
    return keCongRobotPreStatuSearchResponse;
  }

  public void setKeCongRobotPreStatuSearchResponse(
                                                   RobotStatuResponseModel keCongRobotPreStatuSearchResponse) {
    this.keCongRobotPreStatuSearchResponse = keCongRobotPreStatuSearchResponse;
  }
  private boolean periodicenable;
  private NavigateStatuResponseModel navigateStatuResponseModel;
 private  NavigateStatuResponseModel currentStateModel;
 private  NavigateStatuResponseModel previesStatuResponseModel;
  private ReadVarModel readVarModel;
  private final String loadOperation;
  private final String unloadOperation;
  private boolean IsConnected;
  public NavigateStatuResponseModel getCurrentStateModel() {
    return currentStateModel;
  }
private volatile  boolean  ischarging;
  public void setCurrentStateModel(NavigateStatuResponseModel currentStateModel) {
    this.currentStateModel = currentStateModel;
  }

  public NavigateStatuResponseModel getPreviesStatuResponseModel() {
    return previesStatuResponseModel;
  }

  public void setPreviesStatuResponseModel(NavigateStatuResponseModel previesStatuResponseModel) {
    this.previesStatuResponseModel = previesStatuResponseModel;
  }

  public KeCongProcessModel(Vehicle attachedVehicle) {
    super(attachedVehicle);
   this.previesStatuResponseModel=new NavigateStatuResponseModel();
    this.currentStateModel=new NavigateStatuResponseModel();
     this.loadOperation = extractLoadOperation(attachedVehicle);
    this.unloadOperation = extractUnloadOperation(attachedVehicle);
  }
  private static String extractLoadOperation(Vehicle attachedVehicle) {
    String result = attachedVehicle.getProperty(KeCongAdapterConstants.PROPKEY_LOAD_OPERATION);
    if (result == null) {
      result = KeCongAdapterConstants.PROPVAL_LOAD_OPERATION_DEFAULT;
    }
    return result;
  }

  private static String extractUnloadOperation(Vehicle attachedVehicle) {
    String result = attachedVehicle.getProperty(KeCongAdapterConstants.PROPKEY_UNLOAD_OPERATION);
    if (result == null) {
      result = KeCongAdapterConstants.PROPVAL_UNLOAD_OPERATION_DEFAULT;
    }
    return result;
  }
   /**
   * Returns the vehicle's host name/IP address.
   *返回车辆的主机名字和ip
   * @return The vehicle's host name/IP address.
   */
  @Nonnull
  public synchronized String getVehicleHost() {
    return vehicleHost;
  }
 public String getLoadOperation() {
    return this.loadOperation;
  }

  public String getUnloadOperation() {
    return this.unloadOperation;
  }
  /**
   * Sets the vehicle's host name/IP address.
   *设置车辆的主机名字/ip地址
   * @param vehicleHost The vehicle's host name/IP address.
   */
  public synchronized void setVehicleHost(@Nonnull String vehicleHost) {
    String oldValue = this.vehicleHost;
    this.vehicleHost = requireNonNull(vehicleHost, "vehicleHost");

    getPropertyChangeSupport().firePropertyChange(Attribute.VEHICLE_HOST.name(),
                                                  oldValue,
                                                  vehicleHost);
  }
    /**
   * Returns the TCP port number the vehicle is listening on.
   *返回车辆正在监听的端口
   * @return The TCP port number the vehicle is listening on.
   */
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

    getPropertyChangeSupport().firePropertyChange(KeCongProcessModel.Attribute.VEHICLE_PORT.name(),
                                                  oldValue,
                                                  vehiclePort);}
  
  
  @Nonnull
  public synchronized boolean getAutoRunMark() {
    return autorun;
  }
    public synchronized void setAutoRunMark(boolean autorun) {
    boolean oldValue = this.autorun;
    this.autorun = autorun;
    getPropertyChangeSupport().firePropertyChange(KeCongProcessModel.Attribute.AUTO_RUN.name(),
                                                  oldValue,
                                                  autorun);}
     @Nonnull
  public synchronized RobotStatuResponseModel getRobotStatu() {
    return keCongRobotStatuSearchResponse;
  }
    public synchronized void setRobotStatu(RobotStatuResponseModel keCongRobotStatuSearchResponse) {
    RobotStatuResponseModel oldValue = this.keCongRobotStatuSearchResponse;
    this.keCongRobotStatuSearchResponse = keCongRobotStatuSearchResponse;
     setVehicleOrientationAngle(180- Math.abs (57.35* keCongRobotStatuSearchResponse.getPostiontheta()));
     setVehiclePrecisePosition(new Triple((long)(1000*keCongRobotStatuSearchResponse.getPositionx()),(long)(1000*keCongRobotStatuSearchResponse.getPostiony()),0));
    getPropertyChangeSupport().firePropertyChange(KeCongProcessModel.Attribute.RobotStatu.name(),
                                                  oldValue,
                                                  keCongRobotStatuSearchResponse);}
    
    
    
     @Nonnull
  public synchronized NavigateStatuResponseModel getNavigateStatuResponseModel() {
    return navigateStatuResponseModel;
  }
    public synchronized void setNavigateStatuResponseModel(NavigateStatuResponseModel navigateStatuResponseModel) {
    NavigateStatuResponseModel oldValue = this.navigateStatuResponseModel;
    this.navigateStatuResponseModel = navigateStatuResponseModel;
    getPropertyChangeSupport().firePropertyChange(KeCongProcessModel.Attribute.NavigateStatu.name(),
                                                  oldValue,
                                                  navigateStatuResponseModel);}
    
    @Nonnull
  public synchronized boolean getPeriodicEnable() {
    return periodicenable;
  }
    public synchronized void setPeriodicEnable(boolean periodicenable) {
    boolean oldValue = this.periodicenable;
    this.periodicenable = periodicenable;
    getPropertyChangeSupport().firePropertyChange(KeCongProcessModel.Attribute.Periodic.name(),
                                                  oldValue,
                                                  periodicenable);}
     public synchronized ReadVarModel getReadVarModel() {
    return readVarModel;
  }
    public synchronized void setReadVarModel(ReadVarModel readVarModel) {
    ReadVarModel oldValue = this.readVarModel;
    this.readVarModel = readVarModel;
    getPropertyChangeSupport().firePropertyChange(KeCongProcessModel.Attribute.ReadVarModel.name(),
                                                  oldValue,
                                                  readVarModel);}
    
    
     public synchronized boolean getIsConnected() {
    return IsConnected;
  }
    public synchronized void setIsConnected(boolean isconnected) {
    boolean oldValue = this.IsConnected;
    this.IsConnected = isconnected;
    getPropertyChangeSupport().firePropertyChange(KeCongProcessModel.Attribute.IsConnected.name(),
                                                  oldValue,
                                                  isconnected);}
    
    
    private volatile boolean   isLifting;
    
  public static enum Attribute {   
    VEHICLE_HOST,//车辆主站
    VEHICLE_PORT,//车辆端口
    AUTO_RUN,
    RobotStatu,
    Periodic,
    IsConnected,
    NavigateStatu,
    ReadVarModel,
    SINGLE_STEP_MODE;
  }

  /**
   * @return the isLift
   */
  public boolean isIsLift() {
    return isLifting;
  }

  /**
   * @param isLift the isLift to set
   */
  public void setIsLift(boolean isLift) {
    this.isLifting = isLift;
  }
}

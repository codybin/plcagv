/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.vehicle.comadpter;

import com.xintai.kecong.model.NavigateStatuResponseModel;
import com.xintai.kecong.model.ReadVarModel;
import com.xintai.kecong.model.RobotStatuResponseModel;
import javax.annotation.Nonnull;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;

/**
 *
 * @author Lenovo
 */
public class KeCongProcessModelTO extends  VehicleProcessModelTO{

  private int vehiclePort;
  private String vehicleHost;
  private TCSObjectReference<Vehicle> vehicleRef;
  private boolean autorun;
  private RobotStatuResponseModel keCongRobotStatuSearchResponse;
  private boolean periodicenable;
  private NavigateStatuResponseModel navigateStatuResponseModel;
  private ReadVarModel readVarModel;
  private String unloadOperation;
  private String loadOperation;
  private boolean IsConnected;
  /**
   * Returns the host to connect to.
   *
   * @return The host to connect to
   */
  public String getVehicleHost() {
    return vehicleHost;
  }

  /**
   * Sets the host to connect to.
   *
   * @param vehicleHost The host to connect to
   * @return This
   */
  public KeCongProcessModelTO setVehicleHost(String vehicleHost) {
    this.vehicleHost = vehicleHost;
    return this;
  } public String getLoadOperation() {
    return loadOperation;
  }

  public KeCongProcessModelTO setLoadOperation(String loadOperation) {
    this.loadOperation = loadOperation;
    return this;
  }
    public int getVehiclePort() {
    return vehiclePort;
  }
 public String getUnloadOperation() {
    return unloadOperation;
  }

  public KeCongProcessModelTO setUnloadOperation(String unloadOperation) {
    this.unloadOperation = unloadOperation;
    return this;
  }

  /**
   * Sets the port to connect to.
   *
   * @param vehiclePort The port to connect to
   * @return This
   */
  public KeCongProcessModelTO setVehiclePort(int vehiclePort) {
    this.vehiclePort = vehiclePort;
    return this;
  }
   public TCSObjectReference<Vehicle> getVehicleRef() {
    return vehicleRef;
  }
   public boolean  getAutoRunMark() {
    return autorun;
    
  }
    public KeCongProcessModelTO setAutoRunMark(boolean autorun) {
    this.autorun=autorun;
     return this;
    
  }
  @Nonnull
  public  RobotStatuResponseModel getRobotStatu() {   
    return keCongRobotStatuSearchResponse;
  }
    public KeCongProcessModelTO SetRobotStatu(RobotStatuResponseModel keCongRobotStatuSearchResponse) {
    this.keCongRobotStatuSearchResponse = keCongRobotStatuSearchResponse;
    return this;
    }
  /**
   * Sets the vehicle reference.
   *
   * @param vehicleRef The vehicle reference
   * @return This
   */
  public KeCongProcessModelTO setVehicleRef(TCSObjectReference<Vehicle> vehicleRef) {
    this.vehicleRef = vehicleRef;
    return this;
  }
  public  boolean getPeriodicEnable() {
    return periodicenable;
  }
    public  KeCongProcessModelTO setPeriodicEnable(boolean periodicenable) {
    
    this.periodicenable = periodicenable;
    return  this;
  }
 public  NavigateStatuResponseModel getNavigateStatuResponseModel() {
    return navigateStatuResponseModel;
  }
    public  KeCongProcessModelTO setNavigateStatuResponseModel(NavigateStatuResponseModel navigateStatuResponseModel) {
    this.navigateStatuResponseModel = navigateStatuResponseModel;
    return  this;
   }
      public  ReadVarModel getReadVarModel() {
    return readVarModel;
  }
    public  KeCongProcessModelTO setReadVarModel(ReadVarModel readVarModel) 
    {   
    this.readVarModel = readVarModel;
    return  this;
}
     public  boolean getIsConnected() {
    return IsConnected;
  }
    public KeCongProcessModelTO setIsConnected(boolean isconnected) {
    this.IsConnected = isconnected;
    return this;
  }   
}
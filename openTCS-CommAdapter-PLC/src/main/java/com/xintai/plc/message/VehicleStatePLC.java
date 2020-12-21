/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.message;

/***********************************************************************
 * Module:  VehicleStatePLC.java
 * Author:  Lenovo
 * Purpose: Defines the Class VehicleStatePLC
 ***********************************************************************/


import com.xinta.plc.model.VehicleStateModel;
import com.xintai.data.util.ByteQueue;
import com.xintai.data.util.DataConvertUtl;

public class VehicleStatePLC {

  private  VehicleStateModel vehicleStateModel;
 private final   ByteQueue byteQueue;
   public VehicleStatePLC(byte [] data)
   {
   byteQueue=new ByteQueue(data);  
   vehicleStateModel=new VehicleStateModel();
   decode();
   }
   private int getint()
   {
return byteQueue._popU2B();
   }
   private float getfloatdate()
   {
     float f=0f;
     byte[]buf=new byte[4];
     byteQueue.pop(buf);
     f=DataConvertUtl.getFloat(buf);
   return  f;
   }
   
   public VehicleStateModel GetVehicleStateModel()
   {
   return  vehicleStateModel;
   
   }
   private void decode() {
      // TODO: implement
     vehicleStateModel.setChargingSwitch(getint());
     vehicleStateModel.setBatteryPower(getfloatdate());
     vehicleStateModel.setBatteryVoltage(getfloatdate());
     vehicleStateModel.setBatteryCurrent(getfloatdate());
     vehicleStateModel.setBatteryTemprature(getfloatdate());
     vehicleStateModel.setAgvNumber(getint());
     vehicleStateModel.setIP1(getint());
     vehicleStateModel.setIP2(getint());
     vehicleStateModel.setIP3(getint());
     vehicleStateModel.setIP4(getint());
   vehicleStateModel.setMasterScheduling(getint());
   vehicleStateModel.setAgvVspeed(getfloatdate());
   vehicleStateModel.setAgvAspeed(getfloatdate());
   vehicleStateModel.setTotalMileage(getfloatdate());
   vehicleStateModel.setRunTime(getfloatdate());
   vehicleStateModel.setAgvRunState(getint());
   vehicleStateModel.setErrorErrorCode(getint());
   vehicleStateModel.setWarningErrorCode(getint());
   vehicleStateModel.setLastSite(getint());
   vehicleStateModel.setCurrentSite(getint());
   vehicleStateModel.setNextSite(getint());
   vehicleStateModel.setNextTwoSite(getint());
   vehicleStateModel.setTargetSite(getint());
   vehicleStateModel.setTargetSiteCarDirection(getint());
   vehicleStateModel.setPositioningState(getint());
   vehicleStateModel.setBetweenSiteMileage(getfloatdate());
   vehicleStateModel.setNavigationalState(getint());
   vehicleStateModel.setCurrentSiteCarDirection(getint());
   vehicleStateModel.setCurrentSchedulingTask(getint());
   vehicleStateModel.setMaterialStatus(getint());
   vehicleStateModel.setTaskStatus(getint());
   }
}
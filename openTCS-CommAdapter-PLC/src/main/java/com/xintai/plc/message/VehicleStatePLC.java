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


import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.locator.NumericLocator;
import com.xinta.plc.model.VehicleStateModel;
import com.xinta.plc.model.VehicleTaskState;
import com.xintai.data.util.ByteQueue;
import com.xintai.data.util.DataConvertUtl;
import com.xintai.messageserviceinterface.DispacherTaskState;
import com.xintai.messageserviceinterface.MaterialState;
import com.xintai.messageserviceinterface.PLCTaskState;

public class VehicleStatePLC {

  private  VehicleStateModel vehicleStateModel;
 private final   ByteQueue byteQueue;
   public VehicleStatePLC(byte [] data)
   {
   byteQueue=new ByteQueue(data);  
   vehicleStateModel=new VehicleStateModel();
   vehicleTaskState=new VehicleTaskState();
   decode();
   }
   private int getint()
   {
return byteQueue._popU2B();
   }
   private float getfloatdate()
   {
     byte[]data=new byte[4];
     byteQueue.pop(data);
     return Float.intBitsToFloat(((data[2] & 0xff) << 24) | ((data[3] & 0xff) << 16)
                    | ((data[0] & 0xff) << 8) | (data[1] & 0xff));
   }
  private final  VehicleTaskState vehicleTaskState;
   public  VehicleTaskState getVehicleTaskState()
   {
   return  vehicleTaskState;
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
   vehicleTaskState.setDispacherTaskState(DispacherTaskState.intToEnum(vehicleStateModel.getCurrentSchedulingTask()));
   vehicleTaskState.setMaterialState(MaterialState.intToEnum(vehicleStateModel.getMaterialStatus()));
  vehicleTaskState.setpLCTaskState(PLCTaskState.intToEnum(vehicleStateModel.getTaskStatus()));
   }
}
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
     vehicleStateModel.setPostionx(getfloatdate());
     vehicleStateModel.setPositiony(getfloatdate());
     vehicleStateModel.setPositionangle(getfloatdate());
     vehicleStateModel.setCurrentnavigationstation(getint());
     vehicleStateModel.setPostionstate(getint());
    vehicleStateModel.setNavigatestate(getint());
    vehicleStateModel.setNavigatetype(getint());
    vehicleStateModel.setPostionlevel(getfloatdate());
   vehicleStateModel.setBatterypower(getint());
   vehicleStateModel.setBatterytemprature(getfloatdate());
   vehicleStateModel.setBatterycurrent(getfloatdate());
   vehicleStateModel.setBatteryvoltage(getfloatdate());
   vehicleStateModel.setKilometerintotal(getfloatdate());
   vehicleStateModel.setTimeintotal(getfloatdate());
   vehicleStateModel.setCurrentposition(getint());
   vehicleStateModel.setMapname(getint());
   vehicleStateModel.setDispaterstate(getint());
   vehicleStateModel.setKilometertoday(getint());
   vehicleStateModel.setLoadstate(getint());
   }
}
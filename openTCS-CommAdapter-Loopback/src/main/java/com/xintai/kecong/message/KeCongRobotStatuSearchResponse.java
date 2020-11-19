/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.message;

import com.xintai.kecong.model.RobotStatuResponseModel;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Lenovo
 */
public class KeCongRobotStatuSearchResponse  extends KeCongCommandResponse{

  /**
   * @return the robotStatuResponseModel
   */
  public RobotStatuResponseModel getRobotStatuResponseModel() {
    return robotStatuResponseModel;
  }
  private double bodytemprature=0;

  public double getBodytemprature() {
    return bodytemprature;
  }

  public double getPositionx() {
    return positionx;
  }

  public double getPostiony() {
    return postiony;
  }

  public double getPostiontheta() {
    return postiontheta;
  }

  public double getBatterypower() {
    return batterypower;
  }

  public byte getIsblokingornot() {
    return isblokingornot;
  }

  public byte getIschargingornot() {
    return ischargingornot;
  }

  public byte getRunmode() {
    return runmode;
  }

  public byte getMaploadstate() {
    return maploadstate;
  }

  public int getCurrenttargetid() {
    return currenttargetid;
  }

  public double getForwordspeed() {
    return forwordspeed;
  }

  public double getCurvespeed() {
    return curvespeed;
  }

  public double getBatteryvoltage() {
    return batteryvoltage;
  }

  public double getCurrent() {
    return current;
  }

  public byte getCurrenttaskstatue() {
    return currenttaskstatue;
  }

  public byte getResrved() {
    return resrved;
  }

  public int getMapversion() {
    return mapversion;
  }

  public byte[] getReserved() {
    return reserved;
  }

  public double getTotalkilometer() {
    return totalkilometer;
  }

  public double getCurrentrunningtime() {
    return currentrunningtime;
  }

  public double getTotalrunningtime() {
    return totalrunningtime;
  }

  public byte getRobotpositionstatue() {
    return robotpositionstatue;
  }

  public byte[] getReserved1() {
    return reserved1;
  }

  public int getMapnumber() {
    return mapnumber;
  }

  public byte[] getCurrentmapname() {
    return currentmapname;
  }

  public float getConfidenceinterval() {
    return confidenceinterval;
  }

  public byte[] getReserved2() {
    return reserved2;
  }
  private double positionx;
  private double postiony;
  private double postiontheta;
  private double batterypower;
  private byte isblokingornot;
  private byte ischargingornot;
  private byte runmode;
  private byte maploadstate;
  private int currenttargetid;
  private double forwordspeed;
  private double curvespeed;
  private double batteryvoltage;
  private double current;
  private byte currenttaskstatue;
  private byte resrved=0;
  private int mapversion;
  private byte reserved[]={0,0,0,0};
  private double totalkilometer;
  private double currentrunningtime;
  private double totalrunningtime;
  private byte robotpositionstatue;
  private byte reserved1[]={0,0,0};
  private int mapnumber;
  private byte currentmapname[]=new byte[64];
  private float confidenceinterval;
  private byte reserved2[]={0,0,0,0};
private final   RobotStatuResponseModel robotStatuResponseModel;
  public KeCongRobotStatuSearchResponse(ByteQueue bytequeque) {
    super(bytequeque);
    robotStatuResponseModel=new RobotStatuResponseModel();
 // if(value.length==192)
    {
      robotStatuResponseModel.setBodytemprature( DataConvertUtl.bytes2Double(value, 0));
      robotStatuResponseModel.setPositionx( DataConvertUtl.bytes2Double(value, 8));
      robotStatuResponseModel.setPostiony(DataConvertUtl.bytes2Double(value, 16));
      robotStatuResponseModel.setPostiontheta(DataConvertUtl.bytes2Double(value, 24));
      robotStatuResponseModel.setBatterypower( DataConvertUtl.bytes2Double(value, 32));
      robotStatuResponseModel.setIsblokingornot(value[40]);
      robotStatuResponseModel.setIschargingornot(value[41]);
      robotStatuResponseModel.setRunmode(value[42]);
      robotStatuResponseModel.setMaploadstate(value[43]);
       robotStatuResponseModel.setMapnumber(DataConvertUtl.getInt(value,116));
      robotStatuResponseModel.setCurrenttargetid(DataConvertUtl.getInt(value,44));
      robotStatuResponseModel.setForwordspeed( DataConvertUtl.bytes2Double(value,48 ));
      robotStatuResponseModel.setCurvespeed(DataConvertUtl.bytes2Double(value, 56));
      robotStatuResponseModel.setBatteryvoltage(DataConvertUtl.bytes2Double(value,64));
      robotStatuResponseModel.setCurrent( DataConvertUtl.bytes2Double(value,72));
      robotStatuResponseModel.setCurrenttaskstatue(value[80]);
      robotStatuResponseModel.setRobotpositionstatue(value[112]);
      robotStatuResponseModel.setMapversion((byte)(value[82]&0xFF)|(byte)((value[83]&0xFF)<<8));
      robotStatuResponseModel.setTotalkilometer(DataConvertUtl.bytes2Double(value,88 ));
      robotStatuResponseModel.setCurrentrunningtime(DataConvertUtl.bytes2Double(value, 96));
      robotStatuResponseModel.setTotalrunningtime(DataConvertUtl.bytes2Double(value, 104));
     byte[] var=new byte[64];
    System.arraycopy(value, 120, var, 0, 64);
    robotStatuResponseModel.setCurrentmapname(var);
  /*   bodytemprature=  DataConvertUtl.bytes2Double(value, 0);
  positionx= DataConvertUtl.bytes2Double(value, 8);
  postiony= DataConvertUtl.bytes2Double(value, 16);
  postiontheta= DataConvertUtl.bytes2Double(value, 24);
  batterypower= DataConvertUtl.bytes2Double(value, 32);
  isblokingornot=value[40];
  ischargingornot=value[41];
  runmode=value[42];
  maploadstate=value[43];
  currenttargetid= DataConvertUtl.byteArrayToInt(value,44);
  forwordspeed= DataConvertUtl.bytes2Double(value,48 );
  curvespeed= DataConvertUtl.bytes2Double(value, 56);
  batteryvoltage= DataConvertUtl.bytes2Double(value, 64);
  current= DataConvertUtl.bytes2Double(value,72);
  currenttaskstatue=value[80];
  mapversion=(byte)(value[82]&0xFF)|(byte)((value[83]&0XFF)<<8);
  robotpositionstatue=value[112];
  totalkilometer= DataConvertUtl.bytes2Double(value,88 );
  currentrunningtime= DataConvertUtl.bytes2Double(value, 96);
  totalrunningtime= DataConvertUtl.bytes2Double(value, 104);*/
    
    }
  }
  
  
}

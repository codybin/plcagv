/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 package com.xintai.kecong.message;

/**
 *
 * @author Lenovo
 */

  public class KeCongReturnCode
    {   public  static final int Sucees=0x00,
      UnKnown=0x01,
      SeviceCodeErro=0x02,
      ComandCodeErro=0x03,
      MessageHeadErro=0x04,
      CantExcuteCauseInAutoMode=0x80,
      UpdatingMap=0x81,
      NotFinishedPosition=0x82,
      PointOverLimitation=0x83,
      PathNotAllowedOverLimitation=0x84,
      ForbiddenRegionOverLimitaion=0x85,
      CantExcuteCauseInManulMode=0x86,
      UnLoadMap=0x87,
      TargetPositionNonExisit=0x88,
      TargetPositionNotOnPath=0x89,
      PathPlateErroCauseHasNoPathCanArrive=0x8A,
      RobotNotOnThePathCantExcuteNavigatTask=0x8B,
      CantReturnHomeCauseRobotInNoiseshieldingarea=0x8C;
    }


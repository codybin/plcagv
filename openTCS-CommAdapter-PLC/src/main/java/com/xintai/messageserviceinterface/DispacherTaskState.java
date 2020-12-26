/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.messageserviceinterface;
/**
 *
 * @author Lenovo
 */
public enum DispacherTaskState {
  NO_TASK,
  LoadFull_TASK,
  UNLoadFull_TASK,
  LoadEmpty_TASK,
  UNLoadEmpty_TASK,
  Charging_TASK,
  Parking_TASK;
    public static DispacherTaskState intToEnum(int value) {    //将数值转换成枚举值
            switch (value) {
            case 0:
                return NO_TASK;
            case 1:
                return LoadFull_TASK;
            case 2:
                return UNLoadFull_TASK;
            case 3:
                return LoadEmpty_TASK;
            case 4:
                return UNLoadEmpty_TASK;
            default :
                return null;
            }
        }
  
}

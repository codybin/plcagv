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
public enum PLCTaskState {
  UNFinsh,Finsh;
     public static PLCTaskState intToEnum(int value) {    //将数值转换成枚举值
            switch (value) {
            case 0:
                return UNFinsh;
            case 1:
           return Finsh;
            default :
                return null;
            }
        }
  
}

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
public enum MaterialState {
  unload,loadfull,loadempy;
    public static MaterialState intToEnum(int value) {    //将数值转换成枚举值
            switch (value) {
            case 0:
                return unload;
            case 1:
                return loadfull;
            case 2:
                return loadempy;
            default :
                return null;
            }
        }
}

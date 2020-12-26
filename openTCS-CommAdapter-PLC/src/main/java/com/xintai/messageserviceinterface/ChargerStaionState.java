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
public enum ChargerStaionState {
  No,
  ChargerTail_out,
  ChargerTail_inside;
  public static ChargerStaionState intToEnum(int num)
  {
   switch (num) {
            case 0:
                return No;
            case 1:
                return ChargerTail_out;
            case 2:
                return ChargerTail_inside;
            default :
                return null;
   }
  }
  
}

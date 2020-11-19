/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.message;

import java.io.Serializable;

/**
 *
 * @author Lenovo
 */
public abstract class KeCongMessage  implements Serializable{
 
  
  protected final static int [] LincenceCode = {0xEF,0XF3,0X0A,0XFD,0X7E,0X2D,0X43,0X41,0X86,0XFF,0X57,0X68,0X1F,0XFD,0X48,0X9D };
   protected final static byte protocolcode = 0x01;
   protected byte diagramType = 0;
   protected static int communictaionSq = 0;

public static int getCommunictaionSq() {
    return communictaionSq;
  }
   protected  final static byte seviceCode = 0x10;
   protected byte comandCode = 0;
   protected byte returnCode = 0;
   protected final static byte reserved = 0;
   protected short dataLength = 0;
   protected final static byte[] reserved1 = {0,0};
   protected byte[] dataValue;
 
}

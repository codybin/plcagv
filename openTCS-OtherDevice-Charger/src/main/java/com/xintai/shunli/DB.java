/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.shunli;

import com.github.s7connector.api.annotation.S7Variable;
import com.github.s7connector.impl.utils.S7Type;
import java.util.Date;

/**
 *
 * @author Lenovo
 */
public  class DB {
  @S7Variable(type=S7Type.BYTE, byteOffset=0)
  public byte d;
  @S7Variable(type=S7Type.BYTE, byteOffset=1)
  public byte d1;
  @S7Variable(type=S7Type.BYTE, byteOffset=2)
  public byte d2;
  @S7Variable(type=S7Type.BYTE, byteOffset=3)
  public byte d3;
  
  /*@S7Variable(type=S7Type.REAL, byteOffset=0)
  public double d;
  
  @S7Variable(type=S7Type.STRING, byteOffset=4, size=20)
  public String str;
  
  @S7Variable(type=S7Type.BOOL, byteOffset=30, bitOffset=0)
  public boolean b1;
  
  @S7Variable(type=S7Type.BOOL, byteOffset=30, bitOffset=1)
  public boolean b2;
  
  @S7Variable(type=S7Type.BOOL, byteOffset=30, bitOffset=2)
  public boolean b3;
  
  @S7Variable(type=S7Type.BYTE, byteOffset=31)
  public byte by1;
  
  @S7Variable(type=S7Type.DATE_AND_TIME, byteOffset=32)
  public Date date1;
  
  @S7Variable(type=S7Type.TIME, byteOffset=40)
  public long millis;
  
  @S7Variable(type=S7Type.DATE, byteOffset=44)
  public Date date2;*/

  @Override
  public String toString() {
    return "DB{" + "d=" + d + ", d1=" + d1 + ", d2=" + d2 + ", d3=" + d3 + '}';
  }
}

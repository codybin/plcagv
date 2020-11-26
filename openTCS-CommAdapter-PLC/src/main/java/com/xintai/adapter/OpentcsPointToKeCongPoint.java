/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.adapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Lenovo
 */
public class OpentcsPointToKeCongPoint{
  
  private final String pointString;

  public OpentcsPointToKeCongPoint(String pointString)
  {
  this.pointString=pointString;
  
   
  }
  public  int getIntPoint()
  {
String regEx="[^0-9]";  
Pattern p = Pattern.compile(regEx);  
Matcher m = p.matcher(pointString);  
String resultString=  m.replaceAll("").trim();
return  Integer.parseInt(resultString);
   }
}

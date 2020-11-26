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
public class ErroRespone extends KeCongCommandResponse{
  
  public ErroRespone(ByteQueue bytequeque) {
    super(bytequeque);
  }
 public  int GetRurnCode()
 {
 return returnCode;
 
 }
 public int GetFunctionCode()
     {
     return comandCode;
     
     }
}

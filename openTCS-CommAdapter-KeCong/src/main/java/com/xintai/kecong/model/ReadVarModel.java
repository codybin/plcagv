/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.model;

import java.io.Serializable;

/**
 *
 * @author Lenovo
 */
public class ReadVarModel  implements  Serializable{

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the value
   */
  public byte[] getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(byte[] value) {
    this.value = value;
  }
  private String name;
  private byte [] value;
  
}

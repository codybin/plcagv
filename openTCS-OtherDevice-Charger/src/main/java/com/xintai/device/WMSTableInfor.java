/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.device;

/**
 *
 * @author Lenovo
 */
public class WMSTableInfor {
  private String ordertype;
  private String wmsstaion;

  public WMSTableInfor(String ordertype, String wmsstaion) {
    this.ordertype = ordertype;
    this.wmsstaion = wmsstaion;
  }

  public String getOrdertype() {
    return ordertype;
  }

  public void setOrdertype(String ordertype) {
    this.ordertype = ordertype;
  }

  public String getWmsstaion() {
    return wmsstaion;
  }

  public void setWmsstaion(String wmsstaion) {
    this.wmsstaion = wmsstaion;
  }
  
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.erp;

public class OrderInfor  
{

  public OrderInfor(String ordername, String ordertype, String isdenpendent) {
    this.ordername = ordername;
    this.ordertype = ordertype;
    this.isdenpendent = isdenpendent;
  }
    private String ordername;

    private String ordertype;

    private String isdenpendent;

    public void setOrdername(String ordername){
        this.ordername = ordername;
    }
    public String getOrdername(){
        return this.ordername;
    }
    public void setOrdertype(String ordertype){
        this.ordertype = ordertype;
    }
    public String getOrdertype(){
        return this.ordertype;
    }
    public void setIsdenpendent(String isdenpendent){
        this.isdenpendent = isdenpendent;
    }
    public String getIsdenpendent(){
        return this.isdenpendent;
    }
}

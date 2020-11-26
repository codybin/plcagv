/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.informatiomn;

/**
 *添加测试类，后续如果用http方式获得信息，可以通过这样的方式
 * @author Lenovo
 */
public class SinceTechInformation {
public SinceTechInformation(String depart,String contactString,String dataString)
{ this.contact=contact;
this.datatime=dataString;
this.departname=depart;
}
  /**
   * @return the departname
   */
  public String getDepartname() {
    return departname;
  }

  /**
   * @param departname the departname to set
   */
  public void setDepartname(String departname) {
    this.departname = departname;
  }

  /**
   * @return the contact
   */
  public String getContact() {
    return contact;
  }

  /**
   * @param contact the contact to set
   */
  public void setContact(String contact) {
    this.contact = contact;
  }

  /**
   * @return the datatime
   */
  public String getDatatime() {
    return datatime;
  }

  /**
   * @param datatime the datatime to set
   */
  public void setDatatime(String datatime) {
    this.datatime = datatime;
  }
  private String departname;
  private String contact;
  private String datatime;
}

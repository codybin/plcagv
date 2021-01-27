/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mybatis.po;

/**
 *
 * @author Lenovo
 */

public class Employees {

  public Employees(Integer id, String first_name, String last_name, String email_address) {
    this.id = id;
    this.first_name = first_name;
    this.last_name = last_name;
    this.email_address = email_address;
  }

  @Override
  public String toString() {
    return "Employees{" + "id=" + id + ", first_name=" + first_name + ", last_name=" + last_name + ", email_address=" + email_address + '}';
  }

  
  
  private Integer id;
 
  private String first_name;
 
  private String last_name;

  private String email_address;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getFirst_name() {
    return first_name;
  }

  public void setFirst_name(String first_name) {
    this.first_name = first_name;
  }

  public String getLast_name() {
    return last_name;
  }

  public void setLast_name(String last_name) {
    this.last_name = last_name;
  }

  public String getEmail_address() {
    return email_address;
  }

  public void setEmail_address(String email_address) {
    this.email_address = email_address;
  }
}

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
public class IPParameter {

  public IPParameter(String ip, int port, int slaveid) {
    this.ip = ip;
    this.port = port;
    this.slaveid = slaveid;
  }
  private String ip;
  private int port;

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getSlaveid() {
    return slaveid;
  }

  public void setSlaveid(int slaveid) {
    this.slaveid = slaveid;
  }
  private  int slaveid;
}

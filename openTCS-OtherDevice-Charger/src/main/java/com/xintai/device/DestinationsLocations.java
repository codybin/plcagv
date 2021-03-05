/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.device;

import java.util.List;

/**
 *
 * @author Lenovo
 */
public class DestinationsLocations {

  @Override
  public String toString() {
    return "DestinationsLocations{" + "WMSStaion=" + WMSStaion + ", OrderType=" + OrderType + ", ID=" + ID + ", destinations=" + destinations + '}';
  }

 
  private String WMSStaion;

  public String getWMSStaion() {
    return WMSStaion;
  }

  public void setWMSStaion(String WMSStaion) {
    this.WMSStaion = WMSStaion;
  }
  private String OrderType;
  private int ID;
  private Destinations destinations;

  public Destinations getDestinations() {
    return destinations;
  }

  public void setDestinations(Destinations destinations) {
    this.destinations = destinations;
  }
  public int getID() {
    return ID;
  }

  public void setID(int ID) {
    this.ID = ID;
  }
  public String getOrderType() {
    return OrderType;
  }

  public void setOrderType(String OrderType) {
    this.OrderType = OrderType;
  }
  public List<Destination> MergerDestionation(Destinations dstn)
  {
    destinations.getDestinations().addAll(dstn.getDestinations());
  return destinations.getDestinations();
  }
}

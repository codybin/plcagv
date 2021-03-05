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
public class Destinations {
  private List<Destination> destinations;

  @Override
  public String toString() {
    return "Destinations{" + "destinations=" + destinations + '}';
  }
 
  public List<Destination> getDestinations() {
    return destinations;
  }

  public void setDestinations(List<Destination> destinations) {
    this.destinations = destinations;
  }
}

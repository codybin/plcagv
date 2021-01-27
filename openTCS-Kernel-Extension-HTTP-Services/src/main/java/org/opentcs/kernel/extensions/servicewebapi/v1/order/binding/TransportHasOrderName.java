/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opentcs.kernel.extensions.servicewebapi.v1.order.binding;

import java.io.Serializable;

/**
 *
 * @author Lenovo
 */
public class TransportHasOrderName implements Serializable{

  public Transport getTransport() {
    return transport;
  }

  public void setTransport(Transport transport) {
    this.transport = transport;
  }

  public String getTransportname() {
    return transportname;
  }

  public void setTransportname(String transportname) {
    this.transportname = transportname;
  }
private  Transport transport;
private  String transportname;
}

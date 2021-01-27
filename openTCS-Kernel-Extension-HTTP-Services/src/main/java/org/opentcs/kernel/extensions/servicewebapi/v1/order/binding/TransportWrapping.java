/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opentcs.kernel.extensions.servicewebapi.v1.order.binding;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class TransportWrapping implements Serializable{
  private  List<TransportHasOrderName> lsttransport;
  private String odersquencenameString;

  public String getOdersquencenameString() {
    return odersquencenameString;
  }

  public void setOdersquencenameString(String odersquencenameString) {
    this.odersquencenameString = odersquencenameString;
  }

  public List<TransportHasOrderName> getLsttransport() {
    return lsttransport;
  }
  public void setLsttransport(List<TransportHasOrderName> lsttransport) {
    this.lsttransport = lsttransport;
  }
}

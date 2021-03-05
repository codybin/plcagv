/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.interaction.erp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.xintai.WMSTaskTable;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Size;

/**
 *
 * @author Lenovo
 */
public class WMSTaskTables {
  
  @JsonPropertyDescription("The wMSTaskTables")
  @JsonProperty(required = true)
  @Valid
  @Size(min = 1)
  private List<WMSTaskTable> wMSTaskTables;

  @Override
  public String toString() {
    return "WMSTaskTables{" + "wMSTaskTables=" + wMSTaskTables + '}';
  }

  public List<WMSTaskTable> getwMSTaskTables() {
    return wMSTaskTables;
  }
  public void setwMSTaskTables(List<WMSTaskTable> wMSTaskTables) {
    this.wMSTaskTables = wMSTaskTables;
  }
}

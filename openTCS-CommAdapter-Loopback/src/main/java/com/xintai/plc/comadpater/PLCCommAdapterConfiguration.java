/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.comadpater;

/**
 *
 * @author Lenovo
 */
import org.opentcs.configuration.ConfigurationEntry;
import org.opentcs.configuration.ConfigurationPrefix;



@ConfigurationPrefix(PLCCommAdapterConfiguration.PREFIX)
    
public interface PLCCommAdapterConfiguration {

  /**
   * This configuration's prefix.
   */
  String PREFIX = "PLC.commadapter";

  @ConfigurationEntry(
      type = "Boolean",
      description = "Whether to register/enable the example communication adapter.",
      orderKey = "0_enable")
  boolean enable();

}


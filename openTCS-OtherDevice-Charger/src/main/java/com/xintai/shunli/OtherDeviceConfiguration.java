/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.shunli;

/**
 *
 * @author Lenovo
 */
import org.opentcs.configuration.ConfigurationEntry;
import org.opentcs.configuration.ConfigurationPrefix;



@ConfigurationPrefix(OtherDeviceConfiguration.PREFIX)
    
public interface OtherDeviceConfiguration {

  /**
   * This configuration's prefix.
   */
  String PREFIX = "otherdevice.shunli_plc";

  @ConfigurationEntry(
      type = "Boolean",
      description = "Whether to register/enable the example communication adapter.",
      orderKey = "0_enable")
  boolean enable();
   @ConfigurationEntry(
      type = "Integer",
      description = "the port to communication.",
      orderKey = "1")
   int port();
   @ConfigurationEntry(
      type = "String",
      description = "the port to communication.",
      orderKey = "2")
    String  host();
}


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

  @ConfigurationEntry(
      type = "Integer",
      description = "StateOffSetAddr",
      orderKey = "1")
  int stateoffset();
    @ConfigurationEntry(
      type = "Integer",
      description = "StateLength",
      orderKey = "2")
  int statelength();
   @ConfigurationEntry(
      type = "Integer",
      description = "NavigateOffSet",
      orderKey = "3")
  int navigateoffset();
    @ConfigurationEntry(
      type = "Integer",
      description = "SettingVarOffset",
      orderKey = "4")
  int settingoffset();
     @ConfigurationEntry(
      type = "Integer",
      description = "HeartBeatOffset",
      orderKey = "5")
  int heartbeat();
    @ConfigurationEntry(
      type = "Boolean",
      description = "Whether to register/enable report postion to erp.",
      orderKey = "6")
  boolean noticeposition_enable();
   @ConfigurationEntry(
      type = "String",
      description = "the port to report postion",
      orderKey = "7")
  String reportpostionurl_url();
   @ConfigurationEntry(
      type = "Boolean",
      description = "Whether to stop vehicle before five or more points.",
      orderKey = "8")
  boolean stopvehicle_enable();
}


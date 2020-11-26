/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.redis.statistic;

import org.opentcs.configuration.ConfigurationEntry;
import org.opentcs.configuration.ConfigurationPrefix;

/**
 * Configuration entries for the statistics collector.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
@ConfigurationPrefix(RedisCollectorConfiguration.PREFIX)
public interface RedisCollectorConfiguration {

  String PREFIX = "rediscollector";

  @ConfigurationEntry(
      type = "Boolean",
      description = "Whether to enable the statistics collector.",
      orderKey = "0")
  boolean enable();
  
  @ConfigurationEntry(
      type = "IP address",
      description = "Address to which to bind the HTTP server, e.g. 0.0.0.0 or 127.0.0.1.",
      orderKey = "1")
  String bindAddress();

  @ConfigurationEntry(
      type = "Integer",
      description = "Port to which to bind the HTTP server.",
      orderKey = "2")
  int bindPort();

  @ConfigurationEntry(
      type = "String",
      description = "Key allowing access to the API.",
      orderKey = "3")
  String accessKey();

  @ConfigurationEntry(
      type = "Integer",
      description = "Maximum number of status events to be kept.",
      orderKey = "4")
  int statusEventsCapacity();

  @ConfigurationEntry(
      type = "Boolean",
      description = "Whether to use SSL to encrypt connections.",
      orderKey = "5")
  boolean useSsl();
}

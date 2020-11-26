/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.redis;

/**
 *
 * @author Lenovo
 */
/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */


import com.xintai.redis.statistic.RedisCollectorConfiguration;
import com.xintai.redis.statistic.RedisStaticCollector;
import javax.inject.Singleton;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures the service web API extension.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class RedisInjectModule
    extends KernelInjectionModule {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(RedisInjectModule.class);

  @Override
  protected void configure() {
    RedisCollectorConfiguration configuration
        = getConfigBindingProvider().get(RedisCollectorConfiguration.PREFIX,
                                         RedisCollectorConfiguration.class);

    if (!configuration.enable()) {
      LOG.info("RedisCollectorConfiguration disabled by configuration.");
      return;
    }

    bind(RedisCollectorConfiguration.class)
        .toInstance(configuration);

    extensionsBinderOperating().addBinding()
        .to(RedisStaticCollector.class)
        .in(Singleton.class);
  }
}

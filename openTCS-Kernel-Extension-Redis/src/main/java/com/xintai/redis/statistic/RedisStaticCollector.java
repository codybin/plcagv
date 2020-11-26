/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.redis.statistic;

/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
import static java.util.Objects.requireNonNull;
import javax.inject.Inject;
import org.opentcs.components.kernel.KernelExtension;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.util.event.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Collects data from kernel events and logs interesting events to a file that
 * can later be processed for statistical purposes.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class RedisStaticCollector
    implements KernelExtension {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(RedisStaticCollector.class);
  /**
   * Where we register for application events.
   */
  private final EventSource eventSource;

  private boolean initialized;
  /**
   * An event logger for persisting the event data collected.
   */
  private RedisStacticEventLogger statisticsLogger;
  private final RedisCollectorConfiguration redisCollectorConfiguration;
  private final Subscriber subscribe;

  /**
   * Creates a new instance.
   *
   * @param eventSource Where this instance registers for application events.
   * @param redisCollectorConfiguration
   */
  @Inject
  public RedisStaticCollector(@ApplicationEventBus EventSource eventSource,
                  RedisCollectorConfiguration redisCollectorConfiguration ,
                  Subscriber subscriber) {
    this.eventSource = requireNonNull(eventSource, "eventSource");
   this.redisCollectorConfiguration= requireNonNull(redisCollectorConfiguration, "redisCollectorConfiguration");
   this.subscribe=subscriber;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }
      //   String redisIp = "192.168.229.154";
        //int reidsPort = 6379;
    JedisPool jedisPool = new JedisPool(new JedisPoolConfig(),redisCollectorConfiguration.bindAddress(),redisCollectorConfiguration.bindPort());
    Jedis  jedis=jedisPool.getResource(); 
    
    SubThread subThread=new SubThread(jedisPool,subscribe);
    subThread.start();
    statisticsLogger = new RedisStacticEventLogger(jedis);
    statisticsLogger.initialize();
    eventSource.subscribe(statisticsLogger);

    // Remember we're plugged in.
    initialized = true;
  }

  @Override
  public void terminate() {
    if (!isInitialized()) {
      return;
    }
    // Unregister event listener, terminate event processing.
    eventSource.unsubscribe(statisticsLogger);
    statisticsLogger.terminate();

    statisticsLogger = null;
    initialized = false;
  }
}

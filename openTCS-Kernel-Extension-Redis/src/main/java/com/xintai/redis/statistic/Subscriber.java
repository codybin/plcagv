/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.redis.statistic;

import com.google.inject.Inject;
import com.xintai.redis.rediseventhanderl.RedisRequestHandler;
import redis.clients.jedis.JedisPubSub;

/**
 *
 * @author Lenovo
 */
public class Subscriber  extends JedisPubSub {

  private final RedisRequestHandler redisRequestHandler;
  @Inject
public Subscriber(RedisRequestHandler redisRequestHandler)
{
  this.redisRequestHandler=redisRequestHandler;
}
  @Override
  public void onUnsubscribe(String channel, int subscribedChannels) {
  System.out.println(String.format("unsubscribe redis channel, channel %s, subscribedChannels %d", 
                channel, subscribedChannels));
  }

  @Override
  public void onSubscribe(String channel, int subscribedChannels) {
   System.out.println(String.format("subscribe redis channel success, channel %s, subscribedChannels %d", 
                channel, subscribedChannels));
  }

  @Override
  public void onMessage(String channel, String message) {
  System.out.println(String.format("receive redis published message, channel %s, message %s", channel, message));
  switch(channel)
  {
  case "1":
    //redisRequestHandler.handlePostTransportOrder(request, response);
    break;
  case "2":
    break;
    default:
      break;
  }
  
  
  }
  
}

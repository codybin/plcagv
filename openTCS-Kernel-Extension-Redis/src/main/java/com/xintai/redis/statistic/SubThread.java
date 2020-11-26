/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.redis.statistic;

/**
 *
 * @author Lenovo
 */
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class SubThread extends Thread {
    private final JedisPool jedisPool;
    private final Subscriber subscriber;

    private final String channel = "mychannel";

    public SubThread(JedisPool jedisPool, Subscriber subscriber) {
        super("SubThread");
        this.jedisPool = jedisPool;
        this.subscriber=subscriber;
    }

    @Override
    public void run() {
        System.out.println(String.format("subscribe redis, channel %s, thread will be blocked", channel));
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.subscribe(subscriber, channel);
        } catch (Exception e) {
            System.out.println(String.format("subsrcibe channel error, %s", e));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
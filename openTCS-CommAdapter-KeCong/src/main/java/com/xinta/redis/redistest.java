/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xinta.redis;
import java.util.Iterator;
import java.util.Set;
import  redis.clients.jedis.Jedis;
/**
 *
 * @author Lenovo
 */
public class redistest {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    Jedis jedis=new Jedis();
   jedis.flushDB();

   jedis.set("transport", "hi,i am here");
    Set<String> setstr= jedis.keys("*");
    System.out.println(jedis.ping());
    for (Iterator<String> iterator = setstr.iterator(); iterator.hasNext();) {
      String next = iterator.next();
      System.out.println(jedis.get(next));
   }
    
  }
  
}

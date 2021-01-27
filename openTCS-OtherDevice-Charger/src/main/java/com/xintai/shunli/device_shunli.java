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
public interface  device_shunli {
  void init();
  void writedata(Object bean, int dbNum, int byteOffset,int tries);
  <T> T readdata(Class<T> beanClass, int dbNum, int byteOffset,int tries);
  void close();
}

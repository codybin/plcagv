/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.message;

import com.serotonin.util.queue.ByteQueue;

/**
 *
 * @author Lenovo
 */
public class NavigateControl {
   private final int operation;
   private final int pathid;
   private  ByteQueue byteQueue;
   public NavigateControl(int operation,int pathid)
   {
   this.operation=operation;
   this.pathid=pathid;
   }
  public byte [] encodedata()
  {
    byteQueue=new ByteQueue();
    byteQueue.push(operation);
    byteQueue.push(pathid);
 return byteQueue.popAll();
  }
}

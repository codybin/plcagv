/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.statemachine;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.CharSetUtils;
import org.opentcs.common.LoggingScheduledThreadPoolExecutor;
import org.opentcs.util.logging.UncaughtExceptionLogger;

/**
 *
 * @author Lenovo
 */
public class NewMain1 {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    ScheduledExecutorService executor
        = new LoggingScheduledThreadPoolExecutor(
            1,
            (runnable) -> {
              Thread thread = new Thread(runnable, "kernelExecutor");
              thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(false));
              return thread;
            }
        );
    boolean mark=true;
     int count=0;
     
    class nnn implements Runnable
    {
private int count=0;

      public int getCount() {
        return count;
      }
      public nnn() {
      }
      
      

      @Override
      public void run() {
        count=count+2;
         System.out.println(Thread.currentThread().getId()+"A");
      }
    
    
    
    }

    Runnable runnable = () -> {
      {
        
        {
            System.out.println(Thread.currentThread().getId()+"A");
        }        
        
      }
    };
     Runnable runnable1 = () -> { {
        {
          
            System.out.println(Thread.currentThread().getId()+"B");
         
        }        
        
      }
    };
     executor.submit(runnable1);
     executor.shutdown();
     /* nnn dd= new nnn();
     while(mark)
     {
     try {
     Thread.sleep(1000l);
     if(dd.getCount()==10)
     {
     break;
     }
     executor.submit(dd);
     executor.submit(runnable1);
     }
     catch (InterruptedException ex) {
     Logger.getLogger(NewMain1.class.getName()).log(Level.SEVERE, null, ex);
     }
     }*/
  }
  
}

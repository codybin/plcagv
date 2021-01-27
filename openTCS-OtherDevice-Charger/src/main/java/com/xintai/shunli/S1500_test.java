/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.shunli;

import com.github.s7connector.api.S7Connector;
import com.github.s7connector.api.S7Serializer;
import com.github.s7connector.api.factory.S7ConnectorFactory;
import com.github.s7connector.api.factory.S7SerializerFactory;
import com.github.s7connector.impl.nodave.PLCinterface;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lenovo
 */
public class S1500_test {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws IOException {
    // TODO code application logic here
    
    
    Timer timer=new Timer();
    TimerTask timerTask=new TimerTask() {
      @Override
      public void run() {
        //执行周期性查询的任务
      }
    };
    
        	final int port = 9990;

  //  timer.schedule(timerTask, 2000, 200);
     final String HOST = "127.0.0.1";
    deviceservice_shunliimp de=new deviceservice_shunliimp();
    de.readdata(DB.class,0, 4, 3);
    /* DB db = new DB();
    db.str = "Hello!";
    db.d = Math.PI;
    db.b1 = true;
    db.b3 = true;
    db.by1 = 0x5A;
    Date d = new Date();
    db.date1 = d;
    db.date2 = d;
    db.millis = 3600000;*/
    // de.writedata(db, 20, 200);
     /* S7Connector connector;
     
     try {
     //isRun = true;
     connector = S7ConnectorFactory.buildTCPConnector().withHost(HOST).build();
     } catch (Exception e) {
     connector = null;
     //log.error("new s7connector fail {}", e.getMessage(), e);
     }
     if(connector==null)
     return;
     S7Serializer serializer = S7SerializerFactory.buildSerializer(connector);
     //读取数据
     int DB_NUM = 7;
     int BYTE_OFFSET = 0;
     DB plcDb = serializer.dispense(DB.class, DB_NUM, BYTE_OFFSET);
     System.out.println(plcDb.str);
     DB db = new DB();
     db.str = "Hello!";
     db.d = Math.PI;
     db.b1 = true;
     db.b3 = true;
     db.by1 = 0x5A;
     Date d = new Date();
     System.out.println(d);
     System.out.println(d.getTime());
     db.date1 = d;
     db.date2 = d;
     db.millis = 3600000;
     //写数据到S1500 plc中
     serializer.store(db, DB_NUM, BYTE_OFFSET);
     try {
     connector.close();
     }
     catch (IOException ex) {
     Logger.getLogger(S1500_test.class.getName()).log(Level.SEVERE, null, ex);
     }*/
  }
  
}

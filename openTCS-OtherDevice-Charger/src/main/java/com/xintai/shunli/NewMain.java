/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.shunli;

import com.github.s7connector.api.DaveArea;
import com.github.s7connector.api.S7Connector;
import com.github.s7connector.api.factory.S7ConnectorFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


/**
 *
 * @author Lenovo
 */
public class NewMain {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws IOException {
    // TODO code application logic here

		final int port =9098;
		final ServerSocket serverSocket = new ServerSocket(port);
		new Thread(() -> {
			try {
				Socket socket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		S7Connector connector = S7ConnectorFactory.buildTCPConnector()
				.withHost("127.0.0.1")
				.withPort(port)
				.build();

	//	serverSocket.close();

		try {
      DB db = new DB();
      /*   db.str = "Hello!";
      db.d = Math.PI;
      db.b1 = true;
      db.b3 = true;
      db.by1 = 0x5A;
      Date d = new Date();
      db.date1 = d;
      db.date2 = d;
      db.millis = 3600000;*/
		//	connector.write(db,20,200);
		} catch(IllegalArgumentException e){
			return;
		}
serverSocket.close();
		//throw new IllegalArgumentException("fail-case not reached!");
	}

}
  
  


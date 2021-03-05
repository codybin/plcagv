/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.device;

import com.xintai.WMSTaskTable;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class NewMain {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
     DestinationLocationService destinationLocationService=new DestinationLocationService();
    System.out.println(destinationLocationService.findWMSTaskTableByTaskNumber("20210304001").toString());
    WMSTaskTable wMSTaskTable=new WMSTaskTable();
    wMSTaskTable.setTasknumber("20210304002");
    wMSTaskTable.setAGVstate("Unfinsh");
    wMSTaskTable.setStartstation("003");
    wMSTaskTable.setEndstation("004");
    wMSTaskTable.setTasktype("PTRK");
     destinationLocationService.InsertWMSTaskTable(wMSTaskTable);
      wMSTaskTable.setTasknumber("20210304002");
      wMSTaskTable.setAGVstate("3232");
      destinationLocationService.UpdateWMSTaskTable(wMSTaskTable);
      destinationLocationService.Delete("20210304002");
      DestinationsLocations destinationsM1=   destinationLocationService.findDestinationsByOrderType("001");
    DestinationsLocations destinationsM=   destinationLocationService.findDestinationsByOrderType("007");
    if(destinationsM==null||destinationsM1==null)
    {
    return;
    }
     destinationsM1.MergerDestionation(destinationsM.getDestinations());
    destinationsM1.getDestinations().getDestinations().forEach((e)->{System.out.println(e.toString());});
    /*
    jingBianMysqlService jinBianMysqlService=new jingBianMysqlService();
    jingBian_Device jingBian_Device1=   jinBianMysqlService.findDeviceByID(1);
    System.out.println(jingBian_Device1.toString());
    jinBianMysqlService.clearDeviceID(1);
    jinBianMysqlService.setDeviceID(1, 2);*/
  }
}

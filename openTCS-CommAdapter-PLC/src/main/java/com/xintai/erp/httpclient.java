/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.erp;

/**
 *
 * @author Lenovo
 */
public class httpclient {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
     ReportCarPostionTOERP rcptoerp=new ReportCarPostionTOERP("hh", "dd", "ddd", "ss", "00");
      ReportPostionERPService reportPostionERPService=new ReportPostionERPService("http://127.0.0.1:8888");
    System.out.println(reportPostionERPService.SendPostionTOERP(rcptoerp));
  }
  
}

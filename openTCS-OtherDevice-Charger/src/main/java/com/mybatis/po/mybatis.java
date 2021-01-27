/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mybatis.po;

import com.xintai.mysql.MysqlService;

/**
 *
 * @author Lenovo
 */
public class mybatis {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    MysqlService mysqlService=new MysqlService() ;
    System.out.println(mysqlService.findOneByID(1).toString());
    Employees e=new Employees(1, "wukong", "sunwu", "sunwukong@huaguoshan.com");
    mysqlService.Update(e);
    System.out.println(mysqlService.findOneByID(1).toString());
    mysqlService.findAll().forEach((e1)->{System.out.println(e1.toString());});
    mysqlService.Delete(2);
    mysqlService.Insert(new Employees(3, "jiabins", "jj", "jiabinzheng@sin.com"));
    mysqlService.findAll().forEach((e1)->{System.out.println(e1.toString());});
   
  }
  
}

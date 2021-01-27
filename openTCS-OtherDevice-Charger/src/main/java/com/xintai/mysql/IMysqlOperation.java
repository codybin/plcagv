/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.mysql;

import com.mybatis.po.Employees;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface IMysqlOperation {
  public Employees findOneByID(int id);
  public List<Employees>findAll();
  public void Insert(Employees employees);
  public void Update(Employees e);
  public void Delete(int id);
}

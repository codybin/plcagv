/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.mysql;

import java.io.IOException;
import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 *
 * @author Lenovo
 */
public  class MysqlFactory {

  public MysqlFactory() {
    if(sqlSessionFactory==null)
    {
    sqlSessionFactory=getFactory();
    }
  }
  private static SqlSessionFactory sqlSessionFactory;
  public  SqlSessionFactory getSqlSessionFactory() {
    return sqlSessionFactory;
  }
  private SqlSessionFactory getFactory()
  {
   SqlSessionFactory factory = null;
   String resource = "mybatis-config.xml";
    InputStream is;
try {
    is = Resources.getResourceAsStream(resource);
    factory = new SqlSessionFactoryBuilder().build(is);
} catch (IOException e) {
     factory=null;
    System.out.println(e.getMessage());
}
return factory;
  
  }
}

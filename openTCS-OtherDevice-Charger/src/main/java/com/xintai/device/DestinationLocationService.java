/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.device;

import com.mybatis.po.Employees;
import com.xintai.WMSTaskTable;
import com.xintai.mysql.MysqlFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 *
 * @author Lenovo
 */
public class DestinationLocationService {
  private final MysqlFactory mysqlFactory;
  private final SqlSessionFactory mSessionFactory;

  public DestinationLocationService() {
       this.mysqlFactory =new MysqlFactory();
       mSessionFactory = mysqlFactory.getSqlSessionFactory();
  }
  public void UpdateWMSTaskTable(WMSTaskTable wMSTaskTable) {
      SqlSession sqlSession = null;
  try {
    sqlSession =mSessionFactory .openSession();
    if(findWMSTaskTableByTaskNumber(wMSTaskTable.getTasknumber())!=null)
    sqlSession.update("com.mybatis.mapper.TaskTableMapper.updateTaskTable", wMSTaskTable); 
     sqlSession.commit();   
  } catch (Exception e) {
  System.out.println(e.getMessage());
    sqlSession.rollback(); 
}finally{
    // 在 finally 语句中确保资源被顺利关闭
    if(sqlSession != null){
        sqlSession.close();
    }
  } 
  }
  
   public void Delete(String id) {
       SqlSession sqlSession = null;

  try {
    // 打开 SqlSession 会话
    sqlSession =mSessionFactory .openSession();
    if(findWMSTaskTableByTaskNumber(id)!=null)
       sqlSession.delete("com.mybatis.mapper.TaskTableMapper.deleteTaskTable", id);
     sqlSession.commit();   
  } catch (Exception e) {
  System.out.println(e.getMessage());
 
    sqlSession.rollback(); 
}finally{
    // 在 finally 语句中确保资源被顺利关闭
    if(sqlSession != null){
        sqlSession.close();
    }
  } 
  }
  
  public void InsertWMSTaskTable(WMSTaskTable wMSTaskTable) {
    SqlSession sqlSession = null;
  try {
    // 打开 SqlSession 会话
    sqlSession =mSessionFactory .openSession();
    if(findWMSTaskTableByTaskNumber(wMSTaskTable.getTasknumber())==null)
        sqlSession.insert("com.mybatis.mapper.TaskTableMapper.addTaskTable",wMSTaskTable);
     sqlSession.commit();   
  } catch (Exception e) {
  System.out.println(e.getMessage());
    sqlSession.rollback(); 
}finally{
    // 在 finally 语句中确保资源被顺利关闭
    if(sqlSession != null){
        sqlSession.close();
    }
  }
  }
  
  
 public WMSTaskTable findWMSTaskTableByTaskNumber(String number)
 {
    SqlSession sqlSession = null;
   WMSTaskTable  wMSTaskTable=null;
   try {
    // 打开 SqlSession 会话
    sqlSession =mSessionFactory .openSession();
    wMSTaskTable = sqlSession.selectOne("com.mybatis.mapper.TaskTableMapper.getTaskTable", number);//To change body of generated methods, choose Tools | Templates.
    sqlSession.commit();   
  } catch (Exception e) {
  System.out.println(e.getMessage());
    sqlSession.rollback(); 
     return null;// 回滚事务
}finally{
    // 在 finally 语句中确保资源被顺利关闭
    if(sqlSession != null){
        sqlSession.close();
    }
}
   return wMSTaskTable;

 }
     
 public DestinationsLocations  findDestinationsByOrderType(String wmsstaion)
  {
   SqlSession sqlSession = null;
   DestinationsLocations  destinationsM=null;
   //WMSTableInfor wmsti=new WMSTableInfor(ordertype, wmsstaion);
   try {
    // 打开 SqlSession 会话
    sqlSession =mSessionFactory .openSession();
    destinationsM = sqlSession.selectOne("com.mybatis.mapper.LikuLocationMapper.getLikuLocation", wmsstaion);//To change body of generated methods, choose Tools | Templates.
    sqlSession.commit();   
  } catch (Exception e) {
  System.out.println(e.getMessage());
    sqlSession.rollback(); 
     return null;// 回滚事务
}finally{
    // 在 finally 语句中确保资源被顺利关闭
    if(sqlSession != null){
        sqlSession.close();
    }
}
   return destinationsM;
  }

}


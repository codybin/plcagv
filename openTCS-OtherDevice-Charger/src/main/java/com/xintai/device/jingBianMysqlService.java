/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.device;
import com.xintai.mysql.MysqlFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 *
 * @author Lenovo
 */
public class jingBianMysqlService {

  private final MysqlFactory mysqlFactory;
  private final SqlSessionFactory mSessionFactory;

  public jingBianMysqlService() {
       this.mysqlFactory =new MysqlFactory();
       mSessionFactory = mysqlFactory.getSqlSessionFactory();
  }
  public jingBian_Device findDeviceByID(Integer id)
  {
   SqlSession sqlSession = null;
   jingBian_Device  jBianDevice;
  try {
    // 打开 SqlSession 会话
    sqlSession =mSessionFactory .openSession();
    jBianDevice = sqlSession.selectOne("com.mybatis.mapper.jingBianDeviceMapper.selectDeviceByID", id);//To change body of generated methods, choose Tools | Templates.
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
  return jBianDevice;
  }
  public void setDeviceID(int deviceid,int locationindex)
  {
  jingBianLocation jingBianLocation=new jingBianLocation(0, locationindex, 1, deviceid);
  SqlSession sqlSession = null;
  try {
    sqlSession =mSessionFactory .openSession();
  //  if(findOneByID(deviceid)!=null)
    sqlSession.update("com.mybatis.mapper.locationsMapper.setDeviceLocationIndex", jingBianLocation); 
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
  public void clearDeviceID(int deviceid)
  {
    jingBianLocation jingBianLocation=new jingBianLocation(0, 0, 0, deviceid);
  SqlSession sqlSession = null;
  try {
    sqlSession =mSessionFactory .openSession();
  //  if(findOneByID(deviceid)!=null)
    sqlSession.update("com.mybatis.mapper.locationsMapper.clearDeviceLocationIndex", jingBianLocation); 
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
  
}

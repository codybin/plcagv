/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.model;

import java.io.Serializable;

/**
 *
 * @author Lenovo
 */
public class NavigateStatuResponseModel  implements  Serializable{

  /**
   * @return the islifting
   */
  public boolean isIslifting() {
    return islifting;
  }

  /**
   * @param islifting the islifting to set
   */
  public void setIslifting(boolean islifting) {
    this.islifting = islifting;
  }
  private byte statu;
  private final byte reseverd[] ={0,0,0};
  private int targetid=0;
  private final byte reserved2[]={0,0};
  private int targetidhaspssed[]=new int [126];
  private int targetidhasnotpassed[]=new int [126];
private  boolean  islifting=false;

  /**
   * @return the statu
   */
  public byte getStatu() {
    return statu;
  }

public  int getPostionId()
{
  
  int result=0;
  
  for(int i=0;i<targetidhaspssed.length;i++)
  {
  if(targetidhaspssed[i]==0)
  {
    if(i!=0)
  result=targetidhaspssed[i-1];
    else if(i==0)
    {if(statu==4){
      result=targetid;
      }
    }
    break;
  }
  }
  
  return  result;


}
  /**
   * @param statu the statu to set
   */
  public void setStatu(byte statu) {
    this.statu = statu;
  }

  /**
   * @return the targetid
   */
  public int getTargetid() {
    return targetid;
  }

  /**
   * @param targetid the targetid to set
   */
  public void setTargetid(int targetid) {
    this.targetid = targetid;
  }

  /**
   * @return the targetidhaspssed
   */
  public int[] getTargetidhaspssed() {
    return targetidhaspssed;
  }

  /**
   * @param targetidhaspssed the targetidhaspssed to set
   */
  public void setTargetidhaspssed(int[] targetidhaspssed) {
    this.targetidhaspssed = targetidhaspssed;
  }

  /**
   * @return the targetidhasnotpassed
   */
  public int[] getTargetidhasnotpassed() {
    return targetidhasnotpassed;
  }

  /**
   * @param targetidhasnotpassed the targetidhasnotpassed to set
   */
  public void setTargetidhasnotpassed(int[] targetidhasnotpassed) {
    this.targetidhasnotpassed = targetidhasnotpassed;
  }
}

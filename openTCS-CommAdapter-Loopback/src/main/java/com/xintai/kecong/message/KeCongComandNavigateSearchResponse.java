/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.message;

import com.xintai.kecong.model.NavigateStatuResponseModel;

/**
 *
 * @author Lenovo
 */
public class KeCongComandNavigateSearchResponse extends KeCongCommandResponse {

  /**
   * @return the navigateStatuResponseModel
   */
  public NavigateStatuResponseModel getNavigateStatuResponseModel() {
    return navigateStatuResponseModel;
  }

  /**
   * @param navigateStatuResponseModel the navigateStatuResponseModel to set
   */
 
  

  public byte getStatu() {
    return statu;
  }

  public int getTargetid() {
    return targetid;
  }

  public int[] getTargetidhaspssed() {
    return targetidhaspssed;
  }

  public int[] getTargetidhasnotpassed() {
    return targetidhasnotpassed;
  }
 private  NavigateStatuResponseModel navigateStatuResponseModel;
  private byte statu;
  private final byte reseverd[] ={0,0,0};
  private int targetid=0;
  private final byte reserved2[]={0,0};
  private final int targetidhaspssed[]=new int [126];
  private final int targetidhasnotpassed[]=new int [126];
  public KeCongComandNavigateSearchResponse(ByteQueue bytequeque) {
    super(bytequeque);
    navigateStatuResponseModel=new NavigateStatuResponseModel();
    if(value.length==512)
    { navigateStatuResponseModel.setStatu(value[0]);
    navigateStatuResponseModel.setTargetid(((value[4] & 0xff)) | (value[5] << 8 & 0xff));
      statu=value[0];
      targetid=((value[4] & 0xff)) | (value[5] << 8 & 0xff);
      for(int i=0;i<252;i=i+2)
      {
      targetidhaspssed[i/2]=((value[8+i] & 0xff)) | (value[8+i+1]  << 8 & 0xff);
      targetidhasnotpassed[i/2]=((value[260+i] & 0xff) ) | (value[260+i+1]  << 8 & 0xff);
      }
      navigateStatuResponseModel.setTargetidhasnotpassed(targetidhasnotpassed);
      navigateStatuResponseModel.setTargetidhaspssed(targetidhaspssed);
    
    }
  }
  
}

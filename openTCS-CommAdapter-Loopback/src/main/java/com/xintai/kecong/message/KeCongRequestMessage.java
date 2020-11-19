/***********************************************************************
 * Module:  KeCongProtocalMessage.java
 * Author:  Lenovo
 * Purpose: Defines the Class KeCongProtocalMessage
 ***********************************************************************/
package com.xintai.kecong.message;

/** 科聪控制器的消息
 * 
 */
public abstract class KeCongRequestMessage extends KeCongMessage {

  protected  ByteQueue bytequeue=new ByteQueue();
   
protected int tempconsquence=0;
/*   protected boolean expectedresponse=false;
protected boolean ExpectedResponse()
{
return expectedresponse;

}*/
   public abstract void setComandCode();
   public abstract void setDataLength();
   public abstract void addData();
   private int cqs=0;
private void creatmessageheader()
{
  ++communictaionSq;
  cqs=communictaionSq;
setComandCode();
setDataLength();
addData();
}
public int getcqs()
    {
     return cqs&0xffff;
    }
  public static int getCommunictaionSq() {
    return communictaionSq;
  }
 
   
   /** @pdOid e30463b5-17b5-4494-8c62-80c8db263bac */
   public byte[] creatMessage() 
   {   
     for(int i=0;i<LincenceCode.length;i++)
     {
     this.bytequeue.push(LincenceCode[i]);
     
     }  
       this.bytequeue.push(protocolcode);
       creatmessageheader();
      this.bytequeue.push(diagramType);
      this.bytequeue.pushmU2B(communictaionSq);
      this.bytequeue.push(seviceCode);
      this.bytequeue.push(comandCode);
      this.bytequeue.push(returnCode);
      this.bytequeue.push(reserved);
      this.bytequeue.pushmU2B(dataLength);
      this.bytequeue.push(reserved1);
      this.bytequeue.push(dataValue);
      byte [] tempvalue=new byte[bytequeue.size()];
      this.bytequeue.pop(tempvalue);
      return tempvalue;
   }

}

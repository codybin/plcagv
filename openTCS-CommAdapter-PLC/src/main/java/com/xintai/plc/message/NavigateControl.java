/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.message;

import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.locator.NumericLocator;
import com.serotonin.util.queue.ByteQueue;
import com.xintai.data.util.DataConvertUtl;

/**
 *
 * @author Lenovo
 */
public class NavigateControl {
   private final int operation;
   private final int pathid;
   private  ByteQueue byteQueue;
   public NavigateControl(int operation,int pathid)
   {
   this.operation=operation;
   this.pathid=pathid;
   }
  public short [] encodedata()
  {
     NumericLocator num=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,60,DataType.TWO_BYTE_INT_SIGNED);
      short[]numbers= num.valueToShorts(operation);
      NumericLocator num1=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,62,DataType.TWO_BYTE_INT_SIGNED);
      short[]numbers1= num.valueToShorts(pathid);
       return DataConvertUtl.arrayCopy(numbers,numbers1);
  }
}

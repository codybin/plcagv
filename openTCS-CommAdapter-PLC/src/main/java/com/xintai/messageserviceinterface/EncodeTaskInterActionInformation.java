/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.messageserviceinterface;

import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.locator.NumericLocator;
import com.xintai.data.util.DataConvertUtl;

/**
 *
 * @author Lenovo
 */
public class EncodeTaskInterActionInformation {

  public EncodeTaskInterActionInformation(TaskInteractionInformation taskInteractionInformation) {
    this.taskInteractionInformation = taskInteractionInformation;
  }
  
 public short [] EncodeMessage()
 {
   NumericLocator num=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,62,DataType.TWO_BYTE_INT_SIGNED);
 short[]numbers1= num.valueToShorts(taskInteractionInformation.getDispacherTaskState().ordinal());
 short[]numbers2= num.valueToShorts(taskInteractionInformation.getMaterialnum());
 short[]numbers3= num.valueToShorts(taskInteractionInformation.getChargerStaionState().ordinal());
  return DataConvertUtl.arrayCopy(numbers1,numbers2,numbers3); 
 }
 private final TaskInteractionInformation taskInteractionInformation; 
}

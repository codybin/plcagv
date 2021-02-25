/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.agv.charger.device.taitan;

import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.locator.NumericLocator;

/**
 *
 * @author Lenovo
 */
public class ChargerSetModel {
  
  
  private final int enablecharger;
  private final int chargernumber;

  public ChargerSetModel(int enablecharger, int chargernumber) {
    this.enablecharger = enablecharger;
    this.chargernumber = chargernumber;
  }
  public short [] getdata()
  {
   NumericLocator num=new NumericLocator(5, RegisterRange.HOLDING_REGISTER,52,DataType.TWO_BYTE_INT_SIGNED);
   short[] number1= num.valueToShorts(enablecharger);
   short[] number2= num.valueToShorts(chargernumber);
   return arrayCopy(number1,number2);
  }
  
  
  
   private short [] arrayCopy(short []... arrays){
		//数组长度
		int arrayLength = 0;
		//目标数组的起始位置
		int startIndex = 0;

		for(short[] file : arrays){
			arrayLength = arrayLength + file.length;
		}

		short[] fileArray = new short[arrayLength];

		for(int i = 0; i < arrays.length; i++){

			if(i > 0){
				//i为0 时，目标数组的起始位置为0 ,i为1时，目标数组的起始位置为第一个数组长度
				//i为2时，目标数组的起始位置为第一个数组长度+第二个数组长度
				startIndex = startIndex + arrays[i-1].length;
			}

			System.arraycopy(arrays[i], 0, fileArray, startIndex, arrays[i].length);

		}


		return fileArray;
	}
  
}

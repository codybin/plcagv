/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Lenovo
 */
public class DataConvertUtl {
  
  public static short [] arrayCopy(short []... arrays){
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
  
  public static short[] convertToShorts(byte[] data) {
        short[] sdata = new short[data.length / 2];
        for (int i = 0; i < sdata.length; i++)
            sdata[i] = toShort(data[i * 2], data[i * 2 + 1]);
        return sdata;
    }
  private  static short toShort(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xff));
    }
  public static short[] bytesToShort(byte[] bytes) {
		if(bytes==null){
			return null;
		}
		short[] shorts = new short[bytes.length/2];
		ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
	    return shorts;
	}
  public static double bytes2Double(byte[] arr) {
		long value = 0;
		for (int i = 0; i < 8; i++) {
			value |= ((long) (arr[i] & 0xff)) << (8 * i);
		}
		return Double.longBitsToDouble(value);
	}
public static double bytes2Double(byte[] arrr,int offset)
{
byte [] tempbyte=new byte[8];
System.arraycopy(arrr, offset, tempbyte, 0, 8);
return bytes2Double(tempbyte);
}
 public static byte[] double2Bytes(double d) {
		long value = Double.doubleToRawLongBits(d);
		byte[] byteRet = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
		}		
		return byteRet;
	}
 public static byte [] stringtobyte(int length,String name)
  {
  byte []temp1=new byte[length];
     byte []temp= name.getBytes(StandardCharsets.US_ASCII);
     System.arraycopy(temp,0, temp1,0, temp.length>16?16:temp.length);
  return temp1;
  }
  public static int byteArrayToInt(byte[] bytes) {
        int value=0;
        for(int i = 0; i < 4; i++) {
            int shift= (3-i) * 8;
            value +=(bytes[i] & 0xFF) << shift;
        }
        return value;
    }
  //这个地方转换的是四个字节的，但是穿了8个字节，可能先不用改变。
public static int byteArrayToInt(byte[] arrr,int offset)
{
byte [] tempbyte=new byte[8];
System.arraycopy(arrr, offset, tempbyte, 0, 8);
return byteArrayToInt(tempbyte);
}
  public static String toHexString(byte[] byteArray) {
  if (byteArray == null || byteArray.length < 1)
   throw new IllegalArgumentException("this byteArray must not be null or empty");
 
  final StringBuilder hexString = new StringBuilder();
  for (int i = 0; i < byteArray.length; i++) {
   if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
    hexString.append("0");
   hexString.append(Integer.toHexString(0xFF & byteArray[i]));
    hexString.append("        ");
  }
  return hexString.toString().toLowerCase();
 }
  public static byte[] getBytes(int data)  
    {  
        byte[] bytes = new byte[4];  
        bytes[0] = (byte) (data & 0xff);  
        bytes[1] = (byte) ((data & 0xff00) >> 8);  
        bytes[2] = (byte) ((data & 0xff0000) >> 16);  
        bytes[3] = (byte) ((data & 0xff000000) >> 24);  
        return bytes;  
    }  
   public static byte[] getBytes(float data)  
    {  
        int intBits = Float.floatToIntBits(data);  
        return getBytes(intBits);  
    }  
 public static byte[] _getBytes(float data)  
    {  
        int intBits = Float.floatToIntBits(data);  
        return _getBytes(intBits);  
    }  
public static byte[] _getBytes(int data)  
    {  
        byte[] bytes = new byte[4];  
        bytes[2] = (byte) (data & 0xff);  
        bytes[3] = (byte) ((data & 0xff00) >> 8);  
        bytes[0] = (byte) ((data & 0xff0000) >> 16);  
        bytes[1] = (byte) ((data & 0xff000000) >> 24);  
        return bytes;  
    }  
 

    public static byte[] getBytes(short data)  
    {  
        byte[] bytes = new byte[2];  
        bytes[0] = (byte) (data & 0xff);  
        bytes[1] = (byte) ((data & 0xff00) >> 8);  
        return bytes;  
    }  
  
    public static byte[] getBytes(char data)  
    {  
        byte[] bytes = new byte[2];  
        bytes[0] = (byte) (data);  
        bytes[1] = (byte) (data >> 8);  
        return bytes;  
    }  
    public static byte[] getBytes(long data)  
    {  
        byte[] bytes = new byte[8];  
        bytes[0] = (byte) (data & 0xff);  
        bytes[1] = (byte) ((data >> 8) & 0xff);  
        bytes[2] = (byte) ((data >> 16) & 0xff);  
        bytes[3] = (byte) ((data >> 24) & 0xff);  
        bytes[4] = (byte) ((data >> 32) & 0xff);  
        bytes[5] = (byte) ((data >> 40) & 0xff);  
        bytes[6] = (byte) ((data >> 48) & 0xff);  
        bytes[7] = (byte) ((data >> 56) & 0xff);  
        return bytes;  
    }  
  
    
    public static byte[] getBytes(double data)  
    {  
        long intBits = Double.doubleToLongBits(data);  
        return getBytes(intBits);  
    }  
  
    public static byte[] getBytes(String data, String charsetName)  
    {  
        Charset charset = Charset.forName(charsetName);  
        return data.getBytes(charset);  
    }  
  
    public static byte[] getBytes(String data)  
    {  
        return getBytes(data, "GBK");  
    }  
  
      
    public static short getShort(byte[] bytes)  
    {  
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));  
    }  
  
    public static char getChar(byte[] bytes)  
    {  
        return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));  
    }  
  
    public static int getInt(byte[] bytes)  
    {  
        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));  
    }  
      public static int getInt(byte[] bytes,int offset)  
    {  
        return (0xff & bytes[offset]) | (0xff00 & (bytes[1+offset] << 8)) | (0xff0000 & (bytes[2+offset] << 16)) | (0xff000000 & (bytes[3+offset] << 24));  
    }  
     
    public static long getLong(byte[] bytes)  
    {  
        return(0xffL & (long)bytes[0]) | (0xff00L & ((long)bytes[1] << 8)) | (0xff0000L & ((long)bytes[2] << 16)) | (0xff000000L & ((long)bytes[3] << 24))  
         | (0xff00000000L & ((long)bytes[4] << 32)) | (0xff0000000000L & ((long)bytes[5] << 40)) | (0xff000000000000L & ((long)bytes[6] << 48)) | (0xff00000000000000L & ((long)bytes[7] << 56));  
    }  
  
    public static float getFloat(byte[] bytes)  
    {  
        return Float.intBitsToFloat(getInt(bytes));  
    }  
  
    public static double getDouble(byte[] bytes)  
    {  
        long l = getLong(bytes);  
        System.out.println(l);  
        return Double.longBitsToDouble(l);  
    }  
  
    public static String getString(byte[] bytes, String charsetName)  
    {  
        return new String(bytes, Charset.forName(charsetName));  
    }  
  
    public static String getString(byte[] bytes)  
    {  
        return getString(bytes, "GBK");  
    }  
}

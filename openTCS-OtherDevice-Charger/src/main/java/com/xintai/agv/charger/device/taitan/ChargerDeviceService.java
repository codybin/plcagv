/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.agv.charger.device.taitan;

import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.NumericLocator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lenovo
 */
public class ChargerDeviceService {
private final int slaveid;
  public ChargerDeviceService(String ip,int slaveid) {
    this.modbusMaster = getModbusMaster(ip);
    this.slaveid=slaveid;
  }
  ModbusMaster modbusMaster;
  private ModbusMaster getModbusMaster(String IP)
  {
   modbusMaster=  ModbusTcpMaster.getMasterRTUOverTcp(IP);
  return  modbusMaster;
  }
  public boolean WriteChargeSet(int enablecharge,int chargernumber)
  {
    boolean result=false;
   Modbus4jWriter modbus4jWriter=new Modbus4jWriter(modbusMaster);
   ChargerSetModel chargerSetModel=new ChargerSetModel(enablecharge, chargernumber);
    try {
result=   modbus4jWriter.writeRegisters(slaveid, ChargerDeviceVar.EnableAdd, chargerSetModel.getdata());
    }
    catch (ModbusTransportException ex) {
      Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (ModbusInitException ex) {
      Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
    } 
  return result;
  }
   public ChargerStateModel getChargerStateModel()
   {
       ChargerStateModel chargerStateModle=null;
     Modbus4jReader modbus4jReader=new Modbus4jReader(modbusMaster);
      try {
     byte[] data = modbus4jReader.readHoldingRegister(slaveid, 0, 4);
     chargerStateModle=new ChargerStateModel(data);
      }
      catch (ModbusTransportException ex) {
        Logger.getLogger(ChargerDeviceService.class.getName()).log(Level.SEVERE, null, ex);
      }
      catch (ErrorResponseException ex) {
        Logger.getLogger(ChargerDeviceService.class.getName()).log(Level.SEVERE, null, ex);
      }
      catch (ModbusInitException ex) {
        Logger.getLogger(ChargerDeviceService.class.getName()).log(Level.SEVERE, null, ex);
      }
   
   return chargerStateModle;
   
   }
    
}

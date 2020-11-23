/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.modbus;

/**
 *
 * @author Lenovo
 */
import com.serotonin.messaging.StreamTransport;
import com.serotonin.io.serial.SerialParameters;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.ReadCoilsRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.WriteCoilRequest;
import com.serotonin.modbus4j.msg.WriteRegisterRequest;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.sun.tools.javac.jvm.ByteCodes;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.CharSet;
/**
 * @author Matthew Lohbihler
 */
public class ReadSerialTest {
    public static void main11(String[] args) throws Exception {
        SerialParameters serialParameters = new SerialParameters();
        serialParameters.setCommPortId("COM6");
        serialParameters.setBaudRate(9600);
        serialParameters.setDataBits(8);
        serialParameters.setParity(0);
        serialParameters.setStopBits(1);
        serialParameters.setFlowControlIn(0);
        ModbusMaster master = new ModbusFactory().createRtuMaster(serialParameters);
        master.setTimeout(200);
        master.setRetries(1);
        master.init();

        for (int i = 1; i < 5; i++) {
        long start = System.currentTimeMillis();
        System.out.print("Testing " + i + "... ");
        System.out.println(master.testSlaveNode(i));
        System.out.println("Time: " + (System.currentTimeMillis() - start));
        }

        try {
          master.send(new WriteCoilRequest(5, 7, true));
         System.out.println(master.send(new ReadHoldingRegistersRequest(5, 0, 1)));
         System.out.println(master.send(new WriteRegisterRequest(5, 0, 134)));
        }
         catch (Exception e) {
         e.printStackTrace();
         }

           try {
        ReadCoilsRequest request = new ReadCoilsRequest(5, 65534, 1);
        ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(new ReadHoldingRegistersRequest(5, 0, 1));
      
        
        System.out.println(new String( response.getData(),StandardCharsets.UTF_8));
        }
        catch (Exception e) {
        e.printStackTrace();
        }
         
         try {
         ReadCoilsRequest request = new ReadCoilsRequest(5, 65534, 1);
         ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(new ReadHoldingRegistersRequest(5, 0, 1));
         System.out.println(response);
         }
         catch (Exception e) {
         e.printStackTrace();
         }
        // System.out.println(master.scanForSlaveNodes());

        master.destroy();
    }
    public static void main1(String[] args) {
    ModbusProcolCharge modbusProcolCharge=new ModbusProcolCharge(5, "COM6", 9600, 8,0, 1, 0);
      try { 
        modbusProcolCharge.StartCharge(8, true);
        modbusProcolCharge.StopCharge(7, true);
        modbusProcolCharge.GetChargingHeadState(0, 4);
      }
      catch (Exception e) {
        System.out.println(e.getMessage());
      }

      
      
    }
    
     public static void main (String[] args) {
IpParameters ipParameters = new IpParameters();
        // ipParameters.setHost("99.247.60.96");
        // ipParameters.setHost("193.109.41.121");
        //      ipParameters.setHost("10.241.224.195");
        ipParameters.setHost("127.0.0.1");
        ipParameters.setPort(502);
        ipParameters.setEncapsulated(false);

        ModbusFactory modbusFactory = new ModbusFactory();
        // ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, true);
        ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, true);
        master.setTimeout(8000);
        master.setRetries(0);
      try {
        master.init();
  try {
    master.send(new WriteCoilRequest(5, 7, true));
    WriteRegistersRequest request = new WriteRegistersRequest(5, 52, new short[]{2,3,3});
        master.send(request);
  }
  catch (ModbusTransportException ex) {
    Logger.getLogger(ReadSerialTest.class.getName()).log(Level.SEVERE, null, ex);
  }
        System.out.println("com.xintai.modbus.ReadSerialTest.main()1");
      }
      catch (ModbusInitException ex) {
        Logger.getLogger(ReadSerialTest.class.getName()).log(Level.SEVERE, null, ex);
        System.out.println("com.xintai.modbus.ReadSerialTest.main()2");
      }
      
    }
  }

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.ui;


import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.xinta.plc.model.VehicleParameterSetWithPLCMode;
import com.xinta.plc.model.VehicleStateModel;
import com.xintai.plc.comadpater.PLCProcessModel;
import com.xintai.plc.comadpater.PLCProcessModelTO;
import com.xintai.plc.message.comand.SendComandPostion;
import com.xintai.plc.message.comand.SendComandVehicleParameterSet;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import javax.swing.SwingUtilities;

import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.customizations.ServiceCallWrapper;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.opentcs.drivers.vehicle.management.VehicleCommAdapterPanel;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.util.CallWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class PLCControlForm
  extends VehicleCommAdapterPanel{

  private  PLCProcessModelTO processModel;
  private final VehicleService vehicleService;
  private final CallWrapper callWrapper;
private static final Logger LOG = LoggerFactory.getLogger(PLCControlForm.class);
  /**
   * Creates new form NewJPanel
   * @param processModel
   * @param vehicleService
   * @param callWrapper
   */
 @Inject
  public PLCControlForm(@Assisted PLCProcessModelTO processModel,
  @Assisted VehicleService vehicleService,
  @ServiceCallWrapper CallWrapper callWrapper) {
  this.processModel = requireNonNull(processModel, "processModel");
  this.vehicleService = requireNonNull(vehicleService, "vehicleService");
  this.callWrapper = requireNonNull(callWrapper, "callWrapper");
  initComponents();
  initGuiContent();
  }
  @Override
  public void processModelChange(String attributeChanged, VehicleProcessModelTO newprocessModel) {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    if (!(newprocessModel instanceof PLCProcessModelTO)) {
      return;
    }
    processModel = (PLCProcessModelTO) newprocessModel;
    updatePLCVehicleModelData(attributeChanged, processModel);
    updateVehicleProcessModelData(attributeChanged, processModel);
  }
  //更新plc驱动的叉车模型数据
    private void updatePLCVehicleModelData(String attributeChanged,
                                              PLCProcessModelTO processModel) {

 if(Objects.equals(attributeChanged,
                       PLCProcessModel.Attribute.VEHICLE_HOST.name()))
    {
      updateVehicleHost(processModel.getVehicleHost());
    
    } else if (Objects.equals(attributeChanged, PLCProcessModel.Attribute.VEHICLE_PORT.name())) {
      updateVehiclePort(processModel.getVehiclePort());
    }else if (Objects.equals(attributeChanged, PLCProcessModel.Attribute.VEHILCE_STATE.name())) {
   VehicleStateModel vehicleStateModel=    processModel.getPreviousVehicleStateModel();
   updateVehicleStateModel(vehicleStateModel);
   
    }else if (Objects.equals(attributeChanged, PLCProcessModel.Attribute.VEHICLE_SETPARAMETERS.name())) {
   VehicleParameterSetWithPLCMode vps=   processModel.getVehicleParameterSet();
   if(vps==null)
     return;
   /*if(!vps.isIswrite())*/
     /* updateVehicleParaSetting(vps);*/
   
    }
  }
   private void updateVehicleStateModel(VehicleStateModel vehicleStateModel ) {
    SwingUtilities.invokeLater(
        () ->{
        int chargingswitch=vehicleStateModel.getChargingSwitch();
        chargingSwitch.setText(chargingswitch==0?"开关未接通":"开关已接通");
        float batterypower=vehicleStateModel.getBatteryPower();
        batteryPower.setText(String.valueOf(batterypower)+"%");
        float batteryvoltage=vehicleStateModel.getBatteryVoltage();
        batteryVoltage.setText(String.valueOf(batteryvoltage)+"V");
        float batterycurrent=vehicleStateModel.getBatteryCurrent();
        batteryCurrent.setText(String.valueOf(batterycurrent)+"A");
        float batterytemprature=vehicleStateModel.getBatteryTemprature();
        batteryTemprature.setText(String.valueOf(batterytemprature)+"°C");
        int agvnumber=vehicleStateModel.getAgvNumber();
        agvNumber.setText(String.valueOf("no."+agvnumber));
        int ip1=vehicleStateModel.getIP1();
        iP1.setText(String.valueOf(ip1));
        int ip2=vehicleStateModel.getIP2();
        iP2.setText(String.valueOf(ip2));
        int ip3=vehicleStateModel.getIP3();
        iP3.setText(String.valueOf(ip3));
        int ip4=vehicleStateModel.getIP4();
        iP4.setText(String.valueOf(ip4));
        int masterscheduling=vehicleStateModel.getMasterScheduling();
        masterScheduling.setText(masterscheduling==0?"未开启":"已开启");
        float agvvspeed=vehicleStateModel.getAgvVspeed();
        agvVspeed.setText(String.valueOf(agvvspeed)+"mm/s");
        float agvaspeed=vehicleStateModel.getAgvAspeed();
        agvAspeed.setText(String.valueOf(agvaspeed)+"rad/s");
        float totalmileage=vehicleStateModel.getTotalMileage();
        totalMileage.setText(String.valueOf(totalmileage)+"m");
        float runtime=vehicleStateModel.getRunTime();
        runTime.setText(String.valueOf(runtime)+"h");
        int agvrunstate =vehicleStateModel.getAgvRunState();
        String stragvrunstate;
        switch(agvrunstate){
          case 0:
            stragvrunstate="手动";
            break;
         case 1:
            stragvrunstate="停止";
            break;
         case 2:
            stragvrunstate="自动运行";
            break;
         case 3:
            stragvrunstate="报警中";
            break;
         case 4:
            stragvrunstate="充电中";
            break;
         default:
            stragvrunstate="手动";          
        }
        agvRunState.setText(stragvrunstate);
        int errorerrorcode=vehicleStateModel.getErrorErrorCode();
        errorErrorCode.setText(String.valueOf(errorerrorcode));
        int warningerrorcode=vehicleStateModel.getWarningErrorCode();
        warningErrorCode.setText(String.valueOf(warningerrorcode));
        int lastsite=vehicleStateModel.getLastSite();
        lastSite.setText(String.valueOf(lastsite));
        int currentsite=vehicleStateModel.getCurrentSite();
        currentSite.setText(String.valueOf(currentsite));
        int nextsite=vehicleStateModel.getNextSite();
        nextSite.setText(String.valueOf(nextsite));
        int nexttwosite=vehicleStateModel.getNextTwoSite();
        nextTwoSite.setText(String.valueOf(nexttwosite));
        int targetsite=vehicleStateModel.getTargetSite();
        targetSite.setText(String.valueOf(targetsite));
        int targetsitecardirection=vehicleStateModel.getTargetSiteCarDirection();
        targetSiteCarDirection.setText(String.valueOf(targetsitecardirection));
        int positioningstate=vehicleStateModel.getPositioningState();
        String strpositioningstate;
          switch(positioningstate){
          case 0:
            strpositioningstate="定位失败";
            break;
         case 1:
            strpositioningstate="定位正确";
            break;
         case 2:
            strpositioningstate="正在重定位";
            break;
         case 3:
            strpositioningstate="定位成功";
            break;
         default:
            strpositioningstate="定位失败";          
        }
        positioningState.setText(strpositioningstate);
        float betweensitemileage=vehicleStateModel.getBetweenSiteMileage();
        betweenSiteMileage.setText(String.valueOf(betweensitemileage)+"mm");
        int navigationalstate=vehicleStateModel.getNavigationalState();
        String strnavigationalstate;
          switch(navigationalstate){
          case 0:
            strnavigationalstate="无";
            break;
         case 1:
            strnavigationalstate="等待执行导航";
            break;
         case 2:
            strnavigationalstate="正在执行导航";
            break;
         case 3:
            strnavigationalstate="导航暂停";
            break;
         case 4:
            strnavigationalstate="到达";
            break;
         case 5:
            strnavigationalstate="失败";
            break;
         case 6:
            strnavigationalstate="取消";
            break;
         case 7:
            strnavigationalstate="超时";
            break;
         default:
            strnavigationalstate="无";          
        }
        navigationalState.setText(strnavigationalstate);
        int currentschedulingtask=vehicleStateModel.getCurrentSchedulingTask();
        String strcurrentschedulingtask;
         switch(currentschedulingtask){
         case 0:
            strcurrentschedulingtask="无任务";
            break;
         case 1:
            strcurrentschedulingtask="装满轴";
            break;
         case 2:
            strcurrentschedulingtask="卸满轴";
            break;
         case 3:
            strcurrentschedulingtask="装空轴";
            break;
         case 4:
            strcurrentschedulingtask="卸空轴";
            break;
         case 5:
            strcurrentschedulingtask="充电";
            break;
         default:
            strcurrentschedulingtask="无任务";          
        }
         currentSchedulingTask.setText(strcurrentschedulingtask);
        int materialstatus=vehicleStateModel.getMaterialStatus();
        String strmaterialstatus;
         switch(materialstatus){
         case 0:
            strmaterialstatus="空车";
            break;
         case 1:
            strmaterialstatus="满轴";
            break;
         case 2:
            strmaterialstatus="空轴";
            break;
         default:
            strmaterialstatus="空车";          
        }
         materialStatus.setText(strmaterialstatus);
        int taskstatus=vehicleStateModel.getTaskStatus();
         taskStatus.setText(taskstatus==0?"未完成":"已完成");
        
        
        
        
        
        
        
        
        
        
      
    
      
        /* String  postionString= "Point-"+String.format("%04d",id);
        sendvehicletopostion(postionString);*/
        }
    );
  }
   /*  private void updateVehicleParaSetting(VehicleParameterSetWithPLCMode vps ) {
   SwingUtilities.invokeLater(
   () ->{
   setHeartbeatSignal.setText(String.valueOf( vps.getAutorun()));
   setAgvVspeed.setText(String.valueOf(vps.getVspeed()));
   setAgvAspeed.setText(String.valueOf(vps.getAspeed()));
   }
   );
   }*/
   private void sendvehicletopostion(String postion)
   {
   SendComandPostion sendComandPostion=new SendComandPostion(postion);
     sendAdapterCommand(sendComandPostion);
   }
       private void sendAdapterCommand(AdapterCommand command) {
    try {
      callWrapper.call(() -> vehicleService.sendCommAdapterCommand(processModel.getVehicleRef(),
                                                                   command));
    }
    catch (Exception ex) {
      LOG.warn("Error sending comm adapter command '{}'", command, ex);
    }
  }
    private void updateVehicleProcessModelData(String attributeChanged,
                                             VehicleProcessModelTO processModel) {
    if (Objects.equals(attributeChanged,
                       VehicleProcessModel.Attribute.COMM_ADAPTER_ENABLED.name())) {
      updateCommAdapterEnabled(processModel.isCommAdapterEnabled());
    }  else if (Objects.equals(attributeChanged,
                            VehicleProcessModel.Attribute.POSITION.name())) {
    //  updatePosition(processModel.getVehiclePosition());
    }else if (Objects.equals(attributeChanged,
                       VehicleProcessModel.Attribute.COMM_ADAPTER_CONNECTED.name())) {
         updateCommAdapterConnected(processModel.isCommAdapterConnected());  
    }
   
  }
      private void updateCommAdapterConnected(boolean connected) {
          SwingUtilities.invokeLater(() -> {
         isconnect.setSelected(connected);
          });
  }
   private void updateVehicleHost(String host) {
    SwingUtilities.invokeLater(() -> IPTXT.setText(host));
  }
 private void updateVehiclePort(int port) {
    SwingUtilities.invokeLater(() -> PortTXT.setText(Integer.toString(port)));
  }
   private void updateCommAdapterEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> {
     enableadapter.setSelected(enabled);
       IPTXT.setEditable(!enabled);
       PortTXT.setEditable(!enabled);
    });
  }
  
   private void initGuiContent() {
    // Trigger an update for all attributes once first.
    for (VehicleProcessModel.Attribute attribute : VehicleProcessModel.Attribute.values()) {
      processModelChange(attribute.name(), processModel);
    }
    for (PLCProcessModel.Attribute attribute : PLCProcessModel.Attribute.values()) {
      processModelChange(attribute.name(), processModel);
    }
  }
  /**
   * This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        IPTXT = new javax.swing.JTextField();
        enableadapter = new javax.swing.JCheckBox();
        isconnect = new javax.swing.JCheckBox();
        PortTXT = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        batteryVoltage = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        batteryCurrent = new javax.swing.JTextField();
        batteryTemprature = new javax.swing.JTextField();
        chargingSwitch = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        batteryPower = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        agvNumber = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        masterScheduling = new javax.swing.JTextField();
        agvVspeed = new javax.swing.JTextField();
        agvAspeed = new javax.swing.JTextField();
        totalMileage = new javax.swing.JTextField();
        runTime = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        iP1 = new javax.swing.JTextField();
        iP2 = new javax.swing.JTextField();
        iP3 = new javax.swing.JTextField();
        iP4 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        warningErrorCode = new javax.swing.JTextField();
        lastSite = new javax.swing.JTextField();
        currentSite = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        nextSite = new javax.swing.JTextField();
        nextTwoSite = new javax.swing.JTextField();
        targetSite = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        errorErrorCode = new javax.swing.JTextField();
        agvRunState = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        positioningState = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        navigationalState = new javax.swing.JTextField();
        targetSiteCarDirection = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        betweenSiteMileage = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        materialStatus = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        taskStatus = new javax.swing.JTextField();
        currentSchedulingTask = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        setHeartbeatSignal = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        setAgvVspeed = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        setAgvAspeed = new javax.swing.JTextField();
        vehicleparaset = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        setRemoteStart = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        setNextSite = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        setNextTwoSite = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        setTargetSiteCarDirection = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        setMaterialCode = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        setTargetSite = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        setNavigationTask = new javax.swing.JComboBox<>();
        setCurrentSchedulingTask = new javax.swing.JComboBox<>();
        setChargingPileState = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("PLC控制AGV页面"));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("通讯"));

        enableadapter.setText("使能");

        isconnect.setText("连接");

        jLabel9.setText("IP:");

        jLabel10.setText("Port:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(isconnect)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PortTXT, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(enableadapter)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(IPTXT, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IPTXT)
                    .addComponent(enableadapter)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(isconnect)
                    .addComponent(PortTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("叉车状态"));
        jPanel6.setAutoscrolls(true);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("电池状态"));
        jPanel3.setPreferredSize(new java.awt.Dimension(286, 152));

        jLabel1.setText("电池电压：");

        jLabel2.setText("电池电流：");

        jLabel3.setText("电池温度：");

        chargingSwitch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chargingSwitchActionPerformed(evt);
            }
        });

        jLabel21.setText("充电开关:");

        jLabel23.setText("电池电量:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(jLabel23)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(batteryVoltage, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(batteryPower, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chargingSwitch, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(batteryCurrent)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(batteryTemprature)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chargingSwitch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(batteryPower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(batteryVoltage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(batteryCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(batteryTemprature, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("参数状态"));

        jLabel4.setText("agv编号：");

        jLabel5.setText("主控调度：");

        jLabel6.setText("agv线速度：");

        jLabel7.setText("agv角速度：");

        jLabel8.setText("运行总里程：");

        runTime.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        runTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runTimeActionPerformed(evt);
            }
        });

        jLabel25.setText("运行时间：");

        jLabel32.setText("IP4：");

        jLabel33.setText("IP1：");

        jLabel34.setText("IP2：");

        jLabel35.setText("IP3：");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel4)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(totalMileage, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(agvAspeed, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(agvVspeed, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(masterScheduling, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(agvNumber, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(runTime)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33)
                            .addComponent(jLabel34)
                            .addComponent(jLabel35)
                            .addComponent(jLabel32))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(iP1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(iP2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(iP3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(iP4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(agvNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(iP1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(iP2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(iP3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(iP4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(masterScheduling, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(agvVspeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(agvAspeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(totalMileage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(runTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("调度状态"));

        jLabel14.setText("当前站点：");

        jLabel15.setText("下一站点：");

        jLabel16.setText("下二站点：");

        jLabel17.setText("目标站点：");

        jLabel11.setText("Error错误码：");

        jLabel12.setText("Warning错误码：");

        jLabel24.setText("agv运行状态：");

        jLabel13.setText("上一站点：");

        jLabel22.setText("目标点车身方向 ：");

        jLabel26.setText("站点间行走里程 ：");

        jLabel27.setText("定位状态 ：");

        jLabel28.setText("导航状态 ：");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel28)
                            .addGap(40, 40, 40)
                            .addComponent(navigationalState, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel22)
                                .addComponent(jLabel26)
                                .addComponent(jLabel27))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(targetSiteCarDirection, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                .addComponent(targetSite, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(nextTwoSite, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(betweenSiteMileage)
                                .addComponent(positioningState))))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(nextSite, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel12)
                                .addComponent(jLabel13))
                            .addGap(13, 13, 13)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(currentSite, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                .addComponent(lastSite))))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(warningErrorCode, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel24)
                            .addGap(27, 27, 27)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(errorErrorCode, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                .addComponent(agvRunState)))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(agvRunState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(errorErrorCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(warningErrorCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(lastSite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(currentSite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(nextSite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(nextTwoSite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(targetSite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(targetSiteCarDirection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(positioningState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(betweenSiteMileage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(navigationalState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("任务交互"));

        jLabel30.setText("当前调度任务  ：");

        jLabel29.setText("任务状态  ：");

        jLabel31.setText("物料状态  ：");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31)
                    .addComponent(jLabel29))
                .addGap(16, 16, 16)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(taskStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(currentSchedulingTask)
                    .addComponent(materialStatus))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(currentSchedulingTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(materialStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(taskStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 33, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("设置"));

        jLabel19.setText("agv线速度：");

        jLabel20.setText("agv角速度：");

        vehicleparaset.setText("设置");
        vehicleparaset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vehicleparasetActionPerformed(evt);
            }
        });

        jLabel18.setText("心跳信号：");

        jLabel36.setText("远程启动：");

        jLabel37.setText("下一站点：");

        jLabel38.setText("下二站点：");

        jLabel39.setText("目标点车身方向：");

        jLabel40.setText("导航任务：");

        jLabel41.setText("物料编码：");

        jLabel42.setText("充电桩状态：");

        jLabel43.setText("目标站点：");

        jLabel44.setText("当前调度任务：");

        setNavigationTask.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "停止导航", "执行导航", "暂停导航", "清除导航信息" }));

        setCurrentSchedulingTask.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "无任务", "装满轴", "卸满轴", "装空轴", "卸空轴", "充电" }));

        setChargingPileState.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "无", "已弹出", "已缩回" }));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel40)
                    .addComponent(jLabel38)
                    .addComponent(jLabel37)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(setCurrentSchedulingTask, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(vehicleparaset))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(setChargingPileState, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(setNextTwoSite)
                            .addComponent(setNextSite)
                            .addComponent(setNavigationTask, 0, 149, Short.MAX_VALUE)
                            .addComponent(setTargetSiteCarDirection)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addGap(40, 40, 40)
                        .addComponent(setRemoteStart, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(33, 33, 33)
                        .addComponent(setAgvAspeed, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel43)
                            .addComponent(jLabel41))
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(setTargetSite, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(setMaterialCode, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel18))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(setHeartbeatSignal)
                            .addComponent(setAgvVspeed, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(setHeartbeatSignal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(setAgvVspeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(setAgvAspeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(setRemoteStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(setNavigationTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(setNextSite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(setNextTwoSite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(setTargetSiteCarDirection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(setTargetSite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(setCurrentSchedulingTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(setMaterialCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(setChargingPileState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(vehicleparaset)
                .addGap(34, 34, 34))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 104, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

  private void vehicleparasetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vehicleparasetActionPerformed
 
    
    SwingUtilities.invokeLater(
        () ->{
          String heartbeatsignalString= setHeartbeatSignal.getText();
          String vspeedString= setAgvVspeed.getText();
          String aspeedString= setAgvAspeed.getText();
          String remotestartString=setRemoteStart.getText();
          String navigationtaskString=String.valueOf(setNavigationTask.getSelectedIndex()) ;
          String nextsiteString= setNextSite.getText();
          String nexttwositeString= setNextTwoSite.getText();
          String targetsitecardirectionString=setTargetSiteCarDirection.getText();
          String targetsiteString=setTargetSite.getText();
          String currentschedulingtaskString=String.valueOf(setCurrentSchedulingTask.getSelectedIndex()) ;
          String materialcodeString= setMaterialCode.getText();
          String chargingpilestateString=String.valueOf(setChargingPileState.getSelectedIndex());
          
          VehicleParameterSetWithPLCMode vps=new VehicleParameterSetWithPLCMode(Integer.valueOf(heartbeatsignalString),Float.parseFloat(vspeedString),Float.parseFloat(aspeedString),
                                                                                Integer.valueOf(remotestartString),Integer.valueOf(navigationtaskString),Integer.valueOf(nextsiteString),
                                                                                Integer.valueOf(nexttwositeString),Integer.valueOf(targetsitecardirectionString),Integer.valueOf(targetsiteString),
                                                                                Integer.valueOf(currentschedulingtaskString),Integer.valueOf(materialcodeString),Integer.valueOf(chargingpilestateString),true);
          SendComandVehicleParameterSet sendComandVehicleParameterSet=new SendComandVehicleParameterSet(vps);
          sendAdapterCommand(sendComandVehicleParameterSet);
          System.out.println(vps.toString());
        }
    );   
  }//GEN-LAST:event_vehicleparasetActionPerformed

    private void runTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_runTimeActionPerformed

  private void chargingSwitchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chargingSwitchActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_chargingSwitchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField IPTXT;
    private javax.swing.JTextField PortTXT;
    private javax.swing.JTextField agvAspeed;
    private javax.swing.JTextField agvNumber;
    private javax.swing.JTextField agvRunState;
    private javax.swing.JTextField agvVspeed;
    private javax.swing.JTextField batteryCurrent;
    private javax.swing.JTextField batteryPower;
    private javax.swing.JTextField batteryTemprature;
    private javax.swing.JTextField batteryVoltage;
    private javax.swing.JTextField betweenSiteMileage;
    private javax.swing.JTextField chargingSwitch;
    private javax.swing.JTextField currentSchedulingTask;
    private javax.swing.JTextField currentSite;
    private javax.swing.JCheckBox enableadapter;
    private javax.swing.JTextField errorErrorCode;
    private javax.swing.JTextField iP1;
    private javax.swing.JTextField iP2;
    private javax.swing.JTextField iP3;
    private javax.swing.JTextField iP4;
    private javax.swing.JCheckBox isconnect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JTextField lastSite;
    private javax.swing.JTextField masterScheduling;
    private javax.swing.JTextField materialStatus;
    private javax.swing.JTextField navigationalState;
    private javax.swing.JTextField nextSite;
    private javax.swing.JTextField nextTwoSite;
    private javax.swing.JTextField positioningState;
    private javax.swing.JTextField runTime;
    private javax.swing.JTextField setAgvAspeed;
    private javax.swing.JTextField setAgvVspeed;
    private javax.swing.JComboBox<String> setChargingPileState;
    private javax.swing.JComboBox<String> setCurrentSchedulingTask;
    private javax.swing.JTextField setHeartbeatSignal;
    private javax.swing.JTextField setMaterialCode;
    private javax.swing.JComboBox<String> setNavigationTask;
    private javax.swing.JTextField setNextSite;
    private javax.swing.JTextField setNextTwoSite;
    private javax.swing.JTextField setRemoteStart;
    private javax.swing.JTextField setTargetSite;
    private javax.swing.JTextField setTargetSiteCarDirection;
    private javax.swing.JTextField targetSite;
    private javax.swing.JTextField targetSiteCarDirection;
    private javax.swing.JTextField taskStatus;
    private javax.swing.JTextField totalMileage;
    private javax.swing.JButton vehicleparaset;
    private javax.swing.JTextField warningErrorCode;
    // End of variables declaration//GEN-END:variables
}

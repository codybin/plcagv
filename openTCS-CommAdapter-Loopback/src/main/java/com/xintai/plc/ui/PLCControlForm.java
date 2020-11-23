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
import com.xintai.vehicle.comadpter.KeCongProcessModel;
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
   if(!vps.isIswrite())
   updateVehicleParaSetting(vps);
   
    }
  }
   private void updateVehicleStateModel(VehicleStateModel vehicleStateModel ) {
    SwingUtilities.invokeLater(
        () ->{
         float px=  vehicleStateModel.getPostionx();
        postionx.setText(String.valueOf(px));
        float py=vehicleStateModel.getPositiony();
        postiony.setText(String.valueOf(py));
        float angle=vehicleStateModel.getPositionangle();
        positionangle.setText(String.valueOf(angle));
        int navigationstation=vehicleStateModel.getCurrentnavigationstation();
        currentnavigationstation.setText(String.valueOf(navigationstation));
        int intpostionstate=vehicleStateModel.getPostionstate();
        String strpostionstate="定位失败";
        switch(intpostionstate%4){
          case 0:
            strpostionstate="定位失败";
            break;
          case 1:
            strpostionstate="定位正确";
            break;
          case 2:
            strpostionstate="正在重定位";
            break;
          case 3:
            strpostionstate="定位完成";
            break;
        }
        postionstate.setText(strpostionstate);
        int intnavigatestate=vehicleStateModel.getNavigatestate();
        String strnavigatestate="无";
        switch(intnavigatestate%8){
          case 0:
            strnavigatestate="无";
            break;
          case 1:
            strnavigatestate="等待执行导航";
            break;
          case 2:
            strnavigatestate="正在执行导航";
            break;
          case 3:
            strnavigatestate="导航暂停";
            break;
          case 4:
            strnavigatestate="到达";
            break;
          case 5:
            strnavigatestate="失败";
            break;
          case 6:
            strnavigatestate="取消";
            break;
          case 7:
            strnavigatestate="超时";
            break;
        }
        navigatestate.setText(String.valueOf(strnavigatestate));
        int intnavigatetype=vehicleStateModel.getNavigatetype();
        String strnavigatetype="没有导航";
        switch(intnavigatetype){
          case 0:
            strnavigatetype="没有导航";
            break;
          case 1:
            strnavigatetype="自由导航到任意点";
            break;
          case 2:
            strnavigatetype="自由导航到站点";
            break;
          case 3:
            strnavigatetype="路径导航到站点";
            break;
          case 7:
            strnavigatetype="平动转动";
            break;
          case 100:
            strnavigatetype="其他";
            break;
          default:
            strnavigatetype="没有导航";
            break;
        }
        navigatetype.setText(String.valueOf(strnavigatetype));
        float postionlevel1=vehicleStateModel.getPostionlevel();
        postionlevel.setText(String.valueOf(postionlevel1));
        int batterypower1=vehicleStateModel.getBatterypower();
        batterypower.setText(String.valueOf(batterypower1));
        float batterytemprature1=vehicleStateModel.getBatterytemprature();
        batterytemprature.setText(String.valueOf(batterytemprature1));
        float batterycurrent1=vehicleStateModel.getBatterycurrent();
        batterycurrent.setText(String.valueOf(batterycurrent1));
        float batteryvoltage1=vehicleStateModel.getBatteryvoltage();
        batteryvoltage.setText(String.valueOf(batteryvoltage1));
        float kilometerintotal1=vehicleStateModel.getKilometerintotal();
        kilometerintotal.setText(String.valueOf(kilometerintotal1));
        float timeintotal1=vehicleStateModel.getTimeintotal();
        timeintotal.setText(String.valueOf(timeintotal1));
        int id=vehicleStateModel.getCurrentposition();
        currentpostion.setText(String.valueOf(id));
        if(id==0)
          id=1;
        int mapname1=vehicleStateModel.getMapname();
        mapname.setText(String.valueOf(mapname1));
        int intdispaterstate=vehicleStateModel.getDispaterstate();
        String strdispaterstate="单机模式";
        switch(intdispaterstate%3){
           case 0:
             strdispaterstate="单机模式";
             break;
           case 1:
             strdispaterstate="调度模式";
             break;
           case 2:
             strdispaterstate="调度模式但与调度系统失去连接";
             break;
        }
        dispaterstate.setText(String.valueOf(strdispaterstate));
        int kilometertoday1=vehicleStateModel.getKilometertoday();
        kilometertoday.setText(String.valueOf(kilometertoday1));
        int loadstate1=vehicleStateModel.getLoadstate();
        loadstate.setText(String.valueOf(loadstate1));
        /* String  postionString= "Point-"+String.format("%04d",id);
        sendvehicletopostion(postionString);*/
        }
    );
  }
   private void updateVehicleParaSetting(VehicleParameterSetWithPLCMode vps ) {
    SwingUtilities.invokeLater(
        () ->{
          autorun.setText(String.valueOf( vps.getAutorun()));
          vspeed.setText(String.valueOf(vps.getVspeed()));
          aspeed.setText(String.valueOf(vps.getAspeed()));
          }
    );
        }
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
    }
   
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
    postionx = new javax.swing.JTextField();
    jLabel21 = new javax.swing.JLabel();
    postiony = new javax.swing.JTextField();
    jLabel23 = new javax.swing.JLabel();
    jLabel1 = new javax.swing.JLabel();
    positionangle = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();
    currentnavigationstation = new javax.swing.JTextField();
    postionstate = new javax.swing.JTextField();
    navigatestate = new javax.swing.JTextField();
    jLabel5 = new javax.swing.JLabel();
    jLabel6 = new javax.swing.JLabel();
    jLabel7 = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    navigatetype = new javax.swing.JTextField();
    postionlevel = new javax.swing.JTextField();
    batterypower = new javax.swing.JTextField();
    batterytemprature = new javax.swing.JTextField();
    jLabel11 = new javax.swing.JLabel();
    kilometerintotal = new javax.swing.JTextField();
    batteryvoltage = new javax.swing.JTextField();
    jLabel12 = new javax.swing.JLabel();
    jLabel24 = new javax.swing.JLabel();
    jLabel13 = new javax.swing.JLabel();
    jLabel14 = new javax.swing.JLabel();
    timeintotal = new javax.swing.JTextField();
    currentpostion = new javax.swing.JTextField();
    mapname = new javax.swing.JTextField();
    batterycurrent = new javax.swing.JTextField();
    jLabel15 = new javax.swing.JLabel();
    jLabel25 = new javax.swing.JLabel();
    jLabel16 = new javax.swing.JLabel();
    jLabel17 = new javax.swing.JLabel();
    dispaterstate = new javax.swing.JTextField();
    kilometertoday = new javax.swing.JTextField();
    loadstate = new javax.swing.JTextField();
    jPanel2 = new javax.swing.JPanel();
    jLabel18 = new javax.swing.JLabel();
    autorun = new javax.swing.JTextField();
    jLabel19 = new javax.swing.JLabel();
    vspeed = new javax.swing.JTextField();
    jLabel20 = new javax.swing.JLabel();
    aspeed = new javax.swing.JTextField();
    vehicleparaset = new javax.swing.JButton();

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

    postionx.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        postionxActionPerformed(evt);
      }
    });

    jLabel21.setText("位置x:");

    jLabel23.setText("位置y:");

    jLabel1.setText("角度坐标：");

    jLabel2.setText("当前导航站点：");

    jLabel3.setText("定位状态：");

    jLabel4.setText("当前导航状态：");

    jLabel5.setText("当前导航类型：");

    jLabel6.setText("定位置信度：");

    jLabel7.setText("电池电量：");

    jLabel8.setText("电池温度：");

    jLabel11.setText("总里程：");

    jLabel12.setText("累计运行时间：");

    jLabel24.setText("电池电压：");

    jLabel13.setText("当前所在站点：");

    jLabel14.setText("当前地图名：");

    batterycurrent.setHorizontalAlignment(javax.swing.JTextField.LEFT);
    batterycurrent.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        batterycurrentActionPerformed(evt);
      }
    });

    jLabel15.setText("调度状态：");

    jLabel25.setText("电池电流：");

    jLabel16.setText("今日总里程：");

    jLabel17.setText("负载状态：");

    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
      jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel6Layout.createSequentialGroup()
        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel6)
              .addComponent(jLabel7)
              .addComponent(jLabel8))
            .addGap(18, 18, 18)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(postionlevel)
              .addComponent(batterypower)
              .addComponent(batterytemprature)))
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addComponent(jLabel5)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(navigatetype))
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(28, 28, 28)
                .addComponent(positionangle))
              .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currentnavigationstation))
              .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(postionx, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel23)
                .addGap(51, 51, 51)
                .addComponent(postiony, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))))
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel4)
              .addComponent(jLabel3))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(postionstate)
              .addComponent(navigatestate))))
        .addGap(76, 76, 76)
        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel25)
                .addGap(27, 27, 27)
                .addComponent(batterycurrent, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel6Layout.createSequentialGroup()
              .addComponent(jLabel24)
              .addGap(27, 27, 27)
              .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(kilometerintotal, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                .addComponent(batteryvoltage))))
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addComponent(jLabel12)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(timeintotal))
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addComponent(jLabel13)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(currentpostion))
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel14)
              .addComponent(jLabel15))
            .addGap(18, 18, 18)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(mapname)
              .addComponent(dispaterstate)))
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel16)
              .addComponent(jLabel17))
            .addGap(18, 18, 18)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(kilometertoday)
              .addComponent(loadstate))))
        .addGap(48, 48, 48))
    );
    jPanel6Layout.setVerticalGroup(
      jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel6Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(postionx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel21))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(postiony, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel23))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel1)
              .addComponent(positionangle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel2)
              .addComponent(currentnavigationstation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel3)
              .addComponent(postionstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel4)
              .addComponent(navigatestate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel5)
              .addComponent(navigatetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel6)
              .addComponent(postionlevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel7)
              .addComponent(batterypower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel8)
              .addComponent(batterytemprature, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(batterycurrent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel25))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(batteryvoltage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel24))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel11)
              .addComponent(kilometerintotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel12)
              .addComponent(timeintotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel13)
              .addComponent(currentpostion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel14)
              .addComponent(mapname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel15)
              .addComponent(dispaterstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel16)
              .addComponent(kilometertoday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel17)
              .addComponent(loadstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jLabel18.setText("自动：");

    jLabel19.setText("线速度：");

    jLabel20.setText("角速度：");

    vehicleparaset.setText("设置");
    vehicleparaset.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        vehicleparasetActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel18)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(autorun, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(18, 18, 18)
        .addComponent(jLabel19)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(vspeed, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(18, 18, 18)
        .addComponent(jLabel20)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(aspeed, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(18, 18, 18)
        .addComponent(vehicleparaset)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
        .addContainerGap(44, Short.MAX_VALUE)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel18)
          .addComponent(autorun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel19)
          .addComponent(vspeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel20)
          .addComponent(aspeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(vehicleparaset))
        .addGap(37, 37, 37))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents

  private void postionxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postionxActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_postionxActionPerformed

  private void batterycurrentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batterycurrentActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_batterycurrentActionPerformed

  private void vehicleparasetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vehicleparasetActionPerformed
 
    
    SwingUtilities.invokeLater(
        () ->{
          String autorunString= autorun.getText();
          String vspeedString= vspeed.getText();
          String aspeedString= aspeed.getText();
          VehicleParameterSetWithPLCMode vps=new VehicleParameterSetWithPLCMode(Integer.valueOf(autorunString),Float.parseFloat(vspeedString),Float.parseFloat(aspeedString),true);
          SendComandVehicleParameterSet sendComandVehicleParameterSet=new SendComandVehicleParameterSet(vps);
          sendAdapterCommand(sendComandVehicleParameterSet);
          System.out.println(vps.toString());
        }
    );   
  }//GEN-LAST:event_vehicleparasetActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JTextField IPTXT;
  private javax.swing.JTextField PortTXT;
  private javax.swing.JTextField aspeed;
  private javax.swing.JTextField autorun;
  private javax.swing.JTextField batterycurrent;
  private javax.swing.JTextField batterypower;
  private javax.swing.JTextField batterytemprature;
  private javax.swing.JTextField batteryvoltage;
  private javax.swing.JTextField currentnavigationstation;
  private javax.swing.JTextField currentpostion;
  private javax.swing.JTextField dispaterstate;
  private javax.swing.JCheckBox enableadapter;
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
  private javax.swing.JLabel jLabel23;
  private javax.swing.JLabel jLabel24;
  private javax.swing.JLabel jLabel25;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel6;
  private javax.swing.JTextField kilometerintotal;
  private javax.swing.JTextField kilometertoday;
  private javax.swing.JTextField loadstate;
  private javax.swing.JTextField mapname;
  private javax.swing.JTextField navigatestate;
  private javax.swing.JTextField navigatetype;
  private javax.swing.JTextField positionangle;
  private javax.swing.JTextField postionlevel;
  private javax.swing.JTextField postionstate;
  private javax.swing.JTextField postionx;
  private javax.swing.JTextField postiony;
  private javax.swing.JTextField timeintotal;
  private javax.swing.JButton vehicleparaset;
  private javax.swing.JTextField vspeed;
  // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecongcontrol.ui;

import com.google.inject.assistedinject.Assisted;
import com.sun.java.swing.action.CancelAction;
import com.xintai.kecong.comand.SendComanReadVar;
import com.xintai.kecong.comand.SendComandAutoRequest;
import com.xintai.kecong.comand.SendComandMakeSurePoint;
import com.xintai.kecong.comand.SendComandPeriodicStateRequestEnabled;
import com.xintai.kecong.comand.SendComandWriteVar;
import com.xintai.kecong.comand.SendNavigationRequest;
import com.xintai.kecong.comand.SendRequestCommand;
import com.xintai.kecong.mesaage.adapter.OpentcsPointToKeCongPoint;
import com.xintai.kecong.message.DataConvertUtl;
import com.xintai.kecong.message.KeCongActionVar;
import com.xintai.kecong.model.NavigateStatuResponseModel;
import com.xintai.kecong.model.ReadVarModel;
import com.xintai.kecong.model.RobotStatuResponseModel;
import com.xintai.vehicle.comadpter.KeCongProcessModel;
import com.xintai.vehicle.comadpter.KeCongProcessModelTO;
;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import jdk.javadoc.internal.tool.Start;
import jdk.nashorn.internal.ir.ContinueNode;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.customizations.ServiceCallWrapper;
import org.opentcs.data.model.Point;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.opentcs.drivers.vehicle.management.VehicleCommAdapterPanel;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.util.CallWrapper;
import org.opentcs.util.Comparators;

/**
 *
 * @author Lenovo
 */
public class KeCongControlForm
    extends VehicleCommAdapterPanel {

  private  KeCongProcessModelTO processModel;
  private  VehicleService vehicleService;
  private  CallWrapper callWrapper;
private static final Logger LOG = LoggerFactory.getLogger(KeCongControlForm.class);

 @Inject
  public KeCongControlForm(@Assisted KeCongProcessModelTO processModel,
  @Assisted VehicleService vehicleService,
  @ServiceCallWrapper CallWrapper callWrapper) {
  this.processModel = requireNonNull(processModel, "processModel");
  this.vehicleService = requireNonNull(vehicleService, "vehicleService");
  this.callWrapper = requireNonNull(callWrapper, "callWrapper");
  initGuiContent();
  initComponents();
 initComboBoxes();
  }
    private void initComboBoxes() {
    try {
      // Initialize the list of known points. Only add points whose names have a length of 8.
      postioncbx.removeAllItems();
      VarNamecb.removeAllItems();
       actioncbx.removeAllItems();
      callWrapper.call(() -> vehicleService.fetchObjects(Point.class)).stream()
          .sorted(Comparators.objectsByName())
          .forEach
    (
        point -> postioncbx.addItem(point.getName())
    );
     VarNamecb.addItem(KeCongActionVar.PID_ENABLE_STRING);
       VarNamecb.addItem(KeCongActionVar.LIFT_SV);
         VarNamecb.addItem(KeCongActionVar.FINSHI_TASK);
       VarNamecb.addItem(KeCongActionVar.TRUCKLiftData);
        actioncbx.addItem(NavigationControlState.Start.name());
        actioncbx.addItem(NavigationControlState.Pause.name());
        actioncbx.addItem(NavigationControlState.Cancel.name());
        actioncbx.addItem(NavigationControlState.Continue.name());
      // Initialize the list of valid actions.
     
      /*  for (OrderRequest.OrderAction curAction : OrderRequest.OrderAction.values()) {
      actionComboBox.addItem(curAction);
      }*/
    }
    catch (Exception ex) {
     // LOG.warn("Error fetching points", ex);
    }
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
   private void initGuiContent() {
    // Trigger an update for all attributes once first.
    for (VehicleProcessModel.Attribute attribute : VehicleProcessModel.Attribute.values()) {
      processModelChange(attribute.name(), processModel);
    }
    for (KeCongProcessModel.Attribute attribute : KeCongProcessModel.Attribute.values()) {
      processModelChange(attribute.name(), processModel);
    }
  }
  @Override
  public void processModelChange(String attributeChanged, VehicleProcessModelTO newprocessModel) {
     if (!(newprocessModel instanceof KeCongProcessModelTO)) {
      return;
    }
    processModel = (KeCongProcessModelTO) newprocessModel;
    updateKeCongVehicleModelData(attributeChanged, processModel);
    updateVehicleProcessModelData(attributeChanged, processModel);
  
  }
  
  private void updateKeCongVehicleModelData(String attributeChanged,
                                              KeCongProcessModelTO processModel) {

 if(Objects.equals(attributeChanged,
                       KeCongProcessModel.Attribute.VEHICLE_HOST.name()))
    {
      updateVehicleHost(processModel.getVehicleHost());
    
    } else if (Objects.equals(attributeChanged, KeCongProcessModel.Attribute.VEHICLE_PORT.name())) {
      updateVehiclePort(processModel.getVehiclePort());
    }else if (Objects.equals(attributeChanged, KeCongProcessModel.Attribute.AUTO_RUN.name())) {
      updateAutoRunMark(processModel.getAutoRunMark());
    }else if (Objects.equals(attributeChanged, KeCongProcessModel.Attribute.RobotStatu.name())) {
      updateRobotStatu(processModel.getRobotStatu());
    }
 else if (Objects.equals(attributeChanged, KeCongProcessModel.Attribute.Periodic.name())) {
      updatePeriodic(processModel.getPeriodicEnable());
    } else if (Objects.equals(attributeChanged, KeCongProcessModel.Attribute.NavigateStatu.name())) {
      updateNavigateStatu(processModel.getNavigateStatuResponseModel());
    } else if (Objects.equals(attributeChanged, KeCongProcessModel.Attribute.ReadVarModel.name())) {
      updateReadVar(processModel.getReadVarModel());
    }else if (Objects.equals(attributeChanged, KeCongProcessModel.Attribute.IsConnected.name())) {
      updateIsConnected(processModel.getIsConnected());
    }
  }
 private void updateReadVar(ReadVarModel readVarModel) {
    
     if(readVarModel!=null)
     {   SwingUtilities.invokeLater(() ->
    {
     
    VarValuetx.setText(String.valueOf(DataConvertUtl.toHexString(readVarModel.getValue())));
    VarNametxt.setText(String.valueOf(readVarModel.getName()));
    }   
    );}
  }
  private void updateVehicleProcessModelData(String attributeChanged,
                                             VehicleProcessModelTO processModel) {
    if (Objects.equals(attributeChanged,
                       VehicleProcessModel.Attribute.COMM_ADAPTER_ENABLED.name())) {
      updateCommAdapterEnabled(processModel.isCommAdapterEnabled());
    }  else if (Objects.equals(attributeChanged,
                            VehicleProcessModel.Attribute.POSITION.name())) {
      updatePosition(processModel.getVehiclePosition());
    }
   
  }
  private void updatePosition(String vehiclePosition) {
    SwingUtilities.invokeLater(() -> {
      if (vehiclePosition == null) {
        postioncbx.setSelectedItem("");
        return;
      }
      try {
        for (Point curPoint : callWrapper.call(() -> vehicleService.fetchObjects(Point.class))) {
          if (curPoint.getName().equals(vehiclePosition)) {
            postioncbx.setSelectedItem(curPoint.getName());
            break;
          }
        }
      }
      catch (Exception ex) {
        LOG.warn("Error fetching points", ex);
      }
    });
  }
   private void updateNavigateStatu(NavigateStatuResponseModel navigateStatuResponseModel) {
    
     if(navigateStatuResponseModel!=null)
     {   SwingUtilities.invokeLater(() ->
    {
    NavigateStatu.setText(String.valueOf(navigateStatuResponseModel.getStatu()));
    destinationtxt.setText(String.valueOf( navigateStatuResponseModel.getTargetid()));
    }   
    );}
  }
  private void updatePeriodic(boolean mark) {
    SwingUtilities.invokeLater(() -> periodicEnablecb.setSelected(mark));
  }
  private void updateAutoRunMark(boolean mark) {
    SwingUtilities.invokeLater(() -> autorun.setSelected(mark));
  }
private void updateRobotStatu(RobotStatuResponseModel kcresResponse)
{
  
  if(kcresResponse==null)
    return;
 SwingUtilities.invokeLater(() ->
 {
 KCSAccumulateMiles.setText(String.valueOf(String.format("%.2f",   kcresResponse.getTotalkilometer())));
 KCSPositionX.setText(String.valueOf(kcresResponse.getPositionx()));
 KCSPostionY.setText(String.valueOf(kcresResponse.getPostiony()));
 KCSAngel.setText(String.valueOf(kcresResponse.getPostiontheta()));
KCSBatteryVoltage.setText(String.valueOf(String.format("%.2f",   kcresResponse.getBatterypower())));
KCTemperature.setText(String.valueOf(kcresResponse.getBodytemprature()));
KCSAccumulateMiles.setText(String.valueOf(String.format("%.2f",   kcresResponse.getTotalkilometer())));
KCSCurrentSpeed.setText(String.valueOf(String.format("%.2f",   kcresResponse.getCurvespeed())));
KCSDestinationPoint.setText(String.valueOf(kcresResponse.getCurrenttargetid()));
String nameString=new String(kcresResponse.getCurrentmapname(),StandardCharsets.UTF_8).trim();
KCSCurrentMap.setText(nameString);
KCSMapVersion.setText(String.valueOf(kcresResponse.getMapversion()));
KCSLocationConfidence.setText(String.valueOf(kcresResponse.getConfidenceinterval()));
KCSMapNumber.setText(String.valueOf(kcresResponse.getMapnumber()));
KCSStatus.setText(String.valueOf(kcresResponse.getCurrenttaskstatue()));
KCSTotalTime.setText(String.valueOf(String.format("%.2f",   kcresResponse.getTotalrunningtime())));
KCSLocationState.setText(String.valueOf(kcresResponse.getRunmode()));
 KCSstatus.setText(String.valueOf(kcresResponse.getRunmode()));
 autorun.setSelected(kcresResponse.getRunmode()==1);
 KCSBatteryCurrent.setText(String.valueOf(kcresResponse.getCurrent()));
 KCSCurrentTotalMiles.setText(String.valueOf(String.format("%.2f",kcresResponse.getCurrentrunningtime())));
 });
}
 private void updateVehicleHost(String host) {
    SwingUtilities.invokeLater(() -> IPtxt.setText(host));
  }
 private void updateVehiclePort(int port) {
    SwingUtilities.invokeLater(() -> porttxt.setText(Integer.toString(port)));
  }

 private void updateCommAdapterEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> {
      enablecomadptercb.setSelected(enabled);
       autorun.setEnabled(enabled);
       IPtxt.setEditable(!enabled);
       porttxt.setEditable(!enabled);
       movebt.setEnabled(enabled);
      postioncbx.setEnabled(enabled);
      actioncbx.setEnabled(enabled);
    });
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
    enablecomadptercb = new javax.swing.JCheckBox();
    IsConnected = new javax.swing.JRadioButton();
    jLabel1 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    IPtxt = new javax.swing.JTextField();
    porttxt = new javax.swing.JTextField();
    enablecomadptercb1 = new javax.swing.JCheckBox();
    jPanel2 = new javax.swing.JPanel();
    autorun = new javax.swing.JCheckBox();
    movebt = new javax.swing.JButton();
    postioncbx = new javax.swing.JComboBox<>();
    jLabel2 = new javax.swing.JLabel();
    actioncbx = new javax.swing.JComboBox<>();
    jLabel4 = new javax.swing.JLabel();
    periodicEnablecb = new javax.swing.JCheckBox();
    jPanel3 = new javax.swing.JPanel();
    jLabel7 = new javax.swing.JLabel();
    KCSstatus = new javax.swing.JTextField();
    ww = new javax.swing.JLabel();
    KCTemperature = new javax.swing.JTextField();
    jLabel8 = new javax.swing.JLabel();
    KCSPositionX = new javax.swing.JTextField();
    jLabel9 = new javax.swing.JLabel();
    KCSPostionY = new javax.swing.JTextField();
    jLabel10 = new javax.swing.JLabel();
    KCSAngel = new javax.swing.JTextField();
    jLabel11 = new javax.swing.JLabel();
    KCSDestinationPoint = new javax.swing.JTextField();
    jLabel12 = new javax.swing.JLabel();
    jLabel13 = new javax.swing.JLabel();
    jLabel14 = new javax.swing.JLabel();
    jLabel15 = new javax.swing.JLabel();
    jLabel16 = new javax.swing.JLabel();
    jLabel17 = new javax.swing.JLabel();
    jLabel18 = new javax.swing.JLabel();
    jLabel19 = new javax.swing.JLabel();
    KCSBatteryVoltage = new javax.swing.JTextField();
    KCSBatteryCurrent = new javax.swing.JTextField();
    KCSStatus = new javax.swing.JTextField();
    KCSMapVersion = new javax.swing.JTextField();
    KCSAccumulateMiles = new javax.swing.JTextField();
    KCSCurrentTotalMiles = new javax.swing.JTextField();
    KCSTotalTime = new javax.swing.JTextField();
    KCSCurrentSpeed = new javax.swing.JTextField();
    jLabel20 = new javax.swing.JLabel();
    KCSLocationState = new javax.swing.JTextField();
    jLabel21 = new javax.swing.JLabel();
    KCSCurrentMap = new javax.swing.JTextField();
    jLabel22 = new javax.swing.JLabel();
    KCSLocationConfidence = new javax.swing.JTextField();
    jLabel23 = new javax.swing.JLabel();
    KCSMapNumber = new javax.swing.JTextField();
    jPanel4 = new javax.swing.JPanel();
    jLabel5 = new javax.swing.JLabel();
    NavigateStatu = new javax.swing.JTextField();
    jLabel6 = new javax.swing.JLabel();
    destinationtxt = new javax.swing.JTextField();
    jPanel5 = new javax.swing.JPanel();
    jLabel24 = new javax.swing.JLabel();
    VarNamecb = new javax.swing.JComboBox<>();
    VarValuetx = new javax.swing.JTextField();
    ReadVarbt = new javax.swing.JButton();
    WriteVarbt = new javax.swing.JButton();
    VarNametxt = new javax.swing.JTextField();
    VarTypecb = new javax.swing.JComboBox<>();
    jLabel26 = new javax.swing.JLabel();
    jPanel6 = new javax.swing.JPanel();
    liftcontrolbt = new javax.swing.JButton();
    liftdistance = new javax.swing.JTextField();
    jButton1 = new javax.swing.JButton();

    setPreferredSize(new java.awt.Dimension(1000, 800));

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("通讯"));

    enablecomadptercb.setText("通讯使能");
    enablecomadptercb.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        enablecomadptercbActionPerformed(evt);
      }
    });

    IsConnected.setText("连接");

    jLabel1.setText("IP:");

    jLabel3.setText("Port:");

    IPtxt.setToolTipText("");

    porttxt.setToolTipText("");

    enablecomadptercb1.setText("通讯使能");
    enablecomadptercb1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        enablecomadptercb1ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(enablecomadptercb)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel1)
            .addGap(18, 18, 18)
            .addComponent(IPtxt))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(IsConnected)
            .addGap(24, 24, 24)
            .addComponent(jLabel3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(porttxt))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(enablecomadptercb1)
            .addGap(196, 196, 196)))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(enablecomadptercb)
          .addComponent(jLabel1)
          .addComponent(IPtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(enablecomadptercb1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(IsConnected)
          .addComponent(jLabel3)
          .addComponent(porttxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(0, 0, Short.MAX_VALUE))
    );

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("命令"));

    autorun.setText("自动运行");
    autorun.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        autorunActionPerformed(evt);
      }
    });

    movebt.setText("移动");
    movebt.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        movebtActionPerformed(evt);
      }
    });

    jLabel2.setText("位置：");

    jLabel4.setText("动作：");

    periodicEnablecb.setText("周期性状态询问");
    periodicEnablecb.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        periodicEnablecbActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 0, Short.MAX_VALUE)))
            .addGap(46, 46, 46))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
            .addGap(0, 17, Short.MAX_VALUE)
            .addComponent(periodicEnablecb)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(postioncbx, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(actioncbx, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(110, 110, 110))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
            .addComponent(autorun, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(movebt)
            .addGap(21, 21, 21))))
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addGap(4, 4, 4)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(postioncbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(actioncbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(movebt)
          .addComponent(autorun)
          .addComponent(periodicEnablecb))
        .addContainerGap(31, Short.MAX_VALUE))
    );

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("运行状态"));

    jLabel7.setText("状态：");

    ww.setText("温度：");

    jLabel8.setText("X:");

    KCSPositionX.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        KCSPositionXActionPerformed(evt);
      }
    });

    jLabel9.setText("Y:");

    jLabel10.setText("角度：");
    jLabel10.setToolTipText("");

    jLabel11.setText("目标点：");

    jLabel12.setText("前进速度：");

    jLabel13.setText("电池电压：");

    jLabel14.setText("电池电流：");
    jLabel14.setToolTipText("");

    jLabel15.setText("任务状态：");

    jLabel16.setText("地图版本：");

    jLabel17.setText("累计行程：");

    jLabel18.setText("当前累计：");

    jLabel19.setText("累计时间：");

    jLabel20.setText("定位状态：");

    jLabel21.setText("当前地图：");

    jLabel22.setText("定位置信度：");

    KCSLocationConfidence.setHorizontalAlignment(javax.swing.JTextField.LEFT);

    jLabel23.setText("地图数量：");

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addGap(21, 21, 21)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel7)
            .addGap(18, 18, 18)
            .addComponent(KCSstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(ww)
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(jLabel9)
                  .addComponent(jLabel8))))
            .addGap(18, 18, 18)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(KCTemperature)
              .addComponent(KCSPositionX)
              .addComponent(KCSPostionY)))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel10)
            .addGap(18, 18, 18)
            .addComponent(KCSAngel))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel11)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(KCSDestinationPoint)))
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGap(27, 27, 27)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(KCSBatteryCurrent))
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(KCSBatteryVoltage, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGap(18, 18, 18)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addGap(18, 18, 18)
                .addComponent(KCSMapVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addComponent(KCSAccumulateMiles, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addComponent(KCSStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(KCSCurrentSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(1, 1, 1)))
        .addGap(34, 34, 34)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jLabel18)
          .addComponent(jLabel19)
          .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel20)
            .addComponent(jLabel21))
          .addComponent(jLabel22)
          .addComponent(jLabel23))
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
              .addGap(29, 29, 29)
              .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(KCSLocationState)
                .addComponent(KCSCurrentMap)
                .addComponent(KCSLocationConfidence)
                .addComponent(KCSMapNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
              .addGap(28, 28, 28)
              .addComponent(KCSTotalTime, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addGap(28, 28, 28)
            .addComponent(KCSCurrentTotalMiles, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(47, Short.MAX_VALUE))
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel7)
              .addComponent(KCSstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel13)
              .addComponent(KCSBatteryVoltage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel20)
              .addComponent(KCSLocationState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(ww)
              .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(KCTemperature, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel14)
                .addComponent(KCSBatteryCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel21)
                .addComponent(KCSCurrentMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(KCSPositionX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel22)
              .addComponent(KCSLocationConfidence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel12)
              .addComponent(KCSCurrentSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addComponent(jLabel8))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(KCSPostionY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel23)
            .addComponent(KCSMapNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(KCSStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jLabel9)
          .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel10)
                .addComponent(KCSAngel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(KCSMapVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel11)
              .addComponent(KCSDestinationPoint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(KCSAccumulateMiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel17)))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(KCSTotalTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel19))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(KCSCurrentTotalMiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel18))))
        .addGap(0, 0, Short.MAX_VALUE))
    );

    jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("导航状态"));

    jLabel5.setText("导航状态：");
    jLabel5.setToolTipText("");

    jLabel6.setText("目标点：");

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel5)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(NavigateStatu, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel6)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(destinationtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel5)
          .addComponent(NavigateStatu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel6)
          .addComponent(destinationtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
    );

    jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("变量操作"));

    jLabel24.setText("变量名字：");

    ReadVarbt.setText("读取变量");
    ReadVarbt.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        ReadVarbtActionPerformed(evt);
      }
    });

    WriteVarbt.setText("写入变量");
    WriteVarbt.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        WriteVarbtActionPerformed(evt);
      }
    });

    VarTypecb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BOOL", "BYTE", "WORD", "DWORD", "SINT", "INT", "USINT", "DINT", "UINT", "REAL", " ", " ", " ", " " }));

    jLabel26.setText("变量类型：");

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
      jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel5Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel5Layout.createSequentialGroup()
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(WriteVarbt)
                .addGap(18, 18, 18)
                .addComponent(VarValuetx, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(ReadVarbt)
                .addGap(18, 18, 18)
                .addComponent(VarNametxt)))
            .addContainerGap(16, Short.MAX_VALUE))
          .addGroup(jPanel5Layout.createSequentialGroup()
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(27, 27, 27)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(VarTypecb, 0, 131, Short.MAX_VALUE)
              .addComponent(VarNamecb, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(0, 0, Short.MAX_VALUE))))
    );
    jPanel5Layout.setVerticalGroup(
      jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel5Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel24)
          .addComponent(VarNamecb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addGroup(jPanel5Layout.createSequentialGroup()
            .addGap(4, 4, 4)
            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addComponent(VarTypecb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(26, 26, 26)
        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(WriteVarbt)
          .addComponent(VarValuetx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(ReadVarbt)
          .addComponent(VarNametxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("货架控制"));

    liftcontrolbt.setText("升降控制");
    liftcontrolbt.setToolTipText("");
    liftcontrolbt.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        liftcontrolbtActionPerformed(evt);
      }
    });

    liftdistance.setToolTipText("值为0，则为下降，为1，则上升");

    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
      jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(liftdistance)
        .addGap(18, 18, 18)
        .addComponent(liftcontrolbt)
        .addGap(28, 28, 28))
    );
    jPanel6Layout.setVerticalGroup(
      jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel6Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(liftcontrolbt)
          .addComponent(liftdistance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jButton1.setText("jButton1");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                  .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
          .addGroup(layout.createSequentialGroup()
            .addGap(66, 66, 66)
            .addComponent(jButton1)))
        .addGap(0, 0, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
        .addComponent(jButton1)
        .addGap(93, 93, 93))
    );

    getAccessibleContext().setAccessibleName("科聪控制器显示页面");
  }// </editor-fold>//GEN-END:initComponents

  private void KCSPositionXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KCSPositionXActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_KCSPositionXActionPerformed

  private void movebtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_movebtActionPerformed

     Object selectedItem = postioncbx.getSelectedItem();
     Object  selectaction=actioncbx.getSelectedItem();
    NavigationControlState navigationControlState=    Enum.valueOf(NavigationControlState.class, selectaction.toString());
   int operation=navigationControlState.ordinal();
    String destinationIdString = selectedItem instanceof Point
        ? ((Point) selectedItem).getName() : selectedItem.toString();
    int destinationid=new  OpentcsPointToKeCongPoint(destinationIdString).getIntPoint(); 
   sendAdapterCommand(new SendNavigationRequest(String.valueOf(destinationid),(byte)operation,(byte)0));    // TODO add your handling code here:

  }//GEN-LAST:event_movebtActionPerformed

  public  enum  NavigationControlState
  {Start,
   Cancel,
    Pause,
    Continue,
    
  
  
  
  }
  
  private void enablecomadptercbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enablecomadptercbActionPerformed
    // TODO add your handling code here:
    enableCommAdapter(enablecomadptercb.isSelected());
  }//GEN-LAST:event_enablecomadptercbActionPerformed

  private void autorunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autorunActionPerformed

    byte auto=autorun.isSelected()?(byte)1:(byte)0;
    if(autorun.isSelected())
    {
      sendAdapterCommand(new SendComandMakeSurePoint());
    }
     sendAdapterCommand(new SendComandAutoRequest(auto));
 

    // TODO add your handling code here:
  }//GEN-LAST:event_autorunActionPerformed

  private void periodicEnablecbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_periodicEnablecbActionPerformed
    // TODO add your handling code here:
    sendAdapterCommand(new SendComandPeriodicStateRequestEnabled(periodicEnablecb.isSelected()));
  }//GEN-LAST:event_periodicEnablecbActionPerformed

  private void WriteVarbtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WriteVarbtActionPerformed
    // TODO add your handling code here:
  Object object=  VarNamecb.getSelectedItem();
  String name =object.toString();
  String vaString=VarValuetx.getText();
 // byte [] value=  vaString.getBytes(StandardCharsets.UTF_8);
  byte [] value=parsedata(VarTypecb.getSelectedItem().toString(),vaString);
   SendComandWriteVar sendComandWriteVar=new SendComandWriteVar(name, value);
   sendAdapterCommand(sendComandWriteVar);
  }//GEN-LAST:event_WriteVarbtActionPerformed
private byte [] parsedata(String vartype,String value)
{
byte[] datavalue=null;
switch(vartype)
{
  case  "BOOL":
   datavalue=new byte[1];
   datavalue[0]=Byte.parseByte(value);
  break;
  case "REAL":
    datavalue=new byte[4];
    float d=Float.parseFloat(value);
   datavalue =DataConvertUtl.getBytes(d);
    break;
  default: 
    break;
}
return  datavalue;
}
  private void ReadVarbtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReadVarbtActionPerformed
    // TODO add your handling code here:
     Object object=  VarNamecb.getSelectedItem();
     String name=object.toString();
     
     SendComanReadVar sendComanReadVar=new SendComanReadVar(name);
     sendAdapterCommand(sendComanReadVar);
  }//GEN-LAST:event_ReadVarbtActionPerformed

  private void liftcontrolbtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_liftcontrolbtActionPerformed
    // TODO add your handling code here:
       byte[]varvalue=new byte[4];
       if("".equals(liftdistance.getText()))
       {return;
       }else
       {
         varvalue=DataConvertUtl.getBytes(Float.parseFloat(liftdistance.getText()));
       }
        SendComandWriteVar sendComandWriteVar=new SendComandWriteVar(KeCongActionVar.PID_ENABLE_STRING, new byte[]{ Byte.parseByte("1")});
   sendAdapterCommand(sendComandWriteVar);
    SendComandWriteVar sendComandWriteVar1=new SendComandWriteVar(KeCongActionVar.LIFT_SV, varvalue);
   sendAdapterCommand(sendComandWriteVar1);
    SendComandWriteVar sendComandWriteVar2=new SendComandWriteVar(KeCongActionVar.FINSHI_TASK, new byte[]{ Byte.parseByte("0")});
   sendAdapterCommand(sendComandWriteVar2);
  }//GEN-LAST:event_liftcontrolbtActionPerformed

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    // TODO add your handling code here:
    sendAdapterCommand(new SendRequestCommand());
  }//GEN-LAST:event_jButton1ActionPerformed

  private void enablecomadptercb1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enablecomadptercb1ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_enablecomadptercb1ActionPerformed
  private void enableCommAdapter(boolean enable) {
    try {
      if (enable) {
        callWrapper.call(() -> vehicleService.enableCommAdapter(processModel.getVehicleRef()));
      }
      else {
        callWrapper.call(() -> vehicleService.disableCommAdapter(processModel.getVehicleRef()));
      }
    }
    catch (Exception ex) {
      LOG.warn("Error enabling/disabling comm adapter", ex);
    }
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JTextField IPtxt;
  private javax.swing.JRadioButton IsConnected;
  private javax.swing.JTextField KCSAccumulateMiles;
  private javax.swing.JTextField KCSAngel;
  private javax.swing.JTextField KCSBatteryCurrent;
  private javax.swing.JTextField KCSBatteryVoltage;
  private javax.swing.JTextField KCSCurrentMap;
  private javax.swing.JTextField KCSCurrentSpeed;
  private javax.swing.JTextField KCSCurrentTotalMiles;
  private javax.swing.JTextField KCSDestinationPoint;
  private javax.swing.JTextField KCSLocationConfidence;
  private javax.swing.JTextField KCSLocationState;
  private javax.swing.JTextField KCSMapNumber;
  private javax.swing.JTextField KCSMapVersion;
  private javax.swing.JTextField KCSPositionX;
  private javax.swing.JTextField KCSPostionY;
  private javax.swing.JTextField KCSStatus;
  private javax.swing.JTextField KCSTotalTime;
  private javax.swing.JTextField KCSstatus;
  private javax.swing.JTextField KCTemperature;
  private javax.swing.JTextField NavigateStatu;
  private javax.swing.JButton ReadVarbt;
  private javax.swing.JComboBox<String> VarNamecb;
  private javax.swing.JTextField VarNametxt;
  private javax.swing.JComboBox<String> VarTypecb;
  private javax.swing.JTextField VarValuetx;
  private javax.swing.JButton WriteVarbt;
  private javax.swing.JComboBox<String> actioncbx;
  private javax.swing.JCheckBox autorun;
  private javax.swing.JTextField destinationtxt;
  private javax.swing.JCheckBox enablecomadptercb;
  private javax.swing.JCheckBox enablecomadptercb1;
  private javax.swing.JButton jButton1;
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
  private javax.swing.JLabel jLabel26;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
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
  private javax.swing.JButton liftcontrolbt;
  private javax.swing.JTextField liftdistance;
  private javax.swing.JButton movebt;
  private javax.swing.JCheckBox periodicEnablecb;
  private javax.swing.JTextField porttxt;
  private javax.swing.JComboBox<String> postioncbx;
  private javax.swing.JLabel ww;
  // End of variables declaration//GEN-END:variables

  private void updateIsConnected(boolean isconnected) {
      SwingUtilities.invokeLater(() -> IsConnected.setSelected(isconnected));
  }
}

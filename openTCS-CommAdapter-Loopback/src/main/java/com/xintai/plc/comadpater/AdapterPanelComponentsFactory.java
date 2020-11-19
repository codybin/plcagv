/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.comadpater;

/**
 *
 * @author Lenovo
 */
/**
 * Copyright (c) Fraunhofer IML
 */

import com.xintai.plc.ui.PLCControlForm;
import org.opentcs.components.kernel.services.VehicleService;

/**
 * A factory for creating various comm adapter panel specific instances.
 *创建多个通讯适配器的工厂
 * @author Martin Grzenia (Fraunhofer IML)
 */
public interface AdapterPanelComponentsFactory {

  /**
   * Creates a {@link ControlPanel} representing the given process model's content.
   *
   * @param processModel The process model to represent.
   * @param vehicleService The vehicle service used for interaction with the comm adapter.
   * @return The control panel.
   */
 // ControlPanel createControlPanel(ExampleProcessModelTO processModel,
   //                               VehicleService vehicleService);
  
  /**
   * Creates a {@link StatusPanel} representing the given process model's content.
   *创建代表代表给定的模型内容的状态pannel
   * @param processModel The process model to represent.
   * @param vehicleService The vehicle service used for interaction with the comm adapter.
   * @return The status panel.
   */
  //StatusPanel createStatusPanel(ExampleProcessModelTO processModel,
    //                            VehicleService vehicleService);
  
  /**
   *
   * @param processModel
   * @param vehicleService
   * @return
   */
  //Vehicle createVehilePanel(ExampleProcessModelTO processModel,
                       //           VehicleService vehicleService);
  /**
   * @param processModel
   * @param vehicleService
   * @return
   */
   PLCControlForm createPLCControlFormPanel(PLCProcessModelTO processModel,
                                  VehicleService vehicleService);
   
}

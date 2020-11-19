/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opentcs.kernelcontrolcenter;



/**
 *
 * @author Lenovo
 */
/**
 * Copyright (c) Fraunhofer IML
 */

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.xintai.plc.comadpater.AdapterPanelComponentsFactory;
import com.xintai.plc.comadpater.PLCCommAdapterPanelFactory;


import org.opentcs.customizations.controlcenter.ControlCenterInjectionModule;

/**
 * A custom Guice module for project-specific configuration.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class PLCControlCenterInjectionModule
    extends ControlCenterInjectionModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(AdapterPanelComponentsFactory.class));

    commAdapterPanelFactoryBinder().addBinding().to(PLCCommAdapterPanelFactory.class);
  }
}

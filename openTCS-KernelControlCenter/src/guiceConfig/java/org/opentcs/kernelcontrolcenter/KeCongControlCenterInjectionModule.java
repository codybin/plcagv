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
import com.xintai.vehicle.comadpter.AdapterPanelComponentsFactory;
import com.xintai.vehicle.comadpter.KeCongCommAdapterPanelFactory;

import org.opentcs.customizations.controlcenter.ControlCenterInjectionModule;

/**
 * A custom Guice module for project-specific configuration.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class KeCongControlCenterInjectionModule
    extends ControlCenterInjectionModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(AdapterPanelComponentsFactory.class));

    commAdapterPanelFactoryBinder().addBinding().to(KeCongCommAdapterPanelFactory.class);
  }
}

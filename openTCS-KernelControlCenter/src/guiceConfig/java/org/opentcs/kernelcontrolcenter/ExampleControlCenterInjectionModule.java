/**
 * Copyright (c) Fraunhofer IML
 */
package org.opentcs.kernelcontrolcenter;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import example.virtualvehicle.ExampleCommAdapterPanelFactory;

import org.opentcs.customizations.controlcenter.ControlCenterInjectionModule;
import example.virtualvehicle.AdapterPanelComponentsFactory;

/**
 * A custom Guice module for project-specific configuration.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class ExampleControlCenterInjectionModule
    extends ControlCenterInjectionModule {

  @Override
  protected void configure() {
   // install(new FactoryModuleBuilder().build(AdapterPanelComponentsFactory.class));

    commAdapterPanelFactoryBinder().addBinding().to(ExampleCommAdapterPanelFactory.class);
  }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opentcs.virtualvehicle;




import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.xintai.vehicle.comadpter.KeCongAdapterComponentsFactory;
import com.xintai.vehicle.comadpter.KeCongCommAdapterConfiguration;
import com.xintai.vehicle.comadpter.KeCongCommAdapterFactory;
import example.virtualvehicle.ExampleCommAdapterConfiguration;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeCongKernelInjectionModule
    extends KernelInjectionModule {
  
  private static final Logger LOG = LoggerFactory.getLogger(ExampleKernelInjectionModule.class);

  @Override
  protected void configure() {
    
    KeCongCommAdapterConfiguration configuration
        = getConfigBindingProvider().get(KeCongCommAdapterConfiguration.PREFIX,
                                         KeCongCommAdapterConfiguration.class);
    
     if (!configuration.enable()) {
    LOG.info("Example communication adapter disabled by configuration.");
    return;
    }
    
    install(new FactoryModuleBuilder().build(KeCongAdapterComponentsFactory.class));
    vehicleCommAdaptersBinder().addBinding().to(KeCongCommAdapterFactory.class);
  }
}


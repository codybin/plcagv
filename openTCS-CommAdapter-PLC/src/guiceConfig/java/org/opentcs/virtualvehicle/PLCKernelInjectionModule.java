/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opentcs.virtualvehicle;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.xintai.plc.comadpater.PLCAdapterComponentsFactory;
import com.xintai.plc.comadpater.PLCCommAdapterConfiguration;
import com.xintai.plc.comadpater.PLCCommAdapterFactory;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class PLCKernelInjectionModule
    extends KernelInjectionModule {
  
  private static final Logger LOG = LoggerFactory.getLogger(PLCKernelInjectionModule.class);

  @Override
  protected void configure() {
    
PLCCommAdapterConfiguration configuration
        = getConfigBindingProvider().get(PLCCommAdapterConfiguration.PREFIX,
                                         PLCCommAdapterConfiguration.class);
    
     if (!configuration.enable()) {
    LOG.info("Example communication adapter disabled by configuration.");
    return;
    }
    
    install(new FactoryModuleBuilder().build(PLCAdapterComponentsFactory.class));
    vehicleCommAdaptersBinder().addBinding().to(PLCCommAdapterFactory.class);
  }
}
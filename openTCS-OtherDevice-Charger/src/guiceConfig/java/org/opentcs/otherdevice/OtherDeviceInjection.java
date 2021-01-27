
package org.opentcs.otherdevice;
import com.xintai.shunli.OtherDeviceConfiguration;
import com.xintai.shunli.device_shunlicontroller;
import javax.inject.Singleton;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtherDeviceInjection
    extends KernelInjectionModule {
  
  private static final Logger LOG = LoggerFactory.getLogger(OtherDeviceInjection.class);

  @Override
  protected void configure() {
    OtherDeviceConfiguration configuration=getConfigBindingProvider().get(OtherDeviceConfiguration.PREFIX,
    OtherDeviceConfiguration.class);
       if (!configuration.enable()) {
    LOG.info("shunli_plc communication disable .");
    return;
    }
    bind(OtherDeviceConfiguration.class).toInstance(configuration);
    bind(device_shunlicontroller.class).in(Singleton.class);   
  }
}



package org.opentcs.otherdevice;
import com.statemachine.PLCExecutor;
import com.xintai.device.DestinationLocationService;
import com.xintai.shunli.OtherDeviceConfiguration;
import com.xintai.shunli.device_shunlicontroller;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Singleton;
import org.opentcs.common.LoggingScheduledThreadPoolExecutor;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.opentcs.util.logging.UncaughtExceptionLogger;
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
       ScheduledExecutorService executor1
        = new LoggingScheduledThreadPoolExecutor(
            1,
            (runnable) -> {
              Thread thread = new Thread(runnable, "kernelExecutor");
              thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(false));
              return thread;
            }
        );
         bind(ExecutorService.class)
        .annotatedWith(PLCExecutor.class)
        .toInstance(executor1);
    bind(OtherDeviceConfiguration.class).toInstance(configuration);
    bind(device_shunlicontroller.class).in(Singleton.class);  
    bind(DestinationLocationService.class).in(Singleton.class);  
  }
}


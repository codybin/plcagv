/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.virtualvehicle;



import org.opentcs.configuration.ConfigurationEntry;
import org.opentcs.configuration.ConfigurationPrefix;

/**
 * Provides methods to configure the {@link ExampleCommAdapter}.
 *提供配置exampleComadapter的方法
 * @author Leonard Schuengel (Fraunhofer IML)
 */
@ConfigurationPrefix(ExampleCommAdapterConfiguration.PREFIX)
public interface ExampleCommAdapterConfiguration {

  /**
   * This configuration's prefix.
   */
  String PREFIX = "example.commadapter";

  @ConfigurationEntry(
      type = "Boolean",
      description = "Whether to register/enable the example communication adapter.",
      orderKey = "0_enable")
  boolean enable();

}


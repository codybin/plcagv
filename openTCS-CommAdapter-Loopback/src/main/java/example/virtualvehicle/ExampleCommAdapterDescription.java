/**
 * Copyright (c) Fraunhofer IML
 */
package example.virtualvehicle;

import java.util.ResourceBundle;
import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;

/**
 * The comm adapter's {@link VehicleCommAdapterDescription}.
 *  ≈‰∆˜µƒ√Ë ˆ
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class ExampleCommAdapterDescription
    extends VehicleCommAdapterDescription {

  @Override
  public String getDescription() {
    return  ResourceBundle.getBundle("de/fraunhofer/iml/opentcs/example/commadapter/vehicle/Bundle").
  getString("ExampleAdapterFactoryDescription");
  }/* ResourceBundle.getBundle("de/fraunhofer/iml/opentcs/example/commadapter/vehicle/Bundle").
  getString("ExampleAdapterFactoryDescription");*/

  @Override
  public boolean isSimVehicleCommAdapter() {
    return false;
  }
}

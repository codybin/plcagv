/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.vehicle.comadpter;


import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.opentcs.access.KernelServicePortal;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;
import org.opentcs.drivers.vehicle.management.VehicleCommAdapterPanel;
import org.opentcs.drivers.vehicle.management.VehicleCommAdapterPanelFactory;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KeCongCommAdapterPanelFactory
    implements VehicleCommAdapterPanelFactory {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(KeCongCommAdapterPanelFactory.class);
  /**
   * The service portal.
   */
  private final KernelServicePortal servicePortal;
  /**
   * The components factory.
   */
  private final AdapterPanelComponentsFactory componentsFactory;
  /**
   * Whether this factory is initialized or not.
   */
  private boolean initialized;

  /**
   * Creates a new instance.
   *
   * @param servicePortal The service portal.
   * @param componentsFactory The components factory.
   */
  @Inject
  public KeCongCommAdapterPanelFactory(KernelServicePortal servicePortal,
                                        AdapterPanelComponentsFactory componentsFactory) {
    this.servicePortal = requireNonNull(servicePortal, "servicePortal");
    this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");
  }

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }

    initialized = true;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void terminate() {
    if (!isInitialized()) {
      return;
    }

    initialized = false;
  }
//添加面板
  @Override
  public List<VehicleCommAdapterPanel> getPanelsFor(
      @Nonnull VehicleCommAdapterDescription description,
      @Nonnull TCSObjectReference<Vehicle> vehicle,
      @Nonnull VehicleProcessModelTO processModel) {
    requireNonNull(description, "description");
    requireNonNull(vehicle, "vehicle");
    requireNonNull(processModel, "processModel");

    if (!providesPanelsFor(description, processModel)) {
      LOG.debug("Cannot provide panels for '{}' with '{}'.", description, processModel);
      return new ArrayList<>();
    }
    List<VehicleCommAdapterPanel> panels = new ArrayList<>();
  //向控制器添加界面的逻辑在此
    panels.add(componentsFactory.createKeCongControlFormPanel((KeCongProcessModelTO) processModel,
                                              servicePortal.getVehicleService()));
    panels.add(componentsFactory.createOrderCreateFormFormPanel((KeCongProcessModelTO) processModel,servicePortal,servicePortal.getVehicleService()));
    return panels;
  }

  /**
   * Checks whether this factory can provide comm adapter panels for the given description and the
   * given type of process model.
   *
   * @param description The description to check for.
   * @param processModel The process model.
   * @return {@code true} if, and only if, this factory can provide comm adapter panels for the
   * given description and the given type of process model.
   */
  private boolean providesPanelsFor(VehicleCommAdapterDescription description,
                                    VehicleProcessModelTO processModel) {
    return (description instanceof KeCongCommAdapterDescription)
        && (processModel instanceof KeCongProcessModelTO);
  }
}

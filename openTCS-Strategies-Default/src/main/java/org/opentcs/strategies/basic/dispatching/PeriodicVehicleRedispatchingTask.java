/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.strategies.basic.dispatching;

import static java.util.Objects.requireNonNull;
import javax.inject.Inject;
import org.opentcs.components.kernel.services.DispatcherService;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.TransportOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Periodically checks for idle vehicles that could process a transport order.
 * The main purpose of doing this is retrying to dispatch vehicles that were not in a dispatchable
 * state when dispatching them was last tried.
 * A potential reason for this is that a vehicle temporarily reported an error because a safety
 * sensor was triggered.
 *周期性检测可以处理订单的空闲车辆
 * 做这个 的主要目的是重新分车辆（在不可分配状态）.一个潜在的原因是，车辆可能暂时报告一个错误，因为安全传感器被触发
 * @author Stefan Walter (Fraunhofer IML)
 */
public class PeriodicVehicleRedispatchingTask
    implements Runnable {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(PeriodicVehicleRedispatchingTask.class);

  private final DispatcherService dispatcherService;

  private final TCSObjectService objectService;

  /**
   * Creates a new instance.
   *
   * @param dispatcherService The dispatcher service used to dispatch vehicles.
   * @param objectService The object service.
   */
  @Inject
  public PeriodicVehicleRedispatchingTask(DispatcherService dispatcherService,
                                          TCSObjectService objectService) {
    this.dispatcherService = requireNonNull(dispatcherService, "dispatcherService");
    this.objectService = requireNonNull(objectService, "objectService");
  }

  @Override
  public void run() {
    // If there are any vehicles that could process a transport order,
    // trigger the dispatcher once.
    objectService.fetchObjects(Vehicle.class, this::couldProcessTransportOrder).stream()
        .findAny()
        .ifPresent(vehicle -> {
          LOG.debug("Vehicle {} could process transport order, triggering dispatcher ...", vehicle);
          dispatcherService.dispatch();
        });
  }
///车辆的集成状态是在可以利用的转态，车辆的当前位置不为空。并且车辆有能量，车辆没有处理订单，车辆可以处理可分配的订单
  
  private boolean couldProcessTransportOrder(Vehicle vehicle) {
    return vehicle.getIntegrationLevel() == Vehicle.IntegrationLevel.TO_BE_UTILIZED
        && vehicle.getCurrentPosition() != null
        && !vehicle.isEnergyLevelCritical()
        && (processesNoOrder(vehicle)
            || processesDispensableOrder(vehicle));
  }

  private boolean processesNoOrder(Vehicle vehicle) {
    return vehicle.hasProcState(Vehicle.ProcState.IDLE)
        && (vehicle.hasState(Vehicle.State.IDLE)
            || vehicle.hasState(Vehicle.State.CHARGING));
  }

  private boolean processesDispensableOrder(Vehicle vehicle) {
    return vehicle.hasProcState(Vehicle.ProcState.PROCESSING_ORDER)
        && objectService.fetchObject(TransportOrder.class, vehicle.getTransportOrder())
            .isDispensable();
  }
}

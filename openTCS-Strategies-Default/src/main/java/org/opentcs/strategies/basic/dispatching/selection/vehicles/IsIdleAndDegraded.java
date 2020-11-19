/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.strategies.basic.dispatching.selection.vehicles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.opentcs.data.model.Vehicle;
import org.opentcs.strategies.basic.dispatching.selection.RechargeVehicleSelectionFilter;

/**
 * Filters vehicles that are idle and have a degraded energy level.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class IsIdleAndDegraded
    implements RechargeVehicleSelectionFilter {

  @Override
  public Collection<String> apply(Vehicle vehicle) {
    return idleAndDegraded(vehicle) ? new ArrayList<>() : Arrays.asList(getClass().getName());
  }
//车辆充电的条件1：必须是接收订单状态2：先前状态必须是空闲状态；3：当前状态是空闲状态；4:：当前位置不为空；5：订单序列号为空；6：能量水平小于能量的临界水平。
  private boolean idleAndDegraded(Vehicle vehicle) {
    return vehicle.getIntegrationLevel() == Vehicle.IntegrationLevel.TO_BE_UTILIZED
        && vehicle.hasProcState(Vehicle.ProcState.IDLE)
        && vehicle.hasState(Vehicle.State.IDLE)
        && vehicle.getCurrentPosition() != null
        && vehicle.getOrderSequence() == null
        && vehicle.isEnergyLevelDegraded();
  }
}

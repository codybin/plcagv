/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.strategies.basic.dispatching.phase.recharging;

import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import javax.inject.Inject;
import org.opentcs.access.to.order.DestinationCreationTO;
import org.opentcs.access.to.order.TransportOrderCreationTO;
import org.opentcs.components.kernel.Router;
import org.opentcs.components.kernel.services.InternalTransportOrderService;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.strategies.basic.dispatching.AssignmentCandidate;
import org.opentcs.strategies.basic.dispatching.DefaultDispatcherConfiguration;
import org.opentcs.strategies.basic.dispatching.Phase;
import org.opentcs.strategies.basic.dispatching.TransportOrderUtil;
import org.opentcs.strategies.basic.dispatching.selection.candidates.CompositeAssignmentCandidateSelectionFilter;
import org.opentcs.strategies.basic.dispatching.selection.vehicles.CompositeRechargeVehicleSelectionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates recharging orders for any vehicles with a degraded energy level.
 *为任何没有电的叉车创建充电订单
 * @author Stefan Walter (Fraunhofer IML)
 */
public class RechargeIdleVehiclesPhase
    implements Phase {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(RechargeIdleVehiclesPhase.class);
  /**
   * The transport order service.
   */
  private final InternalTransportOrderService orderService;
  /**
   * The strategy used for finding suitable recharge locations.
   * 找到合适的充电位置的充电策略
   */
  private final RechargePositionSupplier rechargePosSupplier;
  /**
   * The Router instance calculating route costs.
   * 计算路线的路线实例
   */
  private final Router router;
  /**
   * A collection of predicates for filtering assignment candidates.
   * 过滤签约订单的检测集合
   */
  private final CompositeAssignmentCandidateSelectionFilter assignmentCandidateSelectionFilter;

  private final CompositeRechargeVehicleSelectionFilter vehicleSelectionFilter;

  private final TransportOrderUtil transportOrderUtil;
  /**
   * The dispatcher configuration.
   * 分配者的配置
   */
  private final DefaultDispatcherConfiguration configuration;
  /**
   * Indicates whether this component is initialized.
   */
  private boolean initialized;

  @Inject
  public RechargeIdleVehiclesPhase(
      InternalTransportOrderService orderService,
      RechargePositionSupplier rechargePosSupplier,
      Router router,
      CompositeAssignmentCandidateSelectionFilter assignmentCandidateSelectionFilter,
      CompositeRechargeVehicleSelectionFilter vehicleSelectionFilter,
      TransportOrderUtil transportOrderUtil,
      DefaultDispatcherConfiguration configuration) {
    this.router = requireNonNull(router, "router");
    this.orderService = requireNonNull(orderService, "orderService");
    this.rechargePosSupplier = requireNonNull(rechargePosSupplier, "rechargePosSupplier");
    this.assignmentCandidateSelectionFilter = requireNonNull(assignmentCandidateSelectionFilter,
                                                             "assignmentCandidateSelectionFilter");
    this.vehicleSelectionFilter = requireNonNull(vehicleSelectionFilter, "vehicleSelectionFilter");
    this.transportOrderUtil = requireNonNull(transportOrderUtil, "transportOrderUtil");
    this.configuration = requireNonNull(configuration, "configuration");
  }

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }

    rechargePosSupplier.initialize();

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

    rechargePosSupplier.terminate();

    initialized = false;
  }

  @Override
  public void run() {
    //如果没有配置则不会进行执行
    if (!configuration.rechargeIdleVehicles()) {
      return;
    }

    orderService.fetchObjects(Vehicle.class).stream()
        .filter(vehicle -> vehicleSelectionFilter.apply(vehicle).isEmpty())
        .forEach(vehicle -> createRechargeOrder(vehicle));
  }
//创建充电订单
 //1、先找到充电目的地。
  //2.根据目的地，创建订单
  private void createRechargeOrder(Vehicle vehicle) {
    List<DriveOrder.Destination> rechargeDests = rechargePosSupplier.findRechargeSequence(vehicle);
    LOG.debug("Recharge sequence for {}: {}", vehicle, rechargeDests);

    if (rechargeDests.isEmpty()) {
      LOG.info("{}: Did not find a suitable recharge sequence.", vehicle.getName());
      return;
    }

    List<DestinationCreationTO> chargeDests = new ArrayList<>(rechargeDests.size());
    for (DriveOrder.Destination dest : rechargeDests) {
      chargeDests.add(
          new DestinationCreationTO(dest.getDestination().getName(), dest.getOperation())
              .withProperties(dest.getProperties())
      );
    }
    // Create a transport order for recharging and verify its processability.
    // The recharge order may be withdrawn unless its energy level is critical.
    //创建一个用于充电的传输订单，并验证其可处理性。
    //除非能量达到临界水平，否则补给命令可能会被撤销。
    TransportOrder rechargeOrder = orderService.createTransportOrder(
        new TransportOrderCreationTO("Recharge-", chargeDests)
            .withIncompleteName(true)
            .withIntendedVehicleName(vehicle.getName())
            .withDispensable(!vehicle.isEnergyLevelCritical())
    );

    Point vehiclePosition = orderService.fetchObject(Point.class, vehicle.getCurrentPosition());
    Optional<AssignmentCandidate> candidate = computeCandidate(vehicle,
                                                               vehiclePosition,
                                                               rechargeOrder)
        .filter(c -> assignmentCandidateSelectionFilter.apply(c).isEmpty());
    // XXX Change this to Optional.ifPresentOrElse() once we're at Java 9+.
    if (candidate.isPresent()) {
      transportOrderUtil.assignTransportOrder(candidate.get().getVehicle(),
                                              candidate.get().getTransportOrder(),
                                              candidate.get().getDriveOrders());
    }
    else {
      // Mark the order as failed, since the vehicle cannot execute it.
      orderService.updateTransportOrderState(rechargeOrder.getReference(),
                                             TransportOrder.State.FAILED);
    }
  }

  private Optional<AssignmentCandidate> computeCandidate(Vehicle vehicle,
                                                         Point vehiclePosition,
                                                         TransportOrder order) {
    return router.getRoute(vehicle, vehiclePosition, order)
        .map(driveOrders -> new AssignmentCandidate(vehicle, order, driveOrders));
  }
}

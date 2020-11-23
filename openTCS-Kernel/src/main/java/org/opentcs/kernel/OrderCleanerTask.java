/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel;

import com.google.common.collect.Iterables;
import java.time.Instant;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import java.util.function.Predicate;
import javax.inject.Inject;
import org.opentcs.components.kernel.OrderSequenceCleanupApproval;
import org.opentcs.components.kernel.TransportOrderCleanupApproval;
import org.opentcs.customizations.kernel.GlobalSyncObject;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.order.OrderSequence;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.kernel.workingset.TransportOrderPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task that periodically removes orders in a final state.
 *一个任务周期性的移除终止状态的订单
 * @author Stefan Walter (Fraunhofer IML)
 */
class OrderCleanerTask
    implements Runnable {

  /**
   * This class's Logger.
   * 类的记录者
   */
  private static final Logger LOG = LoggerFactory.getLogger(OrderCleanerTask.class);
  /**
   * A global object to be used for synchronization within the kernel.
   * 用来在内核中同步的全局变量
   */
  private final Object globalSyncObject;
  /**
   * Keeps all the transport orders.
   * 记录所有的订单
   */
  private final TransportOrderPool orderPool;
  /**
   * Check whether transport orders may be removed.
   * 核查订单是否可能被移除
   */
  private final Set<TransportOrderCleanupApproval> orderCleanupApprovals;
  /**
   * Check whether order sequences may be removed.
   * 核查是否订单序列可能被移除
   */
  private final Set<OrderSequenceCleanupApproval> sequenceCleanupApprovals;
  /**
   * This class's configuration.
   * 类的配置信息
   */
  private final OrderPoolConfiguration configuration;

  /**
   * Creates a new instance.
   *创建一个新的实例
   * @param kernel The kernel.
   * @param configuration This class's configuration.
   */
  @Inject
  public OrderCleanerTask(@GlobalSyncObject Object globalSyncObject,
                          TransportOrderPool orderPool,
                          Set<TransportOrderCleanupApproval> orderCleanupApprovals,
                          Set<OrderSequenceCleanupApproval> sequenceCleanupApprovals,
                          OrderPoolConfiguration configuration) {
    this.globalSyncObject = requireNonNull(globalSyncObject, "globalSyncObject");
    this.orderPool = requireNonNull(orderPool, "orderPool");
    this.orderCleanupApprovals = requireNonNull(orderCleanupApprovals, "orderCleanupApprovals");
    this.sequenceCleanupApprovals = requireNonNull(sequenceCleanupApprovals,
                                                   "sequenceCleanupApprovals");
    this.configuration = requireNonNull(configuration, "configuration");
  }

  public long getSweepInterval() {
    return configuration.sweepInterval();
  }

  @Override
  public void run() {
    synchronized (globalSyncObject) {
      LOG.debug("Sweeping order pool...");
      // Candidates that are created before this point of time should be removed.
      //在这个时间点之前的候选者应该被移除
      Instant creationTimeThreshold = Instant.now().minusMillis(configuration.sweepAge());

      // Remove all transport orders in a final state that do NOT belong to a sequence and that are
      // older than the threshold.
      for (TransportOrder transportOrder
               : orderPool.getObjectPool().getObjects(TransportOrder.class,
                                                      new OrderApproval(creationTimeThreshold))) {
        orderPool.removeTransportOrder(transportOrder.getReference());
      }

      // Remove all order sequences that have been finished, including their transport orders.
      //移除所有的已经被完成的订单，包含它们的移动订单。
      for (OrderSequence orderSequence
               : orderPool.getObjectPool().getObjects(
              OrderSequence.class,
              new SequenceApproval(creationTimeThreshold))) {
        orderPool.removeFinishedOrderSequenceAndOrders(orderSequence.getReference());
      }
    }
  }

  /**
   * Checks whether a transport order may be removed.
   * 核查是否一个订单应该被移除
   */
  private class OrderApproval
      implements Predicate<TransportOrder> {

    private final Instant creationTimeThreshold;

    public OrderApproval(Instant creationTimeThreshold) {
      this.creationTimeThreshold = creationTimeThreshold;
    }

    @Override
    public boolean test(TransportOrder order) {
      if (!order.getState().isFinalState()) {
        return false;
      }
      if (order.getWrappingSequence() != null) {
        return false;
      }
      if (order.getCreationTime().isAfter(creationTimeThreshold)) {
        return false;
      }
      for (TransportOrderCleanupApproval approval : orderCleanupApprovals) {
        if (!approval.test(order)) {
          return false;
        }
      }
      return true;
    }
  }

  /**
   * Checks whether an order sequence may be removed.
   */
  private class SequenceApproval
      implements Predicate<OrderSequence> {

    private final Instant creationTimeThreshold;

    public SequenceApproval(Instant creationTimeThreshold) {
      this.creationTimeThreshold = creationTimeThreshold;
    }

    @Override
    public boolean test(OrderSequence seq) {
      if (!seq.isFinished()) {
        return false;
      }
      List<TCSObjectReference<TransportOrder>> orderRefs = seq.getOrders();
      if (!orderRefs.isEmpty()) {
        TransportOrder lastOrder
            = orderPool.getObjectPool().getObject(TransportOrder.class,
                                                  Iterables.getLast(orderRefs));
        if (lastOrder.getCreationTime().isAfter(creationTimeThreshold)) {
          return false;
        }
      }
      for (OrderSequenceCleanupApproval approval : sequenceCleanupApprovals) {
        if (!approval.test(seq)) {
          return false;
        }
      }
      return true;
    }
  }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.virtualvehicle;

/**
 * Copyright (c) Fraunhofer IML
 */

import static example.virtualvehicle.BoundedCounter.UINT16_MAX_VALUE;
import example.virtualvehicle.OrderRequest.OrderAction;
import org.opentcs.data.model.Point;
import org.opentcs.drivers.vehicle.MovementCommand;

/**
 * Maps {@link MovementCommand}s from openTCS to a telegram sent to the vehicle.
 *
 * @author Mats Wilhelm (Fraunhofer IML)
 */
public class OrderMapper {

  /**
   * Counts the order id's sent to the vehicle.
   */
  private final BoundedCounter orderIdCounter = new BoundedCounter(1, UINT16_MAX_VALUE);

  /**
   * Creates a new instance.
   */
  public OrderMapper() {
  }

  /**
   * Maps the given command to an order request that can be sent to the vehicle.
   *映射被给定的命令到可以被发送到车辆的命令
   * @param command The command to be mapped.
   * @return The order request to be sent.
   * @throws IllegalArgumentException If the movement command could not be mapped properly.
   */
  public OrderRequest mapToOrder(MovementCommand command)
      throws IllegalArgumentException {
    return new OrderRequest(Telegram.ID_DEFAULT,
                            orderIdCounter.getAndIncrement(),
                            extractDestinationId(command.getStep().getDestinationPoint()),
                            OrderAction.stringToAction(command.getFinalOperation()));
  }

  private static int extractDestinationId(Point point)
      throws IllegalArgumentException {
    try {
      return Integer.parseInt(point.getName());
    }
    catch (NumberFormatException e) {
      throw new IllegalArgumentException("Cannot parse point name: " + point.getName(), e);
    }
  }
}


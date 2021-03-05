/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.servicewebapi.v1.order;

import com.xintai.WMSTaskTable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.access.to.order.DestinationCreationTO;
import org.opentcs.access.to.order.OrderSequenceCreationTO;
import org.opentcs.access.to.order.TransportOrderCreationTO;
import org.opentcs.components.kernel.services.DispatcherService;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.ObjectExistsException;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.OrderSequence;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.drivers.vehicle.messages.SetFinshMarkFromMes;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.Destination;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.Property;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.Transport;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.TransportHasOrderName;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.TransportWrapping;

/**
 * Handles requests for creating or withdrawing transport orders.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class OrderHandler {

  /**
   * The service we use to create transport orders.
   */
  private final TransportOrderService orderService;
  /**
   * The service we use to update vehicle states.
   */
  private final VehicleService vehicleService;
  /**
   * The service we use to withdraw transport orders.
   */
  private final DispatcherService dispatcherService;
  /**
   * Executes tasks modifying kernel data.
   */
  private final ExecutorService kernelExecutor;

  /**
   * Creates a new instance.
   *
   * @param orderService Used to create transport orders.
   * @param vehicleService Used to update vehicle state.
   * @param dispatcherService Used to withdraw transport orders.
   * @param kernelExecutor Executes tasks modifying kernel data.
   */
  @Inject
  public OrderHandler(TransportOrderService orderService,
                      VehicleService vehicleService,
                      DispatcherService dispatcherService,
                      @KernelExecutor ExecutorService kernelExecutor) {
    this.orderService = requireNonNull(orderService, "orderService");
    this.vehicleService = requireNonNull(vehicleService, "vehicleService");
    this.dispatcherService = requireNonNull(dispatcherService, "dispatcherService");
    this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
  }
  /***
   * 这个可以创建订单序列，强制一辆车进行订单运行。
   * @param name 订单序列的名字
   * @param transportWrapping 
   * @throws ObjectUnknownException
   * @throws ObjectExistsException
   * @throws KernelRuntimeException
   * @throws IllegalStateException 
   */
    public  void creatOrderSquence(String name,TransportWrapping  transportWrapping)throws ObjectUnknownException,
             ObjectExistsException,
             KernelRuntimeException,
             IllegalStateException 
  { 
  
   /* //获得订单池中订单序列为这个名字的订单
   Optional<TransportOrder> optional =  orderService.fetchObjects(TransportOrder.class).stream().filter((t) -> {
   return t.getWrappingSequence().getName()==name;
   }).findFirst();
   if(optional.isPresent())
   {
   trsOrder=optional.get();
   orderSequence=orderService.fetchObject(OrderSequence.class, trsOrder.getWrappingSequence());
   }else
   {
   OrderSequenceCreationTO sequenceCreationTO=new OrderSequenceCreationTO(name);
   sequenceCreationTO = sequenceCreationTO.withIncompleteName(true);
   sequenceCreationTO = sequenceCreationTO.withFailureFatal(true);
   orderSequence = orderService.createOrderSequence(sequenceCreationTO);
   }*/
     OrderSequenceCreationTO sequenceCreationTO=new OrderSequenceCreationTO(name);
     sequenceCreationTO = sequenceCreationTO.withIncompleteName(true);
     sequenceCreationTO = sequenceCreationTO.withFailureFatal(true);
     OrderSequence orderSequence = orderService.createOrderSequence(sequenceCreationTO);
   List<TransportHasOrderName> lsHasOrderNames= transportWrapping.getLsttransport();
   List<TransportOrderCreationTO> LSICreationTOs=new ArrayList<>();
   for(TransportHasOrderName tn:lsHasOrderNames)
   {
     Transport order=tn.getTransport();
     List<Destination> lsDestinations=new ArrayList<>();
     order.setDestinations(lsDestinations);
     String transportname=tn.getTransportname();
     TransportOrderCreationTO to
         = new TransportOrderCreationTO(transportname, destinations(order))
            .withIntendedVehicleName(order.getIntendedVehicle())
            .withDependencyNames(new HashSet<>(order.getDependencies()))
            .withDeadline(deadline(order))
            .withProperties(properties(order.getProperties()))
             .withWrappingSequence(orderSequence.getName());
       LSICreationTOs.add(to);
   }
    
   //创建订单
   /*  List<DestinationCreationTO>  listdesCreationTOs=new ArrayList<>();
   listdesCreationTOs.add(new DestinationCreationTO("destination","destiaction" ));
   listdesCreationTOs.add(new DestinationCreationTO("destination0","destinaction0"));
   TransportOrderCreationTO orderCreationTO =  new TransportOrderCreationTO("MyOrder-" + UUID.randomUUID(),listdesCreationTOs);
   orderCreationTO.withWrappingSequence(orderSequence.getName());*/
    
  TCSObjectReference<OrderSequence> orderSequence1= orderSequence.getReference();
     try {
      kernelExecutor.submit(() -> {
      LSICreationTOs.forEach((orderCreationTO)->{ orderService.createTransportOrder(orderCreationTO);});
      //如果是最后的订单，则标示这个订单序列完成。
      orderService.markOrderSequenceComplete(orderSequence1);
      dispatcherService.dispatch();
      }).get();
    }
    catch (InterruptedException exc) {
      throw new IllegalStateException("Unexpectedly interrupted");
    }
    catch (ExecutionException exc) {
      if (exc.getCause() instanceof RuntimeException) {
        throw (RuntimeException) exc.getCause();
      }
      throw new KernelRuntimeException(exc.getCause());
    }
  }
  
  
  public void createOrder(String name, Transport order)
      throws ObjectUnknownException,
             ObjectExistsException,
             KernelRuntimeException,
             IllegalStateException {
    requireNonNull(name, "name");
    requireNonNull(order, "order");
    TransportOrderCreationTO to
        = new TransportOrderCreationTO(name, destinations(order))
            .withIntendedVehicleName(order.getIntendedVehicle())
            .withDependencyNames(new HashSet<>(order.getDependencies()))
            .withDeadline(deadline(order))
            .withProperties(properties(order.getProperties()));

    try {
      kernelExecutor.submit(() -> {
        orderService.createTransportOrder(to);
        dispatcherService.dispatch();
      }).get();
    }
    catch (InterruptedException exc) {
      throw new IllegalStateException("Unexpectedly interrupted");
    }
    catch (ExecutionException exc) {
      if (exc.getCause() instanceof RuntimeException) {
        throw (RuntimeException) exc.getCause();
      }
      throw new KernelRuntimeException(exc.getCause());
    }
  }

  
  public void handerfinshinformation(WMSTaskTable wmstable)
  {
    String name=wmstable.getTasknumber();
     if (orderService.fetchObject(TransportOrder.class,name) == null) {
      throw new ObjectUnknownException("Unknown transport order: " + name);
    }
    kernelExecutor.submit(() -> {
      TransportOrder order = orderService.fetchObject(TransportOrder.class, name);
      if (order.getProcessingVehicle() != null) {  
        kernelExecutor.submit(
            () ->{
              vehicleService.sendCommAdapterMessage(order.getProcessingVehicle(), new SetFinshMarkFromMes(wmstable.getAGVstate()));
            });
      }
    });
  }
  public void withdrawByTransportOrder(String name, boolean immediate, boolean disableVehicle)
      throws ObjectUnknownException {
    requireNonNull(name, "name");

    if (orderService.fetchObject(TransportOrder.class, name) == null) {
      throw new ObjectUnknownException("Unknown transport order: " + name);
    }

    kernelExecutor.submit(() -> {
      TransportOrder order = orderService.fetchObject(TransportOrder.class, name);
      if (disableVehicle && order.getProcessingVehicle() != null) {
        vehicleService.updateVehicleIntegrationLevel(order.getProcessingVehicle(),
                                                     Vehicle.IntegrationLevel.TO_BE_RESPECTED);
      }

      dispatcherService.withdrawByTransportOrder(order.getReference(), immediate);
    });
  }

  public void withdrawByVehicle(String name, boolean immediate, boolean disableVehicle)
      throws ObjectUnknownException {
    requireNonNull(name, "name");

    Vehicle vehicle = orderService.fetchObject(Vehicle.class, name);
    if (vehicle == null) {
      throw new ObjectUnknownException("Unknown vehicle: " + name);
    }

    kernelExecutor.submit(() -> {
      if (disableVehicle) {
        vehicleService.updateVehicleIntegrationLevel(vehicle.getReference(),
                                                     Vehicle.IntegrationLevel.TO_BE_RESPECTED);
      }

      dispatcherService.withdrawByVehicle(vehicle.getReference(), immediate);
    });
  }

  private List<DestinationCreationTO> destinations(Transport order) {
    List<DestinationCreationTO> result = new ArrayList<>(order.getDestinations().size());

    for (Destination dest : order.getDestinations()) {
      DestinationCreationTO to = new DestinationCreationTO(dest.getLocationName(),
                                                           dest.getOperation());

      for (Property prop : dest.getProperties()) {
        to = to.withProperty(prop.getKey(), prop.getValue());
      }

      result.add(to);
    }

    return result;
  }

  private Instant deadline(Transport order) {
    return order.getDeadline() == null ? Instant.MAX : order.getDeadline();
  }

  private Map<String, String> properties(List<Property> properties) {
    Map<String, String> result = new HashMap<>();
    for (Property prop : properties) {
      result.put(prop.getKey(), prop.getValue());
    }
    return result;
  }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.redis.statistic;

/**
 *
 * @author Lenovo
 */
/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import org.opentcs.components.Lifecycle;
import org.opentcs.data.TCSObject;
import org.opentcs.data.TCSObjectEvent;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.kernel.extensions.servicewebapi.v1.status.binding.TransportOrderState;
import org.opentcs.kernel.extensions.servicewebapi.v1.status.binding.VehicleState;
import org.opentcs.util.event.EventHandler;
import org.opentcs.util.statistics.StatisticsEvent;
import org.opentcs.util.statistics.StatisticsRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;

/**
 * Writes received events to a file.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class RedisStacticEventLogger
    implements EventHandler,
               Lifecycle {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(RedisStacticEventLogger.class);
  
  private Jedis jedis;
   private final ObjectMapper objectMapper
      = new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  private boolean initialized;

  /**
   * Creates a new instance.
   *
   * @param outputFile The file to append log output to.
   */
  public RedisStacticEventLogger(final  Jedis jedis) {
    this.jedis = requireNonNull(jedis, "outputFile");
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

    jedis.close();

    initialized = false;
  }

  @Override
  public void onEvent(Object event) {
    if (!isInitialized()) {
      LOG.warn("Not properly initialized, ignoring event.");
      return;
    }
    if (event instanceof TCSObjectEvent) {
      processObjectEvent((TCSObjectEvent) event);
    }
  }

  /**
   * Processes a {@link TCSObjectEvent} and logs it if it is interesting.
   *
   * @param event The event to be processed.
   */
  private void processObjectEvent(TCSObjectEvent event) {
    TCSObject<?> object = event.getCurrentOrPreviousObjectState();
    if (object instanceof TransportOrder) {
      processOrderEvent(event);
    }
    else if (object instanceof Vehicle) {
      processVehicleEvent(event);
    }
    else if (object instanceof Point) {
      processPointEvent(event);
    }
  }

  /**
   * Processes an event for a {@link TransportOrder} if it is interesting.
   *
   * @param event The event to be processed.
   */
  private void processOrderEvent(TCSObjectEvent event) {
    if (event.getPreviousObjectState() == null || event.getCurrentObjectState() == null) {
      // We cannot compare two states to find out what happened - ignore.
      return;
    }

    TransportOrder orderOld = (TransportOrder) event.getPreviousObjectState();
    TransportOrder orderNow = (TransportOrder) event.getCurrentObjectState();
    saveinformation(orderNow.getName(),toJson(TransportOrderState.fromTransportOrder(orderNow)));
    // Has the order been activated?
    if (orderNow.hasState(TransportOrder.State.ACTIVE)
        && !orderOld.hasState(TransportOrder.State.ACTIVE)) {
      writeEvent(StatisticsEvent.ORDER_ACTIVATED, orderNow.getName());
      setprocessingstate(orderNow.getName(),StatisticsEvent.ORDER_ACTIVATED.toString());
    }
    // Has the order been assigned to a vehicle?
    if (orderNow.hasState(TransportOrder.State.BEING_PROCESSED)
        && !orderOld.hasState(TransportOrder.State.BEING_PROCESSED)) {
      writeEvent(StatisticsEvent.ORDER_ASSIGNED, orderNow.getName());
       setprocessingstate(orderNow.getName(),StatisticsEvent.ORDER_ASSIGNED.toString());
    }
    // Has the order been finished?
    if (orderNow.hasState(TransportOrder.State.FINISHED)
        && !orderOld.hasState(TransportOrder.State.FINISHED)) {
      writeEvent(StatisticsEvent.ORDER_FINISHED_SUCC, orderNow.getName());
      recordorders(StatisticsEvent.ORDER_FINISHED_SUCC.name(),orderNow.getName());
       setprocessingstate(orderNow.getName(),StatisticsEvent.ORDER_FINISHED_SUCC.toString());
      // Check the order's deadline. Has it been crossed?
      if (orderNow.getFinishedTime().isAfter(orderNow.getDeadline())) {
        writeEvent(StatisticsEvent.ORDER_CROSSED_DEADLINE, orderNow.getName());
          setprocessingstate(orderNow.getName(),StatisticsEvent.ORDER_CROSSED_DEADLINE.toString());
      }
    }
    // Has the order failed?
    if (orderNow.hasState(TransportOrder.State.FAILED)
        && !orderOld.hasState(TransportOrder.State.FAILED)) {
      writeEvent(StatisticsEvent.ORDER_FINISHED_FAIL, orderNow.getName());
       setprocessingstate(orderNow.getName(),StatisticsEvent.ORDER_FINISHED_FAIL.toString());
    }
  }

  /**
   * Processes an event for a {@link Vehicle} if it is intersting.
   *
   * @param event The event to be processed.
   */
  private void processVehicleEvent(TCSObjectEvent event) {
    if (event.getPreviousObjectState() == null || event.getCurrentObjectState() == null) {
      // We cannot compare two states to find out what happened - ignore.
      return;
    }

    Vehicle vehicleOld = (Vehicle) event.getPreviousObjectState();
    Vehicle vehicleNow = (Vehicle) event.getCurrentObjectState();
    
    saveinformation(vehicleNow.getName(),toJson(VehicleState.fromVehicle(vehicleNow)));
    // Did the vehicle get a transport order?
    if (vehicleNow.getTransportOrder() != null && vehicleOld.getTransportOrder() == null) {
      writeEvent(StatisticsEvent.VEHICLE_STARTS_PROCESSING, vehicleNow.getName());
       setprocessingstate(vehicleNow.getName(),StatisticsEvent.VEHICLE_STARTS_PROCESSING.toString());   
    }
    // Did the vehicle finish a transport order?
    if (vehicleNow.getTransportOrder() == null && vehicleOld.getTransportOrder() != null) {
      writeEvent(StatisticsEvent.VEHICLE_STOPS_PROCESSING, vehicleNow.getName());
        setprocessingstate(vehicleNow.getName(),StatisticsEvent.VEHICLE_STOPS_PROCESSING.toString());  
    }
    // Did the vehicle start charging?
    if (vehicleNow.hasState(Vehicle.State.CHARGING)
        && !vehicleOld.hasState(Vehicle.State.CHARGING)) {
      writeEvent(StatisticsEvent.VEHICLE_STARTS_CHARGING, vehicleNow.getName());
       setprocessingstate(vehicleNow.getName(),StatisticsEvent.VEHICLE_STARTS_CHARGING.toString());  
    }
    // Did the vehicle start charging?
    if (!vehicleNow.hasState(Vehicle.State.CHARGING)
        && vehicleOld.hasState(Vehicle.State.CHARGING)) {
      writeEvent(StatisticsEvent.VEHICLE_STOPS_CHARGING, vehicleNow.getName());
       setprocessingstate(vehicleNow.getName(),StatisticsEvent.VEHICLE_STOPS_CHARGING.toString());  
    }
    // If the vehicle is processing an order AND is not in state EXECUTING AND
    // it was either EXECUTING before or not processing, yet, consider it being
    // blocked.
    if (vehicleNow.hasProcState(Vehicle.ProcState.PROCESSING_ORDER)
        && !vehicleNow.hasState(Vehicle.State.EXECUTING)
        && (vehicleOld.hasState(Vehicle.State.EXECUTING)
            || !vehicleOld.hasProcState(Vehicle.ProcState.PROCESSING_ORDER))) {
      writeEvent(StatisticsEvent.VEHICLE_STARTS_WAITING, vehicleNow.getName());
       setprocessingstate(vehicleNow.getName(),StatisticsEvent.VEHICLE_STARTS_WAITING.toString());  
    }
    // Is the vehicle processing an order AND has its state changed from
    // something else to EXECUTING? - Consider it not blocked any more, then.
    if (vehicleNow.hasProcState(Vehicle.ProcState.PROCESSING_ORDER)
        && vehicleNow.hasState(Vehicle.State.EXECUTING)
        && !vehicleOld.hasState(Vehicle.State.EXECUTING)) {
      writeEvent(StatisticsEvent.VEHICLE_STOPS_WAITING, vehicleNow.getName());
       setprocessingstate(vehicleNow.getName(),StatisticsEvent.VEHICLE_STOPS_WAITING.toString());  
    }
  }

  /**
   * Processes an event for a {@link Point} if it is interesting.
   *
   * @param event The event to be processed.
   */
  private void processPointEvent(TCSObjectEvent event) {
    if (event.getPreviousObjectState() == null || event.getCurrentObjectState() == null) {
      // We cannot compare two states to find out what happened - ignore.
      return;
    }

    Point pointOld = (Point) event.getPreviousObjectState();
    Point pointNow = (Point) event.getCurrentObjectState();

    // Did a vehicle arrive at this point?
    if (pointNow.getOccupyingVehicle() != null && pointOld.getOccupyingVehicle() == null) {
      writeEvent(StatisticsEvent.POINT_OCCUPIED, pointNow.getName());
    }
    // Did a vehicle leave this point?
    if (pointNow.getOccupyingVehicle() == null && pointOld.getOccupyingVehicle() != null) {
      writeEvent(StatisticsEvent.POINT_FREED, pointNow.getName());
    }
  }
  private void recordorders(String key,String value)
  {
     Map<String,String> map=  new HashMap<>();
   map.put(key,value );
   jedis.xadd(key, StreamEntryID.NEW_ENTRY, map);  
  }
  private void  setprocessingstate(String key,String value)
  {
  jedis.set(key+"ProcState", value);
  }
  private  void  saveinformation(String KeyString,String jsoninformation)
  {
    /*  Map<String,String> map=  new HashMap<>();
    map.put(KeyString,jsoninformation );
    jedis.xadd(KeyString, StreamEntryID.NEW_ENTRY, map);*/
 jedis.set(KeyString, jsoninformation);
  }
  
    private <T> T fromJson(String jsonString, Class<T> clazz)
      throws IllegalArgumentException {
    try {
      return objectMapper.readValue(jsonString, clazz);
    }
    catch (IOException exc) {
      throw new IllegalArgumentException("Could not parse JSON input", exc);
    }
  }

  private String toJson(Object object)
      throws IllegalStateException {
    try {
      return objectMapper
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(object);
    }
    catch (JsonProcessingException exc) {
      throw new IllegalStateException("Could not produce JSON output", exc);
    }
  }
  /**
   * Logs an event for the named object.
   *
   * @param event The event to be logged.
   * @param objectName The name of the object for which the event happened.
   */
  private void writeEvent(StatisticsEvent event, String objectName) {
    //outputWriter.println(new StatisticsRecord(System.currentTimeMillis(), event, objectName));
  //  jedis.set(event.name(), objectName);
  /*Map<String,String> map=  new HashMap<>();
  map.put(event.name(),objectName );
  jedis.xadd(event.name(), StreamEntryID.NEW_ENTRY, map);*/
 //   jedis.lpush(event.name(),objectName);
  }
}

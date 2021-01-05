/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.plc.filterpoint;

import com.google.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.opentcs.components.kernel.services.InternalPlantModelService;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.Route;
import org.opentcs.drivers.vehicle.MovementCommand;

/**
 *
 * @author Lenovo
 */
public class FilterPoint {
  
  private Set<Point> expandPoints(Point point) {
    return plantModelService.expandResources(Collections.singleton(point.getReference())).stream()
        .filter(resource -> Point.class.equals(resource.getReference().getReferentClass()))
        .map(resource -> (Point) resource)
        .collect(Collectors.toSet());
  }
private final InternalPlantModelService plantModelService;
  @Inject
  public FilterPoint(InternalPlantModelService plantModelService) {
    this.plantModelService=plantModelService;
  }
   private boolean isPointUnoccupiedFor(Point accessPoint,
                                       Vehicle vehicle){
    return expandPoints(accessPoint).stream()
        .allMatch(point -> !pointOccupiedOrTargetedByOtherVehicle(point,
                                                                  vehicle));
  }
   private boolean pointOccupiedOrTargetedByOtherVehicle(Point pointToCheck,
                                                        Vehicle vehicle) {
    return pointToCheck.getOccupyingVehicle() != null
        && !pointToCheck.getOccupyingVehicle().equals(vehicle.getReference());
  }
     public boolean verifycanrun(MovementCommand movementCommand,Vehicle vehicle) {
        List<Route.Step> lstpList=  movementCommand.getRoute().getSteps();
        Route.Step step1=movementCommand.getStep();
        int i= lstpList.indexOf(step1);
        List<Route.Step> llList=lstpList.subList(i, lstpList.size());
        int count=  llList.size();
      if (count<5) {
          Set<Point>setpoint=llList.stream().map( step->step.getDestinationPoint()).collect(Collectors.toSet());
        if (!setpoint.stream().allMatch(point->isPointUnoccupiedFor(point,vehicle))) {
            System.out.println("last five points are parked by other vehicle");
          return true;
        }
      }
      return false;
    }
}

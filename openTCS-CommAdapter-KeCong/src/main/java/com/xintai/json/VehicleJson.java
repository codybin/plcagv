/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.json;

import org.opentcs.data.model.Vehicle;


public class VehicleJson {
    private String name;;
    private int length;
    private int energyLevelGood;
    private int energyLevelCritical;
    private int energyLevel;
    private String integrationLevel;
    private String procState;
    private String transportOrder;
    private String currentPosition;
    private String state;
    

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }
    public void setLength(int length) {
         this.length = length;
     }
     public int getLength() {
         return length;
     }

    public void setEnergyLevelGood(int energyLevelGood) {
         this.energyLevelGood = energyLevelGood;
     }
     public int getEnergyLevelGood() {
         return energyLevelGood;
     }

    public void setEnergyLevelCritical(int energyLevelCritical) {
         this.energyLevelCritical = energyLevelCritical;
     }
     public int getEnergyLevelCritical() {
         return energyLevelCritical;
     }

    public void setEnergyLevel(int energyLevel) {
         this.energyLevel = energyLevel;
     }
     public int getEnergyLevel() {
         return energyLevel;
     }

    public void setIntegrationLevel(String integrationLevel) {
         this.integrationLevel = integrationLevel;
     }
     public String getIntegrationLevel() {
         return integrationLevel;
     }

    public void setProcState(String procState) {
         this.procState = procState;
     }
     public String getProcState() {
         return procState;
     }

    public void setTransportOrder(String transportOrder) {
         this.transportOrder = transportOrder;
     }
     public String getTransportOrder() {
         return transportOrder;
     }

    public void setCurrentPosition(String  currentPosition) {
         this.currentPosition = currentPosition;
     }
     public String getCurrentPosition() {
         return currentPosition;
     }

    public void setState(String state) {
         this.state = state;
     }
     public String getState() {
         return state;
     }
     @Override
    public String toString()
    {
    
   return "Vehicle{"
        + "name=" + getName()
        + ", procState=" + procState
        + ", integrationLevel=" + integrationLevel
        + ", state=" + state
        + ", energyLevel=" + energyLevel
        + ", currentPosition=" + currentPosition
        + ", length=" + length
        + ", transportOrder=" + transportOrder
        + ", energyLevelGood=" + energyLevelGood
        + ", energyLevelCritical=" + energyLevelCritical
        + '}';
    
    
    }

}
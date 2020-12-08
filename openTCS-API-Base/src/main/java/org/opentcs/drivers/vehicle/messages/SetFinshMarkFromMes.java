/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opentcs.drivers.vehicle.messages;


import java.io.Serializable;

/**
 * A message that informs a communication adapter about a speed multiplier it/the vehicle should
 * apply.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class SetFinshMarkFromMes
    implements Serializable {

  /**
   * The speed multiplier in percent.
   */
  private final String multiplier;

  /**
   * Creates a new instance.
   *
   * @param multiplier The speed multiplier in percent.
   */
  public SetFinshMarkFromMes(final String multiplier) {
    this.multiplier =multiplier;
  }

  /**
   * Returns the speed multiplier in percent.
   *
   * @return The speed multiplier in percent.
   */
  public String getFinshMark() {
    return multiplier;
  }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xintai.agv.charger.device.taitan;

import org.opentcs.components.Lifecycle;

/**
 *
 * @author admin
 */

public interface ChargeUtilConfiguration {

  
    /**
    充电器IP杂项名称
   */
  String PROPKEY__RECHARGE_HOST = "HOST";
  
    /**
    充电器站号杂项名称
   */
  String PROPKEY__RECHARGE_SLAVEID = "SLAVEID";
}

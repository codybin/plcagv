/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.action;

import com.xintai.kecong.message.KeCongRequestMessage;

/**
 *
 * @author Lenovo
 */
public interface ISendMessageAction {
void  SendTelegram(KeCongRequestMessage request);

}

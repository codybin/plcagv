/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.statemachine;

import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.UntypedStateMachine;
import org.squirrelframework.foundation.fsm.UntypedStateMachineBuilder;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.impl.AbstractUntypedStateMachine;

/**
 *
 * @author Lenovo
 */
public class NewMain {

 
  public static void main(String[] args) {
    // TODO code application logic here
        UntypedStateMachineBuilder builder = StateMachineBuilderFactory.create(StateMachinePTRK.class);
        //ԭ����ͷ���
        //���豸��״̬
  
        builder.externalTransition().from("IDEL").to("DeviceMiddleZone").on(FSMEventType.toDeviceMiddlePT).callMethod("fromIdelToDeviceMiddle");
        builder.onEntry("DeviceMiddleZone").callMethod("ontoDeviceMiddle");
        //����״̬
        builder.externalTransition().from("DeviceMiddleZone").to("BrigeZonePTRK").on(FSMEventType.toBrigeZonePTRK).callMethod("fromDeviceMiddletoBrigeZone");
        builder.onEntry("BrigeZonePTRK").callMethod("ontoBrigeZone");
        //���ȴ�����״̬
        builder.externalTransition().from("BrigeZonePTRK").to("WaitForUnLoadPT").on(FSMEventType.toWaitForUnLoadPT).callMethod("fromBrigeZonetoWaitForUnLoad");
        builder.onEntry("WaitForUnLoadPT").callMethod("ontoWaitForUnLoad");
        //�������״̬
        builder.externalTransition().from("WaitForUnLoadPT").to("UnLoadStatePT").on(FSMEventType.toUnLoadStatePT).callMethod("fromWaitForUnLoadtoUnLoadState");
        builder.onEntry("UnLoadStatePT").callMethod("ontoUnLoadState");
        //֪ͨ�������״̬
        builder.externalTransition().from("UnLoadStatePT").to("FinshedUnLoadState").on(FSMEventType.toFinshedUnLoadPT).callMethod("fromUnLoadStatetoFinshedUnLoad");
        builder.onEntry("FinshedUnLoadState").callMethod("ontoFinshedUnLoad");
        //���ص�����״̬
          builder.externalTransition().from("FinshedUnLoadState").to("IDEL").on(FSMEventType.toIdelState).callMethod("fromUnLoadStatetoFinshedUnLoad");
        builder.onEntry("IDEL").callMethod("ontoFinshedUnLoad");
        
        //����Ϊ��ȡ��
         builder.externalTransition().from("IDEL").to("BrigeZonePTCK").on(FSMEventType.toBrigeZonePTCK).callMethod("fromIdelToBrigeZone_CK");
         builder.onEntry("BrigeZonePTCK").callMethod("ontoBrigeZone_CK");
         builder.externalTransition().from("BrigeZonePTCK").to("WaitForLoad").on(FSMEventType.toWaitForLoadCK).callMethod("fromBrigeZone_CKToWaitForLoad");
         builder.onEntry("WaitForLoad").callMethod("ontoWaitForLoad");
         builder.externalTransition().from("WaitForLoad").to("LoadStatePT").on(FSMEventType.toLoadStateCK).callMethod("fromWaitForLoadtoLoadState");
        builder.onEntry("LoadStatePT").callMethod("ontoLoadState");
        builder.externalTransition().from("LoadStatePT").to("FinshedLoadState").on(FSMEventType.toFinshedLoadCK).callMethod("fromLoadStatetoFinshedLoad");
        builder.onEntry("FinshedLoadState").callMethod("ontoFinshedLoad");
        // 4. Use State Machine
        UntypedStateMachine fsm = builder.newStateMachine("IDEL");
        //���
        fsm.fire(FSMEventType.toDeviceMiddlePT, 10);
        System.out.println(Thread.currentThread().getId());
         fsm.fire(FSMEventType.toBrigeZonePTRK, 11);
         fsm.fire(FSMEventType.toWaitForUnLoadPT, 12);
         fsm.fire(FSMEventType.toUnLoadStatePT, 13);
         fsm.fire(FSMEventType.toFinshedUnLoadPT, 14);
         fsm.fire(FSMEventType.toIdelState, 15);
         //����
         fsm.fire(FSMEventType.toBrigeZonePTCK, 10);
         fsm.fire(FSMEventType.toWaitForLoadCK, 11);
         fsm.fire(FSMEventType.toLoadStateCK, 12);
         fsm.fire(FSMEventType.toFinshedLoadCK, 13);
         fsm.fire(FSMEventType.toFinshedLoadCK, 14);
         fsm.fire(FSMEventType.toIdelState, 15);
         System.out.println(fsm.getAllStates());
      //  System.out.println("Current state is "+fsm.getCurrentState());
  }
  
}

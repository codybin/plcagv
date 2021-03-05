/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.statemachine;

import org.squirrelframework.foundation.fsm.StateMachineStatus;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.impl.AbstractUntypedStateMachine;

/**
 *
 * @author Lenovo
 */
 @StateMachineParameters(stateType=String.class, eventType=FSMEventType.class, contextType=Integer.class)
   public  class StateMachinePTRK extends AbstractUntypedStateMachine {
        protected void fromIdelToDeviceMiddle(String from, String to, FSMEventType event, Integer context) {
            System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        }
        protected void ontoDeviceMiddle(String from, String to, FSMEventType event,Integer context) {
          //�Ƿ�Ҫ֪ͨMESϵͳ������
            System.out.println("Entry State \'"+to+"\'.");
            System.out.println("devicezone to load cargo");
        }
        protected  void  fromDeviceMiddletoBrigeZone(String from, String to, FSMEventType event, Integer context)
        {
        System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        
        }
         protected void ontoBrigeZone(String from, String to, FSMEventType event,Integer context) {
          //֪ͨWMS�����ţ�context��Ҫ�����������Ϣ������erp��ip�Ͷ˿�
           System.out.println("Entry State \'"+to+"\'.");
           System.out.println("notice erp tasktable number and agv to the brige");
        }
         protected  void  fromBrigeZonetoWaitForUnLoad(String from, String to, FSMEventType event, Integer context)
        {
        System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        
        }
         protected void ontoWaitForUnLoad(String from, String to, FSMEventType event,Integer context) {
           //�ȴ�������context:��Ҫ�����IP��port,��Ҫ�ȴ��ŵ��źš�
            System.out.println("Entry State \'"+to+"\'.");
            System.out.println("wait for unloading material");
        } 
          protected  void  fromWaitForUnLoadtoUnLoadState(String from, String to, FSMEventType event, Integer context)
        {
        System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        
        }
         protected void ontoUnLoadState(String from, String to, FSMEventType event,Integer context) {
           //֪ͨERP �����������Ϣ��������ά�����ϸ������Ϣ
            System.out.println("Entry State \'"+to+"\'.");
            System.err.println("notice erp the complete tasktable information");
        }
          protected  void  fromUnLoadStatetoFinshedUnLoad(String from, String to, FSMEventType event, Integer context)
        {
        System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        
        }
         protected void ontoFinshedUnLoad(String from, String to, FSMEventType event,Integer context) {
           //֪ͨ�������ɡ�
            System.out.println("Entry State \'"+to+"\'.");
            System.out.println("notice warehouse the unload action finshed");
        }
          protected void fromIdelToBrigeZone_CK(String from, String to, FSMEventType event, Integer context) {
            System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        }
        protected void ontoBrigeZone_CK(String from, String to, FSMEventType event,Integer context) {
          //�Ƿ�Ҫ֪ͨMESϵͳ������
            System.out.println("Entry State \'"+to+"\'.");
            System.out.println("on the brige to load cargo");
        }
        
          protected void fromBrigeZone_CKToWaitForLoad(String from, String to, FSMEventType event, Integer context) {
            System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        }
        protected void ontoWaitForLoad(String from, String to, FSMEventType event,Integer context) {
          //�Ƿ�Ҫ֪ͨMESϵͳ������
            System.out.println("Entry State \'"+to+"\'.");
            System.out.println("liku to load cargo");
        }
        
         
           protected  void  fromWaitForLoadtoLoadState(String from, String to, FSMEventType event, Integer context)
        {
        System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        
        }
         protected void ontoLoadState(String from, String to, FSMEventType event,Integer context) {
           //֪ͨERP �����������Ϣ��������ά�����ϸ������Ϣ
            System.out.println("Entry State \'"+to+"\'.");
            System.err.println("loading ");
        }
          protected  void  fromLoadStatetoFinshedLoad(String from, String to, FSMEventType event, Integer context)
        {
        System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        
        }
         protected void ontoFinshedLoad(String from, String to, FSMEventType event,Integer context) {
           //֪ͨ�������ɡ�
            System.out.println("Entry State \'"+to+"\'.");
            System.out.println("finshed load");
        }
        
          @Override
        protected void afterTransitionCausedException(Object fromState, Object toState, Object event, Object context) {
            Throwable targeException = getLastException().getTargetException();
            if(targeException instanceof IllegalArgumentException) {
              
                // do some error clean up job here
                // ...
                // after recovered from this exception, reset the state machine status back to normal
                setStatus(StateMachineStatus.IDLE);
            }
        }
        
    }
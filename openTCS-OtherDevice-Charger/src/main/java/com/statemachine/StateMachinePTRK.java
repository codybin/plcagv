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
          //是否要通知MES系统？待定
            System.out.println("Entry State \'"+to+"\'.");
            System.out.println("devicezone to load cargo");
        }
        protected  void  fromDeviceMiddletoBrigeZone(String from, String to, FSMEventType event, Integer context)
        {
        System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        
        }
         protected void ontoBrigeZone(String from, String to, FSMEventType event,Integer context) {
          //通知WMS车到桥：context需要订单的相关信息，还有erp的ip和端口
           System.out.println("Entry State \'"+to+"\'.");
           System.out.println("notice erp tasktable number and agv to the brige");
        }
         protected  void  fromBrigeZonetoWaitForUnLoad(String from, String to, FSMEventType event, Integer context)
        {
        System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        
        }
         protected void ontoWaitForUnLoad(String from, String to, FSMEventType event,Integer context) {
           //等待放允许，context:需要立库的IP和port,需要等待放的信号。
            System.out.println("Entry State \'"+to+"\'.");
            System.out.println("wait for unloading material");
        } 
          protected  void  fromWaitForUnLoadtoUnLoadState(String from, String to, FSMEventType event, Integer context)
        {
        System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        
        }
         protected void ontoUnLoadState(String from, String to, FSMEventType event,Integer context) {
           //通知ERP 订单的相关信息：包含二维码的详细订单信息
            System.out.println("Entry State \'"+to+"\'.");
            System.err.println("notice erp the complete tasktable information");
        }
          protected  void  fromUnLoadStatetoFinshedUnLoad(String from, String to, FSMEventType event, Integer context)
        {
        System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        
        }
         protected void ontoFinshedUnLoad(String from, String to, FSMEventType event,Integer context) {
           //通知立库放完成。
            System.out.println("Entry State \'"+to+"\'.");
            System.out.println("notice warehouse the unload action finshed");
        }
          protected void fromIdelToBrigeZone_CK(String from, String to, FSMEventType event, Integer context) {
            System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        }
        protected void ontoBrigeZone_CK(String from, String to, FSMEventType event,Integer context) {
          //是否要通知MES系统？待定
            System.out.println("Entry State \'"+to+"\'.");
            System.out.println("on the brige to load cargo");
        }
        
          protected void fromBrigeZone_CKToWaitForLoad(String from, String to, FSMEventType event, Integer context) {
            System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        }
        protected void ontoWaitForLoad(String from, String to, FSMEventType event,Integer context) {
          //是否要通知MES系统？待定
            System.out.println("Entry State \'"+to+"\'.");
            System.out.println("liku to load cargo");
        }
        
         
           protected  void  fromWaitForLoadtoLoadState(String from, String to, FSMEventType event, Integer context)
        {
        System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        
        }
         protected void ontoLoadState(String from, String to, FSMEventType event,Integer context) {
           //通知ERP 订单的相关信息：包含二维码的详细订单信息
            System.out.println("Entry State \'"+to+"\'.");
            System.err.println("loading ");
        }
          protected  void  fromLoadStatetoFinshedLoad(String from, String to, FSMEventType event, Integer context)
        {
        System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        
        }
         protected void ontoFinshedLoad(String from, String to, FSMEventType event,Integer context) {
           //通知立库放完成。
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
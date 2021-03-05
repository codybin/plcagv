/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.statemachine;

import com.google.inject.Inject;
import com.xintai.device.DestinationLocationService;
import static java.util.Objects.requireNonNull;
import java.util.concurrent.ExecutorService;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.StateMachineStatus;
import org.squirrelframework.foundation.fsm.UntypedStateMachine;
import org.squirrelframework.foundation.fsm.UntypedStateMachineBuilder;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.impl.AbstractUntypedStateMachine;

/**
 *
 * @author Lenovo
 */
public class FSMStateMachine {

  private final DestinationLocationService destinationLocationService;
  private final ExecutorService plcService;
@Inject
  public FSMStateMachine(DestinationLocationService destinationLocationService,@PLCExecutor ExecutorService  plcService) {
      this.plcService = requireNonNull(plcService, "kernelExecutor");
    this.destinationLocationService=destinationLocationService;
     UntypedStateMachineBuilder builder = StateMachineBuilderFactory.create(StateMachinePTRK.class);
        //原料盘头入库
        //到设备区状态
        builder.externalTransition().from("IDEL").to("DeviceMiddleZone").on(FSMEventType.toDeviceMiddlePT).callMethod("fromIdelToDeviceMiddle");
        builder.onEntry("DeviceMiddleZone").callMethod("ontoDeviceMiddle");
        //到桥状态
        builder.externalTransition().from("DeviceMiddleZone").to("BrigeZonePTRK").on(FSMEventType.toBrigeZonePTRK).callMethod("fromDeviceMiddletoBrigeZone");
        builder.onEntry("BrigeZonePTRK").callMethod("ontoBrigeZone");
        //到等待放料状态
        builder.externalTransition().from("BrigeZonePTRK").to("WaitForUnLoadPT").on(FSMEventType.toWaitForUnLoadPT).callMethod("fromBrigeZonetoWaitForUnLoad");
        builder.onEntry("WaitForUnLoadPT").callMethod("ontoWaitForUnLoad");
        //放料完成状态
        builder.externalTransition().from("WaitForUnLoadPT").to("UnLoadStatePT").on(FSMEventType.toUnLoadStatePT).callMethod("fromWaitForUnLoadtoUnLoadState");
        builder.onEntry("UnLoadStatePT").callMethod("ontoUnLoadState");
        //通知放料完成状态
        builder.externalTransition().from("UnLoadStatePT").to("FinshedUnLoadState").on(FSMEventType.toFinshedUnLoadPT).callMethod("fromUnLoadStatetoFinshedUnLoad");
        builder.onEntry("FinshedUnLoadState").callMethod("ontoFinshedUnLoad");
        //返回到空闲状态
          builder.externalTransition().from("FinshedUnLoadState").to("IDEL").on(FSMEventType.toIdelState).callMethod("fromUnLoadStatetoFinshedUnLoad");
        builder.onEntry("IDEL").callMethod("ontoFinshedUnLoad");
        
        //到桥为了取料
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
        //入库
        fsm.fire(FSMEventType.toDeviceMiddlePT, 10);
        System.out.println(Thread.currentThread().getId());
         fsm.fire(FSMEventType.toBrigeZonePTRK, 11);
         fsm.fire(FSMEventType.toWaitForUnLoadPT, 12);
         fsm.fire(FSMEventType.toUnLoadStatePT, 13);
         fsm.fire(FSMEventType.toFinshedUnLoadPT, 14);
         fsm.fire(FSMEventType.toIdelState, 15);
         //出库
         fsm.fire(FSMEventType.toBrigeZonePTCK, 10);
         fsm.fire(FSMEventType.toWaitForLoadCK, 11);
         fsm.fire(FSMEventType.toLoadStateCK, 12);
         fsm.fire(FSMEventType.toFinshedLoadCK, 13);
         fsm.fire(FSMEventType.toFinshedLoadCK, 14);
         fsm.fire(FSMEventType.toIdelState, 15);
         System.out.println(fsm.getAllStates());
    
    
  }
  
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
          
          //更新状态到桥状态
          //destinationLocationService.UpdateWMSTaskTable("");
          //获得订单状态发送给ERP
          destinationLocationService.findWMSTaskTableByTaskNumber("");
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
}

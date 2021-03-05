/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.interaction.erp;

/**
 *
 * @author Lenovo
 */
public class ReponseResult {
public Response SUCCESS() {
 return new Response();
  }
public Response SUCCESS(String message) {
return new Response(200, message); }

 public Response SUCCESS_DATA(Object data) {
 return new Response(200, null, data);
  }
 public Response SUCCESS(String message, Object data) {
 return new Response(200, message, data);
  }
public Response ERROR(String message) {
return new Response(500, message);
  }
public Response ERROR(int code,String message,Object data) {
return new Response(code, message, data);
  }
public Response ERROR(int code, String message){
 return new Response(code, message);
  }
}

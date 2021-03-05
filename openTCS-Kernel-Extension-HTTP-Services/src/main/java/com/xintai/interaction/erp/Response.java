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
public class Response {

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
  private int code;
  private String message;
  private Object data;

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }
 public Response() {
    this.code = 200;
  }
 public Response(Object data) {
    this.code = 200;
    this.data = data;
  }
public Response(int code, String message) {
    this.code = code;
    this.message = message;
  }
public Response(int code, String message, Object data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

}

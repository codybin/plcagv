/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.erp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
/**
 *
 * @author Lenovo
 */
public class ReportPostionERPService {
  private final String url;

  public ReportPostionERPService(String url, String encoding) {
    this.url = url;
    this.encoding = encoding;
  }
  public ReportPostionERPService(String url) {
  this(url, "UTF-8");
  }
  private final String encoding;
 public  String SendPostionTOERP(ReportCarPostionTOERP reportCarPostionTOERP)
  {
  String  json=tojson(reportCarPostionTOERP);
 return HttpRequest.sendPost(url, json, encoding);
  }
  private String tojson(ReportCarPostionTOERP reportCarPostionTOERP)
  {
    Object object=JSONArray.toJSON(reportCarPostionTOERP);
  return object.toString();
  }
}

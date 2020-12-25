/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.interaction.erp;

public class FinshInforFromERP
{
    private String location;

    private String action;

    private String finish;

    public void setLocation(String location){
        this.location = location;
    }
    public String getLocation(){
        return this.location;
    }
    public void setAction(String action){
        this.action = action;
    }
    public String getAction(){
        return this.action;
    }
    public void setFinish(String finish){
        this.finish = finish;
    }
    public String getFinish(){
        return this.finish;
    }
}

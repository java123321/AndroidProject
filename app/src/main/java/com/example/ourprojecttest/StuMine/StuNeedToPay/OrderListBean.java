package com.example.ourprojecttest.StuMine.StuNeedToPay;

import java.util.ArrayList;

public class OrderListBean {
   private String orderTime;
    private String orderPrice;
    private ArrayList<ContentInfoBean> drugInfoBeans;
    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }



    public ArrayList<ContentInfoBean> getDrugInfoBeans() {
        return drugInfoBeans;
    }

    public void setDrugInfoBeans(ArrayList<ContentInfoBean> drugInfoBeans) {
        this.drugInfoBeans = drugInfoBeans;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }




}

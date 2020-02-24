package com.example.ourprojecttest.StuNeedToPay;

import java.util.ArrayList;

public class OrderListBean {
   private String orderTime;

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    private String orderPrice;
    private ArrayList<ContentInfoBean> drugInfoBeans;

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

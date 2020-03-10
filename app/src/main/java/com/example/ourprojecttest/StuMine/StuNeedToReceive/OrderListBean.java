package com.example.ourprojecttest.StuMine.StuNeedToReceive;

import com.example.ourprojecttest.StuMine.StuNeedToPay.ContentInfoBean;

import java.util.ArrayList;

public class OrderListBean {
    private String orderPrice;

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    private String orderTime;
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

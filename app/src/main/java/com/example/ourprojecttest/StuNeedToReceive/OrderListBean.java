package com.example.ourprojecttest.StuNeedToReceive;

import com.example.ourprojecttest.StuNeedToPay.ContentInfoBean;

import java.util.ArrayList;

public class OrderListBean {
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

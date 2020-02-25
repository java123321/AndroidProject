package com.example.ourprojecttest.DocMine.DocOrderManagement;

import com.example.ourprojecttest.StuMine.StuNeedToPay.ContentInfoBean;

import java.util.ArrayList;

public class OrderListBean {
    private String receiverName;
    private String receiverTelephone;
    private String receiverAddress;
    private ArrayList<ContentInfoBean> drugInfoBeans;
    private String orderTime;

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
    public ArrayList<ContentInfoBean> getDrugInfoBeans() {
        return drugInfoBeans;
    }

    public void setDrugInfoBeans(ArrayList<ContentInfoBean> drugInfoBeans) {
        this.drugInfoBeans = drugInfoBeans;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverTelephone() {
        return receiverTelephone;
    }

    public void setReceiverTelephone(String receiverTelephone) {
        this.receiverTelephone = receiverTelephone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

}

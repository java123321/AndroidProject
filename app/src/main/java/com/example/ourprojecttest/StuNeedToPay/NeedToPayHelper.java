package com.example.ourprojecttest.StuNeedToPay;

import java.util.ArrayList;

public class NeedToPayHelper {

    public static ArrayList<Object> getDataAfterHandle(ArrayList<OrderListBean> orderListBeans){
        ArrayList<Object> dataList=new ArrayList<>();

        //遍历每一张大订单
        for(OrderListBean orderListBean : orderListBeans){

            //将订单头部信息加入到数据集中
            HeadInfoBean headInfoBean=new HeadInfoBean();
            headInfoBean.setOrderTime(orderListBean.getOrderTime());
            dataList.add(headInfoBean);

            //将同一个订单中的多个药品加入到数据集中
            ArrayList<ContentInfoBean> contentList=orderListBean.getDrugInfoBeans();
         for(int i=0;i<contentList.size();i++){
             dataList.add(contentList.get(i));
         }

            //将订单的底部信息加入到数据集中
         FooterInfoBean footerInfoBean=new FooterInfoBean();
         footerInfoBean.setDrugAmount(String.valueOf(contentList.size()));
         footerInfoBean.setOrderPrice(orderListBean.getOrderPrice());
        dataList.add(footerInfoBean);
        }

        return dataList;
    }

}

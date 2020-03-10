package com.example.ourprojecttest.DocMine.DocOrderManagement;

import android.util.Log;

import com.example.ourprojecttest.StuMine.StuNeedToPay.ContentInfoBean;

import java.util.ArrayList;

public class NeedToPostHelper {

    public static ArrayList<Object> getDataAfterHandle(ArrayList<OrderListBean> orderListBeans){
        ArrayList<Object> dataList=new ArrayList<>();

        //遍历每一张大订单
        for(OrderListBean orderListBean : orderListBeans){

            //将订单头部信息加入到数据集中
            HeadInfoBean headInfoBean=new HeadInfoBean();
            headInfoBean.setReceiverName(orderListBean.getReceiverName());
            headInfoBean.setReceiverTelephone(orderListBean.getReceiverTelephone());
            headInfoBean.setReceiverAddress(orderListBean.getReceiverAddress());
            dataList.add(headInfoBean);

            //将同一个订单中的多个药品加入到数据集中
            ArrayList<ContentInfoBean> contentList=orderListBean.getDrugInfoBeans();
            for(int i=0;i<contentList.size();i++){
                dataList.add(contentList.get(i));
            }
            Log.d("topaydrugnum:",contentList.size()+"");

            //将订单的底部信息加入到数据集中
            FooterInfoBean footerInfoBean=new FooterInfoBean();
            footerInfoBean.setOrderTime(orderListBean.getOrderTime());
            dataList.add(footerInfoBean);
        }

        return dataList;
    }

}

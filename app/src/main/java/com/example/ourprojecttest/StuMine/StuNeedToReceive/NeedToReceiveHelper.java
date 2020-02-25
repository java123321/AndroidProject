package com.example.ourprojecttest.StuMine.StuNeedToReceive;

import com.example.ourprojecttest.StuMine.StuNeedToPay.ContentInfoBean;
import com.example.ourprojecttest.StuMine.StuNeedToPay.FooterInfoBean;
import com.example.ourprojecttest.StuMine.StuNeedToPay.HeadInfoBean;

import java.util.ArrayList;

public class NeedToReceiveHelper {

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
            FooterInfoBean footerInfoBean=new FooterInfoBean();
            dataList.add(footerInfoBean);
        }

        return dataList;
    }
}

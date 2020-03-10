package com.example.ourprojecttest.DocMine.DocOrderManagement;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.StuMine.StuNeedToPay.ContentInfoBean;
import com.example.ourprojecttest.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderManagementAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private final int ITEM_HEADER=1,ITEM_CONTENT=2,ITEM_FOOTER=3;
    Intent intentToOrderManagement =new Intent("com.example.ourprojecttest.OrderManagement");
    Context mContext;
    private final int NOT_POST=2;
    private final int HAVE_POST=5;
    private int TYPE;
    private ArrayList<Object> dataList=new ArrayList<>();

    public OrderManagementAdapter(Context context){
        mContext=context;
    }
    public void setList(ArrayList<Object> list,int type){
        dataList=list;
        TYPE=type;
    }


    //头部布局持有者类
    class HeadViewHolder extends RecyclerView.ViewHolder{
        TextView receiverName;
        TextView receiverTelephone;
        TextView receiverAddress;
        public HeadViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverName=itemView.findViewById(R.id.receiverName);
            receiverTelephone=itemView.findViewById(R.id.receiverTelephone);
            receiverAddress=itemView.findViewById(R.id.receiverAddress);

        }
    }
    //药品布局持有者类
    class ContentViewHolder extends RecyclerView.ViewHolder{
        TextView drugName;
        TextView drugAmount;
        TextView drugUnite;
        ImageView drugPicture;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            drugName=itemView.findViewById(R.id.drugName);
            drugAmount=itemView.findViewById(R.id.drugAmount);
            drugUnite=itemView.findViewById(R.id.drugUnite);
            drugPicture=itemView.findViewById(R.id.drugPicture);
        }
    }
    //脚部布局持有者类
    class FootViewHolder extends RecyclerView.ViewHolder{
        Button alreadPost;
        TextView orderTime;
        public FootViewHolder(@NonNull View itemView) {
            super(itemView);
            alreadPost=itemView.findViewById(R.id.alreadyPostDoc);
            if(TYPE==HAVE_POST){//如果是已经发货的
                alreadPost.setText("删除订单");
            }
            else{
                alreadPost.setText("已发货");
            }
            orderTime=itemView.findViewById(R.id.orderTime);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object object=dataList.get(position);
        if(object instanceof ContentInfoBean){
            return ITEM_CONTENT;
        }
        else if(object instanceof HeadInfoBean){
            return ITEM_HEADER;
        }
        else {
            return ITEM_FOOTER;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==ITEM_CONTENT){
            view=View.inflate(mContext,R.layout.need_to_pay_content,null);
            return new ContentViewHolder(view);
        }
        else if (viewType==ITEM_HEADER){
            view=View.inflate(mContext,R.layout.need_to_post_header,null);
            return new HeadViewHolder(view);
        }
        else{
            view=View.inflate(mContext,R.layout.need_to_post_footer,null);
            return new FootViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type=getItemViewType(position);
        //绑定药品内容
        if (type==ITEM_CONTENT){
            ContentViewHolder contentViewHolder=(ContentViewHolder)holder;
            ContentInfoBean bean=(ContentInfoBean) dataList.get(position);
            contentViewHolder.drugUnite.setText("￥ "+bean.getDrugUnite());
            contentViewHolder.drugAmount.setText("X "+bean.getDrugAmount());
            contentViewHolder.drugName.setText(bean.getDrugName());
            contentViewHolder.drugPicture.setImageDrawable(bean.getDrugPicture());
        }//绑定订单头部
        else if(type==ITEM_HEADER){
            HeadViewHolder headViewHolder=(HeadViewHolder)holder;
            HeadInfoBean bean=(HeadInfoBean)dataList.get(position);
            headViewHolder.receiverName.setText("收货人姓名:"+bean.getReceiverName());
            headViewHolder.receiverTelephone.setText("电话:"+bean.getReceiverTelephone());
            headViewHolder.receiverAddress.setText("地址:"+bean.getReceiverAddress());
        }
        else{//绑定脚部信息
            FootViewHolder footViewHolder=(FootViewHolder)holder;
            final FooterInfoBean bean=(FooterInfoBean) dataList.get(position);
            Date date=new Date();
            date.setTime(Long.valueOf(bean.getOrderTime()));
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            footViewHolder.orderTime.setText("订单时间:"+format.format(date));

            //设置已发货的点击事件
            footViewHolder.alreadPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TYPE==NOT_POST){//如果是点击的已发货按钮
                        intentToOrderManagement.putExtra("havePost",bean.getOrderTime());
                        mContext.sendBroadcast(intentToOrderManagement);
                    }
                    else{//如果是点击的删除订单按钮
                        intentToOrderManagement.putExtra("delete",bean.getOrderTime());
                        mContext.sendBroadcast(intentToOrderManagement);
                    }

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

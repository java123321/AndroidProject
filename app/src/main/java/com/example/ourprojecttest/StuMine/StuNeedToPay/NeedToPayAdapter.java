package com.example.ourprojecttest.StuMine.StuNeedToPay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.StuDrugStore.StuBuyDrug;
import com.example.ourprojecttest.StuMine.AddressActivity;
import com.example.ourprojecttest.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NeedToPayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_HEADER=1,ITEM_CONTENT=2,ITEM_FOOTER=3;
    Intent intentToNeedToPay=new Intent("com.example.ourprojecttest.BUY_ORDER");

    private Context mContext;


 private ArrayList<Object> dataList=new ArrayList<>();


    public NeedToPayAdapter(Context context){
        mContext=context;
    }

    public void setList(ArrayList<Object> list){
        dataList=list;
    }




    //头部布局持有者类
    class HeadViewHolder extends RecyclerView.ViewHolder{
        TextView time;

        public HeadViewHolder(@NonNull View itemView) {
            super(itemView);
            time=itemView.findViewById(R.id.orderTime);
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
        TextView orderInfo;
        Button modefyAddress;
        Button deleteOrder;
        Button goToPay;
        public FootViewHolder(@NonNull View itemView) {
            super(itemView);
            orderInfo=itemView.findViewById(R.id.orderInfo);
            modefyAddress=itemView.findViewById(R.id.modefyAddress);
            deleteOrder=itemView.findViewById(R.id.deleteOrder);
            goToPay=itemView.findViewById(R.id.goToPay);

            //设置修改地址的点击事件
            modefyAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, AddressActivity.class);
                    mContext.startActivity(intent);
                }
            });

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
            view= LayoutInflater.from(mContext).inflate(R.layout.need_to_pay_content,parent,false);
            return new ContentViewHolder(view);
        }
        else if (viewType==ITEM_HEADER){
            view= LayoutInflater.from(mContext).inflate(R.layout.need_to_pay_header,parent,false);
            return new HeadViewHolder(view);
        }
        else{
            view= LayoutInflater.from(mContext).inflate(R.layout.need_to_pay_footer,parent,false);
            return new FootViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
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
                                   Date date=new Date();
                        date.setTime(Long.valueOf(bean.getOrderTime()));
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            headViewHolder.time.setText("订单时间: "+format.format(date));
        }
        else{//绑定脚部信息
            FootViewHolder footViewHolder=(FootViewHolder)holder;
            final FooterInfoBean bean=(FooterInfoBean) dataList.get(position);

                String str ="共"+bean.getDrugAmount()+"件商品 合计:￥ "+bean.getOrderPrice();
                SpannableStringBuilder builder = new SpannableStringBuilder(str);
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FF1493"));
                builder.setSpan(colorSpan, str.indexOf("￥"), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                footViewHolder.orderInfo.setText(builder);

            //footViewHolder.orderInfo.setText("共"+bean.getDrugAmount()+"件商品 合计:￥ "+bean.getOrderPrice());
            //设置去付款的点击事件
            footViewHolder.goToPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        intentToNeedToPay.putExtra("price",bean.getOrderPrice());
                        intentToNeedToPay.putExtra("orderId",bean.getOrderId());
                        mContext.sendBroadcast(intentToNeedToPay);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

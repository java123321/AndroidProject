package com.example.ourprojecttest.DocMine.DocOrderManagement;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.StuMine.StuNeedToPay.ContentInfoBean;
import com.example.ourprojecttest.R;

import java.lang.reflect.Type;
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
    private volatile int TYPE;
    public ArrayList<Object> dataList=new ArrayList<>();

    public OrderManagementAdapter(Context context){
        mContext=context;
    }
    public void setList(ArrayList<Object> list,int type){
        dataList=list;
        TYPE=type;
        Log.d("test.order.manage.type","set.list.type:"+TYPE);
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
            view=LayoutInflater.from(mContext).inflate(R.layout.need_to_pay_content,parent,false);
            return new ContentViewHolder(view);
        }
        else if (viewType==ITEM_HEADER){
            //view=View.inflate(mContext,R.layout.need_to_post_header,null);
            view=LayoutInflater.from(mContext).inflate(R.layout.need_to_post_header,parent,false);
            return new HeadViewHolder(view);
        }
        else{
           // view= LayoutInflater.from(mContext).inflate(R.layout.need_to_post_footer, parent, false);
            view=LayoutInflater.from(mContext).inflate(R.layout.need_to_post_footer,parent,false);
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
            headViewHolder.receiverName.setText("姓名:"+bean.getReceiverName());
            headViewHolder.receiverTelephone.setText("电话:"+bean.getReceiverTelephone());
            headViewHolder.receiverAddress.setText("地址:"+bean.getReceiverAddress());
        }
        else{//绑定脚部信息
            FootViewHolder footViewHolder=(FootViewHolder)holder;
            final FooterInfoBean bean=(FooterInfoBean) dataList.get(position);
            Date date=new Date();
            Log.d("ordermanage","orderTime1:"+bean.getOrderTime());
            date.setTime(Long.valueOf(bean.getOrderTime()));
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            footViewHolder.orderTime.setText("订单时间:"+format.format(date));

            Log.d("test.order.manage.type","footview.type"+ TYPE);
            if(TYPE==HAVE_POST){//如果是已经发货的
                footViewHolder.alreadPost.setText("删除订单");
            }
            else{
                footViewHolder.alreadPost.setText("已发货");
            }



            //设置已发货的点击事件
            footViewHolder.alreadPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TYPE==NOT_POST){//如果是点击的已发货按钮
                       // intentToOrderManagement.putExtra("havePost",bean.getOrderTime());
                       // mContext.sendBroadcast(intentToOrderManagement);
                        final Dialog dialog = new Dialog(mContext,R.style.ActionSheetDialogStyle);
                        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_delete_dingdan, null);
                        TextView no=inflate.findViewById(R.id.no);
                        TextView yes = inflate.findViewById(R.id.yes);
                        TextView jianjie=inflate.findViewById(R.id.jianjie);
                        TextView describe=inflate.findViewById(R.id.describe);
                        jianjie.setText("确认发货");
                        describe.setText("确认已发货？");
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                intentToOrderManagement.putExtra("havePost",bean.getOrderTime());
                                mContext.sendBroadcast(intentToOrderManagement);
                                dialog.dismiss();
                            }
                        });
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setContentView(inflate);
                        dialog.setCancelable(false);
                        Window dialogWindow = dialog.getWindow();
                        //设置Dialog从窗体底部弹出
                        dialogWindow.setGravity( Gravity.CENTER);
                        //获得窗体的属性
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        lp.width =800;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialogWindow.setAttributes(lp);
                        dialog.show();
                    }
                    else{//如果是点击的删除订单按钮
                       // intentToOrderManagement.putExtra("delete",bean.getOrderTime());
                       // mContext.sendBroadcast(intentToOrderManagement);
                        final Dialog dialog = new Dialog(mContext,R.style.ActionSheetDialogStyle);
                        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_delete_dingdan, null);
                        TextView no=inflate.findViewById(R.id.no);
                        TextView yes = inflate.findViewById(R.id.yes);
                        TextView jianjie=inflate.findViewById(R.id.jianjie);
                        TextView describe=inflate.findViewById(R.id.describe);
                        jianjie.setText("删除订单");
                        describe.setText("确认删除订单？");
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                intentToOrderManagement.putExtra("delete",bean.getOrderTime());
                                mContext.sendBroadcast(intentToOrderManagement);
                                dialog.dismiss();
                            }
                        });
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setContentView(inflate);
                        dialog.setCancelable(false);
                        Window dialogWindow = dialog.getWindow();
                        //设置Dialog从窗体底部弹出
                        dialogWindow.setGravity( Gravity.CENTER);
                        //获得窗体的属性
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        lp.width =800;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialogWindow.setAttributes(lp);
                        dialog.show();
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

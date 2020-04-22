package com.example.ourprojecttest.StuMine.StuNeedToReceive;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.ourprojecttest.StuMine.StuNeedToPay.HeadInfoBean;
import com.example.ourprojecttest.StuMine.StuNeedToPay.FooterInfoBean;
import com.example.ourprojecttest.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NeedToReceiveAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
    Intent intentToReceiveOrder=new Intent("com.example.ourprojecttest.RECEIVE_ORDER");
    private final int ITEM_HEADER=1,ITEM_CONTENT=2,ITEM_FOOTER=3;
    Context mContext;
    private ArrayList<Object> dataList=new ArrayList<>();


 public void setContext(Context context){
     mContext=context;
 }


    public void setList(ArrayList<Object> list){
        dataList=list;
    }

    //头部布局持有者类
    class HeadViewHolder extends RecyclerView.ViewHolder{
        TextView orderTime;

        public HeadViewHolder(@NonNull View itemView) {
            super(itemView);
            orderTime=itemView.findViewById(R.id.orderTime);
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
    class FootViewHolder extends RecyclerView.ViewHolder {
        Button confirmReceive;
        public FootViewHolder(@NonNull View itemView) {
            super(itemView);
            confirmReceive=itemView.findViewById(R.id.confirmReceive);
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
                view=View.inflate(mContext, R.layout.need_to_pay_content,null);
                return new ContentViewHolder(view);
            }
            else if (viewType==ITEM_HEADER){
                view=View.inflate(mContext,R.layout.need_to_pay_header,null);
                return new HeadViewHolder(view);
            }
            else{
                view=View.inflate(mContext,R.layout.need_to_receive_footer,null);
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
                contentViewHolder.drugName.setText("药品名: "+bean.getDrugName());
                contentViewHolder.drugPicture.setImageDrawable(bean.getDrugPicture());
            }//绑定订单头部
            else if(type==ITEM_HEADER){
                HeadViewHolder headViewHolder=(HeadViewHolder)holder;
                HeadInfoBean bean=(HeadInfoBean)dataList.get(position);
                Date date=new Date();
                date.setTime(Long.valueOf(bean.getOrderTime()));
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                headViewHolder.orderTime.setText("订单时间:"+format.format(date));
            }
            else{//绑定脚部信息
                FootViewHolder footViewHolder=(FootViewHolder)holder;
                final FooterInfoBean bean=(FooterInfoBean) dataList.get(position);
                //确认收货的点击事件
                footViewHolder.confirmReceive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // AlertDialog.Builder builder  = new AlertDialog.Builder(mContext);
                       // builder.setTitle("提示" ) ;
                       // builder.setMessage("确认已收到药品?" ) ;
                       // builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                       //     @Override
                       //     public void onClick(DialogInterface dialog, int which) {
                       //         intentToReceiveOrder.putExtra("receive",bean.getOrderId());
                       //         mContext.sendBroadcast(intentToReceiveOrder);
                       //     }
                       // });
                       // builder.setNegativeButton("否",null);
                       // builder.show();
                        final Dialog dialog = new Dialog(mContext,R.style.ActionSheetDialogStyle);
                        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_delete_dingdan, null);
                        TextView no=inflate.findViewById(R.id.no);
                        TextView yes = inflate.findViewById(R.id.yes);
                        TextView jianjie=inflate.findViewById(R.id.jianjie);
                        TextView describe=inflate.findViewById(R.id.describe);
                        jianjie.setText("确认收货");
                        describe.setText("确认已收到药品？");
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                intentToReceiveOrder.putExtra("receive",bean.getOrderId());
                                mContext.sendBroadcast(intentToReceiveOrder);
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
                });
            }

        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

}

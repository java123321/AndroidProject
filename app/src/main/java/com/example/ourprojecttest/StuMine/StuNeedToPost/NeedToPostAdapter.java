package com.example.ourprojecttest.StuMine.StuNeedToPost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.R;
import com.example.ourprojecttest.StuMine.StuNeedToPay.ContentInfoBean;
import com.example.ourprojecttest.StuMine.StuNeedToPay.HeadInfoBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NeedToPostAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
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

        public FootViewHolder(@NonNull View itemView) {
            super(itemView);

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
           // view=View.inflate(mContext, R.layout.need_to_pay_content,null);
           view= LayoutInflater.from(mContext).inflate(R.layout.need_to_pay_content,parent,false);
            return new ContentViewHolder(view);
        }
        else if (viewType==ITEM_HEADER){
            //view=View.inflate(mContext,R.layout.need_to_pay_header,null);
            view=LayoutInflater.from(mContext).inflate(R.layout.need_to_pay_header,parent,false);
            return new HeadViewHolder(view);
        }
        else{
           view= LayoutInflater.from(mContext).inflate(R.layout.stu_need_to_post,parent,false);
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
        else{//绑定订单尾部

        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}

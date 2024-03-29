package com.example.ourprojecttest.StuDiagnosis;


import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.Utils.Roundimage;

import java.util.ArrayList;

/**
 * Created by XQS on 2017/10/3 0003.
 */

public  class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private ArrayList<Msg>mMsgList;

    Bitmap left;
    Bitmap right;
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        Roundimage iconLeft;
        Roundimage iconRight;

        public ViewHolder(View view)
        {
            super(view);

            iconLeft=view.findViewById(R.id.chatIconLeft);
            iconRight=view.findViewById(R.id.chatIconRight);
            leftLayout=view.findViewById(R.id.left_layout);
            rightLayout=view.findViewById(R.id.right_layout);
            leftMsg=view.findViewById(R.id.left_msg);
            rightMsg=view.findViewById(R.id.right_msg);
        }
    }
    public MsgAdapter(ArrayList<Msg> msgList, Bitmap left, Bitmap right)
    {
        mMsgList=msgList;
        this.left=left;
        this.right=right;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder,int position)
    {
        Msg msg=mMsgList.get(position);
        if(msg.getType()==Msg.TYPE_RECEIVED)
        {
            //如果是收到的消息则显示左边的布局,将右边的布局隐藏掉
            holder.leftLayout.setVisibility(View.VISIBLE);
            if(left!=null){
                holder.iconLeft.setImageBitmap(left);
            }

            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        }
        else if(msg.getType()==Msg.TYPE_SENT)
        {
            //如果是收到的消息则显示右边的布局，将左边的布局隐藏掉
            holder.rightLayout.setVisibility(View.VISIBLE);

            holder.iconRight.setImageBitmap(right);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
    }
    @Override
    public int getItemCount()
    {
        return mMsgList.size();
    }
}

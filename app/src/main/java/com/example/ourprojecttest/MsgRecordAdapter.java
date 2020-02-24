package com.example.ourprojecttest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MsgRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ArrayList<ChatMsgItem> mList;
    private Context mContext;
    public MsgRecordAdapter(Context context){
        mContext=context;
    }
public void setmList(ArrayList<ChatMsgItem> list){
        mList=list;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        Roundimage chatIcon;
        TextView chatName;
        TextView chatTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatIcon=itemView.findViewById(R.id.chatIcon);
            chatName=itemView.findViewById(R.id.chatName);
            chatTime=itemView.findViewById(R.id.chatTime);
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=View.inflate(mContext,R.layout.msg_record_item,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMsgItem info=mList.get(position);
        ((ViewHolder) holder).chatIcon.setImageBitmap(info.getIcon());
        ((ViewHolder) holder).chatName.setText(info.getName());
        ((ViewHolder) holder).chatTime.setText(info.getTime());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

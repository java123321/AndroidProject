package com.example.ourprojecttest;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ourprojecttest.StuMessage.DisplayMessageDetail;
import java.util.ArrayList;
public class MsgRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private CommonMethod method=new CommonMethod();
    private ArrayList<MessageBean> mList;
    private Context mContext;
    public MsgRecordAdapter(Context context){
        mContext=context;
        Log.d("msginit","context");
    }
public void setmList(ArrayList<MessageBean> list){
        mList=list;
        Log.d("msginit","123");
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        Roundimage chatIcon;
        TextView chatName;
        TextView chatTime;
        LinearLayout messageItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatIcon=itemView.findViewById(R.id.chatIcon);
            chatName=itemView.findViewById(R.id.chatName);
            chatTime=itemView.findViewById(R.id.chatTime);
            messageItem=itemView.findViewById(R.id.messageItem);
            Log.d("msginit","viewholder");
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=View.inflate(mContext,R.layout.msg_record_item,null);
        ViewHolder holder=new ViewHolder(view);
        Log.d("msginit","oncreate");
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       final MessageBean info=mList.get(position);
        Log.d("msginit","position"+position);
        byte[] appIcon=info.getIcon();
        ((ViewHolder) holder).chatIcon.setImageBitmap(BitmapFactory.decodeByteArray(appIcon,0,appIcon.length));
        ((ViewHolder) holder).chatName.setText(info.getName());
        ((ViewHolder) holder).chatTime.setText(info.getTime());
        Log.d("msginit","time"+info.getTime());
        //点击消息项目之后进入聊天窗口
        ((ViewHolder) holder).messageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, DisplayMessageDetail.class);
                intent.putExtra("name",info.getName());
                intent.putExtra("icon",info.getIcon());
                method.writeMessageContentIntoSDcard("MsgContent",info.getMsgList());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("msginit","count1:"+mList.size()+"");
        return mList.size();
    }
}

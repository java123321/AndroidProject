package com.example.ourprojecttest.StuDiagnoseRecord;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.StuDiagnosis.MessageBean;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Utils.Roundimage;
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
        Button delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            delete=itemView.findViewById(R.id.delete_button);
            chatIcon=itemView.findViewById(R.id.chatIcon);
            chatName=itemView.findViewById(R.id.chatName1);
            chatTime=itemView.findViewById(R.id.chatTime);
            messageItem=itemView.findViewById(R.id.messageItem);
            Log.d("msginit","viewholder");
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=View.inflate(mContext,R.layout.swipe_item,null);
        ViewHolder holder=new ViewHolder(view);
        Log.d("msginit","oncreate");
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
       final MessageBean info=mList.get(position);
       ViewHolder viewHolder=(ViewHolder)holder;
        Log.d("msginit","position"+position);
        byte[] appIcon=info.getIcon();
        viewHolder.chatIcon.setImageBitmap(BitmapFactory.decodeByteArray(appIcon,0,appIcon.length));
        viewHolder.chatName.setText("接诊医生:"+info.getName());
        viewHolder.chatTime.setText("问诊事件"+info.getTime());
        Log.d("msginit","time"+info.getTime());
        //点击消息项目之后进入聊天窗口
        viewHolder.messageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, DisplayRecordDetail.class);
                intent.putExtra("name",info.getName());
                intent.putExtra("icon",info.getIcon());
                method.writeMessageContentIntoSDcard("MsgContent",info.getMsgList());
                mContext.startActivity(intent);
            }
        });
        //设置删除按钮的点击事件
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        Log.d("msginit","count1:"+mList.size()+"");
        return mList.size();
    }
}

package com.example.ourprojecttest.StuDiagnosis;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Utils.Roundimage;

import java.util.ArrayList;


public class DisplayDocAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private CommonMethod method=new CommonMethod();
    private ArrayList<DisplayDocBean> mList=new ArrayList<>();
    private Context mContext;
    public DisplayDocAdapter(Context context){
        this.mContext=context;
    }

    public void setList(ArrayList<DisplayDocBean> list){
        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.stu_display_doc_item, null);
        final ViewHolder holder=new ViewHolder(view);

        return holder;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        Roundimage docIcon;
        TextView docName;
        TextView docBrief;
        TextView docSex;
        private LinearLayout item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            docSex=itemView.findViewById(R.id.docSex);
            docIcon=itemView.findViewById(R.id.docIcon);
            docName=itemView.findViewById(R.id.docName);
            docBrief=itemView.findViewById(R.id.docBriefIntroduction);
            item=itemView.findViewById(R.id.displayDocItem);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

         final DisplayDocBean info=mList.get(position);
        ((ViewHolder) holder).docIcon.setImageBitmap(info.getIcon());
        ((ViewHolder) holder).docName.setText("姓名:"+info.getName());
        ((ViewHolder) holder).docBrief.setText("简介:"+info.getBrief());
        ((ViewHolder) holder).docSex.setText("性别:"+info.getSex());
        //设置显示在线医生的点击事件
        ((ViewHolder) holder).item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,OnlineDocDetail.class);
                intent.putExtra("docIcon",method.bitmap2Bytes(info.getIcon()));
                intent.putExtra("docName",info.getName());
                intent.putExtra("docBrief",info.getBrief());
                intent.putExtra("docSex",info.getSex());
                intent.putExtra("docLicense",method.bitmap2Bytes(info.getLicense()));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

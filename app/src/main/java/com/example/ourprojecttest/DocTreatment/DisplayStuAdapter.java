package com.example.ourprojecttest.DocTreatment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Utils.Roundimage;

import java.util.ArrayList;


public class DisplayStuAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<DisplayStuBean> mList=new ArrayList<>();
    private Context mContext;
    public DisplayStuAdapter(Context context){
        this.mContext=context;
    }

    public void setList(ArrayList<DisplayStuBean> list){
        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.doc_display_stu_item, null);
        final ViewHolder holder=new ViewHolder(view);

        return holder;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        Roundimage stuIcon;
        TextView stuName;
        TextView stuAge;
        TextView stuHeight;
        TextView stuWeight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stuIcon=itemView.findViewById(R.id.stuIcon);
            stuName=itemView.findViewById(R.id.stuName);
            stuAge=itemView.findViewById(R.id.stuAge);
            stuHeight =itemView.findViewById(R.id.stuShengao);
            stuWeight =itemView.findViewById(R.id.stuTizhong);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        DisplayStuBean info=mList.get(position);
        ((ViewHolder) holder).stuIcon.setImageBitmap(info.getIcon());
        ((ViewHolder) holder).stuName.setText("姓名:"+info.getName());
        ((ViewHolder) holder).stuAge.setText("出生年月:"+info.getBirthday());
        ((ViewHolder) holder).stuHeight.setText("身高:"+info.getHeight());
        ((ViewHolder) holder).stuWeight.setText("体重:"+info.getWeight());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

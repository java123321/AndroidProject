package com.example.ourprojecttest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class DisplayStuAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<DisplayStuList> mList;
    private Context mContext;
    private View view;

    public DisplayStuAdapter(Context context){
        this.mContext=context;
    }

    public void setList(ArrayList<DisplayStuList> list){
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
        TextView stuShengao;
        TextView stuTizhong;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stuIcon=itemView.findViewById(R.id.stuIcon);
            stuName=itemView.findViewById(R.id.stuName);
            stuAge=itemView.findViewById(R.id.stuAge);
            stuShengao=itemView.findViewById(R.id.stuShengao);
            stuTizhong=itemView.findViewById(R.id.stuTizhong);

        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        DisplayStuList info=mList.get(position);
        ((ViewHolder) holder).stuIcon.setImageBitmap(info.getIcon());
        ((ViewHolder) holder).stuName.setText(info.getName());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

package com.example.ourprojecttest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class DisplayDocAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<DisplayDocList> mList;
    private Context mContext;
    private View view;

    public DisplayDocAdapter(Context context){
        this.mContext=context;
    }

    public void setList(ArrayList<DisplayDocList> list){
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            docIcon=itemView.findViewById(R.id.docIcon);
            docName=itemView.findViewById(R.id.docName);
            docBrief=itemView.findViewById(R.id.docBriefIntroduction);

        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

         DisplayDocList info=mList.get(position);
        ((ViewHolder) holder).docIcon.setImageBitmap(info.getIcon());
        ((ViewHolder) holder).docName.setText(info.getName());
        ((ViewHolder) holder).docBrief.setText(info.getBrief());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

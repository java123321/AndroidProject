package com.example.ourprojecttest.DocTreatment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Utils.Roundimage;

import java.util.ArrayList;
import java.util.Calendar;


public class DisplayStuAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public ArrayList<DisplayStuBean> mList=new ArrayList<>();
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
        View view= LayoutInflater.from(mContext).inflate(R.layout.doc_display_stu_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);

        return holder;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        Roundimage stuIcon;
        TextView stuName;
        TextView stuAge;
        TextView stuHeight;
        TextView stuWeight;
        TextView stuSex;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stuIcon=itemView.findViewById(R.id.stuIcon);
            stuName=itemView.findViewById(R.id.stuName);
            stuAge=itemView.findViewById(R.id.stuAge);
            stuHeight =itemView.findViewById(R.id.stuShengao);
            stuWeight =itemView.findViewById(R.id.stuTizhong);
            stuSex=itemView.findViewById(R.id.stuSex);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DisplayStuBean info=mList.get(position);
        String [] arrs;
        arrs=(info.getBirthday()).split("-");
        String age1=String.valueOf(getAge(arrs[0],arrs[1],arrs[2]));
        ViewHolder viewHolder= ((ViewHolder) holder);
        if(info.getIcon()!=null){
            viewHolder.stuIcon.setImageBitmap(info.getIcon());
        }
        viewHolder.stuName.setText(info.getName());
        viewHolder.stuSex.setText(info.getSex());
        viewHolder.stuAge.setText(age1+" 岁");
        viewHolder.stuHeight.setText(info.getHeight()+" cm");
        viewHolder.stuWeight.setText(info.getWeight()+" kg");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    public  String getAge(String year,String month,String day){
        Calendar cal = Calendar.getInstance();
        int Year=Integer.parseInt(year);
        int Month=Integer.parseInt(month);
        int Day=Integer.parseInt(day);
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow -Year;   //计算整岁数
        if (monthNow <=Month ) {
            if (monthNow == Month) {
                if (dayOfMonthNow < Day)
                    age--;//当前日期在生日之前，年龄减一
            } else {
                age--;//当前月份在生日之前，年龄减一
            }
        }
        if(age<0)
            age=0;
        return Integer.toString(age);
    }
}


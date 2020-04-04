package com.example.ourprojecttest.StuMine.StuInfomation;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.String;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.R;
import com.example.ourprojecttest.StuMine.NumPicker;
import com.example.ourprojecttest.StuMine.Tubiao;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
public class ModifyAdapter extends RecyclerView.Adapter<ModifyAdapter.ViewHolder> implements Serializable {
    private Display display;
    private int toastHeight;
    private List<Tubiao> tubiaos;
    private NumPicker numPicker;
    private Numpickerr numpickerr;
    private Dialog dialog;
    private int mYear,mDay,mMonth,DATE_DIALOG=1;
    String s="男";
    static class ViewHolder extends RecyclerView.ViewHolder {
        View tubiaoView;
        ImageView image;
        TextView name;
        LinearLayout layout;
        TextView xinxi;
        public ViewHolder(View view) {
            super(view);
            tubiaoView = view;
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            xinxi=view.findViewById(R.id.xinxi);
            layout =  view.findViewById(R.id.layout);
        }
    }

    public ModifyAdapter() {
    }

    public List<Tubiao> setList(List<Tubiao> a) {
        tubiaos = a;
        return tubiaos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_modify, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Tubiao tubiao = tubiaos.get(position);
        holder.image.setImageResource(tubiao.getImageId());
        holder.xinxi.setText(tubiao.getXinxi());
        holder.name.setText(tubiao.getName());
        holder.tubiaoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Activity m1=(Activity)view.getContext();
                int position = holder.getAdapterPosition();
                Intent intent;
                final View inflate = LayoutInflater.from(view.getContext()).inflate(R.layout.student_sex, null);
                TextView t=inflate.findViewById(R.id.quding);
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent();
                        intent.setAction("Sex");
                        intent.putExtra("sex",s);
                        view.getContext().sendBroadcast(intent);
                        dialog.dismiss();
                    }
                });
                switch (position) {
                    case 0:
                        intent = new Intent(view.getContext(), ModefyStuName.class);
                        view.getContext().startActivity(intent);
                        break;
                    case 1:
                        Toast.makeText(view.getContext(), "邮箱不支持修改哦 ", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                         s="男";
                         dialog = new Dialog(view.getContext(), R.style.ActionSheetDialogStyle);        //展示对话框
                        //填充对话框的布局
                       final RadioGroup radioGroup=inflate.findViewById(R.id.radioGroup);
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                RadioButton rb = (RadioButton)inflate.findViewById(radioGroup.getCheckedRadioButtonId());
                                s=rb.getText().toString();
                            }
                        });
                        //初始化控件
                        //将布局设置给Dialog
                        dialog.setContentView(inflate);
                        //获取当前Activity所在的窗体
                        Window dialogWindow = dialog.getWindow();
                        //设置Dialog从窗体底部弹出
                        dialogWindow.setGravity(Gravity.BOTTOM);
                        //获得窗体的属性
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        lp.y = 20;//设置Dialog距离底部的距离
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//弹框宽度充斥整个屏幕
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;//       将属性设置给窗体
                        dialogWindow.setAttributes(lp);
                        dialog.show();//显示对话框
                        break;
                    case 3:
                        numPicker=new NumPicker(m1);
                        numPicker.show();
                        numPicker.setContext(view.getContext());
                        break;
                    case 4:
                        numpickerr=new Numpickerr(m1);
                        numpickerr.show();
                        numpickerr.setContext(view.getContext());
                        break;
                    case 5:
                        final Calendar ca = Calendar.getInstance();
                        mYear = ca.get(Calendar.YEAR);
                        mMonth = ca.get(Calendar.MONTH);
                        mDay = ca.get(Calendar.DAY_OF_MONTH);
                        new DatePickerDialog(view.getContext(), mdateListener, mYear, mMonth, mDay).show();
                        break;

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tubiaos.size();
    }
    protected Dialog onCreateDialog(int id,View view) {
        switch (id) {
            case 1:
                return new DatePickerDialog(view.getContext(),mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void display(View view) {
        Intent intent=new Intent();
        intent.setAction("Birth");


        intent.putExtra("mYear",Integer.toString(mYear));
        intent.putExtra("mMonth",Integer.toString(mMonth+1));
        intent.putExtra("mDay",Integer.toString(mDay));
        view.getContext().sendBroadcast(intent);
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display(view);
        }
    };

}
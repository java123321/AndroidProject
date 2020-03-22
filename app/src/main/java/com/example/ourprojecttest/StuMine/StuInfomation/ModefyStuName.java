package com.example.ourprojecttest.StuMine.StuInfomation;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.R;

public class ModefyStuName extends AppCompatActivity {
    private EditText xingming;
    private TextView baocun;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nicheng_student);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        baocun=findViewById(R.id.baocun);
        xingming= findViewById(R.id.xingming);
        baocun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=xingming.getText().toString();
                Intent intent=new Intent();
                intent.setAction("Name");
                intent.putExtra("name",s);
                sendBroadcast(intent);
            }
        });
        xingming.setOnTouchListener(new View.OnTouchListener() {
            Drawable drawable = xingming.getCompoundDrawables()[2];

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                //获取点击焦点
                if (event.getX() > xingming.getWidth() - xingming.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    //其他活动无响应
                    if (event.getAction() != MotionEvent.ACTION_UP)
                        return false;
                    //清空用户名
                    xingming.setText("");
                }
                return false;
            }
        });
    }
}

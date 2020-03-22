package com.example.ourprojecttest.DocMine.DocInformation;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.StuMine.StuInfomation.ModifyAdapter;
import com.example.ourprojecttest.R;

public class ModefyDocName extends AppCompatActivity {
    private EditText xingming;
    private ImageView back;
    private ModifyAdapter adapter;
    private TextView baocun;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        setContentView(R.layout.activity_doc_name);
        baocun=findViewById(R.id.baocun);
        xingming= findViewById(R.id.xingming);
        baocun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=xingming.getText().toString();
                Intent intent=new Intent();
                intent.setAction("Name");
                intent.putExtra("name",s);
                Toast.makeText(ModefyDocName.this, "保存成功！", Toast.LENGTH_SHORT).show();
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

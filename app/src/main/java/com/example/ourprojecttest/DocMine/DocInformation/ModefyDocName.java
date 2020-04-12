package com.example.ourprojecttest.DocMine.DocInformation;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Display;
import android.view.Gravity;
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
    private Display display;
    private int toastHeight;
    private Drawable searchEditDraw;
    private EditText xingming;
    private ImageView back;
    private ModifyAdapter adapter;
    private TextView baocun;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        setContentView(R.layout.activity_doc_name);
        display = getWindowManager().getDefaultDisplay();
        toastHeight = display.getHeight();
        baocun=findViewById(R.id.baocun);
        xingming= findViewById(R.id.xingming);
        searchEditDraw = getResources().getDrawable(R.drawable.chahao);
        searchEditDraw.setBounds(0, 0, 60, 60);
        xingming.setCompoundDrawables(null, null, searchEditDraw, null);
        baocun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=xingming.getText().toString();
                Intent intent=new Intent();
                intent.setAction("Name");
                intent.putExtra("name",s);

                Toast toast = Toast.makeText(ModefyDocName.this, "保存成功！", Toast.LENGTH_SHORT);
                // 这里给了一个1/4屏幕高度的y轴偏移量
                toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                toast.show();
                sendBroadcast(intent);
            }
        });

        xingming.setOnTouchListener(new View.OnTouchListener() {
            Drawable drawable = xingming.getCompoundDrawables()[2];

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                //获取点击焦点
                if (event.getX() > xingming.getWidth() - xingming.getPaddingRight() - 60) {
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

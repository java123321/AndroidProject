package com.example.ourprojecttest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.util.ArrayList;

public class DocOperatActivity extends AppCompatActivity {

    Intent intentToService=new Intent("com.example.ourprojecttest.DOC_UPDATE_SERVICE");//改
    LocalReceiver localReceiver;
    IntentFilter intentFilter;
    private RecyclerView mRecycler;
    private Button view;
    private Button access;
    private DisplayStuAdapter adapter;
    private ArrayList<DisplayStuList> lists=new ArrayList<>();
    private String flag = "";
    String stuID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_operat);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        //开始注册广播监听器，准备接受服务里发送过来的更新挂号信息
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.DOC_UPDATE_PERSONS");//改
        localReceiver=new LocalReceiver();
        getApplicationContext().registerReceiver(localReceiver,intentFilter);
        Log.d("目的","监听学生人数开始");
    }

    private void initView(){

        //创建一个服务
        Intent intentStartService = new Intent(DocOperatActivity.this, DocService.class);
        startService(intentStartService);

        //如果有状态码state代表用户从前台服务跳进来
        Intent intent=getIntent();
        if(intent.hasExtra("state")){
            //如果是-1代表当前是
            if(intent.getStringExtra("state").equals(-1)){

            } else{

            }

        }

        view=findViewById(R.id.view);
        access=findViewById(R.id.access);
        mRecycler=findViewById(R.id.docDisplayStu);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        adapter = new DisplayStuAdapter(DocOperatActivity.this);
        mRecycler.setAdapter(adapter);

        DisplayStuList d=new DisplayStuList();
        d.setIcon(Rfile2Bitmap());
        d.setName("华佗");
        d.setShengao(181l);
        d.setTizhong(99l);
        lists.add(d);
        d=new DisplayStuList();
        d.setIcon(Rfile2Bitmap());
        d.setName("李时珍");
        d.setShengao(191l);
        d.setTizhong(88l);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        adapter.setList(lists);
        adapter.notifyDataSetChanged();

        //查看学生排队人数
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //给服务器发送查看队列的广播
                intentToService.putExtra("msg","View");//改了
                sendBroadcast(intentToService);
                Log.d("线程信息","："+Thread.currentThread().getId()+"   " +
                        "查看挂号人数");

            }
        });
        //医生接入学生事件
        access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //给服务器发送查看队列的广播
                intentToService.putExtra("msg","Access");//改了
                sendBroadcast(intentToService);
                Log.d("线程信息","："+Thread.currentThread().getId()+"  " +
                        "弹出聊天消息");
            }
        });

    }
    private Bitmap Rfile2Bitmap(){
        return BitmapFactory.decodeResource(getResources(),R.drawable.person);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("候诊页面状态","onResume");

    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("候诊页面状态","onPause");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("候诊页面状态","onStart");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("候诊页面状态","onRestart");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("候诊页面状态","onStope");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("候诊页面状态","onDestroy");
    }
    /**
     * 接收服务里传过来的挂号更新信息
     */
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("docop","received");

            Log.d("docop","has chatmsg?"+intent.hasExtra("chatmsg"));
            Log.d("docop","has validate?"+intent.hasExtra("validate"));
            //如果学生发送的是正常聊天消息
            if(intent.hasExtra("chatmsg")){

            }
            else{//如果学生发送的是chat或者deny
                String validateResult=intent.getStringExtra("validate");
                Log.d("docop",validateResult);
                //如果学生拒绝了和医生沟通
                if(validateResult.equals("deny")){

                }
                else{//如果学生统一和医生沟通
                    Log.d("docop","intoChat");
                    Intent intentToChat=new Intent(DocOperatActivity.this,Chat.class);
                    startActivity(intentToChat);
                }
            }
        }
    }

}

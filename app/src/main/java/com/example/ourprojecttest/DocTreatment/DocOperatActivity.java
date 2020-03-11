package com.example.ourprojecttest.DocTreatment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.ourprojecttest.StuDiagnosis.Chat;
import com.example.ourprojecttest.DocService;
import com.example.ourprojecttest.ImmersiveStatusbar;
import com.example.ourprojecttest.R;

import java.util.ArrayList;

public class DocOperatActivity extends AppCompatActivity {

    Intent intentToService=new Intent("com.example.ourprojecttest.DOC_UPDATE_SERVICE");//改
    LocalReceiver localReceiver;
    IntentFilter intentFilter;
    private RecyclerView mRecycler;
    private Button view;
    private Button access;
    private DisplayStuAdapter adapter;
    private ProgressDialog waitingDialog;
    CountDownTimer cdt;
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
        //联网获取数据


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

        cdt = new CountDownTimer(10000,1000) {
            int i = 10;
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                if (waitingDialog != null){
                    Log.d("dialog","等待消失");
                    waitingDialog.dismiss();
                }
            }
        };

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
    protected void onDestroy() {
        super.onDestroy();
        Log.d("候诊页面状态","onDestroy");
    }


    @Override  //退出接诊活动时弹出提示框
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder bdr=new AlertDialog.Builder(this);
            bdr.setMessage("确定要退出接诊吗?");
            bdr.setNegativeButton("取消",null);
            bdr.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //通知医生服务退出看病
                intentToService.putExtra("msg","exit");
                sendBroadcast(intentToService);
                finish();
                }
            });
            bdr.show();
        }
        return false;
    }


    /**
     * 接收服务里传过来的挂号更新信息
     */
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra("Dialog")){
                Log.d("学生邀请","开始");

                waitingDialog= new ProgressDialog(DocOperatActivity.this);
                waitingDialog.setTitle("我是一个等待Dialog");
                waitingDialog.setMessage("等待中...");
                waitingDialog.setIndeterminate(true);
                waitingDialog.setCancelable(false);
                waitingDialog.show();

            }else {
                Log.d("docop","received");

                Log.d("docop5","has chatmsg?"+intent.hasExtra("chatmsg"));
                Log.d("docop6","has chatmsg?"+intent.hasExtra("chatmsg"));
                Log.d("docop","has validate?"+intent.hasExtra("validate"));
                //如果学生发送的是正常聊天消息
                if(intent.hasExtra("chatmsg")){
                    Log.d("docop1","chatmsgis:"+intent.getStringExtra("chatmsg"));
                }
                else{//如果学生发送的是chat或者deny
                    String validateResult=intent.getStringExtra("validate");
                    Log.d("docop",validateResult);
                    //如果学生拒绝了和医生沟通
                    if(validateResult.contains("deny")){
                        Toast.makeText(context, "当前学生:"+validateResult.substring(4)+"放弃了接诊", Toast.LENGTH_SHORT).show();
                        if (waitingDialog != null){
                            waitingDialog.dismiss();
                        }
                    }
                    else{//如果学生统一和医生沟通
                        Log.d("docop","intoChat");
                        Intent intentToChat=new Intent(DocOperatActivity.this, Chat.class);
                        intentToChat.putExtra("stuId",validateResult);
                        intentToChat.putExtra("stuName",intent.getStringExtra("stuName"));
                        intentToChat.putExtra("stuPicture",intent.getByteArrayExtra("stuPicture"));
                        startActivity(intentToChat);
                    }
                    }

            }
        }
    }

}

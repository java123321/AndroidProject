package com.example.ourprojecttest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class Chat extends AppCompatActivity {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;

    Intent intentToService=new Intent("com.example.ourprojecttest.UPDATE_SERVICE");
    CommonMethod method=new CommonMethod();
    private List<Msg> msgList=new ArrayList<>();
    private EditText inputText;
    private Button send;
    private Button close;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    String stuOrDocId;
    Boolean stuOrDoc; //true是学生，false是医生;
    private ImageView video;
    TextView chatName;
    IntentFilter intentFilter;
    LocalReceiver localReceiver;

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("ReceiveMsg")) {
                update(intent.getStringExtra("ReceiveMsg"), TYPE_RECEIVED);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.UPDATE_PERSONS");
        localReceiver=new LocalReceiver();
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);
    }

    @Override
    public void onBackPressed(){
        new  AlertDialog.Builder(Chat.this)
                .setTitle("退出" )
                .setMessage("是否退出聊天")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//如果用户点击了确定按钮则进入与医生的聊天界面
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        intentToService.putExtra("chatMsg",stuOrDocId+"|再见！");
                        sendBroadcast(intentToService);
                       Intent intent = new Intent();
                       setResult(RESULT_OK,intent);
                       finish();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
    private void initView(){
        //与服务器建立websocket连接
        inputText=findViewById(R.id.input_text);
        send=findViewById(R.id.send);
        close=findViewById(R.id.close);
        msgRecyclerView=findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter=new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        video=findViewById(R.id.videoChat);

        //设置视频聊天的点击事件
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //如果是医生登录的话，填写要聊天的学生id
        if(method.getFileData("Type",Chat.this).equals("Doc")){
            stuOrDocId = getIntent().getStringExtra("stuId").trim();
            stuOrDoc = false;
        }
        else {
            stuOrDocId="11111";
            stuOrDoc = true;
        }
        Log.d("chat",stuOrDocId);

        send.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String content=inputText.getText().toString();
                        if(!"".equals(content))
                        {
                            update(content,TYPE_SENT);
                            intentToService.putExtra("chatMsg",stuOrDocId+"|"+content);
                            sendBroadcast(intentToService);
                        }
                    }
                });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                intentToService.putExtra("chatMsg",stuOrDocId+"|再见！");
                sendBroadcast(intentToService);

                if (stuOrDoc){
                    //学生关闭后的操作
                }else {
                    //医生关闭后的操作
                    new  AlertDialog.Builder(Chat.this)
                            .setTitle("操作" )
                            .setMessage("是否给此学生开订单")
                            .setPositiveButton("开单", new DialogInterface.OnClickListener() {//如果用户点击了确定按钮则进入与医生的聊天界面
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    StuId.stuId = stuOrDocId;
                                    Log.d("学生ID",StuId.stuId);
                                    Intent intent=new Intent(Chat.this,ShoppingCartActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("不开单", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent=new Intent(Chat.this,DocOperatActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();

                }
            }
        });
    }


    //该方法用于将消息更新显示到RecyclerView里
    private void update(String content,int type){
        Msg msg=new Msg(content,type);
        msgList.add(msg);
        adapter.notifyItemInserted(msgList.size()-1);
        msgRecyclerView.scrollToPosition(msgList.size()-1);

        //如果是发送者发送消息则清空输入文本框中的数据
        if(type==TYPE_SENT)
        inputText.setText("");//清空输入框中的内容
    }


}

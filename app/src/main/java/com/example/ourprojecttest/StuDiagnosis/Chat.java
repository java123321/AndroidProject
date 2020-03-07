package com.example.ourprojecttest.StuDiagnosis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ourprojecttest.CommonMethod;
import com.example.ourprojecttest.DocTreatment.Prescribe;
import com.example.ourprojecttest.ImmersiveStatusbar;
import com.example.ourprojecttest.MessageBean;
import com.example.ourprojecttest.Msg;
import com.example.ourprojecttest.MsgAdapter;
import com.example.ourprojecttest.PictureStore;
import com.example.ourprojecttest.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Chat extends AppCompatActivity {
    String Type;//代表是学生登录还是医生登录
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;

    Intent intentToStu =new Intent("com.example.ourprojecttest.UPDATE_SERVICE");//将学生的消息传给学生服务
    Intent intentToDoc=new Intent("com.example.ourprojecttest.DOC_UPDATE_SERVICE");//将医生的消息传给医生服务

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
    CommonMethod method=new CommonMethod();
    MessageBean messageBean=new MessageBean();
    String time;
    private ArrayList<Msg> msgList=new ArrayList<>();
    private EditText inputText;
    private TextView chatName;
    private Button send;
    private Button close;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    String stuOrDocId;
    Boolean stuOrDoc; //true是学生，false是医生;
    private ImageView video;
    TextView chatWindowName;//聊天窗口顶部的名字
    IntentFilter intentFilter;
    LocalReceiver localReceiver;

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("ReceiveMsg")) {
                update(intent.getStringExtra("ReceiveMsg"), TYPE_RECEIVED);
                Log.d("ReceiveMsg",intent.getStringExtra("ReceiveMsg"));
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        time=df.format(new Date());
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.ChatMessage");//该意图是接受服务里传来的聊天信息
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
                        intentToStu.putExtra("chatMsg",stuOrDocId+"|再见！");
                        sendBroadcast(intentToStu);
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
        Type=method.getFileData("Type",Chat.this);
        chatWindowName=findViewById(R.id.chatWindowName);//
        inputText=findViewById(R.id.input_text);
        send=findViewById(R.id.send);
        close=findViewById(R.id.close);
        msgRecyclerView=findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        //如果是医生登录的话，填写要聊天的学生id
        Intent intent=getIntent();
        if(Type.equals("Doc")){
            //如果是医生登录，则设置开处方可见
            close.setVisibility(View.VISIBLE);

            stuOrDocId = intent.getStringExtra("stuId").trim();
            String stuName=intent.getStringExtra("stuName").trim();
            messageBean.setName(stuName);
            chatWindowName.setText(stuName+"同学");
            //获取学生头像
            byte[] stuIcon=intent.getByteArrayExtra("stuPicture");
            messageBean.setIcon(stuIcon);
            Log.d("chat","stuIcon"+(stuIcon==null));
            Bitmap stuPicture=BitmapFactory.decodeByteArray(stuIcon,0,stuIcon.length);
            //获取医生的头像
            PictureStore pictureStore=(PictureStore)method.readObjFromSDCard("DocIcon");
            Bitmap docPicture=null;
            if(pictureStore.getFlag()){
                byte[] docIcon=pictureStore.getPicture();
                docPicture=BitmapFactory.decodeByteArray(docIcon,0,docIcon.length);
            }
            adapter=new MsgAdapter(msgList,stuPicture,docPicture);
            stuOrDoc = false;
        }
        else {//如果是学生登录的话，则填写要聊天的医生id
            //如果是学生登录，则设置开处方不可见
            close.setVisibility(View.INVISIBLE);

            stuOrDocId=intent.getStringExtra("docId").trim();
            String docName=intent.getStringExtra("docName").trim();
            messageBean.setName(docName);
            chatWindowName.setText(docName+"医生");
            //获取医生的头像
            byte[] docIcon=intent.getByteArrayExtra("docPicture");
            messageBean.setIcon(docIcon);
            Log.d("chat","docIcon"+(docIcon==null));
            Bitmap docPicture=BitmapFactory.decodeByteArray(docIcon,0,docIcon.length);
            //获取学生的头像
            PictureStore pictureStore=(PictureStore)method.readObjFromSDCard("Icon");
            Bitmap stuPicture=null;
            if(pictureStore.getFlag()){
                byte[] stuIcon=pictureStore.getPicture();
                stuPicture=BitmapFactory.decodeByteArray(stuIcon,0,stuIcon.length);
            }
            adapter=new MsgAdapter(msgList,docPicture,stuPicture);
            stuOrDoc = true;
        }
        Log.d("chatId",stuOrDocId);

        msgRecyclerView.setAdapter(adapter);
        video=findViewById(R.id.videoChat);
        //设置视频聊天的点击事件
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
                            //如果是学生登录则发送给学生服务
                            if(Type.equals("Stu")){
                                intentToStu.putExtra("sendMsg",stuOrDocId+"|"+content);
                                sendBroadcast(intentToStu);
                            }
                            else{//否则将消息发送给医生服务
                                intentToDoc.putExtra("sendMsg",stuOrDocId+"|"+content);
                                sendBroadcast(intentToDoc);
                            }

                        }
                    }
                });
        //设置医生开出方的点击事件
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentToStu.putExtra("chatMsg",stuOrDocId+"|再见！");
                sendBroadcast(intentToStu);

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
                                    Intent intent=new Intent(Chat.this, Prescribe.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("不开单", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
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


    //当退出聊天界面的时候，将聊天记录保存
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
        ArrayList<MessageBean> record=method.readMessageRecordListFromSdCard("MessageRecord");
        if(record==null){
            record=new ArrayList<>();
        }
        messageBean.setTime(time);
        messageBean.setMsgList(msgList);
        record.add(messageBean);
        //将消息记录集合写到sd卡中
        boolean flag=method.writeMessageRecordListIntoSDcard("MessageRecord",record);
        Log.d("chatsave","flag"+flag);
        Log.d("chatsave","save");
        Log.d("chatsave","?1"+(record==null));
        record=null;
        Log.d("chatsave","?12"+(record==null));
        record=method.readMessageRecordListFromSdCard("MessageRecord");
        Log.d("chatsave","?"+(record==null));
    }
}

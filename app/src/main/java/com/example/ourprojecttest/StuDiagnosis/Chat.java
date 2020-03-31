package com.example.ourprojecttest.StuDiagnosis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ourprojecttest.StuDiagnosis.VideoCommunication.CallActivity;
import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.DocTreatment.Prescribe;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.Utils.PictureStore;
import com.example.ourprojecttest.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

public class Chat extends AppCompatActivity {
    private String[] perms= new String[]{//视频聊天需要的权限
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO
    };
    private String type;//代表是学生登录还是医生登录
    private static final int TYPE_RECEIVED = 0;
    private static final int TYPE_SENT = 1;
    private Intent intentToStu = new Intent("com.example.ourprojecttest.UPDATE_SERVICE");//将学生的消息传给学生服务
    private Intent intentToDoc = new Intent("com.example.ourprojecttest.DOC_UPDATE_SERVICE");//将医生的消息传给医生服务
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
    private CommonMethod method = new CommonMethod();
    private MessageBean messageBean = new MessageBean();
    private String time;
    private ArrayList<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private Button prescribe;
    private boolean stuOnline = true;//标记学生在线
    private boolean docOnline = true;//标记医生在线
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private String stuOrDocId;
    private ImageView video;
    private TextView chatWindowName;//聊天窗口顶部的名字
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("ReceiveMsg");
            if (msg.equals("finishChat")) {//如果对方点击返回键退出了聊天
                if (type.equals("Stu")) {//如果当前用户是学生登录

                   // AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
                   // builder.setTitle("提示");
                   // builder.setMessage("当前医生已离开聊天页面，您是否离开当前页面？");
                    //如果用户确定要删除
                   // builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    //    @Override
                    //    public void onClick(DialogInterface dialogInterface, int ii) {
                    //        finish();
                    //    }
                    //});
                    //builder.setNegativeButton("取消", null);
                    //builder.show();
                    //将医生标记为下线
                    final Dialog dialog = new Dialog(Chat.this,R.style.ActionSheetDialogStyle);        //展示对话框
                    //填充对话框的布局
                    View inflate = LayoutInflater.from(Chat.this).inflate(R.layout.layout_student_tuichu, null);
                    //初始化控件
                    TextView yes = inflate.findViewById(R.id.yes);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
                    TextView no = inflate.findViewById(R.id.no);
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    //将布局设置给Dialog
                    dialog.setContentView(inflate);
                    //获取当前Activity所在的窗体

                    Window dialogWindow = dialog.getWindow();
                    //设置Dialog从窗体底部弹出
                    dialogWindow.setGravity( Gravity.CENTER);
                    //获得窗体的属性
                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    lp.width =800;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialogWindow.setAttributes(lp);
//       将属性设置给窗体
                    dialog.show();//显示对话框
                    docOnline = false;

                } else {//如果当前用户是医生登录
                 //   AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
                 //   builder.setTitle("提示");
                 //   builder.setMessage("当前学生已离开聊天页面，您是否需要为其开处方？");
                    //如果用户确定要删除
                 //   builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                 //       @Override
                 //       public void onClick(DialogInterface dialogInterface, int ii) {
                 //           Intent intent = new Intent(Chat.this, Prescribe.class);
                 //           intent.putExtra("stuId", stuOrDocId);
                 //           startActivity(intent);
                 //       }
                 //   });
                 //   builder.setNegativeButton("取消", null);
                 //   builder.show();
                    //将学生标记为下线
                    final Dialog dialog = new Dialog(Chat.this,R.style.ActionSheetDialogStyle);        //展示对话框
                    //填充对话框的布局
                    View inflate = LayoutInflater.from(Chat.this).inflate(R.layout.layout_doc_chufang, null);
                    //初始化控件
                    TextView yes = inflate.findViewById(R.id.yes);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Chat.this, Prescribe.class);
                            intent.putExtra("stuId", stuOrDocId);
                            startActivity(intent);
                        }
                    });
                    TextView no = inflate.findViewById(R.id.no);
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    //将布局设置给Dialog
                    dialog.setContentView(inflate);
                    //获取当前Activity所在的窗体

                    Window dialogWindow = dialog.getWindow();
                    //设置Dialog从窗体底部弹出
                    dialogWindow.setGravity( Gravity.CENTER);
                    //获得窗体的属性
                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    lp.width =800;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialogWindow.setAttributes(lp);
//       将属性设置给窗体
                    dialog.show();//显示对话框
                    stuOnline = false;
                }
            } else {
                update(msg, TYPE_RECEIVED);
            }
            Log.d("ReceiveMsg", msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        time = df.format(new Date());
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.ChatMessage");//该意图是接受服务里传来的聊天信息
        localReceiver = new LocalReceiver();
        localReceiver = new LocalReceiver();
        registerReceiver(localReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);

        builder.setTitle("提示");
        if (type.equals("Stu")) {
            //builder.setMessage("是否结束此次问诊?");
            //builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
              //  @Override
              //  public void onClick(DialogInterface dialog, int which) {
                //    intentToStu.putExtra("chatMsg", stuOrDocId + "|finishChat");
                  //  sendBroadcast(intentToStu);
                  //  finish();
                  //  Log.d("wee", "1");
             //   }
          //  }).setNegativeButton("取消", null)
          //          .show();
            final Dialog dialog = new Dialog(this,R.style.ActionSheetDialogStyle);        //展示对话框
            //填充对话框的布局
            View inflate = LayoutInflater.from(this).inflate(R.layout.layout_tuichujiezhen, null);
            //初始化控件
            TextView yes = inflate.findViewById(R.id.yes);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intentToStu.putExtra("chatMsg", stuOrDocId + "|finishChat");
                    sendBroadcast(intentToStu);
                    finish();
                    Log.d("wee", "1");
                }
            });
            TextView no = inflate.findViewById(R.id.no);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            //将布局设置给Dialog
            dialog.setContentView(inflate);
            //获取当前Activity所在的窗体
            Window dialogWindow = dialog.getWindow();
            //设置Dialog从窗体底部弹出
            dialogWindow.setGravity( Gravity.CENTER);
            //获得窗体的属性
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width =800;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setAttributes(lp);
//       将属性设置给窗体
            dialog.show();//显示对话框
        } else {//如果是医生
            //builder.setMessage("是否结束此次接诊?");
            //builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
              //  @Override
               // public void onClick(DialogInterface dialog, int which) {
               //     intentToDoc.putExtra("chatMsg", stuOrDocId + "|finishChat");
               //     sendBroadcast(intentToDoc);
               //     finish();
               //     Log.d("wee", "2");
               // }
            //}).setNegativeButton("取消", null)
             //       .show();
            final Dialog dialog = new Dialog(this,R.style.ActionSheetDialogStyle);        //展示对话框
            //填充对话框的布局
            View inflate = LayoutInflater.from(this).inflate(R.layout.layout_tuichujiezhen1, null);
            //初始化控件
            TextView yes = inflate.findViewById(R.id.yes);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intentToDoc.putExtra("chatMsg", stuOrDocId + "|finishChat");
                    sendBroadcast(intentToDoc);
                    finish();
                    Log.d("wee", "2");
                }
            });
            TextView no = inflate.findViewById(R.id.no);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            //将布局设置给Dialog
            dialog.setContentView(inflate);
            //获取当前Activity所在的窗体
            Window dialogWindow = dialog.getWindow();
            //设置Dialog从窗体底部弹出
            dialogWindow.setGravity( Gravity.CENTER);
            //获得窗体的属性
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width =800;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setAttributes(lp);
//       将属性设置给窗体
            dialog.show();//显示对话框
        }
    }

    private void initView() {
        //与服务器建立websocket连接
        type = method.getFileData("Type", Chat.this);
        chatWindowName = findViewById(R.id.chatWindowName);//
        inputText = findViewById(R.id.input_text);
        send = findViewById(R.id.send);
        prescribe = findViewById(R.id.close);
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        //如果是医生登录的话，填写要聊天的学生id
        Intent intent = getIntent();
        if (type.equals("Doc")) {
            //如果是医生登录，则设置开处方可见
            prescribe.setVisibility(View.VISIBLE);

            stuOrDocId = intent.getStringExtra("stuId").trim();
            String stuName = intent.getStringExtra("stuName").trim();
            messageBean.setName(stuName);
            chatWindowName.setText(stuName + "同学");
            //获取学生头像
            byte[] stuIcon = intent.getByteArrayExtra("stuPicture");
            messageBean.setIcon(stuIcon);
            Log.d("chat", "stuIcon" + (stuIcon == null));
            Bitmap stuPicture = BitmapFactory.decodeByteArray(stuIcon, 0, stuIcon.length);
            //获取医生的头像
            PictureStore pictureStore = (PictureStore) method.readObjFromSDCard("DocIcon");
            Bitmap docPicture = null;
            if (pictureStore.getFlag()) {
                byte[] docIcon = pictureStore.getPicture();
                docPicture = BitmapFactory.decodeByteArray(docIcon, 0, docIcon.length);
            }
            adapter = new MsgAdapter(msgList, stuPicture, docPicture);
        } else {//如果是学生登录的话，则填写要聊天的医生id
            //如果是学生登录，则设置开处方不可见
            prescribe.setVisibility(View.INVISIBLE);

            stuOrDocId = intent.getStringExtra("docId").trim();
            String docName = intent.getStringExtra("docName").trim();
            messageBean.setName(docName);
            chatWindowName.setText(docName + "医生");
            //获取医生的头像
            byte[] docIcon = intent.getByteArrayExtra("docPicture");
            messageBean.setIcon(docIcon);
            Log.d("chat", "docIcon" + (docIcon == null));
            Bitmap docPicture = BitmapFactory.decodeByteArray(docIcon, 0, docIcon.length);
            //获取学生的头像
            PictureStore pictureStore = (PictureStore) method.readObjFromSDCard("Icon");
            Bitmap stuPicture = null;
            if (pictureStore.getFlag()) {
                byte[] stuIcon = pictureStore.getPicture();
                stuPicture = BitmapFactory.decodeByteArray(stuIcon, 0, stuIcon.length);
            }
            adapter = new MsgAdapter(msgList, docPicture, stuPicture);
        }
        Log.d("chatId", stuOrDocId);
        msgRecyclerView.setAdapter(adapter);
        video = findViewById(R.id.videoChat);
        //设置视频聊天的点击事件
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先申请权限
                if(!EasyPermissions.hasPermissions(Chat.this,perms)){
                    EasyPermissions.requestPermissions(Chat.this,"Need permissions for camera and audio",0,perms);
                }
                else{
                    Intent intentToVideo=new Intent(Chat.this, CallActivity.class);
                    startActivity(intentToVideo);
                }

            }
        });
        send.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = inputText.getText().toString();
                        if (!"".equals(content)) {
                            update(content, TYPE_SENT);
                            //如果是学生登录则发送给学生服务
                            if (type.equals("Stu")) {
                                if (docOnline) {//如果医生在线
                                    intentToStu.putExtra("chatMsg", stuOrDocId + "|" + content);
                                    sendBroadcast(intentToStu);
                                } else {
                                    update("医生已离开，如需问诊请重新挂号", TYPE_RECEIVED);
                                }

                            } else {//如果是医生登录则将消息发送给医生服务
                                if (stuOnline) {//如果学生在线
                                    intentToDoc.putExtra("chatMsg", stuOrDocId + "|" + content);
                                    sendBroadcast(intentToDoc);
                                } else {
                                    update("当前接诊学生已离开，如需为其开处方请点击右上角的开处方按钮！", TYPE_RECEIVED);
                                }

                            }

                        }
                    }
                });
        //设置医生开出方的点击事件
        prescribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentToStu.putExtra("chatMsg", stuOrDocId + "|再见！");
                sendBroadcast(intentToStu);
                Intent intent = new Intent(Chat.this, Prescribe.class);
                intent.putExtra("stuId", stuOrDocId);
                startActivity(intent);
            }
        });
    }

    //该方法用于将消息更新显示到RecyclerView里
    private void update(String content, int type) {
        Msg msg = new Msg(content, type);
        msgList.add(msg);
        adapter.notifyItemInserted(msgList.size() - 1);
        msgRecyclerView.scrollToPosition(msgList.size() - 1);

        //如果是发送者发送消息则清空输入文本框中的数据
        if (type == TYPE_SENT)
            inputText.setText("");//清空输入框中的内容
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    //当退出聊天界面的时候，将聊天记录保存
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
        ArrayList<MessageBean> record = method.readMessageRecordListFromSdCard("MessageRecord");
        if (record == null) {
            record = new ArrayList<>();
        }
        messageBean.setTime(time);
        messageBean.setMsgList(msgList);
        record.add(messageBean);
        //将消息记录集合写到sd卡中
        boolean flag = method.writeMessageRecordListIntoSDcard("MessageRecord", record);
        Log.d("chatsave", "flag" + flag);
        Log.d("chatsave", "save");
        Log.d("chatsave", "?1" + (record == null));
        record = null;
        Log.d("chatsave", "?12" + (record == null));
        record = method.readMessageRecordListFromSdCard("MessageRecord");
        Log.d("chatsave", "?" + (record == null));
    }
}

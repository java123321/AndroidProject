package com.example.ourprojecttest;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.ourprojecttest.StuDiagnosis.RenGongWenZhen;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class DocService extends Service {

    LocalReceiver localReceiver;
    IntentFilter intentFilter;
    Intent intent=new Intent("com.example.ourprojecttest.DOC_UPDATE_PERSONS");//该意图是通知DocOperator里医生接诊前的准备服务
    Intent intentToChat=new Intent("com.example.ourprojecttest.ChatMessage");//该意图是向聊天活动发送聊天消息
    CommonMethod method=new CommonMethod();
    Retreatment listener = new Retreatment();
    ChatListener chatListener = new ChatListener();
    private String CHANNEL_ID = "com.example.ourprojecttest.JieZhen";
    String CHANNEL_NAME = "JieZhen";
    WebSocket webSocket = null;
    Request request = null;
    OkHttpClient client = null;
    String stuId = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    //当接收到活动发来的通知时进行相应操作
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg;

            //如果是聊天信息
            if (intent.hasExtra("sendMsg")) {
                msg = intent.getStringExtra("sendMsg");
                chatListener.socket.send(msg);
                if (msg.contains("再见！")) {
                    chatListener.socket.close(1000, "正常关闭");
                }
            }    //如果是查看排队人数的信息
            else{
                switch (intent.getStringExtra("msg")){
                    case "View":
                        //startForeground(1,getNotification(CHANNEL_ID,"正在接收通知"));
                        send();
                        Log.d("候诊服务", "服务收到查看人数广播通知");
                    break;
                    //医生上线的消息
                    case "Online":
                        retreatmentConnect();
                        Log.d("医生上线", "医生上线了");
                        break;
                    //医生通知队列中第一个学生看病
                    case "Access":
                        webSocket.send("next");
                        client.dispatcher().executorService().shutdown();
                        Log.d("接诊", "弹出队首学生");
                        break;
                    case "exit":
                        listener.socket.close(1000, "正常关闭");

                }

        }

        }
    }

    //第一此创建时触发
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化通知信道服务
        initChannel();
        //开始注册广播监听器，准备接受发送给服务的更新挂号信息
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.DOC_UPDATE_SERVICE");
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);
        //医生开启服务的时候同时打开聊天接口
        chatConnet();
    }
    /**
     * 该方法是建立与服务器端的websocket连接
     *
     */
    void retreatmentConnect() {
        request = new Request.Builder()
                .url(getResources().getString(R.string.ipAdrressSocket)+"IM/websocketdoc?"+method.getFileData("ID",DocService.this))
                .build();
        client = new OkHttpClient();
        webSocket = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
        Log.d("链接状态","发送完查看人数请求");
    }
    //聊天连接方法
     private void chatConnet() {
        String url=getResources().getString(R.string.ipAdrressSocket)+"IM/message/"+method.getFileData("ID",DocService.this);
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request, chatListener);
        Log.d("聊天",url);
        client.dispatcher().executorService().shutdown();
    }
   private void send(){

        webSocket.send("666");
        client.dispatcher().executorService().shutdown();
        Log.d("查看人数","查看人数成功");
    }
    private final class Retreatment extends WebSocketListener {
        WebSocket socket=null;
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            socket=webSocket;
            Log.d("监听器状态","监听器打开");

        }

        //接受消息时回调
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output("onMessage: " + text);
            String info=parseJSONWithJSONObject(text);
            Log.d("msg的内容 ",info);
            int q = info.length();
            //获取返回的人数
            if(info.indexOf("人数")!=-1){
                StringBuilder result=new StringBuilder();
                int i=7;
                while (i<q){
                    result.append(info.charAt(i++));
                }
                Log.d("人数",result+"");

                //发送通知
                Log.d("队伍中人数：",result.toString());
                startForeground(1,getNotification(CHANNEL_ID,info));

            }else if (info.indexOf("向您发送了接诊邀请")!=-1){
                stuId = info.substring(0,info.indexOf("向"));
                Log.d("学生Id",stuId);

                //发送通知
                startForeground(1,getNotification(CHANNEL_ID,stuId+"向你发送问诊"));

                //将候诊学生广播出去
//                intent.putExtra("stuId",stuId);
//                sendBroadcast(intent);

            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            output("onClosing: " + code + "/" + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            output("onClosed: " + code + "/" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("onFailure: " + t.getMessage());
        }
    }
    //改变输出
    private void output(final String txt) {
        Log.d("学生消息：",txt);
    }


    //webSocket回调方法
    private final class ChatListener extends WebSocketListener {
        WebSocket socket=null;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            socket=webSocket;
            output("连接成功聊天");
        }
        //接受消息时回调
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d("学生消息0",text);
            text=parseJSONWithJSONObject(text);
            Log.d("学生消息1",text);
            //如果学生发送的是沟通
            if (text.contains("chat")){
                intent.putExtra("validate",stuId);
                intent.putExtra("stuName",method.subString(text,"学生名字为","学生头像为"));
                //获取学生的头像
                int position=text.indexOf("学生头像为");
                String stuPictureUrl=text.substring(position+5);
                byte[]stuPicture=null;
                try {
                   stuPicture=method.bitmap2Bytes(method.drawableToBitamp(Drawable.createFromStream(new URL(stuPictureUrl).openStream(), "image.jpg"))) ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("chat","stuPicture"+(stuPicture==null));
                intent.putExtra("stuPicture",stuPicture);
                sendBroadcast(intent);
                Log.d("学生消息2","chat");
            }
            else if (text.contains("deny")){//如果学生发送的是拒绝
                intent.putExtra("validate",text);
                sendBroadcast(intent);
                Log.d("学生消息3","deny");
            }
            else if(!text.equals("上线成功!")){//如果学生发送的是正常消息
                intentToChat.putExtra("ReceiveMsg",text);
                sendBroadcast(intentToChat);
                Log.d("学生消息4","chatmsg");
            }

        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            output("onClosing: " + code + "/" + reason);
        }
        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            output("onClosed: " + code + "/" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("onFailure: " + t.getMessage());
            webSocket.close(1000, null);
        }
    }


    //解析服务器传来的字符串
    private String parseJSONWithJSONObject(String data){
        try {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject.getString("msg").trim();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    //开启前台服务
    private void initChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    /**
     * @param chanelId 信号渠道
     * @param content 通知内容
     * @return
     */
    private Notification getNotification(String chanelId , String content){

        Intent intent = new Intent(this, RenGongWenZhen.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,chanelId);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setContentTitle("学生挂号信息")
                .setContentText(content);
        return builder.build();
    }




    //每次startService都会触发
    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}

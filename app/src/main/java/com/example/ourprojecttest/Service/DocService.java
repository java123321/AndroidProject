package com.example.ourprojecttest.Service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.R;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class DocService extends Service {
    private Timer detectChatOfflineTimer = null;
    private Timer detecRetreatOfflineTimer = null;//检测接诊是否掉线的计时器
    private volatile long lastRetreatHeartBeatTime = -1;//代表上次接诊接口的呼吸包时间
    private volatile long lastChatHeartBeatTime=-1;//代表上次聊天接口的呼吸包时间
    private Date date;
    private Timer retreatReconnectTimer = null;
    private Timer chatReconnectTimer = null;
    private Timer chatTimer = null;
    public static volatile boolean docOnline = false;
    private String ipAddress;
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private Intent intentToVideoChat = new Intent("com.example.ourprojecttest.VIDEO_CHAT");//该意图是向聊天活动提供媒体协商信息
    private Intent intentToBeforChat = new Intent("com.example.ourprojecttest.DOC_UPDATE_PERSONS");//该意图是通知DocOperator里医生接诊前的准备服务
    private Intent intentToChat = new Intent("com.example.ourprojecttest.ChatMessage");//该意图是向聊天活动发送聊天消息
    private CommonMethod method = new CommonMethod();
    private Retreatment beforeChatlistener = new Retreatment();
    private ChatListener chatListener = new ChatListener();
    private String CHANNEL_ID = "com.example.ourprojecttest.JieZhen";
    private String CHANNEL_NAME = "JieZhen";
    private Request request = null;
    private OkHttpClient client = null;
    private String stuId = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //当接收到活动发来的通知时进行相应操作
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg;

            //如果是聊天信息
            if (intent.hasExtra("chatMsg")) {
                msg = intent.getStringExtra("chatMsg");
                chatListener.socket.send(msg);
                Log.d("wee", "msg:" + msg);
            }    //如果是查看排队人数的信息
            else {
                switch (intent.getStringExtra("msg")) {
                    //医生上线的消息
                    case "Online": {
                        docOnline = true;
                        retreatConnect();
                        chatConnet();
                        Log.d("医生上线", "医生上线了");
                        break;
                    }
                    //医生通知队列中第一个学生看病
                    case "Access": {
                        beforeChatlistener.socket.send("next");
                        client.dispatcher().executorService().shutdown();
                        Log.d("接诊", "弹出队首学生");
                        break;
                    }
                    case "exit": {
                        docOnline = false;
                        beforeChatlistener.socket.close(1000, "正常关闭");
                        chatListener.socket.close(1000,"正常关闭");
                    }

                }
            }
        }
    }

    //第一此创建时触发
    @Override
    public void onCreate() {
        super.onCreate();
        ipAddress = getResources().getString(R.string.ipAdrress);
        //初始化通知信道服务
        initChannel();
        //开始注册广播监听器，准备接受发送给服务的更新挂号信息
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.DOC_UPDATE_SERVICE");
        localReceiver = new LocalReceiver();
        registerReceiver(localReceiver, intentFilter);
//        //医生开启服务的时候同时打开聊天接口
//        chatConnet();
    }

    /**
     * 该方法是建立与服务器端的websocket连接
     */
    void retreatConnect() {
        request = new Request.Builder()
                .url(getResources().getString(R.string.ipAdrressSocket) + "IM/websocketdoc?" + method.getFileData("ID", DocService.this))
                .build();
        client = new OkHttpClient();
        client.newWebSocket(request, beforeChatlistener);
        client.dispatcher().executorService().shutdown();
        Log.d("链接状态", "发送完查看人数请求");
    }

    //聊天连接方法
    private void chatConnet() {
        String url = getResources().getString(R.string.ipAdrressSocket) + "IM/message/" + method.getFileData("ID", DocService.this);
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request, chatListener);
        Log.d("聊天", url);
        client.dispatcher().executorService().shutdown();
    }

    private final class Retreatment extends WebSocketListener {
        WebSocket socket = null;

        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            socket = webSocket;
            if (retreatReconnectTimer != null) {//如果是断线重连后打开的
                retreatReconnectTimer.cancel();
                retreatReconnectTimer = null;
            }
            socket.send("heartBeat");
            date = new Date();
            lastRetreatHeartBeatTime = date.getTime();
            //接诊接口连接成功后启动检测心跳包是否超市方法
            detectRetreatOffline();
            Log.d("docservice.closed", "onopen");
        }

        //接受消息时回调
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output("onMessage: " + text);
            String info = parseJSONWithJSONObject(text);
            Log.d("c", info);

            if (info.equals("heartBeat")) {//如果服务器返回的是心跳检测包
                date = new Date();
                lastRetreatHeartBeatTime = date.getTime();//更新最新接诊最新心跳包时间
                //接收到心跳包之后，过3秒再次发送心跳包
                Timer timer = new Timer(true);
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        webSocket.send("heartBeat");
                    }
                };
                timer.schedule(tt, 3000);
            } else if (info.equals("当前没有人在挂号，请稍等！")) { //获取返回的人数
                Log.d("docservice.updatestu.count", "当前没有人挂号");
                intentToBeforChat.putExtra("updateStu", "noStuOnline");
                sendBroadcast(intentToBeforChat);
                intentToBeforChat.removeExtra("updateStu");
            } else if (info.endsWith("向您发送了接诊邀请！")) {
                stuId = info.substring(0, info.indexOf("向"));
                Log.d("学生Id", stuId);
                //发送通知
                startForeground(1, getNotification(CHANNEL_ID, stuId + "向你发送问诊"));
                //将即将接诊的消息广播给前台，通知弹出等待窗口
                intentToBeforChat.putExtra("Dialog", "true");
                sendBroadcast(intentToBeforChat);
                intentToBeforChat.removeExtra("Dialog");
            } else if (info.startsWith("updateStu")) {//如果是服务器通知医生更新在线学生
                Log.d("docservice.updatestu.count", "---");
                String stuNumber = info.substring(9);
                if (stuNumber.equals("0")) {//如果当前没有学生排队
                    startForeground(1, getNotification(CHANNEL_ID, "当前暂无学生排队"));
                    intentToBeforChat.putExtra("updateStu", "noStuOnline");
                    sendBroadcast(intentToBeforChat);
                    intentToBeforChat.removeExtra("updateStu");
                } else {
                    Log.d("docservice.updatestu.count", "---111");
                    startForeground(1, getNotification(CHANNEL_ID, "当前有" + stuNumber + "位同学正在挂号排队，请注意及时接诊！"));
                    intentToBeforChat.putExtra("updateStu", "-1");
                    sendBroadcast(intentToBeforChat);
                    intentToBeforChat.removeExtra("updateStu");
                }


            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {

        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            retreatReconnect();
            Log.d("docservice.closed", "close");
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            retreatReconnect();
            Log.d("docservice.closed", "failure");
        }
    }

    //改变输出
    private void output(final String txt) {
        Log.d("学生消息：", txt);
    }


    //webSocket回调方法
    private final class ChatListener extends WebSocketListener {
        WebSocket socket = null;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            socket = webSocket;
            if (chatReconnectTimer != null) {//如果是断线重连后打开的
                chatReconnectTimer.cancel();
                chatReconnectTimer = null;
            }
            socket.send("heartBeat");
            date = new Date();
            lastChatHeartBeatTime = date.getTime();
            //接诊接口连接成功后启动检测心跳包是否超市方法
            detectChatOffline();
            Log.d("docservice.chat.closed", "onopen");
        }

        //接受消息时回调
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d("学生消息0", text);
            text = parseJSONWithJSONObject(text);
            Log.d("学生消息1", text);
            if (text.equals("heartBeat")) {//如果服务器返回的是心跳检测包
                date = new Date();
                lastChatHeartBeatTime = date.getTime();//更新最新接诊最新心跳包时间
                //接收到心跳包之后，过3秒再次发送心跳包
                Timer timer = new Timer(true);
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        webSocket.send("heartBeat");
                    }
                };
                timer.schedule(tt, 3000);
            }else if (text.startsWith("IceInfo") || text.startsWith("SdpInfo") || text.equals("denyVideoChat")) {//如果是和视频聊天有关的协商信息，则发送给视频活动
                intentToVideoChat.putExtra("videoInfo", text);
                sendBroadcast(intentToVideoChat);
                Log.d("docservice", "videoInfo:" + text);

            } else if (text.startsWith("chat")) { //如果学生发送的是沟通
                intentToBeforChat.putExtra("validate", stuId);
                intentToBeforChat.putExtra("stuName", method.subString(text, "学生名字为", "学生头像为"));
                //获取学生的头像
                int position = text.indexOf("学生头像为");
                String stuPictureUrl = text.substring(position + 5);
                byte[] stuPicture = null;
                try {
                    if (stuPictureUrl.equals("null")) {//如果学生头像为空
                        intentToBeforChat.removeExtra("stuPicture");
                    } else {
                        stuPicture = method.bitmap2Bytes(method.drawableToBitamp(Drawable.createFromStream(new URL(ipAddress + stuPictureUrl).openStream(), "image.jpg")));
                        intentToBeforChat.putExtra("stuPicture", stuPicture);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("chat", "stuPicture" + (stuPicture == null));

                sendBroadcast(intentToBeforChat);
                Log.d("学生消息2", "chat");
            } else if (text.startsWith("deny")) {//如果学生发送的是拒绝
                intentToBeforChat.putExtra("validate", text);
                sendBroadcast(intentToBeforChat);
                Log.d("学生消息3", "deny");
            } else if (!text.equals("上线成功!")) {//如果学生发送的是正常消息

                intentToChat.putExtra("ReceiveMsg", text);
                sendBroadcast(intentToChat);
                Log.d("学生消息4", "chatmsg");
            }

        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            output("onClosing: " + code + "/" + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            chatReconnect();
            Log.d("docservice.chat.closed", "onclose");
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            chatReconnect();
            Log.d("docservice.chat.closed", "onfailure");
        }
    }


    private void detectRetreatOffline() {//该方法通过检测接诊接口来判断是否断线

        detecRetreatOfflineTimer = new Timer(true);
        TimerTask tt = new TimerTask() {
            public void run() {
                date = new Date();
                if (date.getTime() - lastRetreatHeartBeatTime > 6000) {//如果客户端距离上次接收到心跳表超过6秒，则判断为连接中断，然后尝试重连
                    retreatReconnect();
                    Log.d("docservice.closed", "heartBeat offline");
                } else {
                    Log.d("docservice.closed", "heartBeat online");
                }
            }

            ;
        };
        detecRetreatOfflineTimer.schedule(tt, 0, 6000);//六秒检测一下是否掉线
    }
    private void detectChatOffline(){//该方法用于检测聊天接口来判断是否断线
        detectChatOfflineTimer=new Timer(true);
        TimerTask tt=new TimerTask() {
            @Override
            public void run() {
                date = new Date();
                if (date.getTime() - lastChatHeartBeatTime > 6000) {//如果客户端距离上次接收到心跳表超过6秒，则判断为连接中断，然后尝试重连
                    chatReconnect();
                    Log.d("docservice.chat.closed", "heartBeat offline");
                } else {
                    Log.d("docservice.chat.closed", "heartBeat online");
                }
            }
        };
        detectChatOfflineTimer.schedule(tt,0,6000);
    }

    //该方法用于接诊断线重连
    private void retreatReconnect() {
        if (detecRetreatOfflineTimer != null) {//在重新连接的时候停止心跳检测
            detecRetreatOfflineTimer.cancel();
            detecRetreatOfflineTimer = null;
        }
        if (docOnline && retreatReconnectTimer == null) {//只有当医生在线,并且没有在连接的情况下才重连
            retreatReconnectTimer = new Timer(true);
            TimerTask tt = new TimerTask() {
                public void run() {
                    retreatConnect();
                    Log.d("docservice.closed", "try to connecting");
                }
            };
            retreatReconnectTimer.schedule(tt, 0, 3000);//每三秒执行一次重连，直到连接成功
        }

    }

    //该方法用于聊天接口断线重连
    private void chatReconnect() {
        if (detectChatOfflineTimer != null) {//在重新连接的时候停止心跳检测
            detectChatOfflineTimer.cancel();
            detectChatOfflineTimer = null;
        }
        if (docOnline && chatReconnectTimer == null) {//只有当医生在线，并且没有在尝试连接的情况下才重连
        chatReconnectTimer=new Timer(true);
        TimerTask tt=new TimerTask() {
            @Override
            public void run() {
                chatConnet();
                Log.d("docservice.chat.closed", "try to connecting");
            }
        };
        chatReconnectTimer.schedule(tt,0,3000);//每三秒执行一次重连，直到连接成功
        }
    }


    //解析服务器传来的字符串
    private String parseJSONWithJSONObject(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject.getString("msg").trim();
        } catch (Exception e) {
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
     * @param content  通知内容
     * @return
     */
    private Notification getNotification(String chanelId, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelId);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setContentTitle("学生挂号信息")
                .setContentText(content);
        return builder.build();
    }


    //每次startService都会触发
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销广播
        unregisterReceiver(localReceiver);
        //关闭通话接口
        chatListener.socket.close(1000, "正常关闭");
        Log.d("docservice.ondestroy", "ondestroy");
    }
}

package com.example.ourprojecttest.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.StuDiagnosis.RenGongWenZhen;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class StuService extends Service {
    private Intent intentToVideoChat = new Intent("com.example.ourprojecttest.VIDEO_CHAT");//该意图是向聊天活动提供媒体协商信息
    private String ipAddress;
    public static boolean isGuaHao = false;//该变量用来标记是否正在挂号，true为挂号，false为不在挂号
    private String stuId;
    private CommonMethod method = new CommonMethod();
    private String docId = "11111";
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private Intent intent = new Intent("com.example.ourprojecttest.UPDATE_PERSONS");//该意图是通知RenGongWenZhen里的聊天前的准备服务
    private Intent intentToChat = new Intent("com.example.ourprojecttest.ChatMessage");//该意图是向聊天活动发送聊天消息
    private GuaHaoListener guaHaoListener = new GuaHaoListener();
    private ChatListener chatListener = new ChatListener();
    private String CHANNEL_ID = "com.example.ourprojecttest.GuaHao";
    private String CHANNEL_NAME = "GuaHao";
    private String name;
    private String nameT="channel_name_1";
    private NotificationManager manager;
    public static final String id = "channel_1";
    private Notification notification = null;


    //当接收到活动发来的挂号通知时进行挂号操作
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg;

            //如果是聊天信息
            if (intent.hasExtra("chatMsg")) {
                msg = intent.getStringExtra("chatMsg");
                chatListener.socket.send(msg);
                if (msg.contains("finishChat")) {
                    chatListener.socket.close(1000, "正常关闭");
                }
            } else {//如果是挂号和聊天前的准备信息
                msg = intent.getStringExtra("msg");
                switch (msg) {
                    case "StartGuaHao": {
                        //开启挂号的连接
                        chatConnet();
                        guaHaoConnect();
                        isGuaHao = true;
                        break;
                    }
                    case "ExitGuaHao": {//如果关闭挂号则关闭socket连接和服务
                        guaHaoListener.socket.close(1000, null);
                        chatListener.socket.close(1000, null);
                        isGuaHao = false;
                        //startForeground(1, getNotification(CHANNEL_ID, "您已取消挂号！", "取消挂号成功"));

                        sendNotification("取消挂号成功","您已取消挂号");
                        //5秒之后关闭前台服务
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                stopForeground(true);
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(task, 5000);
                        Log.d("stustop", "sent");
                        break;
                    }
                    case "Chat": {//如果是学生点击了沟通操作
                        Log.d("guahao", "chat");
                        chatListener.socket.send(docId + "|chat学生名字为" + name + "学生头像为" + method.getFileData("StuIconUrl", StuService.this));

                        break;
                    }
                    case "Deny": {//如果学生点击了拒绝服务
                        chatListener.socket.send(docId + "|deny" + name);
                        //notification.
                        manager.cancel(1);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ipAddress = getResources().getString(R.string.ipAdrress);
        Log.d("service123", "start");
        stuId = method.getFileData("ID", StuService.this);
        //开始注册广播监听器，准备接受发送给服务的更新挂号信息
        name = method.getFileData("Name", StuService.this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.UPDATE_SERVICE");
        localReceiver = new LocalReceiver();
        registerReceiver(localReceiver, intentFilter);
        Log.d("guaHaoService", "服务已创建！");
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    //挂号连接方法
    private void guaHaoConnect() {
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.ipAdrressSocket) + "IM/websocket?" + stuId)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request, guaHaoListener);

        client.dispatcher().executorService().shutdown();
    }

    //聊天连接方法
    void chatConnet() {
        String url = getResources().getString(R.string.ipAdrressSocket) + "IM/message/" + method.getFileData("ID", StuService.this);
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request, chatListener);
        Log.d("挂号", url);
        client.dispatcher().executorService().shutdown();
    }

    private final class GuaHaoListener extends WebSocketListener {
        WebSocket socket = null;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            socket = webSocket;
            Log.d("interfaceguahao", "挂号成功");
        }

        //接受消息时回调
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d("interfaceguahaomessage", text);
            String info = parseJSONWithJSONObject(text);
            //当服务器更新排队人数时:
            if (info.startsWith("您当前排队位次为")) {
                String regEx = "[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(info);
                Log.d("123321", m.replaceAll("").trim());
                //将更新人数广播出去
                intent.putExtra("persons", m.replaceAll("").trim());
                sendBroadcast(intent);
                //result代表当前的排队人数
                method.saveFileData("GuaHaoNumber", m.replaceAll("").trim(), StuService.this);

                //startForeground(1, getNotification(CHANNEL_ID, info, "挂号成功"));
                sendNotification("挂号成功",info);
            }//服务器发送到我的通知时
            else if (info.contains("到你啦！")) {
                //获取医生的id
                String docId = method.subString(info, "医生id为", "医生姓名为");
                //获取医生的名字
                String docName = method.subString(info, "医生姓名为", "医生头像为");
                //获取医生的头像
                int position = info.indexOf("医生头像为");
                String docPictureUrl = info.substring(position + 5);
                byte[] docPicture = null;
                try {
                    if(docPictureUrl==null||docPictureUrl.equals("")){//如果数据库没有用户头像，则设置null
                        intent.removeExtra("docPicture");
                    }else{//如果有数据则获取头像
                        docPicture = method.bitmap2Bytes(method.drawableToBitamp(Drawable.createFromStream(new URL(ipAddress + docPictureUrl).openStream(), "image.jpg")));
                        intent.putExtra("docPicture", docPicture);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //将到你的通知发送个活动
                intent.putExtra("persons", "-1");
                intent.putExtra("docId", docId);
                intent.putExtra("docName", docName);
                Log.d("chat", "docPicture" + (docPicture == null));
                //startForeground(1, getNotification(CHANNEL_ID, docName + "医生即将为您接诊！", "到你了"));
                notification = sendNotification("到你了",docName + "医生即将为您接诊！");
                webSocket.close(1000, "再见");


                sendBroadcast(intent);
                //-1代表到你了
                method.saveFileData("GuaHaoNumber", "-1", StuService.this);
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

    //webSocket回调方法
    private final class ChatListener extends WebSocketListener {
        WebSocket socket = null;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            socket = webSocket;
            Log.d("interfacechat", "连接成功");
        }

        //接受消息时回调
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d("interfacechat", "123" + text);
            text = parseJSONWithJSONObject(text);
            if(text.startsWith("IceInfo")||text.startsWith("SdpInfo")||text.equals("denyVideoChat")){//如果是和视频聊天有关的协商信息，则发送给视频聊天活动
                intentToVideoChat.putExtra("videoInfo",text);
                sendBroadcast(intentToVideoChat);
                Log.d("docservice","videoInfo:"+text);
            }else{
                intentToChat.putExtra("ReceiveMsg", text);
                sendBroadcast(intentToChat);
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
            Log.d("interfacefalure", "学生聊天接口失败");
            webSocket.close(1000, null);
        }
    }

    //解析服务器传来的字符串
    private String parseJSONWithJSONObject(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject.getString("msg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void output(final String content) {
        Log.d("interface", content);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除广播注册
        unregisterReceiver(localReceiver);
        Log.d("service123", "destroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }


    private Notification sendNotification(String title,String content){
        Intent intent = new Intent(this, RenGongWenZhen.class);
        //8.0 以后需要加上channelId 才能正常显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "default";
            String channelName = "默认通知";
            manager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
        }

        //设置TaskStackBuilder
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(RenGongWenZhen.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setShowWhen(true)
                .setWhen(10000L)
                .setAutoCancel(true)
                //.setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {0,1000,0,1000,0,1000,0,1000,1000,0,1000,1000,1000})
                .build();
        manager.notify(1, notification);

        return notification;
    }

}

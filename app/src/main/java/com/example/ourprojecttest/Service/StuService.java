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
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.R;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
    private Date date;
    private Timer guaHaoReconnectTimer=null;
    private Timer chatReconnectTimer = null;
    private Timer detecGuaHaoOfflineTimer=null;
    private Timer detectChatOfflineTimer = null;
    private final int reconnectInterval=3000;//两次重连的间隔
    private final int sendHeartBeatInterval=5000;//发送心跳包的时间间隔
    private final int judegOfflineInterval=8000;//判断掉线的时间间隔
    private volatile long lastGuaHaoHeartBeatTime=-1;
    private volatile long lastChatHeartBeatTime=-1;//代表上次聊天接口的呼吸包时间
    private Intent intentToVideoChat = new Intent("com.example.ourprojecttest.VIDEO_CHAT");//该意图是向聊天活动提供媒体协商信息
    private String ipAddress;
    public static volatile boolean isGuaHao = false;//该变量用来标记是否正在挂号，true为挂号，false为不在挂号
    public static volatile boolean isChat=true;
    private String stuId;
    private CommonMethod method = new CommonMethod();
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private Intent intent = new Intent("com.example.ourprojecttest.UPDATE_PERSONS");//该意图是通知RenGongWenZhen里的聊天前的准备服务
    private Intent intentToChat = new Intent("com.example.ourprojecttest.ChatMessage");//该意图是向聊天活动发送聊天消息
    private GuaHaoListener guaHaoListener = new GuaHaoListener();
    private ChatListener chatListener = new ChatListener();
    private String CHANNEL_ID = "com.example.ourprojecttest.GuaHao";
    private String CHANNEL_NAME = "GuaHao";
    private String name;
    public static final String id = "channel_1";


    //当接收到活动发来的挂号通知时进行挂号操作
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg;

            //如果是聊天信息
            if (intent.hasExtra("chatMsg")) {
                msg = intent.getStringExtra("chatMsg");
                chatListener.socket.send(msg);
            } else {//如果是挂号和聊天前的准备信息
                msg = intent.getStringExtra("msg");
                switch (msg) {
                    case "StartGuaHao": {
                        //开启挂号的连接
                        isGuaHao = true;
                        isChat=true;
                        chatConnet();
                        guaHaoConnect();
                        break;
                    }
                    case "ExitGuaHao": {//如果关闭挂号则关闭socket连接和服务
                        if(isGuaHao){
                            isGuaHao = false;
                            guaHaoListener.socket.close(1000, null);
                        }
                        if(isChat){
                            isChat=false;
                            chatListener.socket.close(1000,null);
                        }
                        if(!intent.hasExtra("finishedGuaHao")){
                            startForeground(1, getNotification(CHANNEL_ID, "取消挂号成功", "您已取消挂号！"));
                        }
                        break;
                    }
                    case "Chat": {//如果是学生点击了沟通操作
                        Log.d("guahao", "chat");
                        chatListener.socket.send(intent.getStringExtra("docId") + "|chat学生名字为" + name + "学生头像为" + method.getFileData("StuIconUrl", StuService.this));
                        Log.d("stuservice.chat.id:",intent.getStringExtra("docId"));
                        //用户点击沟通之后，将挂号接口取消掉
//                        if(isGuaHao){
//                            guaHaoListener.socket.close(1000, null);
//                            isGuaHao = false;
//                        }
                        break;
                    }
                    case "Deny": {//如果学生点击了拒绝服务
                        chatListener.socket.send(intent.getStringExtra("docId") + "|deny" + name);
                            startForeground(1, getNotification(CHANNEL_ID, "提示", "您已拒绝了与医生问诊！"));
                        //用户点击取消之后将挂号服务接口取消掉
                        if(isGuaHao){
                            isGuaHao = false;
                            guaHaoListener.socket.close(1000, null);
                        }
                        if(isChat){
                            isChat=false;
                            chatListener.socket.close(1000,null);
                        }
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

        //初始化通知信道服务
        initChannel();
        Log.d("service123", "start");
        stuId = method.getFileData("ID", StuService.this);
        //开始注册广播监听器，准备接受发送给服务的更新挂号信息
        name = method.getFileData("Name", StuService.this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.UPDATE_SERVICE");
        localReceiver = new LocalReceiver();
        registerReceiver(localReceiver, intentFilter);
        Log.d("guaHaoService", "服务已创建！");
//        //当服务启动的时候开启聊天接口
//        chatConnet();
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

    private void detectGuaHaoOffline(){//该方法通过检测挂号接口来判断是否断线
        detecGuaHaoOfflineTimer = new Timer(true);
        TimerTask tt = new TimerTask() {
            public void run() {
                date = new Date();
                if (date.getTime() - lastGuaHaoHeartBeatTime > judegOfflineInterval) {//如果客户端距离上次接收到心跳表超过6秒，则判断为连接中断，然后尝试重连
                    guaHaoReconnect();
                    Log.d("stuservice.closed", "heartBeat offline");
                } else {
                    Log.d("stuservice.closed", "heartBeat online");
                }
            }

            ;
        };
        detecGuaHaoOfflineTimer.schedule(tt, 0, judegOfflineInterval);
    }

    //该方法用于挂号断线重连
    private void guaHaoReconnect(){
        if (detecGuaHaoOfflineTimer != null) {//在重新连接的时候停止心跳检测
            detecGuaHaoOfflineTimer.cancel();
            detecGuaHaoOfflineTimer = null;
        }
        if (isGuaHao && guaHaoReconnectTimer == null) {//只有当学生在挂号,并且没有重连连接的情况下才重连
            guaHaoReconnectTimer = new Timer(true);
            TimerTask tt = new TimerTask() {
                public void run() {
                    guaHaoConnect();
                    Log.d("stuservice.closed", "try to connecting");
                }
            };
            guaHaoReconnectTimer.schedule(tt, 0, reconnectInterval);
        }
    }


    private final class GuaHaoListener extends WebSocketListener {
        WebSocket socket = null;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            socket = webSocket;
            if (guaHaoReconnectTimer != null) {//如果是断线重连后打开的
                guaHaoReconnectTimer.cancel();
                guaHaoReconnectTimer = null;
            }
            socket.send("heartBeat");
            date = new Date();
            lastGuaHaoHeartBeatTime = date.getTime();
            //接诊接口连接成功后启动检测心跳包是否超市方法
            detectGuaHaoOffline();
            Log.d("stuservice.closed", "onopen");
        }

        //接受消息时回调
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d("interfaceguahaomessage", text);
            String info = parseJSONWithJSONObject(text);
            //当服务器更新排队人数时:

            if (info.equals("heartBeat")) {//如果服务器返回的是心跳检测包
                date = new Date();
                lastGuaHaoHeartBeatTime = date.getTime();//更新最新接诊最新心跳包时间
                //接收到心跳包之后，过3秒再次发送心跳包
                Timer timer = new Timer(true);
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        webSocket.send("heartBeat");
                    }
                };
                timer.schedule(tt, sendHeartBeatInterval);
            } else if (info.startsWith("您当前排队位次为")) {
                String regEx = "[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(info);
                Log.d("123321", m.replaceAll("").trim());
                //将更新人数广播出去
                intent.putExtra("persons", m.replaceAll("").trim());
                sendBroadcast(intent);
//                //result代表当前的排队人数
//                method.saveFileData("GuaHaoNumber", m.replaceAll("").trim(), StuService.this);

                startForeground(1, getNotification(CHANNEL_ID, "挂号成功", info));
//                sendNotification("挂号成功", info);
            }//服务器发送到我的通知时
            else if (info.startsWith("到你啦")) {
                //获取医生的id
                String docId = method.subString(info, "医生id为", "医生姓名为");
                //获取医生的名字
                String docName = method.subString(info, "医生姓名为", "医生头像为");
                //获取医生的头像
                int position = info.indexOf("医生头像为");
                String docPictureUrl = info.substring(position + 5);
                byte[] docPicture = null;
                try {
                    if (docPictureUrl == null || docPictureUrl.equals("")) {//如果数据库没有用户头像，则设置null
                        intent.removeExtra("docPicture");
                    } else {//如果有数据则获取头像
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
                startForeground(1, getNotification(CHANNEL_ID,  "到你了", docName+"医生即将为您接诊！"));
                sendBroadcast(intent);
                //当通知用户到你的时候，关闭挂号接口
                if(isGuaHao){
                    isGuaHao = false;
                    guaHaoListener.socket.close(1000, null);
                }
            }else if(info.startsWith("当前没有医生在线")){
                intent.putExtra("noDocOnline","");
                sendBroadcast(intent);
                startForeground(1, getNotification(CHANNEL_ID, "提示", "当前暂无医生在线，请稍后再来！"));
                intent.removeExtra("noDocOnline");
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
//            webSocket.close(1000, null);
//            output("onClosing: " + code + "/" + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            guaHaoReconnect();
            Log.d("stuservice.closed", "close");
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            guaHaoReconnect();
            Log.d("stuservice.closed", "failure");
        }
    }


    private void detectChatOffline(){//该方法用于检测聊天接口来判断是否断线
        detectChatOfflineTimer=new Timer(true);
        TimerTask tt=new TimerTask() {
            @Override
            public void run() {
                date = new Date();
                if (date.getTime() - lastChatHeartBeatTime > judegOfflineInterval) {//如果客户端距离上次接收到心跳表超过6秒，则判断为连接中断，然后尝试重连
                    chatReconnect();
                    Log.d("stuservice.chat.closed", "heartBeat offline");
                } else {
                    Log.d("stuservice.chat.closed", "heartBeat online");
                }
            }
        };
        detectChatOfflineTimer.schedule(tt,0,judegOfflineInterval);
    }
    //该方法用于聊天接口断线重连
    private void chatReconnect() {
        if (detectChatOfflineTimer != null) {//在重新连接的时候停止心跳检测
            detectChatOfflineTimer.cancel();
            detectChatOfflineTimer = null;
        }
        if (isChat && chatReconnectTimer == null) {//只有当医生在线，并且没有在尝试连接的情况下才重连
            chatReconnectTimer=new Timer(true);
            TimerTask tt=new TimerTask() {
                @Override
                public void run() {
                    chatConnet();
                    Log.d("stuservice.chat.closed", "try to connecting");
                }
            };
            chatReconnectTimer.schedule(tt,0,reconnectInterval);//每三秒执行一次重连，直到连接成功
        }
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
            Log.d("stuservice.chat.closed", "onopen");
        }
        //接受消息时回调
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d("interfacechat", "123" + text);
            text = parseJSONWithJSONObject(text);

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
                timer.schedule(tt, sendHeartBeatInterval);
            }else if (text.startsWith("IceInfo") || text.startsWith("SdpInfo") || text.equals("denyVideoChat")) {//如果是和视频聊天有关的协商信息，则发送给视频聊天活动
                intentToVideoChat.putExtra("videoInfo", text);
                sendBroadcast(intentToVideoChat);
                Log.d("docservice", "videoInfo:" + text);
            } else {
                intentToChat.putExtra("ReceiveMsg", text);
                sendBroadcast(intentToChat);
            }
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {

        }
        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            chatReconnect();
            Log.d("stuservice.chat.closed", "onclose");
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            chatReconnect();
            Log.d("stuservice.chat.closed", "failure");
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
//        //关闭通话接口
//        chatListener.socket.close(1000, "正常关闭");
//        Log.d("service123", "destroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    /**
     * @param chanelId 信号渠道
     * @param content  通知内容
     * @return
     */
    private Notification getNotification(String chanelId, String title,String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelId);
        builder.setSmallIcon(R.drawable.icon)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setContentTitle(title)
                .setContentText(content);
        return builder.build();
    }

    //开启前台服务
    private void initChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
    }
}

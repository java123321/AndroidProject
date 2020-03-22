package com.example.ourprojecttest.Service;

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
import androidx.core.app.NotificationCompat;

import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.StuDiagnosis.RenGongWenZhen;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class StuService extends Service {
    String stuId;
    CommonMethod method=new CommonMethod();
    String docId="11111";
    LocalReceiver localReceiver;
    IntentFilter intentFilter;
    Intent intent=new Intent("com.example.ourprojecttest.UPDATE_PERSONS");//该意图是通知RenGongWenZhen里的聊天前的准备服务
    Intent intentToChat=new Intent("com.example.ourprojecttest.ChatMessage");//该意图是向聊天活动发送聊天消息
    GuaHaoListener guaHaoListener = new GuaHaoListener();
    ChatListener chatListener = new ChatListener();
    private String CHANNEL_ID = "com.example.ourprojecttest.GuaHao";
    String CHANNEL_NAME = "GuaHao";
    String name;

        //当接收到活动发来的挂号通知时进行挂号操作
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg;

            //如果是聊天信息
            if(intent.hasExtra("chatMsg")){
                msg=intent.getStringExtra("chatMsg");
                chatListener.socket.send(msg);
                if(msg.contains("finishChat")){
                    chatListener.socket.close(1000,"正常关闭");
                }
            }
            else{//如果是挂号和聊天前的准备信息
                 msg=intent.getStringExtra("msg");
                 //如果关闭挂号则关闭socket连接和服务
                if(msg.equals("ExitGuaHao")){
                    guaHaoListener.socket.close(1000, null);
                    stopSelf();
                    Log.d("stustop","sent");
                }
                else if(msg.equals("Chat")){//如果是学生点击了沟通操作
                    Log.d("guahao","chat");
                    chatListener.socket.send(docId+"|chat学生名字为"+name+"学生头像为"+method.getFileData("StuIconUrl", StuService.this));
                }
                else if(msg.equals("Deny")){
                    chatListener.socket.send(docId+"|deny"+name);
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stuId=method.getFileData("ID", StuService.this);
        //初始化通知信道服务
        initChannel();
        //开始注册广播监听器，准备接受发送给服务的更新挂号信息
        name=method.getFileData("Name", StuService.this);
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.UPDATE_SERVICE");
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);
        Log.d("guaHaoService","服务已创建！");
        //开启挂号的连接
        chatConnet();
        guaHaoConnect();
        }
    //挂号连接方法
   private void guaHaoConnect() {
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.ipAdrressSocket)+"IM/websocket?"+stuId)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request, guaHaoListener);

        client.dispatcher().executorService().shutdown();
    }

    //聊天连接方法
    void chatConnet() {
        String url=getResources().getString(R.string.ipAdrressSocket)+"IM/message/"+method.getFileData("ID", StuService.this);
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request, chatListener);
        Log.d("挂号",url);
        client.dispatcher().executorService().shutdown();
    }
    private final class GuaHaoListener extends WebSocketListener {
                    WebSocket socket=null;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            socket=webSocket;
            Log.d("interfaceguahao","挂号成功");
        }

        //接受消息时回调
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d("interfaceguahaomessage",text);
            String info=parseJSONWithJSONObject(text);
            //当服务器更新排队人数时:
            if(info.startsWith("您当前排队位次为")){
                String regEx="[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(info);
                Log.d("123321",m.replaceAll("").trim());
                //将更新人数广播出去
                intent.putExtra("persons",m.replaceAll("").trim());
                sendBroadcast(intent);
              //result代表当前的排队人数
              method.saveFileData("GuaHaoNumber",m.replaceAll("").trim(), StuService.this);

             startForeground(1,getNotification(CHANNEL_ID,info));
            }//服务器发送到我的通知时
            else if(info.contains("到你啦！")){
                //获取医生的id
                String docId=method.subString(info,"医生id为","医生姓名为");
                //获取医生的名字
                String docName=method.subString(info,"医生姓名为","医生头像为");
                //获取医生的头像
                int position=info.indexOf("医生头像为");
                String docPictureUrl=info.substring(position+5);
                byte[] docPicture=null;
                try {
                     docPicture=method.bitmap2Bytes(method.drawableToBitamp(Drawable.createFromStream(new URL(docPictureUrl).openStream(), "image.jpg"))) ;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //将到你的通知发送个活动
                intent.putExtra("persons","-1");
                intent.putExtra("docId",docId);
                intent.putExtra("docName",docName);
                Log.d("chat","docPicture"+(docPicture==null));
                intent.putExtra("docPicture",docPicture);
                startForeground(1,getNotification(CHANNEL_ID,"到你了，"+docName+"医生即将为您接诊！"));
                webSocket.close(1000,"再见");

                sendBroadcast(intent);
                //-1代表到你了
                method.saveFileData("GuaHaoNumber","-1", StuService.this);
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
        WebSocket socket=null;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            socket=webSocket;
            Log.d("interfacechat","连接成功");
        }
        //接受消息时回调
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d("interfacechat","123"+text);
            text=parseJSONWithJSONObject(text);
            intentToChat.putExtra("ReceiveMsg",text);
            sendBroadcast(intentToChat);
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
            Log.d("interfacefalure","学生聊天接口失败");
            webSocket.close(1000, null);
        }
    }


            //解析服务器传来的字符串
    private String parseJSONWithJSONObject(String data){
        try {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject.getString("msg");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void output(final String content) {
        Log.d("interface",content);
    }

    /**
     *
     *
     * @param chanelId 信号渠道
     * @param content 通知内容
     * @return
     */
    private Notification getNotification(String chanelId ,String content){

    Intent intent = new Intent(this, RenGongWenZhen.class);
    PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

    NotificationCompat.Builder builder=new NotificationCompat.Builder(this,chanelId);
        builder.setSmallIcon(R.mipmap.ic_launcher)
        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
        .setContentIntent(pi)
        .setContentTitle("挂号成功")
        .setContentText(content);
        return builder.build();
}


    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除广播注册
        unregisterReceiver(localReceiver);
    }

    //开启前台服务
    private void initChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;
    }

}

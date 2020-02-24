package com.example.ourprojecttest;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class DocChatService extends Service {

    LocalReceiver localReceiver;
    IntentFilter intentFilter;
    EchoWebSocketListener listener = new EchoWebSocketListener();
    WebSocket webSocket = null;
    Request request = null;
    OkHttpClient client = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //获取活动传入的指令进行相应的操作
    class LocalReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            send(msg);
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //开始监听聊天消息
        intentFilter = new IntentFilter();
        intentFilter.addAction("docChat");
        localReceiver = new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);
        //开启聊天链接
        connect();
    }

    void connect(){
        request = new Request.Builder()
                .url(getResources().getString(R.string.ipAdrressSocket)+"IM/websocketdoc?11111")
                .build();
        client = new OkHttpClient();
        webSocket = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    void send(String msg){
        webSocket.send(msg);
        client.dispatcher().executorService().shutdown();
    }

    private final class EchoWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {

            Log.d("监听器状态", "监听器打开");

        }

        //接受消息时回调
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output("onMessage: " + text);
            String info = parseJSONWithJSONObject(text);
            Log.d("msg内容",info);

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
    private void output(final String txt) {
        Log.d("信息：",txt);
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
}

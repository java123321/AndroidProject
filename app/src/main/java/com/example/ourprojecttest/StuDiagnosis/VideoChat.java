package com.example.ourprojecttest.StuDiagnosis;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourprojecttest.PerfeActivity;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Utils.CommonMethod;

import android.view.KeyEvent;
import android.view.MotionEvent;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import java.util.LinkedList;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class VideoChat extends AppCompatActivity implements View.OnClickListener {
    private Display display;
    private int toastHeight;
    private CommonMethod method=new CommonMethod();
    private String type;//代表是学生登录还是医生登录
    private Intent intentToDoc = new Intent("com.example.ourprojecttest.DOC_UPDATE_SERVICE");//将医生的消息传给医生服务
    private Intent intentToStu = new Intent("com.example.ourprojecttest.UPDATE_SERVICE");//将学生的消息传给学生服务
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private String stuOrDocId;
    private LinearLayout chartTools;
    private TextView switcCamera;
    private TextView loundSperaker;
    private SurfaceViewRenderer localView;
    private SurfaceViewRenderer remoteView;
    private PeerConnectionFactory mPeerConnectionFactory;
    private CameraVideoCapturer mVideoCapturer;
    private VideoTrack mVideoTrack;
    private AudioTrack mAudioTrack;
    private EglBase mEglBase;
    private MediaStream mMediaStream;
    private MediaConstraints pcConstraints;
    private MediaConstraints sdpConstraints;
    private LinkedList<PeerConnection.IceServer> iceServers;
    private Peer mPeer;
    private boolean isOffer = false;
    private AudioManager mAudioManager;
    private VideoTrack remoteVideoTrack;
    private ChatListener chatListener = new ChatListener();

    //接收从服务中发过来的广播
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String videoInfo=intent.getStringExtra("videoInfo");
            Log.d("videoreceive",videoInfo);
            if(videoInfo.equals("denyVideoChat")){//如果对方拒绝视频聊天，给出提示
              //  AlertDialog.Builder builder = new AlertDialog.Builder(VideoChat.this);
              //  builder.setTitle("提示");
              //  builder.setMessage("对方拒绝了您的视频聊天！");
              //  //用户点击确定之后销毁视频聊天界面
              //  builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
              //      @Override
              //      public void onClick(DialogInterface dialog, int which) {
              //          finish();
              //      }
              //  });
              //  builder.show();
                String s="对方拒绝了您的视频聊天";
                show(R.layout.layout_tishi_email,s);
            }else if (videoInfo.startsWith("IceInfo")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(videoInfo.substring(7).toString());
                    IceCandidate candidate = null;
                    candidate = new IceCandidate(
                            jsonObject.getString("id"),
                            jsonObject.getInt("label"),
                            jsonObject.getString("candidate")
                    );
                    mPeer.peerConnection.addIceCandidate(candidate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("someoneonline", "yes1");
                Log.d("someoneonline", "yes1:" + jsonObject.toString());
            } else if (videoInfo.startsWith("SdpInfo")) {
                if (mPeer == null) {
                    mPeer = new Peer();
                }
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(videoInfo.substring(7));
                    SessionDescription description = new SessionDescription
                            (SessionDescription.Type.fromCanonicalForm(jsonObject.getString("type")),
                                    jsonObject.getString("description"));
                    mPeer.peerConnection.setRemoteDescription(mPeer, description);
                    if (!isOffer) {
                        mPeer.peerConnection.createAnswer(mPeer, sdpConstraints);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("someoneonline", "yes2");
                Log.d("someoneonline", "yes2:" + jsonObject.toString());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_chat);

        Log.d("callactivity","stuordocid:"+stuOrDocId);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.VIDEO_CHAT");
        localReceiver = new LocalReceiver();
        registerReceiver(localReceiver, intentFilter);
        initview();
        init();
        Intent intent=getIntent();
        stuOrDocId=intent.getStringExtra("stuOrDocId");//首先获取对方聊天人的id

        //如果当前端是被叫端，则向对方发送协商信息
        if(intent.getStringExtra("type").equals("invite")){
            isOffer = true;
            if (mPeer == null) {
                mPeer = new Peer();
            }
            Log.d("someoneonline", "yes");
            mPeer.peerConnection.createOffer(mPeer, sdpConstraints);
        }else {//如果是主叫方，则向对方发送协商邀请
            sendMessageToServer(stuOrDocId+"|callVideo");
            Log.d("videoChat","sendcallvideo");
        }
    }

    private void init() {
        //初始化PeerConnectionFactory
        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(getApplicationContext())
                        .setEnableVideoHwAcceleration(true)
                        .createInitializationOptions());

        //创建PeerConnectionFactory
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        mPeerConnectionFactory = new PeerConnectionFactory(options);
        //设置视频Hw加速,否则视频播放闪屏
        mPeerConnectionFactory.setVideoHwAccelerationOptions(mEglBase.getEglBaseContext(), mEglBase.getEglBaseContext());

        initConstraints();
        mVideoCapturer = createVideoCapture(this);
        VideoSource videoSource = mPeerConnectionFactory.createVideoSource(mVideoCapturer);
        mVideoTrack = mPeerConnectionFactory.createVideoTrack("videtrack", videoSource);
        //设置视频画质 i:width i1 :height i2:fps
        mVideoCapturer.startCapture(720, 1280, 30);
        AudioSource audioSource = mPeerConnectionFactory.createAudioSource(new MediaConstraints());
        mAudioTrack = mPeerConnectionFactory.createAudioTrack("audiotrack", audioSource);
        //播放本地视频
        mVideoTrack.addRenderer(new VideoRenderer(localView));
        //创建媒体流并加入本地音视频
        mMediaStream = mPeerConnectionFactory.createLocalMediaStream("localstream");
        mMediaStream.addTrack(mVideoTrack);
        mMediaStream.addTrack(mAudioTrack);

        // chatConnect();
        Log.d("connect", "yes");
    }

    private CameraVideoCapturer createVideoCapture(Context context) {
        CameraEnumerator enumerator;
        if (Camera2Enumerator.isSupported(context)) {
            enumerator = new Camera2Enumerator(context);
        } else {
            enumerator = new Camera1Enumerator(true);
        }
        final String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                CameraVideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                CameraVideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    private void initConstraints() {
        iceServers = new LinkedList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:23.21.150.121").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
        pcConstraints = new MediaConstraints();
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("RtpDataChannels", "true"));
        sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
    }

    //该方法用于将消息发送给信令服务器
    private void sendMessageToServer(String message){
        if(type.equals("Stu")){
            intentToStu.putExtra("chatMsg",message);
            sendBroadcast(intentToStu);
            intentToStu.removeExtra("chatMsg");
        }
        else{
            intentToDoc.putExtra("chatMsg",message);
            sendBroadcast(intentToDoc);
            intentToDoc.removeExtra("chatMsg");
        }

    }

    private void initview() {

        display = getWindowManager().getDefaultDisplay();
        toastHeight = display.getHeight();
        type = method.getFileData("Type", VideoChat.this);
        chartTools = findViewById(R.id.charttools_layout);
        switcCamera = findViewById(R.id.switch_camera_tv);
        loundSperaker = findViewById(R.id.loundspeaker_tv);
        switcCamera.setOnClickListener(this);
        loundSperaker.setOnClickListener(this);
        localView = findViewById(R.id.localVideoView);
        remoteView = findViewById(R.id.remoteVideoView);

        //创建EglBase对象
        mEglBase = EglBase.create();

        //初始化localView
        localView.init(mEglBase.getEglBaseContext(), null);
        localView.setKeepScreenOn(true);
        localView.setMirror(true);
        localView.setZOrderMediaOverlay(true);
        localView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        localView.setEnableHardwareScaler(false);

        //初始化remoteView
        remoteView.init(mEglBase.getEglBaseContext(), null);
        remoteView.setMirror(false);
        remoteView.setZOrderMediaOverlay(true);
        remoteView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        remoteView.setEnableHardwareScaler(false);

        //关闭扬声器
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        assert mAudioManager != null;
        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        mAudioManager.setSpeakerphoneOn(false);
    }

    class Peer implements PeerConnection.Observer, SdpObserver {
        PeerConnection peerConnection;
        Peer() {
            peerConnection = mPeerConnectionFactory.createPeerConnection(iceServers, pcConstraints, this);
            peerConnection.addStream(mMediaStream);
        }

        // PeerConnection.Observer

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.d("observerinfo:", "onSignalingChange" + signalingState.toString());
        }


        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.d("observerinfo:", "onIceConnectionChange:" + iceConnectionState.toString());
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                remoteVideoTrack.dispose();
                remoteView.clearImage();
                mPeer = null;
                isOffer = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       //AlertDialog.Builder builder = new AlertDialog.Builder(VideoChat.this);
                       // builder.setTitle("提示");
                       // builder.setMessage("对方已退出视频聊天！");
                        //用户点击确定之后销毁视频聊天界面
                       // builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                       //     @Override
                        //    public void onClick(DialogInterface dialog, int which) {
                         //       finish();
                         //   }
                       // });
                       // builder.show();
                        String s="对方已退出视频聊天";
                        show(R.layout.layout_tishi_email,s);
                    }
                });
            }
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            Log.d("observerinfo:", "onIceConnectionReceivingChange:" + b);
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            Log.d("observerinfo:", "onIceGatheringChange:" + iceGatheringState.toString());
        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            Log.d("observerinfo:", "onIceCandidate:" + iceCandidate.toString());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("label", iceCandidate.sdpMLineIndex);
                jsonObject.put("id", iceCandidate.sdpMid);
                jsonObject.put("candidate", iceCandidate.sdp);

//                chatListener.socket.send("IceInfo" + jsonObject.toString());
                Log.d("sendice", jsonObject.toString());
                sendMessageToServer(stuOrDocId+"|IceInfo"+jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
            Log.d("observerinfo:", "onIceCandidatesRemoved:" + iceCandidates.toString());
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            Log.d("observerinfo:", "onAddStream:" + mediaStream.toString());
            remoteVideoTrack = mediaStream.videoTracks.get(0);
            remoteVideoTrack.addRenderer(new VideoRenderer(remoteView));

        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.d("observerinfo:", "onRemoveStream:" + mediaStream.toString());
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
            Log.d("observerinfo:", "onDataChannel:" + dataChannel.toString());
        }

        @Override
        public void onRenegotiationNeeded() {
            Log.d("observerinfo:", "onRenegotiationNeeded:");
        }

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
            Log.d("observerinfo:", "onAddTrack:");
        }

        //    SdpObserver

        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            Log.d("observerinfo:", "onCreateSuccess:");
            peerConnection.setLocalDescription(this, sessionDescription);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", sessionDescription.type.canonicalForm());
                jsonObject.put("description", sessionDescription.description);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("sendsdp", jsonObject.toString());
            sendMessageToServer(stuOrDocId+"|SdpInfo"+jsonObject.toString());
        }

        @Override
        public void onSetSuccess() {
            Log.d("observerinfo:", "onSetSuccess:");
        }

        @Override
        public void onCreateFailure(String s) {
            Log.d("observerinfo:", "onCreateFailure:");
        }

        @Override
        public void onSetFailure(String s) {
            Log.d("observerinfo:", "onSetFailure:");
        }
    }

    //webSocket回调方法
    class ChatListener extends WebSocketListener {
        WebSocket socket = null;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            socket = webSocket;
            Log.d("interfacechat", "连接成功");
            //webSocket.send("hello");
        }


        //接受消息时回调
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            if (text.equals("SomeOneOnline")) {
                isOffer = true;
                if (mPeer == null) {
                    mPeer = new Peer();
                }
                Log.d("someoneonline", "yes");
                mPeer.peerConnection.createOffer(mPeer, sdpConstraints);
            } else if (text.startsWith("IceInfo")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(text.substring(7).toString());
                    IceCandidate candidate = null;
                    candidate = new IceCandidate(
                            jsonObject.getString("id"),
                            jsonObject.getInt("label"),
                            jsonObject.getString("candidate")
                    );
                    mPeer.peerConnection.addIceCandidate(candidate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("someoneonline", "yes1");
                Log.d("someoneonline", "yes1:" + jsonObject.toString());
            } else if (text.startsWith("SdpInfo")) {
                if (mPeer == null) {
                    mPeer = new Peer();
                }
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(text.substring(7));
                    SessionDescription description = new SessionDescription
                            (SessionDescription.Type.fromCanonicalForm(jsonObject.getString("type")),
                                    jsonObject.getString("description"));
                    mPeer.peerConnection.setRemoteDescription(mPeer, description);
                    if (!isOffer) {
                        mPeer.peerConnection.createAnswer(mPeer, sdpConstraints);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("someoneonline", "yes2");
                Log.d("someoneonline", "yes2:" + jsonObject.toString());
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);

        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {

        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.d("interfacefalure", "学生聊天接口失败");
            webSocket.close(1000, null);
        }

    }

    @Override
    protected void onDestroy() {
//        if (mSocket != null) {
//            mSocket.disconnect();
//        }
        if (mVideoCapturer != null) {
            try {
                mVideoCapturer.stopCapture();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (mPeer != null) {
            mPeer.peerConnection.close();
            mPeer = null;
        }
        if (mVideoTrack != null) {
            mVideoTrack.dispose();
        }
        if (mAudioTrack != null) {
            mAudioTrack.dispose();
        }
        super.onDestroy();
        unregisterReceiver(localReceiver);

        Log.d("videodestroy",type);
    }
    //监听音量键控制视频通话音量
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:{//点击返回键是进行确认
                tuichuvideo();
                return true;
            }
            case KeyEvent.KEYCODE_VOLUME_UP:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                toggleChartTools();
                break;
        }
        return super.onTouchEvent(event);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_camera_tv:
                mVideoCapturer.switchCamera(new CameraVideoCapturer.CameraSwitchHandler() {
                    @Override
                    public void onCameraSwitchDone(boolean b) {
                        //切换摄像头完成

                    }

                    @Override
                    public void onCameraSwitchError(String s) {
                        //切换摄像头错误
                    }
                });
                break;

            case R.id.loundspeaker_tv:
                if (mAudioManager.isSpeakerphoneOn()) {
                    mAudioManager.setSpeakerphoneOn(false);

                    Toast toast = Toast.makeText(VideoChat.this, "扬声器已关闭！", Toast.LENGTH_SHORT);
                    // 这里给了一个1/4屏幕高度的y轴偏移量
                    toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                    toast.show();
                } else {
                    mAudioManager.setSpeakerphoneOn(true);
                    Toast toast = Toast.makeText(VideoChat.this, "扬声器已打开！", Toast.LENGTH_SHORT);
                    // 这里给了一个1/4屏幕高度的y轴偏移量
                    toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                    toast.show();

                }
                break;
        }

    }
    private void toggleChartTools() {
        if (chartTools.isShown()) {
            chartTools.setVisibility(View.INVISIBLE);
        } else {
            chartTools.setVisibility(View.VISIBLE);
        }
    }



    public void show(int x,String s1){
        final Dialog dialog = new Dialog(VideoChat.this,R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(VideoChat.this).inflate(x, null);
        TextView describe=inflate.findViewById(R.id.describe);
       // TextView jianjie=inflate.findViewById(R.id.jianjie);
        describe.setText(s1);
        //jianjie.setText(s2);
        TextView yes = inflate.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.CENTER);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width =800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    private void tuichuvideo(){
        final Dialog dialog = new Dialog(this,R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_tuichuvideno, null);
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
    }

}
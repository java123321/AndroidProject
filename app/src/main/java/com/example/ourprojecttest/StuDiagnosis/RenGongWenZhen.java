package com.example.ourprojecttest.StuDiagnosis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.Service.StuService;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import okio.ByteString;


public class RenGongWenZhen extends AppCompatActivity {

    private VibrationEffect vibrationEffect ;
    private Vibrator vibrator;
    private Handler mOffHandler;
    private Timer mOffTime;
    private String ipAddress;
    private Display display;
    // 获取屏幕高度
    private int height;
    private final int SUCCESS = 1;
    private final int FAULT = 0;
    private CommonMethod method = new CommonMethod();
    private Intent intentToService = new Intent("com.example.ourprojecttest.UPDATE_SERVICE");
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private LinearLayout noDoctor;
    private Button guanbi;
    private Button guaHao;
    private TextView displayStuRank;
    private DisplayDocAdapter adapter;
    private RecyclerView mRecycler;
    private SwipeRefreshLayout refresh;
    private MediaPlayer mediaPlayer;//MediaPlayer对象
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("msgwhat", "what:" + msg.what);
            refresh.setRefreshing(false);
            switch (msg.what) {
                case SUCCESS:
                    ArrayList<DisplayDocBean> list = (ArrayList<DisplayDocBean>) msg.obj;
                    Log.d("msgwhat", "size" + list.size());
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    noDoctor.setVisibility(View.GONE);
                    mRecycler.setVisibility(View.VISIBLE);
                    break;
                case FAULT:
                    noDoctor.setVisibility(View.VISIBLE);
                    mRecycler.setVisibility(View.GONE);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ren_gong_wen_zhen);
        ipAddress = getResources().getString(R.string.ipAdrress);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        //开始注册广播监听器，准备接受服务里发送过来的更新挂号信息
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.UPDATE_PERSONS");
        localReceiver = new LocalReceiver();
        registerReceiver(localReceiver, intentFilter);
    }

    /**
     * 接收服务里传过来的挂号更新信息
     */
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if(intent.hasExtra("noDocOnline")){
                noDoctor.setVisibility(View.VISIBLE);
                mRecycler.setVisibility(View.GONE);
                adapter.mList.clear();
                adapter.notifyDataSetChanged();
            }else if (intent.hasExtra("persons")) {
                String person = intent.getStringExtra("persons");
                if (person.equals("-1")) {//如果是-1的话代表到你了，发出提示窗口
                    show(intent);

                } else {//否则显示当前排队人数
                    displayStuRank.setText("当前排队位次: " + intent.getStringExtra("persons") + "位");
                }
            }
        }
    }

    @Override  //退出接诊活动时弹出提示框
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(StuService.isGuaHao){//如果学生正在挂号，给学生弹出提示
                final Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);        //展示对话框
                //填充对话框的布局
                View inflate = LayoutInflater.from(this).inflate(R.layout.layout_goutong, null);
                //初始化控件
                TextView countTime = inflate.findViewById(R.id.countTime);
                countTime.setText("您当前正在挂号，如果退出当前界面将终止挂号功能，您可以选择点击手机home键返回桌面，app将在后台为您保持继续挂号，是否要结束挂号?");
                TextView yes = inflate.findViewById(R.id.yes);
                countTime.setTextSize(12);
                yes.setOnClickListener(view -> {
                    intentToService.putExtra("msg", "ExitGuaHao");
                    sendBroadcast(intentToService);
                    finish();
                });
                TextView no = inflate.findViewById(R.id.no);
                no.setOnClickListener(view -> {
                    dialog.dismiss();
                });

                //将布局设置给Dialog
                dialog.setContentView(inflate);
                //获取当前Activity所在的窗体
                Window dialogWindow = dialog.getWindow();
                //设置Dialog从窗体底部弹出
                dialogWindow.setGravity(Gravity.CENTER);
                //获得窗体的属性
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = 800;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(lp);
                dialog.show();//显示对话框
            }else{
                finish();
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
        //释放音频文件
        if((mediaPlayer!=null)&&mediaPlayer.isLooping()){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Log.d("wenzhen", "onDestroy");
    }

    //从服务器获取当前在线医生的信息
    private void getData() {
        adapter.mList.clear();
        adapter.notifyDataSetChanged();
        Log.d("msgwhat","startgetdata");
        noDoctor.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
        refresh.setRefreshing(true);
        final String url = ipAddress + "IM/GetOnlineDoc";
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONToDoc(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //解析在线医生信息json
    private void parseJSONToDoc(String data) {
        Log.d("msgwhat", "data" + data);
        ArrayList<DisplayDocBean> list = new ArrayList<>();
        Message msg = Message.obtain();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (!jsonObject.has("#x")) {
                    DisplayDocBean info = new DisplayDocBean();
                    info.setName(jsonObject.getString("Doc_Name"));
                    info.setBrief(jsonObject.getString("Doc_Introduce"));
                    info.setSex(jsonObject.getString("Doc_Sex"));
                    Log.d("rengongwenzhen.doc.icon",ipAddress+jsonObject.getString("Doc_Icon"));
                    //设置医生头像
                    info.setIcon(method.drawableToBitamp(Drawable.createFromStream(new URL(ipAddress + jsonObject.getString("Doc_Icon")).openStream(), "image.jpg")));


                    //设置医生的执照
                    info.setLicense(method.drawableToBitamp(Drawable.createFromStream(new URL(ipAddress + jsonObject.getString("Doc_License")).openStream(), "image.jpg")));
                    list.add(info);
                } else {//如果当前没有在线医生
                    msg.what = FAULT;
                    handler.sendMessage(msg);
                    return;
                }
            }

            Log.d("msgwhat", "size1" + list.size());
            msg.what = SUCCESS;
            msg.obj = list;
            handler.sendMessage(msg);

        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {


        //初始化震动模块
        vibrator=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrationEffect = VibrationEffect.createWaveform(new long[]{1000,1000},0);
        display = getWindowManager().getDefaultDisplay();
        // 获取屏幕高度
        height = display.getHeight();
        refresh = findViewById(R.id.swipeRefresh);
        //设置下拉刷新的的更新事件
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        refresh.setColorSchemeColors(getResources().getColor(R.color.color_bottom));
        refresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.color_progressbar));
        noDoctor = findViewById(R.id.noDoctor);
        noDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        mRecycler = findViewById(R.id.stuDisplayDoc);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        adapter = new DisplayDocAdapter(RenGongWenZhen.this);
        mRecycler.setAdapter(adapter);
        //联网获取数据
        getData();
        guanbi = findViewById(R.id.stu_wenzhen_guanbi);
        guaHao = findViewById(R.id.stu_wenzhen_guahao);
        displayStuRank = findViewById(R.id.stuWenZhenDisplayGuaHaoInfo);
        //设置点击挂号的点击事件
        guaHao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果当前医生不在线
                if(noDoctor.getVisibility()==View.VISIBLE){
                    Toast toast = Toast.makeText(RenGongWenZhen.this, "当前暂无医生接诊，请稍后再来！", Toast.LENGTH_SHORT);
                    // 这里给了一个1/4屏幕高度的y轴偏移量
                    toast.setGravity(Gravity.BOTTOM, 0, height / 5);
                    toast.show();
                }else{
                    //如果服务在运行
                    if (StuService.isGuaHao) {
                        Toast toast = Toast.makeText(RenGongWenZhen.this, "正在挂号，请勿重复点击！", Toast.LENGTH_SHORT);
                        // 这里给了一个1/4屏幕高度的y轴偏移量
                        toast.setGravity(Gravity.BOTTOM, 0, height / 5);
                        toast.show();
                    } else {
                        //通知服务开启挂号
                        intentToService.putExtra("msg", "StartGuaHao");
                        sendBroadcast(intentToService);
                    }
                }
            }
        });

        //关闭的点击事件
        guanbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果在挂号在运行
                if (StuService.isGuaHao) {
                    //给服务发送取消挂号的广播
                    intentToService.putExtra("msg", "ExitGuaHao");
                    sendBroadcast(intentToService);
                    displayStuRank.setText("当前排队位次: 暂无位次信息");
                } else {
                    Toast toast = Toast.makeText(RenGongWenZhen.this, "您暂未开启挂号！", Toast.LENGTH_SHORT);
                    // 这里给了一个1/4屏幕高度的y轴偏移量
                    toast.setGravity(Gravity.BOTTOM, 0, height / 5);
                    toast.show();
                }
            }
        });

    }

    //学生取消之后弹出确认框
    private void stuCancelConfirmDialog() {
        final Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_tishi, null);
        //初始化控件
        TextView yes = inflate.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
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
        dialogWindow.setGravity(Gravity.CENTER);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    private void stuCountTimeToDeny(final TextView countTime, final Dialog mDialog) {
        mediaPlayer= MediaPlayer.create(this,R.raw.tkzc);
        mediaPlayer.start();
        vibrator.vibrate(vibrationEffect);
        mOffHandler = new Handler() {
            public void handleMessage(Message msg) {

                if (msg.what > 0) {
                    ////动态显示倒计时
                    countTime.setText("同意医生的接诊请求吗？" + msg.what + "秒后默认拒绝！");
                } else {
                    ////倒计时结束后关闭计时器
                    mOffTime.cancel();
                    vibrator.cancel();
                    mediaPlayer.stop();
                    //给服务发送取消挂号的广播
                    intentToService.putExtra("msg", "ExitGuaHao");
                    sendBroadcast(intentToService);

                    //关闭倒计时窗口
                    mDialog.dismiss();
                    //弹出学生放弃沟通提示窗口
                    Log.d("docop", "dialog13");
                    stuCancelConfirmDialog();
                }
                super.handleMessage(msg);
            }

        };
        //倒计时
        mOffTime = new Timer(true);
        TimerTask tt = new TimerTask() {
            int countTime = 20;
            public void run() {
                if (countTime > 0) {
                    countTime--;
                }
                Message msg = new Message();
                msg.what = countTime;
                mOffHandler.sendMessage(msg);
            }
        };
        mOffTime.schedule(tt, 0, 1000);
    }

    private Dialog show(final Intent intent) {
        final Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_goutong, null);
        //初始化控件
        TextView countTime = inflate.findViewById(R.id.countTime);
        stuCountTimeToDeny(countTime, dialog);//该方法用于显示倒计时
        TextView yes = inflate.findViewById(R.id.yes);
        yes.setOnClickListener(view -> {
        //学生点击沟通之后取消计时器
            mOffTime.cancel();
            vibrator.cancel();//停止震动
            mediaPlayer.stop();

            dialog.dismiss();
            intentToService.putExtra("msg", "Chat");
            intentToService.putExtra("docId",intent.getStringExtra("docId"));
            sendBroadcast(intentToService);
            //准备跳到聊天界面，并将医生的di放到意图里
            Intent intentToChat = new Intent(RenGongWenZhen.this, Chat.class);
            intentToChat.putExtra("docId", intent.getStringExtra("docId"));
            intentToChat.putExtra("docName", intent.getStringExtra("docName"));
            //对医生的头像进行判断是否有无
            if (intent.hasExtra("docPicture")) {
                intentToChat.putExtra("docPicture", intent.getByteArrayExtra("docPicture"));
            } else {
                intentToChat.removeExtra("docPicture");
            }
            startActivity(intentToChat);
        });
        TextView no = inflate.findViewById(R.id.no);
        no.setOnClickListener(view -> {
            //学生点击放弃沟通之后取消计时器
            mOffTime.cancel();
            vibrator.cancel();//停止震动
            mediaPlayer.stop();
            dialog.dismiss();
            //给服务发送拒绝问诊的广播
            intentToService.putExtra("msg", "Deny");
            intentToService.putExtra("docId",intent.getStringExtra("docId"));
            sendBroadcast(intentToService);
        });
        //将布局设置给Dialog
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
        return dialog;
    }

}

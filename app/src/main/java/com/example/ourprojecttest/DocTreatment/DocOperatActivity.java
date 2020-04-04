package com.example.ourprojecttest.DocTreatment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.ourprojecttest.StuDiagnosis.Chat;
import com.example.ourprojecttest.Service.DocService;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DocOperatActivity extends AppCompatActivity {
    private TextView dispalyStuNumber;
    private Display display;
    // 获取屏幕高度
    private int height;
    private Handler mOffHandler;
    private Timer mOffTime;
    private Dialog mDialog, dialog1;
    private String ipAddress;
    private final int SUCCESS = 1;
    private View x;
    private TextView mn;
    private final int FAULT = 0;
    private Intent intentToService = new Intent("com.example.ourprojecttest.DOC_UPDATE_SERVICE");//改
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private CommonMethod method = new CommonMethod();
    private RecyclerView mRecycler;
    private SwipeRefreshLayout refresh;
    private Button access;
    private LinearLayout noStudent;
    private DisplayStuAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("msgwhat", "what:" + msg.what);
            refresh.setRefreshing(false);
            switch (msg.what) {
                case SUCCESS:
                    ArrayList<DisplayStuBean> list = (ArrayList<DisplayStuBean>) msg.obj;
                    Log.d("msgwhat", "size" + list.size());
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    dispalyStuNumber.setText("当前学生挂号人数: "+list.size()+"人");
                    noStudent.setVisibility(View.GONE);
                    mRecycler.setVisibility(View.VISIBLE);
                    break;
                case FAULT:
                    dispalyStuNumber.setText("当前学生挂号人数: 0人");
                    noStudent.setVisibility(View.VISIBLE);
                    mRecycler.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_operat);
        ipAddress = getResources().getString(R.string.ipAdrress);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        //开始注册广播监听器，准备接受服务里发送过来的更新挂号信息
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.DOC_UPDATE_PERSONS");//改
        localReceiver = new LocalReceiver();
        getApplicationContext().registerReceiver(localReceiver, intentFilter);
        Log.d("目的", "监听学生人数开始");
    }

    //从服务器获取当前在线学生的信息
    private void getData() {
        Log.d("msgwhat","statgetstunumber");
        refresh.setRefreshing(true);
        refresh.setColorSchemeColors(getResources().getColor(R.color.color_bottom));
        refresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.color_progressbar));
        final String url = ipAddress + "IM/GetOnlineStu";
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
                    parseJSONToStu(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //解析在线医生信息json
    private void parseJSONToStu(String data) {
        Log.d("msgwhat", "data" + data);
        ArrayList<DisplayStuBean> list = new ArrayList<>();
        list.clear();
        Message msg = Message.obtain();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (!jsonObject.has("#x")) {
                    DisplayStuBean info = new DisplayStuBean();
                    info.setName(jsonObject.getString("Stu_Name"));
                    ;
                    info.setSex(jsonObject.getString("Stu_Sex"));
                    info.setBirthday(jsonObject.getString("Stu_Birth"));
                    info.setHeight(jsonObject.getString("Stu_Height"));
                    info.setWeight(jsonObject.getString("Stu_Weight"));
                    //设置学生头像
                    String stuIconUrl = jsonObject.getString("Stu_Icon").trim();
                    if (stuIconUrl == null || stuIconUrl.equals("")) {//如果学生没有设置头像
                        info.setIcon(method.drawableToBitamp(Drawable.createFromStream(new URL(ipAddress + jsonObject.getString("Stu_Icon")).openStream(), "image.jpg")));
                    } else {
                        info.setIcon(null);
                    }

                    list.add(info);
                } else {//如果当前没有在线学生
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

        display = getWindowManager().getDefaultDisplay();
        // 获取屏幕高度
        height = display.getHeight();

        //如果有状态码state代表用户从前台服务跳进来
        Intent intent = getIntent();
        if (intent.hasExtra("state")) {
            //如果是-1代表当前是
            if (intent.getStringExtra("state").equals(-1)) {

            } else {

            }
        }
        refresh = findViewById(R.id.swipeRefresh);
        //设置下拉刷新的的更新事件
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        dispalyStuNumber=findViewById(R.id.displayStuNumber);
        noStudent = findViewById(R.id.noStudent);
        access = findViewById(R.id.access);
        mRecycler = findViewById(R.id.docDisplayStu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        adapter = new DisplayStuAdapter(DocOperatActivity.this);
        mRecycler.setAdapter(adapter);
        //联网获取数据
        getData();
        //医生点击接诊的点击事件
        access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noStudent.getVisibility() == View.VISIBLE) {
                    Toast toast = Toast.makeText(DocOperatActivity.this, "当前暂无学生问诊，请等候！", Toast.LENGTH_SHORT);
                    // 这里给了一个1/4屏幕高度的y轴偏移量
                    toast.setGravity(Gravity.BOTTOM, 0, height / 5);
                    toast.show();
                } else {
                    intentToService.putExtra("msg", "Access");
                    sendBroadcast(intentToService);
                    Log.d("线程信息", "：" + Thread.currentThread().getId() + "  " +
                            "弹出聊天消息");
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("候诊页面状态", "onDestroy");
    }


    @Override  //退出接诊活动时弹出提示框
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            show();
        }
        return false;
    }

    //学生取消之后弹出确认框
    private void stuCancelConfirmDialog() {
        final Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_jixujiezhen, null);
        //初始化控件
        TextView yes = inflate.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentToService.putExtra("msg", "Access");
                sendBroadcast(intentToService);
                dialog.dismiss();
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
        dialogWindow.setGravity(Gravity.CENTER);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
//       将属性设置给窗体
        dialog.show();//显示对话框
    }

    //学生等待确认期间，医生端弹出倒计时窗口
    private void waitStuConfirmDialog() {
        dialog1 = new Dialog(this, R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_time, null);
        TextView mn = inflate.findViewById(R.id.time);
        //将布局设置给Dialog
        dialog1.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog1.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
//       将属性设置给窗体
        dialog1.show();//显示对话框
        mOffHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what > 0) {
                    ////动态显示倒计时
                    mn.setText("正在等待学生确认\n确认剩余时间为:" + msg.what + "秒");
                } else {
                    ////倒计时结束后关闭计时器
                    mOffTime.cancel();
                    //关闭倒计时窗口
                    dialog1.dismiss();
                    //弹出学生取消沟通提示窗口
                    Log.d("docop", "dialog13");
                    stuCancelConfirmDialog();
                }
                super.handleMessage(msg);
            }

        };
        //倒计时
        mOffTime = new Timer(true);
        TimerTask tt = new TimerTask() {
            int countTime = 10;

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


    /**
     * 接收服务里传过来的挂号更新信息
     */
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("updateStu")) {//如果是更新挂号学生
                getData();
            } else if (intent.hasExtra("Dialog")) {
                //创建对话框
                waitStuConfirmDialog();
            } else {
                Log.d("docop", "received");

                Log.d("docop5", "has chatmsg?" + intent.hasExtra("chatmsg"));
                Log.d("docop6", "has chatmsg?" + intent.hasExtra("chatmsg"));
                Log.d("docop", "has validate?" + intent.hasExtra("validate"));
                //如果学生发送的是正常聊天消息
                if (intent.hasExtra("chatmsg")) {
                    Log.d("docop1", "chatmsgis:" + intent.getStringExtra("chatmsg"));
                } else {//如果学生发送的是chat或者deny
                    String validateResult = intent.getStringExtra("validate");
                    Log.d("docop", validateResult);
                    //如果学生拒绝了和医生沟通
                    if (validateResult.contains("deny")) {
                        mOffTime.cancel();
                        dialog1.dismiss();
                        //弹出学生拒绝沟通提示框
                        stuCancelConfirmDialog();
                        Log.d("docop", "dialog13");
                    } else {//如果学生同意和医生沟通
                        //将计时器取消
                        mOffTime.cancel();
                        dialog1.dismiss();
                        Log.d("docop", "intoChat");
                        Intent intentToChat = new Intent(DocOperatActivity.this, Chat.class);
                        intentToChat.putExtra("stuId", validateResult);
                        intentToChat.putExtra("stuName", intent.getStringExtra("stuName"));
                        if (intent.hasExtra("stuPicture")) {
                            intentToChat.putExtra("stuPicture", intent.getByteArrayExtra("stuPicture"));
                        } else {
                            intentToChat.removeExtra("stuPicture");
                        }

                        startActivity(intentToChat);
                    }
                }
            }
        }
    }

    public Dialog show() {
        final Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_tuichuliaotian, null);
        //初始化控件
        TextView yes = inflate.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentToService.putExtra("msg", "exit");
                sendBroadcast(intentToService);
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
        dialogWindow.setGravity(Gravity.CENTER);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
//       将属性设置给窗体
        dialog.show();//显示对话框
        return dialog;
    }
}



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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.Service.StuService;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import okio.ByteString;


public class RenGongWenZhen extends AppCompatActivity {
    private final int SUCCESS=1;
    private final int FAULT=0;
    CommonMethod method=new CommonMethod();
    Intent intentToService=new Intent("com.example.ourprojecttest.UPDATE_SERVICE");
    LocalReceiver localReceiver;
    IntentFilter intentFilter;
    private TextView noDoctor;
    private Button guanbi;
    private Button guaHao;
    private TextView text;
    private DisplayDocAdapter adapter;
    private TextView mShutDownTextView;
    private RecyclerView mRecycler;
    private ArrayList<DisplayDocBean> lists=new ArrayList<>();
    private SwipeRefreshLayout refresh;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("msgwhat","what:"+msg.what);
            refresh.setRefreshing(false);
            switch (msg.what){
                case SUCCESS:
                    ArrayList<DisplayDocBean>list=(ArrayList<DisplayDocBean>)msg.obj;
                    Log.d("msgwhat","size"+list.size());
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
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        //开始注册广播监听器，准备接受服务里发送过来的更新挂号信息
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.UPDATE_PERSONS");
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);
    }
    /**
     * 接收服务里传过来的挂号更新信息
     */
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if(intent.hasExtra("persons")){
                String person=intent.getStringExtra("persons");

                if(person.equals("-1")){//如果是-1的话代表到你了，发出提示窗口
                    Log.d("chat0","0102");
                    final Dialog dialog = new AlertDialog.Builder(RenGongWenZhen.this).setTitle("选择")
                            .setCancelable(false)
                            //.setView(mShutDownTextView)
                            .setPositiveButton("沟通", new DialogInterface.OnClickListener() {//如果用户点击了确定按钮则进入与医生的聊天界面
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //给医生发通知表明学生统一看病
                                    intentToService.putExtra("msg","Chat");
                                    sendBroadcast(intentToService);
                                    //准备跳到聊天界面，并将医生的di放到意图里
                                    Intent intentToChat=new Intent(RenGongWenZhen.this, Chat.class);
                                    intentToChat.putExtra("docId",intent.getStringExtra("docId"));
                                    intentToChat.putExtra("docName",intent.getStringExtra("docName"));
                                    intentToChat.putExtra("docPicture",intent.getByteArrayExtra("docPicture"));
                                    startActivity(intentToChat);
                                }
                            })
                            .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    intentToService.putExtra("msg","Deny");
                                    sendBroadcast(intentToService);
                                }
                            }).create();
                            dialog.show();

                            CountDownTimer cdt = new CountDownTimer(10000,1000) {
                                int i = 10;
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }
                                @Override
                                public void onFinish() {
                                    if (dialog != null){
                                        dialog.dismiss();
                                    }
                                    intentToService.putExtra("msg","Deny");
                                    sendBroadcast(intentToService);

                                }
                            };
                            cdt.start();
                }
                else{//否则显示当前排队人数
                    text.setText("当前挂号位次为第"+intent.getStringExtra("persons")+"位");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("wenzhen","onDestroy");
    }

    //从服务器获取当前在线医生的信息
    private void getData(){
        refresh.setRefreshing(true);
        final String url=getResources().getString(R.string.ipAdrress)+"IM/GetOnlineDoc";
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
    private void parseJSONToDoc(String data){
        Log.d("msgwhat","data"+data);
        ArrayList<DisplayDocBean> list=new ArrayList<>();
        Message msg=Message.obtain();
        try{
            JSONArray jsonArray=new JSONArray(data);
          for(int i=0;i<jsonArray.length();i++){
              JSONObject jsonObject=jsonArray.getJSONObject(i);

              if(!jsonObject.has("#x")){
                  DisplayDocBean info=new DisplayDocBean();
                  info.setName(jsonObject.getString("Doc_Name"));;
                  info.setBrief(jsonObject.getString("Doc_Introduce"));
                  info.setSex(jsonObject.getString("Doc_Sex"));
                  //设置医生头像
                  info.setIcon(method.drawableToBitamp( Drawable.createFromStream(new URL(jsonObject.getString("Doc_Icon")).openStream(),"image.jpg")));
                    //设置医生的执照
                  info.setLicense(method.drawableToBitamp( Drawable.createFromStream(new URL(jsonObject.getString("Doc_License")).openStream(),"image.jpg")));
                  list.add(info);
              }
              else {//如果当前没有在线医生
                    msg.what=FAULT;
                    handler.sendMessage(msg);
                    return;
              }
          }

          Log.d("msgwhat","size1"+list.size());
          msg.what=SUCCESS;
          msg.obj=list;
          handler.sendMessage(msg);

        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView(){
        //如果有状态码state代表用户从前台服务跳进来
        Intent intent=getIntent();
        if(intent.hasExtra("state")){
            //如果是-1代表当前是
          if(intent.getStringExtra("state").equals(-1)){

          }
          else{

          }

        }
        refresh=findViewById(R.id.swipeRefresh);
        //设置下拉刷新的的更新事件
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        noDoctor=findViewById(R.id.noDoctor);
        noDoctor.setText("当前暂无医生在线，请稍后再来！");
        mRecycler=findViewById(R.id.stuDisplayDoc);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        adapter = new DisplayDocAdapter(RenGongWenZhen.this);
        mRecycler.setAdapter(adapter);
        //联网获取数据
        getData();
        guanbi=findViewById(R.id.stu_wenzhen_guanbi);
        guaHao=findViewById(R.id.stu_wenzhen_guahao);
        text=findViewById(R.id.stuWenZhenDisplayGuaHaoInfo);


        //设置点击挂号的点击事件
        guaHao.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //如果服务在运行
                if(method.isServiceWork(RenGongWenZhen.this,"com.example.ourprojecttest.Service.StuService")){
                    Toast.makeText(RenGongWenZhen.this,"正在挂号，请勿重复点击！",Toast.LENGTH_SHORT).show();
                }
                else{
                    //创建一个服务
                    Intent intentStartService = new Intent(RenGongWenZhen.this, StuService.class);
                    startService(intentStartService);
                }
            }
        });

        //关闭的点击事件
        guanbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //如果服务在运行
                if(method.isServiceWork(RenGongWenZhen.this,"com.example.ourprojecttest.Service.StuService")){
                    //给服务发送取消挂号的广播
                    intentToService.putExtra("msg","ExitGuaHao");
                    sendBroadcast(intentToService);
                    Toast.makeText(RenGongWenZhen.this,"挂号取消成功！",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RenGongWenZhen.this,"您暂未开启挂号！",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}

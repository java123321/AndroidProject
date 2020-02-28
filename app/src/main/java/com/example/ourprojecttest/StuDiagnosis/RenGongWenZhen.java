package com.example.ourprojecttest.StuDiagnosis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourprojecttest.CommonMethod;
import com.example.ourprojecttest.DisplayDocAdapter;
import com.example.ourprojecttest.DisplayDocList;
import com.example.ourprojecttest.GuaHaoService;
import com.example.ourprojecttest.ImmersiveStatusbar;
import com.example.ourprojecttest.R;

import java.util.ArrayList;

//import okio.ByteString;


public class RenGongWenZhen extends AppCompatActivity {
    CommonMethod method=new CommonMethod();
    Intent intentToService=new Intent("com.example.ourprojecttest.UPDATE_SERVICE");
    LocalReceiver localReceiver;
    IntentFilter intentFilter;
    private Button guanbi;
    private Button guaHao;
    private TextView text;
    private DisplayDocAdapter adapter;
    private RecyclerView mRecycler;
    private ArrayList<DisplayDocList> lists=new ArrayList<>();

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
                    new  AlertDialog.Builder(RenGongWenZhen.this)
                            .setTitle("确认" )
                            .setMessage("点击沟通进入与医生对话界面，点击放弃将取消此次挂号资格！" )
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
                            })
                            .show();
                }
                else{//否则显示当前排队人数
                    text.setText("当前挂号位次为第"+intent.getStringExtra("persons")+"位");
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("wenzhen","onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("wenzhen","onPause");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("wenzhen","onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("wenzhen","onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("wenzhen","onStope");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("wenzhen","onDestroy");
    }


    private Bitmap Rfile2Bitmap(){
        return BitmapFactory.decodeResource(getResources(),R.drawable.person);
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
        mRecycler=findViewById(R.id.stuDisplayDoc);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        adapter = new DisplayDocAdapter(RenGongWenZhen.this);
        mRecycler.setAdapter(adapter);

        DisplayDocList d=new DisplayDocList();
        d.setIcon(Rfile2Bitmap());
        d.setName("华佗");
        d.setBrief("华佗手术很很牛皮");
        lists.add(d);
        d=new DisplayDocList();
        d.setIcon(Rfile2Bitmap());
        d.setName("李时珍");
        d.setBrief("李时珍尝遍百草");
        lists.add(d);
        d=new DisplayDocList();
        d.setIcon(Rfile2Bitmap());
        d.setName("钟南山");
        d.setBrief("钟南山抗击非典");
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        lists.add(d);
        adapter.setList(lists);
        adapter.notifyDataSetChanged();

        guanbi=findViewById(R.id.stu_wenzhen_guanbi);
        guaHao=findViewById(R.id.stu_wenzhen_guahao);
        text=findViewById(R.id.stuWenZhenDisplayGuaHaoInfo);


        //设置点击挂号的点击事件
        guaHao.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //如果服务在运行
                if(method.isServiceWork(RenGongWenZhen.this,"com.example.ourprojecttest.GuaHaoService")){
                    Toast.makeText(RenGongWenZhen.this,"正在挂号，请勿重复点击！",Toast.LENGTH_SHORT).show();
                }
                else{
                    //创建一个服务
                    Intent intentStartService = new Intent(RenGongWenZhen.this, GuaHaoService.class);
                    startService(intentStartService);
                }
            }
        });

        //关闭的点击事件
        guanbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //如果服务在运行
                if(method.isServiceWork(RenGongWenZhen.this,"com.example.ourprojecttest.GuaHaoService")){
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

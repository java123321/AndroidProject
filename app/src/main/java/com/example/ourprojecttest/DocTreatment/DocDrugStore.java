package com.example.ourprojecttest.DocTreatment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ourprojecttest.R;
import com.example.ourprojecttest.StuDrugStore.StuDrugStoreFragment;

public class DocDrugStore extends AppCompatActivity {
    private Display display;
    private int toastHeight;
    private StuDrugStoreFragment drugStore;
    private LocalReceiver localReceiver;
    private Receiver receiver;
    private Receiver1 receiver1;
    private IntentFilter intentFilter;
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg=intent.getStringExtra("msg");
            if(msg.equals("success")){
                Toast toast = Toast.makeText(DocDrugStore.this, "药品添加成功！", Toast.LENGTH_SHORT);
                // 这里给了一个1/4屏幕高度的y轴偏移量
                toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                toast.show();
            }
            else{
                Toast toast = Toast.makeText(DocDrugStore.this, "此药品已添加！", Toast.LENGTH_SHORT);
                // 这里给了一个1/4屏幕高度的y轴偏移量
                toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                toast.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_drug_store);
        display = getWindowManager().getDefaultDisplay();
        toastHeight = display.getHeight();

        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.DocDrugStore");
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);
        // 获取FragmentManager
        FragmentManager fragmentManager = getFragmentManager();
        IntentFilter intentFilter1=new IntentFilter();
        IntentFilter intentFilter2=new IntentFilter();
        intentFilter2.addAction("xianshi");
        intentFilter1.addAction("yincang");
        receiver=new Receiver();
        receiver1=new Receiver1();
        registerReceiver(receiver1,intentFilter2);
        registerReceiver(receiver,intentFilter1);
        // 开始事务管理
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        drugStore=new StuDrugStoreFragment();
        drugStore.flag=true;
        transaction.add(R.id.docDrugMain,  drugStore);
        transaction.show(drugStore);
        transaction.commit();
        Log.d("kaiyao","yes");
        Log.d("docstore","create");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
    }
    public  class Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            drugStore.addNewDrug.setVisibility(View.INVISIBLE);
            Log.d("yincang","隐藏");
        }
    }
    public  class Receiver1 extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            drugStore.addNewDrug.setVisibility(View.VISIBLE);
            Log.d("xianshi","显示");
        }
    }
}

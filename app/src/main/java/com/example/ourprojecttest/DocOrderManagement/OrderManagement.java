package com.example.ourprojecttest.DocOrderManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.ImmersiveStatusbar;
import com.example.ourprojecttest.StuNeedToPay.ContentInfoBean;
import com.example.ourprojecttest.R;

import java.util.ArrayList;

public class OrderManagement extends AppCompatActivity {

    LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    RecyclerView recyclerView;
    OrderManagementAdapter adapter;
    Button needPost;
    Button alreadyPost;

    //接收适配器里传来的广播
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
    }

    private Bitmap Rfile2Bitmap(){
        return BitmapFactory.decodeResource(getResources(),R.drawable.test);
    }

    private void initView(){
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.OrderManagement");
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);
        recyclerView=findViewById(R.id.orderManageRecylerview);
        adapter=new OrderManagementAdapter(OrderManagement.this);

        ArrayList<OrderListBean>list=new ArrayList<>();
        OrderListBean bean=new OrderListBean();
        bean.setReceiverName("独孤晓峰");
        bean.setReceiverTelephone("12345678901");
        bean.setReceiverAddress("23号楼230宿舍");
        bean.setOrderTime("2020/2/10");

        ArrayList<ContentInfoBean> dataList=new ArrayList<>();
        ContentInfoBean data=new ContentInfoBean();

        data.setDrugAmount("25");
        data.setDrugName("999感冒灵");
        data.setDrugUnite("12");

        data.setDrugPicture(Rfile2Bitmap());
        dataList.add(data);
        dataList.add(data);
        dataList.add(data);
        bean.setDrugInfoBeans(dataList);
        list.add(bean);

        bean=new OrderListBean();
        bean.setOrderTime("1222/11/12");
        bean.setReceiverName("郭靖dsgks");
        bean.setReceiverTelephone("12220");
        bean.setReceiverAddress("23号楼230宿舍");
        data=new ContentInfoBean();
        data.setDrugAmount("25");
        data.setDrugName("999感冒灵");
        data.setDrugUnite("12");
        data.setDrugPicture(Rfile2Bitmap());
        dataList=new ArrayList<>();
        dataList.add(data);
        dataList.add(data);
        bean.setDrugInfoBeans(dataList);

        list.add(bean);
        adapter.setList(NeedToPostHelper.getDataAfterHandle(list));
        adapter.notifyDataSetChanged();
    LinearLayoutManager layoutManager=new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);
    needPost=findViewById(R.id.needPost);
    needPost.setBackgroundColor(Color.parseColor("#00BFFF"));
    alreadyPost=findViewById(R.id.alreadyPost);

    //设置代发货的点击事件
    needPost.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        needPost.setBackgroundColor(Color.parseColor("#00BFFF"));
        alreadyPost.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    });

    //设置已发货的点击事件
    alreadyPost.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("already","0000");
            needPost.setBackgroundColor(Color.parseColor("#FFFFFF"));
            alreadyPost.setBackgroundColor(Color.parseColor("#00BFFF"));
        }
    });
    }
}

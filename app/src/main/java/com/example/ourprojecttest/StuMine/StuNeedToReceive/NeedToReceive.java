package com.example.ourprojecttest.StuMine.StuNeedToReceive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.ourprojecttest.CommonMethod;
import com.example.ourprojecttest.ImmersiveStatusbar;
import com.example.ourprojecttest.StuMine.StuNeedToPay.ContentInfoBean;
import com.example.ourprojecttest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NeedToReceive extends AppCompatActivity {
    private final int RECEIVE_SUCCESS=2;
    private final int RECEIVE_FAILT=3;
    private CommonMethod method=new CommonMethod();
    private String id;
    private RecyclerView recyclerView;
    private NeedToReceiveAdapter adapter;
    private SwipeRefreshLayout refresh;
    private final int NO_ORDER=0;
    private final int HAVE_ORDER=1;
    LocalReceiver localReceiver;
    private IntentFilter intentFilter;

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
               String orderId=intent.getStringExtra("receive");
                receive(orderId);
        }
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case RECEIVE_SUCCESS:{
                    Toast.makeText(NeedToReceive.this, "收货成功", Toast.LENGTH_SHORT).show();
                    break;
                }
                case RECEIVE_FAILT:{
                    Toast.makeText(NeedToReceive.this, "收货失败", Toast.LENGTH_SHORT).show();
                    break;
                }
                case HAVE_ORDER:{
                    ArrayList<OrderListBean> orderList=(ArrayList<OrderListBean>)msg.obj;
                    adapter.setList(NeedToReceiveHelper.getDataAfterHandle(orderList));
                    adapter.notifyDataSetChanged();
                    refresh.setRefreshing(false);
                    break;
                }
                case NO_ORDER:{
                    refresh.setRefreshing(false);
                    Toast.makeText(NeedToReceive.this, "暂无代收货订单", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    private void receive(String orderId){
        final String url=getResources().getString(R.string.ipAdrress)+"IM/GetNeedToPayOrder?type=receive&id="+id+"&orderId="+orderId;
        Log.d("finish",url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string().trim();
                    Message msg=handler.obtainMessage();
                    if(responseData.equals("收货成功")){//如果数据库修改成功
                        msg.what=RECEIVE_SUCCESS;
                    }
                    else{
                        msg.what=RECEIVE_FAILT;
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_to_receive);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.RECEIVE_ORDER");
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);
    }
    private void getData(){
        refresh.setRefreshing(true);
        final String url=getResources().getString(R.string.ipAdrress)+"IM/GetNeedToPayOrder?type=stuNeedReceive&id="+id;
        Log.d("topay",url);
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
                    parseJSON(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSON(String data){
        Log.d("topay",data);
        ArrayList<OrderListBean> orderList=new ArrayList<>();
        OrderListBean orderBean=new OrderListBean();
        ArrayList<ContentInfoBean> drugDataList=new ArrayList<>();
        ContentInfoBean drugData;

        Message msg=handler.obtainMessage();
        try {
            JSONArray jsonArray=new JSONArray(data);
            if(jsonArray.length()==1){//说明当前用户没有待付款订单
                msg.what=NO_ORDER;
                handler.sendMessage(msg);
            }
            else{
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject object= null;
                    object = jsonArray.getJSONObject(i);
                    if(object.has("orderTime")){//如果是订单
                        orderBean.setOrderPrice(object.getString("orderPrice"));//设置订单总价格
                        orderBean.setOrderTime(object.getString("orderTime"));
                        orderBean.setDrugInfoBeans(drugDataList);//将药品信息的集合添加到订单中
                        drugDataList=new ArrayList<>();//在新建一个药品集合
                        orderList.add(orderBean);//将一个订单加入到订单集合中
                        orderBean=new OrderListBean();//在新建一个订单
                    }
                    else{//如果是药品
                        drugData=new ContentInfoBean();
                        drugData.setDrugAmount(object.getString("DrugAmount"));
                        drugData.setDrugName(object.getString("Drug_Name"));
                        drugData.setDrugUnite(object.getString("Drug_Price"));
                        final String imageUrl = object.getString("Drug_Index");
                        try {
                            drugData.setDrugPicture( Drawable.createFromStream(new URL(imageUrl).openStream(), "image.jpg"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        drugDataList.add(drugData);//将药品添加到药品列表里
                    }
                }
                msg.what=HAVE_ORDER;
                msg.obj=orderList;
                handler.sendMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initView(){
        id=method.getFileData("ID",this);
        refresh=findViewById(R.id.refresh);
        //设置下拉刷新过去数据
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        recyclerView=findViewById(R.id.needToReceiveRecycler);
        adapter=new NeedToReceiveAdapter();
        adapter.setContext(NeedToReceive.this);
        LinearLayoutManager manager=new LinearLayoutManager(NeedToReceive.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        getData();
    }

}

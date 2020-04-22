package com.example.ourprojecttest.DocMine.DocOrderManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
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

public class OrderManagement extends AppCompatActivity {
    private String ipAddress;
    private final int DELETE_SUCCESS=6;
    private final int DELETE_FAULT=7;
    private final int HAVE_POST=5;//获取已经发货的标记位
    private final int POST_SUCCESS=3;
    private final int POST_FAILT=4;
    private final int NO_DATA=1;
    private final int HAVE_DATA=2;
    private SwipeRefreshLayout refresh;
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private RecyclerView recyclerView;
    private OrderManagementAdapter adapter;
    private Button needPost;
    private Button alreadyPost;
    private String type="notPost";

    private Handler handler =new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DELETE_SUCCESS:{
                    Toast.makeText(OrderManagement.this, "订单删除成功", Toast.LENGTH_SHORT).show();
                    break;
                }
                case DELETE_FAULT:{
                    Toast.makeText(OrderManagement.this, "订单删除失败", Toast.LENGTH_SHORT).show();
                    break;
                }
                case POST_SUCCESS:{
                    Toast.makeText(OrderManagement.this, "发货成功", Toast.LENGTH_SHORT).show();
                    break;
                }
                case POST_FAILT:{
                    Toast.makeText(OrderManagement.this, "发货失败", Toast.LENGTH_SHORT).show();
                }
                case NO_DATA:{
                    adapter.dataList.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(OrderManagement.this, "暂无订单", Toast.LENGTH_SHORT).show();
                    refresh.setRefreshing(false);
                    break;
                }
                case HAVE_POST:{//展示已发货的
                    ArrayList<OrderListBean> orderList=(ArrayList<OrderListBean>)msg.obj;
                    adapter.setList(NeedToPostHelper.getDataAfterHandle(orderList),HAVE_POST);
                    adapter.notifyDataSetChanged();
                    refresh.setRefreshing(false);
                    break;
                }
                case HAVE_DATA:{//展示未发货的
                    ArrayList<OrderListBean> orderList=(ArrayList<OrderListBean>)msg.obj;
                    adapter.setList(NeedToPostHelper.getDataAfterHandle(orderList),HAVE_DATA);
                    adapter.notifyDataSetChanged();
                    refresh.setRefreshing(false);
                    break;
                }
            }
        }
    };

    //接收适配器里传来的广播
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果是点击已发货按钮
            if(intent.hasExtra("havePost")){
                String orderId=intent.getStringExtra("havePost");
                havePost(orderId,"havePost");
            }
            //如果是点击的删除订单按钮
            if(intent.hasExtra("delete")){
                String orderId=intent.getStringExtra("delete");
                havePost(orderId,"delete");
            }

        }
    }

private void havePost(String orderId,String type){
    final String url=getResources().getString(R.string.ipAdrress)+"IM/GetNeedToPayOrder?type="+type+"&orderId="+orderId;
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
                if(responseData.equals("发货成功")){//如果数据库修改成功
                    msg.what=POST_SUCCESS;
                }
                else if(responseData.equals("发货失败")){
                    msg.what=POST_FAILT;
                }
                else if(responseData.equals("删除成功")){
                    msg.what=DELETE_SUCCESS;
                }
                else if(responseData.equals("删除失败")){
                    msg.what=DELETE_FAULT;
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
        setContentView(R.layout.activity_order_management);
        ipAddress=getResources().getString(R.string.ipAdrress);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
    }

    private void getData(final String type){
        refresh.setRefreshing(true);
        final String url=getResources().getString(R.string.ipAdrress)+"IM/GetNeedToPayOrder?type="+type;
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
                    parseJSON(responseData,type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void parseJSON(String data,String type){
        ArrayList<OrderListBean> orderList=new ArrayList<>();
        OrderListBean orderBean=new OrderListBean();
        ArrayList<ContentInfoBean> drugDataList=new ArrayList<>();
        ContentInfoBean drugData;
        Message msg=handler.obtainMessage();
        try{
            JSONArray jsonArray=new JSONArray(data);
            Log.d("ordermanage","data:"+data);

            JSONObject object;
            if(jsonArray.length()==0){
                msg.what=NO_DATA;
                handler.sendMessage(msg);
            }
            else{
            for(int i=0;i<jsonArray.length();i++){
                     object=jsonArray.getJSONObject(i);
                     if(object.has("orderTime")){//如果是关于收货地址信息
                        if(i!=0){
                            orderBean.setDrugInfoBeans(drugDataList);//将药品信息的集合添加到订单中
                            drugDataList=new ArrayList<>();
                            orderList.add(orderBean);//将订单加入到订单集合中
                            orderBean=new OrderListBean();//在新建一个订单
                        }
                         orderBean.setOrderTime(object.getString("orderTime"));
                         Log.d("ordermanage","time2:"+object.getString("orderTime"));
                         orderBean.setReceiverName(object.getString("stuName"));
                         orderBean.setReceiverTelephone(object.getString("stuPhone"));
                         orderBean.setReceiverAddress(object.getString("stuAddress"));
                     }
                     else{//如果是关于药品信息
                        drugData=new ContentInfoBean();
                        drugData.setDrugAmount(object.getString("drugAmount"));
                        drugData.setDrugName(object.getString("drugName"));
                        drugData.setDrugUnite(object.getString("drugPrice"));
                        final String imageUrl=object.getString("drugPicture");
                        Log.d("topayimage",imageUrl);
                         try {
                             drugData.setDrugPicture(Drawable.createFromStream(new URL(ipAddress+imageUrl).openStream(),"image.jpg"));
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                         drugDataList.add(drugData);
                     }
            }
            orderBean.setDrugInfoBeans(drugDataList);
            orderList.add(orderBean);//将最后一次循环剩下的drugDataList加入到订单中
                if(type.equals("notPost")){//如果是获取未发货的
                    msg.what=HAVE_DATA;
                }
                else{//如果是获取已经发货的
                    msg.what=HAVE_POST;
                }
                msg.obj=orderList;
                handler.sendMessage(msg);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    private void initView(){
        refresh=findViewById(R.id.swipeRefresh);
        //设置下拉刷新过去数据
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(type);
            }
        });
        refresh.setColorSchemeColors(getResources().getColor(R.color.color_bottom));
        refresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.color_progressbar));
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.OrderManagement");
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);
        recyclerView=findViewById(R.id.orderManageRecylerview);
        adapter=new OrderManagementAdapter(OrderManagement.this);
    LinearLayoutManager layoutManager=new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);
    getData("notPost");
    needPost=findViewById(R.id.needPost);
    needPost.setBackgroundColor(Color.parseColor("#00BFFF"));
    alreadyPost=findViewById(R.id.alreadyPost);

    //设置代发货的点击事件
    needPost.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            type="notPost";
        needPost.setBackgroundColor(Color.parseColor("#00BFFF"));
        alreadyPost.setBackgroundColor(Color.parseColor("#FFFFFF"));
            getData("notPost");//获取代发货订单
        }
    });

    //设置已发货的点击事件
    alreadyPost.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("already","0000");
            type="getHavePost";
            needPost.setBackgroundColor(Color.parseColor("#FFFFFF"));
            alreadyPost.setBackgroundColor(Color.parseColor("#00BFFF"));
            getData("getHavePost");//获取已经发货的订单
        }
    });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
    }
}

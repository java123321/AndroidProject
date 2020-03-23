package com.example.ourprojecttest.StuMine.StuHistoryOrder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.StuMine.StuNeedToPay.ContentInfoBean;
import com.example.ourprojecttest.StuMine.StuNeedToReceive.NeedToReceiveHelper;
import com.example.ourprojecttest.StuMine.StuNeedToReceive.OrderListBean;
import com.example.ourprojecttest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryOrder extends AppCompatActivity {
    private String ipAddress;
    private final int DELETE_SUCCESS=2;
    private final int DELETE_FAULT=3;
    private final int SUCCESS=1;
    private final int FAULT=0;
    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;
    private HistoryOrderAdapter adapter;
    private String id;
    private CommonMethod method=new CommonMethod();
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DELETE_SUCCESS:{
                    Toast.makeText(HistoryOrder.this, "订单删除成功", Toast.LENGTH_SHORT).show();
                    break;
                }
                case DELETE_FAULT:{
                    Toast.makeText(HistoryOrder.this, "订单删除失败", Toast.LENGTH_SHORT).show();
                    break;
                }
                case SUCCESS:{
                    ArrayList<OrderListBean> orderList=(ArrayList<OrderListBean>)msg.obj;
                    adapter.setList(NeedToReceiveHelper.getDataAfterHandle( orderList));
                    adapter.notifyDataSetChanged();
                    refresh.setRefreshing(false);
                    writeOrderListIntoSDcard("stuHistoryOrder",orderList);
                    break;
                }
                case FAULT:{
                    Toast.makeText(HistoryOrder.this, "暂无历史订单", Toast.LENGTH_SHORT).show();
                    refresh.setRefreshing(false);
                    break;
                }
            }
        }
    };
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String orderId=intent.getStringExtra("delete");
            delete(orderId);
        }
    }
    private void delete(String orderId){
        final String url=ipAddress+"IM/GetNeedToPayOrder?type=stuDelete&orderId="+orderId;
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
                    if(responseData.equals("删除成功")){//如果数据库修改成功
                        msg.what=DELETE_SUCCESS;
                    }
                    else{
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
        setContentView(R.layout.activity_history_order);
        ipAddress=getResources().getString(R.string.ipAdrress);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明

        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.HISTORY_ORDER");
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);
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
                msg.what=FAULT;
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
                            drugData.setDrugPicture( Drawable.createFromStream(new URL(ipAddress+imageUrl).openStream(), "image.jpg"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        drugDataList.add(drugData);//将药品添加到药品列表里
                    }
                }
                msg.what=SUCCESS;
                msg.obj=orderList;
                handler.sendMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void getData(){
        refresh.setRefreshing(true);
        final String url=ipAddress+"IM/GetNeedToPayOrder?type=historyOrder&id="+id;
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

    private void initView(){
        id=method.getFileData("ID",this);
        Log.d("history","111");
        refresh=findViewById(R.id.refresh);
        //设置下拉刷新过去数据
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        recyclerView=findViewById(R.id.historyOrderRecyclerView);
        adapter=new HistoryOrderAdapter();
        adapter.setContext(HistoryOrder.this);
        LinearLayoutManager manager=new LinearLayoutManager(HistoryOrder.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        //如果本地缓存订单数据不为空，则先显示出来
        ArrayList<OrderListBean> orderList=readOrderListFromSdCard("stuHistoryOrder");
        if(orderList!=null){
            adapter.setList(NeedToReceiveHelper.getDataAfterHandle(orderList));
            adapter.notifyDataSetChanged();
        }
        getData();
        Log.d("history","000");
    }

    /**
     * 该方法用于将订单对象数组集合写入sd卡
     *
     * @param fileName 文件名
     * @param list     集合
     * @return true 保存成功
     */
    public boolean writeOrderListIntoSDcard(String fileName, ArrayList<OrderListBean> list) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();//获取sd卡目录
            File sdFile = new File(sdCardDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(sdFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(list);//写入
                fos.close();
                oos.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 读取sd卡中的订单数组对象
     *
     * @param fileName 文件名
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList<OrderListBean> readOrderListFromSdCard(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  //检测sd卡是否存在
            ArrayList<OrderListBean> list;
            File sdCardDir = Environment.getExternalStorageDirectory();
            File sdFile = new File(sdCardDir, fileName);
            try {
                FileInputStream fis = new FileInputStream(sdFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                list = (ArrayList<OrderListBean>) ois.readObject();
                fis.close();
                ois.close();
                return list;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
                return null;
            } catch (OptionalDataException e) {
                e.printStackTrace();
                return null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
}

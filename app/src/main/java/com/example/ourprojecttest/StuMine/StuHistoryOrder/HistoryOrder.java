package com.example.ourprojecttest.StuMine.StuHistoryOrder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.example.ourprojecttest.ImmersiveStatusbar;
import com.example.ourprojecttest.StuMine.StuNeedToPay.ContentInfoBean;
import com.example.ourprojecttest.StuMine.StuNeedToReceive.NeedToReceiveHelper;
import com.example.ourprojecttest.StuMine.StuNeedToReceive.OrderListBean;
import com.example.ourprojecttest.R;

import java.util.ArrayList;

public class HistoryOrder extends AppCompatActivity {
    RecyclerView recyclerView;
    HistoryOrderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
    }

    private Bitmap Rfile2Bitmap(){
        return BitmapFactory.decodeResource(getResources(),R.drawable.test);
    }
    private void initView(){
        Log.d("history","111");
        recyclerView=findViewById(R.id.historyOrderRecyclerView);
        adapter=new HistoryOrderAdapter();
        adapter.setContext(HistoryOrder.this);
        LinearLayoutManager manager=new LinearLayoutManager(HistoryOrder.this);
        recyclerView.setLayoutManager(manager);

        //创建模拟订单数据
        ArrayList<OrderListBean> orderListBeans=new ArrayList<>();
        OrderListBean bean=new OrderListBean();
        bean.setOrderTime("1222/12/22");

        ArrayList<ContentInfoBean> dataList=new ArrayList<>();
        ContentInfoBean data=new ContentInfoBean();
        data.setDrugAmount("25");
        data.setDrugName("999感冒灵");
        data.setDrugUnite("12");


        dataList.add(data);
        dataList.add(data);
        dataList.add(data);
        bean.setDrugInfoBeans(dataList);
        orderListBeans.add(bean);

        bean=new OrderListBean();
        bean.setOrderTime("2222/12/22");
        data=new ContentInfoBean();
        data.setDrugAmount("25");
        data.setDrugName("999感冒灵");
        data.setDrugUnite("12");

        dataList=new ArrayList<>();
        dataList.add(data);
        dataList.add(data);
        bean.setDrugInfoBeans(dataList);
        orderListBeans.add(bean);
        adapter.setList(NeedToReceiveHelper.getDataAfterHandle(orderListBeans));
        recyclerView.setAdapter(adapter);
        Log.d("history","000");
    }

}

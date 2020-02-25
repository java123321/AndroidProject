package com.example.ourprojecttest.StuMine.StuNeedToReceive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.ourprojecttest.ImmersiveStatusbar;
import com.example.ourprojecttest.StuMine.StuNeedToPay.ContentInfoBean;
import com.example.ourprojecttest.R;

import java.util.ArrayList;

public class NeedToReceive extends AppCompatActivity {

    private Bitmap Rfile2Bitmap(){
    return BitmapFactory.decodeResource(getResources(),R.drawable.test);
}

    RecyclerView recyclerView;
    NeedToReceiveAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_to_receive);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
    }

    private void initView(){
        recyclerView=findViewById(R.id.needToReceiveRecycler);
        adapter=new NeedToReceiveAdapter();
        adapter.setContext(NeedToReceive.this);
        LinearLayoutManager manager=new LinearLayoutManager(NeedToReceive.this);
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

        data.setDrugPicture(Rfile2Bitmap());
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
        data.setDrugPicture(Rfile2Bitmap());
        dataList=new ArrayList<>();
        dataList.add(data);
        dataList.add(data);
        bean.setDrugInfoBeans(dataList);
        orderListBeans.add(bean);

        adapter.setList(NeedToReceiveHelper.getDataAfterHandle(orderListBeans));
        recyclerView.setAdapter(adapter);
    }

}

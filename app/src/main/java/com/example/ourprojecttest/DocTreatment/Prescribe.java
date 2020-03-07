package com.example.ourprojecttest.DocTreatment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ourprojecttest.DocBottomNavigation;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.StuMine.ShoppingCart.ShoppingCartBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Prescribe extends AppCompatActivity {
    Intent intentToDocBottom=new Intent("com.example.ourprojecttest.DocDrugStore");
    private ArrayList<PrescribeBean>list=new ArrayList<>();
    private Button prescribe;
    private TextView showPrice;
    private RecyclerView recycler;
    private PrescribeAdapter adapter;
    private TextView addDrug;
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private Set<String> set=new HashSet<>();
    private DecimalFormat df = new DecimalFormat("##0.00");
    private double totalPrice=0.00;

    //该接收器可以接受医生从药店里给学生选的药品
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果是添加药品发过来的通知
            Log.d("pres","pass");
            if(intent.hasExtra("drugId")){
                Log.d("pres","pass1");
                PrescribeBean bean=new PrescribeBean();
                String id=intent.getStringExtra("drugId");
                String price=intent.getStringExtra("drugPrice");
                //将新加的药品价格统计到价格总数中
                totalPrice+=Double.valueOf(price);

                //如果是添加的同一个药品
                if(set.contains(id)){
                    intentToDocBottom.putExtra("msg","repeat");
                    sendBroadcast(intentToDocBottom);
                }
                else{
                    //将药品id加入到set中用来记录某药品是否已经添加到药方中
                    set.add(id);
                    bean.setId(id);
                    bean.setDrugName(intent.getStringExtra("drugName"));
                    bean.setDrugPrice(price);
                    byte[]pic=intent.getByteArrayExtra("drugPicture");
                    bean.setDrugPicture(BitmapFactory.decodeByteArray(pic,0,pic.length));
                    list.add(bean);
                    Log.d("pres","pas25");
                    intentToDocBottom.putExtra("msg","success");
                    sendBroadcast(intentToDocBottom);
                }
            }
            else if(intent.hasExtra("sub")){//如果是修改药品数量发过来的减少药品价格
                Log.d("pres","pass2");
                Double sub=Double.valueOf(intent.getStringExtra("sub"));
                totalPrice-=sub;
                showPrice.setText("订单总价:￥"+df.format(totalPrice));
            }
            else{//如果是修改药品数量发过来的增加药品价格
                Log.d("pres","pass3");
                Double add=Double.valueOf(intent.getStringExtra("add"));
                totalPrice+=add;
                showPrice.setText("订单总价:￥"+df.format(totalPrice));
            }
        }
    }

    //当返回到医生返回到开药界面时更新RecyclerView
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("pres","length"+list.size());
        adapter.setList(list);
        adapter.notifyDataSetChanged();
        showPrice.setText("订单总价:￥"+df.format(totalPrice));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescribe);
        //开始注册广播监听器
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.Perscribe");
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);
        Log.d("cribe","register");
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
        Log.d("cribe","unregister");
    }

    private void initView(){
        addDrug=findViewById(R.id.addDrug);
        prescribe=findViewById(R.id.prescribe);
        showPrice=findViewById(R.id.showPrice);
        recycler=findViewById(R.id.recycler);
        adapter=new PrescribeAdapter(Prescribe.this);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        //设置添加药品的点击事件
        addDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent=new Intent(Prescribe.this, DocDrugStore.class);
            startActivity(intent);
            }
        });
    }
}

package com.example.ourprojecttest.StuMine.ShoppingCart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ourprojecttest.CommonMethod;
import com.example.ourprojecttest.Drug;
import com.example.ourprojecttest.ImmersiveStatusbar;
import com.example.ourprojecttest.R;
import com.google.gson.Gson;


import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ShoppingCartActivity extends AppCompatActivity {
    LinearLayout hideCart;
    ArrayList<ShoppingCartBean> deleteList;
    LocalReceiver localReceiver;
    IntentFilter intentFilter;
    private TextView empty;
    private Button buyNow;
    private Button bianji;
    private TextView payPrice;
    private RecyclerView mRecycler;
    private ShoppingCartAdapter mAdapter;
    private ArrayList<ShoppingCartBean> lists;
    private LinearLayout quanxuanWrap;
    private ImageView quanxuan;
    private String showText = "";
    ArrayList<Drug> drugs = new ArrayList<>();
    CommonMethod method=new CommonMethod();
    DecimalFormat df = new DecimalFormat("##0.00");

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    //成功
                    new AlertDialog.Builder(ShoppingCartActivity.this).setTitle("正确").setMessage("成功").setNegativeButton("确定",null).show();
                    break;
                case -1:
                    //失败
                    new AlertDialog.Builder(ShoppingCartActivity.this).setTitle("错误").setMessage("失败").setNegativeButton("确定",null).show();
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        //开始注册广播监听器
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.UPDATE_DATA");
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);

    }

    //解除广播注册
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
        Log.d("shopcart","destroy");
    }

    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
          update(Double.valueOf(intent.getStringExtra("value")));
        }
    }

public  void update(Double d){
    payPrice.setText("合计:￥ "+String.valueOf(df.format(d)));
}

    private void initView(){
        hideCart=findViewById(R.id.stu_shopping_cart_hide_cart);
        empty=findViewById(R.id.empty);
        buyNow=findViewById(R.id.stu_shopping_cart_buy_now);
        bianji=findViewById(R.id.stu_shopping_cart_bianji);
        payPrice=findViewById(R.id.stu_shopping_cart_pay_price);
        quanxuanWrap=findViewById(R.id.stu_shopping_cart_quanxuan_wrap);
        quanxuan=findViewById(R.id.stu_shopping_cart_quanxuan);
        mRecycler =findViewById(R.id.stu_shopping_cart_recyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        mAdapter = new ShoppingCartAdapter(this);
        mRecycler.setAdapter(mAdapter);
            lists=method.readListFromSdCard("ShoppingCartList");
        //当购物车内容是空的情况下
        if(lists==null||lists.size()==0){
            Log.d("cart","null");
            hideCart.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }else{//不为空的情况下
            Log.d("cart","notnull");
            empty.setVisibility(View.GONE);
            hideCart.setVisibility(View.VISIBLE);
            mAdapter.setList(lists);
            mAdapter.notifyDataSetChanged();
        }
        buyNow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //立即购买的点击事件
                if(buyNow.getText().toString().trim().equals("去结算")){
                        deleteList=mAdapter.getList();
                        for(int i=deleteList.size()-1;i>=0;i--){
                            if(deleteList.get(i).getChecked().equals("true")){
                                Drug drug = new Drug();
                                drug.setId(deleteList.get(i).getId());
                                drug.setNum(String.valueOf(deleteList.get(i).getTotalPrice()/Double.valueOf(deleteList.get(i).getDrugPrice())));
                                drugs.add(drug);
                                deleteList.remove(i);
                            }
                        }
                        mAdapter.setList(deleteList);
                        mAdapter.notifyDataSetChanged();
                        method.writeListIntoSDcard("ShoppingCartList",deleteList);

                        Gson gson = new Gson();
                        final String jsonStr = gson.toJson(drugs);
                        System.out.println(jsonStr);

                        final String conversion = method.conversion(jsonStr);
                        Log.d("json",jsonStr);
                }
                else{//清除商品的点击事件
                    deleteList=mAdapter.getList();
                    for(int i=deleteList.size()-1;i>=0;i--){
                        if(deleteList.get(i).getChecked().equals("true")){
                            deleteList.remove(i);
                        }
                    }
                    mAdapter.setList(deleteList);
                    mAdapter.notifyDataSetChanged();
                    //将删除后的数组写入到本地
                       method.writeListIntoSDcard("ShoppingCartList",deleteList);
                }

            }
        });


        //设置编辑的点击事件
        bianji.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                //如果本来是管理
                if(bianji.getText().toString().equals("管理")){
                    bianji.setText("完成");
                    buyNow.setText("删除");
                    payPrice.setVisibility(View.INVISIBLE);
                }
                else{//如果本来是完成
                    bianji.setText("管理");
                    buyNow.setText("去结算");
                    payPrice.setVisibility(View.VISIBLE);
                }
            }
        });



        //设置全选的点击事件
        quanxuanWrap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (quanxuan.getDrawable().getCurrent().getConstantState().equals(getResources().getDrawable(R.drawable.unchecked).getConstantState())){
//当image1的src为R.drawable.A时，设置image1的src为R.drawable.B
                    quanxuan.setImageResource(R.drawable.checked);
                    for(ShoppingCartBean list:lists){
                        list.setChecked("true");
                    }
                }else{
//否则设置image1的src为R.drawable.A
                    quanxuan.setImageResource(R.drawable.unchecked);
                    for(ShoppingCartBean list:lists){
                        list.setChecked("false");
                    }
                }
                mAdapter.setList(lists);
                mAdapter.notifyDataSetChanged();
                update(method.calculatePrice(lists,lists.size()));
            }
        });
    }


    private void parseJSONWithJSONObject(String jsonData){
        try{

            JSONObject jsonObject=new JSONObject(jsonData);
            String code=jsonObject.getString("code");
            Message msg = Message.obtain();

            if(code.equals("200")){
                msg.what = 0;

            }
            else{
                msg.what =-1;
            }
            handler.sendMessage(msg);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

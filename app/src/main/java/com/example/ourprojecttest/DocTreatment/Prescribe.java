package com.example.ourprojecttest.DocTreatment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourprojecttest.R;
import com.example.ourprojecttest.StuDrugStore.UpDrugMsgActivity;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Prescribe extends AppCompatActivity {
    private Display display;
    private int toastHeight;
    private LinearLayout empty;
    private String ipAddress;
    private final int SUCCESS = 1;
    private final int FAULT = 0;
    private final int NO_DRUG=2;
    private Intent intentToDocBottom = new Intent("com.example.ourprojecttest.DocDrugStore");
    private ArrayList<PrescribeBean> list = new ArrayList<>();
    private Button prescribe;
    private TextView showPrice;
    private RecyclerView recycler;
    private PrescribeAdapter adapter;
    private TextView addDrug;
    private Button addDrugToo;
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private String stuId;
    private Set<String> set = new HashSet<>();
    private DecimalFormat df = new DecimalFormat("##0.00");
    private double totalPrice = 0.00;
    private String orderPrice = "0.00";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:{
                    String s="添加订单成功！";
                    show(R.layout.layout_chenggong,s);
                    break;
                }
                case FAULT:{
                    Toast toast = Toast.makeText(Prescribe.this, "订单添加失败！", Toast.LENGTH_SHORT);
                    // 这里给了一个1/4屏幕高度的y轴偏移量
                    toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                    toast.show();
                    break;
                }
                case NO_DRUG:{
                    Toast toast = Toast.makeText(Prescribe.this, "当前处方为空，请先添加药品！", Toast.LENGTH_SHORT);
                    // 这里给了一个1/4屏幕高度的y轴偏移量
                    toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                    toast.show();
                    break;
                }
            }
        }
    };
    //该接收器可以接受医生从药店里给学生选的药品
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果是添加药品发过来的通知
            Log.d("pres", "pass");
            if (intent.hasExtra("drugId")) {
                Log.d("pres", "pass1");
                PrescribeBean bean = new PrescribeBean();
                String id = intent.getStringExtra("drugId");
                String price = intent.getStringExtra("drugPrice");
                //将新加的药品价格统计到价格总数中
                totalPrice += Double.valueOf(price);

                //如果是添加的同一个药品
                if (set.contains(id)) {
                    intentToDocBottom.putExtra("msg", "repeat");
                    sendBroadcast(intentToDocBottom);
                } else {
                    //将药品id加入到set中用来记录某药品是否已经添加到药方中
                    set.add(id);
                    bean.setId(id);
                    bean.setDrugName(intent.getStringExtra("drugName"));
                    bean.setDrugAmount(1);
                    bean.setDrugPrice(price);
                    byte[] pic = intent.getByteArrayExtra("drugPicture");
                    bean.setDrugPicture(BitmapFactory.decodeByteArray(pic, 0, pic.length));
                    list.add(bean);
                    Log.d("pres", "pas25");
                    intentToDocBottom.putExtra("msg", "success");
                    sendBroadcast(intentToDocBottom);
                }
            } else if (intent.hasExtra("sub")) {//如果是修改药品数量发过来的减少药品价格
                Log.d("pres", "pass2");
                Double sub = Double.valueOf(intent.getStringExtra("sub"));
                totalPrice -= sub;
                updateDisplayPrice(totalPrice);
            } else {//如果是修改药品数量发过来的增加药品价格
                Log.d("pres", "pass3");
                Double add = Double.valueOf(intent.getStringExtra("add"));
                totalPrice += add;
                updateDisplayPrice(totalPrice);
            }
        }
    }

    //显示订单总价格
    private void updateDisplayPrice(Double d) {
        orderPrice = df.format(d);
        String str = "合计:￥" + orderPrice;
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FF1493"));
        builder.setSpan(colorSpan, 3, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        showPrice.setText(builder);
    }

    //当返回到医生返回到开药界面时更新RecyclerView
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("pres", "length" + list.size());
        if(list.size()!=0){
            //当医生开处方的内容不为空的时候显示recycleview
            empty.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
            adapter.setList(list);
            adapter.notifyDataSetChanged();
            updateDisplayPrice(totalPrice);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescribe);
        ipAddress = getResources().getString(R.string.ipAdrress);
        //开始注册广播监听器
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.Perscribe");
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        localReceiver = new LocalReceiver();
        registerReceiver(localReceiver, intentFilter);
        Log.d("cribe", "register" );
        initView();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
        Intent intent=new Intent();
        intent.setAction("xianshi");
        sendBroadcast(intent);
        Log.d("cribe", "unregister");
    }

    private void initView() {
        display = getWindowManager().getDefaultDisplay();
        toastHeight = display.getHeight();
        empty = findViewById(R.id.empty);
        //默认医生没有开处方
        empty.setVisibility(View.VISIBLE);

        //获取开药学生的id
        stuId = getIntent().getStringExtra("stuId");
        addDrug = findViewById(R.id.addDrug);
        addDrugToo=findViewById(R.id.addOrderDrug);
        prescribe = findViewById(R.id.prescribe);
        showPrice = findViewById(R.id.showPrice);
        updateDisplayPrice(0.00);
        recycler = findViewById(R.id.recycler);
        recycler.setVisibility(View.GONE);
        adapter = new PrescribeAdapter(Prescribe.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        //设置添加药品的点击事件
        addDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Prescribe.this, DocDrugStore.class);
                startActivity(intent);
            }
        });
        //设置界面为空的时候的添加药品按钮的事件
        addDrugToo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Prescribe.this, DocDrugStore.class);
                startActivity(intent);
            }
        });
        //设置开处方按钮的点击事件
        prescribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //如果当前处方药品数量为空，则弹出提示
                        if(empty.getVisibility()==View.VISIBLE){
                            Message msg=Message.obtain();
                            msg.what=NO_DRUG;
                            handler.sendMessage(msg);
                        }else{
                            JSONArray jsonArray = new JSONArray();
                            try {
                                JSONObject object = new JSONObject();
                                object.put("stuId", stuId);
                                jsonArray.put(object);

                                for (int i = 0; i < list.size(); i++) {
                                    object = new JSONObject();
                                    PrescribeBean bean = list.get(i);
                                    object.put("drugId", bean.getId());
                                    object.put("drugAmount", bean.getDrugAmount());
                                    jsonArray.put(object);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append("type=notPost&order=")//notPost是学生待付款的订单
                                    .append(jsonArray.toString());
                            Log.d("result", "arrayis:" + jsonArray.toString());
                            byte[] data = stringBuffer.toString().getBytes();

                            String strUrl = ipAddress + "IM/UploadOrder";
                            try {
                                URL url = new URL(strUrl);
                                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                urlConnection.setConnectTimeout(3000);//设置连接超时时间
                                urlConnection.setDoInput(true);//设置输入流采用字节流
                                urlConnection.setDoOutput(true);//设置输出采用字节流
                                urlConnection.setRequestMethod("POST");
                                urlConnection.setUseCaches(false);//使用post方式不能使用缓存
                                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//设置meta参数
                                urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
                                urlConnection.setRequestProperty("Charset", "utf-8");
                                //获得输出流，向服务器写入数据
                                OutputStream outputStream = urlConnection.getOutputStream();
                                outputStream.write(data);
                                int response = urlConnection.getResponseCode();//获得服务器的响应码
                                Message msg = handler.obtainMessage();
                                if (response == HttpURLConnection.HTTP_OK) {

                                    InputStream inputStream = urlConnection.getInputStream();
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    byte[] result = new byte[1024];
                                    int len = 0;
                                    while ((len = inputStream.read(result)) != -1) {
                                        byteArrayOutputStream.write(result, 0, len);
                                    }
                                    String resultData = new String(byteArrayOutputStream.toByteArray()).trim();

                                    if (resultData.equals("订单添加成功")) {
                                        msg.what = SUCCESS;
                                        Log.d("result", "success3");
                                    } else {
                                        msg.what = FAULT;
                                        Log.d("result", "fault1");
                                    }
                                } else {
                                    msg.what = FAULT;
                                    Log.d("result", "fault2");
                                }
                                handler.sendMessage(msg);
                                Log.d("result", "312");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();

            }
        });
    }
    public void show(int x, String s) {
        final Dialog dialog = new Dialog(Prescribe.this, R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(Prescribe.this).inflate(x, null);
        TextView describe = inflate.findViewById(R.id.describe);
        describe.setText(s);
        TextView yes = inflate.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    finish();
            }
        });
        dialog.setContentView(inflate);
        dialog.setCancelable(false);
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }
}

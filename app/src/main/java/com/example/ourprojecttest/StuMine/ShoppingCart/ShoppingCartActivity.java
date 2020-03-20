package com.example.ourprojecttest.StuMine.ShoppingCart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.example.ourprojecttest.AuthResult;
import com.example.ourprojecttest.CommonMethod;
import com.example.ourprojecttest.ImmersiveStatusbar;
import com.example.ourprojecttest.OrderInfoUtil2_0;
import com.example.ourprojecttest.PayResult;
import com.example.ourprojecttest.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


public class ShoppingCartActivity extends AppCompatActivity {
    private String orderPrice = "0.00";
    private LinearLayout hideCart;
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private TextView empty;
    private Button buyNow;
    private Button bianji;
    private TextView payPrice;
    private RecyclerView mRecycler;
    private ShoppingCartAdapter mAdapter;
    private ArrayList<ShoppingCartBean> lists;
    private LinearLayout selectAllButton;
    private ImageView selectAll;
    private boolean selectAllFlag = false;
    private String stuId;
    private CommonMethod method = new CommonMethod();
    private DecimalFormat df = new DecimalFormat("##0.00");
    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    private final int SUCCESS = 3;
    private final int FAULT = 4;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private static final String APPID = "2016101700706177";
    private static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDJ1ys/sxQE6Dem09YM4AeabOd04OU2jC+fRTrcZFHNrRdL2FoglSe0G9To5rMsWLUp6yTUWK4aYPRbUUVdzqeYYzcBbg9wzph1aEDM0EvAfL+EMVk2vOXd+ALKCiiMoT5SwC94yU9G+BcuMIywc2kqXZTnaJpKaeGlVm/IUyiePZ31FWP2dMzLh1IYq9OP8udk7Xn/gDvpQz+00IMOugudCxa/F9VvwqRQaUJE8QnxSVSYxsVfobZ1aY2ddt+tUxbirFYaHadfJAwTOK9BrerXULk8S2RQPM7PVJdi8QNxwHssYgZ5oSYQsjGvpJBT/1boJttWkP7vKU++rTWsE/rjAgMBAAECggEAFXggro7i0z7MJJ+lxgrSZDevSvxdBTdWHW/kueql1OXTc4rY01xqM7s+I2PerRnmc0YCzd987WtgspHrefXwV8I96JYHaG1hRCPJuL9zP09Fo88H+U2QedLWoR0BgSvpkC3HHuby2s6q0IvzexVbm1kG7LJwrveiO8785ucJjAM7ZO6rMR7FGoPHn8YMmZL0KLqx9GIKBYqIDK7kaghVY5b+rEpG9DeC8DYNGUBBx1CGBKNvTfj2xaJjzN1BPz4OI8++Z5LI58AbYVXGRfT9WsSfL2mIpxD7RTxuIjwhaLzBQk7l2ex0osplrQOy6BoKEja/bfRawf8Pc86Bo1+e8QKBgQDyCz6fYAZp/WyJT/VMMSS7FlLz6/ixIfrkVJwkbj1jtpe2HoMCkOjIudaTYuxA3fg0qZsD2cY7/OryRZEEGYe2TRCWLIS0G/p67D9erY3Dty7+5xwLIDbBoYMEfk9i3UaUGzrtgvEUvQDrIGl4pid+ftklTU191d+KzokuiOL6yQKBgQDVen2ioEZGjeEfiqAdGy1MAtkmJsXY3kBUWo0gfk56lgx35kjVbEsIu0wD/zzTFHsf0XHmakSgmsBvq3ElaQAZKXY11fotOq3EXYoX7IvaA9GZR8EjeccrGcdQePK52cR9AcvoM4cG421kscYhfdjw0r5a+QDJwwf7HDXlWy9ySwKBgQChWp+njVMZSykUrKoA3e33jl1EYHWMV/OyTTk+DAN+upWOge6iQkn8re5+mH6Yi6DQMpS1T3MYQHW7hmazDfXrsJozEoBwtQoY8e8Yxafw5eg9Y4HNZO87y9jUoQN5C7vmNfTlqtneElVPaW8GT/WaHSPS+yKClZYNKbxHuldeCQKBgCg6gv5ocZXOGsRU3UNe4bRXPRCBcfsiNsEupzWeV6+mIwddMBB37dPhZ7vBF3c3ftRKJcqj7/bL8sOYbSP9m3UiaRJQFmr7ic9dSS6k9t3IpnDaIr1Kr4uhufuiLytyrCJaelBxlVpo9S5qicm5623GaPS/w7RBunlJoaZs/o3tAoGABx7XniHaLOWr1yOd35AZsR7OMVNcAug18wXb2nxWjgaLSZ9C9mxnmvPdFKGaDY48Qeud3HfpJb76J4cx1lXg4seJu8T/P5xAKNf9GgfR26mUyZLeJBqklwLwlABXZj/ZSfrrz7vbCEETPxfgMTvAebnYW5DvkTT0Sk/hR/Zi5cM=";
    private static final String RSA_PRIVATE = "";

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS: {
                    Toast.makeText(ShoppingCartActivity.this, "订单添加成功", Toast.LENGTH_SHORT).show();
                    break;
                }
                case FAULT: {
                    Toast.makeText(ShoppingCartActivity.this, "订单添加失败", Toast.LENGTH_SHORT).show();
                    break;
                }
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     * ——————就是指商户根据后台接收的支付宝异步通知的支付结果为准{}
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        //用户支付成功之后，开始将订单上传到数据库
                        uploadOrder();
                        Log.d("msp", "2");
                    } else {

                        Log.d("msp", "3");
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();
                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(ShoppingCartActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(ShoppingCartActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//沙箱环境需要的代码
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        //开始注册广播监听器
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.UPDATE_DATA");
        localReceiver = new LocalReceiver();
        registerReceiver(localReceiver, intentFilter);

    }

    //解除广播注册
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
        //退出购物车活动时，保存当前的购物车信息（药品数量）
        method.writeListIntoSDcard("ShoppingCartList", mAdapter.getList());
        Log.d("shopcart", "destroy");
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            update(Double.valueOf(intent.getStringExtra("value")));
            Log.d("selectall", "1");
            boolean all = intent.getBooleanExtra("selectAll", false);
            Log.d("selectall", "2");
            Log.d("selectall", "all:" + all);
            //如果有全选的消息
            if (all) {
                selectAll.setImageResource(R.drawable.checked);
                selectAllFlag = true;
                Log.d("selectall", "true");
            } else if (selectAllFlag) {//如果没有发送全选通知且当前全选选中的时候将它置位未选中
                selectAll.setImageResource(R.drawable.unchecked);
                selectAllFlag = false;
                Log.d("selectall", "false");
            }
        }
    }

    public void update(Double d) {
        orderPrice = df.format(d);
        String str = "合计:￥" + orderPrice;
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FF1493"));
        builder.setSpan(colorSpan, 3, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        payPrice.setText(builder);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton("Confirm", null)
                .setOnDismissListener(onDismiss)
                .show();
    }

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    /**
     * 支付宝支付业务示例
     */
    public void payV2(String amount) {
        Log.d("amount", amount);

        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            showAlert(this, "Error: Missing APPID or RSA_PRIVATE in PayDemoActivity.");
            return;
        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         *
         * url—是通知给支付宝回调的接口
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2, "", amount);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                Log.d("msp", "-10");
                PayTask alipay = new PayTask(ShoppingCartActivity.this);
                Log.d("msp", "-9");
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.d("msp", result.toString());
                Log.d("msp", "1");
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void uploadOrder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //先获取购物车中的药品数组
                    ArrayList<ShoppingCartBean> drugList = mAdapter.getList();
                    Set<String> set = (Set<String>) method.readObjFromSDCard("drugIdSet");

                    JSONArray jsonArray = new JSONArray();
                    JSONObject object = new JSONObject();
                    object.put("stuId", stuId);
                    jsonArray.put(object);
                    for (int i = drugList.size() - 1; i >= 0; i--) {
                        ShoppingCartBean bean = drugList.get(i);
                        if (bean.getChecked().equals("true")) {
                            object = new JSONObject();
                            object.put("drugId", bean.getId());
                            object.put("drugAmount", bean.getDrugAmount());
                            jsonArray.put(object);
                            drugList.remove(i);
                            set.remove(bean.getId());
                        }
                    }

                    //构造post数据并上传服务器
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("type=finishPay&order=")//finishPay 代表学生刚刚付完钱
                            .append(jsonArray.toString());
                    byte[] data = stringBuffer.toString().getBytes();
                    String strUrl = getResources().getString(R.string.ipAdrress) + "IM/UploadOrder";
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
                    Message msg = mHandler.obtainMessage();
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
                    mHandler.sendMessage(msg);
                    Log.d("result", "312");
                } catch (JSONException | MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void initView() {
        //获取学生的id
        stuId = method.getFileData("ID",ShoppingCartActivity.this);
        hideCart = findViewById(R.id.stu_shopping_cart_hide_cart);
        empty = findViewById(R.id.empty);
        buyNow = findViewById(R.id.stu_shopping_cart_buy_now);
        bianji = findViewById(R.id.stu_shopping_cart_bianji);
        payPrice = findViewById(R.id.stu_shopping_cart_pay_price);
        selectAllButton = findViewById(R.id.stu_shopping_cart_quanxuan_wrap);
        selectAll = findViewById(R.id.stu_shopping_cart_quanxuan);
        mRecycler = findViewById(R.id.stu_shopping_cart_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        mAdapter = new ShoppingCartAdapter(this);
        mRecycler.setAdapter(mAdapter);
        lists = method.readListFromSdCard("ShoppingCartList");
        //当购物车内容是空的情况下
        if (lists == null || lists.size() == 0) {
            Log.d("cart", "null");
            hideCart.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {//不为空的情况下
            Log.d("cart", "notnull");
            empty.setVisibility(View.GONE);
            hideCart.setVisibility(View.VISIBLE);
            mAdapter.setList(lists);
            mAdapter.notifyDataSetChanged();
            //统计药品总价格
            Double[] info = method.calculatePrice(lists, lists.size());
            update(info[0]);
            //判断是否全选
            if (info[1] == 1.0) {
                selectAll.setImageResource(R.drawable.checked);
                selectAllFlag = true;
            } else {
                selectAll.setImageResource(R.drawable.unchecked);
            }
        }
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //立即购买的点击事件
                if (buyNow.getText().toString().trim().equals("去结算")) {
                    //去付款
                    payV2(orderPrice);
                } else {//清除商品的点击事件
                    //先获取购物车中的药品数组
                    ArrayList<ShoppingCartBean> deleteList = mAdapter.getList();
                    Set<String> set = (Set<String>) method.readObjFromSDCard("drugIdSet");
                    for (int i = deleteList.size() - 1; i >= 0; i--) {
                        ShoppingCartBean bean = deleteList.get(i);
                        if (bean.getChecked().equals("true")) {
                            deleteList.remove(i);
                            set.remove(bean.getId());
                        }
                    }
                    mAdapter.setList(deleteList);
                    mAdapter.notifyDataSetChanged();
                    method.writeListIntoSDcard("ShoppingCartList", deleteList);
                    method.saveObj2SDCard("drugIdSet", set);
                }

            }
        });

        //设置编辑的点击事件
        bianji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果本来是管理
                if (bianji.getText().toString().equals("管理")) {
                    bianji.setText("完成");
                    buyNow.setText("删除");
                    payPrice.setVisibility(View.INVISIBLE);
                } else {//如果本来是完成
                    bianji.setText("管理");
                    buyNow.setText("去结算");
                    payPrice.setVisibility(View.VISIBLE);
                }
            }
        });

        //设置全选的点击事件
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //当全选按钮没有选中的时候
                if (selectAllFlag == false) {
                    selectAllFlag=true;
                    selectAll.setImageResource(R.drawable.checked);
                    for (ShoppingCartBean list : lists) {
                        list.setChecked("true");
                    }
                } else {
                    selectAllFlag=false;
                    selectAll.setImageResource(R.drawable.unchecked);
                    for (ShoppingCartBean list : lists) {
                        list.setChecked("false");
                    }
                }
                mAdapter.setList(lists);
                mAdapter.notifyDataSetChanged();
                update(method.calculatePrice(lists, lists.size())[0]);
            }
        });
    }


}

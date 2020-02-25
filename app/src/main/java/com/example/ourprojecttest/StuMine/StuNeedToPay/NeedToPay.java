package com.example.ourprojecttest.StuMine.StuNeedToPay;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.example.ourprojecttest.AuthResult;
import com.example.ourprojecttest.CommonMethod;
import com.example.ourprojecttest.ImmersiveStatusbar;
import com.example.ourprojecttest.OrderInfoUtil2_0;
import com.example.ourprojecttest.PayResult;
import com.example.ourprojecttest.R;

import java.util.ArrayList;
import java.util.Map;

public class NeedToPay extends AppCompatActivity {

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    public static final String APPID = "2016101700706177";

    private static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDJ1ys/sxQE6Dem09YM4AeabOd04OU2jC+fRTrcZFHNrRdL2FoglSe0G9To5rMsWLUp6yTUWK4aYPRbUUVdzqeYYzcBbg9wzph1aEDM0EvAfL+EMVk2vOXd+ALKCiiMoT5SwC94yU9G+BcuMIywc2kqXZTnaJpKaeGlVm/IUyiePZ31FWP2dMzLh1IYq9OP8udk7Xn/gDvpQz+00IMOugudCxa/F9VvwqRQaUJE8QnxSVSYxsVfobZ1aY2ddt+tUxbirFYaHadfJAwTOK9BrerXULk8S2RQPM7PVJdi8QNxwHssYgZ5oSYQsjGvpJBT/1boJttWkP7vKU++rTWsE/rjAgMBAAECggEAFXggro7i0z7MJJ+lxgrSZDevSvxdBTdWHW/kueql1OXTc4rY01xqM7s+I2PerRnmc0YCzd987WtgspHrefXwV8I96JYHaG1hRCPJuL9zP09Fo88H+U2QedLWoR0BgSvpkC3HHuby2s6q0IvzexVbm1kG7LJwrveiO8785ucJjAM7ZO6rMR7FGoPHn8YMmZL0KLqx9GIKBYqIDK7kaghVY5b+rEpG9DeC8DYNGUBBx1CGBKNvTfj2xaJjzN1BPz4OI8++Z5LI58AbYVXGRfT9WsSfL2mIpxD7RTxuIjwhaLzBQk7l2ex0osplrQOy6BoKEja/bfRawf8Pc86Bo1+e8QKBgQDyCz6fYAZp/WyJT/VMMSS7FlLz6/ixIfrkVJwkbj1jtpe2HoMCkOjIudaTYuxA3fg0qZsD2cY7/OryRZEEGYe2TRCWLIS0G/p67D9erY3Dty7+5xwLIDbBoYMEfk9i3UaUGzrtgvEUvQDrIGl4pid+ftklTU191d+KzokuiOL6yQKBgQDVen2ioEZGjeEfiqAdGy1MAtkmJsXY3kBUWo0gfk56lgx35kjVbEsIu0wD/zzTFHsf0XHmakSgmsBvq3ElaQAZKXY11fotOq3EXYoX7IvaA9GZR8EjeccrGcdQePK52cR9AcvoM4cG421kscYhfdjw0r5a+QDJwwf7HDXlWy9ySwKBgQChWp+njVMZSykUrKoA3e33jl1EYHWMV/OyTTk+DAN+upWOge6iQkn8re5+mH6Yi6DQMpS1T3MYQHW7hmazDfXrsJozEoBwtQoY8e8Yxafw5eg9Y4HNZO87y9jUoQN5C7vmNfTlqtneElVPaW8GT/WaHSPS+yKClZYNKbxHuldeCQKBgCg6gv5ocZXOGsRU3UNe4bRXPRCBcfsiNsEupzWeV6+mIwddMBB37dPhZ7vBF3c3ftRKJcqj7/bL8sOYbSP9m3UiaRJQFmr7ic9dSS6k9t3IpnDaIr1Kr4uhufuiLytyrCJaelBxlVpo9S5qicm5623GaPS/w7RBunlJoaZs/o3tAoGABx7XniHaLOWr1yOd35AZsR7OMVNcAug18wXb2nxWjgaLSZ9C9mxnmvPdFKGaDY48Qeud3HfpJb76J4cx1lXg4seJu8T/P5xAKNf9GgfR26mUyZLeJBqklwLwlABXZj/ZSfrrz7vbCEETPxfgMTvAebnYW5DvkTT0Sk/hR/Zi5cM=";
    private static final String RSA_PRIVATE = "";

    LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private RecyclerView recyclerView;
    private NeedToPayAdapter adapter;
    CommonMethod method=new CommonMethod();

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //当接收的广播为付款时，则调用支付宝进行支付
           if(intent.hasExtra("price")){
               payV2(intent.getStringExtra("price"));
           }

           //当接收到的广播为显示详情时，则弹出窗口进行展示详情
           if(intent.hasExtra("detail")){
               String[] items=intent.getStringArrayExtra("detail");
               AlertDialog.Builder listDialog=new AlertDialog.Builder(NeedToPay.this);
               listDialog.setTitle("药品详情");
               listDialog.setItems(items,null);
               listDialog.show();
           }

        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        showAlert(NeedToPay.this, "Payment success:" + payResult);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showAlert(NeedToPay.this, "Payment failed:" + payResult);
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
                        showAlert(NeedToPay.this, "Authentication success:" + authResult);
                    } else {
                        // 其他状态值则为授权失败
                        showAlert(NeedToPay.this, "Authentication failed:" + authResult);
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
        setContentView(R.layout.activity_need_to_pay);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.BUY_ORDER");
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);


    }

    private Bitmap Rfile2Bitmap(){
        return BitmapFactory.decodeResource(getResources(),R.drawable.test);
    }

    private void initView(){
        recyclerView=findViewById(R.id.displayOrder);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new NeedToPayAdapter(NeedToPay.this);


        //创建模拟订单数据
        ArrayList<OrderListBean> orderListBeans=new ArrayList<>();


        OrderListBean bean=new OrderListBean();
        bean.setOrderPrice("25.00");
        bean.setOrderTime("1954/12/11");

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
        bean.setOrderPrice("12.00");
        bean.setOrderTime("1984/12/11");
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


       adapter.setList(NeedToPayHelper.getDataAfterHandle(orderListBeans));

      //  adapter.setList(list);
        recyclerView.setAdapter(adapter);

    }

    /**
     * 支付宝支付业务示例
     */
    public void payV2(String amount) {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            showAlert(NeedToPay.this, "Error: Missing APPID or RSA_PRIVATE in PayDemoActivity.");
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
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2,"",amount);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(NeedToPay.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

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

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }
    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton("Confirm", null)
                .setOnDismissListener(onDismiss)
                .show();
    }
}

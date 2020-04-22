package com.example.ourprojecttest.StuDrugStore;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.example.ourprojecttest.AlipayModule.AuthResult;
import com.example.ourprojecttest.LoginActivity;
import com.example.ourprojecttest.StuMine.StuNeedToPay.NeedToPay;
import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.AlipayModule.OrderInfoUtil2_0;
import com.example.ourprojecttest.AlipayModule.PayResult;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.StuMine.AddressActivity;

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
import java.util.Map;

public class StuBuyDrug extends AppCompatActivity {
    private String ipAddress;
    private Display display;
    private int toastHeight;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    private static final String APPID = "2016101700706177";
    private static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDJ1ys/sxQE6Dem09YM4AeabOd04OU2jC+fRTrcZFHNrRdL2FoglSe0G9To5rMsWLUp6yTUWK4aYPRbUUVdzqeYYzcBbg9wzph1aEDM0EvAfL+EMVk2vOXd+ALKCiiMoT5SwC94yU9G+BcuMIywc2kqXZTnaJpKaeGlVm/IUyiePZ31FWP2dMzLh1IYq9OP8udk7Xn/gDvpQz+00IMOugudCxa/F9VvwqRQaUJE8QnxSVSYxsVfobZ1aY2ddt+tUxbirFYaHadfJAwTOK9BrerXULk8S2RQPM7PVJdi8QNxwHssYgZ5oSYQsjGvpJBT/1boJttWkP7vKU++rTWsE/rjAgMBAAECggEAFXggro7i0z7MJJ+lxgrSZDevSvxdBTdWHW/kueql1OXTc4rY01xqM7s+I2PerRnmc0YCzd987WtgspHrefXwV8I96JYHaG1hRCPJuL9zP09Fo88H+U2QedLWoR0BgSvpkC3HHuby2s6q0IvzexVbm1kG7LJwrveiO8785ucJjAM7ZO6rMR7FGoPHn8YMmZL0KLqx9GIKBYqIDK7kaghVY5b+rEpG9DeC8DYNGUBBx1CGBKNvTfj2xaJjzN1BPz4OI8++Z5LI58AbYVXGRfT9WsSfL2mIpxD7RTxuIjwhaLzBQk7l2ex0osplrQOy6BoKEja/bfRawf8Pc86Bo1+e8QKBgQDyCz6fYAZp/WyJT/VMMSS7FlLz6/ixIfrkVJwkbj1jtpe2HoMCkOjIudaTYuxA3fg0qZsD2cY7/OryRZEEGYe2TRCWLIS0G/p67D9erY3Dty7+5xwLIDbBoYMEfk9i3UaUGzrtgvEUvQDrIGl4pid+ftklTU191d+KzokuiOL6yQKBgQDVen2ioEZGjeEfiqAdGy1MAtkmJsXY3kBUWo0gfk56lgx35kjVbEsIu0wD/zzTFHsf0XHmakSgmsBvq3ElaQAZKXY11fotOq3EXYoX7IvaA9GZR8EjeccrGcdQePK52cR9AcvoM4cG421kscYhfdjw0r5a+QDJwwf7HDXlWy9ySwKBgQChWp+njVMZSykUrKoA3e33jl1EYHWMV/OyTTk+DAN+upWOge6iQkn8re5+mH6Yi6DQMpS1T3MYQHW7hmazDfXrsJozEoBwtQoY8e8Yxafw5eg9Y4HNZO87y9jUoQN5C7vmNfTlqtneElVPaW8GT/WaHSPS+yKClZYNKbxHuldeCQKBgCg6gv5ocZXOGsRU3UNe4bRXPRCBcfsiNsEupzWeV6+mIwddMBB37dPhZ7vBF3c3ftRKJcqj7/bL8sOYbSP9m3UiaRJQFmr7ic9dSS6k9t3IpnDaIr1Kr4uhufuiLytyrCJaelBxlVpo9S5qicm5623GaPS/w7RBunlJoaZs/o3tAoGABx7XniHaLOWr1yOd35AZsR7OMVNcAug18wXb2nxWjgaLSZ9C9mxnmvPdFKGaDY48Qeud3HfpJb76J4cx1lXg4seJu8T/P5xAKNf9GgfR26mUyZLeJBqklwLwlABXZj/ZSfrrz7vbCEETPxfgMTvAebnYW5DvkTT0Sk/hR/Zi5cM=";
    private static final String RSA_PRIVATE = "";
    private CommonMethod method = new CommonMethod();
    private Button buy;
    private TextView yaoPinName;    //用于在商品详情里显示药品的名字
    private TextView displayPrice;  //用于显示药品单价,不随个数变化而变化，显示在上部页面详情里的单价
    private View weixin;
    private View zhifubao;
    private ImageView weiXinChecked;
    private ImageView zhiFuBaoChecked;
    private ImageView jianHao;
    private ImageView jiaHao;
    private ImageView displayDrugPicture;
    private TextView yaoPinNumber;
    private TextView stuDiscribe;
    private TextView priceShow;//用于显示药品单价,随个数变化而变化,显示在底部实付款位置
    private String orderPrice;//订单总价格，用于传给支付宝支付接口
    private TextView address;
    private LinearLayout addressChange;
    private String drugId;
    private boolean AliPayFlag = true;
    double unitePrice;
    private boolean addressMessage = true;
    private DecimalFormat df = new DecimalFormat("##0.00");

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

                        upOrder();//用户支付成功后将药品订单添加到数据库中
                        String s1="支付成功";
                        show(R.layout.layout_chenggong,s1);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        //showAlert(StuBuyDrug.this, "Payment failed:" + payResult);
                        //AlertDialog.Builder builder = new AlertDialog.Builder(StuBuyDrug.this);
                        //builder.setTitle("提示");
                        //builder.setMessage("支付失败！");
                        //builder.setPositiveButton("确定", null);
                        //builder.show();
                        String s1="支付失败";
                        show(R.layout.layout_tishi_email,s1);
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
                        //showAlert(StuBuyDrug.this, "Authentication success:" + authResult);
                        Toast toast = Toast.makeText(StuBuyDrug.this, "授权成功！", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                        toast.show();
                    } else {
                        // 其他状态值则为授权失败
//                        showAlert(StuBuyDrug.this, "Authentication failed:" + authResult);
                        Toast toast = Toast.makeText(StuBuyDrug.this, "授权失败！", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                        toast.show();
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    private void upOrder(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray jsonArray = new JSONArray();
                try {
                    JSONObject object = new JSONObject();
                    object.put("stuId",method.getFileData("ID",StuBuyDrug.this ));
                    jsonArray.put(object);
                    object = new JSONObject();
                    object.put("drugId", drugId);
                    object.put("drugAmount", yaoPinNumber.getText().toString().trim());
                    jsonArray.put(object);
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("type=finishPay&order=")//notPost是学生待付款的订单
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
                        if (response == HttpURLConnection.HTTP_OK) {

                            InputStream inputStream = urlConnection.getInputStream();
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            byte[] result = new byte[1024];
                            int len = 0;
                            while ((len = inputStream.read(result)) != -1) {
                                byteArrayOutputStream.write(result, 0, len);
                            }

                            String resultData = new String(byteArrayOutputStream.toByteArray()).trim();
                            Log.d("stubuydrug.resultdata",resultData);

                        }
                        Log.d("result", "312");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void updateDisplayPrice(String orderPrice) {
        this.orderPrice=orderPrice;
        String str = "合计:￥" + orderPrice;
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FF1493"));
        builder.setSpan(colorSpan, 3, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        priceShow.setText(builder);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//沙箱环境需要的代码
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_buy_yaopin);
        ipAddress = getResources().getString(R.string.ipAdrress);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        //设置点击减号事件
        jianHao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.valueOf(yaoPinNumber.getText().toString().trim());
                if (number > 1) {
                    number--;
                    updateDisplayPrice(df.format(unitePrice * number));
                    yaoPinNumber.setText(String.valueOf(number));
                }

            }
        });

        //设置点击加号事件
        jiaHao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.valueOf(yaoPinNumber.getText().toString().trim());
                number++;
                updateDisplayPrice(df.format(unitePrice * number));
                Log.d("stubuydrug",df.format(unitePrice*number));
                yaoPinNumber.setText(String.valueOf(number));
            }
        });

        //设置选择微信支付的点击事件
        weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AliPayFlag = false;
                weiXinChecked.setVisibility(View.VISIBLE);
                zhiFuBaoChecked.setVisibility(View.INVISIBLE);
            }
        });
        //设置支付宝支付的点击事件
        zhifubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AliPayFlag = true;
                zhiFuBaoChecked.setVisibility(View.VISIBLE);
                weiXinChecked.setVisibility(View.INVISIBLE);
            }
        });
        //设置立即购买的点击事件
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AliPayFlag) {
                    if(method.getFileData("Address",StuBuyDrug.this).equals("用户暂未设置收货地址")||method.getFileData("Phone",StuBuyDrug.this).equals("用户暂未设置手机号码")||method.getFileData("Name",StuBuyDrug.this).equals("用户暂未设置名字")){
                        Toast toast = Toast.makeText(StuBuyDrug.this, "您暂未完善收获地址信息，请先完善！", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                        toast.show();
                    }else{
                        payV2(orderPrice);
                    }
                    } else {
                        String s1="微信支付功能尚未开通，尽请期待！";
                        show(R.layout.layout_tishi_email,s1);
                    }
            }
        });
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
                PayTask alipay = new PayTask(StuBuyDrug.this);
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

    private void initView() {
        display = getWindowManager().getDefaultDisplay();
        // 获取屏幕高度
        toastHeight = display.getHeight();
        displayDrugPicture = findViewById(R.id.displayDrugPicture);
        buy = findViewById(R.id.stuBuyImmediately);
        addressChange = findViewById(R.id.stu_yaodian_shouhuo_change);
        address = findViewById(R.id.stu_buyyao_address);
        weixin = findViewById(R.id.weiXinPay);
        zhifubao = findViewById(R.id.zhiFuBaoPay);
        weiXinChecked = findViewById(R.id.weiXinPayChecked);
        //默认支付宝支付
        weiXinChecked.setVisibility(View.INVISIBLE);
        zhiFuBaoChecked = findViewById(R.id.zhiFuBaoChecked);
        zhiFuBaoChecked.setVisibility(View.VISIBLE);
        jianHao = findViewById(R.id.buyYaoJianHao);
        jiaHao = findViewById(R.id.buyYaoJiaHao);
        yaoPinNumber = findViewById(R.id.yaoPinNumber);
        priceShow = findViewById(R.id.stuBuyPrice);
        yaoPinName = findViewById(R.id.stuName);
        stuDiscribe = findViewById(R.id.stuDiscribe);
        displayPrice = findViewById(R.id.stuPrcie);

        Intent intent=getIntent();
        //显示药品图片
        byte[] appIcon = intent.getByteArrayExtra("picture");
        displayDrugPicture.setImageBitmap(BitmapFactory.decodeByteArray(appIcon, 0, appIcon.length));

        unitePrice = Double.parseDouble(intent.getStringExtra("price"));
        drugId=intent.getStringExtra("drugId");
        //用于显示药品单价
        displayPrice.setText("￥ " + df.format(unitePrice));
        //设置药品价格
        updateDisplayPrice(df.format(unitePrice));
        Log.d("stubuydrug",df.format(unitePrice));
        //设置药品名字
        yaoPinName.setText(intent.getStringExtra("name"));
        stuDiscribe.setText(intent.getStringExtra("description"));
        //设置用户的收货地址
            address.setText(method.getFileData("Name", StuBuyDrug.this) + " " + method.getFileData("Phone", StuBuyDrug.this) + "\n" + method.getFileData("Address",StuBuyDrug.this));
        //设置收获地址的点击事件
        addressChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StuBuyDrug.this, AddressActivity.class);
                startActivity(intent);
            }
        });
    }
    public void show(int x,String s){
        final Dialog dialog = new Dialog(StuBuyDrug.this,R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(StuBuyDrug.this).inflate(x, null);
        TextView describe=inflate.findViewById(R.id.describe);
        describe.setText(s);
        TextView yes = inflate.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.CENTER);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width =800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }


}

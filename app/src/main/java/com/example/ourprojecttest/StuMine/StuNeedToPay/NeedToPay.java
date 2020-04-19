package com.example.ourprojecttest.StuMine.StuNeedToPay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.example.ourprojecttest.AlipayModule.AuthResult;
import com.example.ourprojecttest.PerfeActivity;
import com.example.ourprojecttest.RegisterActivity;
import com.example.ourprojecttest.StuMine.ShoppingCart.ShoppingCartActivity;
import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.AlipayModule.OrderInfoUtil2_0;
import com.example.ourprojecttest.AlipayModule.PayResult;
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
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NeedToPay extends AppCompatActivity {
    private LinearLayout empty;
    private Display display;
    private int toastHeight;
    private String ipAddress;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private final int SUCCESS=3;
    private final int FAULT=4;
    private final int UPDATE_SUCCESS=5;
    private final int UPDATE_FAULT=6;
    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    private static final String APPID = "2016101700706177";
    private SwipeRefreshLayout refresh;
    private static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDJ1ys/sxQE6Dem09YM4AeabOd04OU2jC+fRTrcZFHNrRdL2FoglSe0G9To5rMsWLUp6yTUWK4aYPRbUUVdzqeYYzcBbg9wzph1aEDM0EvAfL+EMVk2vOXd+ALKCiiMoT5SwC94yU9G+BcuMIywc2kqXZTnaJpKaeGlVm/IUyiePZ31FWP2dMzLh1IYq9OP8udk7Xn/gDvpQz+00IMOugudCxa/F9VvwqRQaUJE8QnxSVSYxsVfobZ1aY2ddt+tUxbirFYaHadfJAwTOK9BrerXULk8S2RQPM7PVJdi8QNxwHssYgZ5oSYQsjGvpJBT/1boJttWkP7vKU++rTWsE/rjAgMBAAECggEAFXggro7i0z7MJJ+lxgrSZDevSvxdBTdWHW/kueql1OXTc4rY01xqM7s+I2PerRnmc0YCzd987WtgspHrefXwV8I96JYHaG1hRCPJuL9zP09Fo88H+U2QedLWoR0BgSvpkC3HHuby2s6q0IvzexVbm1kG7LJwrveiO8785ucJjAM7ZO6rMR7FGoPHn8YMmZL0KLqx9GIKBYqIDK7kaghVY5b+rEpG9DeC8DYNGUBBx1CGBKNvTfj2xaJjzN1BPz4OI8++Z5LI58AbYVXGRfT9WsSfL2mIpxD7RTxuIjwhaLzBQk7l2ex0osplrQOy6BoKEja/bfRawf8Pc86Bo1+e8QKBgQDyCz6fYAZp/WyJT/VMMSS7FlLz6/ixIfrkVJwkbj1jtpe2HoMCkOjIudaTYuxA3fg0qZsD2cY7/OryRZEEGYe2TRCWLIS0G/p67D9erY3Dty7+5xwLIDbBoYMEfk9i3UaUGzrtgvEUvQDrIGl4pid+ftklTU191d+KzokuiOL6yQKBgQDVen2ioEZGjeEfiqAdGy1MAtkmJsXY3kBUWo0gfk56lgx35kjVbEsIu0wD/zzTFHsf0XHmakSgmsBvq3ElaQAZKXY11fotOq3EXYoX7IvaA9GZR8EjeccrGcdQePK52cR9AcvoM4cG421kscYhfdjw0r5a+QDJwwf7HDXlWy9ySwKBgQChWp+njVMZSykUrKoA3e33jl1EYHWMV/OyTTk+DAN+upWOge6iQkn8re5+mH6Yi6DQMpS1T3MYQHW7hmazDfXrsJozEoBwtQoY8e8Yxafw5eg9Y4HNZO87y9jUoQN5C7vmNfTlqtneElVPaW8GT/WaHSPS+yKClZYNKbxHuldeCQKBgCg6gv5ocZXOGsRU3UNe4bRXPRCBcfsiNsEupzWeV6+mIwddMBB37dPhZ7vBF3c3ftRKJcqj7/bL8sOYbSP9m3UiaRJQFmr7ic9dSS6k9t3IpnDaIr1Kr4uhufuiLytyrCJaelBxlVpo9S5qicm5623GaPS/w7RBunlJoaZs/o3tAoGABx7XniHaLOWr1yOd35AZsR7OMVNcAug18wXb2nxWjgaLSZ9C9mxnmvPdFKGaDY48Qeud3HfpJb76J4cx1lXg4seJu8T/P5xAKNf9GgfR26mUyZLeJBqklwLwlABXZj/ZSfrrz7vbCEETPxfgMTvAebnYW5DvkTT0Sk/hR/Zi5cM=";
    private static final String RSA_PRIVATE = "";
    private String id;
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private RecyclerView recyclerView;
    private NeedToPayAdapter adapter;
    private String orderId=null;
    private CommonMethod method=new CommonMethod();

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra("flag")){
                Toast toast = Toast.makeText(NeedToPay.this, "您暂未完善收获地址信息，请先完善！", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                toast.show();
            }


            //当接收的广播为付款时，则调用支付宝进行支付
           if(intent.hasExtra("price")){
               orderId=intent.getStringExtra("orderId");
               payV2(intent.getStringExtra("price"));
           }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SUCCESS:{//表示付款成功！
                    Toast toast = Toast.makeText(NeedToPay.this, "付款成功！", Toast.LENGTH_SHORT);
                    // 这里给了一个1/4屏幕高度的y轴偏移量
                    toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                    toast.show();
                    break;
                }
                case UPDATE_FAULT:{//表示付款失败
                    Toast toast = Toast.makeText(NeedToPay.this, "付款失败！", Toast.LENGTH_SHORT);
                    // 这里给了一个1/4屏幕高度的y轴偏移量
                    toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                    toast.show();
                    break;
                }
                case FAULT:{//表示当前暂无待付款订单
                    Log.d("needtopay","fault");
                  empty.setVisibility(View.VISIBLE);
                  recyclerView.setVisibility(View.GONE);
                    refresh.setRefreshing(false);
                    refresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.color_progressbar));
                    recyclerView=findViewById(R.id.historyOrderRecyclerView);
                    break;
                }
                case SUCCESS:{//表示获取订单成功
                    ArrayList<OrderListBean> orderList=(ArrayList<OrderListBean>)msg.obj;
                    adapter.setList(NeedToPayHelper.getDataAfterHandle(orderList));
                    adapter.notifyDataSetChanged();
                    refresh.setRefreshing(false);
                    //将订单数组对象保存到本地
                    writeOrderListIntoSDcard("stuNeedToPayOrder",orderList);
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
                    // 判断resultStatus 为900        0则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。

                        Log.d("payment","succ");
                        finishedPay();
                       // AlertDialog.Builder builder = new AlertDialog.Builder(NeedToPay.this);
                       // builder.setTitle("提示");
                       // builder.setMessage("支付成功！");
                       // builder.setPositiveButton("确定", null);
                       // builder.show();
                        String s1="支付成功";
                        show(R.layout.layout_chenggong,s1);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        showAlert(NeedToPay.this, "Payment failed:" + payResult);
                        //Log.d("payment","false");
                        //AlertDialog.Builder builder = new AlertDialog.Builder(NeedToPay.this);
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
                        //showAlert(NeedToPay.this, "Authentication success:" + authResult);
                        Toast toast = Toast.makeText(NeedToPay.this, "授权成功！", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                        toast.show();
                    } else {
                        // 其他状态值则为授权失败
                        //showAlert(NeedToPay.this, "Authentication failed:" + authResult);
                        Toast toast = Toast.makeText(NeedToPay.this, "授权失败！", Toast.LENGTH_SHORT);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//沙箱环境需要的代码
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_to_pay);
        ipAddress=getResources().getString(R.string.ipAdrress);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.BUY_ORDER");
        localReceiver=new LocalReceiver();
        registerReceiver(localReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
    }

    private void finishedPay(){
        final String url=ipAddress+"IM/GetNeedToPayOrder?type=finishPay&id="+id+"&orderId="+orderId;
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
                    Message msg=mHandler.obtainMessage();
                    if(responseData.equals("修改成功")){//如果数据库修改成功
                        msg.what=UPDATE_SUCCESS;
                    }
                    else{
                        msg.what=UPDATE_FAULT;
                    }
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void getData(){
        //获取订单的时候显示recycleview才能看到下拉刷新图标
        recyclerView.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
        refresh.setRefreshing(true);
        final String url=ipAddress+"IM/GetNeedToPayOrder?type=getOrder&id="+id;//finishPay是指学生刚付款款的订单
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

    private void parseJSON(String data){
        Log.d("topay",data);
        ArrayList<OrderListBean> orderList=new ArrayList<>();
        OrderListBean orderBean=new OrderListBean();
        ArrayList<ContentInfoBean> drugDataList=new ArrayList<>();
        ContentInfoBean drugData;
        Message msg=mHandler.obtainMessage();
        try {
            JSONArray jsonArray=new JSONArray(data);
            if(jsonArray.length()==1){//说明当前用户没有待付款订单
                msg.what=FAULT;
                mHandler.sendMessage(msg);
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
                mHandler.sendMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void initView(){
        empty=findViewById(R.id.empty);
        display = getWindowManager().getDefaultDisplay();
        toastHeight = display.getHeight();

        id=method.getFileData("ID",this);
        refresh=findViewById(R.id.dropDownToRefresh);
        //设置下拉刷新过去数据
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        String add = method.getFileData("Address", NeedToPay.this);
        if (add.equals("用户暂未设置收货地址")||add == "" ) {
            AddressMessage.addressMessage = false;
        }
        refresh.setColorSchemeColors(getResources().getColor(R.color.color_bottom));
        refresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.color_progressbar));
        recyclerView=findViewById(R.id.displayOrder);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new NeedToPayAdapter(NeedToPay.this);
        recyclerView.setAdapter(adapter);
        ArrayList<OrderListBean> orderList=readOrderListFromSdCard("stuNeedToPayOrder");
        //如果本地缓存订单数据不为空，则先显示出来
        if(orderList!=null){
            adapter.setList(NeedToPayHelper.getDataAfterHandle(orderList));
            adapter.notifyDataSetChanged();
        }
        getData();//获取待付款订单
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
    public void show(int x,String s){
        final Dialog dialog = new Dialog(NeedToPay.this,R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(NeedToPay.this).inflate(x, null);
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

package com.example.ourprojecttest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourprojecttest.StuMine.NumPicker;
import com.example.ourprojecttest.StuMine.StuInfomation.Numpickerr;
import com.example.ourprojecttest.StuMine.StuInfomation.StuInformation;
import com.example.ourprojecttest.StuMine.Tubiao;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;

import org.json.JSONObject;

import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PerfeActivity extends AppCompatActivity {
    private Display display;
    private int toastHeight;
    private String ipAddress;
    private TextView stuName;
    private TextView stuHei;
    private TextView stuWei;
    private TextView stuBir;
    private RadioButton radioMen,radioWomen;
    private Button btnRegister;
    private TextView dateDisplay;
    private LinearLayout wei,hei,btn;
    private int mYear, mMonth, mDay;
    final int DATE_DIALOG = 1;
    private String userNo;
    private String userPwd;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 0:
                    String s="注册成功，准备好登录了吗？";
                    Intent intent = new Intent(PerfeActivity.this,LoginActivity.class);
                    show(R.layout.layout_tishi_email,s,intent,R.drawable.zcsuccess);
                    Log.d("registerresult","success");
                    break;
                case -1:
                    String s1="邮箱已被使用";
                    show(R.layout.layout_tishi_email,s1);
                    //new AlertDialog.Builder(PerfeActivity.this).setTitle("错误").setMessage("邮箱已被使用").setNegativeButton("确定",null).show();
                default:
                    break;
            }



        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfe);
        ipAddress=getResources().getString(R.string.ipAdrress);
        display = getWindowManager().getDefaultDisplay();
        toastHeight = display.getHeight();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        Intent intent = getIntent();
        //注册身高广播
        Receiver1 receiver1=new Receiver1();
        IntentFilter hei=new IntentFilter();
        hei.addAction("Height");
        registerReceiver(receiver1,hei);
        //注册体重广播
        Receiver2 receiver2=new Receiver2();
        IntentFilter wei=new IntentFilter();
        wei.addAction("Weight");
        registerReceiver(receiver2,wei);
        userNo = intent.getStringExtra("string_no");
        userPwd = intent.getStringExtra("string_pwd");

        dateDisplay = findViewById(R.id.dateDisplay);

        dateDisplay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });

        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        initView();
        initListener();
    }
    //初始化数据
    private void initView(){
        stuName =findViewById(R.id.stuName);
        stuHei = findViewById(R.id.stuHei);
        stuWei =  findViewById(R.id.stuWei);
        stuBir = findViewById(R.id.dateDisplay);
        btnRegister = findViewById(R.id.stuReg);
        radioMen =  findViewById(R.id.radioMen);
        radioWomen =  findViewById(R.id.radioWomen);
    }
    //初始化方法
    private void initListener(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = stuName.getText().toString();
                String userHei = stuHei.getText().toString();
                String userWei = stuWei.getText().toString();
                String userBir = stuBir.getText().toString();

                boolean flag = true;
                if (!radioWomen.isChecked()&&!radioMen.isChecked()){
                   // new AlertDialog.Builder(PerfeActivity.this).setTitle("错误").setMessage("请选择性别").setNegativeButton("确定",null).show();
                    String s="请选择性别";
                    show(R.layout.layout_tishi_email,s);
                }
                else if (TextUtils.isEmpty(userName)||TextUtils.isEmpty(userBir)||TextUtils.isEmpty(userHei)||TextUtils.isEmpty(userWei)){
                    //new AlertDialog.Builder(PerfeActivity.this).setTitle("错误").setMessage("值不能为空").setNegativeButton("确定",null).show();
                    String s="值不能为空";
                    show(R.layout.layout_tishi_email,s);
                }
                else if (flag)
                {
                    //
                    interData();
                }



            }
        });
        stuWei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Numpickerr numpickerr=new Numpickerr(PerfeActivity.this);
                numpickerr.show();
                numpickerr.setContext(PerfeActivity.this);
            }
        });

        stuHei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumPicker numPicker=new NumPicker(PerfeActivity.this);
                numPicker.show();
                numPicker.setContext(PerfeActivity.this);
            }
        });

    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void display() {
        dateDisplay.setText(new StringBuffer().append(mYear).append("-").append(mMonth+1).append("-").append(mDay).append(" "));
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display();
        }
    };

    //向数据库中插入数据
    private void interData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String userName = stuName.getText().toString().trim();
                    String UserHei[] = (stuHei.getText().toString().trim()).split(" ");
                    String userHei=UserHei[0];
                    String UserWei[] = (stuWei.getText().toString().trim()).split(" ");
                    String userWei=UserWei[0];
                    String userBir = stuBir.getText().toString().trim();
                    String url = "";
                    if (radioMen.isChecked())
                    {
                        url = ipAddress+"IM/servlet/LoginDataServlet?no="+userNo+"&name="+userName+"&pwd="+userPwd+"&sex=男&birth="+userBir+"&height="+userHei+"&weight="+userWei+"&sno=9999";
                    }
                    else
                    {
                        url = ipAddress+"IM/servlet/LoginDataServlet?no="+userNo+"&name="+userName+"&pwd="+userPwd+"&sex=女&birth="+userBir+"&height="+userHei+"&weight="+userWei+"&sno=9999";
                    }

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithJSONObject(responseData);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void parseJSONWithJSONObject(String jsonData){
        Log.d("registerInfo",jsonData);
        try{
            JSONObject jsonObject=new JSONObject(jsonData);
            int code=jsonObject.getInt("code");
            Message msg = Message.obtain();

            if (code==0)
            {
                msg.what = 0 ;
            }
            else
            {
                msg.what = -1;
            }
            handler.sendMessage(msg);

        } catch ( Exception e)
        {
            e.printStackTrace();
        }
    }
    public void show(int x,String s){
    final Dialog dialog = new Dialog(PerfeActivity.this,R.style.ActionSheetDialogStyle);        //展示对话框
    //填充对话框的布局
    View inflate = LayoutInflater.from(PerfeActivity.this).inflate(x, null);
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
    public void show(int x,String s,Intent intent,int y){
        final Dialog dialog = new Dialog(PerfeActivity.this,R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(PerfeActivity.this).inflate(x, null);
        TextView describe=inflate.findViewById(R.id.describe);
        describe.setText(s);
        TextView yes = inflate.findViewById(R.id.yes);
        ImageView p=inflate.findViewById(R.id.picture);
        p.setImageDrawable(getResources().getDrawable(y));
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
                dialog.dismiss();
                RegisterActivity.activity.finish();//销毁登录活动
                finish();//销毁完善信息活动
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

    public class Receiver1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getStringExtra("Height");
            stuHei.setText(s);
        }
    }

    public class Receiver2 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getStringExtra("Weight");
            stuWei.setText(s);

        }
    }
}


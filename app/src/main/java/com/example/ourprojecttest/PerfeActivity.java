package com.example.ourprojecttest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.ourprojecttest.Utils.ImmersiveStatusbar;

import org.json.JSONObject;

import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PerfeActivity extends AppCompatActivity {
    private String ipAddress;
    private TextView stuName;
    private TextView stuHei;
    private TextView stuWei;
    private TextView stuBir;
    private RadioButton radioMen,radioWomen;
    private Button btnRegister;
    private Button btn;
    private TextView dateDisplay;
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
                   // new AlertDialog.Builder(PerfeActivity.this).setTitle("跳转").setMessage("注册成功,准备好登陆了吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                   //     @Override
                   //     public void onClick(DialogInterface dialogInterface, int i) {
                   //         Intent intent = new Intent(PerfeActivity.this,MailboxActivity.class);
                   //         startActivity(intent);
                   //     }
                   // }).setNegativeButton("取消",null).show();
                    String s="注册成功，准备好登录了吗？";
                    Intent intent = new Intent(PerfeActivity.this,MailboxActivity.class);
                    show(R.layout.layout_tishi_email,s,intent);
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
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        Intent intent = getIntent();
        userNo = intent.getStringExtra("string_no");
        userPwd = intent.getStringExtra("string_pwd");

        btn = (Button) findViewById(R.id.dateChoose);
        dateDisplay = (TextView) findViewById(R.id.dateDisplay);

        btn.setOnClickListener(new View.OnClickListener() {

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
        stuName = (TextView) findViewById(R.id.stuName);
        stuHei = (TextView) findViewById(R.id.stuHei);
        stuWei = (TextView) findViewById(R.id.stuWei);
        stuBir = (TextView) findViewById(R.id.dateDisplay);
        btnRegister = (Button) findViewById(R.id.stuReg);
        radioMen = (RadioButton) findViewById(R.id.radioMen);
        radioWomen = (RadioButton) findViewById(R.id.radioWomen);
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
                    flag = false;
                    String s="请选择性别";
                    show(R.layout.layout_tishi_email,s);
                }
                else if (TextUtils.isEmpty(userName)||TextUtils.isEmpty(userBir)||TextUtils.isEmpty(userHei)||TextUtils.isEmpty(userWei)){
                    //new AlertDialog.Builder(PerfeActivity.this).setTitle("错误").setMessage("值不能为空").setNegativeButton("确定",null).show();
                    flag = false;
                    String s="值不能为空";
                    show(R.layout.layout_tishi_email,s);
                }
                else if (!checkHei(userHei)&&flag){
                    //new AlertDialog.Builder(PerfeActivity.this).setTitle("错误").setMessage("请合理输入身高").setNegativeButton("确定",null).show();
                    flag = false;
                    String s="请输入合理身高";
                    show(R.layout.layout_tishi_email,s);
                }
                else if (!checkWei(userWei)&&flag){
                   // new AlertDialog.Builder(PerfeActivity.this).setTitle("错误").setMessage("请输入合理体重").setNegativeButton("确定",null).show();
                    flag = false;
                    String s="请输入合理体重";
                    show(R.layout.layout_tishi_email,s);
                }
                else if (flag)
                {
                    //
                    interData();
                }



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
    //检验身高
    private boolean checkHei(String hei){
        try {
            if (Integer.parseInt(hei)<=300&&Integer.parseInt(hei)>=100)
            {
                return true;
            }
            else
            {
                return false;
            }

        }catch (NumberFormatException e){
            //new AlertDialog.Builder(RegisterActivity.this).setTitle("错误").setMessage("请输入数字").setNegativeButton("确定",null).show();
            return false;
        }

    }
    //检验体重
    private boolean checkWei(String wei){
        try {
            if (Integer.parseInt(wei)<=150&&Integer.parseInt(wei)>=30)
            {
                return true;
            }
            else
            {
                return false;
            }
        }catch (NumberFormatException e){
            return false;
        }

    }
    //向数据库中插入数据
    private void interData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String userName = stuName.getText().toString().trim();
                    String userHei = stuHei.getText().toString().trim();
                    String userWei = stuWei.getText().toString().trim();
                    String userBir = stuBir.getText().toString().trim();
                    String url = "";
                    if (radioMen.isChecked())
                    {
                        url = ipAddress+"IM1/servlet/LoginDataServlet?no="+userNo+"&name="+userName+"&pwd="+userPwd+"&sex=男&birth="+userBir+"&height="+userHei+"&weight="+userWei+"&sno=9999";
                    }
                    else
                    {
                        url = ipAddress+"IM1/servlet/LoginDataServlet?no="+userNo+"&name="+userName+"&pwd="+userPwd+"&sex=女&birth="+userBir+"&height="+userHei+"&weight="+userWei+"&sno=9999";
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
        try{
            JSONObject jsonObject=new JSONObject(jsonData);
            String code=jsonObject.getString("code");
            Message msg = Message.obtain();

            if (code.equals("0"))
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
    public void show(int x,String s,Intent intent){
        final Dialog dialog = new Dialog(PerfeActivity.this,R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(PerfeActivity.this).inflate(x, null);
        TextView describe=inflate.findViewById(R.id.describe);
        describe.setText(s);
        TextView yes = inflate.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
                dialog.dismiss();
            }
        });
        TextView no = inflate.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
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


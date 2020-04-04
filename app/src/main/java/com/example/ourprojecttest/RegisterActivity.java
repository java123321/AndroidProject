package com.example.ourprojecttest;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.ourprojecttest.SendEmail.MyEamil;
import com.example.ourprojecttest.StuMine.ShoppingCart.ShoppingCartActivity;
import com.example.ourprojecttest.StuMine.ShoppingCart.ShoppingCartBean;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private String code="";
    private String ipAddress;
    private Button btn;
    private TextView stuNo;
    private TextView stuPwd;
    private TextView stuPwd_two;
    private TextView stuMsg;
    private Button btnRegister;
    private Button getmsg;
    private TimeCount time;
    private boolean isHide=true;
    private Drawable drawableEyeOpen,drawableEyeClose;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    sendCode();
                    //60秒倒计时
                    break;
                case -1:
                    // new AlertDialog.Builder(RegisterActivity.this).setTitle("错误").setMessage("邮箱已被使用").setNegativeButton("确定",null).show();
                    String s="邮箱已被使用";
                    show(R.layout.layout_tishi_email,s);
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ipAddress=getResources().getString(R.string.ipAdrress);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        //获取验证码
        time = new TimeCount(60000, 1000);

        initView();
        initListener();
        drawableEyeClose = getResources().getDrawable(R.drawable.biyan);
        drawableEyeOpen = getResources().getDrawable(R.drawable.zhengyan);

        stuPwd.setOnTouchListener(new View.OnTouchListener() {

            final Drawable[] drawables = stuPwd.getCompoundDrawables();//获取密码框的drawable数组
            final int eyeWidth = drawables[2].getBounds().width();// 眼睛图标的宽度

            Drawable drawable = stuPwd.getCompoundDrawables()[2];

            public boolean onTouch(View view, MotionEvent event) {
                if (event.getX() > stuPwd.getWidth() - stuPwd.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    if (event.getAction() != MotionEvent.ACTION_UP)
                        return false;

                    //如果当前密码框是密文
                    if (isHide) {
                        drawableEyeOpen.setBounds(drawables[2].getBounds());//设置睁开眼睛的界限

                        stuPwd.setCompoundDrawables(drawables[0], null, drawableEyeOpen, null);
                        Log.d("loginfalse", String.valueOf(isHide));
                        stuPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        isHide = false;
                    }
                    //如果当前密码框是明文
                    else {
                        drawableEyeClose.setBounds(drawables[2].getBounds());//设置闭眼的界限
                        stuPwd.setCompoundDrawables(drawables[0], null, drawableEyeClose, null);

                        Log.d("logintrue", String.valueOf(isHide));
                        stuPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        isHide = true;
                    }

                }
                return false;
            }
        });
        stuPwd_two.setOnTouchListener(new View.OnTouchListener() {

            final Drawable[] drawables = stuPwd_two.getCompoundDrawables();//获取密码框的drawable数组
            final int eyeWidth = drawables[2].getBounds().width();// 眼睛图标的宽度

            Drawable drawable = stuPwd_two.getCompoundDrawables()[2];

            public boolean onTouch(View view, MotionEvent event) {
                if (event.getX() > stuPwd_two.getWidth() - stuPwd_two.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    if (event.getAction() != MotionEvent.ACTION_UP)
                        return false;

                    //如果当前密码框是密文
                    if (isHide) {
                        drawableEyeOpen.setBounds(drawables[2].getBounds());//设置睁开眼睛的界限

                        stuPwd_two.setCompoundDrawables(drawables[0], null, drawableEyeOpen, null);
                        Log.d("loginfalse", String.valueOf(isHide));
                        stuPwd_two.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        isHide = false;
                    }
                    //如果当前密码框是明文
                    else {
                        drawableEyeClose.setBounds(drawables[2].getBounds());//设置闭眼的界限
                        stuPwd_two.setCompoundDrawables(drawables[0], null, drawableEyeClose, null);

                        Log.d("logintrue", String.valueOf(isHide));
                        stuPwd_two.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        isHide = true;
                    }

                }
                return false;
            }
        });

    }

    //验证码60秒倒计时
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            getmsg.setClickable(false);
            getmsg.setText("(" + millisUntilFinished / 1000 + ") 秒后可重发");
        }
        @Override
        public void onFinish() {
            getmsg.setText("重新获取验证码");
            getmsg.setClickable(true);
        }

    }
    //初始化数据
    private void initView(){
        getmsg =  this.findViewById(R.id.getmsg);
        getmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code =String.valueOf((int)(Math.random() * 10000));
                interData();

            }
        });
        stuNo =  findViewById(R.id.stuNo);
        stuPwd =  findViewById(R.id.stuPwd);
        stuPwd_two =  findViewById(R.id.stuPwd_two);
        stuMsg =  findViewById(R.id.msg);
        btnRegister =  findViewById(R.id.stuReg);
    }
    //初始化方法
    private void initListener(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userNo = stuNo.getText().toString();
                final String userPwd = stuPwd.getText().toString();
                String userPwd_two = stuPwd_two.getText().toString();
                String userMsg = stuMsg.getText().toString();

                boolean flag = true;
                if (!checkNo(userNo)&&flag){
                   // new AlertDialog.Builder(RegisterActivity.this).setTitle("错误").setMessage("请输入正确邮箱").setNegativeButton("确定",null).show();
                   // flag = false;
                    String s="请输入正确邮箱";
                  show(R.layout.layout_tishi_email,s);
                  flag=false;
                }



                else if (!checkPwd(userPwd)&&flag){
                   // new AlertDialog.Builder(RegisterActivity.this).setTitle("错误").setMessage("密码长度为6到16位").setNegativeButton("确定",null).show();
                    flag = false;

                    String s="密码长度6到16位";
                    show(R.layout.layout_tishi_email,s);
                }
                else if (!checkPwd_repitition(userPwd,userPwd_two)&&flag){
                    //new AlertDialog.Builder(RegisterActivity.this).setTitle("错误").setMessage("两次密码不一致").setNegativeButton("确定",null).show();
                    flag = false;
                    String s="两次密码不一致";
                    show(R.layout.layout_tishi_email,s);
                }
                else if (!checkCode(userMsg)&&flag)
                {
                    //new AlertDialog.Builder(RegisterActivity.this).setTitle("错误").setMessage("验证码错误").setNegativeButton("确定",null).show();
                    flag = false;
                    String s="验证码错误";
                    show(R.layout.layout_tishi_email,s);
                }
                else if (flag)
                {
                    final Dialog dialog = new Dialog(RegisterActivity.this,R.style.ActionSheetDialogStyle);        //展示对话框
                    //填充对话框的布局
                    View inflate = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.layout_jixuwanshan, null);
                    //初始化控件
                    TextView yes = inflate.findViewById(R.id.yes);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(RegisterActivity.this,PerfeActivity.class);
                            intent.putExtra("string_no",userNo);
                            intent.putExtra("string_pwd",userPwd);
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

                    //将布局设置给Dialog
                    dialog.setContentView(inflate);
                    //获取当前Activity所在的窗体

                    Window dialogWindow = dialog.getWindow();
                    //设置Dialog从窗体底部弹出
                    dialogWindow.setGravity( Gravity.CENTER);
                    //获得窗体的属性
                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    lp.width =800;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialogWindow.setAttributes(lp);
//       将属性设置给窗体
                    dialog.show();//显示对话框
                }
            }
        });
    }
    //发送验证码
    private void sendCode()
    {
        stuNo =  findViewById(R.id.stuNo);
        final String userNo = stuNo.getText().toString().trim();
        if (checkNo(userNo)&&!userNo.isEmpty()) {
            time.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //MyEamil myEamil = new MyEamil();
                    MyEamil.sendMail(userNo, code);
                }

            }).start();
        }
        else{
           // new AlertDialog.Builder(RegisterActivity.this).setTitle("错误").setMessage("邮箱格式错误").setNegativeButton("确定",null).show();
            String s="邮箱格式不正确";
            show(R.layout.layout_tishi_email,s);
        }
    }
    //验证验证码是否一致
    private boolean checkCode(String userCode)
    {
        if (userCode.equals(code))
            return true;
        else
            return false;
    }
    //验证密码是否一致
    private boolean checkPwd_repitition(String pwd,String pwd_two){
        if (pwd.equals(pwd_two))
        {
            return true;
        }
        else
        {
            return false;
        }

    }
    //验证密码
    private boolean checkPwd(String pwd){
        if (pwd.length()<=16 && pwd.length()>=6){
            return true;
        }
        else {
            return false;
        }
    }
    //检验邮箱
    private boolean checkNo(String no){
        boolean tag = true;
        final String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        final Pattern pattern = Pattern.compile(pattern1);
        final Matcher mat = pattern.matcher(no);
        if (!mat.find()) {
            tag = false;
        }
        return tag;
    }
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
                            String userNo = stuNo.getText().toString().trim();
                            String url = "";
                            url = ipAddress+"IM/servlet/IsExist_Register?no="+userNo;
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
            private void parseJSONWithJSONObject(String jsonData ){
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
    final Dialog dialog = new Dialog(RegisterActivity.this,R.style.ActionSheetDialogStyle);        //展示对话框
    //填充对话框的布局
    View inflate = LayoutInflater.from(RegisterActivity.this).inflate(x, null);
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






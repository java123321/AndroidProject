package com.example.ourprojecttest.StuMine;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ourprojecttest.PerfeActivity;
import com.example.ourprojecttest.RegisterActivity;
import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Address;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddressActivity extends AppCompatActivity {
    private String ipAddress;
    private CommonMethod method=new CommonMethod();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    //成功
                   // new AlertDialog.Builder(AddressActivity.this).setTitle("正确").setMessage("成功").setNegativeButton("确定",null).show();
                    String s1="恭喜您，操作成功";
                    show(R.layout.layout_chenggong,s1);
                    break;
                case -1:
                    //败
                    //new AlertDialog.Builder(AddressActivity.this).setTitle("错误").setMessage("失败").setNegativeButton("确定",null).show();
                    String s2="很遗憾，操作失败";
                    show(R.layout.layout_tishi_email,s2);
                default:
                    break;
            }
        }
    };
    private static final int SHOW = 0x00000000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ipAddress=getResources().getString(R.string.ipAdrress);
        Button compile =  findViewById(R.id.compile);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        final EditText add_name =  findViewById(R.id.con_name);
        final EditText add_phone =  findViewById(R.id.con_phone);
        final EditText add_address =  findViewById(R.id.con_address);
        final Button add_submit = findViewById(R.id.submit);
        final TextView show_address =  findViewById(R.id.msg);

        if (method.getFileData("Phone",AddressActivity.this)!=null&&method.getFileData("Address",AddressActivity.this)!=null)
            show_address.setText("用户名:"+method.getFileData("Name",AddressActivity.this)+"\n电话:"+method.getFileData("Phone",AddressActivity.this)+"\n地址:"+method.getFileData("Address",AddressActivity.this));
        compile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_address.setVisibility(SHOW);
                add_name.setVisibility(SHOW);
                add_submit.setVisibility(SHOW);
                add_phone.setVisibility(SHOW);

            }
        });
        add_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String show_name = add_name.getText().toString().trim();
                final String show_phone = add_phone.getText().toString().trim();
                final String address = add_address.getText().toString().trim();
                boolean flag = true;
                if (show_name.isEmpty()||show_phone.isEmpty()||address.isEmpty())
                {
                    //new AlertDialog.Builder(AddressActivity.this).setTitle("错误").setMessage("请补全信息").setNegativeButton("确定",null).show();
                    String s1="请补全信息";
                    show(R.layout.layout_tishi_email,s1);
                    flag = false;
                }else if (!check_phone(show_phone)&&flag)
                {
                   // new AlertDialog.Builder(AddressActivity.this).setTitle("错误").setMessage("请填入11位手机号").setNegativeButton("确定",null).show();
                    String s1="请填入11位手机号";
                    show(R.layout.layout_tishi_email,s1);
                    flag = false;
                }else if (flag){
                    method.saveFileData("Name",show_name,AddressActivity.this);
                    method.saveFileData("Phone",show_phone,AddressActivity.this);
                    method.saveFileData("Address",address,AddressActivity.this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                                parseJSONWithJSONObject(upAddressByPost(show_name,address,show_phone,method.getFileData("ID", AddressActivity.this)));
//
                        }
                    }).start();
                    show_address.setText("姓名："+show_name.trim()+"\n"+"电话："+show_phone.trim()+"\n"+"地址："+address.trim());
                    add_address.setVisibility(View.INVISIBLE);
                    add_name.setVisibility(View.INVISIBLE);
                    add_submit.setVisibility(View.INVISIBLE);
                    add_phone.setVisibility(View.INVISIBLE);
                }


            }
        });
    }

    private String upAddressByPost(String stu_name,String address,String show_phone,String stuId){
        StringBuilder info=new StringBuilder();
        info.append("name=")
                .append(stu_name)
                .append("&address=")
                .append(address)
                .append("&phone=")
                .append(show_phone)
                .append("&stuId=")
                .append(stuId);


        byte[] data = info.toString().getBytes();
        try {
            URL url = new URL(ipAddress + "IM/UpdateAddress");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);//设置连接超时时间
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
                return new String(byteArrayOutputStream.toByteArray()).trim();

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    private void parseJSONWithJSONObject(String jsonData){
        try{

            JSONObject jsonObject=new JSONObject(jsonData);
            String code=jsonObject.getString("code");
            Message msg = Message.obtain();

            if(code.equals("0")){
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
//    private String conversion(String origin){
//        if(origin.equals("")||origin.equals(null)){
//            return "";
//        }
//        else{
//            StringBuilder sb=new StringBuilder();
//            for(int i=0;i<origin.length();i++) {
//                sb.append(Integer.toHexString(origin.charAt(i)&0xffff));
//            }
//            return sb.toString().trim();
//        }
//    }
    public boolean check_phone(String phone){
        Pattern p = Pattern
                .compile("^((1[0-9][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }
    public void show(int x,String s){
        final Dialog dialog = new Dialog(AddressActivity.this,R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(AddressActivity.this).inflate(x, null);
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

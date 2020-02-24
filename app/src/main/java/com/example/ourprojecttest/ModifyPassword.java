package com.example.ourprojecttest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ModifyPassword extends AppCompatActivity {
    CommonMethod method=new CommonMethod();
    EditText oldPass;
    EditText newPass;
    EditText newPassAgain;
    Button confirm;
    private final int SUCCESS=1;
    private final int FAILED=0;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    Toast.makeText(ModifyPassword.this, "密码修改成功，下次请使用新密码登录！", Toast.LENGTH_SHORT).show();
                    break;
                case FAILED:
                    Toast.makeText(ModifyPassword.this, "修改失败，请稍后再试！", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
    }


    private void initView(){
        oldPass=findViewById(R.id.stu_modify_old_pass);
        newPass=findViewById(R.id.stu_modify_new_pass);
        newPassAgain=findViewById(R.id.stu_modify_new_pass_again);
        confirm=findViewById(R.id.stu_modify_pass_confirm);

        //设置修改密码的点击事件
        confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //如果旧密码输入正确
                if(method.getFileData("Passworld", ModifyPassword.this).equals(oldPass.getText().toString().trim())){
                    //如果两次密码输入一致
                    Log.d("modipass","-----------");
                    Log.d("modipass1",newPass.getText().toString().trim());
                    Log.d("modipass2",newPassAgain.getText().toString().trim());
                    if(newPass.getText().toString().trim().equals(newPassAgain.getText().toString().trim())){
                        //开启线程更改密码
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String url=getResources().getString(R.string.ipAdrress)+"IM/servlet/PasswordModify?no="+method.getFileData("ID", ModifyPassword.this)+"&pwd="+newPass.getText().toString().trim();
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url(url)
                                        .build();
                                try{
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    parseJSONWithJSONObject(responseData);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    else{//如果两次新密码输入不一致的话
                        Toast.makeText(ModifyPassword.this, "两次新密码输入不一致，请重新您输入！", Toast.LENGTH_SHORT).show();
                    }
                }
                else{//如果旧密码输入错误
                    Toast.makeText(ModifyPassword.this, "旧密码错误，密码修改失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void parseJSONWithJSONObject(String jsonData){
        try{
            String code=null;
            JSONObject jsonObject=new JSONObject(jsonData);
            code=jsonObject.getString("code");

            Message msg = Message.obtain();
            //若修改成功

            if(code.equals("-1")||code.equals("0")){
                //将更改后的新密码保存到本地，以防止以后从本地调用密码出错
                method.saveFileData("Passworld",newPass.getText().toString().trim(), ModifyPassword.this);
                msg.what=SUCCESS;
                handler.sendMessage(msg);
            }
            else{//修改失败
                msg.what=FAILED;
                handler.sendMessage(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }


}

package com.example.ourprojecttest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ourprojecttest.SendEmail.MyEamil;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FindPass extends AppCompatActivity {
    private EditText find_pass_input_new_pass;
    private EditText find_pass_user_name;
    private EditText input_verification_box;
    private Button get_verification_code;
    private Button find_pass_confirm;
    private final int MODIFY_SUCCESS=0;
    private final int MODIFY_FAILED=-1;
    private final int ID_NOT_EXIST=1;
    private final int VERIFICATION_CODE_ERROR=2;
    private final int TIME_COUNT=3;
    int num=-1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                //开始验证码倒计时
                case TIME_COUNT:
                    TimeCount time = new TimeCount(60000, 1000);
                    time.start();
                    break;
                    //验证码错误
                case VERIFICATION_CODE_ERROR:
                    Toast.makeText(FindPass.this,"验证码错误，修改失败!",Toast.LENGTH_SHORT).show();
                    break;
                    //当前用户账号不存在
                case ID_NOT_EXIST:
                    Toast.makeText(FindPass.this, "当前用户不存在，请注册！", Toast.LENGTH_SHORT).show();
                    break;
                    //密码修改成功
                case MODIFY_SUCCESS:
                    Toast.makeText(FindPass.this, "密码修改成功，请重新登录！", Toast.LENGTH_SHORT).show();
                    break;
                    //密码修改失败(可能网络不好等原因修改失败，极少出现)
                case MODIFY_FAILED:
                    Toast.makeText(FindPass.this,"密码修改失败",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pass);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        //getSupportActionBar().hide();
        find_pass_user_name=findViewById(R.id.find_pass_user_name);
        get_verification_code=findViewById(R.id.get_verification_code);
        find_pass_confirm=findViewById(R.id.find_pass_confirm);
        find_pass_input_new_pass=findViewById(R.id.find_pass_input_new_pass);
        input_verification_box=findViewById(R.id.input_verification_box);


        //获取验证码的点击事件
        get_verification_code.setOnClickListener(new View.OnClickListener(){//设置发送验证码的点击事件
          @Override
          public void onClick(View view) {

              if (find_pass_user_name.getText().toString()!=""&&checkNo(find_pass_user_name.getText().toString())){
                  //随机生成6位的验证码
                  num = (int)(Math.random()*1000000+1);

                  //开启验证id是否存在的线程
                  new Thread(new Runnable() {
                      @Override
                      public void run() {
                          String url=getResources().getString(R.string.ipAdrress)+"IM/servlet/IsExist_Register?no="+find_pass_user_name.getText().toString().trim();

                          OkHttpClient client = new OkHttpClient();
                          Request request = new Request.Builder()
                                  .url(url)
                                  .build();
                          try{
                              Response response = client.newCall(request).execute();
                              String responseData = response.body().string();
                              parseJSONWithJSONObject1(responseData);
                          }catch (Exception e){
                              e.printStackTrace();
                          }

                      }
                  }).start();

              }
              else {//用户名为空或者不是一个合法的邮箱地址

              }
          }
      });
               //设置找回密码的确认点击事件
        find_pass_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(input_verification_box.getText().toString().trim().equals(String.valueOf(num))){//如果验证码正确

                  //开启设置新密码的子线程
                  new Thread(new Runnable() {
                      @Override
                      public void run() {
                          String url=getResources().getString(R.string.ipAdrress)+"IM/servlet/PasswordModify?no="+find_pass_user_name.getText().toString().trim()+"&pwd="+find_pass_input_new_pass.getText().toString().trim();
                          OkHttpClient client = new OkHttpClient();
                          Request request = new Request.Builder()
                                  .url(url)
                                  .build();

                          try {

                              Response response = client.newCall(request).execute();

                              String responseData = response.body().string();

                              parseJSONWithJSONObject2(responseData);
                          } catch (Exception e) {
                              e.printStackTrace();
                          }
                      }
                  }).start();

              }
              else{
                  Message msg = Message.obtain();
                  msg.what=VERIFICATION_CODE_ERROR;
                  handler.sendMessage(msg);
              }
            }
        });

    }

    private void parseJSONWithJSONObject1(String jsonData){
try{
    String code=null;
    JSONObject jsonObject=new JSONObject(jsonData);
    code=jsonObject.getString("code");
    //如果当前用户名存在
    Log.d("findpasscode",code);

    //如果当前用户在数据库存在的话开始向邮箱发送验证码
    if(code.equals("-1")){
        //向倒计时程序发送消息
        Message msg = Message.obtain();
        msg.what=TIME_COUNT;
        handler.sendMessage(msg);
                    //向用户邮箱发送验证码
                MyEamil myEamil=new MyEamil();
                myEamil.sendMail(find_pass_user_name.getText().toString().trim(),String.valueOf(num));//发送邮箱和验证码
    }
    else{//如果当前用户名不存在
        Message msg = Message.obtain();
        msg.what=ID_NOT_EXIST;
        handler.sendMessage(msg);
    }
Log.d("findpasstest","-------------");
}catch (Exception e){
    e.printStackTrace();
}
    }



    private void parseJSONWithJSONObject2(String jsonData){
        try{
            String code="0000";
            JSONObject jsonObject=new JSONObject(jsonData);
            code=jsonObject.getString("code");
            Message msg = Message.obtain();

            Log.d("FindPass_tiaoshi",code);
            if(code.equals("0")||code.equals("-1")){
                msg.what =MODIFY_SUCCESS;

            }
            else{
                msg.what =MODIFY_FAILED;
            }
            handler.sendMessage(msg);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
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
             //60S到计时方法
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            get_verification_code.setClickable(false);
            get_verification_code.setText("(" + millisUntilFinished / 1000 + ") 秒后可重发");
        }
        @Override
        public void onFinish() {
            get_verification_code.setText("重新获取验证码");
            get_verification_code.setClickable(true);
        }

    }

}

package com.example.ourprojecttest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourprojecttest.NavigationBar.DocBottomNavigation;
import com.example.ourprojecttest.NavigationBar.StuBottomNavigation;
import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.Utils.PictureStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Display display;
    // 获取屏幕高度
    private int toastHeight;
    private String ipAddress;
    private Button login;
    private final int SUCCESS = 0;
    private final int ERROR = -1;
    private RadioButton radioButton_doc, radioButton_stu;
    private ProgressBar progressBar;
    private EditText userName, passWord;
    private boolean isHide = true;
    private Drawable searchEditDraw,searchEditDraw1,searchEditDraw2,searchEditDraw3,searchEditDraw4;
    private TextView login_forget_pass;
    private Drawable drawableEyeOpen, drawableEyeClose;
    private CommonMethod method = new CommonMethod();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    Log.d("dneglu", "----------");

                    //如果是学生登录
                    if (radioButton_stu.isChecked()) {
                        Intent intent = new Intent(LoginActivity.this, StuBottomNavigation.class);
                        startActivity(intent);
                        Log.d("sssicon", "handler、、");
                    } else {//如果是医生登录
                        Intent intent = new Intent(LoginActivity.this, DocBottomNavigation.class);
                        startActivity(intent);
                        Log.d("sssicon", "handler、、j");
                    }
                    //隐藏加载圆圈
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d("sssicon", "handler");
                    //成功登录之后销毁登陆界面
                    finish();
                    break;
                case ERROR:
                    Toast toast = Toast.makeText(LoginActivity.this, "账号或密码错误！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                    toast.show();

                    progressBar.setVisibility(View.INVISIBLE);
                default:
                    break;
            }
        }
    };



    //如果用户拒绝权限则无法正常使用app
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            System.exit(0);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //检测是否有写的权限
        try {
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        initView();
    }

    private void initView() {

        display = getWindowManager().getDefaultDisplay();
        // 获取屏幕高度
        toastHeight = display.getHeight();
        ipAddress = getResources().getString(R.string.ipAdrress);
        login_forget_pass = findViewById(R.id.login_forget_pass);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        //获取radiobutton
        radioButton_doc = findViewById(R.id.doctor);
        radioButton_stu = findViewById(R.id.student);
        //获取文本框
        userName = findViewById(R.id.user_name);
        passWord = findViewById(R.id.user_psw);
        //获取眼睛图片资源
        drawableEyeClose = getResources().getDrawable(R.drawable.biyan);
        drawableEyeOpen = getResources().getDrawable(R.drawable.zhengyan);
        //获取加载圆圈
        progressBar = findViewById(R.id.loginProgress);
        progressBar.setVisibility(View.INVISIBLE);
        searchEditDraw = getResources().getDrawable(R.drawable.yonghu);
        searchEditDraw1 = getResources().getDrawable(R.drawable.chahao);
        searchEditDraw2 = getResources().getDrawable(R.drawable.suo);
        searchEditDraw3 = getResources().getDrawable(R.drawable.biyan);
        searchEditDraw.setBounds(0, 0, 60, 60);
        searchEditDraw1.setBounds(0, 0, 60, 60);
        searchEditDraw2.setBounds(0, 0, 60, 60);
        searchEditDraw3.setBounds(0, 0, 60, 60);
        searchEditDraw4 = getResources().getDrawable(R.drawable.zhengyan);
        searchEditDraw4.setBounds(0, 0, 60, 60);
        userName.setCompoundDrawables(searchEditDraw, null, searchEditDraw1, null);
        passWord.setCompoundDrawables(searchEditDraw2, null, searchEditDraw3, null);
        //注册点击密码的点击事件

        login_forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindPass.class);
                startActivity(intent);
            }
        });
        //注册叉号的点击事件
        userName.setOnTouchListener(new View.OnTouchListener() {
            Drawable drawable = userName.getCompoundDrawables()[2];

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                //获取点击焦点
                if (event.getX() > userName.getWidth() - userName.getPaddingRight() -60) {
                    //其他活动无响应
                    if (event.getAction() != MotionEvent.ACTION_UP)
                        return false;
                    //清空用户名
                    userName.setText("");
                }
                return false;
            }
        });
        //注册小眼睛的点击事件
        passWord.setOnTouchListener(new View.OnTouchListener() {
            final Drawable[] drawables = passWord.getCompoundDrawables();//获取密码框的drawable数组
            final int eyeWidth = drawables[2].getBounds().width();// 眼睛图标的宽度

            Drawable drawable = passWord.getCompoundDrawables()[2];

            public boolean onTouch(View view, MotionEvent event) {
                if (event.getX() > passWord.getWidth() - passWord.getPaddingRight() - 60) {
                    if (event.getAction() != MotionEvent.ACTION_UP)
                        return false;

                    //如果当前密码框是密文
                    if (isHide) {
                        drawableEyeOpen.setBounds(drawables[2].getBounds());//设置睁开眼睛的界限
                        passWord.setCompoundDrawables(drawables[0], null, searchEditDraw4, null);
                        Log.d("loginfalse", String.valueOf(isHide));
                        passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        isHide = false;
                    }
                    //如果当前密码框是明文
                    else {
                        drawableEyeClose.setBounds(drawables[2].getBounds());//设置闭眼的界限
                        passWord.setCompoundDrawables(drawables[0], null, searchEditDraw3, null);
                        Log.d("logintrue", String.valueOf(isHide));
                        passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        isHide = true;
                    }
                }
                return false;
            }
        });
        //登陆

        login = findViewById(R.id.btn_login);//找到按钮
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("denglu", "chenggong");
                final String name = userName.getText().toString().trim();
                final String pass = passWord.getText().toString().trim();
                //如果用户名或密码为空的话则取消登录
                if (name.equals("") || pass.equals("")) {

                    Toast toast = Toast.makeText(LoginActivity.this, "用户名或密码不能为空！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                    toast.show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url;
                        //如果是学生登录
                        if (radioButton_stu.isChecked()) {
                            url = ipAddress + "IM/servlet/Login?no=" + name + "&pwd=" + pass;
                            Log.d("login", url);
                        } else {//如果是医生登录
                            url = ipAddress + "IM/servlet/Login_Doc?no=" + name + "&pwd=" + pass;
                            Log.d("login", url);
                        }
                        //  Log.d("dengluURL",url);
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            parseJSONToValidatePassword(responseData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }


    //该方法用来解析验证密码是否正确
    private void parseJSONToValidatePassword(String jsonData) {
        Log.d("denglu", "response:" + jsonData);
        try {

            JSONObject jsonObject = new JSONObject(jsonData);
            String code = jsonObject.getString("code");
            Message msg = Message.obtain();

            if (code.equals("0")) {

                method.saveFileData("ID", userName.getText().toString().trim(), LoginActivity.this);
                method.saveFileData("Passworld", passWord.getText().toString().trim(), LoginActivity.this);
                msg.what = SUCCESS;
                if (radioButton_stu.isChecked()) {
                    method.saveFileData("Type", "Stu", LoginActivity.this);
                    initStuInformation();
                    Log.d("denglu", "initfinish");
                } else {
                    method.saveFileData("Type", "Doc", LoginActivity.this);
                    initDocInformation();
                }

            } else {
                msg.what = ERROR;
            }
            handler.sendMessage(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDocInformation() {
        String url = ipAddress + "IM/GetDocInformation?no=" + method.getFileData("ID", LoginActivity.this);
        Log.d("dengluurl", url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();

            String responseData = response.body().string();
            Log.d("denglu", "resdata:" + responseData);
            parseJSONToDoc(responseData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //解析医生的的信息
    private void parseJSONToDoc(String jsonData) {
        Log.d("denglu", jsonData);
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            JSONObject jsonObject = jsonArray.getJSONObject(1);

            //将医生的的头像等级保存到本地
            if (jsonObject.has("Doc_Title")) {
                method.saveFileData("DocTitle", jsonObject.getString("Doc_Title"), LoginActivity.this);
            } else {
                method.saveFileData("DocTitle", "暂无", LoginActivity.this);
            }

            //将医生的名字保存到本地
            if (jsonObject.has("Doc_Name")) {
                Log.d("docname", jsonObject.getString("Doc_Name"));
                method.saveFileData("DocName", jsonObject.getString("Doc_Name"), LoginActivity.this);
            } else {
                method.saveFileData("DocName", "医生暂未设置名字", LoginActivity.this);
            }

            //将医生的性别保存到本地
            if (jsonObject.has("Doc_Sex")) {
                method.saveFileData("DocSex", jsonObject.getString("Doc_Sex"), LoginActivity.this);
            } else {
                method.saveFileData("DocSex", "医生暂未设置性别", LoginActivity.this);
            }

            //将医生的科室保存到本地
            if (jsonObject.has("Doc_Offices")) {
                method.saveFileData("DocOffices", jsonObject.getString("Doc_Offices"), LoginActivity.this);

            } else {
                method.saveFileData("DocOffices", "医生暂未设置科室", LoginActivity.this);
            }
            //将医生的简介保存到本地
            if (jsonObject.has("Doc_Introduce")) {
                method.saveFileData("DocIntroduce", jsonObject.getString("Doc_Introduce"), LoginActivity.this);
            } else {
                method.saveFileData("DocIntroduce", "医生暂未设置性别", LoginActivity.this);
            }
            //将医生的头像保存到本地
            PictureStore pictureStore = (PictureStore) method.readObjFromSDCard("DocIcon");
            Log.d("nsssico1", "login0" + String.valueOf(pictureStore == null));
            if (pictureStore == null) {
                pictureStore = new PictureStore();
            }
            //如果数据库有医生的头像，就把他存到本地
            if (jsonObject.has("Doc_Icon")) {
                pictureStore.setFlag(true);
                byte[] as = method.bitmap2Bytes(method.drawableToBitamp(Drawable.createFromStream(new URL(ipAddress + jsonObject.getString("Doc_Icon")).openStream(), "image.jpg")));

                pictureStore.setPicture(as);
                method.saveObj2SDCard("DocIcon", pictureStore);
            } else {
                pictureStore.setFlag(false);
                method.saveObj2SDCard("DocIcon", pictureStore);
            }
            //将医生的执照保存到本地
            pictureStore = (PictureStore) method.readObjFromSDCard("DocLicense");
            if (pictureStore == null) {
                pictureStore = new PictureStore();
            }
            if (jsonObject.has("Doc_License")) {
                pictureStore.setFlag(true);
                byte[] as = method.bitmap2Bytes(method.drawableToBitamp( Drawable.createFromStream(new URL(ipAddress + jsonObject.getString("Doc_License")).openStream(), "image.jpg")));
                pictureStore.setPicture(as);
                method.saveObj2SDCard("DocLicense", pictureStore);
            } else {
                pictureStore.setFlag(false);
                method.saveObj2SDCard("DocLicense", pictureStore);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //将用户的个人地址信息保存到本地
    private void initStuInformation() {
        String url = ipAddress + "IM/GetUserInformation?name=" + method.getFileData("ID", LoginActivity.this) + "&type=" + method.getFileData("Type", LoginActivity.this);
        Log.d("dengluurl", url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            parseJSONToStu(responseData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //解析学生信息
    private void parseJSONToStu(String jsonData) {
        Log.d("loginstu", jsonData);
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            JSONObject jsonObject = jsonArray.getJSONObject(1);
            //将医生的上班时间保存到本地
            if (jsonObject.has("Work_Time")) {
                method.saveFileData("WorkTime", jsonObject.getString("Work_Time"), LoginActivity.this);
            } else {
                method.saveFileData("WorkTime", "管理员暂未设置医生上班时间", LoginActivity.this);
            }

            //将学生stu_No存放到本地
            if (jsonObject.has("Stu_No")) {
                method.saveFileData("No", jsonObject.getString("Stu_No"), LoginActivity.this);
            } else {
                method.saveFileData("No", "用户暂未设置No", LoginActivity.this);
            }
            //将address保存到本地
            if (jsonObject.has("Stu_Address")) {
                method.saveFileData("Address", jsonObject.getString("Stu_Address"), LoginActivity.this);
            } else {
                method.saveFileData("Address", "用户暂未设置收货地址", LoginActivity.this);
            }

            //将手机号码保存到本地
            if (jsonObject.has("Stu_Phone")) {
                method.saveFileData("Phone", jsonObject.getString("Stu_Phone"), LoginActivity.this);
            } else {
                method.saveFileData("Phone", "用户暂未设置手机号码", LoginActivity.this);
            }
            //将收获人的名字保存到本地
            if (jsonObject.has("Stu_Name")) {
                method.saveFileData("Name", jsonObject.getString("Stu_Name"), LoginActivity.this);
            } else {
                method.saveFileData("Name", "用户暂未设置名字", LoginActivity.this);
            }
            //将用户的性别保存到本地
            if (jsonObject.has("Stu_Sex")) {
                method.saveFileData("Sex", jsonObject.getString("Stu_Sex"), LoginActivity.this);
            } else {
                method.saveFileData("Sex", "用户暂未设置性别", LoginActivity.this);
            }
            //将用户的身高保存到本地
            if (jsonObject.has("Stu_Height")) {
                method.saveFileData("Height", jsonObject.getString("Stu_Height"), LoginActivity.this);
            } else {
                method.saveFileData("Height", "用户暂未设置身高", LoginActivity.this);
            }
            //将用户的体重保存的本地
            if (jsonObject.has("Stu_Weight")) {
                method.saveFileData("Weight", jsonObject.getString("Stu_Weight"), LoginActivity.this);
            } else {
                method.saveFileData("Weight", "用户暂未设置体重", LoginActivity.this);
            }
            //将用户的出生年月保存到本地
            if (jsonObject.has("Stu_Birth")) {
                method.saveFileData("Birthday", jsonObject.getString("Stu_Birth"), LoginActivity.this);
            } else {
                method.saveFileData("Birthday", "用户暂未设置出生年月", LoginActivity.this);
            }
            Log.d("denglu", "1");
            //将用户的头像保存到本地
            PictureStore pictureStore = (PictureStore) method.readObjFromSDCard("Icon");
            Log.d("nsssico1", "login0" + String.valueOf(pictureStore == null));
            if (pictureStore == null) {
                pictureStore = new PictureStore();
            }
            Log.d("denglu", "2");
            if (jsonObject.has("Stu_Icon")) {
                Log.d("login.stu.icon","value:"+jsonObject.getString("Stu_Icon"));
                Log.d("denglu", "haveIcon");
                //将学生头像的url保存到本地，后续聊天的时候学生会将自己的头像url发送给医生
                method.saveFileData("StuIconUrl", jsonObject.getString("Stu_Icon"), LoginActivity.this);
                pictureStore.setFlag(true);
                Log.d("denglu", "8");
                String te = jsonObject.getString("Stu_Icon");
                Log.d("denglu", te);
                byte[] as = method.bitmap2Bytes(method.drawableToBitamp(Drawable.createFromStream(new URL(ipAddress + jsonObject.getString("Stu_Icon")).openStream(), "image.jpg")));
                Log.d("denglu", "9");
                pictureStore.setPicture(as);
                method.saveObj2SDCard("Icon", pictureStore);
                pictureStore = (PictureStore) method.readObjFromSDCard("Icon");
                Log.d("denglu", "4");
            } else {//如果数据库里没有头像
                Log.d("denglu", "no_Icon");
                pictureStore.setFlag(true);
                pictureStore.setPicture(method.bitmap2Bytes(BitmapFactory.decodeResource(getResources(),R.drawable.yonghu1)));
                method.saveObj2SDCard("Icon", pictureStore);
                //如果学生没有设置头像的话，则将头像字符串设置为null
                method.saveFileData("StuIconUrl", "null", LoginActivity.this);
                Log.d("denglu", "5");
            }
            Log.d("denglu", "3");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

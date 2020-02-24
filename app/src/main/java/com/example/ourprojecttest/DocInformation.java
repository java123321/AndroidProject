package com.example.ourprojecttest;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DocInformation extends AppCompatActivity  {
    private Dialog dialog;
    private CommonMethod method =new CommonMethod();
    private static final String TAG = "DocActivity";
    private static final int REQUEST_TAKE_PHOTO = 0;// 拍照
    private static final int REQUEST_CROP = 1;// 裁剪
    private static final int SCAN_OPEN_PHONE = 2;// 相册
    private static final int REQUEST_PERMISSION = 100;
    private boolean hasPermission = false;
    private Uri imgUri;
    private File file1,file2;
    private File imgFile;
    private Uri  mCutUri;
    private Roundimage img;
    private ImageView license;
    private Bitmap bitmap1,bitmap2;
    private TextView name,sex,title,offices,No;
    private String name1,offices1,sex1,title1,uri1,introduce1,type1,license1,no,state1;
    private EditText introduce;
    private File files;
    private Map<String,File> fileMap=new HashMap<>();
    private int q1=0,q2=0;//判断是头像点击相机还是执照点击；
    String s;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_modify);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        IntentFilter Name=new IntentFilter();
        IntentFilter Sex=new IntentFilter();
        IntentFilter Title=new IntentFilter();
        IntentFilter Offices=new IntentFilter();
        Offices.addAction("Offices");
        Title.addAction("Title");
        Sex.addAction("Sex");
        Name.addAction("Name");
        Receiver3 receiver3=new Receiver3();
        Receiver2 receiver2=new Receiver2();
        Receiver1 receiver1=new Receiver1();
        Receiver receiver=new Receiver();
        registerReceiver(receiver3,Offices);
        registerReceiver(receiver2,Title);
        registerReceiver(receiver1,Sex);
        registerReceiver(receiver,Name);
        name=findViewById(R.id.name);
        img=findViewById(R.id.picture);
        sex=findViewById(R.id.sex);
        offices=findViewById(R.id.offices);
        No=findViewById(R.id.no);
        title=findViewById(R.id.title);
        license=findViewById(R.id.license);
        introduce=findViewById(R.id.introduce);
        //设置医生执照
        PictureStore pictureStore=(PictureStore) method.readObjFromSDCard("DocLicense");
        if(pictureStore.getFlag()){
            byte[] b=pictureStore.getPicture();
            license.setImageBitmap(BitmapFactory.decodeByteArray(b,0,b.length));
        }
        //设置医生头像
        PictureStore pictureStore1=( PictureStore)method.readObjFromSDCard("DocIcon");
        if(pictureStore1.getFlag()){
            byte[] appIcon=pictureStore1.getPicture();
            img.setImageBitmap(BitmapFactory.decodeByteArray(appIcon,0,appIcon.length));
        }
        //设置医生的性别
        sex.setText(method.getFileData("DocSex",DocInformation.this));
        //设置医生姓名
        name.setText(method.getFileData("DocName",DocInformation.this));
        //设置医生职称
        title.setText(method.getFileData("DocTitle",DocInformation.this));
        //设置医生科室
        offices.setText(method.getFileData("DocOffices",DocInformation.this));
        //设置医生简介
        introduce.setText(method.getFileData("DocIntroduce",DocInformation.this));
        No.setText(method.getFileData("ID",DocInformation.this));
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                q2++;
                dialog=show();
            }
        });

        LinearLayout touxiang=findViewById(R.id.touxiang);
        LinearLayout zhicheng=findViewById(R.id.zhicheng);
        LinearLayout keshi=findViewById(R.id.keshi);
        keshi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumPicker_offices numPicker_offices=new NumPicker_offices(DocInformation.this);
                numPicker_offices.show();
                numPicker_offices.setContext(DocInformation.this);
            }
        });
        zhicheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumPicker_title num=new NumPicker_title((Activity)view.getContext());
                num.show();
                num.setContext((DocInformation.this));
            }
        });
        LinearLayout xingbie=(LinearLayout)findViewById(R.id.xingbie);
        xingbie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {   s="男";
                final View inflate = LayoutInflater.from(view.getContext()).inflate(R.layout.student_sex, null);
                dialog = new Dialog(view.getContext(), R.style.ActionSheetDialogStyle);        //展示对话框
                //填充对话框的布局
                final RadioGroup radioGroup=inflate.findViewById(R.id.radioGroup);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton rb = (RadioButton)inflate.findViewById(radioGroup.getCheckedRadioButtonId());
                        s=rb.getText().toString();
                    }
                });
                TextView queding=inflate.findViewById(R.id.quding);
                queding.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent();
                        intent.putExtra("Sex",s);
                        intent.setAction("Sex");
                        sendBroadcast(intent);
                        dialog.dismiss();
                    }
                });
                //初始化控件
                //将布局设置给Dialog
                dialog.setContentView(inflate);
                //获取当前Activity所在的窗体
                Window dialogWindow = dialog.getWindow();
                //设置Dialog从窗体底部弹出
                dialogWindow.setGravity(Gravity.BOTTOM);
                //获得窗体的属性
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.y = 20;//设置Dialog距离底部的距离
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;//弹框宽度充斥整个屏幕
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;//       将属性设置给窗体
                dialogWindow.setAttributes(lp);
                dialog.show();//显示对话框
            }
        });
        final View inflate1 = LayoutInflater.from(this).inflate(R.layout.layout_tanchuang, null);
        touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                q1++;dialog=show();
            }
        });
        LinearLayout xingming=(LinearLayout)findViewById(R.id.xingming);
        xingming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DocInformation.this,activity_doc_name.class);
                startActivity(intent);
            }
        });
    }
    public Dialog show(){
        Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_tanchuang, null);
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        TextView takephoto=inflate.findViewById(R.id.takePhoto);
        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
                if (hasPermission) {
                    takePhoto();
                }
            }
        });
        TextView choosephoto=inflate.findViewById(R.id.choosePhoto);
        choosephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
                if (hasPermission) {
                    openGallery();
                }
            }
        });
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//弹框宽度充斥整个屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
        return dialog;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SCAN_OPEN_PHONE);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查是否有存储和拍照权限
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            ) {
                hasPermission = true;
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
            } else {
                Toast.makeText(this, "权限授予失败！", Toast.LENGTH_SHORT).show();
                hasPermission = false;
            }
        }
    }

    // 拍照
    private void takePhoto() {
        // 要保存的文件名
        String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());
        String fileName = "photo_" + time;
        // 创建一个文件夹
        String path = Environment.getExternalStorageDirectory() + "/take_photo";
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }
        // 要保存的图片文件
        imgFile = new File(file, fileName + ".jpeg");
        // 将file转换成uri
        // 注意7.0及以上与之前获取的uri不一样了，返回的是provider路径
        imgUri = getUriForFile(this, imgFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 添加Uri读取权限
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        // 或者
//        grantUriPermission("com.rain.takephotodemo", imgUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 添加图片保存位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    // 图片裁剪
    private void cropPhoto(Uri uri, boolean fromCapture) {
        Intent intent = new Intent("com.android.camera.action.CROP"); //打开系统自带的裁剪图片的intent
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");

        // 注意一定要添加该项权限，否则会提示无法裁剪
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.putExtra("scale", true);

        // 设置裁剪区域的宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 设置裁剪区域的宽度和高度
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        // 取消人脸识别
        intent.putExtra("noFaceDetection", true);
        // 图片输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 若为false则表示不返回数据
        intent.putExtra("return-data", false);
        // 指定裁剪完成以后的图片所保存的位置,pic info显示有延时
        if (fromCapture) {
            // 如果是使用拍照，那么原先的uri和最终目标的uri一致,注意这里的uri必须是Uri.fromFile生成的
            mCutUri = Uri.fromFile(imgFile);
        } else { // 从相册中选择，那么裁剪的图片保存在take_photo中
            String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());
            String fileName = "photo_" + time;
            File mCutFile = new File(Environment.getExternalStorageDirectory() + "/take_photo/", fileName + ".jpeg");
            fileMap.put("ssss",mCutFile);
            if (!mCutFile.getParentFile().exists()) {
                mCutFile.getParentFile().mkdirs();
            }
            mCutUri = Uri.fromFile(mCutFile);

        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCutUri);
        Toast.makeText(this, "剪裁图片", Toast.LENGTH_SHORT).show();
        // 以广播方式刷新系统相册，以便能够在相册中找到刚刚所拍摄和裁剪的照片
        Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentBc.setData(uri);
        this.sendBroadcast(intentBc);

        startActivityForResult(intent, REQUEST_CROP); //设置裁剪参数显示图片至ImageVie
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommonMethod commonMethod=new CommonMethod();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 拍照并进行裁剪
                case REQUEST_TAKE_PHOTO:
                    Log.e(TAG, "onActivityResult: imgUri:REQUEST_TAKE_PHOTO:" + imgUri.toString());
                    cropPhoto(imgUri, true);
                    break;
                // 裁剪后设置图片
                case REQUEST_CROP:
                    try{
                    if(mCutUri!=null) {
                        if (q1 == 1) {
                            file1=uri2File(mCutUri);
                            img.setImageURI(mCutUri);
                            Intent intent=new Intent();
                            intent.setAction("Picture");
                            intent.putExtra("Picture",mCutUri.toString());
                            sendBroadcast(intent);
                            q1 = 0;
                            InputStream stream = getContentResolver().openInputStream(mCutUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(stream);
                            stream.close();
                            PictureStore pictureStore=new PictureStore();
                            pictureStore.setFlag(true);
                            byte[] as=commonMethod.bitmap2Bytes(bitmap);
                            pictureStore.setPicture(as);
                            commonMethod.saveObj2SDCard("DocIcon",pictureStore);
                        }
                        if (q2 == 1) {
                            file2=uri2File(mCutUri);
                            license.setImageURI(mCutUri);
                            q2 = 0;
                            InputStream stream = getContentResolver().openInputStream(mCutUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(stream);
                            stream.close();
                            PictureStore pictureStore=new PictureStore();
                            pictureStore.setFlag(true);
                            byte[] as=commonMethod.bitmap2Bytes(bitmap);
                            pictureStore.setPicture(as);
                            commonMethod.saveObj2SDCard("DocLicense",pictureStore);
                        }
                    }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.e(TAG, "onActivityResult: imgUri:REQUEST_CROP:" + mCutUri.toString());
                    dialog.dismiss();
                    break;
                // 打开图库获取图片并进行裁剪
                case SCAN_OPEN_PHONE:
                    Log.e(TAG, "onActivityResult: SCAN_OPEN_PHONE:" + data.getData().toString());
                    cropPhoto(data.getData(), false);
                    break;
                default:
                    break;
            }
        }
    }

    // 从file中获取uri
    // 7.0及以上使用的uri是contentProvider content://com.rain.takephotodemo.FileProvider/images/photo_20180824173621.jpg
    // 6.0使用的uri为file:///storage/emulated/0/take_photo/photo_20180824171132.jpg
    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "url", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context,Intent intent){
            String s=intent.getStringExtra("name");;
            name.setText(s);
        }
    }
    public class Receiver1 extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){
            String s=intent.getStringExtra("Sex");
            sex.setText(s);
            Toast.makeText(DocInformation.this,"保存成功",Toast.LENGTH_SHORT).show();
        }
    }
    public class Receiver2 extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){
            String s=intent.getStringExtra("Title");
            title.setText(s);
            Toast.makeText(DocInformation.this,"保存成功",Toast.LENGTH_SHORT).show();
        }
    }
    public class Receiver3 extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){
            String s=intent.getStringExtra("Offices");
            offices.setText(s);
            Toast.makeText(DocInformation.this,"保存成功",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy(){
        final CommonMethod commonMethod=new CommonMethod();
        super.onDestroy();
       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   String ID= method.getFileData("ID",getBaseContext());
                   OkHttpClient client = new OkHttpClient();
                   String name2= method.str2HexStr(name.getText().toString()).toString();
                   commonMethod.saveFileData("DocName",name.getText().toString(),getBaseContext());
                   String offices2= method.str2HexStr(offices.getText().toString());
                   commonMethod.saveFileData("DocOffice",offices.getText().toString(),getBaseContext());
                   String title2= method.str2HexStr(title.getText().toString());
                   commonMethod.saveFileData("DocTitle",title.getText().toString(),getBaseContext());
                   String s=sex.getText().toString();
                   commonMethod.saveFileData("DocSex",sex.getText().toString(),getBaseContext());
                   String sex2= method.str2HexStr(sex.getText().toString()).toString();
                   String introduce2= method.str2HexStr(introduce.getText().toString());
                   commonMethod.saveFileData("DocIntroduce",introduce.getText().toString(),getBaseContext());
                   String url=getResources().getString(R.string.ipAdrress) + "/IM/UpdateInformation?no=" + ID + "&name=" + name2+"&type="+type1+"&offices="+offices2+"&state=rest&introduce="+introduce2+"&title="+title2+"&sex="+sex2+"&isStu=false";
                   Log.d("url",url);
                   Request request = new Request.Builder().url(url)
                           .build();
                       Response response = client.newCall(request).execute();

                       String responseData = response.body().string();

               }
               catch (Exception e){
                   e.printStackTrace();
               }
           }
       }).start();
       new Thread(new Runnable() {
           @Override
           public void run() {
               method.uploadMultiFile(file1, getResources().getString(R.string.ipAdrress) + "/IM/PictureUpload?id=" + method.getFileData("ID", getBaseContext()) + "&type=Icon_Doc");
               method.uploadMultiFile(file2, getResources().getString(R.string.ipAdrress) + "/IM/PictureUpload?id=" + method.getFileData("ID", getBaseContext()) + "&type=Icon_License");
           }
       }).start();

    }
    public Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            java.net.URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;
            int length = http.getContentLength();
            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
        }
        catch (Exception e) {
            Log.d("ssss","未完成！");
            e.printStackTrace();
        }
        return bm;
    }
    private File uri2File(Uri uri) {
        String img_path;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null,
                null, null);
        if (actualimagecursor == null) {
            img_path = uri.getPath();
        }
        else {
            int actual_image_column_index = actualimagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            img_path = actualimagecursor
                    .getString(actual_image_column_index);
        }
        File file = new File(img_path);
        return file;
    }
}

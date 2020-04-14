package com.example.ourprojecttest.StuMine.StuInfomation;
import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.Utils.PictureStore;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Utils.Roundimage;
import com.example.ourprojecttest.StuMine.NumPicker;
import com.example.ourprojecttest.StuMine.Tubiao;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 该项目主要用于拍照练习
 * 适配安卓7.0及以上
 */
public class StuInformation extends AppCompatActivity implements View.OnClickListener {
    private Display display;
    private int toastHeight;
    private String ipAddress;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_TAKE_PHOTO = 0;// 拍照
    private static final int REQUEST_CROP = 1;// 裁剪
    private static final int SCAN_OPEN_PHONE = 2;// 相册
    private static final int REQUEST_PERMISSION = 100;
    private Roundimage img;
    private RecyclerView recyclerView;
    private Uri imgUri; // 拍照时返回的uri
    private Uri mCutUri;// 图片裁剪时返回的uri
    private boolean hasPermission = false;
    private File imgFile;// 拍照保存的图片文件
    private List<Tubiao> aa = new ArrayList<>();
    private ModifyAdapter adapter;
    private File file;
    private String Birthday;
    private String age1;
    private CommonMethod commonMethod=new CommonMethod();
    private Receiver v;
    private Receiver1 mm;
    private Receiver2 receiver2;
    private Receiver3 receiver3;
    private Receiver4 receiver4;
    private Dialog dialog;
    private NumPicker numPicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        ipAddress=getResources().getString(R.string.ipAdrress);
        display = getWindowManager().getDefaultDisplay();
        toastHeight = display.getHeight();
        IntentFilter intentFilter = new IntentFilter();
        IntentFilter sex1 = new IntentFilter();
        IntentFilter weight1 = new IntentFilter();
        IntentFilter height1 = new IntentFilter();
        IntentFilter bithday1=new IntentFilter();
        bithday1.addAction("Birth");
        intentFilter.addAction("Name");
        sex1.addAction("Sex");
        weight1.addAction("Weight");
        height1.addAction("Height");
        v = new Receiver();
        mm = new Receiver1();
        receiver2 = new Receiver2();
        receiver3 = new Receiver3();
        receiver4=new Receiver4();
        registerReceiver(receiver4,bithday1);
        registerReceiver(receiver2, height1);
        registerReceiver(v, intentFilter);
        registerReceiver(mm, sex1);
        registerReceiver(receiver3, weight1);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        recyclerView =  findViewById(R.id.recycle_view);
        LinearLayout layout =  findViewById(R.id.layout);
        img =  findViewById(R.id.roundViw);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = show();
            }
        });
        LayoutInflater m = LayoutInflater.from(this);
        numPicker = new NumPicker(this);
        checkPermissions();
        StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        String weight = commonMethod.getFileData("Weight",getBaseContext())+ " 千克";
        String name = commonMethod.getFileData("Name",getBaseContext());
        String email =commonMethod.getFileData("ID",getBaseContext());
        String sex = commonMethod.getFileData("Sex",getBaseContext());
        String height = commonMethod.getFileData("Height",getBaseContext()) + " 厘米";
        Birthday = commonMethod.getFileData("Birthday",getBaseContext());
        String [] arrs=new String[3];
        arrs=Birthday.split("-");
        age1=getAge(arrs[0],arrs[1],arrs[2]);
        PictureStore pictureStore=( PictureStore)commonMethod.readObjFromSDCard("Icon");
        if(pictureStore.getFlag()){
            byte[] appIcon=pictureStore.getPicture();
            img.setImageBitmap(BitmapFactory.decodeByteArray(appIcon,0,appIcon.length));
        }
        initTubiao(name, email, sex, height, weight, age1);
        adapter = new ModifyAdapter();
        adapter.setList(aa);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhoto:
                checkPermissions();
                if (hasPermission) {
                    takePhoto();
                }
                break;
            case R.id.choosePhoto:
                checkPermissions();
                if (hasPermission) {
                    openGallery();
                }
                break;
            case R.id.quding:

                Toast toast = Toast.makeText(StuInformation.this, "jjdf！", Toast.LENGTH_SHORT);
                // 这里给了一个1/4屏幕高度的y轴偏移量
                toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                toast.show();
            default:
                break;
        }
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

                Toast toast = Toast.makeText(StuInformation.this, "权限授予失败！", Toast.LENGTH_SHORT);
                // 这里给了一个1/4屏幕高度的y轴偏移量
                toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                toast.show();
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
            if (!mCutFile.getParentFile().exists()) {
                mCutFile.getParentFile().mkdirs();
            }
            mCutUri = Uri.fromFile(mCutFile);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCutUri);

        Toast toast = Toast.makeText(StuInformation.this, "剪裁图片！", Toast.LENGTH_SHORT);
        // 这里给了一个1/4屏幕高度的y轴偏移量
        toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
        toast.show();

        // 以广播方式刷新系统相册，以便能够在相册中找到刚刚所拍摄和裁剪的照片
        Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentBc.setData(uri);
        this.sendBroadcast(intentBc);
        startActivityForResult(intent, REQUEST_CROP); //设置裁剪参数显示图片至ImageVie
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 拍照并进行裁剪
                case REQUEST_TAKE_PHOTO:
                    Log.e(TAG, "onActivityResult: imgUri:REQUEST_TAKE_PHOTO:" + imgUri.toString());
                    cropPhoto(imgUri, true);
                    break;
                // 裁剪后设置图片
                case REQUEST_CROP:
                    try {

                        if (mCutUri != null)
                            img.setImageURI(mCutUri);
                        Intent intent=new Intent();
                        intent.putExtra("Picture",mCutUri.toString());
                        intent.setAction("Picture");
                        sendBroadcast(intent);

                        Toast toast = Toast.makeText(StuInformation.this, "头像更改成功！", Toast.LENGTH_SHORT);
                        // 这里给了一个1/4屏幕高度的y轴偏移量
                        toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
                        toast.show();
                        dialog.dismiss();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
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
            uri = FileProvider.getUriForFile(context.getApplicationContext(),"url", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public Dialog show() {
        Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_tanchuang, null);
        //初始化控件
        TextView choosePhoto = inflate.findViewById(R.id.choosePhoto);
        TextView takePhoto =  inflate.findViewById(R.id.takePhoto);
        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
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
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
        return dialog;
    }
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getStringExtra("name");
            aa.remove(0);
            Tubiao n = new Tubiao("姓名", s, R.drawable.jiantou);
            aa.add(0, n);
            adapter.setList(aa);
            adapter.notifyDataSetChanged();

            Toast toast = Toast.makeText(StuInformation.this, "保存成功！", Toast.LENGTH_SHORT);
            // 这里给了一个1/4屏幕高度的y轴偏移量
            toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
            toast.show();
        }
    }


    public class Receiver1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getStringExtra("sex");
            aa.remove(2);
            Tubiao n = new Tubiao("性别", s, R.drawable.jiantou);
            aa.add(2, n);
            adapter.setList(aa);
            adapter.notifyDataSetChanged();

            Toast toast = Toast.makeText(StuInformation.this, "保存成功！", Toast.LENGTH_SHORT);
            // 这里给了一个1/4屏幕高度的y轴偏移量
            toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
            toast.show();

        }
    }

    public class Receiver2 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getStringExtra("Height");
            aa.remove(3);
            Tubiao n = new Tubiao("身高", s, R.drawable.jiantou);
            aa.add(3, n);
            adapter.setList(aa);
            adapter.notifyDataSetChanged();

            Toast toast = Toast.makeText(StuInformation.this, "保存成功！", Toast.LENGTH_SHORT);
            // 这里给了一个1/4屏幕高度的y轴偏移量
            toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
            toast.show();
        }
    }

    public class Receiver3 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getStringExtra("Weight");
            aa.remove(4);
            Tubiao n = new Tubiao("体重", s, R.drawable.jiantou);
            aa.add(4, n);
            adapter.setList(aa);
            adapter.notifyDataSetChanged();

            Toast toast = Toast.makeText(StuInformation.this, "保存成功！", Toast.LENGTH_SHORT);
            // 这里给了一个1/4屏幕高度的y轴偏移量
            toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
            toast.show();
        }
    }

    public class Receiver4 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar cal = Calendar.getInstance();
            String mYear = intent.getStringExtra("mYear");
            String mMonth = intent.getStringExtra("mMonth");
            String mDay=intent.getStringExtra("mDay");
            Birthday=mYear+"-"+mMonth+"-"+mDay;
            String age=getAge(mYear,mMonth,mDay);
            aa.remove(5);
            Tubiao n = new Tubiao("年龄",age  , R.drawable.jiantou);
            aa.add(5, n);
            adapter.setList(aa);
            adapter.notifyDataSetChanged();

            Toast toast = Toast.makeText(StuInformation.this, "保存成功！", Toast.LENGTH_SHORT);
            // 这里给了一个1/4屏幕高度的y轴偏移量
            toast.setGravity(Gravity.BOTTOM,0,toastHeight/5);
            toast.show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    private void initTubiao(String name, String email, String sex, String height, String weight, String age) {
        Tubiao name1 = new Tubiao("姓名", name, R.drawable.jiantou);
        aa.add(name1);
        Tubiao email1 = new Tubiao("邮箱", email, R.drawable.jiantou);
        aa.add(email1);
        Tubiao sex2 = new Tubiao("性别", sex, R.drawable.jiantou);
        aa.add(sex2);
        Tubiao height2 = new Tubiao("身高", height, R.drawable.jiantou);
        aa.add(height2);
        Tubiao weight2 = new Tubiao("体重", weight, R.drawable.jiantou);
        aa.add(weight2);
        Tubiao age2 = new Tubiao("年龄",age, R.drawable.jiantou);
        aa.add(age2);
        Log.d("sss", aa.size() + "");
    }

    private void uploadInfo(String ID,String name,String sex,String Birthday,String height,String weight){

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("no=")//notPost是学生待付款的订单
                .append(ID+"&name=")
                .append(name+"&sex=")
                .append(sex+"&birth=")
                .append(Birthday+"&height=")
                .append(height+"&weight=")
                .append(weight+"&isStu=true");
        byte[] data = stringBuffer.toString().getBytes();
        String strUrl = ipAddress + "IM/UpdateInformation";
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
            } else {

            }
            Log.d("result", "312");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(v);
        unregisterReceiver(mm);
        unregisterReceiver(receiver2);
        unregisterReceiver(receiver3);
        unregisterReceiver(receiver4);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    file = uri2File(mCutUri);
                    InputStream stream = getContentResolver().openInputStream(mCutUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    stream.close();
                    PictureStore pictureStore=new PictureStore();
                    pictureStore.setFlag(true);
                    byte[] as=commonMethod.bitmap2Bytes(bitmap);
                    pictureStore.setPicture(as);
                    commonMethod.saveObj2SDCard("Icon",pictureStore);
                    commonMethod.uploadMultiFile(file, ipAddress + "/IM/PictureUpload?id=" + commonMethod.getFileData("ID", getBaseContext()) + "&type=Icon_Stu");

                }
                catch (Exception e){
                    e.printStackTrace();http://139.196.103.219:8080/IM/PictureUpload?id=12345&type=Drug_stu"
                    Log.d("sssss","上传失败！");
                }
                String ID = commonMethod.getFileData("ID", StuInformation.this);
                Tubiao s =  aa.get(0);
                commonMethod.saveFileData("Birthday",Birthday,getBaseContext());
                String name = s.getXinxi();
                commonMethod.saveFileData("Name",s.getXinxi(),getBaseContext());
                Tubiao s1 =  aa.get(2);
                String sex = s1.getXinxi();
                commonMethod.saveFileData("Sex",s1.getXinxi(),getBaseContext());
                Tubiao s2 =  aa.get(3);
                String height1 = s2.getXinxi();
                String[] arr = height1.split(" ");
                String height = arr[0];
                commonMethod.saveFileData("Height",height,getBaseContext());
                Tubiao s3 =  aa.get(4);
                String weight1 = s3.getXinxi();                             //将修改后的数据回传给服务器
                String[] ars = weight1.split(" ");
                String weight = ars[0];
                commonMethod.saveFileData("Weight",weight,getBaseContext());
//                    OkHttpClient client = new OkHttpClient();
//                    Log.d("保存日期",Birthday);
//                    Request request = new Request.Builder().url(ipAddress + "/IM/UpdateInformation?no=" + ID + "&name=" + name + "&sex=" + sex + "&birth=" + Birthday + "&height=" + height + "&weight=" + weight+"&isStu=true")
//                            .build();
//                    Response response = client.newCall(request).execute();
//                    String responseData = response.body().string();
                    uploadInfo(ID,name,sex,Birthday,height,weight);
            }
        }).start();
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
    public  String getAge(String year,String month,String day){
        Calendar cal = Calendar.getInstance();
        int Year=Integer.parseInt(year);
        int Month=Integer.parseInt(month);
        int Day=Integer.parseInt(day);
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow -Year;   //计算整岁数
        if (monthNow <=Month ) {
            if (monthNow == Month) {
                if (dayOfMonthNow < Day)
                    age--;//当前日期在生日之前，年龄减一
            } else {
                age--;//当前月份在生日之前，年龄减一
            }
        }
        if(age<0)
            age=0;
        return Integer.toString(age);
    }
}



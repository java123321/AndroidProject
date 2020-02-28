package com.example.ourprojecttest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourprojecttest.StuMine.ShoppingCart.ShoppingCartBean;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.blankj.utilcode.util.UriUtils.uri2File;

public class UpDrugMsgActivity extends AppCompatActivity implements View.OnClickListener{

    private final int UPPictureFinished=2;
    public Uri url;
    public String path ;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    private Uri imageUri;
    public TextView drug_name;
    public TextView drug_num;
    public TextView drug_resume;
    public TextView drug_price;
    public TextView show;
    public Button submit;
    public Spinner kind;
    public Spinner attribute;
    public String addOrup;
    private boolean hasPermission = false;
    private static final int REQUEST_TAKE_PHOTO = 0;// 拍照
    private static final int REQUEST_CROP = 1;// 裁剪
    private static final int SCAN_OPEN_PHONE = 2;// 相册
    private static final int REQUEST_PERMISSION = 100;
    private Uri imgUri; // 拍照时返回的uri
    private Uri mCutUri;// 图片裁剪时返回的uri
    private File imgFile;// 拍照保存的图片文件
    private static final String TAG = "MainActivity";
    Dialog dialog;
    String fileName = "test";
    File file;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case UPPictureFinished:


                case 0:
                    //成功
                    new AlertDialog.Builder(UpDrugMsgActivity.this).setTitle("正确").setMessage("成功").setNegativeButton("确定",null).show();
                    break;
                case -1:
                    //失败
                    new AlertDialog.Builder(UpDrugMsgActivity.this).setTitle("错误").setMessage("失败").setNegativeButton("确定",null).show();
                default:
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_drug_manage);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        submit =  findViewById(R.id.add);
        kind =  findViewById(R.id.drug_kind);
        attribute = findViewById(R.id.drug_attribyte);
        show = findViewById(R.id.show);
        drug_name =  findViewById(R.id.drug_name);
        drug_price =  findViewById(R.id.drug_price);
        drug_num = findViewById(R.id.drug_num);
        drug_resume =  findViewById(R.id.drug_msg);
        picture =  findViewById(R.id.picture);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog= show();
            }
        });

        if (getIntent().getStringExtra("adjust").trim().equals("0"))
        {
            show.setText("添加药品");
            addOrup = "UploadDrug";
        }
        else {
            Intent intent=getIntent();
            drug_num.setText(intent.getStringExtra("amount").trim());
            drug_name.setText(intent.getStringExtra("drugName").trim());
            drug_price.setText(intent.getStringExtra("drugPrice").trim());
            drug_resume.setText(intent.getStringExtra("drugDescription").trim());
            byte[] appIcon=intent.getByteArrayExtra("drugPicture");
            picture.setImageBitmap(BitmapFactory.decodeByteArray(appIcon,0,appIcon.length));
            show.setText("修改药品");
            addOrup = "UpdateDrugInformation";
            createFileWithByte(appIcon);
        }

//        if (StuId.stuId == "")
//        {
//            my_order.setVisibility(View.GONE);
//            add_shopping_car.setVisibility(View.GONE);
//        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String attribute_s = attribute.getSelectedItem().toString().trim();
                final String kind_s = kind.getSelectedItem().toString().trim();
                final String drug_name_s = drug_name.getText().toString().trim();
                final String drug_price_s = drug_price.getText().toString().trim();
                final String drug_num_s = drug_num.getText().toString().trim();
                final String describe = drug_resume.getText().toString().trim();
                boolean flag = true;
                if (drug_name_s.isEmpty()||drug_num_s.isEmpty()||drug_price_s.isEmpty()||path==""||describe.isEmpty()){
                    new AlertDialog.Builder(UpDrugMsgActivity.this).setTitle("错误").setMessage("请完善信息").setNegativeButton("确定",null).show();
                    flag = false;
                }else if (!check_num(drug_num_s)&&flag){
                    new AlertDialog.Builder(UpDrugMsgActivity.this).setTitle("错误").setMessage("请填入数字").setNegativeButton("确定",null).show();
                    flag = false;
                }else if (!check_price(drug_price_s)&&flag){
                    new AlertDialog.Builder(UpDrugMsgActivity.this).setTitle("错误").setMessage("请填入正确价格 ").setNegativeButton("确定",null).show();
                }
                //
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadDrugInfo(file,getResources().getString(R.string.ipAdrress)+"IM/PictureUpload?type=Drug",drug_name_s,drug_price_s,kind_s,describe,drug_num_s,attribute_s);
                    }
                }).start();
            }
        });
    }
    public void onClick(View v) {}
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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SCAN_OPEN_PHONE);
    }

    private void createFileWithByte(byte[] bytes) {
        // TODO Auto-generated method stub
        /**
         * 创建File对象，其中包含文件所在的目录以及文件的命名
         */
        file = new File(Environment.getExternalStorageDirectory(),
                fileName);
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 如果文件存在则删除
            if (file.exists()) {
                file.delete();
            }
            // 在文件系统中根据路径创建一个新的空文件
            file.createNewFile();
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
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
        Toast.makeText(this, "剪裁图片", Toast.LENGTH_SHORT).show();
        // 以广播方式刷新系统相册，以便能够在相册中找到刚刚所拍摄和裁剪的照片
        Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentBc.setData(uri);
        this.sendBroadcast(intentBc);
        startActivityForResult(intent, REQUEST_CROP); //设置裁剪参数显示图片至ImageVie
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
                            file = uri2File(mCutUri);
                        picture.setImageURI(mCutUri);
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

    private void parseJSONWithJSONObject(String jsonData){
        try{
            Log.d("updrugjson",jsonData);
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
    /**
     * 该方法用于上传文件
     * @param file 要上传的文件对象
     * @param url  需要访问的url地址
     *
     */
    public void uploadDrugInfo(File file, String url, final String  drug_name_s, final String drug_price_s, final String kind_s, final String describe, final String drug_num_s, final String attribute_s) {
        Log.d("updrugurl1",url);
        String imageType = "multipart/form-data";
        //File file = new File(imgUrl);//imgUrl为图片位置
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fileName", "head_image.jpg", fileBody)
                .addFormDataPart("imagetype", imageType)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpBuilder
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
                                                  @Override
                                                  public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                      Log.d("result","falure");
                                                      System.out.println("失败");
                                                  }
                                                  @Override
                                                  public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                      String htmlStr = response.body().string();
                                                      Log.d("result0", htmlStr);
                                                      String chart = htmlStr.substring(htmlStr.indexOf("h"), htmlStr.indexOf("j"));
                                                      chart = chart+"jpg";
                                                      //上传图片成功并获取图片的地址之后开始上传药品的文本信息
                                                      String url;
                                                      url=getResources().getString(R.string.ipAdrress)+"IM/"+addOrup+"?name="+drug_name_s+"&price="+drug_price_s+"&type="+kind_s+"&describe="+describe+"&amount="+drug_num_s+"&index="+chart+"&attribute="+attribute_s;
                                                      Log.d("updrugurl",url);
                                                      OkHttpClient client = new OkHttpClient();
                                                      Request request = new Request.Builder()
                                                              .url(url)
                                                              .build();
                                                      try {
                                                          Response responseText = client.newCall(request).execute();
                                                          String responseData = responseText.body().string();
                                                          parseJSONWithJSONObject(responseData);
                                                      } catch (Exception e) {
                                                          e.printStackTrace();
                                                      }
                                                  }
                                              }
        );
    }

    public boolean check_num(String num){
        String regex="^\\d+$";
        if(num.matches(regex)==true)
            return true;
        else
            return false;

    }
    public boolean check_price(String price) {
        String re1 = "^[0-9]+\\.[0-9]{0,2}$";
        String re2 = "^\\d+$";
        if (price.matches(re1)||price.matches(re2))
            return true;
        else
            return false;
    }
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grandResults) {
        switch (requestCode) {
            case 1:
                if (grandResults.length>0 && grandResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                } else {
                    Toast.makeText(this,"you denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    /**
     * bitmap转化成byte数组
     * @param bm 需要转换的Bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private String saveBitmap(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory() + "/image");
        if (!file.exists())
            file.mkdirs();
        File imgFile = new File(file.getAbsolutePath() + ".jpg");
        if (imgFile.exists())
            imgFile.delete();
        try {
            FileOutputStream outputStream = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            outputStream.flush();
            outputStream.close();
            Uri uri = Uri.fromFile(imgFile);
            return uri.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * drawable转化成bitmap的方法
     * @param drawable 需要转换的Drawable
     */
    public static Bitmap drawableToBitamp(Drawable drawable) {
        Bitmap bitmap;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        //System.out.println("Drawable转Bitmap");
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    @TargetApi(28)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri,String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            path = imagePath;
            picture.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"filed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    public Dialog show() {
        Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_tanchuang, null);
        //初始化控件
        TextView choosePhoto = inflate.findViewById(R.id.choosePhoto);
        TextView takePhoto =  inflate.findViewById(R.id.takePhoto);
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
                if (hasPermission) {
                    openGallery();
                }
            }
        });
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
                if (hasPermission) {
                    takePhoto();
                }
//                if (ContextCompat.checkSelfPermission(UpDrugMsgActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//                    ActivityCompat.requestPermissions(UpDrugMsgActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//                }else {
//                    openAlbum();
//                }
            }
        });
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

    /**
     * 读取sd卡对象
     *
     * @param fileName 文件名
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList<ShoppingCartBean> readListFromSdCard(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  //检测sd卡是否存在
            ArrayList<ShoppingCartBean> list;
            File sdCardDir = Environment.getExternalStorageDirectory();
            File sdFile = new File(sdCardDir, fileName);
            try {
                FileInputStream fis = new FileInputStream(sdFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                list = (ArrayList<ShoppingCartBean>) ois.readObject();
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
}

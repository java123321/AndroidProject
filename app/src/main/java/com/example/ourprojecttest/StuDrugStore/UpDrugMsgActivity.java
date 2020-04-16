package com.example.ourprojecttest.StuDrugStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.Display;
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

import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.StuMine.ShoppingCart.ShoppingCartBean;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

public class UpDrugMsgActivity extends AppCompatActivity {
    private final int MODEFY_DRUG_SUCCESS=12;
    private final int MODEFY_DRUG_FAULT=13;
    private final int FAULT_DUE_NET=15;
    private final int UPLOAD_DRUG_SUCCESS = 10;
    private final int UPLOAD_DRUG_FAULT = 11;
    private final int DELETE_SUCCESS = 5;
    private final int DELETE_FAULT = 6;
    private Button deleteOrder;
    private Display display;
    private int toastHeight;
    private String ipAddress;
    private String path;
    private static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    private TextView drug_name;
    private TextView drug_num;
    private TextView drug_resume;
    private TextView drug_price;
    private TextView show;
    private Button submit;
    private Spinner kind;
    private Spinner attribute;
    private String addOrup;
    private boolean hasPermission = false;
    private static final int REQUEST_TAKE_PHOTO = 0;// 拍照
    private static final int REQUEST_CROP = 1;// 裁剪
    private static final int SCAN_OPEN_PHONE = 2;// 相册
    private static final int REQUEST_PERMISSION = 100;
    private Uri imgUri; // 拍照时返回的uri
    private Uri mCutUri;// 图片裁剪时返回的uri
    private File imgFile;// 拍照保存的图片文件
    private static final String TAG = "MainActivity";
    private Dialog dialog;
    private String fileName = "test";
    private File file;
    private String drugId;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("updatedrug", "what:" + msg.what);
            switch (msg.what) {
                case DELETE_SUCCESS: {//代表药品删除成功
                    String s = "该药品已成功删除！";
                    show(R.layout.layout_chenggong, s, 1);
                    break;
                }
                case DELETE_FAULT: {//代表药品删除失败
//                    Toast toast = Toast.makeText(UpDrugMsgActivity.this, "药品删除失败，请稍后再试！", Toast.LENGTH_SHORT);
//                case DELETE_FAULT: {
                   // Toast toast = Toast.makeText(UpDrugMsgActivity.this, "药品删除失败，请稍后再试！", Toast.LENGTH_SHORT);
                    // 这里给了一个1/4屏幕高度的y轴偏移量
                    //toast.setGravity(Gravity.BOTTOM, 0, toastHeight / 5);
                    //toast.show();
                    String s = "药品删除失败！";
                    show(R.layout.layout_tishi_email, s, 0);
                    break;
                }
                case UPLOAD_DRUG_SUCCESS:{//代表药品上传成功
                    String s1 = "操作成功";
                    show(R.layout.layout_chenggong, s1, 1);
                    break;
                }
                case UPLOAD_DRUG_FAULT:{//代表药品上传失败
                    String s = "失败";
                    show(R.layout.layout_tishi_email, s, 0);
                    break;
                }
                case MODEFY_DRUG_SUCCESS:{//代表药品修改信息成功
                    //阿边在这里弹出提示框通知用户药品信息修改成功
                    //将toast换成确认框即可，toast为了方便测试
                    Toast.makeText(UpDrugMsgActivity.this, "修改信息成功", Toast.LENGTH_SHORT).show();
                    break;
                }
                case MODEFY_DRUG_FAULT:{//代表药品修改信息失败
                    //同理，弹出通知提示用户修改药品失败
                    Toast.makeText(UpDrugMsgActivity.this, "修改信息失败", Toast.LENGTH_SHORT).show();
                    break;
                }
                case FAULT_DUE_NET:{//由于网络原因造成的修改或上传药品失败
                    //同理，弹出通知提示用户稍后再次尝试
                    Toast.makeText(UpDrugMsgActivity.this, "失败，请稍后尝试", Toast.LENGTH_SHORT).show();
                }
                default:
                    break;
            }
        }
    };

    //该方法用于删除数据库中的药品
    private void deleteOrder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = ipAddress + "IM/DeleteDrug?drugId=" + drugId;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Log.d("updrugmsg.delete.url", url);
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string().trim();
                    Log.d("denglu", "resdata:" + responseData);
                    Message msg = handler.obtainMessage();
                    if (responseData.equals("删除成功")) {
                        msg.what = DELETE_SUCCESS;
                    } else {
                        msg.what = DELETE_FAULT;
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_drug_manage);
        ipAddress = getResources().getString(R.string.ipAdrress);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        deleteOrder = findViewById(R.id.delete);
        //设置删除药品的点击事件
        deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(UpDrugMsgActivity.this, R.style.ActionSheetDialogStyle);        //展示对话框
                //填充对话框的布局
                View inflate = LayoutInflater.from(UpDrugMsgActivity.this).inflate(R.layout.layout_delete_yaopin, null);
                TextView no = inflate.findViewById(R.id.no);
                TextView yes = inflate.findViewById(R.id.yes);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteOrder();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(inflate);
                dialog.setCancelable(false);
                Window dialogWindow = dialog.getWindow();
                //设置Dialog从窗体底部弹出
                dialogWindow.setGravity(Gravity.CENTER);
                //获得窗体的属性
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = 800;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(lp);
                dialog.show();
            }
        });
        display = getWindowManager().getDefaultDisplay();
        toastHeight = display.getHeight();
        submit = findViewById(R.id.add);
        kind = findViewById(R.id.drug_kind);
        attribute = findViewById(R.id.drug_attribyte);
        show = findViewById(R.id.show);
        drug_name = findViewById(R.id.drug_name);
        drug_price = findViewById(R.id.drug_price);
        drug_num = findViewById(R.id.drug_num);
        drug_resume = findViewById(R.id.drug_msg);
        picture = findViewById(R.id.picture);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = show();
            }
        });

        if (getIntent().getStringExtra("adjust").trim().equals("0")) {//添加药品
            show.setText("添加药品");
            addOrup = "UploadDrug";
        } else {
            Intent intent = getIntent();
            drugId = intent.getStringExtra("drugId").trim();
            drug_num.setText(intent.getStringExtra("amount").trim());
            drug_name.setText(intent.getStringExtra("drugName").trim());
            drug_price.setText(intent.getStringExtra("drugPrice").trim());
            drug_resume.setText(intent.getStringExtra("drugDescription").trim());
            byte[] appIcon = intent.getByteArrayExtra("drugPicture");
            picture.setImageBitmap(BitmapFactory.decodeByteArray(appIcon, 0, appIcon.length));
            show.setText("修改药品");
            addOrup = "UpdateDrugInformation";
            createFileWithByte(appIcon);
        }
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
                if (drug_name_s.isEmpty() || drug_num_s.isEmpty() || drug_price_s.isEmpty() || path == "" || describe.isEmpty()) {
                    String s1 = "请完善信息";
                    show(R.layout.layout_tishi_email, s1, 0);
                    //new AlertDialog.Builder(UpDrugMsgActivity.this).setTitle("错误").setMessage("请完善信息").setNegativeButton("确定", null).show();
                } else if (!check_num(drug_num_s) && flag) {
                    //new AlertDialog.Builder(UpDrugMsgActivity.this).setTitle("错误").setMessage("请填入数字").setNegativeButton("确定", null).show();
                    String s1 = "请输入数字";
                    show(R.layout.layout_tishi_email, s1, 0);
                } else if (!check_price(drug_price_s) && flag) {
                    //new AlertDialog.Builder(UpDrugMsgActivity.this).setTitle("错误").setMessage("请填入正确价格 ").setNegativeButton("确定", null).show();
                    String s1 = "请输入正确价格";
                    show(R.layout.layout_tishi_email, s1, 0);
                }
                //
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadDrugInfo(file, ipAddress + "IM/PictureUpload?type=Drug", drug_name_s, drug_price_s, kind_s, describe, drug_num_s, attribute_s);
                    }
                }).start();
            }
        });
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
        Toast toast = Toast.makeText(UpDrugMsgActivity.this, "剪裁图片！", Toast.LENGTH_SHORT);
        // 这里给了一个1/4屏幕高度的y轴偏移量
        toast.setGravity(Gravity.BOTTOM, 0, toastHeight / 5);
        toast.show();
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
                    } catch (Exception e) {
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

//    private void parseJSONWithJSONObject(String jsonData) {
//        try {
//            Log.d("updrugjson", jsonData);
//            JSONObject jsonObject = new JSONObject(jsonData);
//            String code = jsonObject.getString("code");
//            Message msg = Message.obtain();
//
//            if (code.equals("0")) {
//                msg.what = 0;
//
//            } else {
//                msg.what = -1;
//            }
//            handler.sendMessage(msg);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 该方法用于上传文件
     *
     * @param file 要上传的文件对象
     * @param url  需要访问的url地址
     */
    public void uploadDrugInfo(File file, String url, final String drug_name_s, final String drug_price_s, final String kind_s, final String describe, final String drug_num_s, final String attribute_s) {
        Log.d("updrugurl1", url);
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
                                                      Log.d("result", "falure");
                                                      System.out.println("失败");
                                                  }
                                                  @Override
                                                  public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                      String pictureUrl = ParseJSON(response.body().string());
                                                      //上传图片成功并获取图片的地址之后开始上传药品的文本信息
                                                      upDrugTextInfo(drug_name_s,drug_price_s,kind_s,describe,drug_num_s,pictureUrl,attribute_s);

//                                                      String url;
//                                                      url = ipAddress + "IM/" + addOrup + "?name=" + drug_name_s + "&price=" + drug_price_s + "&type=" + kind_s + "&describe=" + describe + "&amount=" + drug_num_s + "&index=" + pictureUrl + "&attribute=" + attribute_s;
//                                                      Log.d("updrugurl", url);
//                                                      OkHttpClient client = new OkHttpClient();
//                                                      Request request = new Request.Builder()
//                                                              .url(url)
//                                                              .build();
//                                                      try {
//                                                          Response responseText = client.newCall(request).execute();
//                                                          String responseData = responseText.body().string();
//                                                          parseJSONWithJSONObject(responseData);
//                                                      } catch (Exception e) {
//                                                          e.printStackTrace();
//                                                      }
                                                  }
                                              }
        );
    }


    private void upDrugTextInfo(String drug_name_s, String drug_price_s, String kind_s, String describe, String drug_num_s, String pictureUrl, String attribute_s) {
        StringBuilder info=new StringBuilder();
         info.append("name=").append(drug_name_s ).append("&price=").append(drug_price_s).append("&type=").append(kind_s ).append("&describe=").append(describe).append("&amount=").append(drug_num_s).append("&index=").append(pictureUrl ).append("&attribute=").append(attribute_s);
         if(addOrup.equals("UpdateDrugInformation")){//如果是修改药品信息
             info.append("&drugId=").append(drugId);
         }

        byte[] data = info.toString().getBytes();
        try {
            URL url = new URL(ipAddress + "IM/" + addOrup);
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
                String resultData = new String(byteArrayOutputStream.toByteArray()).trim();

                if (resultData.equals("添加成功")) {
                    msg.what = UPLOAD_DRUG_SUCCESS;
                    Log.d("result", "success3");
                } else if(resultData.equals("添加失败")){
                    msg.what = UPLOAD_DRUG_FAULT;
                    Log.d("result", "fault1");
                }else if(resultData.equals("修改成功")){
                    msg.what=MODEFY_DRUG_SUCCESS;
                }else if(resultData.equals("修改失败")){
                    msg.what=MODEFY_DRUG_FAULT;
                }
            } else {
                msg.what = FAULT_DUE_NET;
                Log.d("result", "fault2");
            }
            handler.sendMessage(msg);
            Log.d("result", "312");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private String ParseJSON(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return jsonObject.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean check_num(String num) {
        String regex = "^\\d+$";
        if (num.matches(regex) == true)
            return true;
        else
            return false;

    }

    public boolean check_price(String price) {
        String re1 = "^[0-9]+\\.[0-9]{0,2}$";
        String re2 = "^\\d+$";
        if (price.matches(re1) || price.matches(re2))
            return true;
        else
            return false;
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandResults) {
        switch (requestCode) {
            case 1:
                if (grandResults.length > 0 && grandResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {

                    Toast toast = Toast.makeText(UpDrugMsgActivity.this, "you denied the permission！", Toast.LENGTH_SHORT);
                    // 这里给了一个1/4屏幕高度的y轴偏移量
                    toast.setGravity(Gravity.BOTTOM, 0, toastHeight / 5);
                    toast.show();
                }
                break;
            default:
        }
    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
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
        } else {

            Toast toast = Toast.makeText(UpDrugMsgActivity.this, "filed to get image！", Toast.LENGTH_SHORT);
            // 这里给了一个1/4屏幕高度的y轴偏移量
            toast.setGravity(Gravity.BOTTOM, 0, toastHeight / 5);
            toast.show();
        }
    }

    public Dialog show() {
        Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_tanchuang, null);
        //初始化控件
        TextView choosePhoto = inflate.findViewById(R.id.choosePhoto);
        TextView takePhoto = inflate.findViewById(R.id.takePhoto);
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

    public void show(int x, String s, int y) {
        final Dialog dialog = new Dialog(UpDrugMsgActivity.this, R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(UpDrugMsgActivity.this).inflate(x, null);
        TextView describe = inflate.findViewById(R.id.describe);
        describe.setText(s);
        TextView yes = inflate.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (y == 0)
                    dialog.dismiss();
                else
                    finish();
            }
        });
        dialog.setContentView(inflate);
        dialog.setCancelable(false);
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }
}

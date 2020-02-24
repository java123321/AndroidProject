package com.example.ourprojecttest;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Fragment;

import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class FragmentOfDrugManage extends Fragment implements View.OnClickListener  {
    private ImageView picture;
    private Button takephoto;
    private Button add;
    private Button choose;
    private TextView drug_name;
    private TextView drug_price;
    private TextView drug_num;
    private TextView drug_kind;
    private EditText drug_msg;
    String filePath;
    /**     *这个方法可以进行适配器和布局的绑定，初始化Item点击监听     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contain, Bundle savedInstanceState) {
        //这里获取要替换掉FrameLayout的布局fragment_knowledge.xml
        Log.d("ooo","成功拍");
        View view = inflater.inflate(R.layout.doc_drug_manage,contain,false);
        Log.d("ooo","成功拍");
       /* picture = view.findViewById(R.id.picture);
        takephoto = view.findViewById(R.id.take_photo);
        choose = view.findViewById(R.id.chooseFromAlbum);
        add = view.findViewById(R.id.add);
        drug_name = view.findViewById(R.id.drug_name);
        drug_kind = view.findViewById(R.id.drug_kind);
        drug_msg = view.findViewById(R.id.drug_msg);
        drug_num = view.findViewById(R.id.drug_updata_num);
        drug_price = view.findViewById(R.id.drug_price);

        takephoto.setOnClickListener(this);
        choose.setOnClickListener(this);*/
        add = view.findViewById(R.id.add);
        picture = view.findViewById(R.id.picture);
        Log.d("ooo","成功拍");
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
                Log.d("lll","成功拍");
            }
        });
        add.setOnClickListener(this);
        return view;
    }



    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.take_photo:

                break;
            case R.id.add:
                // 调用系统相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                // 取当前时间为照片名
                String mPictureFile = DateFormat.format("yyyyMMdd_hhmmss",	Calendar.getInstance(Locale.CHINA))	+ ".jpg";
                Log.d("onactivity", "mPictureFile：" + mPictureFile);
                filePath = getPhotoPath() + mPictureFile;
                // 通过文件创建一个uri中
                Uri imageUri = Uri.fromFile(new File(filePath));
                // 保存uri对应的照片于指定路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 2);

                break;


            case R.id.picture:
                show();
                break;
            default:
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                picture.setImageBitmap(bitmap);
            }
        } else if (requestCode == 3) {
            // 表示选择图片库的图片结果
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                picture.setImageURI(uri);
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Log.e("takePhoto", filePath);
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                picture.setImageURI(Uri.fromFile(new File(filePath)));
                picture.setImageBitmap(bitmap);
            }
        }
    }

    private String getPhotoPath() {

        return Environment.getExternalStorageDirectory() + "/DCIM/";

    }
    public Dialog show() {
        Dialog dialog = new Dialog(getView().getContext(), R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(getView().getContext()).inflate(R.layout.layout_tanchuang, null);
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



}

package com.example.ourprojecttest.DocMine;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.NavigationBar.DocBottomNavigation;
import com.example.ourprojecttest.DocMine.DocInformation.DocInformation;
import com.example.ourprojecttest.DocMine.DocOrderManagement.OrderManagement;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.WelcomeActivity;
import com.example.ourprojecttest.StuMine.ModifyPassword;
import com.example.ourprojecttest.Utils.PictureStore;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Utils.Roundimage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DocMineFragment extends Fragment {
    Context mContext;
    private CommonMethod commonMethod=new CommonMethod();
    private TextView name,offices,status;
    private Roundimage img;
    private LinearLayout exit;
    private LinearLayout modefyPassword;
    private LinearLayout orderManage;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstansceState) {
        View view = inflater.inflate(R.layout.doc_frag_my, container, false);
        mContext=getContext();
        Activity a = getActivity();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("Name");
        Receiver receiver=new Receiver();
        a.registerReceiver(receiver,intentFilter);
        IntentFilter intentFilter1=new IntentFilter();
        intentFilter1.addAction("Title");
        Receiver1 receiver1=new Receiver1();
        a.registerReceiver(receiver1,intentFilter1);
        IntentFilter intentFilter2=new IntentFilter();
        intentFilter2.addAction("Offices");
        Receiver2 receiver2=new Receiver2();
        a.registerReceiver(receiver2,intentFilter2);
        IntentFilter intentFilter3=new IntentFilter();
        intentFilter3.addAction("Picture");
        Receiver3 receiver3=new Receiver3();
        a.registerReceiver(receiver3,intentFilter3);
        name=view.findViewById(R.id.name);
        offices=view.findViewById(R.id.offices);
        status=view.findViewById(R.id.status);
        img=view.findViewById(R.id.img);
        exit=view.findViewById(R.id.docExitAccount);
        //设置医生头像

        PictureStore pictureStore1=( PictureStore)commonMethod.readObjFromSDCard("DocIcon");
        if(pictureStore1.getFlag()){
            byte[] appIcon=pictureStore1.getPicture();
            img.setImageBitmap(BitmapFactory.decodeByteArray(appIcon,0,appIcon.length));
        }

        name.setText(commonMethod.getFileData("DocName",view.getContext()));
        status.setText(commonMethod.getFileData("DocTitle",view.getContext()));
        offices.setText(commonMethod.getFileData("DocOffices",view.getContext()));
        //设置退出账号的点击事件
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               show();
            }
        });
        modefyPassword=view.findViewById(R.id.docModefyAddress);
        //设置修改密码的点击事件
        modefyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ModifyPassword.class);
                mContext.startActivity(intent);
            }
        });
        ImmersiveStatusbar.getInstance().Immersive(a.getWindow(), a.getActionBar());//状态栏透明
        LinearLayout information=view.findViewById(R.id.information);
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), DocInformation.class);
                mContext.startActivity(intent);
            }
        });
        orderManage = view.findViewById(R.id.docOrderManage);
        orderManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), OrderManagement.class);
                mContext.startActivity(intent);
            }
        });
        return  view;
    }
    private Dialog show(){
        final Dialog dialog = new Dialog(mContext,R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_stu_tuichu, null);
        //初始化控件
        TextView yes = view.findViewById(R.id.yes);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, WelcomeActivity.class);
                mContext.startActivity(intent);
            }
        });

        TextView no =view.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("exit","111");
                dialog.dismiss();
            }
        });

        //将布局设置给Dialog
        dialog.setContentView(view);
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
        return dialog;
    }


    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getStringExtra("name");
            name.setText(s);
        }
    }

    public class Receiver1 extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){
            String s=intent.getStringExtra("Title");
            status.setText(s);
        }
    }

    public class Receiver2 extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){

            String s=intent.getStringExtra("Offices");
            offices.setText(s);
        }
    }

    public class Receiver3 extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){
            img.setImageURI(Uri.parse(intent.getStringExtra("Picture")));
        }
    }
}

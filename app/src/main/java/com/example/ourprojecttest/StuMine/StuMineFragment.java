package com.example.ourprojecttest.StuMine;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Fragment;

import com.example.ourprojecttest.NavigationBar.StuBottomNavigation;
import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.NavigationBar.DocBottomNavigation;
import com.example.ourprojecttest.StuMine.StuInfomation.StuInformation;
import com.example.ourprojecttest.WelcomeActivity;
import com.example.ourprojecttest.StuMine.StuHistoryOrder.HistoryOrder;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.StuMine.StuNeedToPay.NeedToPay;
import com.example.ourprojecttest.StuMine.StuNeedToPost.NeedToPost;
import com.example.ourprojecttest.StuMine.StuNeedToReceive.NeedToReceive;
import com.example.ourprojecttest.Utils.PictureStore;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Utils.Roundimage;
import com.example.ourprojecttest.StuMine.ShoppingCart.ShoppingCartActivity;

public class StuMineFragment extends Fragment {
    private  Receiver1 receiver1;
    private  Receiver receiver;
    private Activity a;
    private Context mContext;
    private CommonMethod method=new CommonMethod();
    private Roundimage img;
    private TextView Name,Email;
    private LinearLayout fukuan;
    private LinearLayout receive;
    private LinearLayout post;
    private LinearLayout modify;
    private TextView historyOrder;
    private  LinearLayout tuichu;
    private View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstansceState) {
        view = inflater.inflate(R.layout.stu_frag_my, container, false);
        mContext=getContext();
        a=getActivity();
        IntentFilter setName=new IntentFilter();
        IntentFilter setPicture=new IntentFilter();
        setPicture.addAction("Picture");
        setName.addAction("Name");
        receiver1=new Receiver1();
        receiver=new Receiver();
        a.registerReceiver(receiver1,setPicture);
        a.registerReceiver(receiver, setName);
        ImmersiveStatusbar.getInstance().Immersive(a.getWindow(),a.getActionBar());//状态栏透明
        //初始化布局
        initView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        a.unregisterReceiver(receiver);
        a.unregisterReceiver(receiver1);
    }

    private void initView(){
        LinearLayout linearLayout =  view.findViewById(R.id.xinxi);
        LinearLayout address=view.findViewById(R.id.address);
        img=view.findViewById(R.id.roundViw);
        Name=view.findViewById(R.id.name);
        Email=view.findViewById(R.id.email);
        fukuan=view.findViewById(R.id.fukuan);
        tuichu=view.findViewById(R.id.tuichu);
        modify=view.findViewById(R.id.supervise);
        receive=view.findViewById(R.id.recieveDrug);
        post=view.findViewById(R.id.postDrug);
        historyOrder=view.findViewById(R.id.checkHistoryOrder);
        //设置我的碎片中学生的名字
        Name.setText(method.getFileData("Name",mContext));
        //设置学生的emai12345    12345
        Email.setText(method.getFileData("No",mContext));
        //设置学生的头像
        PictureStore pictureStore=( PictureStore)method.readObjFromSDCard("Icon");
        if(pictureStore.getFlag()){
            byte[] appIcon=pictureStore.getPicture();
            img.setImageBitmap(BitmapFactory.decodeByteArray(appIcon,0,appIcon.length));
        }
        LinearLayout shoppcar=view.findViewById(R.id.shoppcar);
        tuichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }
        });
        //设置查看历史订单的点击事件
        historyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, HistoryOrder.class);
                startActivity(intent);
            }
        });
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, ModifyPassword.class);
                startActivity(intent);
            }
        });
        //设置待收货的点击事件
        receive.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            Intent intent=new Intent(mContext, NeedToReceive.class);
            startActivity(intent);
            }
        });
        //设置代发货的点击事件
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent=new Intent(mContext, NeedToPost.class);
            startActivity(intent);
            }
        });
        //设置待付款的点击事件
        fukuan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            Intent intent=new Intent(mContext, NeedToPay.class);
            startActivity(intent);
            }
        });
        shoppcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ShoppingCartActivity.class);
                startActivity(intent);
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StuInformation.class);
                startActivity(intent);
            }
        });
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), AddressActivity.class);
                startActivity(intent);

            }
        });
        LinearLayout supervise=view.findViewById(R.id.supervise);

        supervise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),  ModifyPassword.class);
                startActivity(intent);
            }
        });
    }



    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getStringExtra("name");
            Name.setText(s);
        }
    }
    public class Receiver1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Uri uri=Uri.parse(intent.getStringExtra("Picture"));
            img.setImageURI(uri);
        }
    }

    public Dialog show(){
        final Dialog dialog = new Dialog(mContext,R.style.ActionSheetDialogStyle);        //展示对话框
        //填充对话框的布局
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.dialog_stu_tuichu, null);
        //初始化控件
        TextView yes = inflate.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, WelcomeActivity.class);
                startActivity(intent);
                StuBottomNavigation.activity.finish();
            }
        });
        TextView no = inflate.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //将布局设置给Dialog
        dialog.setContentView(inflate);
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
    }
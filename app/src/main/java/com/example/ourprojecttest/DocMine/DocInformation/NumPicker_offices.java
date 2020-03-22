package com.example.ourprojecttest.DocMine.DocInformation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.ourprojecttest.Utils.NumPickView;
import com.example.ourprojecttest.StuMine.NumPicker;
import com.example.ourprojecttest.R;

import java.util.List;

public class NumPicker_offices {
    Context mcontext;
    private Activity mActivity;
    private TextView tvCancel;
    private TextView tvComfirm;
    private TextView tvTitle;
    private NumPickView mNpv,mNpv2;
    private Dialog mDialog;
    private String h1,h2,h3;
    private NumPicker.OnCancelClickListener mCancelListener;
    private NumPicker.onComfirmClickListener mComfirmListener;
    private int currentSelecedNum,currentSelecedNum1;
    private List<String> L1,L2;
    public void setContext(Context a){
        mcontext=a;
    }
    NumPicker_offices(Activity activity) {
        mActivity = activity;
        initDialog();

    }

    private void initDialog() {
        mDialog = new Dialog(mActivity, R.style.time_dialog);
        mDialog.setContentView(mActivity.getLayoutInflater().inflate(R.layout.wheel_offices, null));
        Display dd = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        dd.getMetrics(dm);
        WindowManager.LayoutParams attributes = mDialog.getWindow().getAttributes();
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        attributes.height = (int) (dm.heightPixels * 0.4);
        attributes.width = dm.widthPixels;
        mDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnimation);
        tvCancel = mDialog.findViewById(R.id.tvCancel);
        tvComfirm = mDialog.findViewById(R.id.tvConfirm);
        tvTitle = mDialog.findViewById(R.id.tvTitle);
        mNpv = mDialog.findViewById(R.id.offices);
        setListener();
    }
    private void setListener() {
        mNpv.setOnSelectNumListener(new NumPickView.OnSelectNumListener() {
            @Override
            public void onSelected(int num) {
                currentSelecedNum = num;
            }
        });

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tvComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                h1=mNpv.getData();
                Intent intent=new Intent();
                intent.setAction("Offices");
                intent.putExtra("Offices",h1);
                mcontext.sendBroadcast(intent);
                mDialog.dismiss();
                if (mComfirmListener != null) {
                    mComfirmListener.onClick(currentSelecedNum);
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (mCancelListener != null) {
                    mCancelListener.onClick();
                }
            }
        });
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void selecNum(int num) {
        mNpv.select(num);
    }

    public void setOnCancelListener(NumPicker.OnCancelClickListener listener) {
        this.mCancelListener = listener;
    }

    public void setOnComfirmListener(NumPicker.onComfirmClickListener listener) {
        this.mComfirmListener = listener;
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public interface OnCancelClickListener {
        void onClick();
    }

    public interface onComfirmClickListener {
        void onClick(int num);
    }
    public class Listenerk implements NumPicker.OnCancelClickListener {
        @Override
        public void onClick(){
            mDialog.dismiss();
        }
    }
    private static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();

    }


}



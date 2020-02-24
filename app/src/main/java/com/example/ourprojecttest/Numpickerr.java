package com.example.ourprojecttest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

public class Numpickerr {
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
    void setContext(Context a){
        mcontext=a;
    }
    Numpickerr(Activity activity) {
        mActivity = activity;
        initDialog();

    }

    private void initDialog() {
        mDialog = new Dialog(mActivity, R.style.time_dialog);
        mDialog.setContentView(mActivity.getLayoutInflater().inflate(R.layout.wheel_weight, null));
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
        mNpv2=mDialog.findViewById(R.id.numPickView2);
        mNpv = mDialog.findViewById(R.id.numPickView);
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
                h2=mNpv2.getData();
                h3=h1+h2;
                Log.d("s",h3);
                Intent intent=new Intent();
                intent.setAction("Weight");
                intent.putExtra("Weight",h3);
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


}



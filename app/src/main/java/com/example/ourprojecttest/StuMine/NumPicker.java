package com.example.ourprojecttest.StuMine;

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

import com.example.ourprojecttest.Utils.NumPickView;
import com.example.ourprojecttest.R;

import java.util.List;

/**
 * Author cjet
 * Date   2018-1-16 14:02
 */

public class NumPicker {
    Context mcontext;
    private Activity mActivity;
    private TextView tvCancel;
    private TextView tvComfirm;
    private TextView tvTitle;
    private NumPickView mNpv,mNpv2;
    private Dialog mDialog;
    private String h1,h2,h3;
    private OnCancelClickListener mCancelListener;
    private onComfirmClickListener mComfirmListener;
    private int currentSelecedNum,currentSelecedNum1;
    private List<String> L1,L2;
    public void setContext(Context a){
        mcontext=a;
    }
    public NumPicker(Activity activity) {
        mActivity = activity;
        initDialog();

    }

    private void initDialog() {
        mDialog = new Dialog(mActivity, R.style.time_dialog);
        mDialog.setContentView(mActivity.getLayoutInflater().inflate(R.layout.wheel_hight, null));
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
                intent.setAction("Height");
                intent.putExtra("Height",h3);
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

    public void setOnCancelListener(OnCancelClickListener listener) {
        this.mCancelListener = listener;
    }

    public void setOnComfirmListener(onComfirmClickListener listener) {
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
public class Listenerk implements OnCancelClickListener{
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

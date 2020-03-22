package com.example.ourprojecttest.StuDiagnosis;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.Utils.DiffuseView;
import com.example.ourprojecttest.R;

public class StuDiagnosisFragment extends Fragment {
        Context mContext;
        private TextView docWorkTime;
        CommonMethod method=new CommonMethod();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stu_frag_wenzhen_fore,container,false);
        mContext=getContext();
        initView(view);
        LinearLayout l=view.findViewById(R.id.ssss);
        DiffuseView diffuseView1=view.findViewById(R.id.diffuseView1);
        diffuseView1.start();
        return view;
    }


    private void initView(View view){
        Button rgwenzhen=view.findViewById(R.id.wenzhen1);

        //注册人工问诊的点击事件
        rgwenzhen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), RenGongWenZhen.class);
                startActivity(intent);
            }
        });

    }
}

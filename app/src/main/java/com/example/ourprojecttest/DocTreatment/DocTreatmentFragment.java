package com.example.ourprojecttest.DocTreatment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.Button;

import com.example.ourprojecttest.DiffuseView;
import com.example.ourprojecttest.DocService;
import com.example.ourprojecttest.R;


public class DocTreatmentFragment extends Fragment {

    Intent intentToService=new Intent("com.example.ourprojecttest.DOC_UPDATE_SERVICE");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contain, Bundle savedInstanceState) {
        //这里获取要替换掉FrameLayout的布局fragment_knowledge.xml
        View view = inflater.inflate(R.layout.doc_frag_houzhen,contain,false);
        Log.d("进入候诊界面","候诊界面");
        Button houzhen=view.findViewById(R.id.jiezhen);
        DiffuseView diffuseView=view.findViewById(R.id.diffuseView);
        diffuseView.start();
        //创建一个服务
        Intent intentStartService = new Intent(getContext(), DocService.class);
        getContext().startService(intentStartService);
        //注册医生上线的点击事件
        houzhen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //给服务发送挂号的广播
                intentToService.putExtra("msg","Online");
                getContext().sendBroadcast(intentToService);
                Intent intent=new Intent(getContext(), DocOperatActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}

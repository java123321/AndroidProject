package com.example.ourprojecttest.StuDiagnoseRecord;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.StuDiagnosis.MessageBean;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;

import java.util.ArrayList;

public class StuRecordFragment extends Fragment {
    private TextView recordTitle;
    private TextView noRecordText;
    private String type;
    private CommonMethod method = new CommonMethod();
    private RecyclerView mRecycler;
    private MsgRecordAdapter adapter;
    private ArrayList<MessageBean> lists;
    private LinearLayout empty;
    private Context mContext = null;
    private SwipeRefreshLayout refresh;
    private LinearLayoutManager layoutManager;
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stu_frag_msg_front, container, false);
        Log.d("msginit", "test0");
        Activity a=getActivity();
        mContext = getContext();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.ourprojecttest.MSG_RECORD");
        localReceiver = new LocalReceiver();
        mContext.registerReceiver(localReceiver, intentFilter);
        ImmersiveStatusbar.getInstance().Immersive(a.getWindow(),a.getActionBar());//状态栏透明
        initView(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContext.unregisterReceiver(localReceiver);
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            //用户收到删除的广播后保存到本地
            method.writeMessageRecordListIntoSDcard("MessageRecord",adapter.getmList());
        }
    }
    private void initView(View view){

        type=method.getFileData("Type",mContext);
        recordTitle=view.findViewById(R.id.recordTitle);
        noRecordText=view.findViewById(R.id.noRecordText);
        //设置下拉刷新的监听事件
        refresh = view.findViewById(R.id.swipeRefresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(true);
                getData();
                refresh.setRefreshing(false);
            }
        });
        refresh.setColorSchemeColors(getResources().getColor(R.color.color_bottom));
        refresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.color_progressbar));

        empty=view.findViewById(R.id.empty);
        mRecycler = view.findViewById(R.id.stuMsgRecordRecycler);
        layoutManager = new LinearLayoutManager(mContext);
        // 如果lists为空，则代表还无聊天记录
        lists = method.readMessageRecordListFromSdCard("MessageRecord");
        Log.d("stulist", "is?" + (lists == null));
        //如果为null，表明当前暂无聊天记录

        if(type.equals("Stu")){
            recordTitle.setText("问诊记录");
            noRecordText.setText("暂无问诊记录!");
        }else{
            recordTitle.setText("接诊记录");
            noRecordText.setText("暂无接诊记录!");
        }
        getData();
    }
    private void getData() {

        lists = method.readMessageRecordListFromSdCard("MessageRecord");
        Log.d("stulist", "is?" + (lists == null));

        if (lists == null||lists.size()==0) {
            mRecycler.setVisibility(View.GONE);
           empty.setVisibility(View.VISIBLE);
        } else {
            Log.d("stulist", "size?" + lists.size());
            mRecycler.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            Log.d("stulist", "iss?" + (lists.size()));
            mRecycler.setLayoutManager(layoutManager);
            adapter = new MsgRecordAdapter(getContext());
            adapter.setmList(lists);
            mRecycler.setAdapter(adapter);
        }

    }

}

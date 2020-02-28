package com.example.ourprojecttest.StuMessage;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ourprojecttest.CommonMethod;
import com.example.ourprojecttest.DisplayDocAdapter;
import com.example.ourprojecttest.DisplayDocList;
import com.example.ourprojecttest.MessageBean;
import com.example.ourprojecttest.MsgRecordAdapter;
import com.example.ourprojecttest.R;
import java.util.ArrayList;

public class StuMessageFragment extends Fragment {
    private CommonMethod method=new CommonMethod();
    private RecyclerView mRecycler;
    private MsgRecordAdapter adapter;
    private ArrayList<MessageBean> lists;
    private TextView noRecord;
    private Context mContext=null;
    private SwipeRefreshLayout refresh;
    private LinearLayoutManager layoutManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view=inflater.inflate(R.layout.stu_frag_msg_front,container,false);
        Log.d("msginit","test0");
        //设置下拉刷新的监听事件
        refresh=view.findViewById(R.id.swipeRefresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            refresh.setRefreshing(true);
               getData();
               refresh.setRefreshing(false);
            }
        });
        mContext=getContext();
        noRecord=view.findViewById(R.id.noRecord);
        noRecord.setText("hello");
        mRecycler=view.findViewById(R.id.stuMsgRecordRecycler);
        layoutManager=new LinearLayoutManager(mContext);
       // 如果lists为空，则代表还无聊天记录
        lists=method.readMessageRecordListFromSdCard("MessageRecord");
        Log.d("stulist","is?"+(lists==null));
        //如果为null，表明当前暂无聊天记录
        getData();
        return view;
    }
private void getData(){
    if(lists==null){
        mRecycler.setVisibility(View.INVISIBLE);
        noRecord.setVisibility(View.VISIBLE);
        noRecord.setText("暂无消息记录");
    }
    else {
        mRecycler.setVisibility(View.VISIBLE);
        noRecord.setVisibility(View.INVISIBLE);
        Log.d("stulist","iss?"+(lists.size()));
        mRecycler.setLayoutManager(layoutManager);
        adapter=new MsgRecordAdapter(getContext());
        adapter.setmList(lists);
        mRecycler.setAdapter(adapter);
    }

}
    private Bitmap Rfile2Bitmap(){
        return BitmapFactory.decodeResource(getResources(),R.drawable.person);
    }

}

package com.example.ourprojecttest.StuMessage;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.ChatMsgItem;
import com.example.ourprojecttest.MsgRecordAdapter;
import com.example.ourprojecttest.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class StuMessageFragment extends Fragment {

    private RecyclerView mRecycler;
    private MsgRecordAdapter adapter;
    private ArrayList<ChatMsgItem> lists=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view=inflater.inflate(R.layout.stu_frag_msg_front,container,false);


        mRecycler=view.findViewById(R.id.stuMsgRecordRecycler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(layoutManager);
        adapter=new MsgRecordAdapter(getContext());
        mRecycler.setAdapter(adapter);

        ChatMsgItem item=new ChatMsgItem();
        item.setName("华佗");
        item.setTime("1222/2/12");
        item.setIcon(Rfile2Bitmap());
        lists.add(item);
        lists.add(item);
        lists.add(item);
        adapter.setmList(lists);
        adapter.notifyDataSetChanged();
        return view;
    }

    private Bitmap Rfile2Bitmap(){
        return BitmapFactory.decodeResource(getResources(),R.drawable.person);
    }

}

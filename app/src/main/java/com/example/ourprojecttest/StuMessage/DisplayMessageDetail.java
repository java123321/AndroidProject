package com.example.ourprojecttest.StuMessage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ourprojecttest.CommonMethod;
import com.example.ourprojecttest.Msg;
import com.example.ourprojecttest.MsgAdapter;
import com.example.ourprojecttest.PictureStore;
import com.example.ourprojecttest.R;

import java.util.ArrayList;

public class DisplayMessageDetail extends AppCompatActivity {
    private CommonMethod method=new CommonMethod();
    private RecyclerView mRecycler;
    private MsgAdapter adapter;
    private String type;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message_detail);
        initView();
    }

    private void initView(){
        Intent intent=getIntent();
        type=method.getFileData("Type",this);


        mRecycler=findViewById(R.id.recycler);
        title=findViewById(R.id.chatName);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        byte[] stuIcon;
        byte[] docIcon;
        PictureStore pictureStore;
        ArrayList<Msg>content=method.readMessageContentFromSdCard("MsgContent");
        //如果是学生登录
        if(type.equals("Stu")){
            title.setText(intent.getStringExtra("name")+"医生");
            docIcon=intent.getByteArrayExtra("icon");
            pictureStore=(PictureStore)method.readObjFromSDCard("Icon");
            stuIcon=pictureStore.getPicture();
            Bitmap doc= BitmapFactory.decodeByteArray(docIcon,0,docIcon.length);
            Bitmap stu=BitmapFactory.decodeByteArray(stuIcon,0,stuIcon.length);
            adapter=new MsgAdapter(content,doc,stu);
        }
        else{//如果是医生登录
            title.setText(intent.getStringExtra("name")+"同学");
            stuIcon=intent.getByteArrayExtra("icon");
            pictureStore=(PictureStore)method.readObjFromSDCard("DocIcon");
            docIcon=pictureStore.getPicture();
            Bitmap stu=BitmapFactory.decodeByteArray(stuIcon,0,stuIcon.length);
            Bitmap doc=BitmapFactory.decodeByteArray(docIcon,0,docIcon.length);
            adapter=new MsgAdapter(content,stu,doc);
        }
        mRecycler.setAdapter(adapter);
    }

}

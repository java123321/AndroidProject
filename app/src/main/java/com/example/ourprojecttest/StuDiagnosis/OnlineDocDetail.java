package com.example.ourprojecttest.StuDiagnosis;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.Utils.Roundimage;

public class OnlineDocDetail extends AppCompatActivity {
    private Roundimage docIcon;
    private TextView docName;
    private TextView docSex;
    private TextView docBrief;
    private ImageView docLicense;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_doc_detail);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明
        initView();
    }

    private void initView(){
        Intent intent=getIntent();
        docIcon=findViewById(R.id.docIcon);
        docName=findViewById(R.id.docName);
        docSex=findViewById(R.id.docSex);
        docBrief=findViewById(R.id.docBrief);
        docLicense=findViewById(R.id.docLicense);
        byte[] icon=intent.getByteArrayExtra("docIcon");
        docIcon.setImageBitmap(BitmapFactory.decodeByteArray(icon,0,icon.length));
        docName.setText("姓名:"+intent.getStringExtra("docName"));
        docSex.setText("性别:"+intent.getStringExtra("docSex"));
        docBrief.setText(intent.getStringExtra("docBrief"));
        icon=intent.getByteArrayExtra("docLicense");
        docLicense.setImageBitmap(BitmapFactory.decodeByteArray(icon,0,icon.length));
    }
}

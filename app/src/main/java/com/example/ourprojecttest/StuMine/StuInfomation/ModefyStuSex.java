package com.example.ourprojecttest.StuMine.StuInfomation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ourprojecttest.R;

public class ModefyStuSex extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_sex);
        LinearLayout l=findViewById(R.id.lll);
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.student_sex, null);
        TextView t=layout.findViewById(R.id.quding);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ssss","111111");
            }
        });
    }
}

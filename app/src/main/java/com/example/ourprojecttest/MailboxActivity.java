package com.example.ourprojecttest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ourprojecttest.Utils.ImmersiveStatusbar;

public class MailboxActivity extends AppCompatActivity {

    String code="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailbox);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(),getActionBar());//状态栏透明

        final TextView msg = this.findViewById(R.id.msg);
        Button getmsg = this.findViewById(R.id.getmsg);
        Button check = this.findViewById(R.id.check);

        getmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = getConde();
                msg.setText(code);

            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!code.equals(msg.getText().toString()))
                {
                    new AlertDialog.Builder(MailboxActivity.this).setTitle("错误").setMessage("验证码错误").setNegativeButton("确定",null).show();
                }
                else
                {
                    new AlertDialog.Builder(MailboxActivity.this).setTitle("跳转").setMessage("验证成功,准备好登陆了吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(MailboxActivity.this,MailboxActivity.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("取消",null).show();
                }
            }
        });

    }
    private String getConde(){
        String code1 ="";
        for (int i =0;i<4;i++)
        {
            code1 = code1 + (int)(Math.random() * 10);
        }
        return code1;
    }

}

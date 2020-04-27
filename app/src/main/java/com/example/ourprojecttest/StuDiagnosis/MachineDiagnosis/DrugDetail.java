package com.example.ourprojecttest.StuDiagnosis.MachineDiagnosis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ourprojecttest.R;

public class DrugDetail extends AppCompatActivity {
    private TextView name;
    private TextView py;
    private TextView guige;
    private TextView unite;
    private TextView wenhao;
    private TextView changjia;
    private TextView tiaoxingma;
    private TextView zhuzhi;
    private TextView shuomingshu;
    private TextView buchong;
    private TextView otc;
    private TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_detail);
        initView();
    }

    private void initView(){
        Intent intent=getIntent();
        name=findViewById(R.id.name);
        name.setText("药品名字:"+intent.getStringExtra("goodsName"));
        py=findViewById(R.id.py);
        py.setText("拼音简码:"+intent.getStringExtra("pyCode"));
        guige=findViewById(R.id.guige);
        guige.setText("规格:"+intent.getStringExtra("guiGe"));
        unite=findViewById(R.id.unite);
        unite.setText("单位:"+intent.getStringExtra("unit"));
        wenhao=findViewById(R.id.wenhao);
        wenhao.setText("批准文号:"+intent.getStringExtra("approvalNumber"));
        changjia=findViewById(R.id.changjia);
        changjia.setText("生产厂家:"+intent.getStringExtra("manufacture"));
        tiaoxingma=findViewById(R.id.tiaoxingma);
        tiaoxingma.setText("条形码号:"+intent.getStringExtra("barCode"));
        zhuzhi=findViewById(R.id.zhuzhi);
        zhuzhi.setText("主治:"+intent.getStringExtra("cureDisease"));
        shuomingshu=findViewById(R.id.shuomingshu);
        shuomingshu.setText("说明书:"+intent.getStringExtra("explainBook"));
        buchong=findViewById(R.id.buchong);
        buchong.setText("补充说明:"+intent.getStringExtra("additionalExplain"));
        otc=findViewById(R.id.otc);
        if(intent.getStringExtra("otc").equals("1")){
            otc.setText("属性: OTC");
        }else{
            otc.setText("属性: RX");
        }
        time=findViewById(R.id.time);
        time.setText("上架时间:"+intent.getStringExtra("upTime"));

    }
}

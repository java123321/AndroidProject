package com.example.ourprojecttest.StuDiagnosis.MachineDiagnosis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ourprojecttest.R;

public class DiseaseDetail extends AppCompatActivity {
    private TextView diseaseName;
    private TextView diseaseAlias;
    private TextView infectious;
    private TextView treatmentDepart;
    private TextView insurance;
    private TextView population;
    private TextView cocurrentDisease;
    private TextView symptom;
    private TextView location;
    private TextView duration;
    private TextView recoveryRate;
    private TextView introduction;
    private Button suggestDrug;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_detail);
        initView();
    }

    private void initView(){
        Intent intent=getIntent();
        diseaseName=findViewById(R.id.diseaseName);
        diseaseName.setText("疾病名称:"+intent.getStringExtra("diseaseName"));
        diseaseAlias=findViewById(R.id.diseaseAlias);
        diseaseAlias.setText("疾病别名:"+intent.getStringExtra("diseaseAlias"));
        infectious=findViewById(R.id.infectious);
        infectious.setText("感染性:"+intent.getStringExtra("infectious"));
        treatmentDepart=findViewById(R.id.treatmentDepart);
        treatmentDepart.setText("治疗科室:"+intent.getStringExtra("concurrentDisease"));
        insurance=findViewById(R.id.insurance);
        insurance.setText("医保类型:"+intent.getStringExtra("belongInsurance"));
        population=findViewById(R.id.population);
        population.setText("患病人群:"+intent.getStringExtra("population"));
        cocurrentDisease=findViewById(R.id.cocurrentDisease);
        cocurrentDisease.setText("当前疾病:"+intent.getStringExtra("concurrentDisease"));
        symptom=findViewById(R.id.symptom);
        symptom.setText("症状:"+intent.getStringExtra("symptom"));
        location=findViewById(R.id.location);
        location.setText("疾病部位:"+intent.getStringExtra("diseaseLocation"));
        duration=findViewById(R.id.duration);
        duration.setText("疾病周期:"+intent.getStringExtra("treatmentDuration"));
        recoveryRate=findViewById(R.id.recoveryRate);
        recoveryRate.setText("恢复率:"+intent.getStringExtra("recoveryRate"));
        introduction=findViewById(R.id.introduction);
        introduction.setText("疾病介绍:"+intent.getStringExtra("introducton"));
        suggestDrug=findViewById(R.id.suggestDrug);

        //设置药品推荐按钮的点击事件
        suggestDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intentToRecommondDrug=new Intent(DiseaseDetail.this,RecommendDrug.class);
            intentToRecommondDrug.putExtra("diseaseName",intent.getStringExtra("diseaseName"));
            startActivity(intentToRecommondDrug);
            }
        });
    }




}

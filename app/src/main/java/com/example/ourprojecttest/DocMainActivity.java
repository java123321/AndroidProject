package com.example.ourprojecttest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DocMainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_main);
        //age.setImageResource(R.drawable.unknowledge);
        //myImage.setImageResource(R.drawable.unmy);
        //linkText.setTextColor(getResources().getColor(R.color.theBlue));
        //lifeText.setTextColor(getResources().getColor(R.color.theGray));
        //knowText.setTextColor(getResources().getColor(R.color.theGray));
        //myText.setTextColor(getResources().getColor(R.color.theGray));
        //----------------------------------------------------------------------------------
        // ---------------------------添加联系人Fragment-------------------------------------
        //FragmentOfLinkMan fragmentOfLinkMan = new FragmentOfLinkMan();
        //FragmentManager fragmentManagerLinkMan = getFragmentManager();
        //FragmentTransaction transactionLinkMan = fragmentManagerLinkMan.beginTransaction();
        //transactionLinkMan.replace(R.id.frameLayoutId,fragmentOfLinkMan);
        //transactionLinkMan.commit();
        //ImageView linkImage = (ImageView)findViewById(R.id.use_linkImageId);
        //TextView linkText = (TextView)findViewById(R.id.use_linkTextId);
        //linkImage.setImageResource(R.drawable.linkman);
        //linkText.setTextColor(getResources().getColor(R.color.theBlue));
        //------------------------------------------------------------------------------------------
        // ---------------------------------添加监听事件---------------------------------------------
        LinearLayout linkll = (LinearLayout)findViewById(R.id.linkManTouchId);
        LinearLayout lifell = (LinearLayout)findViewById(R.id.lifeTouchId);
        LinearLayout knowll = (LinearLayout)findViewById(R.id.knowledgeTouchId);
        LinearLayout myll = (LinearLayout)findViewById(R.id.myTouchId);
        linkll.setOnClickListener(this);
        lifell.setOnClickListener(this);
        knowll.setOnClickListener(this);
        myll.setOnClickListener(this);
        //------------------------------------------------------------------------------------------
        }
        @Override
        public void onClick(View view){
        ImageView linkImage = (ImageView)findViewById(R.id.use_linkImageId);
        ImageView lifeImage = (ImageView)findViewById(R.id.use_lifeImageId);
        ImageView knowImage = (ImageView)findViewById(R.id.use_knowImageId);
        ImageView myImage = (ImageView)findViewById(R.id.use_myImageId);
        TextView linkText = (TextView)findViewById(R.id.use_linkTextId);
        TextView lifeText = (TextView)findViewById(R.id.use_lifeTextId);
        TextView knowText = (TextView)findViewById(R.id.use_knowTextId);
        TextView myText = (TextView)findViewById(R.id.use_myTextId);
        switch (view.getId()){
            case R.id.linkManTouchId://点击“联系人”触发的监听事件
                // ---------------------------联系人处高亮，其他灰色---------------------------------
                linkImage.setImageResource(R.drawable.information);
                lifeImage.setImageResource(R.drawable.uninquiry);
                knowImage.setImageResource(R.drawable.un_drug_store);
                myImage.setImageResource(R.drawable.unmy);
                //改变下面字体颜色
                //linkText.setTextColor(getResources().getColor(R.color.theBlue));
                //lifeText.setTextColor(getResources().getColor(R.color.theGray));
                //knowText.setTextColor(getResources().getColor(R.color.theGray));
                //myText.setTextColor(getResources().getColor(R.color.theGray));
                //----------------------------------------------------------------------------------
                // ---------------------------添加联系人Fragment-------------------------------------
                FragmentOfDrugManage fragmentOfDrugManage = new FragmentOfDrugManage();
                FragmentManager fragmentManagerLinkMan = getFragmentManager();
                FragmentTransaction transactionLinkMan = fragmentManagerLinkMan.beginTransaction();
                transactionLinkMan.replace(R.id.frameLayoutId,fragmentOfDrugManage);
                transactionLinkMan.commit();
        //----------------------------------------------------------------------------------
                 break;
             case R.id.lifeTouchId://点击“生活”触发的监听事件
        // ---------------------------生活处高亮，其他灰色-----------------------------------
                linkImage.setImageResource(R.drawable.uninformation);
                lifeImage.setImageResource(R.drawable.inquiry);
                knowImage.setImageResource(R.drawable.un_drug_store);
                myImage.setImageResource(R.drawable.unmy);
                //linkText.setTextColor(getResources().getColor(R.color.theGray));
                //lifeText.setTextColor(getResources().getColor(R.color.theBlue));
                //knowText.setTextColor(getResources().getColor(R.color.theGray));
                //myText.setTextColor(getResources().getColor(R.color.theGray));
        //----------------------------------------------------------------------------------
        // ---------------------------添加美食Fragment---------------------------------------
             /*   FragmentOfFood fragmentOfFood = new FragmentOfFood();
                FragmentManager fragmentManagerFood = getFragmentManager();
                FragmentTransaction transactionFood = fragmentManagerFood.beginTransaction();
                transactionFood.replace(R.id.frameLayoutId,fragmentOfFood);
                transactionFood.commit();*/
        //----------------------------------------------------------------------------------
                 break;
             case R.id.knowledgeTouchId://点击“知识”触发的监听事件
        // ---------------------------知识处高亮，其他灰色-----------------------------------
                linkImage.setImageResource(R.drawable.uninformation);
                lifeImage.setImageResource(R.drawable.uninquiry);
                knowImage.setImageResource(R.drawable.drug_storage);
                myImage.setImageResource(R.drawable.unmy);
                /*
                linkText.setTextColor(getResources().getColor(R.color.theGray));
                lifeText.setTextColor(getResources().getColor(R.color.theGray));
                knowText.setTextColor(getResources().getColor(R.color.theBlue));
                myText.setTextColor(getResources().getColor(R.color.theGray));
        //----------------------------------------------------------------------------------
        // ---------------------------添加知识Fragment---------------------------------------
                FragmentOfKnowLedge fragmentOfKnow = new FragmentOfKnowLedge();
                FragmentManager fragmentManagerKnow = getFragmentManager();
                FragmentTransaction transactionKnow = fragmentManagerKnow.beginTransaction();
                transactionKnow.replace(R.id.frameLayoutId,fragmentOfKnow);
                transactionKnow.commit();*/
        //----------------------------------------------------------------------------------
                 break;
            case R.id.myTouchId://点击“我的”触发的监听事件
        // ---------------------------我的处高亮，其他灰色-----------------------------------
                linkImage.setImageResource(R.drawable.uninformation);
                lifeImage.setImageResource(R.drawable.uninquiry);
                knowImage.setImageResource(R.drawable.un_drug_store);
                myImage.setImageResource(R.drawable.my);
                /*
                linkText.setTextColor(getResources().getColor(R.color.theGray));
                lifeText.setTextColor(getResources().getColor(R.color.theGray));
                knowText.setTextColor(getResources().getColor(R.color.theGray));
                myText.setTextColor(getResources().getColor(R.color.theBlue));*/
        //----------------------------------------------------------------------------------
                 break;
            default:
                break;
    }

    }
}

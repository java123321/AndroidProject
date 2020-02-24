package com.example.ourprojecttest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ourprojecttest.DocMine.DocMineFragment;
import com.example.ourprojecttest.DocTreatment.DocTreatmentFragment;
import com.example.ourprojecttest.StuDrugStore.StuDrugStoreFragment;
import com.example.ourprojecttest.StuMessage.StuMessageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

public class DocBottomNavigation extends AppCompatActivity {
    public static Activity activity;
    private StuDrugStoreFragment yaodian_frag = null;
    private DocMineFragment wode=null;
    private DocTreatmentFragment houzhen=null;
    private StuMessageFragment msg=null;
    /** 上次点击返回键的时间 */
    private long lastBackPressed;
    /** 两次点击的间隔时间 */
    private static final int QUIT_INTERVAL = 3000;
    int lastnum = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchContent(1);
                    return true;
                case R.id.navigation_dashboard:
                    switchContent(2);
                    return true;
                case R.id.navigation_yaodian:
                    switchContent(3);
                    return true;

                case R.id.navigation_wode:
                    switchContent(4);
                    return true;
            }
            return false;
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            long backPressed = System.currentTimeMillis();
            if (backPressed - lastBackPressed > QUIT_INTERVAL) {
                lastBackPressed = backPressed;
                Toast.makeText(this,"再按一次退出",Toast.LENGTH_LONG).show();

            } else {
                Intent intent=new Intent(DocBottomNavigation.this,DocService.class);
                stopService(intent);
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_bott_navi);
        activity=this;

        //当用户登录成功之后销毁欢迎界面
        Log.d("bottom","0");
       // MainActivity.main.finish();
        Log.d("bottom","1");
        initFragment();
        //创建医生接收信息的前台服务
        Intent intentStartService = new Intent(DocBottomNavigation.this, DocService.class);
        startService(intentStartService);

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.doc_bott_view);
        bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(0).getItemId());
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);//当menu>3时文字正常显示
    }

    //初始化碎片
    private void initFragment() {
        // 获取FragmentManager
        FragmentManager fragmentManager = getFragmentManager();
        // 开始事务管理
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //创建碎片并加入transaction
        yaodian_frag = new StuDrugStoreFragment();
        wode=new DocMineFragment();
        houzhen=new DocTreatmentFragment();
        msg=new StuMessageFragment();

        transaction.add(R.id.doc_main_layout,msg);
        transaction.add(R.id.doc_main_layout, yaodian_frag);
        transaction.add(R.id.doc_main_layout,wode);
        transaction.add(R.id.doc_main_layout,houzhen);

        transaction.hide(msg);
        transaction.hide(wode);
        transaction.hide(yaodian_frag);
        transaction.show(houzhen);
        transaction.commit();
    }
    private void switchContent(int choice) {

        switch (choice){
            case 1:
                if (lastnum != 1) {
                    showFragment("houzhen");
                    lastnum = 1;
                }
                break;
            case 2:
                if (lastnum != 2) {
                    showFragment("msg");
                    lastnum = 2;
                }
                break;
            case 3:
                if (lastnum != 3) {
                    showFragment("yaodian");
                    lastnum = 3;
                }
                break;
            case 4:
                if (lastnum != 4) {
                    showFragment("wode");
                    lastnum = 4;
                }
                break;

        }

    }

    private void showFragment(String name) {
        // 获取FragmentManager
        FragmentManager fragmentManager = getFragmentManager();
        // 开始事务管理
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (name){
            case "houzhen":
                transaction.hide(msg);
                transaction.hide(wode);
                transaction.hide(yaodian_frag);
                transaction.show(houzhen);
                break;
            case "yaodian":
                transaction.hide(msg);
                transaction.hide(houzhen);
                transaction.hide(wode);
                transaction.show(yaodian_frag);
                break;
            case "wode":
                transaction.hide(msg);
                transaction.hide(houzhen);
                transaction.hide(yaodian_frag);
                transaction.show(wode);
                break;
            case "msg":
                transaction.hide(wode);
                transaction.hide(houzhen);
                transaction.hide(yaodian_frag);
                transaction.show(msg);
        }
        transaction.commit();
    }
}

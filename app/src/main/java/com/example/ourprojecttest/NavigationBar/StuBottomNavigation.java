package com.example.ourprojecttest.NavigationBar;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Service.StuService;
import com.example.ourprojecttest.StuDiagnosis.StuDiagnosisFragment;
import com.example.ourprojecttest.StuDrugStore.StuDrugStoreFragment;
import com.example.ourprojecttest.StuDiagnoseRecord.StuRecordFragment;
import com.example.ourprojecttest.StuMine.StuMineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

public class StuBottomNavigation extends AppCompatActivity {
    public static Activity activity;
    private CommonMethod method = new CommonMethod();
    private StuDrugStoreFragment yaodian_frag = null;
    private StuMineFragment wode_frag = null;
    private StuDiagnosisFragment wenzhen_frag = null;
    private StuRecordFragment msg_frag = null;
    private int lastnum = 1;
    private BottomNavigationView bottomNavigationView;
    /**
     * 上次点击返回键的时间
     */
    private long lastBackPressed;
    /**
     * 两次点击的间隔时间
     */
    private static final int QUIT_INTERVAL = 3000;

    /**
     * 重写onKeyDown()
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            long backPressed = System.currentTimeMillis();
            if (backPressed - lastBackPressed > QUIT_INTERVAL) {
                lastBackPressed = backPressed;
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_LONG).show();

            } else {
                Intent intent = new Intent(StuBottomNavigation.this, StuService.class);
                stopService(intent);
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switchContent(3);
        bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(2).getItemId());
        Log.d("drug1", "134");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        Log.d("drug1", "124");
        Intent intent=getIntent();
        Log.d("drug1", "124"+intent.getStringExtra("from"));
        setContentView(R.layout.activity_stu_bott_navi);
        bottomNavigationView = findViewById(R.id.stu_bott_view);
        bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(0).getItemId());
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);//当menu>3时文字正常显示
        Log.d("mytest", "navi");
        initFragment();
        //启动学生服务，以便为后续的挂号提供服务
        Intent intentStartService = new Intent(StuBottomNavigation.this, StuService.class);
        startService(intentStartService);
    }
    private void switchContent(int choice) {
        switch (choice) {
            case 1:
                if (lastnum != 1) {
                    showFragment("wenzhen");
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

    //初始化碎片
    private void initFragment() {
        // 获取FragmentManager
        FragmentManager fragmentManager = getFragmentManager();
        // 开始事务管理
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //创建碎片并加入transaction
        yaodian_frag = new StuDrugStoreFragment();
        wode_frag = new StuMineFragment();
        wenzhen_frag = new StuDiagnosisFragment();
        msg_frag = new StuRecordFragment();
        transaction.add(R.id.stu_main_layout, yaodian_frag);
        transaction.add(R.id.stu_main_layout, wode_frag);
        transaction.add(R.id.stu_main_layout, wenzhen_frag);
        transaction.add(R.id.stu_main_layout, msg_frag);
        transaction.hide(msg_frag);
        transaction.hide(wode_frag);
        transaction.hide(yaodian_frag);
        transaction.show(wenzhen_frag);
        transaction.commit();
    }

    private void showFragment(String name) {
        // 获取FragmentManager
        FragmentManager fragmentManager = getFragmentManager();
        // 开始事务管理
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (name) {
            case "wenzhen":
                transaction.hide(msg_frag);
                transaction.hide(wode_frag);
                transaction.hide(yaodian_frag);
                transaction.show(wenzhen_frag);
                break;
            case "yaodian":
                transaction.hide(msg_frag);
                transaction.hide(wode_frag);
                transaction.hide(wenzhen_frag);
                transaction.show(yaodian_frag);
                break;
            case "wode":
                transaction.hide(msg_frag);
                transaction.hide(wenzhen_frag);
                transaction.hide(yaodian_frag);
                transaction.show(wode_frag);
                break;
            case "msg":
                transaction.hide(wenzhen_frag);
                transaction.hide(yaodian_frag);
                transaction.hide(wode_frag);
                transaction.show(msg_frag);
        }
        transaction.commit();
    }
}

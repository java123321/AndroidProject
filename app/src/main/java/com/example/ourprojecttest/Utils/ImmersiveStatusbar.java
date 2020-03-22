package com.example.ourprojecttest.Utils;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

/**
 * Created by zouqi on 2017/4/15.
 */

public class ImmersiveStatusbar {

    private static ImmersiveStatusbar immersiveStatusbar;

    // 构造函数私有化
    private ImmersiveStatusbar() {

    }


    public static ImmersiveStatusbar getInstance() {

        if (immersiveStatusbar == null) {
            // 加锁提高使用效率
            synchronized (ImmersiveStatusbar.class) {
                if (immersiveStatusbar == null) {
                    immersiveStatusbar = new ImmersiveStatusbar();
                }
            }
        }
        return immersiveStatusbar;

    }

    /***
     * 状态栏透明化
     * @param window    Window对象
     * @param actionBar ActionBar对象
     */
    public void Immersive(Window window, ActionBar actionBar) {

        if (Build.VERSION.SDK_INT >= 22) {

            View view = window.getDecorView();

            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            view.setSystemUiVisibility(option);
            // 将状态栏设置成透明色
            window.setStatusBarColor(Color.TRANSPARENT);

        }
        // 将ActionBar隐藏


    }
}

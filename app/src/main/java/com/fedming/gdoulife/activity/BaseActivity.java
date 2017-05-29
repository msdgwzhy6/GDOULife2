package com.fedming.gdoulife.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * 基类，封装Activity通用方法
 * Created by fedming on 2016/9/1.
 */

public class BaseActivity extends AppCompatActivity {

    private String LogTag = "BaseActivity:";
    protected String queueName = getClass().getName();
    public String TAG = "fdm";

    //this activity context
    protected Context mContext = null;

    //application context
    protected Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        context = getApplicationContext();

        Log.i("fdm", String.format("queueName = %s", queueName));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @TargetApi(19)
    protected static void setTranslucentStatus(Activity activity, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }

        win.setAttributes(winParams);
    }

    protected void exitAllActivity() {

    }

}

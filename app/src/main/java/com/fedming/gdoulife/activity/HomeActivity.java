package com.fedming.gdoulife.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fedming.gdoulife.R;
import com.fedming.gdoulife.fragment.CourseFragment;
import com.fedming.gdoulife.fragment.LibraryFragment;
import com.fedming.gdoulife.fragment.MoreFragment;
import com.fedming.gdoulife.fragment.NewsFragment;
import com.fedming.gdoulife.utils.ScreenUtils;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;

/**
 * 主页 HomeActivity
 * Created by fedming on 2016/9/4.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private RadioButton newsRadioButton;
    private RadioButton libRadioButton;
    private RadioButton discoveryRadioButton;
    private RadioButton moreRadioButton;
    private RelativeLayout splashRelativeLayout;
    private ImageView splashImageView;

    private FragmentManager fragmentManager;
    private long clickBackButtonTime = 0;
    private int clickToExitInterval = 2000;
    private static final int STOPSPLASH = 0;
    //time in milliseconds
    private static final long SPLASHTIME = 2000;

    private NewsFragment newsFragment;
    private LibraryFragment libraryFragment;
    private CourseFragment courseFragment;
    private MoreFragment moreFragment;

    private Handler splashHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STOPSPLASH:
                    splashRelativeLayout.setVisibility(View.GONE);
                    //显示状态栏
                    ScreenUtils.setNomalScreen(getWindow());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN | SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_home);
        initView();

        Message msg = new Message();
        msg.what = STOPSPLASH;
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);

    }

    private void initView() {

        newsRadioButton = (RadioButton) findViewById(R.id.nav_news_radio_button);
        libRadioButton = (RadioButton) findViewById(R.id.nav_lib_radio_button);
        discoveryRadioButton = (RadioButton) findViewById(R.id.nav_discovery_radio_button);
        moreRadioButton = (RadioButton) findViewById(R.id.nav_more_radio_button);
        splashRelativeLayout = (RelativeLayout) findViewById(R.id.splash_relativeLayout);
        splashImageView = (ImageView) findViewById(R.id.splash_imageView);

//        switch ((int)(Math.random() * 4)){
//            case 0:
//                splashImageView.setImageResource(R.mipmap.splash);
//                break;
//            case 1:
//                splashImageView.setImageResource(R.mipmap.splash);
//                break;
//            case 2:
//                splashImageView.setImageResource(R.mipmap.splash);
//                break;
//            case 3:
//                splashImageView.setImageResource(R.mipmap.splash);
//                break;
//            default:
//                break;
//        }

        newsRadioButton.setOnClickListener(this);
        libRadioButton.setOnClickListener(this);
        discoveryRadioButton.setOnClickListener(this);
        moreRadioButton.setOnClickListener(this);

        ScreenUtils.setFullscreen(getWindow());
//        setAlphaAnimation(splashImageView);
        fragmentManager = getSupportFragmentManager();
        setTabSelection(0);
    }

    private void setTabSelection(int index) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:

                if (newsFragment == null) {

                    newsFragment = new NewsFragment();
                    transaction.add(R.id.frame_layout, newsFragment);
                } else {
                    transaction.show(newsFragment);
                }
                break;

            case 1:

                if (libraryFragment == null) {

                    libraryFragment = new LibraryFragment();
                    transaction.add(R.id.frame_layout, libraryFragment);
                } else {
                    transaction.show(libraryFragment);
                }

                break;

            case 2:

                if (courseFragment == null) {

                    courseFragment = new CourseFragment();
                    transaction.add(R.id.frame_layout, courseFragment);
                } else {
                    transaction.show(courseFragment);
                }
                break;

            case 3:

                if (moreFragment == null) {

                    moreFragment = new MoreFragment();
                    transaction.add(R.id.frame_layout, moreFragment);
                } else {
                    transaction.show(moreFragment);
                }
                break;
        }
//		transaction.commit();
        transaction.commitAllowingStateLoss();
    }

    private void hideFragments(FragmentTransaction transaction) {

        if (courseFragment != null) {
            transaction.hide(courseFragment);
        }

        if (newsFragment != null) {
            transaction.hide(newsFragment);
        }

        if (libraryFragment != null) {
            transaction.hide(libraryFragment);
        }

        if (moreFragment != null) {
            transaction.hide(moreFragment);
        }
    }

    private void doubleClickToExit() {

        if ((System.currentTimeMillis() - clickBackButtonTime) > clickToExitInterval) {

            Toast.makeText(getApplicationContext(), R.string.double_click_to_exit, Toast.LENGTH_SHORT).show();
            clickBackButtonTime = System.currentTimeMillis();

        } else {
            finish();
        }
    }

    private void setAlphaAnimation(View view) {

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_animation);
        view.startAnimation(animation);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            doubleClickToExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.nav_news_radio_button:
                setTabSelection(0);
                break;
            case R.id.nav_lib_radio_button:
                setTabSelection(1);
                break;
            case R.id.nav_discovery_radio_button:
                setTabSelection(2);
                break;
            case R.id.nav_more_radio_button:
                setTabSelection(3);
                break;
        }

    }
}

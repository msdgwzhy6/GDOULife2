package com.fedming.gdoulife.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.fedming.gdoulife.R;
import com.fedming.gdoulife.app.AppConfig;

public class LifeInfoActivity extends BaseActivity {

    private int flag;
    private ProgressBar progressBar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_info);

        flag = getIntent().getExtras().getInt("flag");

        initView();
    }

    private void initView() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadsImagesAutomatically(true);
        //微信里面的文章加载是通过js来实现的，所以我们需要设置WebView去设置javaScript可用
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(false);
        if (flag == 0){
            webView.loadUrl("http://mp.weixin.qq.com/s/ieHx8JmpzlfD8_9yRHyyfA");
            progressBar.setVisibility(View.GONE);
        }else if (flag == 1){
            webView.loadUrl("http://mp.weixin.qq.com/s/XWzy_WSeJcAc_FIzFubmfA");
            progressBar.setVisibility(View.GONE);
        }else if (flag == 2){
            webView.loadUrl("http://mp.weixin.qq.com/s/MPKlqbO_EwQQ_Z5deZRZ0A");
            progressBar.setVisibility(View.GONE);
        }else {
            webView.loadDataWithBaseURL(AppConfig.NEWS_BASE_URL, "数据异常，请稍后再试...", "text/html; charset=UTF-8", "utf-8", null);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

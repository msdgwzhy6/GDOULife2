package com.fedming.gdoulife.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.fedming.gdoulife.R;
import com.fedming.gdoulife.utils.LogUtil;
import com.fedming.gdoulife.utils.StringUtils;
import com.fedming.gdoulife.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.Call;

public class BookDetailActivity extends BaseActivity {

    private String bookLink;
    private String title;
    private Document document = null;

    private ProgressBar progressBar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_detail);

        bookLink = getIntent().getExtras().getString("bookLink");
        LogUtil.i("bookLink:" + bookLink);

        initView();
        initData();
    }

    private void initView() {

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        webView = (WebView) findViewById(R.id.books_detail_webView);
    }

    private void initData() {

        if (!StringUtils.isFine(bookLink)){
            return;
        }

        OkHttpUtils
                .get()
                .url(bookLink)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressBar.setVisibility(View.GONE);
                        refreshView(response);
                    }
                });
    }

    private void refreshView(String response) {

//        LogUtil.i(response);

        try {
            document = Jsoup.parse(response);
            title = document.title();
            Elements select = document.select("div.header");
            select.remove();
            Elements select1 = document.select("div.footer");
            select1.remove();
            Elements select2 = document.getElementsByTag("form");
            select2.remove();
            Element select3 = document.select("div.detailList").last();
            select3.remove();
            webView.loadData(document.toString(), "text/html; charset=UTF-8", null);
            setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShort(context,"数据异常，请稍后再试");
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
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

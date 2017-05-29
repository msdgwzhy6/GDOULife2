package com.fedming.gdoulife.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fedming.gdoulife.R;
import com.fedming.gdoulife.app.AppConfig;
import com.fedming.gdoulife.utils.LogUtil;
import com.fedming.gdoulife.utils.NetWorkUtils;
import com.fedming.gdoulife.utils.StringUtils;
import com.fedming.gdoulife.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.util.ArrayList;

import okhttp3.Call;

public class NewsDetailActivity extends BaseActivity {

    private String newsLink;
    private String readNumber;
    private String newsTitle;
    private String newsInfo;
    private String newsContent;
    private ProgressBar progressBar;
    private WebView newsDetailWebView;
    private TextView errorTextView;

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        setContentView(R.layout.activity_news_detail);

        newsLink = getIntent().getExtras().getString("newsLink");
        readNumber = getIntent().getExtras().getString("readNumber");
//        LogUtil.i(String.format("新闻链接：%s", newsLink));
        initView();
        initData();
    }

    private void initData() {
        if (!StringUtils.isFine(newsLink)) {
            return;
        }
        if (!NetWorkUtils.isConnected(mContext)){
            ToastUtils.showLong(mContext,"网络连接异常！");
            newsDetailWebView.setVisibility(View.GONE);
            errorTextView.setVisibility(View.VISIBLE);
        }
        OkHttpUtils
                .get()
                .url(newsLink)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressBar.setVisibility(View.GONE);
                        initNewsParser(response);
                    }
                });
    }

    private void initView() {

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        newsDetailWebView = (WebView) findViewById(R.id.news_detail_webView);
        errorTextView = (TextView) findViewById(R.id.error_textView);
        newsDetailWebView.setDrawingCacheEnabled(true);
        WebSettings webSettings = newsDetailWebView.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS); //支持内容重新布局
        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        requestPermission();
                        getSnapshot();
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 解析、拼接并加载HTML文档
     *
     * @param htmlStr htmlStr
     */
    private void initNewsParser(String htmlStr) {

        if (!StringUtils.isFine(htmlStr)) {
            return;
        }

        try {
            Document doc = Jsoup.parse(htmlStr);
            Elements title = doc.select("div#content_head");
            newsTitle = String.format("<h3 style=\"border-left: solid 4px #3497DA;\"> %s </h3>", title.first().getElementsByTag("h1").text());
            newsInfo = String.format("<p style=\"font-family:arial;color:grey;font-size:12px;\"> %s 浏览：%s 次</p>"
                    , title.first().getElementsByTag("h2").text().split("来源")[0], readNumber);

            Elements article = doc.select("div#endtext");

            //重设图片宽高，不改变文件图标(.gif)的大小。
            Elements pElements = article.first().getElementsByTag("p");
            int pSize = pElements.size();
            for (int i = 0; i < pSize; i++) {
                LogUtil.i("img：" + pElements.get(i).getElementsByTag("img").toString());
                Elements imageElements = pElements.get(i).getElementsByTag("img");
                boolean isGif = imageElements.toString().contains(".gif");
                if (!imageElements.isEmpty() && !isGif) {
                    imageElements.first().removeAttr("height").removeAttr("width").attr("style", "width: 95%");
                }
            }
            //修改table属性，使其适应手机界面，如果有
            Elements tableElements = article.first().getElementsByTag("table");
            int tableSize = tableElements.size();
            for (int i = 0; i < tableSize; i++) {
                tableElements.get(i).removeAttr("width").removeAttr("style")
                        .attr("style", "width: 100%; margin-left: auto; margin-right: auto;");
                Elements tr = article
                        .first()
                        .getElementsByTag("table").get(i)
                        .getElementsByTag("tbody").first().getElementsByTag("tr");
                int trSize = tr.size();
                for (int j = 0; j < trSize; j++) {
                    tr.get(j).removeAttr("style");
                    Elements td = tr.get(j).getElementsByTag("td");
                    td.first().attr("style", "padding: 0px 0px; border: 1px solid windowtext; border-image: none; background-color: transparent;");
                    int tdSize = td.size();
                    for (int k = 0; k < tdSize; k++) {
                        td.get(k).removeAttr("width").attr("padding", "padding: 0px 0px");
                    }
                }
            }

            //增加一个分割线
            String br = "<hr align=center width=100% color=#CBCBCB size=0.5>";
            if (0 != pSize) {
                //html文档的拼接
                newsContent = String.format("<!DOCTYPE html> <body padding: 10px 1px;>%s%s%s%s%s</body> </html>", newsTitle, newsInfo, br, article.html(), "<br>");
                //使用WebView加载数据
                newsDetailWebView.loadDataWithBaseURL(AppConfig.NEWS_BASE_URL, newsContent, "text/html; charset=UTF-8", "utf-8", null);
            } else {
                newsContent = String.format("<!DOCTYPE html> <body padding: 10px 1px;>%s%s%s%s%s</body> </html>", newsTitle, newsInfo, br, "该新闻暂时没有更多细节~", "<br>");
                newsDetailWebView.loadDataWithBaseURL(AppConfig.NEWS_BASE_URL, newsContent, "text/html; charset=UTF-8", "utf-8", null);
                Snackbar.make(newsDetailWebView, "Without detail now ~", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShort(context, "数据异常，请稍后再试");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    /**
     * 当系统版本大于5.0时 开启enableSlowWholeDocumentDraw 获取整个html文档内容
     */
    private void checkSdkVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
    }

    /**
     * 当build target为23时，需要动态申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200:
                boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                Log.d("fdm", "writeAccepted--" + writeAccepted);
                break;

        }
    }

    private void getSnapshot() {
        checkSdkVersion();
        float scale = newsDetailWebView.getScale();
        int webViewHeight = (int) (newsDetailWebView.getContentHeight() * scale + 0.5);
        Bitmap bitmap = Bitmap.createBitmap(newsDetailWebView.getWidth(), webViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        newsDetailWebView.draw(canvas);
        try {
            String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            String fileName = String.format("%s%s%s%s",Environment.getExternalStorageDirectory().getPath(),
                    "/Pictures/news_share_",currentTimeMillis,".jpg" );
//            Log.i("fdm", "getSnapshot: "+fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            //压缩bitmap到输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream);
//            Log.i("fdm", "getByteCount: "+bitmap.getByteCount());
            fileOutputStream.close();
            bitmap.recycle();
            ArrayList<Uri> uris = new ArrayList<>();
            Uri uri = Uri.parse("file://" + fileName);
            uris.add(uri);
            shareToTimeLine("#校园新闻#",uris);
        } catch (Exception e) {
            Log.e("fdm", e.getMessage());
        }
    }

    /**
     * 分享多图到朋友圈，多张图片加文字
     *
     * @param uris
     */
    private void shareToTimeLine(String title, ArrayList<Uri> uris) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");

        intent.putExtra("Kdescription", title);

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivity(intent);
    }
}

package com.fedming.gdoulife.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fedming.gdoulife.R;
import com.fedming.gdoulife.biz.SquareSystemBiz;
import com.fedming.gdoulife.utils.SharedPreferencesUtils;
import com.fedming.gdoulife.utils.StringUtils;
import com.fedming.gdoulife.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import okhttp3.Call;

import static com.fedming.gdoulife.app.AppConfig.HOST;
import static com.fedming.gdoulife.app.AppConfig.REFERER;
import static com.fedming.gdoulife.app.AppConfig.URL_CODE;
import static com.fedming.gdoulife.app.AppConfig.URL_LOGIN;
import static com.fedming.gdoulife.app.AppConfig.USER_ARGENT;

/**
 * 课表
 * Created by Bruce on 2016/9/9.
 */
public class CourseFragment extends BaseFragment implements View.OnClickListener {

    private View courseView;
    private ProgressDialog progressDialog;

    private LinearLayout beforeLoginLinearLayout;
    private EditText accountEditText;
    private EditText passwordEditText;
    private EditText imgCodeEditText;
    private ImageView imgCodeImageView;
    private Button getCourseButton;
    private WebView webView;
    private TextView exitTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getActivity();
        courseView = inflater.inflate(R.layout.fragment_course, container, false);
        initView();

        return courseView;

    }

    private void initView() {

        beforeLoginLinearLayout = (LinearLayout) courseView.findViewById(R.id.before_login_linearLayout);
        exitTextView = (TextView) courseView.findViewById(R.id.exit_course_textView);
        accountEditText = (EditText) courseView.findViewById(R.id.account_editText);
        passwordEditText = (EditText) courseView.findViewById(R.id.password_editText);
        imgCodeEditText = (EditText) courseView.findViewById(R.id.img_code_editText);
        imgCodeImageView = (ImageView) courseView.findViewById(R.id.code_imageView);
        getCourseButton = (Button) courseView.findViewById(R.id.get_course_button);

        webView = (WebView) courseView.findViewById(R.id.course_webView);
        WebSettings settings = webView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setTextSize(WebSettings.TextSize.SMALLER);

        initProgressDialog();

        getCourseButton.setOnClickListener(this);
        imgCodeImageView.setOnClickListener(this);
        exitTextView.setOnClickListener(this);

        String courseHTML = SharedPreferencesUtils.get(mContext, "CourseHTML", "").toString();
        if (StringUtils.isFine(courseHTML)) {
            exitTextView.setVisibility(View.VISIBLE);
            beforeLoginLinearLayout.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadData(courseHTML, "text/html; charset=UTF-8", null);
        } else {
            getCheckCode();
        }

    }

    /**
     * 获取验证码
     */
    private void getCheckCode() {

        OkHttpUtils
                .get()
                .url(URL_CODE)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(mContext, "无法获取验证码");
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        imgCodeImageView.setImageBitmap(response);
                    }
                });
    }

    /**
     * 登录正方系统
     */
    /**
     * 登录并获取课表
     */
    private void doLogin() {

        String account, password, imgCode;
        if (accountEditText.getText().length() != 12) {
            ToastUtils.showShort(mContext, "请输入12位账号");
        } else if (passwordEditText.getText().length() == 0) {
            ToastUtils.showShort(mContext, "请输入密码");
        } else if (imgCodeEditText.getText().length() == 0) {
            ToastUtils.showShort(mContext, "请输入验证码");
        } else {
            progressDialog.show();
            account = accountEditText.getText().toString();
            password = passwordEditText.getText().toString();
            imgCode = imgCodeEditText.getText().toString();

            OkHttpUtils
                    .post()
                    .url(URL_LOGIN)
                    .addHeader("Referer", REFERER)
                    .addHeader("Host", HOST)
                    .addHeader("User-Agent", USER_ARGENT)
                    .addParams("__VIEWSTATE", "dDwtNTE2MjI4MTQ7Oz61L6x6++KxDmUi3mVHED4viE+96g==")
                    .addParams("txtUserName", account)
                    .addParams("TextBox2", password)
                    .addParams("txtSecretCode", imgCode)
                    .addParams("RadioButtonList1", "学生")
                    .addParams("Button1", "")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(String response, int id) {
//                            Log.i(TAG, "Login Response: " + response);
                            getCourse(response);
                        }
                    });
        }

    }

    /**
     * 获取课表
     */

    private void getCourse(String content) {

        try {
            String url = SquareSystemBiz.parseMenu(content).get("学生个人课表");
            OkHttpUtils
                    .get()
                    .url(url)
                    .addHeader("Referer", REFERER)
                    .addHeader("Host", HOST)
                    .addHeader("User-Agent", USER_ARGENT)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            progressDialog.dismiss();
                            beforeLoginLinearLayout.setVisibility(View.GONE);
                            webView.setVisibility(View.VISIBLE);
                            exitTextView.setVisibility(View.VISIBLE);
                            Document document = Jsoup.parse(response);
                            Element element = document.getElementById("Table1");
                            String part1 = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>课程表</title></head><body><style type=\"text/css\">table{border-collapse: collapse;  }table, td, th {border: 1px solid #bdbdbd;}</style>";
                            String html = String.format("%s%s%s", part1, element.toString(), "</body></html>");
                            SharedPreferencesUtils.put(mContext, "CourseHTML", html);
                            webView.loadData(html, "text/html; charset=UTF-8", null);

                            Log.i(TAG, "Course Response: " + element.toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initProgressDialog() {

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在获取中,请稍后...");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.get_course_button:
                doLogin();
                break;
            case R.id.code_imageView:
                getCheckCode();
                break;
            case R.id.exit_course_textView:
                webView.loadData("", "text/html; charset=UTF-8", null);
                webView.setVisibility(View.GONE);
                exitTextView.setVisibility(View.INVISIBLE);
                beforeLoginLinearLayout.setVisibility(View.VISIBLE);
                getCheckCode();
                break;
            default:
                break;
        }
    }

}

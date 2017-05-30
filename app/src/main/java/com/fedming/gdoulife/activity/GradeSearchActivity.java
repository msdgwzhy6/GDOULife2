package com.fedming.gdoulife.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.fedming.gdoulife.R;
import com.fedming.gdoulife.adapter.GradeRecyclerViewAdapter;
import com.fedming.gdoulife.biz.SquareSystemBiz;
import com.fedming.gdoulife.utils.LogUtil;
import com.fedming.gdoulife.utils.SharedPreferencesUtils;
import com.fedming.gdoulife.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.fedming.gdoulife.app.AppConfig.HOST;
import static com.fedming.gdoulife.app.AppConfig.REFERER;
import static com.fedming.gdoulife.app.AppConfig.URL_CODE;
import static com.fedming.gdoulife.app.AppConfig.URL_LOGIN;
import static com.fedming.gdoulife.app.AppConfig.USER_ARGENT;

public class GradeSearchActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout loginLinearLayout;
    private EditText accountEditText;
    private EditText passwordEditText;
    private EditText imgCodeEditText;
    private ImageView imgCodeImageView;
    private Spinner spinner;
    private CheckBox checkBox;
    private Button loginButton;
    private ProgressDialog progressDialog;
    private WebView webView;

    private View headerView;

    public String TAG = "fdm";
    private String XUENIAN;
    private String XUEQI;
    private boolean isRemember = true;

    private TextView userNameTextView;
    //    private Button exitButton;
    private RecyclerView gradeRecyclerView;
    private LinearLayout contentLinearLayout;

    private GradeRecyclerViewAdapter adapter;
    private List<Map<String, String>> courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        initView();

        getCheckCode();
    }

    private void initView() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginLinearLayout = (LinearLayout) findViewById(R.id.login_linearLayout);
        accountEditText = (EditText) findViewById(R.id.account_editText);
        passwordEditText = (EditText) findViewById(R.id.password_editText);
        imgCodeEditText = (EditText) findViewById(R.id.img_code_editText);
        imgCodeImageView = (ImageView) findViewById(R.id.code_imageView);
        spinner = (Spinner) findViewById(R.id.spinner);
        checkBox = (CheckBox) findViewById(R.id.is_remember_pwd_checkBox);
        loginButton = (Button) findViewById(R.id.login_button);

        contentLinearLayout = (LinearLayout) findViewById(R.id.content_linearLayout);
//        userNameTextView = (TextView) findViewById(R.id.userName_textView);
        gradeRecyclerView = (RecyclerView) findViewById(R.id.grade_recyclerView);
        gradeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new GradeRecyclerViewAdapter();
        gradeRecyclerView.setAdapter(adapter);

        webView = (WebView) findViewById(R.id.grade_webView);
//        exitButton = (Button)findViewById(R.id.exit_button);

        headerView = LayoutInflater.from(mContext).inflate(R.layout.activity_grade_head_view, null);
        userNameTextView = (TextView) headerView.findViewById(R.id.userName_textView);
        initProgressDialog();
        imgCodeImageView.setOnClickListener(this);
        loginButton.setOnClickListener(this);
//        exitButton.setOnClickListener(this);

        //获取保存的账号密码
        String savedAccount = String.valueOf(SharedPreferencesUtils.get(mContext, "squareSystemAccount", ""));
        String savedPassword = String.valueOf(SharedPreferencesUtils.get(mContext, "squareSystemPassword", ""));
        accountEditText.setText(savedAccount);
        passwordEditText.setText(savedPassword);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRemember = isChecked;
            }
        });

        String[] items = {"2014-2015 第一学期", "2014-2015 第二学期", "2015-2016 第一学期", "2015-2016 第二学期"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        XUENIAN = "2014-2015";
                        XUEQI = "1";
                        break;
                    case 1:
                        XUENIAN = "2014-2015";
                        XUEQI = "2";
                        break;
                    case 2:
                        XUENIAN = "2015-2016";
                        XUEQI = "1";
                        break;
                    case 3:
                        XUENIAN = "2015-2016";
                        XUEQI = "2";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                XUENIAN = "2015-2016";
                XUEQI = "2";
            }
        });
    }

    /**
     * 登录并获取课表
     */
    private void doLogin() {

        String account, password, imgCode;

        if (accountEditText.getText().length() == 0) {
            ToastUtils.showShort(mContext, "请输入账号");
        } else if (passwordEditText.getText().length() == 0) {
            ToastUtils.showShort(mContext, "请输入密码");
        } else if (imgCodeEditText.getText().length() == 0) {
            ToastUtils.showShort(mContext, "请输入验证码");
        } else {
            progressDialog.show();
            account = accountEditText.getText().toString();
            password = passwordEditText.getText().toString();
            imgCode = imgCodeEditText.getText().toString();

            if (isRemember) {
                SharedPreferencesUtils.put(mContext, "squareSystemAccount", account);
                SharedPreferencesUtils.put(mContext, "squareSystemPassword", password);
            } else {
                SharedPreferencesUtils.put(mContext, "squareSystemAccount", account);
                SharedPreferencesUtils.put(mContext, "squareSystemPassword", "");
            }

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
                            Log.i(TAG, "Login Response: " + response);
                            getViewStateValue(response);
                        }
                    });
        }

    }

    private void getViewStateValue(final String content) {
        String url = SquareSystemBiz.parseMenu(content).get("成绩查询");
        if (TextUtils.isEmpty(url)) {
            progressDialog.dismiss();
            ToastUtils.showLong(mContext, "查询失败，请检查你的账号密码！");
            return;
        }
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
                        String viewstate = Jsoup.parse(response).select("input[name=__VIEWSTATE]").val();
                        LogUtil.d(viewstate);
                        getGrade(content, viewstate);
                    }
                });
    }

    private void getGrade(String content, String VIEWSTATE) {
        try {
            String url = SquareSystemBiz.parseMenu(content).get("成绩查询");
            if (TextUtils.isEmpty(url)) {
                progressDialog.dismiss();
                ToastUtils.showLong(mContext, "查询失败，请检查你的账号密码验证码！");
                return;
            }
            Log.i(TAG, "course url: " + url);
            OkHttpUtils
                    .post()
                    .url(url)
                    .addHeader("Referer", REFERER)
                    .addHeader("Host", HOST)
                    .addHeader("User-Agent", USER_ARGENT)
                    .addParams("__VIEWSTATE", VIEWSTATE)
                    .addParams("ddlXN", XUENIAN)
                    .addParams("ddlXQ", XUEQI)
                    .addParams("btn_xq", "学期成绩")
                    .addParams("", "")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            progressDialog.dismiss();

                        }

                        @Override
                        public void onResponse(String response, int id) {


                            try {
                                Document document = Jsoup.parse(response);

                                Element nameElement = document.getElementById("Table1").getElementsByTag("tr").get(1);
                                String name = nameElement.getElementsByTag("td").get(1).text().replace("姓名：", "");
                                Log.i(TAG, "name: " + name);
                                Element element = document.getElementById("Datagrid1");
//                                String part1 = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>课程表</title></head><body><style type=\"text/css\">table{border-collapse: collapse;  }table, td, th {border: 1px solid #bdbdbd;}</style>";
//                                String html = String.format("%s%s%s", part1, element.toString(), "</body></html>");
//                                webView.loadData(html, "text/html; charset=UTF-8", null);

                                int courseSize = element.getElementsByTag("tr").size();
                                Elements elementsCourse = element.getElementsByTag("tr");
                                courseList = new ArrayList<>();
                                for (int i = 1; i < courseSize; i++) {

                                    Map<String, String> map = new HashMap<>();
                                    Elements td = elementsCourse.get(i).getElementsByTag("td");
                                    for (int j = 0; j < td.size(); j++) {
                                        map.put(String.valueOf(j), td.get(j).text());
                                    }
                                    courseList.add(map);
                                }
                                adapter.addDatas(courseList);
                                adapter.setHeaderView(headerView);
                                userNameTextView.setText(name);

                                loginLinearLayout.setVisibility(View.GONE);
                                contentLinearLayout.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();

                            } catch (Exception e) {
                                ToastUtils.showShort(mContext, "数据异常，请稍后再试");
                                progressDialog.dismiss();
                            } finally {
                                progressDialog.dismiss();
                            }
                        }
                    });
        } catch (Exception e) {
            ToastUtils.showShort(mContext, "数据异常，请稍后再试");
            progressDialog.dismiss();
            e.printStackTrace();
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
            case R.id.login_button:
                doLogin();
                break;
            case R.id.code_imageView:
                getCheckCode();
                break;
            case R.id.exit_button:
                contentLinearLayout.setVisibility(View.GONE);
                loginLinearLayout.setVisibility(View.VISIBLE);
                break;
            default:
                break;
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

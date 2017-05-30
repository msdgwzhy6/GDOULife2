package com.fedming.gdoulife.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fedming.gdoulife.R;
import com.fedming.gdoulife.adapter.BorrowedBooksRecyclerViewAdapter;
import com.fedming.gdoulife.app.AppConfig;
import com.fedming.gdoulife.utils.SharedPreferencesUtils;
import com.fedming.gdoulife.utils.StringUtils;
import com.fedming.gdoulife.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
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

import static com.fedming.gdoulife.app.AppConfig.LIBRARY_FURL;
import static com.fedming.gdoulife.app.AppConfig.LIBRARY_RENEW_URL;
import static com.fedming.gdoulife.app.AppConfig.LOGIN_LIBRARY_URL;
import static com.fedming.gdoulife.app.AppConfig.USER_ARGENT;
import static com.zhy.http.okhttp.OkHttpUtils.post;

/**
 * 我的图书馆
 * Created by Bruce on 2016/9/11.
 */
public class LibraryHomeFragment extends BaseFragment {

    private View libraryHomeView;
    private LinearLayout loginLinearlayout;
    private LinearLayout contentLinearlayout;
    private EditText accountEditText;
    private EditText passwordEditText;
    private CheckBox checkBox;
    private Button loginButton;
    private Button exitButton;
    private ProgressDialog progressDialog;

    private TextView userNameTextView;
    private TextView errorTextView;
    private RecyclerView borrowedBookRecyclerView;

    private String account;
    private String password;
    private String jsessionid;
    public String TAG = "fdm";
    private boolean isRemember = true;

    private BorrowedBooksRecyclerViewAdapter adapter;

    public static LibraryHomeFragment newInstance(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);

        LibraryHomeFragment libraryHomeFragment = new LibraryHomeFragment();
        libraryHomeFragment.setArguments(bundle);

        return libraryHomeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getActivity();
        libraryHomeView = inflater.inflate(R.layout.fragment_library_home, container, false);

        initView();
        return libraryHomeView;
    }

    private void initView() {

        initProgressDialog();
        loginLinearlayout = (LinearLayout) libraryHomeView.findViewById(R.id.login_linearLayout);
        contentLinearlayout = (LinearLayout) libraryHomeView.findViewById(R.id.content_linearLayout);
        accountEditText = (EditText) libraryHomeView.findViewById(R.id.account_editText);
        passwordEditText = (EditText) libraryHomeView.findViewById(R.id.password_editText);
        checkBox = (CheckBox) libraryHomeView.findViewById(R.id.is_remember_pwd_checkBox);
        loginButton = (Button) libraryHomeView.findViewById(R.id.login_library_button);
        exitButton = (Button) libraryHomeView.findViewById(R.id.exit_button);

        userNameTextView = (TextView) libraryHomeView.findViewById(R.id.userName_textView);
        errorTextView = (TextView) libraryHomeView.findViewById(R.id.lib_error_textView);
        borrowedBookRecyclerView = (RecyclerView) libraryHomeView.findViewById(R.id.borrowed_book_recyclerView);
        borrowedBookRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new BorrowedBooksRecyclerViewAdapter();
        borrowedBookRecyclerView.setAdapter(adapter);

        //获取保存的账号密码
        String savedAccount = String.valueOf(SharedPreferencesUtils.get(mContext, "libAccount", ""));
        String savedPassword = String.valueOf(SharedPreferencesUtils.get(mContext, "libPassword", ""));
        accountEditText.setText(savedAccount);
        passwordEditText.setText(savedPassword);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRemember = isChecked;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (accountEditText.getText().length() == 0 || passwordEditText.getText().length() == 0) {
                    Toast.makeText(mContext, "请先输入账号密码", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    account = accountEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    loginLibrary(account, password);

                    if (isRemember) {
                        SharedPreferencesUtils.put(mContext, "libAccount", account);
                        SharedPreferencesUtils.put(mContext, "libPassword", password);
                    } else {
                        SharedPreferencesUtils.put(mContext, "libAccount", account);
                        SharedPreferencesUtils.put(mContext, "libPassword", "");
                    }
                }

            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLinearlayout.setVisibility(View.VISIBLE);
                contentLinearlayout.setVisibility(View.GONE);
            }
        });

    }

    private void initProgressDialog() {

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在登录中,请稍后...");
    }

    /**
     * 登陆图书馆
     */
    private void loginLibrary(String account, String password) {
        post()
                .url(LOGIN_LIBRARY_URL)
                .addHeader("Referer", LOGIN_LIBRARY_URL)
                .addHeader("User-Agent", USER_ARGENT)
                .addParams("rdid", account)
                .addParams("rdPasswd", StringUtils.parseStrToMd5L32(password))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        ToastUtils.showShort(mContext, "系统异常~");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        getUserInformation();
                        Log.i(TAG, "onResponse: " + response);
                    }
                });
    }

    /**
     * 获取登陆后的用户数据
     */
    private void getUserInformation() {
        OkHttpUtils
                .get()
                .addHeader("Referer", AppConfig.LIBRARY_USER_CENTER_URL)
                .addHeader("User-Agent", USER_ARGENT)
                .url(AppConfig.LIBRARY_USER_CENTER_URL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(mContext, "数据异常~");
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        refreshView(response);
                        progressDialog.dismiss();
                    }
                });
    }

    private void refreshView(String response) {

        if (!StringUtils.isFine(response)) {
            return;
        }
        List<Map<String, String>> bookList = null;
        Element element;
//        String accountNumber = null;
        String accountName = "";
        Document document = Jsoup.parse(response);
        try {
            /**
             * 获取账号，姓名
             */
            element = document.getElementById("right_div");
            Elements elements = element.children().first().children().get(4).getElementsByTag("table").first().getElementsByTag("tbody").first().getElementsByTag("tr").first().getElementsByTag("td");
            //账号
//            accountNumber = elements.get(0).text();
            //姓名
            accountName = elements.get(1).text().split("】")[1];
            Log.i(TAG, "accountName: " + accountName);
            userNameTextView.setText(accountName);

            if (StringUtils.isFine(accountName)) {
                ToastUtils.showShort(mContext, "登录成功~");
                loginLinearlayout.setVisibility(View.GONE);
                contentLinearlayout.setVisibility(View.VISIBLE);
            }
            /**
             * 获取借阅图书详细信息
             */
            Element elementBook = element.getElementById("loanList").getElementById("contentTable");
            int bookSize = elementBook.getElementsByTag("tr").size();
            Elements elementsBook = elementBook.getElementsByTag("tr");
            bookList = new ArrayList<>();
            for (int i = 1; i < bookSize; i++) {
                Map<String, String> map = new HashMap<>();
                Elements td = elementsBook.get(i).getElementsByTag("td");
                for (int j = 0; j < td.size(); j++) {
                    map.put(String.valueOf(j), td.get(j).text());
                }
                bookList.add(map);
            }
            adapter.addDatas(bookList);
//            final List<Map<String, String>> finalBookList = bookList;
//            adapter.setItemClickListener(new BaseRecycleViewHolderView.MyItemClickListener() {
//                @Override
//                public void onItemClick(View view, int position) {
//                    String bookNo = finalBookList.get(position).get("0");
//                    doRenew(bookNo);
//                }
//            });
        } catch (Exception e) {
            Log.i(TAG, "Data Error");
        } finally {
            errorTextView.setVisibility(View.VISIBLE);
        }
    }

    private void doRenew(String bookNo) {

        progressDialog.setMessage("正在续借中，请稍后...");
        progressDialog.show();
        OkHttpUtils
                .post()
                .url(LIBRARY_RENEW_URL)
                .addHeader("Referer", LOGIN_LIBRARY_URL)
                .addHeader("User-Agent", USER_ARGENT)
                .addParams("barcodeList", bookNo)
                .addParams("furl", LIBRARY_FURL)
                .addParams("renewAll", "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        ToastUtils.showShort(mContext, "系统异常~");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressDialog.dismiss();
                        ToastUtils.showShort(mContext, "续借成功~");
                        Log.i(TAG, "onResponse: " + response);
                    }
                });
    }
}

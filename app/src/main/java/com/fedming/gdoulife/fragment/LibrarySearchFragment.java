package com.fedming.gdoulife.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.fedming.gdoulife.R;
import com.fedming.gdoulife.activity.BookSearchResultActivity;
import com.fedming.gdoulife.adapter.BaseRecycleViewHolderView;
import com.fedming.gdoulife.adapter.LibraryRecyclerViewAdapter;
import com.fedming.gdoulife.app.AppConfig;
import com.fedming.gdoulife.biz.LibraryHomePageBiz;
import com.fedming.gdoulife.utils.StringUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 图书检索
 * Created by Bruce on 2016/9/11.
 */
public class LibrarySearchFragment extends BaseFragment {

    private View librarySearchView;
    private TextView errorTextView;
    private SearchView searchView;
    private ProgressBar progressBar;
    private RecyclerView libHotSearchRecyclerView;
    private List<Map<String, String>> list;

    public static LibrarySearchFragment newInstance(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);

        LibrarySearchFragment librarySearchFragment = new LibrarySearchFragment();
        librarySearchFragment.setArguments(bundle);

        return librarySearchFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getActivity();
        librarySearchView = inflater.inflate(R.layout.fragment_library_search, container, false);

        initView();
        initData();
        return librarySearchView;
    }

    private void initView() {

        errorTextView = (TextView) librarySearchView.findViewById(R.id.lib_error_textView);
        progressBar = (ProgressBar) librarySearchView.findViewById(R.id.progress_bar);
        searchView = (SearchView) librarySearchView.findViewById(R.id.search_view);
        libHotSearchRecyclerView = (RecyclerView) librarySearchView.findViewById(R.id.library_hot_search_recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        libHotSearchRecyclerView.setLayoutManager(linearLayoutManager);

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent intent = new Intent(context, BookSearchResultActivity.class);
                intent.putExtra("keyWord", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void initData() {

        OkHttpUtils
                .get()
                .url(AppConfig.LIBRARY_HOME)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressBar.setVisibility(View.GONE);
                        errorTextView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressBar.setVisibility(View.GONE);
                        if (errorTextView.isShown()) {
                            errorTextView.setVisibility(View.GONE);
                        }
                        refreshView(response);
                    }
                });
    }

    private void refreshView(String response) {

        list = new ArrayList<>();
        list.addAll(LibraryHomePageBiz.parseHotItems(response));
//        Log.i("fdm", "refreshViewList: "+list.size());
        LibraryRecyclerViewAdapter libraryRecyclerViewAdapter = new LibraryRecyclerViewAdapter();
        libraryRecyclerViewAdapter.addDatas(list);
        libraryRecyclerViewAdapter.setItemClickListener(new BaseRecycleViewHolderView.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (StringUtils.isFine(list.get(position).get("bookLink"))) {

                    Intent intent = new Intent(context, BookSearchResultActivity.class);
                    String keyWord = null;
                    try {
                        keyWord = list.get(position).get("bookLink").split("kw=")[1].split("&searchtype")[0];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("keyWord", keyWord.trim());
                    startActivity(intent);
                }
            }
        });

        libHotSearchRecyclerView.setAdapter(libraryRecyclerViewAdapter);
    }
}

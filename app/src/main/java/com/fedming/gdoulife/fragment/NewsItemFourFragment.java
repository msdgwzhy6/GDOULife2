package com.fedming.gdoulife.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fedming.gdoulife.R;
import com.fedming.gdoulife.activity.NewsDetailActivity;
import com.fedming.gdoulife.adapter.BaseRecycleViewHolderView;
import com.fedming.gdoulife.adapter.NewsRecyclerViewAdapter;
import com.fedming.gdoulife.app.AppConfig;
import com.fedming.gdoulife.biz.NewsItemBiz;
import com.fedming.gdoulife.utils.NetWorkUtils;
import com.fedming.gdoulife.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;

/**
 * 公示公告
 * Created by Bruce on 2016/9/9.
 */
public class NewsItemFourFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private View contentView;
    private Context context;
    private RecyclerView newsRecyclerView;
    private NewsRecyclerViewAdapter newsRecyclerViewAdapter;

    private int currentPage = 1;
    private List<Map<String, String>> newsList = new ArrayList<>();

    public static NewsItemFourFragment newInstance(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);

        NewsItemFourFragment newsItemFourFragment = new NewsItemFourFragment();
        newsItemFourFragment.setArguments(bundle);

        return newsItemFourFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        contentView = inflater.inflate(R.layout.fragment_news_content, container, false);
        context = getActivity();
        initView();
        initData(currentPage);
        return contentView;
    }

    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipe_refresh_layout);
        newsRecyclerView = (RecyclerView) contentView.findViewById(R.id.fragment_news_recyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        newsRecyclerViewAdapter = new NewsRecyclerViewAdapter(context);
        newsRecyclerView.setAdapter(newsRecyclerViewAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.light_blue_600),
                getResources().getColor(R.color.green_300),
                getResources().getColor(R.color.orange_600));
        swipeRefreshLayout.setOnRefreshListener(this);

        //第一次进入页面的时候显示加载进度条
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
    }

    private void initData(int newsPage) {

        String newsListUrl = String.format(Locale.CHINA, "%s&page=%d", AppConfig.NEWS_GONGGAO, newsPage);
        if (!NetWorkUtils.isConnected(mContext)){
            ToastUtils.showLong(mContext,"网络连接异常，请检查后重试！");
            swipeRefreshLayout.setRefreshing(false);
        }else {
            OkHttpUtils
                    .get()
                    .url(newsListUrl)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            swipeRefreshLayout.setRefreshing(false);
                            refreshView(response);
                        }
                    });
        }
    }


    private void refreshView(String response) {

        newsList.addAll(NewsItemBiz.parseNewsItems(response));
        newsRecyclerViewAdapter.addDatas(newsList);
        newsRecyclerViewAdapter.setItemClickListener(new BaseRecycleViewHolderView.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(context, NewsDetailActivity.class)
                        .putExtra("readNumber", newsList.get(position).get("number"))
                        .putExtra("newsLink", newsList.get(position).get("href")));
            }
        });
        newsRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItem = manager.getItemCount();
                    if (lastVisibleItem >= (totalItem - 6)) {
                        currentPage++;
                        initData(currentPage);
                    }
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        newsRecyclerViewAdapter.removeDatas();
        initData(currentPage);
        Toast.makeText(context,"已成功刷新新闻列表~",Toast.LENGTH_SHORT).show();
    }
}

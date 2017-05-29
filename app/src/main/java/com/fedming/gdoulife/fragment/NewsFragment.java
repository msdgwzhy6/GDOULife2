package com.fedming.gdoulife.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fedming.gdoulife.R;
import com.fedming.gdoulife.adapter.NewsTabLayoutAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻
 * Created by Bruce on 2016/9/9.
 */
public class NewsFragment extends BaseFragment {

    private View newsView;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private List<String> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getActivity();
        newsView = inflater.inflate(R.layout.fragment_news,container,false);
        initView();

        return newsView;

    }

    private void initView() {

        tabLayout = (TabLayout) newsView.findViewById(R.id.title_bar_TabLayout);
        viewPager = (ViewPager) newsView.findViewById(R.id.fragment_news_content_ViewPager);

        list.add("综 合");
        list.add("科 教");
        list.add("校 园");
        list.add("公 告");

        NewsTabLayoutAdapter newsTabLayoutAdapter = new NewsTabLayoutAdapter(getActivity().getSupportFragmentManager(), list);
        viewPager.setAdapter(newsTabLayoutAdapter);

        tabLayout.setupWithViewPager(viewPager);
        /**
         * @deprecated Use {@link #setupWithViewPager(ViewPager)} to link a TabLayout with a ViewPager
         * together. When that method is used, the TabLayout will be automatically updated
         * when the {@link PagerAdapter} is changed.
         * @Deprecated
         * public void setTabsFromPagerAdapter(@Nullable final PagerAdapter adapter) {
         *     setPagerAdapter(adapter, false);
         * }
         */
        tabLayout.setTabsFromPagerAdapter(newsTabLayoutAdapter);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

    }
}

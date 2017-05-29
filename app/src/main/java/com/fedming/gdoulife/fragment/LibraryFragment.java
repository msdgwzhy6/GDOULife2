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
import com.fedming.gdoulife.adapter.LibraryTabLayoutAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 图书馆
 * Created by Bruce on 2016/9/9.
 */
public class LibraryFragment extends BaseFragment {

    private View libraryView;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private List<String> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getActivity();
        libraryView = inflater.inflate(R.layout.fragment_library,container,false);
        initView();

        return libraryView;

    }

    private void initView() {

        tabLayout = (TabLayout) libraryView.findViewById(R.id.title_bar_TabLayout);
        viewPager = (ViewPager) libraryView.findViewById(R.id.fragment_library_content_ViewPager);

        list.add("图书检索");
        list.add("我的图书馆");

        LibraryTabLayoutAdapter libraryTabLayoutAdapter = new LibraryTabLayoutAdapter(getActivity().getSupportFragmentManager(), list);
        viewPager.setAdapter(libraryTabLayoutAdapter);

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
        tabLayout.setTabsFromPagerAdapter(libraryTabLayoutAdapter);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

    }
}

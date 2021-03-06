package com.fedming.gdoulife.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fedming.gdoulife.fragment.NewsItemFourFragment;
import com.fedming.gdoulife.fragment.NewsItemOneFragment;
import com.fedming.gdoulife.fragment.NewsItemThreeFragment;
import com.fedming.gdoulife.fragment.NewsItemTwoFragment;

import java.util.List;

/**
 * NewsTabLayoutAdapter
 * Created by Android-3 on 2016/8/13.
 */
public class NewsTabLayoutAdapter extends FragmentPagerAdapter {

    private List<String> list;
    private NewsItemOneFragment newsItemOneFragment;
    private NewsItemTwoFragment newsItemTwoFragment;
    private NewsItemThreeFragment newsItemThreeFragment;
    private NewsItemFourFragment newsItemFourFragment;

    public NewsTabLayoutAdapter(FragmentManager fragmentManager, List<String> list) {

        super(fragmentManager);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:

                if (newsItemOneFragment == null) {
                    newsItemOneFragment = NewsItemOneFragment.newInstance(position);
                }
                return newsItemOneFragment;

            case 1:

                if (newsItemTwoFragment == null) {
                    newsItemTwoFragment = NewsItemTwoFragment.newInstance(position);
                }
                return newsItemTwoFragment;

            case 2:

                if (newsItemThreeFragment == null) {
                    newsItemThreeFragment = NewsItemThreeFragment.newInstance(position);
                }
                return newsItemThreeFragment;

            case 3:

                if (newsItemFourFragment == null) {
                    newsItemFourFragment = NewsItemFourFragment.newInstance(position);
                }
                return newsItemFourFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position);
    }
}

package com.fedming.gdoulife.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fedming.gdoulife.fragment.LibraryHomeFragment;
import com.fedming.gdoulife.fragment.LibrarySearchFragment;

import java.util.List;

/**
 * LibraryTabLayoutAdapter
 * Created by Android-3 on 2016/9/11.
 */
public class LibraryTabLayoutAdapter extends FragmentPagerAdapter {

    private List<String> list;
    private LibrarySearchFragment librarySearchFragment;
    private LibraryHomeFragment libraryHomeFragment;

    public LibraryTabLayoutAdapter(FragmentManager fragmentManager, List<String> list) {

        super(fragmentManager);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:

                if (librarySearchFragment == null) {
                    librarySearchFragment = LibrarySearchFragment.newInstance(position);
                }
                return librarySearchFragment;

            case 1:

                if (libraryHomeFragment == null) {
                    libraryHomeFragment = LibraryHomeFragment.newInstance(position);
                }
                return libraryHomeFragment;
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

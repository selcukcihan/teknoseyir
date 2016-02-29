package com.selcukcihan.android.teknoseyir;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by SELCUKCI on 29.2.2016.
 */
public class PlaylistPagerAdapter extends FragmentPagerAdapter {
    private List<String> mPages;

    public PlaylistPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);

        mPages = new LinkedList<String>();
        mPages.add("Karma");
        mPages.add("Bilgisayar");
        mPages.add("Telefon");
        mPages.add("Gündem");
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return mPages.size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        return PlaylistFragment.newInstance(mPages.get(position));
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return mPages.get(position);
    }
}

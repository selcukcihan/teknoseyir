package com.selcukcihan.android.teknoseyir;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.api.services.youtube.YouTube;

import java.nio.channels.Channel;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by SELCUKCI on 29.2.2016.
 */
public class ChannelPagerAdapter extends FragmentPagerAdapter {
    private final Context mContext;
    private List<Playlist> mPages;

    public ChannelPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);

        mContext = context;

        mPages = new LinkedList<Playlist>();
        mPages.add(new Playlist(
                mContext.getResources().getString(R.string.karma),
                "https://www.googleapis.com/youtube/v3/channels?part=contentDetails&forUsername=teknoseyir&key={" + DeveloperKey.DEVELOPER_KEY + "}"));
        mPages.add(new Playlist(mContext.getResources().getString(R.string.bilgisayar), ""));
        mPages.add(new Playlist(mContext.getResources().getString(R.string.telefon), ""));
        mPages.add(new Playlist(mContext.getResources().getString(R.string.gundem), ""));
    }



    // Returns total number of pages
    @Override
    public int getCount() {
        return mPages.size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        return ChannelFragment.newInstance(mPages.get(position));
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return mPages.get(position).getName();
    }
}

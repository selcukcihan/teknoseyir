package com.selcukcihan.android.teknoseyir;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

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

    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<Fragment>();

    public ChannelPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);

        mContext = context;

        mPages = new LinkedList<Playlist>();
        mPages.add(new Playlist(mContext.getResources().getString(R.string.karma), "UUFIHrIGT0MBMRHzQtmzOWlQ"));
        mPages.add(new Playlist(mContext.getResources().getString(R.string.bilgisayar), "PLImzRKDoJEgEoIe2v_z706686pSFZtjt-"));
        mPages.add(new Playlist(mContext.getResources().getString(R.string.telefon), "PLImzRKDoJEgHq9nQFuJEZoZEMjPA2r-NV"));
        mPages.add(new Playlist(mContext.getResources().getString(R.string.gundem), "PLImzRKDoJEgHyeIWtq60bav801SWwt515"));
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
    }
}

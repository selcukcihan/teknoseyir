package com.selcukcihan.android.teknoseyir;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
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
        mPages.add(new Playlist(mContext.getResources().getString(R.string.karma), "UUFIHrIGT0MBMRHzQtmzOWlQ",
                R.drawable.ic_home_white_24dp));
        mPages.add(new Playlist(mContext.getResources().getString(R.string.bilgisayar), "PLImzRKDoJEgEoIe2v_z706686pSFZtjt-",
                R.drawable.ic_computer_white_24dp));
        mPages.add(new Playlist(mContext.getResources().getString(R.string.telefon), "PLImzRKDoJEgHq9nQFuJEZoZEMjPA2r-NV",
                R.drawable.ic_smartphone_white_24dp));
        mPages.add(new Playlist(mContext.getResources().getString(R.string.gundem), "PLImzRKDoJEgHyeIWtq60bav801SWwt515",
                R.drawable.ic_chrome_reader_mode_white_24dp));
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return mPages.size();
    }

    public Playlist getPlaylistItem(int position) {
        return mPages.get(position);
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        return ChannelFragment.newInstance(mPages.get(position));
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        //return mPages.get(position).getName()
        Drawable image = ContextCompat.getDrawable(mContext, mPages.get(position).getIconId());
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;

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

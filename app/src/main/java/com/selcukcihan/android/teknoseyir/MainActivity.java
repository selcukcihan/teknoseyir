package com.selcukcihan.android.teknoseyir;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnFullscreenListener {
    private ChannelPagerAdapter mPagerAdapter;
    private TabPageChangeListener mTabPageChangeListener;
    private ViewPager mPager;
    private Toolbar mToolbar;

    private boolean mIsFullscreen = false;
    /** The request code when calling startActivityForResult to recover from an API service error. */
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(generateTitle());
        setSupportActionBar(mToolbar);

        int previousTabPosition = 0;
        if (savedInstanceState != null) {
            previousTabPosition = savedInstanceState.getInt("previousTabPosition", 0);
        }
        setupPagers(previousTabPosition);

        mToolbar.setSubtitle(generateSubtitle(mPagerAdapter.getPlaylistItem(0).getName()));

        checkYouTubeApi();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("previousTabPosition", mTabPageChangeListener.mPreviousPosition);
    }

    private void doLayout() {
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        ((TabLayout) findViewById(R.id.tabs)).setVisibility(mIsFullscreen ? View.GONE : View.VISIBLE);
        ((ChannelFragment) mPagerAdapter.getRegisteredFragment(mPager.getCurrentItem())).layout(isPortrait, mIsFullscreen);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doLayout();
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.mIsFullscreen = isFullscreen;
        doLayout();
    }

    public void onClickClose(View view) {
        ((ChannelFragment) mPagerAdapter.getRegisteredFragment(mPager.getCurrentItem())).onClickClose(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Recreate the activity if user performed a recovery action
            recreate();
        }
    }

    private void checkYouTubeApi() {
        YouTubeInitializationResult errorReason =
                YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else if (errorReason != YouTubeInitializationResult.SUCCESS) {
            String errorMessage =
                    String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private CharSequence generateSubtitle(String text) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimaryDark)), 0, text.length(), 0);
        builder.append(spannable);
        return builder;
    }

    private CharSequence generateTitle() {
        String [] titleWords = getResources().getString(R.string.app_name).split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");

        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString tekno = new SpannableString(titleWords[0].toLowerCase());
        tekno.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleWords[0].length(), 0);
        builder.append(tekno);

        SpannableString seyir = new SpannableString(titleWords[1].toLowerCase());
        seyir.setSpan(new ForegroundColorSpan(Color.WHITE), 0, titleWords[1].length(), 0);
        seyir.setSpan(new BackgroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary)), 0, titleWords[1].length(), 0);
        builder.append(seyir);

        return builder;
    }

    public boolean currentFragmentIs(Fragment fragment) {
        return fragment == mPagerAdapter.getItem(mPager.getCurrentItem());
    }

    private void setupPagers(int previousTabPosition) {
        mPagerAdapter = new ChannelPagerAdapter(this, getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.top_pager);
        mPager.setAdapter(mPagerAdapter);

        mTabPageChangeListener = new TabPageChangeListener(previousTabPosition);
        mPager.addOnPageChangeListener(mTabPageChangeListener);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);
    }

    private final class TabPageChangeListener implements ViewPager.OnPageChangeListener {
        private int mPreviousPosition;

        public TabPageChangeListener(int previousPosition) {
            mPreviousPosition = previousPosition;
        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            String name = mPagerAdapter.getPlaylistItem(position).getName();
            mToolbar.setSubtitle(MainActivity.this.generateSubtitle(name));
            ChannelFragment channelFragment = mPagerAdapter.getRegisteredFragment(mPreviousPosition);
            if (channelFragment != null && position != mPreviousPosition) {
                channelFragment.pauseVideoFragment(true);
            }
            mPreviousPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}

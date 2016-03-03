package com.selcukcihan.android.teknoseyir;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnFullscreenListener {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private ChannelPagerAdapter mPagerAdapter;
    private ViewPager mPager;
    private TabLayout tabLayout;
    private Toolbar mToolbar;

    private boolean mIsFullscreen = false;
    /** The request code when calling startActivityForResult to recover from an API service error. */
    private static final int RECOVERY_DIALOG_REQUEST = 1;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(generateTitle());
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(true);

        mPagerAdapter = new ChannelPagerAdapter(this, getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.top_pager);
        mPager.setAdapter(mPagerAdapter);

        mToolbar.setSubtitle(generateSubtitle(mPagerAdapter.getPlaylistItem(0).getName()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);
        //mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int mPreviousPosition = -1;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String name = mPagerAdapter.getPlaylistItem(position).getName();
                mToolbar.setSubtitle(MainActivity.this.generateSubtitle(name));

                if (mPreviousPosition != -1) {
                    ChannelFragment channelFragment = mPagerAdapter.getRegisteredFragment(mPreviousPosition);
                    if (channelFragment != null) {
                        channelFragment.onClickClose(null);
                    }
                }
                mPreviousPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayUseLogoEnabled(true);

        checkYouTubeApi();
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.mIsFullscreen = isFullscreen;
        ((ChannelFragment) mPagerAdapter.getRegisteredFragment(mPager.getCurrentItem())).onFullscreen(isFullscreen);
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
}

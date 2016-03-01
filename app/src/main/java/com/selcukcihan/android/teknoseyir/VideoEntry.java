package com.selcukcihan.android.teknoseyir;

/**
 * Created by SELCUKCI on 1.3.2016.
 */

public class VideoEntry {
    private final String mText;
    private final String mVideoId;

    public VideoEntry(String text, String videoId) {
        this.mText = text;
        this.mVideoId = videoId;
    }

    public String getText() { return mText; }
    public String getVideoId() { return mVideoId; }
}

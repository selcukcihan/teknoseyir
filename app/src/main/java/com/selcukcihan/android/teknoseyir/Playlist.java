package com.selcukcihan.android.teknoseyir;

/**
 * Created by SELCUKCI on 1.3.2016.
 */
public class Playlist {
    private final String mName;
    private final String mURI;

    public Playlist(String name, String URI) {
        mName = name;
        mURI = URI;
    }

    public String getName() { return mName; }
    public String getURI() { return mURI; }
}

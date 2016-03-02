package com.selcukcihan.android.teknoseyir;

/**
 * Created by SELCUKCI on 1.3.2016.
 */
public class Playlist {
    private final String mName;
    private final String mPlaylistId;

    public Playlist(String name, String playlistId) {
        mName = name;
        mPlaylistId = playlistId;
    }

    public String getName() { return mName; }
    public String getPlaylistId() { return mPlaylistId; }
}

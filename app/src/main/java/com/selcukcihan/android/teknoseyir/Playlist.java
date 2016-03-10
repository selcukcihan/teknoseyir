package com.selcukcihan.android.teknoseyir;

/**
 * Created by SELCUKCI on 1.3.2016.
 */
public class Playlist {
    private boolean mIsChannel;
    private final String mName;
    private final String mPlaylistId;
    private final Integer mIconId;

    public Playlist(String name, String playlistId, Integer iconId) {
        mIsChannel = false;
        mName = name;
        mPlaylistId = playlistId;
        mIconId = iconId;
    }

    public Playlist makeChannel() {
        mIsChannel = true;
        return this;
    }

    public String getName() { return mName; }
    public String getPlaylistId() { return mPlaylistId; }
    public Integer getIconId() { return mIconId; }
    public String getSearchURL(String nextPageToken) {
        return (mIsChannel
                ? "https://www.googleapis.com/youtube/v3/search?part=snippet%2CcontentDetails&maxResults=50&order=date"
                        + "&channelId=" + mPlaylistId
                        + (nextPageToken != null ? "&pageToken=" + nextPageToken : "")
                        + "&key=" + DeveloperKey.DEVELOPER_KEY
                : "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&maxResults=50"
                        + "&playlistId=" + mPlaylistId
                        + (nextPageToken != null ? "&pageToken=" + nextPageToken : "")
                        + "&key=" + DeveloperKey.DEVELOPER_KEY);
    }
}

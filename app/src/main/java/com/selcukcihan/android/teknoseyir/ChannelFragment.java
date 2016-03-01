package com.selcukcihan.android.teknoseyir;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.api.services.youtube.YouTube;

/**
 * Created by SELCUKCI on 1.3.2016.
 */
public class ChannelFragment extends Fragment {
    private final static String PLAYLIST_NAME_PARAMETER = "com.selcukcihan.android.teknoseyir.PLAYLIST_NAME_PARAMETER";
    private final static String PLAYLIST_URI_PARAMETER = "com.selcukcihan.android.teknoseyir.PLAYLIST_URI_PARAMETER";

    private Playlist mPlaylist;

    public static ChannelFragment newInstance(Playlist playlist) {
        ChannelFragment fragment = new ChannelFragment();

        Bundle args = new Bundle();
        args.putString(PLAYLIST_NAME_PARAMETER, playlist.getName());
        args.putString(PLAYLIST_URI_PARAMETER, playlist.getURI());
        fragment.setArguments(args);
        return fragment;
    }

    public ChannelFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PlaylistFragment fragment = (PlaylistFragment) getChildFragmentManager().findFragmentById(R.id.playlist_fragment);
        fragment.setPlaylist(mPlaylist);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlaylist = new Playlist(getArguments().getString(PLAYLIST_NAME_PARAMETER), getArguments().getString(PLAYLIST_URI_PARAMETER));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_channel, container, false);
        return v;
    }
}

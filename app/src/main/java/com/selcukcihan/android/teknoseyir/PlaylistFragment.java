package com.selcukcihan.android.teknoseyir;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by SELCUKCI on 29.2.2016.
 */
public class PlaylistFragment extends Fragment {
    private static final String PARAM = "group";

    // TODO: Rename and change types of parameters
    private String mGroup;

    // TODO: Rename and change types and number of parameters
    public static PlaylistFragment newInstance(String group) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        // args.setClassLoader(EvradGroup.class.getClassLoader());
        args.putString(PARAM, group);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroup = getArguments().getString(PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        ((TextView) rootView.findViewById(R.id.deneme_text)).setText(mGroup);
        //final ListView listView = (ListView) rootView.findViewById(R.id.listview);

        return rootView;
    }

}

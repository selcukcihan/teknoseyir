package com.selcukcihan.android.teknoseyir;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.api.services.youtube.YouTube;

import java.nio.channels.Channel;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by SELCUKCI on 1.3.2016.
 */
public class ChannelFragment extends ListFragment {
    private final static String PLAYLIST_NAME_PARAMETER = "com.selcukcihan.android.teknoseyir.PLAYLIST_NAME_PARAMETER";
    private final static String PLAYLIST_ID_PARAMETER = "com.selcukcihan.android.teknoseyir.PLAYLIST_ID_PARAMETER";
    private final static String PLAYLIST_ICON_ID_PARAMETER = "com.selcukcihan.android.teknoseyir.PLAYLIST_ICON_ID_PARAMETER";

    private Playlist mPlaylist;
    private VideoAdapter mAdapter;
    private View mVideoBox;
    private List<VideoEntry> mVideos = new LinkedList<VideoEntry>();
    private View mCloseButton;

    private boolean mIsFullscreen;
    private ProgressDialog mDialog;

    public static ChannelFragment newInstance(Playlist playlist) {
        ChannelFragment fragment = new ChannelFragment();

        Bundle args = new Bundle();
        args.putString(PLAYLIST_NAME_PARAMETER, playlist.getName());
        args.putString(PLAYLIST_ID_PARAMETER, playlist.getPlaylistId());
        args.putInt(PLAYLIST_ICON_ID_PARAMETER, playlist.getIconId());
        fragment.setArguments(args);
        return fragment;
    }

    public ChannelFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mVideoBox = getActivity().findViewById(R.id.video_box);
        mCloseButton = getActivity().findViewById(R.id.close_button);
        mVideoBox.setVisibility(View.GONE);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        new RetrieveJSONTask().execute(mPlaylist.getPlaylistId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlaylist = new Playlist(getArguments().getString(PLAYLIST_NAME_PARAMETER),
                    getArguments().getString(PLAYLIST_ID_PARAMETER),
                    getArguments().getInt(PLAYLIST_ICON_ID_PARAMETER));
            mAdapter = new VideoAdapter(getActivity(), mVideos);
            setListAdapter(mAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_channel, container, false);
        if (savedInstanceState == null) {
            Fragment fragment = VideoFragment.newInstance();
            //add child fragment
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.video_fragment_container, fragment, mPlaylist.getPlaylistId())
                    .commit();
        }
        return v;
    }

    public void onFullscreen(boolean isFullscreen) {
        this.mIsFullscreen = isFullscreen;
        layout();
    }

    /**
     * Sets up the layout programatically for the three different states. Portrait, landscape or
     * fullscreen+landscape. This has to be done programmatically because we handle the orientation
     * changes ourselves in order to get fluent fullscreen transitions, so the xml layout resources
     * do not get reloaded.
     */
    private void layout() {
        boolean isPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        this.getView().setVisibility(mIsFullscreen ? View.GONE : View.VISIBLE);
        mAdapter.setLabelVisibility(isPortrait);

        mCloseButton.setVisibility(isPortrait ? View.VISIBLE : View.GONE);

        VideoFragment fragment = (VideoFragment) getChildFragmentManager().findFragmentByTag(mPlaylist.getPlaylistId());

        if (mIsFullscreen) {
            mVideoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
            setLayoutSize(fragment.getView(), MATCH_PARENT, MATCH_PARENT);
            setLayoutSizeAndGravity(mVideoBox, MATCH_PARENT, MATCH_PARENT, Gravity.TOP | Gravity.LEFT);
        } else if (isPortrait) {
            setLayoutSize(this.getView(), MATCH_PARENT, MATCH_PARENT);
            setLayoutSize(fragment.getView(), MATCH_PARENT, WRAP_CONTENT);
            setLayoutSizeAndGravity(mVideoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM);
        } else {
            mVideoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
            int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
            setLayoutSize(this.getView(), screenWidth / 4, MATCH_PARENT);
            int videoWidth = screenWidth - screenWidth / 4 - dpToPx(5);
            setLayoutSize(fragment.getView(), videoWidth, WRAP_CONTENT);
            setLayoutSizeAndGravity(mVideoBox, videoWidth, WRAP_CONTENT,
                    Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static void setLayoutSize(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        params.gravity = gravity;
        view.setLayoutParams(params);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        VideoFragment fragment = (VideoFragment) getChildFragmentManager().findFragmentByTag(mPlaylist.getPlaylistId());
        View videoBox = this.getView().findViewById(R.id.video_box);
        if (fragment != null) {
            String videoId = mVideos.get(position).getVideoId();
            fragment.setVideoId(videoId);

            // The videoBox is INVISIBLE if no video was previously selected, so we need to show it now.
            if (videoBox.getVisibility() != View.VISIBLE) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // Initially translate off the screen so that it can be animated in from below.
                    videoBox.setTranslationY(videoBox.getHeight());
                }
                videoBox.setVisibility(View.VISIBLE);
            }

            // If the fragment is off the screen, we animate it in.
            if (videoBox.getTranslationY() > 0) {
                videoBox.animate().translationY(0).setDuration(300);
            }
        }
    }

    private void discardDialog() {
        if ((mDialog != null) && mDialog.isShowing())
            mDialog.dismiss();
        mDialog = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        discardDialog();
    }

    public void onClickClose(View view) {
        this.getListView().clearChoices();
        this.getListView().requestLayout();
        VideoFragment fragment = (VideoFragment) getChildFragmentManager().findFragmentByTag(mPlaylist.getPlaylistId());
        if (fragment != null) {
            fragment.pause();
            ViewPropertyAnimator animator = mVideoBox.animate()
                    .translationYBy(mVideoBox.getHeight())
                    .setDuration(300);
            runOnAnimationEnd(animator, new Runnable() {
                @Override
                public void run() {
                    mVideoBox.setVisibility(View.GONE);
                }
            });
        }
    }

    @TargetApi(16)
    private void runOnAnimationEnd(ViewPropertyAnimator animator, final Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            animator.withEndAction(runnable);
        } else {
            animator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    runnable.run();
                }
            });
        }
    }

    private final class RetrieveJSONTask extends AsyncTask<String, Void, List<VideoEntry>> {
        private Exception mException;

        protected List<VideoEntry> doInBackground(String... urls) {

            try {
                YoutubeRepository repo = new YoutubeRepository();
                return repo.getVideosOf(urls[0]);
            } catch (Exception e) {
                this.mException = e;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            ChannelFragment.this.mDialog = new ProgressDialog(getContext());
            ChannelFragment.this.mDialog.setMessage(ChannelFragment.this.mDialog.getContext().getResources().getString(R.string.waiting));
            ChannelFragment.this.mDialog.show();
        }

        protected void onPostExecute(List<VideoEntry> videos) {
            ChannelFragment.this.discardDialog();
            if (mException == null) {
                mVideos = videos;
                mAdapter.updateData(videos);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}

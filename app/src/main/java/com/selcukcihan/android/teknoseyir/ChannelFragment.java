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
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.ListView;


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
    private List<VideoEntry> mVideos = new LinkedList<VideoEntry>();

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
        this.getView().findViewById(R.id.video_box).setVisibility(View.GONE);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        layout(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT, false);

        new RetrieveJSONTask().execute(mPlaylist.getPlaylistId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlaylist = new Playlist(getArguments().getString(PLAYLIST_NAME_PARAMETER),
                    getArguments().getString(PLAYLIST_ID_PARAMETER),
                    getArguments().getInt(PLAYLIST_ICON_ID_PARAMETER));
            setListAdapter(new VideoAdapter(getActivity(), mVideos));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_channel, container, false);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((VideoAdapter) getListAdapter()).releaseLoaders();
    }

    /**
     * Sets up the layout programatically for the three different states. Portrait, landscape or
     * fullscreen+landscape. This has to be done programmatically because we handle the orientation
     * changes ourselves in order to get fluent fullscreen transitions, so the xml layout resources
     * do not get reloaded.
     */
    public void layout(boolean isPortrait, boolean isFullScreen) {
        // For full screen, we should not see the list of videos in ChannelFragment
        this.getView().findViewById(android.R.id.list).setVisibility(isFullScreen ? View.GONE : View.VISIBLE);

        ((VideoAdapter) getListAdapter()).setLabelVisibility(!isFullScreen);
        this.getActivity().findViewById(R.id.toolbar).setVisibility(isFullScreen ? View.GONE : View.VISIBLE);

        this.getView().findViewById(R.id.close_button).setVisibility(isPortrait ? View.VISIBLE : View.GONE);

        VideoFragment fragment = (VideoFragment) getChildFragmentManager().findFragmentByTag(mPlaylist.getPlaylistId());

        if (isFullScreen) {
            this.getView().findViewById(R.id.video_box).setTranslationY(0); // Reset any translation that was applied in portrait.
            if (fragment != null) {
                setLayoutSize(((View) fragment.getView().getParent()), MATCH_PARENT, MATCH_PARENT);
            }
            setLayoutSizeAndGravity(this.getView().findViewById(R.id.video_box), MATCH_PARENT, MATCH_PARENT, Gravity.TOP | Gravity.LEFT);
        } else if (isPortrait) {
            ((LinearLayout) this.getView()).setOrientation(LinearLayout.VERTICAL);
            ((LinearLayout) this.getView()).setGravity(Gravity.NO_GRAVITY);

            if (fragment != null) {
                setLayoutSize(((View) fragment.getView().getParent()), MATCH_PARENT, WRAP_CONTENT);
            }
            setLayoutSizeAndGravity(this.getView().findViewById(R.id.video_box), MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM);
        } else {
            ((LinearLayout) this.getView()).setOrientation(LinearLayout.HORIZONTAL);
            ((LinearLayout) this.getView()).setGravity(Gravity.LEFT);

            this.getView().findViewById(R.id.video_box).setTranslationY(0); // Reset any translation that was applied in portrait.
            int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
            setLayoutSize(this.getView(), screenWidth / 4, MATCH_PARENT);
            int videoWidth = screenWidth - screenWidth / 4 - dpToPx(5);
            if (fragment != null) {
                setLayoutSize(((View) fragment.getView().getParent()), videoWidth, WRAP_CONTENT);
            }
            setLayoutSizeAndGravity(this.getView().findViewById(R.id.video_box),
                    videoWidth, WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
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
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        params.gravity = gravity;
        view.setLayoutParams(params);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(mPlaylist.getPlaylistId());
        VideoFragment videoFragment;
        if (fragment == null) {
            videoFragment = VideoFragment.newInstance();
            //add child fragment
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.video_fragment_container, videoFragment, mPlaylist.getPlaylistId())
                    .commit();
        } else{
            videoFragment = (VideoFragment) fragment;
        }

        //VideoFragment fragment = (VideoFragment) getChildFragmentManager().findFragmentByTag(mPlaylist.getPlaylistId());
        View videoBox = this.getView().findViewById(R.id.video_box);
        String videoId = mVideos.get(position).getVideoId();
        videoFragment.setVideoId(videoId);

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

    public void pauseVideoFragment(boolean destroy) {
        VideoFragment fragment = (VideoFragment) getChildFragmentManager().findFragmentByTag(mPlaylist.getPlaylistId());
        if (fragment != null) {
            fragment.pause();
            if (destroy) {
                getChildFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
                this.getView().findViewById(R.id.video_box).setVisibility(View.GONE);
                this.getListView().clearChoices();
                this.getListView().requestLayout();
            } else {
                ViewPropertyAnimator animator = this.getView().findViewById(R.id.video_box).animate()
                        .translationYBy(this.getView().findViewById(R.id.video_box).getHeight())
                        .setDuration(300);
                runOnAnimationEnd(animator, new Runnable() {
                    @Override
                    public void run() {
                        View view = ChannelFragment.this.getView().findViewById(R.id.video_box);
                        if (view != null) {
                            view.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    }

    public void onClickClose(View view) {
        this.getListView().clearChoices();
        this.getListView().requestLayout();
        pauseVideoFragment(false);
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
            if (((MainActivity) getActivity()).currentFragmentIs(ChannelFragment.this)) {
                ChannelFragment.this.mDialog = new ProgressDialog(getContext());
                ChannelFragment.this.mDialog.setMessage(ChannelFragment.this.mDialog.getContext().getResources().getString(R.string.waiting));
                ChannelFragment.this.mDialog.show();
            }
        }

        protected void onPostExecute(List<VideoEntry> videos) {
            ChannelFragment.this.discardDialog();
            if (mException == null) {
                mVideos = videos;
                ((VideoAdapter) ChannelFragment.this.getListAdapter()).updateData(videos);
                ((VideoAdapter) ChannelFragment.this.getListAdapter()).notifyDataSetChanged();
            }
        }
    }

}

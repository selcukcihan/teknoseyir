package com.selcukcihan.android.teknoseyir;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SELCUKCI on 1.3.2016.
 */
public class VideoAdapter extends BaseAdapter {
    private final Playlist mPlaylist;
    private final YoutubeRepository mRepo;

    private final List<View> entryViews;
    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
    private final LayoutInflater inflater;
    private final ThumbnailListener thumbnailListener;

    private boolean labelsVisible;

    public VideoAdapter(Context context, Playlist playlist) {
        mPlaylist = playlist;
        entryViews = new ArrayList<View>();
        thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
        inflater = LayoutInflater.from(context);
        thumbnailListener = new ThumbnailListener();

        labelsVisible = true;

        mRepo = new YoutubeRepository(mPlaylist);
    }

    public void fill() {
        new RetrieveJSONTask().execute();
    }

    public void releaseLoaders() {
        for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
            loader.release();
        }
    }

    public void setLabelVisibility(boolean visible) {
        labelsVisible = visible;
        for (View view : entryViews) {
            view.findViewById(R.id.text).setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getCount() {
        return mRepo.size();
    }

    @Override
    public VideoEntry getItem(int position) {
        return mRepo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1 && mRepo.hasMoreVideos()) { // scrolled to the last item
            new RetrieveJSONTask().execute();
        }

        View view = convertView;
        VideoEntry entry = mRepo.get(position);

        // There are three cases here
        if (view == null) {
            // 1) The view has not yet been created - we need to initialize the YouTubeThumbnailView.
            view = inflater.inflate(R.layout.video_list_item, parent, false);
            YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
            thumbnail.setTag(entry.getVideoId());
            thumbnail.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
        } else {
            YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
            YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(thumbnail);
            if (loader == null) {
                // 2) The view is already created, and is currently being initialized. We store the
                //    current videoId in the tag.
                thumbnail.setTag(entry.getVideoId());
            } else {
                // 3) The view is already created and already initialized. Simply set the right videoId
                //    on the loader.
                thumbnail.setImageResource(R.drawable.loading_thumbnail);
                loader.setVideo(entry.getVideoId());
            }
        }
        TextView label = ((TextView) view.findViewById(R.id.text));
        label.setText(entry.getText());
        label.setVisibility(labelsVisible ? View.VISIBLE : View.GONE);
        return view;
    }

    private final class RetrieveJSONTask extends AsyncTask<Void, Void, Void> {
        private Exception mException;

        protected Void doInBackground(Void... params) {
            try {
                VideoAdapter.this.mRepo.fetch();
            } catch (Exception e) {
                this.mException = e;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            /*
            if (((MainActivity) getActivity()).currentFragmentIs(ChannelFragment.this)) {
                ChannelFragment.this.mDialog = new ProgressDialog(getContext());
                ChannelFragment.this.mDialog.setMessage(ChannelFragment.this.mDialog.getContext().getResources().getString(R.string.waiting));
                ChannelFragment.this.mDialog.show();
            }*/
        }

        protected void onPostExecute(Void result) {
            //ChannelFragment.this.discardDialog();
            if (mException == null) {
                VideoAdapter.this.notifyDataSetChanged();

                //mVideos = videos;
                //((VideoAdapter) ChannelFragment.this.getListAdapter()).updateData(videos);
                //((VideoAdapter) ChannelFragment.this.getListAdapter()).notifyDataSetChanged();
            }
        }
    }


    private final class ThumbnailListener implements  YouTubeThumbnailView.OnInitializedListener, YouTubeThumbnailLoader.OnThumbnailLoadedListener {

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            thumbnailViewToLoaderMap.put(view, loader);
            view.setImageResource(R.drawable.loading_thumbnail);
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);
        }

        @Override
        public void onInitializationFailure(
                YouTubeThumbnailView view, YouTubeInitializationResult loader) {
            view.setImageResource(R.drawable.no_thumbnail);
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
        }

        @Override
        public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
            view.setImageResource(R.drawable.no_thumbnail);
        }
    }

}

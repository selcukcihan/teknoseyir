package com.selcukcihan.android.teknoseyir;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by SELCUKCI on 29.2.2016.
 */
public class PlaylistFragment extends ListFragment {
    private YouTube mYoutube;

    private Playlist mPlaylist;

    private VideoAdapter mAdapter;
    private View mVideoBox;
    private List<VideoEntry> mVideos;

    public void setPlaylist(Playlist playlist) {
        mPlaylist = playlist;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initVideos() {
        mVideos = new LinkedList<VideoEntry>();
        if (mPlaylist.getURI().isEmpty()) {
            return;
        } else {
            new RetrieveJSONTask().execute("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&forUsername=teknoseyir&key=" + DeveloperKey.DEVELOPER_KEY);
        }
    }
/*
    private void initVideos() {
        mVideos = new LinkedList<VideoEntry>();
        if (mPlaylist.getURI().isEmpty()) {
            return;
        }
        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.


            // Prompt the user to enter a query term.
            String queryTerm = getInputQuery();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = mYoutube.search().list("id,snippet");

            search.setKey(DeveloperKey.DEVELOPER_KEY);
            search.setQ(queryTerm);

            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(20L);

            // Call the API and print results.
            SearchListResponse searchResponse = search.executeUsingHead();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                prettyPrint(searchResultList.iterator(), queryTerm);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }*/
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        return view;
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initVideos();

        mVideoBox = getActivity().findViewById(R.id.video_box);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String videoId = mVideos.get(position).getVideoId();
/*
        VideoFragment videoFragment =
                (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
        videoFragment.setVideoId(videoId);

        // The videoBox is INVISIBLE if no video was previously selected, so we need to show it now.
        if (mVideoBox.getVisibility() != View.VISIBLE) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Initially translate off the screen so that it can be animated in from below.
                mVideoBox.setTranslationY(mVideoBox.getHeight());
            }
            mVideoBox.setVisibility(View.VISIBLE);
        }

        // If the fragment is off the screen, we animate it in.
        if (mVideoBox.getTranslationY() > 0) {
            mVideoBox.animate().translationY(0).setDuration(300);
        }*/
    }

    private final class RetrieveJSONTask extends AsyncTask<String, Void, List<VideoEntry>> {
        private Exception mException;

        public String getJSON(String url, int timeout) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Content-length", "0");
                c.setUseCaches(false);
                c.setAllowUserInteraction(false);
                c.setConnectTimeout(timeout);
                c.setReadTimeout(timeout);
                c.connect();
                int status = c.getResponseCode();

                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        return sb.toString();
                }

            } catch (MalformedURLException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return null;
        }

        protected List<VideoEntry> doInBackground(String... urls) {
            List<VideoEntry> videos = new LinkedList<VideoEntry>();
            try {
                String firstJSON = getJSON(urls[0], 10000);
                String uploads = (new JSONObject(firstJSON)).getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails").getJSONObject("relatedPlaylists").getString("uploads");

                String secondJSON = getJSON("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&maxResults=50&playlistId=UUFIHrIGT0MBMRHzQtmzOWlQ&key=" + DeveloperKey.DEVELOPER_KEY, 10000);
                JSONArray array = (new JSONObject(secondJSON)).getJSONArray("items");



                for (int i = 0; i < array.length(); i++) {
                    String videoId = array.getJSONObject(i).getJSONObject("contentDetails").getString("videoId");
                    String title = array.getJSONObject(i).getJSONObject("snippet").getString("title");
                    videos.add(new VideoEntry(title, videoId));
                }
            } catch (Exception e) {
                this.mException = e;
            }
            return videos;
        }

        protected void onPostExecute(List<VideoEntry> videos) {
            if (mException == null) {
                mVideos = videos;
                PlaylistFragment.this.mAdapter = new VideoAdapter(getActivity(), mVideos);
                PlaylistFragment.this.setListAdapter(mAdapter);
            }
        }
    }

}

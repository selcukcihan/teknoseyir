package com.selcukcihan.android.teknoseyir;

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
 * Created by SELCUKCI on 2.3.2016.
 */
public class YoutubeRepository {
    private final List<VideoEntry> mVideos;
    private final Playlist mPlaylist;
    private String mNextPageToken = null;

    public YoutubeRepository(Playlist playlist) {
        mVideos = new LinkedList<VideoEntry>();
        mPlaylist = playlist;
    }

    public int size() {
        return mVideos.size();
    }

    public VideoEntry get(int position) {
        return mVideos.get(position);
    }


    public boolean hasMoreVideos() {
        return mNextPageToken != null && !mNextPageToken.isEmpty();
    }

    public void fetch() throws JSONException {
        List<VideoEntry> videos = new LinkedList<VideoEntry>();
        String jsonString = getJSON(mPlaylist.getSearchURL(mNextPageToken));
        JSONObject json = new JSONObject(jsonString);
        JSONArray array = json.getJSONArray("items");
        if (json.has("nextPageToken")) { // if there is a next page, get the token
            mNextPageToken = json.getString("nextPageToken");
        } else { // end of the list, no next pages, hasMoreVideos should return false from that point on
            mNextPageToken = "";
        }

        for (int i = 0; i < array.length(); i++) {
            String videoId = array.getJSONObject(i).getJSONObject("contentDetails").getString("videoId");
            String title = array.getJSONObject(i).getJSONObject("snippet").getString("title");
            videos.add(new VideoEntry(title, videoId));
        }
        mVideos.addAll(videos);
    }

    private String getJSON(String url) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);
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

}

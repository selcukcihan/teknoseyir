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
    private String mUploadsListId;

    public List<VideoEntry> getVideosOf(String playlistId) throws JSONException {
        List<VideoEntry> videos = new LinkedList<VideoEntry>();


        String secondJSON = getJSON("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&maxResults=50&playlistId="
                + playlistId + "&key=" + DeveloperKey.DEVELOPER_KEY);
        JSONArray array = (new JSONObject(secondJSON)).getJSONArray("items");

        for (int i = 0; i < array.length(); i++) {
            String videoId = array.getJSONObject(i).getJSONObject("contentDetails").getString("videoId");
            String title = array.getJSONObject(i).getJSONObject("snippet").getString("title");
            videos.add(new VideoEntry(title, videoId));
        }
        return videos;
    }

    public List<VideoEntry> getVideosFrom(String playlistTitle) throws JSONException {
        String firstJSON = getJSON("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&forUsername=teknoseyir&key="
                + DeveloperKey.DEVELOPER_KEY);
        String channelId = (new JSONObject(firstJSON)).getJSONArray("items").getJSONObject(0).getString("id");
        String playlistsJSON = getJSON("https://www.googleapis.com/youtube/v3/playlists?part=snippet&maxResults=50&channelId=" + channelId + "&key=" + DeveloperKey.DEVELOPER_KEY);
        JSONArray array = (new JSONObject(playlistsJSON)).getJSONArray("items");

        List<VideoEntry> videos = new LinkedList<VideoEntry>();
        for (int i = 0; i < array.length(); i++) {
            String title = array.getJSONObject(i).getJSONObject("snippet").getString("title");
            String playlistId = array.getJSONObject(i).getString("id");
            if (title.equalsIgnoreCase(playlistTitle)) {
                return getVideosOf(playlistId);
            }
        }
        return videos;
    }

    public List<VideoEntry> getAllVideos() throws JSONException {
        List<VideoEntry> videos = new LinkedList<VideoEntry>();

        String firstJSON = getJSON("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&forUsername=teknoseyir&key="
                + DeveloperKey.DEVELOPER_KEY);
        String uploads = (new JSONObject(firstJSON)).getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails").getJSONObject("relatedPlaylists").getString("uploads");
        mUploadsListId = uploads;

        String secondJSON = getJSON("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&maxResults=50&playlistId="
                + mUploadsListId + "&key=" + DeveloperKey.DEVELOPER_KEY);
        JSONArray array = (new JSONObject(secondJSON)).getJSONArray("items");

        for (int i = 0; i < array.length(); i++) {
            String videoId = array.getJSONObject(i).getJSONObject("contentDetails").getString("videoId");
            String title = array.getJSONObject(i).getJSONObject("snippet").getString("title");
            videos.add(new VideoEntry(title, videoId));
        }
        return videos;
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

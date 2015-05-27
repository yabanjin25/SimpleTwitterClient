package com.codepath.apps.mysimpletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ayamanaka on 5/25/15.
 */
public class Url {
    private String expandedUrl;
    private String url;
    private int startIndex;
    private int endIndex;
    private String displayUrl;

    public String getUrl() {
        return url;
    }

    public String getExpandedUrl() {
        return expandedUrl;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public int getEndIndex() {
        return endIndex;
    }

    // Deserialize the JSON
    public static Url fromJSON(JSONObject jsonObject) {
        Url url = new Url();
        try {
            url.expandedUrl = jsonObject.getString("expanded_url");
            url.url = jsonObject.getString("url");
            url.displayUrl = jsonObject.getString("display_url");
            url.startIndex = jsonObject.getJSONArray("indices").getInt(0);
            url.endIndex = jsonObject.getJSONArray("indices").getInt(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static ArrayList<Url> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Url> urls = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject urlJson = jsonArray.getJSONObject(i);
                Url url = Url.fromJSON(urlJson);
                if (url != null) {
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return urls;
    }

    // Deserialize the JSON
    public static Url fromMediaJSON(JSONObject jsonObject) {
        Url url = new Url();
        try {
            url.expandedUrl = jsonObject.getString("media_url");
            url.url = jsonObject.getString("url");
            url.displayUrl = jsonObject.getString("display_url");
            url.startIndex = jsonObject.getJSONArray("indices").getInt(0);
            url.endIndex = jsonObject.getJSONArray("indices").getInt(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return url;
    }
}

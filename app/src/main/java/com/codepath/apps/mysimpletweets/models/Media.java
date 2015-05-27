package com.codepath.apps.mysimpletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ayamanaka on 5/25/15.
 */
public class Media {
    private Url url;
    private long id;
    private String type;
    private int startIndex;
    private int endIndex;

    public int getEndIndex() {
        return endIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public String getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public Url getUrl() {
        return url;
    }

    // Deserialize the JSON
    public static Media fromJSON(JSONObject jsonObject) {
        Media media = new Media();
        try {
            media.url = Url.fromMediaJSON(jsonObject);
            media.id = jsonObject.getLong("id");
            media.type = jsonObject.getString("type");
            media.startIndex = jsonObject.getJSONArray("indices").getInt(0);
            media.endIndex = jsonObject.getJSONArray("indices").getInt(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return media;
    }

    public static ArrayList<Media> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Media> medias = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject mediaJson = jsonArray.getJSONObject(i);
                Media media = Media.fromJSON(mediaJson);
                if (media != null) {
                    medias.add(media);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return medias;
    }
}

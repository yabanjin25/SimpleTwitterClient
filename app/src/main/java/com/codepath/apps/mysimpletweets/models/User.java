package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ayamanaka on 5/21/15.
 */
public class User {
    // list the attributes
    private String name;
    private long uId;
    private String username;
    private String profileImageUrl;
    private String tagLine;
    private int tweetsCount;
    private int followingCount;
    private int followersCount;

    // deserialize JSON into a user object
    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();
        try {
            user.name = jsonObject.getString("name");
            user.uId = jsonObject.getLong("id");
            user.username = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.tagLine = jsonObject.getString("description");
            user.tweetsCount = jsonObject.getInt("statuses_count");
            user.followingCount = jsonObject.getInt("friends_count");
            user.followersCount = jsonObject.getInt("followers_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public long getuId() {
        return uId;
    }

    public String getName() {
        return name;
    }

    public String getTagLine() {
        return tagLine;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getTweetsCount() {
        return tweetsCount;
    }
}

package com.codepath.apps.mysimpletweets.models;

import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

// Parse the JSON + store the data, encapsulate state logic or display logic
public class Tweet {
    // List out all of the attributes
    private String body;
    private long uid;
    private User user;
    private String createdAt;
    private boolean retweeted;
    private int retweetCount;
    private boolean favorited;
    private int favoriteCount;
    private ArrayList<Url> urls;
    private ArrayList<Media> medias;
    private boolean isRetweet;
    private Tweet retweetedTweet;
    private long inReplyToStatusId;


    public String getCreatedAt() {
        return createdAt;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public ArrayList<Url> getUrls() {
        return urls;
    }

    public ArrayList<Media> getMedias() {
        return medias;
    }

    public boolean isRetweet() {
        return isRetweet;
    }

    public Tweet getRetweetedTweet() {
        return retweetedTweet;
    }

    public long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setInReplyToStatusId(long inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public void setRetweetedTweet(Tweet retweetedTweet) {
        this.retweetedTweet = retweetedTweet;
    }

    public void setRetweet(boolean isRetweet) {
        this.isRetweet = isRetweet;
    }

    public void setMedias(ArrayList<Media> medias) {
        this.medias = medias;
    }

    public void setUrls(ArrayList<Url> urls) {
        this.urls = urls;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    // Deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.urls = Url.fromJSONArray(jsonObject.getJSONObject("entities").getJSONArray("urls"));

            if (jsonObject.isNull("in_reply_to_status_id")) {
                tweet.inReplyToStatusId = -1;
            } else {
                tweet.inReplyToStatusId = jsonObject.getLong("in_reply_to_status_id");
            }

            if (jsonObject.getJSONObject("entities").has("media")) {
                tweet.medias = Media.fromJSONArray(jsonObject.getJSONObject("entities").getJSONArray("media"));
            } else {
                tweet.medias = new ArrayList<>();
            }

            if (jsonObject.has("retweeted_status")) {
                tweet.isRetweet = true;
                tweet.retweetedTweet = fromJSON(jsonObject.getJSONObject("retweeted_status"));
            } else {
                tweet.isRetweet = false;
                tweet.retweetedTweet = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return getShortenedTimeForDisplay(relativeDate);
    }

    private String getShortenedTimeForDisplay(String longFormattedTime)
    {
        if (longFormattedTime.matches("Yesterday")) {
            return "1d";
        } else if (longFormattedTime.matches("[\\d]{1,2} [A-Za-z]+ ago")) {
            String[] longFormattedTimeArray = longFormattedTime.split(" ");
            return longFormattedTimeArray[0] + longFormattedTimeArray[1].charAt(0);
        } else if (longFormattedTime.matches("[A-Za-z]+ [\\d]{1,2}, [\\d]{4}")) {
            String[] longFormattedTimeArray = longFormattedTime.split("[ |,]");
            return longFormattedTimeArray[0] + " " + longFormattedTimeArray[1];
        }
        return longFormattedTime;
    }

    public Spanned getTweetBodyForDisplay()
    {
        String body = getBody();

        for (int i = medias.size() - 1; i >= 0; i--) {
            Media media = medias.get(i);
            int startIndex = media.getStartIndex();

            body = body.substring(0, startIndex);
        }

        for (int i = urls.size() - 1; i >= 0; i--) {
            Url url = urls.get(i);
            int startIndex = url.getStartIndex();
            int endIndex = url.getEndIndex();

            body = body.substring(0, startIndex) + getHtmlUrl(url) + body.substring(endIndex);
        }

        return Html.fromHtml(body);
    }

    private String getHtmlUrl(Url url)
    {
        String htmlUrl = "<a href=\"" + url.getExpandedUrl() + "\">" + url.getDisplayUrl() + "</a>";
        return htmlUrl;
    }
}

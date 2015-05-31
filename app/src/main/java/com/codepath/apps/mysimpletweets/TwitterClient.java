package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.util.ArrayList;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "QedmF96GHL047pfRSj44cJBwP";       // Change this
	public static final String REST_CONSUMER_SECRET = "yRFBiEcLQK9FsdXi4eXM3as02mtz3a98zII4zf1xQBfp7WRUZ1"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

    private TwitterClientListener listener;

    public interface TwitterClientListener {
        public void onRefreshTimeLine();
        public void onLoadMoreTweets(ArrayList<Tweet> resultTweets);
    }

    public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}


    // METHOD == ENDPOINT

    // HomeTimeLine - get us the home timeline
    public void getHomeTimeline(AsyncHttpResponseHandler handler)
    {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 100);
        params.put("since_id", 1);
        getClient().get(apiUrl, params, handler);
    }

    public void getMentionsTimeline(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 100);
        params.put("since_id", 1);
        getClient().get(apiUrl, params, handler);
    }

    public void tweet(String tweetMessage)
    {
        tweet(tweetMessage, 0);
    }

    // Composing a tweet
    public void tweet(String tweetMessage, long inReplyToStatusId)
    {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweetMessage);

        if (inReplyToStatusId != 0) {
            params.put("in_reply_to_status_id", inReplyToStatusId);
        }

        getClient().post(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                if (listener != null) {
                    listener.onRefreshTimeLine(); // <---- fire listener here
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    // LoadMoreHomeTimeLineTweets
    public void loadMoreHomeTimelineTweets(long idOfOldestTweet)
    {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 100);
        params.put("max_id", (idOfOldestTweet - 1));
        Log.d("DEBUG", apiUrl);
        Log.d("DEBUG", params.toString());
        getClient().get(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", statusCode + "");
                Log.d("DEBUG", json.toString());
                ArrayList<Tweet> resultTweets = new ArrayList<Tweet>();
                resultTweets.addAll(Tweet.fromJSONArray(json));

                if (listener != null) {
                    listener.onLoadMoreTweets(resultTweets); // <---- fire listener here
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void loadMoreMentionsTimelineTweets(long idOfOldestTweet)
    {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 100);
        params.put("max_id", (idOfOldestTweet - 1));
        Log.d("DEBUG", apiUrl);
        Log.d("DEBUG", params.toString());
        getClient().get(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", statusCode + "");
                Log.d("DEBUG", json.toString());
                ArrayList<Tweet> resultTweets = new ArrayList<Tweet>();
                resultTweets.addAll(Tweet.fromJSONArray(json));

                if (listener != null) {
                    listener.onLoadMoreTweets(resultTweets); // <---- fire listener here
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void loadMoreUserTimelineTweets(String screenName, long idOfOldestTweet)
    {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 100);
        params.put("screen_name", screenName);
        params.put("max_id", (idOfOldestTweet - 1));
        Log.d("DEBUG", apiUrl);
        Log.d("DEBUG", params.toString());
        getClient().get(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", statusCode + "");
                Log.d("DEBUG", json.toString());
                ArrayList<Tweet> resultTweets = new ArrayList<Tweet>();
                resultTweets.addAll(Tweet.fromJSONArray(json));

                if (listener != null) {
                    listener.onLoadMoreTweets(resultTweets); // <---- fire listener here
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void setTwitterClientListener(TwitterClientListener listener) {
        this.listener = listener;
    }

    // Favoriting a Tweet
    public void favorite(long idOfStatusToFavorite, AsyncHttpResponseHandler handler)
    {
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", idOfStatusToFavorite);
        getClient().post(apiUrl, params, handler);
    }

    // Unfavoriting a Tweet
    public void unfavorite(long idOfStatusToFavorite, AsyncHttpResponseHandler handler)
    {
        String apiUrl = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", idOfStatusToFavorite);
        getClient().post(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler)
    {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 100);
        params.put("since_id", 1);
        params.put("screen_name", screenName);
        getClient().get(apiUrl, params, handler);
    }

    public void getUserInfo(String screenName, AsyncHttpResponseHandler handler)
    {
        String apiUrl = getApiUrl("users/lookup.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        getClient().get(apiUrl, params, handler);
    }

    public void getPrimaryUserInfo(AsyncHttpResponseHandler handler)
    {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        getClient().get(apiUrl, null, handler);
    }


	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

}
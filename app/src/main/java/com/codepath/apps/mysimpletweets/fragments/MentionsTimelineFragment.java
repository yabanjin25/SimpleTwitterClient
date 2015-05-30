package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MentionsTimelineFragment extends TweetsListFragment {

    TwitterClient client;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient();    //singleton client
        client.setTwitterClientListener(new TwitterClient.TwitterClientListener() {
            @Override
            public void onRefreshTimeLine() {
                Toast.makeText(getActivity(), "Tweet posted", Toast.LENGTH_SHORT).show();
                populateTimeline();
            }

            public void onLoadMoreTweets(ArrayList<Tweet> moreTweets)
            {
                aTweets.addAll(moreTweets);
                aTweets.notifyDataSetChanged();
                idOfOldestTweetResult = getIdOfOldestTweetResult();
            }
        });

        populateTimeline();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                if (page >= 500) {
                    displayMaxResultsToast();
                } else {
                    loadMoreTweets();
                }
            }
        });

        setupSwipeToRefresh(v);

        return v;
    }

    // Send an API request to get the timeline JSON
    // Fill the listview by creating the tweet objects from the JSON
    private void populateTimeline() {
        client.getMentionsTimeline(new JsonHttpResponseHandler() {
            // Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                // JSON Here
                // Deserialize JSON
                // Create Models
                // Load the model into listview
                aTweets.clear();
                aTweets.addAll(Tweet.fromJSONArray(json));
                aTweets.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);

                idOfOldestTweetResult = getIdOfOldestTweetResult();
            }

            // Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private void setupSwipeToRefresh(View v)
    {
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private long getIdOfOldestTweetResult()
    {
        Tweet oldestTweet = tweets.get(tweets.size() - 1);
        long idOfOldestTweet = oldestTweet.getUid();
        return idOfOldestTweet;
    }

    private void displayMaxResultsToast()
    {
        Toast.makeText(getActivity(), "Maximum results reached", Toast.LENGTH_SHORT).show();
    }

    private void loadMoreTweets()
    {
        client.loadMoreMentionsTimelineTweets(idOfOldestTweetResult);
    }
}

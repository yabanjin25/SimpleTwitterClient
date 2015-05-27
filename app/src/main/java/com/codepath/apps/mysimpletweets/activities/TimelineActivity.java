package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity {

    TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;

    private SwipeRefreshLayout swipeContainer;
    private final int COMPOSE_REQUEST_CODE = 15;
    private final int COMPOSE_REPLY_REQUEST_CODE = 25;
    public static final int RESULTS_PER_PAGE = 100;
    public long idOfOldestTweetResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, this, tweets);
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                //customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                if (page >= 500) {
                    displayMaxResultsToast();
                } else {
                    loadMoreTweets();
                }
            }
        });

        client = TwitterApplication.getRestClient();    //singleton client
        client.setTwitterClientListener(new TwitterClient.TwitterClientListener() {
            @Override
            public void onRefreshTimeLine() {
                Toast.makeText(TimelineActivity.this, "Tweet posted", Toast.LENGTH_SHORT).show();
                populateTimeline();
            }

            public void onLoadMoreTweets(ArrayList<Tweet> moreTweets)
            {
                aTweets.addAll(moreTweets);
                aTweets.notifyDataSetChanged();
                idOfOldestTweetResult = getIdOfOldestTweetResult();
            }
        });
        setupSwipeToRefresh();
        populateTimeline();
    }

    // Send an API request to get the timeline JSON
    // Fill the listview by creating the tweet objects from the JSON
    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
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
                Log.d("DEBUG", aTweets.toString());

                idOfOldestTweetResult = getIdOfOldestTweetResult();
            }

            // Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {
            Intent i = new Intent(this, ComposeActivity.class);
            this.startActivityForResult(i, COMPOSE_REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    // Send an API request to tweet a new message
    private void tweet(String tweetMessage) {
        client.tweet(tweetMessage);
    }

    // Send an API request to tweet a new message
    private void tweet(String tweetMessage, long inReplyToStatusId) {
        client.tweet(tweetMessage, inReplyToStatusId);
    }

    private void loadMoreTweets()
    {
        client.loadMoreTweets(idOfOldestTweetResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == COMPOSE_REQUEST_CODE) {
            // Extract name value from result extras
            String newTweetMessage = data.getExtras().getString("tweetMessage");
            //int code = data.getExtras().getInt("code", 0);
            tweet(newTweetMessage);
        }

        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == COMPOSE_REPLY_REQUEST_CODE) {
            // Extract name value from result extras
            String newTweetMessage = data.getExtras().getString("tweetMessage");
            long inReplyToStatusId = data.getExtras().getLong("inReplyToStatusId", 0);
            tweet(newTweetMessage, inReplyToStatusId);
        }
    }

    private void setupSwipeToRefresh()
    {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
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

    private void displayMaxResultsToast()
    {
        Toast.makeText(this, "Maximum results reached", Toast.LENGTH_SHORT).show();
    }

    private long getIdOfOldestTweetResult()
    {
        Tweet oldestTweet = tweets.get(tweets.size() - 1);
        long idOfOldestTweet = oldestTweet.getUid();
        return idOfOldestTweet;
    }
}

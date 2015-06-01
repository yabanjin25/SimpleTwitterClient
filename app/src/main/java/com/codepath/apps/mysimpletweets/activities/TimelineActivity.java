package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;

public class TimelineActivity extends ActionBarActivity {

    TwitterClient client;

    protected final int COMPOSE_REQUEST_CODE = 15;
    protected final int COMPOSE_REPLY_REQUEST_CODE = 25;
    public static final int RESULTS_PER_PAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.ic_twitter_icon);
        setSupportActionBar(toolbar);

        client = TwitterApplication.getRestClient();    //singleton client

        // Get the ViewPager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        // Set the ViewPager adapter for the pager
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        // Find the sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setTextColor(Color.parseColor("#55ACEE"));
        tabStrip.setBackgroundColor(Color.WHITE);
        // Attach the tabstrip to the viewpager
        tabStrip.setViewPager(vpPager);
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
//    private void tweet(String tweetMessage) {
//        client.tweet(tweetMessage);
//    }
//
//    // Send an API request to tweet a new message
//    private void tweet(String tweetMessage, long inReplyToStatusId) {
//        client.tweet(tweetMessage, inReplyToStatusId);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // REQUEST_CODE is defined above
//        if (resultCode == RESULT_OK && requestCode == COMPOSE_REQUEST_CODE) {
//            // Extract name value from result extras
//            String newTweetMessage = data.getExtras().getString("tweetMessage");
//            //int code = data.getExtras().getInt("code", 0);
//            tweet(newTweetMessage);
//        }
//
//        // REQUEST_CODE is defined above
//        if (resultCode == RESULT_OK && requestCode == COMPOSE_REPLY_REQUEST_CODE) {
//            // Extract name value from result extras
//            String newTweetMessage = data.getExtras().getString("tweetMessage");
//            long inReplyToStatusId = data.getExtras().getLong("inReplyToStatusId", 0);
//            tweet(newTweetMessage, inReplyToStatusId);
//        }
    }

    public void onProfileView(MenuItem mi) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    // Return the order of the fragments in the viewpager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        // How the adapter gets the manager to insert or remove fragments from the activity
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // The order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            }

            if (position == 1) {
                return new MentionsTimelineFragment();
            }

            return null;
        }

        // How many frgaments there are to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }

        // Return the tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}

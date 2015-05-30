package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ProfileActivity extends ActionBarActivity {

    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        client = TwitterApplication.getRestClient();
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
                // my current user's account info
                getSupportActionBar().setTitle("@" + user.getUsername());
                populateProfileHeader(user);
            }
        });

        // Get the screenname from the activity that launches this
        String screenName = getIntent().getStringExtra("screen_name");
        // Create the usertimeline fragment
        if (savedInstanceState == null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
            // Display the user fragment within this activity (dynamically)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit(); // changes the fragments
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateProfileHeader(User user)
    {
        ImageView ivUserProfileImage = (ImageView) findViewById(R.id.ivUserProfileImage);
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvTweetsCount = (TextView) findViewById(R.id.tvTweetsCount);
        TextView tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
        TextView tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);

        Picasso.with(this).load(user.getProfileImageUrl()).into(ivUserProfileImage);
        tvUserName.setText(user.getName());
        tvTagline.setText(user.getTagLine());
        tvTweetsCount.setText(Html.fromHtml("<b>" + user.getTweetsCount() + "</b>"));
        tvFollowersCount.setText(Html.fromHtml("<b>" + user.getFollowersCount() + "</b>"));
        tvFollowingCount.setText(Html.fromHtml("<b>" + user.getFollowingCount() + "</b>"));
    }
}

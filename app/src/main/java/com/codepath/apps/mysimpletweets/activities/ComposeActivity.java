package com.codepath.apps.mysimpletweets.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;

public class ComposeActivity extends ActionBarActivity {

    EditText etCompose;
    Button btTweet;
    TextView tvCharCount;
    String usernameToReplyTo;
    long inReplyToStatusId;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        client = TwitterApplication.getRestClient();

        if (getIntent().hasExtra("replyToUsername")) {
            usernameToReplyTo = getIntent().getStringExtra("replyToUsername");
        } else {
            usernameToReplyTo = "";
        }

        if (getIntent().hasExtra("inReplyToStatusId")) {
            inReplyToStatusId = getIntent().getLongExtra("inReplyToStatusId", 0);
        } else {
            inReplyToStatusId = 0;
        }

        setupViews();

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateCharCountTextView(s);
                updateTweetButton(s);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViews()
    {
        btTweet = (Button) findViewById(R.id.btTweet);
        btTweet.setEnabled(false);

        etCompose = (EditText) findViewById(R.id.etCompose);

        if (!usernameToReplyTo.isEmpty()) {
            etCompose.setText("@" + usernameToReplyTo + " ");
        }

        etCompose.requestFocus();

        tvCharCount = (TextView) findViewById(R.id.tvCharCount);
    }

    public void updateCharCountTextView(Editable s)
    {
        int charCount = 140 - s.length();
        tvCharCount.setText(charCount + " characters left");

        if (charCount < 0) {
            tvCharCount.setTextColor(Color.RED);
        } else {
            tvCharCount.setTextColor(Color.GRAY);
        }
    }

    public void updateTweetButton(Editable s)
    {
        if (s.length() == 0 || s.length() > 140) {
            btTweet.setEnabled(false);
        } else {
            btTweet.setEnabled(true);
        }
    }

    public void onTweet(View v) {
        client.tweet(etCompose.getText().toString(), inReplyToStatusId);
        finish();
    }
}

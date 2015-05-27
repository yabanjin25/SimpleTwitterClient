package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.activities.ComposeActivity;
import com.codepath.apps.mysimpletweets.activities.TimelineActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

// Take the tweet objects and turn them into views displayed in the list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{

    private final int REQUEST_CODE = 25;
    private TimelineActivity activity;
    private TwitterClient client;

    private static class ViewHolder {
        ImageView ivProfileImage;
        TextView tvFullName;
        TextView tvUsername;
        TextView tvCreatedTime;
        TextView tvBody;
        TextView tvRetweet;
        ImageView ivMedia;
        ImageView ivReplyImage;
        ImageView ivRetweetImage;
        TextView tvRetweetCount;
        ImageView ivFavoriteImage;
        TextView tvFavoriteCount;
        LinearLayout llFavoriteViews;
    }

    public TweetsArrayAdapter(Context context, TimelineActivity activity, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
        this.activity = activity;
        this.client = TwitterApplication.getRestClient();
    }

    // override and setup custom getView

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. get the tweet
        final Tweet tweet = getItem(position);
        // 2. find or inflate the template
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);

            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvFullName = (TextView) convertView.findViewById(R.id.tvFullName);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvCreatedTime = (TextView) convertView.findViewById(R.id.tvCreatedTime);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvRetweet = (TextView) convertView.findViewById(R.id.tvRetweet);
            viewHolder.ivMedia = (ImageView) convertView.findViewById(R.id.ivMedia);
            viewHolder.ivReplyImage = (ImageView) convertView.findViewById(R.id.ivReplyImage);
            viewHolder.ivRetweetImage = (ImageView) convertView.findViewById(R.id.ivRetweetImage);
            viewHolder.tvRetweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
            viewHolder.ivFavoriteImage = (ImageView) convertView.findViewById(R.id.ivFavoriteImage);
            viewHolder.tvFavoriteCount = (TextView) convertView.findViewById(R.id.tvFavoriteCount);
            viewHolder.llFavoriteViews = (LinearLayout) convertView.findViewById(R.id.llFavoriteViews);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Tweet tweetToInflate;

        if (tweet.isRetweet()) {
            tweetToInflate = tweet.getRetweetedTweet();
        } else {
            tweetToInflate = tweet;
        }

        // 4. populate data into the subviews

        if (tweet.isRetweet()) {
            viewHolder.tvRetweet.setVisibility(View.VISIBLE);
            viewHolder.tvRetweet.setText(tweet.getUser().getName() + " retweeted");
        } else {
            viewHolder.tvRetweet.setVisibility(View.GONE);
        }
        viewHolder.tvFullName.setText(tweetToInflate.getUser().getName());
        viewHolder.tvUsername.setText("@" + tweetToInflate.getUser().getUsername());
        viewHolder.tvCreatedTime.setText(tweetToInflate.getRelativeTimeAgo());
        viewHolder.tvBody.setText(tweetToInflate.getTweetBodyForDisplay());
        viewHolder.tvBody.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.tvRetweetCount.setText(tweetToInflate.getRetweetCount() + "");
        viewHolder.tvFavoriteCount.setText(tweetToInflate.getFavoriteCount() + "");
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(tweetToInflate.getUser().getProfileImageUrl()).into(viewHolder.ivProfileImage);

        if (tweetToInflate.getMedias().size() > 0 && tweetToInflate.getMedias().get(0).getType().equals("photo")) {
            viewHolder.ivMedia.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(tweetToInflate.getMedias().get(0).getUrl().getExpandedUrl()).into(viewHolder.ivMedia);
        } else {
            viewHolder.ivMedia.setVisibility(View.GONE);
        }

        viewHolder.ivReplyImage.setClickable(true);
        viewHolder.ivReplyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ComposeActivity.class);
                i.putExtra("inReplyToStatusId", tweetToInflate.getUid());
                i.putExtra("replyToUsername", tweetToInflate.getUser().getUsername());
                activity.startActivityForResult(i, REQUEST_CODE);
            }
        });

        viewHolder.llFavoriteViews.setClickable(true);
        viewHolder.llFavoriteViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ImageView ivFav = (ImageView) v.findViewById(R.id.ivFavoriteImage);
                final TextView tvFavCount = (TextView) v.findViewById(R.id.tvFavoriteCount);

                if (tweet.isFavorited()) {
                    client.unfavorite(tweetToInflate.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                            try {
                                int newFavoriteCount = jsonObject.getInt("favorite_count");
                                tvFavCount.setText(newFavoriteCount + "");
                                ivFav.setImageResource(R.drawable.ic_action_favorite);
                                tweet.setFavorited(false);
                                tweet.setFavoriteCount(newFavoriteCount);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", errorResponse.toString());
                        }
                    });
                } else {
                    client.favorite(tweetToInflate.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                            try {
                                int newFavoriteCount = jsonObject.getInt("favorite_count");
                                tvFavCount.setText(newFavoriteCount + "");
                                ivFav.setImageResource(R.drawable.ic_action_favorited);
                                tweet.setFavorited(true);
                                tweet.setFavoriteCount(newFavoriteCount);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", errorResponse.toString());
                        }
                    });
                }
            }
        });

        if (tweet.isRetweeted()) {
            viewHolder.ivRetweetImage.setImageResource(R.drawable.ic_action_retweeted);
        } else {
            viewHolder.ivRetweetImage.setImageResource(R.drawable.ic_action_retweet);
        }

        if (tweet.isFavorited()) {
            viewHolder.ivFavoriteImage.setImageResource(R.drawable.ic_action_favorited);
        } else {
            viewHolder.ivFavoriteImage.setImageResource(R.drawable.ic_action_favorite);
        }

        // 5. Return the view to be inserted into the list
        return convertView;
    }
}

package de.sachinpan.clevelandbrowns;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.melnykov.fab.FloatingActionButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetViewAdapter;

import java.util.List;

import de.sachinpan.clevelandbrowns.adapters.ViewPagerAdapter;
import io.fabric.sdk.android.Fabric;


public class HomeFragment extends Fragment {
    private ViewPager pager;
    private Activity activity;

    public HomeFragment() {

    }

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "6e7A1VTD0Kv196WS5IwKUsq7B";
    private static final String TWITTER_SECRET = "wKqkNStv57hVEacUOj6niJLB7h1JTceqjDMVUyeiNCfdY3HbUK";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = this.getActivity();
        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(activity, new Crashlytics(), new Twitter(authConfig));

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final ListView tweetLV = (ListView) root.findViewById(R.id.listView);

        final TweetViewAdapter adapter = new TweetViewAdapter<CompactTweetView>(activity);
        tweetLV.setAdapter(adapter);

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService service = twitterApiClient.getStatusesService();
        service.userTimeline(null,"browns",null,20L,null,null,true,null,null,new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> listResult) {
                final List<Tweet> tweets = listResult.data;
                adapter.getTweets().addAll(tweets);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(TwitterException e) {
                Log.e("Twiiter", e.toString() + ", " + e.getMessage());
            }
        });

        pager = (ViewPager) root.findViewById(R.id.pager);
        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TweetComposer.Builder builder = new TweetComposer.Builder(activity)
                        .text("Go #Browns");

                builder.show();
            }
        });

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        pager.setAdapter(viewPagerAdapter);
        pager.setCurrentItem(viewPagerAdapter.getCount() - 1);
        return root;
    }

}

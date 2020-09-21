package com.parse.starter;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    FloatingActionButton nightMode;
    FloatingActionButton brightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setTitle("Your Feed");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        nightMode = findViewById(R.id.night);
        brightMode = findViewById(R.id.bright);
        MainActivity.isDarkModeOn = MainActivity.sharedPreferences1.getBoolean("isDarkModeOn", false);

        nightMode.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        MainActivity.editor.putBoolean("isDarkModeOn", true);
                        MainActivity.editor.apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        recreate();
                        nightMode.setVisibility(View.INVISIBLE);
                        brightMode.setVisibility(View.VISIBLE);
                        Log.i("dark", "enable");
                        MainActivity.editor.putBoolean("isDarkModeOn", true);
//                        MainActivity.editor.apply();
//                        recreate();
                    }
                });
        brightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                MainActivity.editor.putBoolean("isDarkModeOn", false);
                MainActivity.editor.apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
                nightMode.setVisibility(View.VISIBLE);
                brightMode.setVisibility(View.INVISIBLE);
                Log.i("light", "disable");
//                        MainActivity.editor.putBoolean("isDarkModeOn", false);
//                        MainActivity.editor.apply();
//                        recreate();
            }
        });




        if (MainActivity.isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            nightMode.setVisibility(View.INVISIBLE);
            brightMode.setVisibility(View.VISIBLE);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            brightMode.setVisibility(View.INVISIBLE);
            nightMode.setVisibility(View.VISIBLE);
        }



        final ListView listView = findViewById(R.id.listview);

       final List<Map<String,String>> tweetData = new ArrayList<>();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tweet");
        query.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing"));
        query.orderByDescending("createdAt");
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    for(ParseObject tweet : objects){
                        Map<String,String> tweetInfo = new HashMap<>();
                        tweetInfo.put("content",tweet.getString("tweet"));
                        tweetInfo.put("username",tweet.getString("username"));
                        tweetData.add(tweetInfo);
                    }
                    SimpleAdapter simpleAdapter = new SimpleAdapter(FeedActivity.this,tweetData,android.R.layout.simple_list_item_2, new String[]{"content","username"}, new int[]{android.R.id.text1, android.R.id.text2});
                    listView.setAdapter(simpleAdapter);

                }
            }
        });

    }
}

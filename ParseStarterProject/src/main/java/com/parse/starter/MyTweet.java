package com.parse.starter;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MyTweet extends AppCompatActivity {
    FloatingActionButton nightMode;
    FloatingActionButton brightMode;
//    SimpleAdapter simpleAdapter;
       ArrayAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tweet);

        setTitle("Your Tweet");

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        nightMode = findViewById(R.id.nightMode);
        brightMode = findViewById(R.id.back);
        MainActivity.isDarkModeOn = MainActivity.sharedPreferences1.getBoolean("isDarkModeOn", false);

        nightMode.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        recreate();
                        nightMode.setVisibility(View.INVISIBLE);
                        brightMode.setVisibility(View.VISIBLE);
                        MainActivity.editor.putBoolean("isDarkModeOn", true);
                        MainActivity.editor.apply();
                    }
                });
        brightMode.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        recreate();
                        nightMode.setVisibility(View.VISIBLE);
                        brightMode.setVisibility(View.INVISIBLE);
                        MainActivity.editor.putBoolean("isDarkModeOn", false);
                        MainActivity.editor.apply();
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

//        final List<Map<String,String>> tweetData = new ArrayList<>();
        final List<String> tweetData = new ArrayList<>();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tweet");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.orderByDescending("createdAt");
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    for(ParseObject tweet : objects){
//                        Map<String,String> tweetInfo = new HashMap<>();
//                        tweetInfo.put("content",tweet.getString("tweet"));

                        tweetData.add(tweet.getString("tweet"));
                    }
//                   simpleAdapter = new SimpleAdapter(MyTweet.this,tweetData,android.R.layout.simple_list_item_1, new String[]{"content"}, new int[]{android.R.id.text1});
//                    listView.setAdapter(simpleAdapter);
                    simpleAdapter = new ArrayAdapter(MyTweet.this,android.R.layout.simple_list_item_1, tweetData);
                    listView.setAdapter(simpleAdapter);

                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(MyTweet.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this Tweet?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                               Log.i("Info", tweetData.get(position));
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Tweet");
                                query.whereEqualTo("tweet",tweetData.get(position));
                                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                                query.getFirstInBackground(new GetCallback<ParseObject>() {

                                    @Override
                                    public void done(ParseObject object, com.parse.ParseException arg0) {
                                        // TODO Auto-generated method stub

                                            try {
                                                object.delete();
                                                object.saveInBackground();
                                               recreate();
                                                Toast.makeText(MyTweet.this, "Tweet deleted :)", Toast.LENGTH_SHORT).show();


                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                                Toast.makeText(MyTweet.this, e1.getMessage(), Toast.LENGTH_SHORT).show();

                                            }


                                    }
                                });

//



                            }
                        })
                        .setNegativeButton("No",null)
                        .show();

                return true;
            }
        });

    }
}
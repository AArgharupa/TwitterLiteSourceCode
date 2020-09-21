package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
ArrayList<String> users = new ArrayList<>();
ArrayAdapter adapter;
    FloatingActionButton nightMode;
    FloatingActionButton brightMode;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.tweet, menu);
        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.tweet){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Send a Tweet");
            final EditText tweetEditText = new EditText(this);
            builder.setView(tweetEditText);
            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("info", tweetEditText.getText().toString());
                    ParseObject tweet = new ParseObject("Tweet");
                    tweet.put("tweet",tweetEditText.getText().toString());
                    tweet.put("username",ParseUser.getCurrentUser().getUsername());
                    tweet.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e== null){
                                Toast.makeText(UserActivity.this,"Tweet Sent!", Toast.LENGTH_LONG).show();

                            }else {
                                Toast.makeText(UserActivity.this,"Tweet Failed :(", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }

            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("info","I don't wanna tweet!");
                    dialog.cancel();
                }
            });
            builder.show();

        }else if(item.getItemId()== R.id.logout){
ParseUser.logOut();
Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.viewFeed){
            Intent intent = new Intent(getApplicationContext(),FeedActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.viewTweet){
            Intent intent = new Intent(getApplicationContext(),MyTweet.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.help){
            Intent intent = new Intent(getApplicationContext(),helpActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("User List");

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
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_checked,users);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView)view;
                if(checkedTextView.isChecked()){
                   // Log.i("Info","Checked!");

                    ParseUser.getCurrentUser().add("isFollowing", users.get(position));
                    Toast.makeText(UserActivity.this,"Now Following! :)",Toast.LENGTH_SHORT).show();
                }else{
                   // Log.i("Info","Not checked");
                    ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(position));

                    List tempUsers = ParseUser.getCurrentUser().getList("isFollowing");
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().put("isFollowing",tempUsers);
                    Toast.makeText(UserActivity.this,"Unfollowed! :(",Toast.LENGTH_SHORT).show();


                }
                ParseUser.getCurrentUser().saveInBackground();

            }

        });

        ParseUser.getCurrentUser().add("isFollowing", null);
        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());

        userParseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null && objects.size()>0){
                    for(ParseUser user : objects){
                        users.add(user.getUsername());
                    }
                    adapter.notifyDataSetChanged();
                    for(String username: users){
                       if(ParseUser.getCurrentUser().getList("isFollowing").contains(username)){
                            listView.setItemChecked(users.indexOf(username),true);
                        }

                    }
                }
            }
        });
    }
}
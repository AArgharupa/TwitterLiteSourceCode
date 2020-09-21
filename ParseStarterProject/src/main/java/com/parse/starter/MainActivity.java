/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements  View.OnClickListener,View.OnKeyListener {

  Boolean signUpModeActive = true;
  TextView login;
  EditText username;
  EditText password;
  FloatingActionButton nightMode;
  FloatingActionButton brightMode;
  static SharedPreferences sharedPreferences1;
  static SharedPreferences.Editor editor;
  static boolean isDarkModeOn;
  RelativeLayout layout;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = new MenuInflater(this);
    menuInflater.inflate(R.menu.menu1, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.help){
      Intent intent = new Intent(getApplicationContext(),helpActivity.class);
      startActivity(intent);
    }

    return super.onOptionsItemSelected(item);
  }

  public  void showUserList(){
    Intent intent = new Intent(getApplicationContext(),UserActivity.class);
    startActivity(intent);

  }

  public void signUpClicked(View view){

    if (username.getText().toString().matches("") || password.getText().toString().matches("")) {

      Toast.makeText(this, "A username or password is required",Toast.LENGTH_SHORT).show();
    }else {
      if (signUpModeActive) {
        ParseUser user = new ParseUser();
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Log.i("SignUp", "Success");
              showUserList();
            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });

      }else {
        //login
        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if(user!= null){
              Log.i("LogIn","ok!");
              showUserList();
            }else{
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      }
    }


  }






  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setTitle("Twitter Lite");

    layout = findViewById(R.id.background);
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    nightMode = findViewById(R.id.nightMode);
    brightMode = findViewById(R.id.back);
    login = findViewById(R.id.loginTextView);
    login.setOnClickListener(this);
    username = findViewById(R.id.usernameEditText);
    password = findViewById(R.id.passwordEditText);
    ImageView logo = findViewById(R.id.imageView2);
    RelativeLayout background = findViewById(R.id.background);
    logo.setOnClickListener(this);
    background.setOnClickListener(this);

    password.setOnKeyListener(this);
    if(ParseUser.getCurrentUser() != null){
      showUserList();
    }

    sharedPreferences1 = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
    editor = sharedPreferences1.edit();
    isDarkModeOn = sharedPreferences1.getBoolean("isDarkModeOn", false);


    if (isDarkModeOn) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      layout.setBackgroundColor(0);
      layout.setBackgroundResource(R.drawable.water);

      nightMode.setVisibility(View.INVISIBLE);
      brightMode.setVisibility(View.VISIBLE);
      login.setTextColor(0xff1591e4);
    }
    else {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      layout.setBackgroundColor(0);
      layout.setBackgroundResource(R.drawable.book);
      brightMode.setVisibility(View.INVISIBLE);
      nightMode.setVisibility(View.VISIBLE);
      login.setTextColor(0xff000000);

    }


    nightMode.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view)
              {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                layout.setBackgroundColor(0);
                layout.setBackgroundResource(R.drawable.water);
               login.setTextColor(0xFF1591E4);
                recreate();
                nightMode.setVisibility(View.INVISIBLE);
                brightMode.setVisibility(View.VISIBLE);
                editor.putBoolean("isDarkModeOn", true);
                editor.apply();
              }
            });
    brightMode.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view)
              {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                layout.setBackgroundColor(0);
                layout.setBackgroundResource(R.drawable.book);
                login.setTextColor(0xff000000);

                recreate();
                nightMode.setVisibility(View.VISIBLE);
                brightMode.setVisibility(View.INVISIBLE);
                editor.putBoolean("isDarkModeOn", false);
                editor.apply();
              }
            });







    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  @Override
  public void onClick(View v) {
    if(v.getId() == R.id.loginTextView){
      Button signUpButton = findViewById(R.id.button);
      if(signUpModeActive){
        signUpModeActive = false;
        signUpButton.setText("LOGIN");
        login.setText("or,SIGN UP");
      }else{
        signUpModeActive = true;
        signUpButton.setText("SIGN UP");
        login.setText("or,LOGIN");
      }
    }else if(v.getId() == R.id.imageView2 || v.getId() == R.id.background){
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
  }

  @Override
  public boolean onKey(View v, int keyCode, KeyEvent event) {

    if(keyCode == event.KEYCODE_ENTER && event.getAction()== KeyEvent.ACTION_DOWN){
      signUpClicked(v);
    }
    return false;
  }
}
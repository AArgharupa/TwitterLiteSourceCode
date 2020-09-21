package com.parse.starter;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class helpActivity extends AppCompatActivity {
    TextView textView;
    FloatingActionButton nightMode;
    FloatingActionButton brightMode;
    FloatingActionButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        textView = findViewById(R.id.textView);
        back = findViewById(R.id.backButton);


        textView.setMovementMethod(new ScrollingMovementMethod());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        nightMode = findViewById(R.id.nightMode);
        brightMode = findViewById(R.id.brightMode);
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
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
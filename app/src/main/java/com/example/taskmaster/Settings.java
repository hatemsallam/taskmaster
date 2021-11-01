package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        SharedPreferences.Editor editor = sharedPreferences.edit();

        findViewById(R.id.button_save).setOnClickListener(view -> {
            TextView text = findViewById(R.id.text_username);

            String userName =text.getText().toString();

            editor.putString("userName",userName);
            editor.apply();


        });


    }
}
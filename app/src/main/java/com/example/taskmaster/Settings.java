package com.example.taskmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MainActivity.sendAnalytic();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Button button1 = findViewById(R.id.button_save);
        button1.setOnClickListener(view -> {
            TextView text = findViewById(R.id.text_username);
            RadioButton team1BTN = findViewById(R.id.team1_BTN_id);
            RadioButton team2BTN = findViewById(R.id.team2_BTN_id);
            RadioButton team3BTN = findViewById(R.id.team3_BTN_id);


            String id = "0";
            String name="";
            if(team1BTN.isChecked()){
                id="1";
                name ="Jo Hikers";
            }
            else if(team2BTN.isChecked()){
                id="2";
                name ="React Divers";
            }
            else if(team3BTN.isChecked()){
                id="3";
                name ="Runtime terror Team";
            }
            String username = text.getText().toString();
            editor.putString("USERNAME", username);
            editor.putString("TeamName",id);
            editor.putString("name",name);
            editor.apply();
            Toast submitted = Toast.makeText(getApplicationContext(),"SAVED!",Toast.LENGTH_SHORT);
            submitted.show();
        });


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
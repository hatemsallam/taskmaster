package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TaskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Intent intent = getIntent();
        String title = intent.getExtras().getString("title");
        TextView text = findViewById(R.id.text_taskTitle);
        text.setText(title);
        String body = intent.getExtras().getString("body");
        TextView text2 = findViewById(R.id.text_taskbody);
        text2.setText(body);
        String state = intent.getExtras().getString("state");
        TextView text3 = findViewById(R.id.text_taskstate);
        text3.setText(state);


    }
}
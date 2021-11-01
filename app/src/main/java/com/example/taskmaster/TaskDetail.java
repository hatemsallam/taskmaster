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

        String taskTitle = intent.getExtras().getString("task");

        TextView text = findViewById(R.id.text_taskTitle);

        text.setText(taskTitle);


    }
}
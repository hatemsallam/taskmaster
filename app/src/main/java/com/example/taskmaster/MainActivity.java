package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Task1","first Task",State.NEW.toString()));
        tasks.add(new Task("Task2","second Task",State.ASSIGNED.toString()));
        tasks.add(new Task("Task3","third Task",State.COMPLETE.toString()));
        tasks.add(new Task("Task4","fourth Task",State.IN_PROGRESS.toString()));
        tasks.add(new Task("Task5","fifth Task",State.COMPLETE.toString()));
        tasks.add(new Task("Task6","sixth Task",State.NEW.toString()));
        tasks.add(new Task("Task7","seventh Task",State.ASSIGNED.toString()));
        tasks.add(new Task("Task8","eighth Task",State.IN_PROGRESS.toString()));
        RecyclerView recyclerView = findViewById(R.id.recyclerid);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TaskAdapter(tasks));





        Button button1 = findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this,AddTask.class);
                startActivity(intent1);
            }
        });


        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this,AllTasks.class);
                startActivity(intent2);
            }
        });

        Button button3 = findViewById(R.id.button_settings);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(MainActivity.this,Settings.class);
                startActivity(intent3);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPreferences.getString("userName","Go and set the username");

        TextView welcome = findViewById(R.id.text_userNameReciever);
        welcome.setText(userName + "'s tasks");
    }

    public void taskDetail (View view) {
        int id = view.getId();
        TextView text = findViewById(id);
        String taskName = text.getText().toString();
        Intent intent4 = new Intent(MainActivity.this,TaskDetail.class);
        intent4.putExtra("task",taskName);
        startActivity(intent4);
    }


}
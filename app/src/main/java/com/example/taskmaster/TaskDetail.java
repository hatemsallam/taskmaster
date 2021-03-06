package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class TaskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Intent intent = getIntent();
        String title = intent.getExtras().getString("task_title");
        TextView text = findViewById(R.id.text_taskTitle);
        text.setText(title);
        String body = intent.getExtras().getString("task_body");
        TextView text2 = findViewById(R.id.text_taskbody);
        text2.setText(body);
        String state = intent.getExtras().getString("task_state");
        TextView text3 = findViewById(R.id.text_taskstate);
        text3.setText(state);

        ImageView taskImage = findViewById(R.id.taskimage);
        String fileUrl = intent.getExtras().getString("file_url");

        Picasso.get().load(fileUrl).into(taskImage);

        taskImage.setOnClickListener(view -> {
            Intent intentUrl = new Intent(Intent.ACTION_VIEW);
            intentUrl.setData(Uri.parse(fileUrl));
            startActivity(intentUrl);
        });


    }
}
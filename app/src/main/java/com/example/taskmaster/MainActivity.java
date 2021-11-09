package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="MainActivity" ;
    Handler handler;
    RecyclerView recyclerView ;
    ArrayList<Task> tasks = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            Amplify.addPlugin(new AWSDataStorePlugin()); // stores records locally
            Amplify.addPlugin(new AWSApiPlugin()); // stores things in DynamoDB and allows us to perform GraphQL queries
            Amplify.configure(getApplicationContext());

            Log.i(TAG, "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e(TAG, "Could not initialize Amplify", error);
        }


//        TaskDataBase db = TaskDataBase.getInstance(getApplicationContext());
//        tasks = (ArrayList<Task>) db.taskDao().getAll();
//        RecyclerView recyclerView = findViewById(R.id.recyclerid);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new TaskAdapter(tasks));


        recyclerView = findViewById(R.id.recyclerid);
        apiTaskGetter();

        handler = new Handler(Looper.getMainLooper(), message -> {
            Log.i("In Handler","In Handler");
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new TaskAdapter(tasks));
            recyclerView.getAdapter().notifyDataSetChanged();
            return false;
        });


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

//    public void taskDetail (View view) {
//        int id = view.getId();
//        TextView text = findViewById(id);
//        String taskName = text.getText().toString();
//        Intent intent4 = new Intent(MainActivity.this,TaskDetail.class);
//        intent4.putExtra("task",taskName);
//        startActivity(intent4);
//    }

    public void apiTaskGetter(){
        Amplify.API.query(ModelQuery.list(com.amplifyframework.datastore.generated.model.Task.class),
                taskReceived->{
                    tasks = new ArrayList<>();
                    for(com.amplifyframework.datastore.generated.model.Task task : taskReceived.getData())
                    {tasks.add(task);}
                    handler.sendEmptyMessage(0);
                },
                error->{Log.e(TAG, "Error In Retrieving Tasks", error); });
    }
}
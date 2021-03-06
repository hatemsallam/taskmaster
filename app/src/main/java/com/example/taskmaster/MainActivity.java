package com.example.taskmaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.core.Amplify;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;

import com.amazonaws.mobileconnectors.pinpoint.targeting.TargetingClient;
import com.amazonaws.mobileconnectors.pinpoint.targeting.endpointProfile.EndpointProfileUser;
import com.amazonaws.mobileconnectors.pinpoint.targeting.endpointProfile.EndpointProfile;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private List<Task> addedTasks = new ArrayList<>();
    Handler handler;
    Handler userNameHandler;
    String username = "";
    private static PinpointManager pinpointManager;
    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i(TAG, "INIT => " + userStateDetails.getUserState().toString());
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            final String token = task.getResult();
                            Log.d(TAG, "Registering push notifications token: " + token);
                            pinpointManager.getNotificationClient().registerDeviceToken(token);
                        }
                    });
        }
        return pinpointManager;
    }

    public void assignUserIdToEndpoint() {
        TargetingClient targetingClient = pinpointManager.getTargetingClient();
        EndpointProfile endpointProfile = targetingClient.currentEndpoint();
        EndpointProfileUser endpointProfileUser = new EndpointProfileUser();
        endpointProfileUser.setUserId("UserIdValue");
        endpointProfile.setUser(endpointProfileUser);
        targetingClient.updateEndpointProfile(endpointProfile);
        Log.d(TAG, "Assigned user ID " + endpointProfileUser.getUserId() +
                " to endpoint " + endpointProfile.getEndpointId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getAuthUserName();
        System.out.println("***************ON CREATE ****************");
        Intent intent = getIntent();
        String extra = intent.getStringExtra("Configured");
        System.out.println(extra);
//        if (extra == null) {
//            configureAmplify();
//        }
        sendAnalytic();
        //====================================================
        getPinpointManager(getApplicationContext());
        assignUserIdToEndpoint();
        //====================================================


        userNameHandler = new Handler(Looper.getMainLooper(),message -> {
            username = message.getData().getString("userName");
            TextView text = findViewById(R.id.text_userNameReciever);
            text.setText(username + "'s Tasks");
            return false;
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Button logOutBTN = findViewById(R.id.logoutbtn);
        logOutBTN.setOnClickListener(view -> {
            Amplify.Auth.signOut(
                    () -> {
                        Log.i("AuthQuickstart", "Signed out successfully");
                        Intent intent4 = new Intent(this, SignIn.class);
                        startActivity(intent4);
                    },
                    error -> Log.e("AuthQuickstart", error.toString())
            );
        });


        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, AddTask.class);
                startActivity(intent1);
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this, AllTasks.class);
                startActivity(intent2);
            }
        });






        Button button6 = findViewById(R.id.button_settings);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent6 = new Intent(MainActivity.this, Settings.class);
                startActivity(intent6);
            }
        });

        // Lab 28 create data to use it in the view

//        List<Task> taskData = new ArrayList<>();
//        taskData.add(new Task("Task 1" , "Linked List revision", "In progress"));
//        taskData.add(new Task("Task 2 ", "Review Stacks and Queues" ,"New"));
//        taskData.add(new Task("Task 3 ", "Solve the Lab" ,"Complete"));

        saveTeamToApi("Team1","1");
        saveTeamToApi("Team2","2");
        saveTeamToApi("Team3","3");


    }


    public static void sendAnalytic() {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("PasswordReset")
                .addProperty("Channel", "SMS")
                .addProperty("Successful", true)
                .addProperty("ProcessDuration", 792)
                .addProperty("UserAge", 120.3)
                .build();

        Amplify.Analytics.recordEvent(event);
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


    private void configureAmplify() {

        try {
            System.out.println("**********TRY METHOD************");
            Amplify.addPlugin(new AWSDataStorePlugin());
            // stores records locally
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSApiPlugin()); // stores things in DynamoDB and allows us to perform GraphQL queries
            Amplify.configure(getApplicationContext());


            Log.i(TAG, "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e(TAG, "Could not initialize Amplify", error);
        }

    }

    @Override
    protected void onResume() {
        System.out.println("******************ON RESUME ****************");

        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String USER = sharedPreferences.getString("USERNAME", "");
        String teamName = sharedPreferences.getString("TeamName", "");
        String name = sharedPreferences.getString("name", "");
        TextView intro = findViewById(R.id.text_userNameReciever);
        intro.setText(USER + "'s " + "TASKS");
//        if (!name.equals("")){
//            TextView teamNameField = findViewById(R.id.team_tasks_field);
//            teamNameField.setText(name + "'s " + "TASKS");
//        }


//        TaskDao taskDao;
//        AppDatabase appDatabase;
//        SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(this);
//        addedTasks = (List<com.amplifyframework.datastore.generated.model.Task>) AppDatabase.getInstance(this).taskDao().getAll();

        RecyclerView allTasksRecyclerView = findViewById(R.id.recyclerid);
        List<Task> tasks= new ArrayList<>();
        if(teamName.equals("")){
            tasks = getTasksFromApi(allTasksRecyclerView);
        }
        else{
            tasks = getTeamDataFromApi(allTasksRecyclerView);
        }

        Log.i("Hatem",tasks.toString());
        allTasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allTasksRecyclerView.setAdapter(new TaskAdapter(tasks));

        Log.i(TAG, "onResume: tasks " + tasks);
        RecyclerView recyclerView = findViewById(R.id.recyclerid);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new TaskAdapter(addedTasks));
        List<Task> finalTasks = tasks;
        recyclerView.setAdapter(new TaskAdapter(tasks, new TaskAdapter.OnTaskItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intentTaskDetails = new Intent(getApplicationContext(), TaskDetail.class);
                System.out.println(finalTasks.get(position).getTeamId()+"HEREEEEEEEEEE");
                intentTaskDetails.putExtra("task_title", finalTasks.get(position).getTitle());
                intentTaskDetails.putExtra("task_body", finalTasks.get(position).getBody());
                intentTaskDetails.putExtra("task_state", finalTasks.get(position).getState());
                intentTaskDetails.putExtra("team_id", finalTasks.get(position).getTeamId()+"team");
                intentTaskDetails.putExtra("file_url", finalTasks.get(position).getFileKey());
                startActivity(intentTaskDetails);

            }
        }));
    }

    private  List<Task> getTasksFromApi( RecyclerView recycler_view ){
        Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                recycler_view.getAdapter().notifyDataSetChanged();
                return false;
            }
        });
        List<Task> addedTasks=new ArrayList<>();
        Amplify.API.query(
                ModelQuery.list(Task.class),
                response -> {
                    for (Task task : response.getData()) {
                        addedTasks.add(task);
                        addedTasks.toString();
                        Log.i(TAG, addedTasks.toString());
                        Log.i(TAG, "Successful query, Task Found.");
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "TASK NOT FOUND.")
        );

        return  addedTasks;
    }


    private  List<Task> getTeamDataFromApi( RecyclerView recycler_view ){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String team = sharedPreferences.getString("TeamName","");
        String name = sharedPreferences.getString("name","");
        System.out.println(team +"TEAAAAAAAAAAMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
        Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                recycler_view.getAdapter().notifyDataSetChanged();
                return false;
            }
        });

        List<Task> addedTasks=new ArrayList<>();
        Amplify.API.query(
                ModelQuery.list(Task.class,Task.TEAM_ID.contains(team)),
                response -> {
                    for (Task task : response.getData()) {
                        addedTasks.add(task);
                        addedTasks.toString();
                        Log.i(TAG, addedTasks.toString());
                        Log.i(TAG, "Successful query, Tasks Found.");
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.i(TAG, "TASK NOT FOUND")
        );

        return  addedTasks;
    }

    // saving teams to the cloud
    private void saveTeamToApi(String teamName, String id) {
        Team team = Team.builder().teamName(teamName).id(id).build();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Amplify.API.mutate(
                ModelMutation.create(team), results -> {
                    Log.i(TAG, "Team Saved");
                    System.out.println(team.getId());
                    editor.putString("team_Id", team.getId());
                    editor.apply();
                }, error -> {
                    Log.i(TAG, "Team Not Saved");
                }
        );
    }
    public void getAuthUserName(){
        Amplify.Auth.fetchUserAttributes(
                attributes -> {Log.i("AuthDemo", "User attributes = " + attributes.get(2).getValue());
                    Bundle bundle = new Bundle();
                    bundle.putString("userName",attributes.get(2).getValue());
                    Message message = new Message();
                    message.setData(bundle);
                    userNameHandler.sendMessage(message);
                },
                error -> Log.e("AuthDemo", "Failed to fetch user attributes.", error)
        );
    }
}
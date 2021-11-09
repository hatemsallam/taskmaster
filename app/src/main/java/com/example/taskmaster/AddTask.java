package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;

public class AddTask extends AppCompatActivity {
    private static final String TAG = "AddTask";
   public static int counter = 0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Button button2 = findViewById(R.id.button3);
        button2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                TextView textView = findViewById(R.id.textView6);
                counter++;
                textView.setText("Total Tasks :"+counter);

                EditText taskTitleField = findViewById(R.id.plaintext_tasktitle);
                String taskTitle = taskTitleField.getText().toString();

                EditText taskBodyField = findViewById(R.id.plaintext_taskbody);
                String taskBody = taskBodyField.getText().toString();

                EditText taskStateField = findViewById(R.id.plaintext_taskstate);
                String taskState = taskStateField.getText().toString();

                Task task = new Task(taskTitle, taskBody, taskState);
                Long addedTaskID = TaskDataBase.getInstance(getApplicationContext()).taskDao().insertTask(task);



                com.amplifyframework.datastore.generated.model.Task task1 = com.amplifyframework.datastore.generated.model.Task.builder()
                        .title(taskTitle).body(taskBody).state(taskState).build();
                apiTaskSave(task1);

                Toast punchToast = Toast.makeText(getApplicationContext(),"submitted!", Toast.LENGTH_SHORT);
                punchToast.show();
            }
        });

    }

    public void apiTaskSave(com.amplifyframework.datastore.generated.model.Task task){
        Amplify.API.mutate(ModelMutation.create(task),
                taskSaved ->{ Log.i(TAG, "Task Is Saved => " + taskSaved.getData().getTitle());},
                error->{Log.e(TAG, "Task Is Not Saved => " + error.toString());});
    }




}
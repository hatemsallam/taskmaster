package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;

import br.com.onimur.handlepathoz.HandlePathOz;
import br.com.onimur.handlepathoz.HandlePathOzListener;
import br.com.onimur.handlepathoz.model.PathOz;
import static android.content.Intent.ACTION_PICK;
import static android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class AddTask extends AppCompatActivity implements HandlePathOzListener.SingleUri {
    private static final String TAG = "AddTask";
   public static int counter = 0 ;
    private static final int REQUEST_PERMISSION = 123;
    private static final int REQUEST_OPEN_GALLERY = 1111;
    private HandlePathOz handlePathOz;
    String fileName;
    Handler fileNameHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        handlePathOz = new HandlePathOz(this, this);

        Button uploadBtn = findViewById(R.id.choosefileid);
        uploadBtn.setOnClickListener(view -> {
            openFile();
        });

        fileNameHandler = new Handler(Looper.getMainLooper(), message -> {
            fileName = message.getData().getString("fileName1");
            return false;
        });

        Button button2 = findViewById(R.id.button4);
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

                RadioButton team1Btn = findViewById(R.id.team1_id);
                RadioButton team2Btn = findViewById(R.id.team2_id);
                RadioButton team3Btn = findViewById(R.id.team3_id);

                String id = "0";
                if(team1Btn.isChecked()){
                    id="1";
                }
                else if(team2Btn.isChecked()){
                    id="2";
                }
                else if(team3Btn.isChecked()){
                    id="3";
                }

                Task task = new Task(taskTitle, taskBody, taskState);
                Long addedTaskID = TaskDataBase.getInstance(getApplicationContext()).taskDao().insertTask(task);



                com.amplifyframework.datastore.generated.model.Task task1 = com.amplifyframework.datastore.generated.model.Task.builder()
                        .teamId(id).title(taskTitle).body(taskBody).state(taskState).fileKey(fileName).build();
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


    @Override
    public void onRequestHandlePathOz(@NonNull PathOz pathOz, @Nullable Throwable throwable) {
        if (throwable != null) {
            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }
        String filename = FilenameUtils.getName(pathOz.getPath());
        File file = new File(pathOz.getPath());
        Amplify.Storage.uploadFile(
                filename,
                file,
                result -> {Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey());
                    Amplify.Storage.getUrl(
                            result.getKey(),
                            resultUrl -> {
                                Log.i("MyAmplifyApp", "Successfully generated: " + resultUrl.getUrl());
                                Bundle bundle = new Bundle();
                                bundle.putString("fileName1",resultUrl.getUrl().toString());
                                Message message = new Message();
                                message.setData(bundle);
                                fileNameHandler.sendMessage(message);},
                            error -> Log.e("MyAmplifyApp", "URL generation failure", error)
                    );
                },
                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );
    }

    private void openFile(){
        if (checkSelfPermission()) {
            Intent intent;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                intent = new Intent(ACTION_PICK, EXTERNAL_CONTENT_URI);
            } else {
                intent = new Intent(ACTION_PICK, INTERNAL_CONTENT_URI);
            }

            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.putExtra("return-data", true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivityForResult(intent, REQUEST_OPEN_GALLERY);
        }
    }

    private boolean checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            return false;
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                //set Uri to handle
                Log.i("in onActivityResult",uri.toString());
                handlePathOz.getRealPath(uri);
                //show Progress Loading
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFile();
            } else {
                Log.i(TAG + " ==> onRequestPermissionsResult", "Error : Permission Field");
            }
        }
    }

}
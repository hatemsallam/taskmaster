package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

public class SignIn extends AppCompatActivity {
    private static final String TAG = "SignIn";
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        configureAmplify();
        Button signUpButton = findViewById(R.id.SignUpbuttonsignInpage);
        Button signInButton = findViewById(R.id.signInbuttonid);
        Toast toast = Toast.makeText(this, "Invalid Email or Password , Try Again", Toast.LENGTH_LONG);
        handler = new Handler(Looper.getMainLooper(), message -> {
            toast.show();
            return false;
        });


        signUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
        });

        signInButton.setOnClickListener(view -> {
            EditText emailText = findViewById(R.id.emailinputid);
            EditText passwordText = findViewById(R.id.signInpasswordInput);
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            Amplify.Auth.signIn(
                    email,
                    password,
                    result -> {
                        Log.i("AuthQuickstart", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
                        Intent intent1 = new Intent(this, MainActivity.class);
                        startActivity(intent1);
                    },
                    error -> {
                        Log.e("AuthQuickstart", error.toString());
                        handler.sendEmptyMessage(1);
                    }
            );

        });


    }


    private void configureAmplify() {

        try {
            System.out.println("**********TRY METHOD************");
//            Amplify.addPlugin(new AWSDataStorePlugin());
            // stores records locally
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSApiPlugin()); // stores things in DynamoDB and allows us to perform GraphQL queries
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());



            Log.i(TAG, "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e(TAG, "Could not initialize Amplify", error);
        }

    }
}
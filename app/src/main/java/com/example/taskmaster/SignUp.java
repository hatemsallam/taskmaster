package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        Button signUpKActionBtn = findViewById(R.id.SignUppagebuttonid);
        signUpKActionBtn.setOnClickListener(view -> {
            EditText userNameText = findViewById(R.id.userNameSignupId);
            EditText emailText = findViewById(R.id.userNameSignupId2);
            EditText passwordText = findViewById(R.id.userNameSignupId3);

            String userName = userNameText.getText().toString();
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            AuthSignUpOptions options = AuthSignUpOptions.builder()
                    .userAttribute(AuthUserAttributeKey.name(),userName )
                    .build();
            Amplify.Auth.signUp(email, password, options,
                    result -> {
                        Log.i("AuthQuickStart ==> Sign Up Page", "Result: " + result.toString());
//                        editor.putString("userEmail", email);
//                        editor.apply();
                        Intent intent = new Intent(this, ConfirmUser.class);
                        intent.putExtra("userEmail",email);
                        startActivity(intent);},
                    error -> Log.e("AuthQuickStart ==> Sign Up Page", "Sign up failed", error)
            );
        });



    }
}
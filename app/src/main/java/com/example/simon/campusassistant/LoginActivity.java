package com.example.simon.campusassistant;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Xinmeng Lyu.
 */

public class LoginActivity extends Activity {
    private Button mLoginButton;
    private Button mSignupButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar mProgressBar;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mSignupButton = (Button) findViewById(R.id.signup_button);
        emailEditText = (EditText) findViewById(R.id.name_editText);
        passwordEditText = (EditText) findViewById(R.id.password_editText);
        mProgressBar = (ProgressBar) findViewById(R.id.login_progressBar);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        mSignupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(){
        String loginEmail = emailEditText.getText().toString().trim();
        String loginPassword = passwordEditText.getText().toString().trim();

        if(TextUtils.isEmpty(loginEmail)){
            //The email is empty
            emailEditText.setError("Email required");
            emailEditText.requestFocus();
            return;
        }

        //check validation of email
        if(!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()){
            emailEditText.setError("Please enter valid email.");
            emailEditText.requestFocus();
        }

        if(TextUtils.isEmpty(loginPassword)){
            //The password is empty
            passwordEditText.setError("Password required");
            passwordEditText.requestFocus();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);

        mFirebaseAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mProgressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

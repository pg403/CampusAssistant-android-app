package com.example.simon.campusassistant;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

/**
 * Created by Xinmeng Lyu.
 */

public class SignupActivity extends Activity {

    private Button registerButton ;
    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseAuth mFirebaseAuth;
    private ProgressBar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        registerButton = (Button) findViewById(R.id.retister_button);
        emailEditText = (EditText) findViewById(R.id.signupEmail_editText);
        passwordEditText = (EditText) findViewById(R.id.signupPassword_editText);
        mBar = (ProgressBar) findViewById(R.id.signup_progressBar);

        mFirebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser(){
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //The email is empty
            emailEditText.setError("Email required");
            emailEditText.requestFocus();
            return;
        }

        //check validation of email
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please enter valid email.");
            emailEditText.requestFocus();
        }

        if(TextUtils.isEmpty(password)){
            //The password is empty
            passwordEditText.setError("Password required");
            passwordEditText.requestFocus();
            return;
        }

        if(password.length()<6){
            passwordEditText.setError("Password must contains at least 6 characters.");
            passwordEditText.requestFocus();
        }

        mBar.setVisibility(View.VISIBLE);
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            //User has successfully signed up, Now go to the main activity
                            Toast.makeText(SignupActivity.this, "Sign-up successful.", Toast.LENGTH_SHORT).show();
                            mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(SignupActivity.this, "User already existed." , Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(SignupActivity.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}

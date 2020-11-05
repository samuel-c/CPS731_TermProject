package com.example.cps731_termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button btnSignIn;
    TextView btnRegister;
    private FirebaseAuth mAuth;

    private final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Log.d(TAG, "test: " + Boolean.toString(mAuth.getCurrentUser() != null));
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        else {
            mEmail = findViewById(R.id.editTextEmail);
            mPassword = findViewById(R.id.editTextPassword);

            btnSignIn = findViewById(R.id.buttonLogin);
            btnRegister = findViewById(R.id.buttonLoginRegister);

            btnSignIn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String email = mEmail.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
                }
            });
            btnRegister.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "AHHHHHHHHHHHHHH");
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                }
            });
        }

    }
}
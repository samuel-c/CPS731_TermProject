package com.example.cps731_termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    //String userID;

    EditText mFullName, mEmail, mPassword, mPhoneNumber;
    Button mRegisterBtn;

    private final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        mFullName = findViewById(R.id.editTextRegisterName);
        mEmail = findViewById(R.id.editTextRegisterEmail);
        mPhoneNumber = findViewById(R.id.editTextRegisterPhoneNumber);
        mPassword = findViewById(R.id.editTextRegisterPassword);

        mRegisterBtn = findViewById(R.id.buttonRegister2);
        if (user != null){
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        }
        else{
            mRegisterBtn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    String email = mEmail.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();
                    String fName = mFullName.getText().toString().trim();
                    String phoneNum = mPhoneNumber.getText().toString().trim();

                    if (TextUtils.isEmpty(email)){
                        mEmail.setError("Email is required.");
                    }
                    if (TextUtils.isEmpty(password)){
                        mEmail.setError("Password is Required!");
                    }
                    if (password.length() < 6){
                        mPassword.setError("Password must be longer than 6 characters");
                    }

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener((task -> {
                        if (task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, Boolean.toString(user == null));
                            Log.d(TAG, "test: " + user.getUid());
                            DocumentReference documentReference = db.collection("users").document(user.getUid());
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("fName", fName);
                            userData.put("email", email);
                            userData.put("pNum", phoneNum);
                            documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: User Profile is created for " + user.getUid());
                                }
                            }). addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                        }
                        else{
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }));
                }
            });
        }


    }

    public void OnStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser account){

        if(account != null){
            Toast.makeText(this,"U Signed In successfully",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,LoginActivity.class));

        }else {
            Toast.makeText(this,"U Didnt signed in",Toast.LENGTH_LONG).show();
        }

    }
}
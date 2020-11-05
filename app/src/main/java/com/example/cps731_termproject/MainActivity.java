package com.example.cps731_termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String USER_KEY = "username";
    public static final String PASSWORD_KEY = "username";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private final String TAG = "MainActivity";

    TextView mName, mEmail;
    Button btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        //Auth.signOut();
        // If not authenticated redirect to login
        if (user != null){
            mName = findViewById(R.id.textViewDisplayName);
            mEmail = findViewById(R.id.textViewDisplayEmail);

            Log.d(TAG, Boolean.toString(user == null));
            Log.d(TAG, "test: " + user.getUid());

            DocumentReference documentReference = db.collection("users").document(user.getUid());

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            mName.setText(document.getString("fName"));
                            mEmail.setText(document.getString("email"));
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                    else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            btnSignOut = findViewById(R.id.buttonSignOut);
            btnSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });

        }
        else{
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        }


        //String name = db.

//        String name = user.getDisplayName();
//        String email = user.getEmail();


//        mName.setText(name);
//

//        mEmail.setText(email);
    }



}
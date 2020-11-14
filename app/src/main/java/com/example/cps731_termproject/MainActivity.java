package com.example.cps731_termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.cps731_termproject.utils.Alarm;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private final String TAG = "MainActivity";

    TextView mName, mEmail;
    Button btnSignOut;

    // UI
    EditText editText;
    Button btnAdd, btnReset, btnAddAlarm;
    RecyclerView recyclerView;
    TimePicker timePicker;

    List<Alarm> dataList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    RoomDB database;
    AlarmClockAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediaPlayer alarmMusic = new MediaPlayer();
        alarmMusic = alarmMusic.create(getApplicationContext(), R.raw.alarm_sound);

        // Authenticate com.example.cps731_termproject.utils.User
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

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

            // UI
            recyclerView = findViewById(R.id.recycler_view);
            btnAdd = findViewById(R.id.btn_add);
            btnReset = findViewById(R.id.btn_reset);
            btnAddAlarm = findViewById(R.id.btn_add_alarm);
            editText = findViewById(R.id.edit_text);


            //timePicker = (TimePicker)findViewById(R.id.timePicker)

            // init database
            database = RoomDB.getInstance(this);
            // store database value in data list
            dataList = database.mainDao().getAll();

            linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new AlarmClockAdapter(MainActivity.this, dataList);
            recyclerView.setAdapter(adapter);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sText = editText.getText().toString().trim();
                    Log.d(TAG, "Test: " + sText);
                    if(!sText.equals("")){
                        //If not empty
                        Alarm data = new Alarm();
                        data.setAlarmName(sText);
                        database.mainDao().insert(data);
                        Log.d(TAG, "Test: " + data.toString());
                        editText.setText("");

                        dataList.clear();
                        dataList.addAll(database.mainDao().getAll());
                        adapter.notifyDataSetChanged();
                    }
                }
            });

            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Delete all data from db
                    database.mainDao().deleteAll(dataList);

                    dataList.clear();
                    dataList.addAll(database.mainDao().getAll());
                    adapter.notifyDataSetChanged();
                }
            });



            Calendar c = Calendar.getInstance();
            TimePickerDialog.OnTimeSetListener d = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Date currentTime = Calendar.getInstance().getTime();

                    Alarm alarm = new Alarm("Alarm #" + (dataList.size() + 1), hourOfDay, minute, 0);
                    database.mainDao().insert(alarm);

                    dataList.clear();
                    dataList.addAll(database.mainDao().getAll());
                    adapter.notifyDataSetChanged();
                    //c.set((Calendar.HOUR_OF_DAY), hourOfDay);
                    //c.set((Calendar.MINUTE), minute);



                    if (c.get(Calendar.AM_PM) == 0){
                        Log.d(TAG, "Test2: Hour: " + c.get(Calendar.HOUR_OF_DAY) + ", Minutes: " + c.get(Calendar.MINUTE) + "AM");
                        Log.d(TAG, "Current hr: " + currentTime.getHours());
                        Log.d(TAG, "Current min: " + currentTime.getMinutes());

                    }
                    else{
                        Log.d(TAG, "Test2: Hour: " + c.get(Calendar.HOUR_OF_DAY) + ", Minutes: " + c.get(Calendar.MINUTE) + "PM");
                        Log.d(TAG, "Current time: " + currentTime);
                        Log.d(TAG, "Current hr: " + currentTime.getHours());
                        Log.d(TAG, "Current min: " + currentTime.getMinutes());
                    }

                }
            };

            btnAddAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(MainActivity.this, d, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
                    //new TimePickerDialog(MainActivity.this, d, c.)
                }
            });
            /*

                btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sText = editText.getText().toString().trim();
                    Log.d(TAG, "Test: " + sText);
                    if(!sText.equals("")){
                        //If not empty
                        Alarm data = new Alarm();
                        data.setAlarmName(sText);
                        database.mainDao().insert(data);
                        Log.d(TAG, "Test: " + data.toString());
                        editText.setText("");

                        dataList.clear();
                        dataList.addAll(database.mainDao().getAll());
                        adapter.notifyDataSetChanged();
                    }
                }
            });


            RecyclerView recyclerView = findViewById(R.id.recyclerview);
            final AlarmClockAdapter adapter = new AlarmClockAdapter(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager((this)));

            // Set up the AlarmViewModel.
            mAlarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
            // Get all the alarms from the database
            // and associate them to the adapter.
            mAlarmViewModel.getAllAlarms().observe(this, new Observer<List<Alarm>>() {
                @Override
                public void onChanged(@Nullable final List<Alarm> alarms) {
                    // Update the cached copy of the alarms in the adapter.
                    adapter.setAlarms(alarms);
                }
            });

             */
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
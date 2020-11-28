package com.example.cps731_termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.cps731_termproject.utils.Alarm;
import com.example.cps731_termproject.utils.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;


    private final String TAG = "MainActivity";

    TextView mName;
    //TextView mEmail;
    //Button btnSignOut;

    // UI
    EditText editText;
    Button btnAdd, btnReset, btnAddAlarm;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    TimePicker timePicker;

    List<Alarm> dataList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    RoomDB database;
    public AlarmClockAdapter adapter;
    AlarmManager alarmManager;

    private Toolbar toolbar;


    private static final int REQUEST = 112;
    String [] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) MainActivity.this, PERMISSIONS, REQUEST );
        }
        if (!NetworkUtils.isNetworkConnected(this)){
            startActivity(new Intent(MainActivity.this, NoInternetActivity.class));
        }

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        MediaPlayer alarmMusic = new MediaPlayer();
        alarmMusic = alarmMusic.create(getApplicationContext(), R.raw.alarm_sound);

        // Authenticate
        mAuth = getmAuth();
        user = mAuth.getCurrentUser();
        db = getDb();

        if (user != null){
            mName = findViewById(R.id.textViewDisplayName);
            //mEmail = findViewById(R.id.textViewDisplayEmail);

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
                            mName.setText("Welcome back, " + document.getString("fName") + "!");
                            //mEmail.setText(document.getString("email"));
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                    else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
/*
            btnSignOut = findViewById(R.id.buttonSignOut);
            btnSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });

 */

            // UI
            recyclerView = findViewById(R.id.recycler_viewAlarms);
            btnAdd = findViewById(R.id.btn_add);
            btnReset = findViewById(R.id.btn_reset);
            btnAddAlarm = findViewById(R.id.btn_add_alarm);
            editText = findViewById(R.id.edit_textLocation);
            fab = findViewById(R.id.fab);


            //timePicker = (TimePicker)findViewById(R.id.timePicker)

            // init database
            database = RoomDB.getInstance(this);
            // store database value in data list
            dataList = database.mainDao().getAll();

            linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new AlarmClockAdapter(MainActivity.this, dataList);
            recyclerView.setAdapter(adapter);


/*
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sText = editText.getText().toString().trim();
                    Log.d(TAG, "Test: " + sText);
                    if(!sText.equals("")){
                        //If not empty
                        Date currentTime = Calendar.getInstance().getTime();
                        Alarm data = new Alarm(sText, currentTime.getHours(), currentTime.getMinutes(),0);
                        //data.setAlarmName(sText);
                        database.mainDao().insert(data);
                        Log.d(TAG, "Test: " + data.toString());
                        editText.setText("");

                        dataList.clear();
                        dataList.addAll(database.mainDao().getAll());
                        adapter.notifyDataSetChanged();
                    }
                }
            });
*/
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

            // Alarm ringtone
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Ringtone ringtone = RingtoneManager.getRingtone(this, alarmUri);

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

                    alarm = database.mainDao().getAlarmByName(alarm.getAlarmName(), alarm.getHours(), alarm.getMinutes());

                    // Initialize current Alarm
                    Calendar currentAlarmTime = Calendar.getInstance();
                    currentAlarmTime.set(Calendar.HOUR_OF_DAY, alarm.getHours());
                    currentAlarmTime.set(Calendar.MINUTE, alarm.getMinutes());
                    currentAlarmTime.set(Calendar.SECOND, 0);

                    if (currentAlarmTime.before(Calendar.getInstance()))
                        currentAlarmTime.add(Calendar.DAY_OF_MONTH, 1); // Go next day

                    Log.d(TAG, "Cal: " + currentAlarmTime.getTime().toString());

                    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                    intent.putExtra("alarmSID", alarm.getId());
                    intent.putExtra("newContext", true);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Log.d(TAG, "Alarm Started: Name: " + alarm.getAlarmName() + ", ID: " + alarm.getId());


                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, currentAlarmTime.getTimeInMillis(), pendingIntent);


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

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar currentTime = Calendar.getInstance();
                    currentTime.add(Calendar.SECOND, 5);
                    Alarm alarm = new Alarm("Alarm #" + (dataList.size() + 1), currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), currentTime.get(Calendar.SECOND));
                    database.mainDao().insert(alarm);

                    dataList.clear();
                    dataList.addAll(database.mainDao().getAll());
                    adapter.notifyDataSetChanged();
                    //c.set((Calendar.HOUR_OF_DAY), hourOfDay);
                    //c.set((Calendar.MINUTE), minute);

                    alarm = database.mainDao().getAlarmByName(alarm.getAlarmName(), alarm.getHours(), alarm.getMinutes());

                    // Initialize current Alarm
                    Calendar currentAlarmTime = Calendar.getInstance();
                    currentAlarmTime.set(Calendar.HOUR_OF_DAY, alarm.getHours());
                    currentAlarmTime.set(Calendar.MINUTE, alarm.getMinutes());
                    currentAlarmTime.set(Calendar.SECOND, alarm.getSeconds());

                    if (currentAlarmTime.before(Calendar.getInstance()))
                        currentAlarmTime.add(Calendar.DAY_OF_MONTH, 1); // Go next day

                    Log.d(TAG, "Cal: " + currentAlarmTime.getTime().toString());

                    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                    intent.putExtra("alarmSID", alarm.getId());
                    intent.putExtra("newContext", true);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Log.d(TAG, "Alarm Started: Name: " + alarm.getAlarmName() + ", ID: " + alarm.getId() + ", " + alarm.getFormattedTime());


                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, currentAlarmTime.getTimeInMillis(), pendingIntent);
                }
            });

            btnAddAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, InfoActivity.class));
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(MainActivity.this, d, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false).show();
                }
            });

        }
        else{
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }


}
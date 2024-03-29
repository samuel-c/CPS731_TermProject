package com.example.cps731_termproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cps731_termproject.utils.Alarm;
import com.example.cps731_termproject.utils.NetworkUtils;
import com.example.cps731_termproject.utils.RingtonePlayer;
import com.example.cps731_termproject.utils.WakeLocker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmActivity extends AppCompatActivity {

    private final String TAG = "AlarmActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    // UI
    TextView text_time;
    Button btn_alarm_off, btn_snooze;

    // Database
    RoomDB database;

    // Alarm manager
    private AlarmManager alarmManager;

    AlarmClockAdapter adapter;

    @Override
    public void onBackPressed(){
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if (!NetworkUtils.isNetworkConnected(this)){
            startActivity(new Intent(this, NoInternetActivity.class));
        }

        alarmManager = (AlarmManager) AlarmActivity.this.getSystemService(Context.ALARM_SERVICE);


        database = RoomDB.getInstance(this);
        // store database value in data list
        //dataList = database.mainDao().getAll();

        // UI
        btn_alarm_off = findViewById(R.id.btn_alarm_off);
        btn_snooze = findViewById(R.id.btn_snooze);
        text_time = findViewById(R.id.txt_time);

        // Get associated alarm
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Alarm alarm = (Alarm) database.mainDao().getAlarm(bundle.getInt("alarmSID"));
        int sID = alarm.getId();

        btn_snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "SWITCH CLICKED");
                alarm.setState(0); // STANDBY state

                RingtonePlayer.ringtone.stop(); // Stop ringing

                // Get current Alarm Time
                Calendar currentTime = Calendar.getInstance();
                currentTime.add(Calendar.SECOND, 5);

                Intent intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
                intent.putExtra("alarmSID", alarm.getId());
                intent.putExtra("newContext", false);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, sID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.d(TAG, "Alarm Started: " + alarm.getAlarmName() + sID);

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, currentTime.getTimeInMillis(), pendingIntent);

                // Update current alarm state
                database.mainDao().updateState(sID, alarm.getState());

                Log.d(TAG, "Switch is: " + (alarm.getState() != 1 ? "on" : "off"));

                /*
                // Update dataset
                dataList.clear();
                dataList.addAll(database.mainDao().getAll());
                notifyDataSetChanged();

                 */
            }
        });

        // UI Time Update
        final Handler someHandler = new Handler(getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                text_time.setText((calendar.get(Calendar.HOUR) < 10 ? "0" + calendar.get(Calendar.HOUR) : calendar.get(Calendar.HOUR)) + ":" +
                        (calendar.get(Calendar.MINUTE) < 10 ? "0" + calendar.get(Calendar.MINUTE) : calendar.get(Calendar.MINUTE)) + " " +
                        (calendar.get(Calendar.AM_PM) == 0 ? "AM": "PM" ));
                someHandler.postDelayed(this, 1000);
            }
        }, 10);

        btn_alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean temp;
                int tempState = 1; // default alarm off
                for (int i = 0 ; i < alarm.getDaysOfWeek().length; i ++){
                    temp = alarm.getDaysOfWeek()[i];
                    // If repeat change state to standby
                    if (temp){
                        tempState = 0; // Standby
                        break;
                    }
                }

                alarm.setState(tempState);
                database.mainDao().updateState(alarm.getId(), tempState);
                Log.i(TAG, "" + tempState);
                RingtonePlayer.ringtone.stop();
                startActivity(new Intent(AlarmActivity.this, InfoActivity.class));
                finish();
            }
        });

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

    }
}
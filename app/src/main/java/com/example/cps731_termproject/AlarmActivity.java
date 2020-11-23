package com.example.cps731_termproject;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cps731_termproject.utils.WakeLocker;

public class AlarmActivity extends AppCompatActivity {

    Button btn_alarm_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        btn_alarm_off = findViewById(R.id.btn_alarm_off);
        btn_alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WakeLocker.release(); // Turn off screen lock
            }
        });

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        //Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        //ringtone.play();
    }
}
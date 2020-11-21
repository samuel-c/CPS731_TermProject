package com.example.cps731_termproject;

import androidx.appcompat.app.AppCompatActivity;

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
    }
}
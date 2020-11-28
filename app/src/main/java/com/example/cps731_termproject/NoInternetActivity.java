package com.example.cps731_termproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cps731_termproject.utils.NetworkUtils;

public class NoInternetActivity extends AppCompatActivity {

    Button btn_tryAgain;

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        btn_tryAgain = findViewById(R.id.btn_tryAgain);

        btn_tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkConnected(getApplicationContext())){
                    startActivity(new Intent(NoInternetActivity.this, MainActivity.class));
                }

            }
        });

    }
}
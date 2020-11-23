package com.example.cps731_termproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.example.cps731_termproject.utils.Alarm;
import com.example.cps731_termproject.utils.RingtonePlayer;
import com.example.cps731_termproject.utils.WakeLocker;
//import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 0;
    private final String TAG = "AlarmReceiver";
    public RoomDB database;

    @Override
    public void onReceive(Context context, Intent intent) {
        database = RoomDB.getInstance(context);
        Toast.makeText(context, "ALARM GOING OFF", Toast.LENGTH_LONG).show();
        Log.d(TAG, "ALARM GOING OFF!!!!!!!!");
        WakeLocker.acquire(context);
        try{
            Alarm alarm = database.mainDao().getAlarm(intent.getIntExtra("alarmSID", 0));
            boolean newContext = intent.getBooleanExtra("newContext", true);
            alarm.setState(2); // RINGING state

            RingtonePlayer.RingtonePlayer(context);

            //Bundle bundle = intent.getExtras();
            //Alarm alarm = (Alarm) bundle.getSerializableExtra("alarm");

            if (newContext) {
                Intent newIntent = new Intent(context, AlarmActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                newIntent.putExtra("alarmSID", alarm.getId());
                context.startActivity(newIntent);
            }
        }
        catch(Exception e){
            Toast.makeText(context, "There was an error, but we got the alarm", Toast.LENGTH_LONG).show();
            Log.e("AlarmReceiver", Log.getStackTraceString(e));
        }



        /*
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Create the content intent for the notification, which launches this activity
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Build the notification
        Notification notification = new Notification.Builder(context).setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("Test")
                .setContentText("Alarm")
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL).build();

        /*
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stand_up)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);



        //Deliver the notification
        notificationManager.notify(NOTIFICATION_ID, notification);
        */
    }

}
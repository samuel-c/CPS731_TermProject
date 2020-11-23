package com.example.cps731_termproject.utils;

import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.cps731_termproject.R;

import java.io.Serializable;

@Entity(tableName = "alarm_table")
public class Alarm implements Serializable {
    //private static final Object R = ;

    public int STANDBY = 0;
    public int STOPPED = 1;
    public int RINGING = 2;
    @Ignore
    public Ringtone ringtone;

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "alarmName")
    private String alarmName;

    @ColumnInfo(name = "hourOfDay")
    public int hourOfDay;

    @ColumnInfo(name = "minutes")
    int minutes;

    @ColumnInfo(name = "seconds")
    int seconds;

    @ColumnInfo(name = "daysOfWeek")
    boolean [] daysOfWeek;

    @ColumnInfo(name = "state")
    int state;

    @ColumnInfo(name = "vibration")
    boolean vibration;
    //@NonNull
    //@ColumnInfo(name = "music")
    //MediaPlayer alarmMusic;

    public Alarm(String alarmName, int hours, int minutes, int seconds, Ringtone ringtone){
        this.alarmName = alarmName;
        this.hourOfDay = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        daysOfWeek = new boolean[] {false, false, false, false, false, false, false};
        state = STANDBY;
        vibration = false;
        this.ringtone = ringtone;
        //this.alarmMusic = alarmMusic;
    }

    public Alarm(){
        this.id = id;
        this.alarmName = alarmName;
        this.hourOfDay = 10;
        this.minutes = 10;
        this.seconds = 10;
        daysOfWeek = new boolean[] {false, false, false, false, false, false, false}; //Sun to Sat
        state = STANDBY;
        vibration = false;
        ringtone = null;

    }

    /*
     * This constructor is annotated using @Ignore, because Room expects only
     * one constructor by default in an entity class.
     */


    @Ignore
    public Alarm(int id, String alarmName, int hour, int min, int sec, Ringtone ringtone){
        this.id = id;
        this.alarmName = alarmName;
        this.hourOfDay = hour;
        this.minutes = min;
        this.seconds = sec;
        this.ringtone = ringtone;
        daysOfWeek = new boolean[] {false, false, false, false, false, false, false};
        state = STANDBY;
        vibration = false;

        //this.alarmMusic = alarmMusic;
    }



    public void snooze (int minutes){

    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHours() {
        return hourOfDay;
    }

    public void setHours(int hours) {
        this.hourOfDay = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }


    public boolean[] getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(boolean[] daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }



    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }

    /*
    public MediaPlayer getAlarmMusic() {
        return alarmMusic;
    }

    public void setAlarmMusic(MediaPlayer alarmMusic) {
        this.alarmMusic = alarmMusic;
    }
*/
    public String toString(){
        String output = "";

        int hour = this.hourOfDay % 12;
        if (hour == 0)
            hour = 12;

        if (hour < 10){
            output += "0" + hour + ":";
        }
        else{
            output += hour + ":";
        }

        if (minutes < 10){
            output += "0" + minutes;
        }
        else{
            output += minutes;
        }

        if (this.hourOfDay < 12)
            output += " AM";
        else
            output += " PM";

        return output;
    }

}

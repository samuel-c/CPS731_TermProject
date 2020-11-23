package com.example.cps731_termproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cps731_termproject.utils.Alarm;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MainDao {

    //Insert
    @Insert(onConflict = REPLACE)
    void insert(Alarm alarm);

    //Delete
    @Delete
    void delete(Alarm alarm);

    //Delete all
    @Delete
    void deleteAll(List<Alarm> alarmList);

    /*
    //Update
    @Query("UPDATE alarm_table SET alarmName = :sText, hours = :sHours, minutes = :sMinutes, seconds = :sSeconds WHERE ID = :sID")
    void update(int sID, String sText, int sHours, int sMinutes, int sSeconds);
     */

    //Update
    @Query("UPDATE alarm_table SET alarmName = :sText WHERE ID = :sID")
    void update(int sID, String sText);

    //Update
    @Query("UPDATE alarm_table SET state = :sState WHERE ID = :sID")
    void updateState(int sID, int sState);

    //Update Time
    @Query("UPDATE alarm_table SET hourOfDay = :sHourOfDay, minutes = :sMinutes WHERE ID = :sID")
    void updateTime(int sID, int sHourOfDay, int sMinutes);

    //Update
    @Query("UPDATE alarm_table SET vibration = :sVibration WHERE ID = :sID")
    void updateVib(int sID, boolean sVibration);

    //Update
    @Query("UPDATE alarm_table SET daysOfWeek = :sDaysOfWeek WHERE ID = :sID")
    void updateDOW(int sID, String sDaysOfWeek);

    //Get all data
    @Query("SELECT * FROM alarm_table")
    List<Alarm> getAll();

    @Query("SELECT * FROM alarm_table WHERE alarmName = :sName AND hourOfDay = :sHour AND minutes = :sMinutes")
    Alarm getAlarmByName(String sName, int sHour, int sMinutes);

    @Query("SELECT * FROM alarm_table WHERE ID = :sID")
    Alarm getAlarm(int sID);
}

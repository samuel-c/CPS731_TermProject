package com.example.cps731_termproject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.cps731_termproject.utils.Alarm;

import java.util.List;

/**
 * Data Access Object (DAO) for an Alarm.
 * Each method performs a database operation, such as inserting or deleting a word,
 * running a DB query, or deleting all words.
 */

@Dao
public interface AlarmDao {

    @Insert
    void insert(Alarm alarm);

    @Query("DELETE FROM alarm_table")
    void deleteAll();

    @Delete
    void deleteWord(Alarm alarm);

    @Query("SELECT * from alarm_table LIMIT 1")
    Alarm[] getAnyAlarm();

    @Query("SELECT * from alarm_table ORDER BY alarmName ASC")
    LiveData<List<Alarm>> getAllWords();

    @Update
    void update(Alarm... alarm);

}

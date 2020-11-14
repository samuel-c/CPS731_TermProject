package com.example.cps731_termproject;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.cps731_termproject.utils.Alarm;
import com.example.cps731_termproject.utils.Converters;

@Database(entities = {Alarm.class}, version= 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class RoomDB extends RoomDatabase {

    // Database Instance
    private static RoomDB database;
    // Database Name
    private static String DATABASE_NAME = "database";

    public synchronized static RoomDB getInstance(Context context){
        if (database == null){
            // If database is null initialize
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        // Return Database
        return database;
    }

    // Create DAO
    public abstract MainDao mainDao();
}

package com.example.cps731_termproject.utils;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static boolean [] fromString(String value) {
        String [] test = value.split(",");
        boolean [] temp = new boolean[7];
        for (int i = 0 ; i < test.length; i ++){
            if(test[i].equals("true"))
                temp[i] = true;
            else
                temp[i] = false;
        }
        return value == null ? null : temp;
    }

    @TypeConverter
    public static String arrayToString(boolean [] array) {
        String temp = array[0] + "," + array[1] + "," + array[2] + "," + array[3] +"," + array[4] + "," + array[5]+ "," + array[6];

        return array == null ? null : temp;
    }
}

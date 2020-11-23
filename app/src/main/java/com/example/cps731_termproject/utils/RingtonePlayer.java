package com.example.cps731_termproject.utils;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;

public class RingtonePlayer {

    public static Ringtone ringtone;

    public static void RingtonePlayer(Context context){
        ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        ringtone.setLooping(true);

        ringtone.play();
    }

}

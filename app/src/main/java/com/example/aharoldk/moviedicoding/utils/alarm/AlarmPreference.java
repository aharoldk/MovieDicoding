package com.example.aharoldk.moviedicoding.utils.alarm;

import android.content.Context;
import android.content.SharedPreferences;


public class AlarmPreference {
    private final String PREF_NAME = "AlarmPreference";
    private final String KEY_REPEATING_TIME = "repeatingTime";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;

    public AlarmPreference(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public void setRepeatingTime(String time){
        editor.putString(KEY_REPEATING_TIME, time);
        editor.commit();
    }

    public String getRepeatingTime(){
        return mSharedPreferences.getString(KEY_REPEATING_TIME, null);
    }
}

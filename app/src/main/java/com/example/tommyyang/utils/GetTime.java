package com.example.tommyyang.utils;

import android.text.format.Time;

/**
 * Created by tommy.yang on 9/27/2016.
 */
public class GetTime {
    private static Time time;
    private static String timeString;
    public static String getTime() {
        time = new Time();
        time.setToNow();
        timeString = (time.month + 1) + "/" + time.monthDay + "/"
                + time.hour + "-" + time.minute + "-" + time.second;
        return timeString;
    }
}
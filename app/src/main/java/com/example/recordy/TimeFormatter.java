package com.example.recordy;

import android.util.TimeUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeFormatter {

    public String getTime(long actualValue) {
        Date nowDate = new Date();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(nowDate.getTime() - actualValue);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(nowDate.getTime() - actualValue);
        long hours = TimeUnit.MILLISECONDS.toHours(nowDate.getTime() - actualValue);
        long days = TimeUnit.MILLISECONDS.toDays(nowDate.getTime() - actualValue);

        if (seconds < 60) {
            return "just now";
        } else if (minutes == 1) {
            return "a minute ago";
        }else if (minutes < 60 && minutes > 1 ){
            return minutes + " minutes ago" ;
        } else if (hours == 1) {
            return "an hour ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else if (days == 1) {
            return "a day ago";
        } else {
            return days + " days ago";
        }
    }
}

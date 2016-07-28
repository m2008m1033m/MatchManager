package com.dxbcom.matchmanager.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;

import com.dxbcom.matchmanager.MatchManagerApplication;
import com.dxbcom.matchmanager.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mohammed on 3/4/16.
 */
public class MiscUtils {


    public static String getDurationFormatted(Date date) {
        if (date == null) return "N/A";
        int currentSeconds = (int) (Calendar.getInstance().getTimeInMillis() / 1000);
        int seconds = (int) (date.getTime() / 1000);

        seconds = currentSeconds - seconds;

        if (seconds < 60)
            return MatchManagerApplication.getContext().getString(seconds == 1 ? R.string.s_second_ago : R.string.s_seconds_ago, seconds);

        seconds = seconds / 60;
        if (seconds < 60)
            return MatchManagerApplication.getContext().getString(seconds == 1 ? R.string.s_minute_ago : R.string.s_minutes_ago, seconds);

        seconds = seconds / 60;
        if (seconds < 24)
            return MatchManagerApplication.getContext().getString(seconds == 1 ? R.string.s_hour_ago : R.string.s_hours_ago, seconds);

        seconds = seconds / 24;
        if (seconds < 30)
            return MatchManagerApplication.getContext().getString(seconds == 1 ? R.string.s_day_ago : R.string.s_days_ago, seconds);

        seconds = seconds / 30;
        if (seconds < 12)
            return MatchManagerApplication.getContext().getString(seconds == 1 ? R.string.s_month_ago : R.string.s_months_ago, seconds);

        seconds = seconds / 12;
        return MatchManagerApplication.getContext().getString(seconds == 1 ? R.string.s_year_ago : R.string.s_years_ago, seconds);

    }

    // Decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(String photoPath, int size) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(photoPath), null, o);

            // The new size we want to scale to

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= size ||
                    o.outHeight / scale / 2 >= size) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(photoPath), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertStringToHashTag(String str) {
        String[] bits = str.split(" ");
        String finalDescription = "";
        for (String bit : bits) {
            if (bit.trim().isEmpty()) continue;
            if (!bit.startsWith("#"))
                bit = "#" + bit;
            finalDescription += bit + " ";
        }

        return finalDescription;
    }

    public static String getDayOfWeek(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
        }
        return "";
    }

    public static String concatArray(String[] arr, String d) {
        if (arr == null) return "";
        String r = "";
        for (String elem : arr) {
            r += elem.trim() + d;
        }
        return r.substring(0, r.length() - d.length());
    }

    public static int convertDP2Pixel(int dp) {
        Resources r = MatchManagerApplication.getContext().getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}

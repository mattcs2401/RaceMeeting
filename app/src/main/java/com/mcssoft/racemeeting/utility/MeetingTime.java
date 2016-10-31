package com.mcssoft.racemeeting.utility;


import android.content.Context;
import android.preference.PreferenceManager;

import mcssoft.com.racemeeting3.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * utility class for general manipulation of time values (primarily for display).
 */
public class MeetingTime {

    private MeetingTime(Context context) {
        this.context = context;
        setTimeFormatFromPreferences();
    }

    /**
     * Get (initialise) an instance of MeetingTime.
     * @param context The current context.
     * @return An instance of MeetingTime.
     * @throws IllegalStateException if instance already initialised.
     */
    public static synchronized MeetingTime getInstance(Context context) {
        if(!instanceExists()) {
            instance = new MeetingTime(context);
            return instance;
        }
        throw new IllegalStateException(R.string.meeting_time_already_init + "");
    }

    /**
     * Get the current MeetingTime instance.
     * @return The current MeetingTime instance.
     * @throws IllegalStateException if instance is null.
     */
    public static synchronized MeetingTime getInstance() {
        if(instanceExists()) {
            return instance;
        }
        throw new IllegalStateException(R.string.meeting_time_not_init + "");
    }

    public static boolean instanceExists() {
        return instance != null ? true : false;
    }

    /**
     * Get the time as a string formatted HH:MM.
     * @param timeInMillis The time value in mSec.
     * @param meridiem Flag to indicate if 12HR time is appended with AM/PM.
     * @return The time based on the parameter timeInMillis value.
     */
    public String getTimeFromMillis(long timeInMillis, boolean meridiem) {

        String time;
        SimpleDateFormat sdFormat;

        calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(new Date(timeInMillis));

        setTimeFormatFromPreferences();

        if(timeFormat.equals(MeetingConstants.TIME_FORMAT_PREF_12HR)) {
            sdFormat = new SimpleDateFormat(MeetingConstants.TIME_FORMAT_12HR, Locale.getDefault());

//            if(meridiem) {
//                time = time.replace("am","AM").replace("pm","PM");
//            } else {
//                time = time.replace("AM","").replace("PM","");
//            }
        } else {
            sdFormat = new SimpleDateFormat(MeetingConstants.TIME_FORMAT_24HR, Locale.getDefault());
        }

        time = sdFormat.format(calendar.getTime());
        return time;
    }

    /**
     * Get the time in milli seconds based on the given hour and minute values.
     * @param time int[2] where; [0] == hour, [1] ==  minute (can be null).
     * @return The time in milli seconds, or null.
     */
    public long getMillisFromTimeComponent(int [] time) {
        if (time == null || time.length != 2) {
            return MeetingConstants.INIT_DEFAULT;
        }

        calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, time[0]);
        calendar.set(Calendar.MINUTE, time[1]);

        return calendar.getTimeInMillis();
    }

    /**
     * Get the current time in milli seconds. Internally sets the value for chaining toString().
     * @return The time in milli seconds.
     */
    public long getCurrentTimeInMillis() {
        return Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
    }

    /**
     * Get the HH (hour) and MM (minute) time components of the given time.
     * @param timeInMillis The time value in mSec.
     * @param component An identifier for the time component.
     * @return The time components; int[0] == hour or minute, and int[1] == minute or -1, or null
     *         if the parameter is unrecognised.
     */
    public int [] getTimeComponent(long timeInMillis, int component) {

        int [] time = new int[2];

        calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(new Date(timeInMillis));

        switch(component) {
            case MeetingConstants.TIME_COMPONENT_HOUR_MINUTE:
                time[0] = calendar.get(Calendar.HOUR_OF_DAY);
                time[1] = calendar.get(Calendar.MINUTE);
                break;
            case MeetingConstants.TIME_COMPONENT_HOUR:
                time[0] = calendar.get(Calendar.HOUR_OF_DAY);
                time[1] = MeetingConstants.INIT_DEFAULT;
                break;
            case MeetingConstants.TIME_COMPONENT_MINUTE:
                time[0] = calendar.get(Calendar.MINUTE);
                time[1] = MeetingConstants.INIT_DEFAULT;
                break;
            default:
                time = null;
        }
        return time;
    }

    public String getTimeFormatPrefKey() {
        String timeFormatPrefKey = null;
        if (timeFormat.equals(MeetingConstants.TIME_FORMAT_PREF_12HR)) {
            timeFormatPrefKey = MeetingConstants.TIME_FORMAT_PREF_12HR_KEY;
        } else if (timeFormat.equals(MeetingConstants.TIME_FORMAT_PREF_24HR)) {
            timeFormatPrefKey = MeetingConstants.TIME_FORMAT_PREF_24HR_KEY;
        }
        return timeFormatPrefKey;
    }

    /**
     * Get a time value in millis that is the current time minus (parameter).
     * @param minusTime A time value in milliseconds.
     * @return A time value that is the current time minus (parameter).
     */
    public long getTimeMinus(int minusTime) {
        calendar = Calendar.getInstance(Locale.getDefault());
        int minute = calendar.get(calendar.MINUTE);

        minute = minute - (minusTime / 1000);
        calendar.set(Calendar.MINUTE, minute);

        return calendar.getTimeInMillis();
    }

    public void destroy() {
        // Called in the ListingActivity.onDestroy().
        context = null;
        instance = null;
    }

    /**
     * Set the time format 12HR/24HR from the app's shared preferences.
     */
    private void setTimeFormatFromPreferences() {
        String formatKey = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(MeetingConstants.TIME_FORMAT_PREF_KEY, null);

        if(formatKey.equals(MeetingConstants.TIME_FORMAT_PREF_12HR_KEY)) {
            timeFormat = MeetingConstants.TIME_FORMAT_PREF_12HR;
        } else if(formatKey.equals(MeetingConstants.TIME_FORMAT_PREF_24HR_KEY)) {
            timeFormat = MeetingConstants.TIME_FORMAT_PREF_24HR;
        }

    }

    private String timeFormat;    // the current time format.
    private Calendar calendar;    // multi method general usage variable.
    private Context context;      // app context for shared preferences.

    private static volatile MeetingTime instance = null;
}


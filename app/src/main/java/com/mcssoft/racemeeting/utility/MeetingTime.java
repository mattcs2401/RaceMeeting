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
     */
    public static synchronized MeetingTime getInstance(Context context) {
        if(!instanceExists()) {
            instance = new MeetingTime(context);
        }
        return instance;
    }

    /**
     * Get the current MeetingTime instance.
     * @return The current MeetingTime instance.
     */
    public static synchronized MeetingTime getInstance() {
        return instance;
    }

    public static boolean instanceExists() {
        return instance != null ? true : false;
    }

    /**
     * Get the time as a string formatted HH:MM.
     * @param timeInMillis The time value in mSec.
     * @return The time based on the parameter timeInMillis value.
     */
    public String getFormattedTimeFromMillis(long timeInMillis) {

        String time;
        SimpleDateFormat sdFormat;

        calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(new Date(timeInMillis));

        setTimeFormatFromPreferences();

        if(timeFormat.equals(MeetingConstants.TIME_FORMAT_PREF_12HR)) {
            sdFormat = new SimpleDateFormat(MeetingConstants.TIME_FORMAT_12HR, Locale.getDefault());
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
     * Get the current time in milli seconds.
     * @return The time in milli seconds.
     */
    public long getTimeInMillis() {
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

        // TBA - possible logic flaw here if 'minute' ends up a minus value.
        minute = (minute - minusTime);

        calendar.set(Calendar.MINUTE, minute);

        return calendar.getTimeInMillis();
    }

    /**
     * Ensure instance values are made NULL.
     */
    public void destroy() {
        context = null;
        instance = null;
    }

    /**
     * Check if the current time is after the time given.
     * @param timeInMillis The given time.
     * @return True if current time is after the given time, else false.
     */
    public boolean isTimeAfter(long timeInMillis) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(new Date(timeInMillis));
        return Calendar.getInstance().after(calendar);
    }

    /**
     * Get a value that represents midnight (00:00 hours of the current day).
     * @return Time in mSec representing mignight.
     */
    public long getMidnight() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Check if the given time is a time from today.
     * @param timeInMillis The given time.
     * @return True if the given time is a time from today, else false.
     * Note: A return value of false means a time on a previous day.
     */
    public boolean isTimeToday(long timeInMillis) {
        Calendar today = Calendar.getInstance(Locale.getDefault());
        Calendar toCheck = Calendar.getInstance(Locale.getDefault());
        toCheck.setTime(new Date(timeInMillis));

        if(toCheck.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return true;
        }

        return false;
    }

    /**
     * Set the time format 12HR/24HR from the app's shared preferences.
     */
    private void setTimeFormatFromPreferences() {
        boolean format = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(MeetingConstants.TIME_FORMAT_PREF_KEY, false);

        if(format) {
            timeFormat = MeetingConstants.TIME_FORMAT_PREF_24HR;
        } else {
            timeFormat = MeetingConstants.TIME_FORMAT_PREF_12HR;
        }
    }

    private String timeFormat;    // the current time format.
    private Calendar calendar;    // multi method general usage variable.
    private Context context;      // app context for shared preferences.

    private static volatile MeetingTime instance = null;
}


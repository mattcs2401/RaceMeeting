package com.mcssoft.racemeeting.utility;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.Map;

import mcssoft.com.racemeeting.R;

/**
 * Utility class to get app SharedPreferences.
 */
public class MeetingPreferences {

    private MeetingPreferences(Context context) {
        this.context = context;
        getPreferences();
    }

    public static synchronized MeetingPreferences getInstance(Context context) {
        if(!instanceExists()) {
            instance = new MeetingPreferences(context);
        }
        return instance;
    }

    public static synchronized MeetingPreferences getInstance() {
        return instance;
    }

    public static boolean instanceExists() {
        return instance != null ? true : false;
    }

    /**
     * Get the details for the 'Meetings To Show' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public String[] meetingShowPref() {
       String[] prefVals = new String[2];
        prefVals[0] = getDefaultSharedPreferences()
                .getString(MeetingConstants.SHOW_MEETING_PREF_KEY, null);
        prefVals[1] = context.getResources()
                .getStringArray(R.array.meetingShowWhichVals)[Integer.parseInt(prefVals[0])];
        return prefVals;
    }

    /**
     * Get the details for the 'Meeting Time Format' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public boolean meetingTimeFormatPref() {
        return getDefaultSharedPreferences().getBoolean(MeetingConstants.TIME_FORMAT_PREF_KEY, false);
    }

    public String meetingDefaultRaceCodePref() {
        return getDefaultSharedPreferences()
                .getString(MeetingConstants.RACE_CODE_PREF_VAL_KEY, null);
    }

    public int meetingDefaultRaceCodeId() {
        return getDefaultSharedPreferences()
                .getInt(MeetingConstants.RACE_CODE_PREF_ID_KEY, MeetingConstants.INIT_DEFAULT);
    }

    /**
     * Get the details for the 'Meeting Past Race Time' preference.
     * @return True if checked.
     */
    public boolean meetingPastTimePref() {
        return getDefaultSharedPreferences()
                .getBoolean(MeetingConstants.TIME_PAST_PREF_KEY, false);
    }

    /**
     * Get the details for the 'Meeting Reminder Time' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public int meetingReminderTimePref() {
        return getDefaultSharedPreferences()
                .getInt(R.string.pref_reminder_key + "", MeetingConstants.INIT_DEFAULT);
    }

    public boolean meetingNotificationEnablePref() {
        return getDefaultSharedPreferences().getBoolean(MeetingConstants.NOTIFY_ENABLE_PREF_KEY, false);
    }

    public boolean meetingNotifySoundPref() {
        return getDefaultSharedPreferences().getBoolean(MeetingConstants.NOTIFY_SOUND_PREF_KEY, false);
    }

    public boolean meetingNotifyVibratePref() {
        return getDefaultSharedPreferences().getBoolean(MeetingConstants.NOTIFY_VIBRATE_PREF_KEY, false);
    }

    public int meetingNotifyVibrationsPref() {
        return getDefaultSharedPreferences().getInt(MeetingConstants.NOTIFY_PREF_KEY,
                MeetingConstants.NOTIFY_DEFAULT_PREF_VAL);
    }

    /**
     * Get a listing of all the preferences (as currently set).
     * @return The preference listing in a bundle.
     */
    public Bundle getAllPreferences() {
        return getPreferences();
    }

    /**
     * Utility to null out the context parameter. Called in the MainActivity.onDestroy().
     */
    public void destroy() {
        context = null;
        instance = null;
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private Bundle getPreferences() {

        Map<String,?> prefsMap = getDefaultSharedPreferences().getAll();

        if(prefsMap.isEmpty()) {
            // No SharedPreferences set yet. App has probably been uninstalled then re-installed
            // and/or cache and data cleared. Set the app preferences defaults.
            PreferenceManager.setDefaultValues(context, R.xml.meeting_preferences, false);
            prefsMap = getDefaultSharedPreferences().getAll();
        }

        Bundle prefsState = new Bundle();

        for (String key : prefsMap.keySet()) {
            Object obj = prefsMap.get(key);
            prefsState.putString(key, obj.toString());
        }

        return prefsState;
    }

    private Context context;
    private static volatile MeetingPreferences instance = null;
}

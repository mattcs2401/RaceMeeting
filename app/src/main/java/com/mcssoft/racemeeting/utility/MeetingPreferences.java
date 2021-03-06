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
     */
    public String meetingShowPref() {
        return getDefaultSharedPreferences()
                .getString(MeetingResources.getInstance()
                        .getString(R.string.race_show_meetings_pref_val_key), null);
    }

    /**
     * Get the details for the 'Meeting Time Format' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public boolean meetingTimeFormatPref() {
        return getDefaultSharedPreferences().getBoolean(MeetingResources.getInstance()
                .getString(R.string.time_format_pref_key), false);
    }

    public String meetingRaceCodePref() {
        return getDefaultSharedPreferences()
                .getString(MeetingResources.getInstance()
                        .getString(R.string.race_code_pref_val_key), null);
    }

    public int meetingRaceCodeId() {
        return getDefaultSharedPreferences()
                .getInt(MeetingResources.getInstance()
                        .getString(R.string.race_code_pref_id_key), R.integer.init_default);
    }

    /**
     * Get the details for the 'Meeting Past Race Time' preference.
     * @return True if checked.
     */
    public boolean meetingPastTimePref() {
        return getDefaultSharedPreferences()
                .getBoolean(MeetingResources.getInstance()
                        .getString(R.string.time_past_pref_key), false);
    }

    /**
     * Get the details for the 'Meeting Reminder Time' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public int meetingReminderTimePref() {
        return getDefaultSharedPreferences()
                .getInt(R.string.pref_reminder_key + "", R.integer.init_default);
    }

    public boolean meetingNotificationEnablePref() {
        return getDefaultSharedPreferences().getBoolean(MeetingResources.getInstance()
                .getString(R.string.notify_enable_pref_key), false);
    }

    public boolean meetingNotifySoundPref() {
        return getDefaultSharedPreferences().getBoolean(MeetingResources.getInstance()
                .getString(R.string.notify_sound_pref_key), false);
    }

    public boolean meetingNotifyVibratePref() {
        return getDefaultSharedPreferences().getBoolean(MeetingResources.getInstance()
                .getString(R.string.notify_vibrate_pref_key), false);
    }

    public int meetingNotifyVibrationsPref() {
        return getDefaultSharedPreferences().getInt(MeetingResources.getInstance()
                .getString(R.string.notify_pref_key),
                R.integer.notify_default_pref_val);
    }

    /**
     * Gets the id of the currently selected radio button of the race show preference.
     * @return The id of the currently selected radio button on preference save.
     */
    public int meetingRaceShowPref() {
        return getDefaultSharedPreferences()
                .getInt(MeetingResources.getInstance()
                        .getString(R.string.race_show_meetings_pref_id_key), R.integer.init_default);
    }

    /**
     * Gets the state of the 'Include date' checkbox on the race show preference 'Show all'.
     * @return True if was checked on preference save.
     */
    public boolean meetingRaceShowDatePref() {
        return getDefaultSharedPreferences()
                .getBoolean(MeetingResources.getInstance()
                        .getString(R.string.race_show_meetings_incl_date_key), false);
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

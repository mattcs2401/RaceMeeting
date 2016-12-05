package com.mcssoft.racemeeting.utility;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.Map;

import mcssoft.com.racemeeting3.R;

/**
 * Utility class to get/set app SharedPreferences.
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
     * Get the value of the 'Preference Notifications' slider.
     * @return True if checked.
     */
    public boolean meetingNotificationPref() {
        return getDefaultSharedPreferences()
                .getBoolean(MeetingConstants.MEETING_NOTIFICATIONS_KEY, false);
    }

    /**
     * Get the details for the 'Meetings To Show' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public String[] meetingShowPref() {
       String[] prefVals = new String[2];
        prefVals[0] = getDefaultSharedPreferences()
                .getString(MeetingConstants.MEETING_SHOW_KEY, null);
        prefVals[1] = context.getResources()
                .getStringArray(R.array.meetingShowWhichVals)[Integer.parseInt(prefVals[0])];
        return prefVals;
    }

    /**
     * Get the details for the 'Meeting Time Format' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public String[] meetingTimeFormatPref() {
        String[] prefVals = new String[2];
        prefVals[0] = getDefaultSharedPreferences()
                .getString(MeetingConstants.TIME_FORMAT_PREF_KEY, null);
        prefVals[1] = context.getResources()
                .getStringArray(R.array.meetingTimeFormatVals)[Integer.parseInt(prefVals[0])];
        return prefVals;
    }

    /**
     * Get the details for the 'Meeting Time Format' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public String[] meetingDefaultRaceCodePref() {
        String[] prefVals = new String[2];
        prefVals[0] = getDefaultSharedPreferences()
                .getString(MeetingConstants.DEFAULT_RACE_CODE_PREF_KEY, null);
        prefVals[1] = context.getResources()
                .getStringArray(R.array.meetingDefaultRaceCodeVals)[Integer.parseInt(prefVals[0])];
        return prefVals;
    }

    /**
     * Get the value of the 'Meeting Time Past & Prior Actions' checkbox.
     * @return True if checked.
     */
    public boolean meetingTimePastPrior() {
        return getDefaultSharedPreferences()
                .getBoolean(MeetingConstants.TIME_ACTIONS_PREF_KEY, false);
    }

    /**
     * Get the details for the 'Meeting Past Race Time' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public String[] meetingPastTimePref() {
        String[] prefVals = new String[2];
        prefVals[0] = getDefaultSharedPreferences()
                .getString(MeetingConstants.TIME_PAST_PREF_KEY, null);
        prefVals[1] = context.getResources()
                .getStringArray(R.array.meetingPastRaceTimeVals)[Integer.parseInt(prefVals[0])];
        return prefVals;
    }

    /**
     * Get the details for the 'Meeting Prior Reminder Time' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public int meetingPriorTimePref() {
        return getDefaultSharedPreferences()
                .getInt(R.string.pref_time_prior_key + "", MeetingConstants.INIT_DEFAULT);
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

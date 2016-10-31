package com.mcssoft.racemeeting.utility;


import android.content.Context;
import android.preference.PreferenceManager;

import java.util.Map;

import mcssoft.com.racemeeting3.R;

/**
 * Utility class to get/set app SharedPreferences.
 */
public class MeetingPreferences {

    private MeetingPreferences(Context context) {
        this.context = context;
        if(getAllPreferences().isEmpty()) {
            setDefaultValues();
        }
    }

    public static synchronized MeetingPreferences getInstance(Context context) {
        // Note: Instantiated in the ListingActivty.onCreate method.
        if(instance == null) {
            instance = new MeetingPreferences(context);
            return instance;
        }
        throw new IllegalStateException(R.string.meeting_pref_already_init + "");
    }

    public static synchronized MeetingPreferences getInstance() {
        if(instance != null) {
            return instance;
        }
        throw new IllegalStateException(R.string.meeting_pref_not_init + "");
    }

    public static boolean instanceExists() {
        return instance != null ? true : false;
    }

    /**
     * Get the value of the 'Preference Notifications' slider.
     * @return True if checked.
     */
    public boolean meetingNotificationPref() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(MeetingConstants.MEETING_NOTIFICATIONS_KEY, false);
    }

    /**
     * Get the details for the 'Meeting Time Format' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public String[] meetingTimeFormatPref() {
        prefVals = new String[2];
        prefVals[0] = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(MeetingConstants.TIME_FORMAT_PREF_KEY, null);
        prefVals[1] = context.getResources().getStringArray(R.array.meetingTimeFormatVals)[Integer.parseInt(prefVals[0])];
        return prefVals;
    }

    /**
     * Get the details for the 'Meeting Time Format' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public String[] meetingDefaultRaceCodePref() {
        prefVals = new String[2];
        prefVals[0] = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(MeetingConstants.DEFAULT_RACE_CODE_PREF_KEY, null);
        prefVals[1] = context.getResources().getStringArray(R.array.meetingDefaultRaceCodeVals)[Integer.parseInt(prefVals[0])];
        return prefVals;
    }

    /**
     * Get the value of the 'Meeting Time Past & Prior Actions' checkbox.
     * @return True if checked.
     */
    public boolean meetingTimePastPrior() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(MeetingConstants.TIME_ACTIONS_PREF_KEY, false);
    }

    /**
     * Get the details for the 'Meeting Past Race Time' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public String[] meetingPastTimePref() {
        prefVals = new String[2];
        prefVals[0] = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(MeetingConstants.TIME_PAST_PREF_KEY, null);
        prefVals[1] = context.getResources().getStringArray(R.array.meetingPastRaceTimeVals)[Integer.parseInt(prefVals[0])];
        return prefVals;
    }

    /**
     * Get the details for the 'Meeting Prior Reminder Time' preference.
     * @return [0] preference (array) value, [1] preference text.
     */
    public String[] meetingPriorTimePref() {
        prefVals = new String[2];
        prefVals[0] = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(MeetingConstants.TIME_PRIOR_PREF_KEY, null);
        prefVals[1] = context.getResources().getStringArray(R.array.meetingPriorReminderTimeVals)[Integer.parseInt(prefVals[0])];
        return prefVals;
    }

    /**
     * Get a listing of all the preferences (as currently set).
     * @return The preference list.
     */
    public Map<String,?> getAllPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context).getAll();
    }

    /**
     * Set the preference default values.
     */
    public void setDefaultValues() {
        PreferenceManager.setDefaultValues(context, R.xml.meeting_preferences, false);
    }

    public boolean checkPreferencesEmpty() {
        boolean isEmpty = false;
        if(getAllPreferences().isEmpty()) {
            isEmpty = true;
        }
        return isEmpty;
    }

    public void destroy() {
        // Called in the ListingActivity.onDestroy().
        context = null;
        instance = null;
    }

    String [] prefVals;      // general array value used where array is return type.
    private Context context;
    private static volatile MeetingPreferences instance = null;
}

package com.mcssoft.racemeeting.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingPreferences;

import mcssoft.com.racemeeting3.R;


public class PreferencesActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    protected void onCreate(Bundle savedState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedState);

        addPreferencesFromResource(R.xml.meeting_preferences);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        cbpUseDefaults = (CheckBoxPreference) getPreferenceManager().findPreference(MeetingConstants.TIME_ACTIONS_PREF_KEY);
        cbpUseDefaults.setOnPreferenceChangeListener(this);
        meetingPrefCat = (PreferenceCategory) getPreferenceManager().findPreference(MeetingConstants.TIME_ACTIONS_PREFCAT_KEY);
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();

        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
        boolean tdpk = sharedPrefs.getBoolean(MeetingConstants.TIME_ACTIONS_PREF_KEY, false);
        cbpUseDefaults.setChecked(tdpk);
        enableMeetingTimeDefaults(tdpk);
    }

    @Override
    public void onStop() {
        // Note: Can't put result Intent here as ListingFragment.onStart() is called before this.
        Log.d(LOG_TAG, "onStop");
        super.onStop();

        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Listeners">
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // 1. This is called when the 'Meeting Time Actions' CheckBoxPreference is pressed.
        // 2. This will fire 1st, then the onSharedPreferenceChanged().
        Log.d(LOG_TAG, "onPreferenceChange");
        if(preference.getKey().equals(MeetingConstants.TIME_ACTIONS_PREF_KEY)) {
            cbpUseDefaults.setChecked((boolean) newValue);
        }
        enableMeetingTimeDefaults(cbpUseDefaults.isChecked());
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        Log.d(LOG_TAG, "onSharedpreferenceChanged");
        // 1. If the user simply cancels (selects back), then this method isn't called.
        // 2. Method basically just shows notifications.

        if(MeetingPreferences.getInstance().meetingNotificationPref()) {

            String prefVal; // the preference value.

            if (key.equals(MeetingConstants.TIME_FORMAT_PREF_KEY)) {
                prefVal = MeetingPreferences.getInstance().meetingTimeFormatPref()[1];
                Toast.makeText(this, "Time format preference changed to " + prefVal + ".", Toast.LENGTH_SHORT).show();
            } else if (key.equals(MeetingConstants.DEFAULT_RACE_CODE_PREF_KEY)) {
                prefVal = MeetingPreferences.getInstance().meetingDefaultRaceCodePref()[1];
                Toast.makeText(this, "Default race code preference changed to " + prefVal + ".", Toast.LENGTH_SHORT).show();
            } else if (key.equals(MeetingConstants.TIME_ACTIONS_PREF_KEY)) {
                if (MeetingPreferences.getInstance().meetingTimePastPrior()) {
                    Toast.makeText(this, "Meeting past and prior time defaults enabled.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Meeting past and prior time defaults disabled.", Toast.LENGTH_SHORT).show();
                }
            } else if (key.equals(MeetingConstants.TIME_PAST_PREF_KEY)) {
                prefVal = MeetingPreferences.getInstance().meetingPastTimePref()[1];
                Toast.makeText(this, "Past race time preference changed to '" + prefVal + "'.", Toast.LENGTH_SHORT).show();
            } else if (key.equals(MeetingConstants.TIME_PRIOR_PREF_KEY)) {
                prefVal = MeetingPreferences.getInstance().meetingPriorTimePref()[1];
                Toast.makeText(this, "Prior reminder time preference changed to '" + prefVal + "'.", Toast.LENGTH_SHORT).show();
            }
        }
        setResult(MeetingConstants.PREFERENCES_CHANGED);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private void enableMeetingTimeDefaults(boolean enabled) {
        if(enabled) {
            meetingPrefCat.setEnabled(true);
        } else {
            meetingPrefCat.setEnabled(false);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
    private SharedPreferences sharedPrefs;
    private CheckBoxPreference cbpUseDefaults;
    private PreferenceCategory meetingPrefCat;

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>

}
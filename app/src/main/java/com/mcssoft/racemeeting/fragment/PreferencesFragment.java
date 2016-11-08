package com.mcssoft.racemeeting.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingPreferences;

import mcssoft.com.racemeeting3.R;

public class PreferencesFragment extends PreferenceFragment
        implements View.OnClickListener,
        Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public PreferencesFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.preferences_main, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.id_toolbar);
        toolbar.setTitle(R.string.app_name_prefs);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        toolbar.setNavigationOnClickListener(this);

        addPreferencesFromResource(R.xml.meeting_preferences);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        cbpUseDefaults = (CheckBoxPreference) getPreferenceManager().findPreference(MeetingConstants.TIME_ACTIONS_PREF_KEY);
        cbpUseDefaults.setOnPreferenceChangeListener(this);
        meetingPrefCat = (PreferenceCategory) getPreferenceManager().findPreference(MeetingConstants.TIME_ACTIONS_PREFCAT_KEY);

        return rootView;
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
                Toast.makeText(getActivity(), "Time format preference changed to " + prefVal + ".", Toast.LENGTH_SHORT).show();
            } else if (key.equals(MeetingConstants.DEFAULT_RACE_CODE_PREF_KEY)) {
                prefVal = MeetingPreferences.getInstance().meetingDefaultRaceCodePref()[1];
                Toast.makeText(getActivity(), "Default race code preference changed to " + prefVal + ".", Toast.LENGTH_SHORT).show();
            } else if (key.equals(MeetingConstants.TIME_ACTIONS_PREF_KEY)) {
                if (MeetingPreferences.getInstance().meetingTimePastPrior()) {
                    Toast.makeText(getActivity(), "Meeting past and prior time defaults enabled.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Meeting past and prior time defaults disabled.", Toast.LENGTH_SHORT).show();
                }
            } else if (key.equals(MeetingConstants.TIME_PAST_PREF_KEY)) {
                prefVal = MeetingPreferences.getInstance().meetingPastTimePref()[1];
                Toast.makeText(getActivity(), "Past race time preference changed to '" + prefVal + "'.", Toast.LENGTH_SHORT).show();
            } else if (key.equals(MeetingConstants.TIME_PRIOR_PREF_KEY)) {
                prefVal = MeetingPreferences.getInstance().meetingPriorTimePref()[1];
                Toast.makeText(getActivity(), "Prior reminder time preference changed to '" + prefVal + "'.", Toast.LENGTH_SHORT).show();
            }
        }
        getActivity().setResult(MeetingConstants.PREFERENCES_CHANGED);
    }

    @Override
    public void onClick(View v) {
        getActivity().finish();
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

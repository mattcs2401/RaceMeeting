package com.mcssoft.racemeeting.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingPreferences;

import mcssoft.com.racemeeting.R;

public class PreferencesFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.preferences_main, container, false);

//        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.id_toolbar);
//        toolbar.setTitle(R.string.app_name_prefs);
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
//        toolbar.setNavigationOnClickListener(this);

        addPreferencesFromResource(R.xml.meeting_preferences);
        sharedPrefs = MeetingPreferences.getInstance().getDefaultSharedPreferences();

        return rootView;
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();

        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Listeners">
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        Log.d(LOG_TAG, "onSharedpreferenceChanged");
        // If the user simply cancels (selects back), then this method isn't called.

        if(MeetingPreferences.getInstance().meetingNotificationPref()) {

            String prefVal; // the preference value.
            if(key.equals(MeetingConstants.MEETING_SHOW_KEY)) {
                prefVal = MeetingPreferences.getInstance().meetingShowPref()[1];
                Toast.makeText(getActivity(), "Meeting show preference changed to " + prefVal + ".", Toast.LENGTH_SHORT).show();
            } else if (key.equals(MeetingConstants.TIME_FORMAT_PREF_KEY)) {
                prefVal = String.valueOf(MeetingPreferences.getInstance().meetingTimeFormatPref());
                if(prefVal.equals("false")) {prefVal = "12HR";} else {prefVal = "24HR";};
                Toast.makeText(getActivity(), "Time format preference changed to " + prefVal + ".", Toast.LENGTH_SHORT).show();
            } else if (key.equals(MeetingConstants.DEFAULT_RACE_CODE_PREF_KEY)) {
                prefVal = MeetingPreferences.getInstance().meetingDefaultRaceCodePref()[1];
                Toast.makeText(getActivity(), "Default race code preference changed to " + prefVal + ".", Toast.LENGTH_SHORT).show();
            } else if (key.equals(MeetingConstants.TIME_PAST_PREF_KEY)) {
                prefVal = String.valueOf(MeetingPreferences.getInstance().meetingPastTimePref());
                if(prefVal.equals("false")) {prefVal = "no highlight.";} else {prefVal = "highlight.";};
                Toast.makeText(getActivity(), "Past race time preference changed to '" + prefVal + "'.", Toast.LENGTH_SHORT).show();
            } else if (key.equals(MeetingConstants.TIME_PRIOR_PREF_KEY)) {
                int val = MeetingPreferences.getInstance().meetingReminderTimePref();
                Toast.makeText(getActivity(), "Prior reminder time preference changed to '" + val + "'.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
    private SharedPreferences sharedPrefs;

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

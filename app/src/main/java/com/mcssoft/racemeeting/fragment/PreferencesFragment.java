package com.mcssoft.racemeeting.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mcssoft.com.racemeeting.R;

public class PreferencesFragment extends PreferenceFragment {

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.preferences_main, container, false);

//        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.id_toolbar);
//        toolbar.setTitle(R.string.app_name_prefs);
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
//        toolbar.setNavigationOnClickListener(this);

        addPreferencesFromResource(R.xml.meeting_preferences);

        return rootView;
    }

//    @Override
//    public void onStart() {
//        Log.d(LOG_TAG, "onStart");
//        super.onStart();
//    }

//    @Override
//    public void onStop() {
//        Log.d(LOG_TAG, "onStop");
//        super.onStop();
//    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
//    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

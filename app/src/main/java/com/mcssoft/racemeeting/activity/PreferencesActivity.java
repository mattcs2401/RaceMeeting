package com.mcssoft.racemeeting.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.mcssoft.racemeeting.fragment.PreferencesFragment;

import mcssoft.com.racemeeting3.R;

public class PreferencesActivity extends PreferenceActivity {

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    protected void onCreate(Bundle savedState) {
//        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedState);

        setContentView(R.layout.preferences_main);

        getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, new PreferencesFragment())
                            .commit();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
//    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}
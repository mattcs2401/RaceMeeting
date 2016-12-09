package com.mcssoft.racemeeting.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.mcssoft.racemeeting.fragment.PreferencesFragment;

import mcssoft.com.racemeeting3.R;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.preferences_main);
        getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, new PreferencesFragment())
                            .commit();
    }
}
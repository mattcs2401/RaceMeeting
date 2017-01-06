package com.mcssoft.racemeeting.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

import mcssoft.com.racemeeting.R;

public class RaceCodesDialog extends DialogPreference {

    public RaceCodesDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            setRaceCodePreference();
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }

    /*
      Basically just a check that this custom preference exists. App may have been un-installed
      and then re-installed.
     */
    private void checkRaceCodePreference() {

    }

    /*
      Get the race code preference.
     */
    private void getRaceCodePreference() {

    }

    /*
      Set the race code preference (basically a manual persist).
    */
    private void setRaceCodePreference() {
        SharedPreferences.Editor spe
                = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

    }

    private void initialise() {
        setPersistent(true);
        setDialogLayoutResource(R.layout.dialog_race_code);
        checkRaceCodePreference();
    }

    private int rbId;
    private RadioGroup radioGroup;
}

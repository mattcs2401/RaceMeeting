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

public class MeetingsToShowDialog extends DialogPreference {

    public MeetingsToShowDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            setMeetingPreference();
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
    private void checkMeetingPreference() {

    }

    /*
      Get the meeting(s) to show preference.
     */
    private void getMeetingPreference() {

    }

    /*
      Set the meeting(s) to show preference (basically a manual persist).
    */
    private void setMeetingPreference() {
        SharedPreferences.Editor spe
                = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

    }

    private void initialise() {
        setPersistent(true);
        setDialogLayoutResource(R.layout.dialog_meeting_show);
        checkMeetingPreference();
    }

    private int rbId;
    private RadioGroup radioGroup;

}

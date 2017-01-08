package com.mcssoft.racemeeting.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mcssoft.racemeeting.utility.MeetingConstants;

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
        radioGroup = (RadioGroup) view.findViewById(R.id.id_rg_race_codes);

    }

    /*
      Basically just a check that this custom preference exists. App may have been un-installed
      and then re-installed.
     */
    private void checkRaceCodePreference() {
        rbId = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getInt(MeetingConstants.RACE_CODE_PREF_ID_KEY, MeetingConstants.INIT_DEFAULT);

        if (rbId == MeetingConstants.INIT_DEFAULT) {

        }
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

        //RadioButton rb = radioGroup;
        rbId = radioGroup.getCheckedRadioButtonId();
        rbText  = ((RadioButton) radioGroup.getChildAt(rbId)).getText().toString();
        spe.putInt(MeetingConstants.RACE_CODE_PREF_ID_KEY, rbId);
        spe.putString(MeetingConstants.RACE_CODE_PREF_VAL_KEY, rbText);
    }

    private void initialise() {
        setPersistent(true);
        setDialogLayoutResource(R.layout.dialog_race_codes);
        checkRaceCodePreference();
    }

    private int rbId;
    private String rbText;
    private RadioGroup radioGroup;
}

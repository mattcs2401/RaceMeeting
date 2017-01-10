package com.mcssoft.racemeeting.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingPreferences;

import mcssoft.com.racemeeting.R;

public class RaceCodesDialog extends DialogPreference {

    public RaceCodesDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkRaceCodePreference();
        setDialogLayoutResource(R.layout.dialog_race_codes);
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
        rbId = MeetingPreferences.getInstance().getDefaultSharedPreferences()
                .getInt(MeetingConstants.RACE_CODE_PREF_ID_KEY, MeetingConstants.INIT_DEFAULT);
        radioButton = (RadioButton) radioGroup.findViewById(rbId);
        radioButton.setChecked(true);
    }

    /*
      Basically just a check that this custom preference exists. App may have been un-installed
      and then re-installed.
     */
    public void checkRaceCodePreference() {
        // Has to be PreferenceManager if 1st time app run.
        if(!(PreferenceManager.getDefaultSharedPreferences(getContext())
                .contains(MeetingConstants.RACE_CODE_PREF_VAL_KEY))) {

            // If the preference doesn't exist, set the default for this preference.
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.dialog_race_codes, null);
            RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.id_rg_race_codes);
            int count = radioGroup.getChildCount();

            for(int ndx = 0; ndx < count; ndx++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(ndx);
                String text = radioButton.getText().toString();
                if(text.equals(MeetingConstants.RACE_CODE_DEFAULT_VAL)) {
                    int id = radioButton.getId();
                    text = MeetingConstants.RACE_CODE_DEFAULT_VAL;

                    SharedPreferences.Editor spe =
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    spe.putInt(MeetingConstants.RACE_CODE_PREF_ID_KEY, id).apply();
                    spe.putString(MeetingConstants.RACE_CODE_PREF_VAL_KEY, text).apply();
                    break;
                }
            }
        }
    }

    /*
      Set the race code preference (basically a manual persist).
    */
    private void setRaceCodePreference() {
        rbId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) radioGroup.findViewById(rbId);
        newRbText = radioButton.getText().toString();

        String oldRbText = MeetingPreferences.getInstance().getDefaultSharedPreferences()
                .getString(MeetingConstants.RACE_CODE_PREF_VAL_KEY, null);

        if(oldRbText != newRbText) {
            // Only update if preference actually changed (i.e. a different one selected).
            SharedPreferences.Editor spe
                    = MeetingPreferences.getInstance().getDefaultSharedPreferences().edit();
            spe.putInt(MeetingConstants.RACE_CODE_PREF_ID_KEY, rbId).apply();
            spe.putString(MeetingConstants.RACE_CODE_PREF_VAL_KEY, newRbText).apply();
            notifyChanged();
        }
    }

    private int rbId;
    private String newRbText;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
}

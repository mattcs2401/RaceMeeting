package com.mcssoft.racemeeting.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingPreferences;

import mcssoft.com.racemeeting.R;

public class MeetingsToShowDialog extends DialogPreference implements RadioGroup.OnCheckedChangeListener {

    public MeetingsToShowDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkMeetingPreference();
        setDialogLayoutResource(R.layout.dialog_meeting_show);
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
        radioGroup = (RadioGroup) view.findViewById(R.id.id_rg_show_meetings);
        radioGroup.setOnCheckedChangeListener(this);
        int radioButtonId = MeetingPreferences.getInstance().getDefaultSharedPreferences()
                .getInt(MeetingConstants.RACE_SHOW_MEETINGS_PREF_ID_KEY, MeetingConstants.INIT_DEFAULT);

        RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
        radioButton.setChecked(true);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.id_cb_meeting_include_date);
        if(!(radioButton.getText().toString().equals(MeetingConstants.RACE_SHOW_MEETINGS_DEFAULT_VAL))) {
            // not equal to Show only today and is checked.
            checkBox.setEnabled(true);
        } else {
            checkBox.setEnabled(false);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        
    }

    /*
          Basically just a check that this custom preference exists. App may have been un-installed
          and then re-installed.
         */
    private void checkMeetingPreference() {
        // Has to be PreferenceManager if it's the 1st time the app is run.
        if(!(PreferenceManager.getDefaultSharedPreferences(getContext())
                .contains(MeetingConstants.RACE_SHOW_MEETINGS_PREF_VAL_KEY))) {
            // If the preference doesn't exist, set the default for this preference.

            SharedPreferences.Editor spe =
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.dialog_meeting_show, null);
            RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.id_rg_show_meetings);
            int count = radioGroup.getChildCount();

            for(int ndx = 0; ndx < count; ndx++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(ndx);
                String text = radioButton.getText().toString();
                if(text.equals(MeetingConstants.RACE_SHOW_MEETINGS_DEFAULT_VAL)) {
                    int radioButtonId = radioButton.getId();
                    spe.putInt(MeetingConstants.RACE_SHOW_MEETINGS_PREF_ID_KEY, radioButtonId).apply();
                    spe.putString(MeetingConstants.RACE_SHOW_MEETINGS_PREF_VAL_KEY, text).apply();
                    // no need to keep going.
                    break;
                }
            }

            spe.putBoolean(MeetingConstants.RACE_SHOW_MEETINGS_INCL_DATE_KEY,
                    MeetingConstants.RACE_SHOW_MEETINGS_INCL_DATE_DEFAULT_VAL);
        }
    }

    /*
      Set the meeting(s) to show preference (basically a manual persist).
    */
    private void setMeetingPreference() {
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
        String newRbText = radioButton.getText().toString();

        String oldRbText = MeetingPreferences.getInstance().getDefaultSharedPreferences()
                .getString(MeetingConstants.RACE_SHOW_MEETINGS_PREF_VAL_KEY, null);

        if(oldRbText != newRbText) {
            // Only update if preference actually changed (i.e. a different one selected).
            SharedPreferences.Editor spe
                    = MeetingPreferences.getInstance().getDefaultSharedPreferences().edit();
            spe.putInt(MeetingConstants.RACE_SHOW_MEETINGS_PREF_ID_KEY, radioButtonId).apply();
            spe.putString(MeetingConstants.RACE_SHOW_MEETINGS_PREF_VAL_KEY, newRbText).apply();
            notifyChanged();
        }
    }

    private RadioGroup radioGroup;

}

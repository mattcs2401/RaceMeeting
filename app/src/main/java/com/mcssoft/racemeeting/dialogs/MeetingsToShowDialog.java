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

public class MeetingsToShowDialog extends DialogPreference
        implements View.OnClickListener {

    public MeetingsToShowDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkMeetingPreference();
        setDialogLayoutResource(R.layout.dialog_meeting_show);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            saveMeetingPreference();
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        radioGroup = (RadioGroup) view.findViewById(R.id.id_rg_show_meetings);
        rbShowAll = (RadioButton) view.findViewById(R.id.id_rb_meeting_show_all);
        rbShowToday = (RadioButton) view.findViewById(R.id.id_rb_meeting_show_today);
        checkBox = (CheckBox) view.findViewById(R.id.id_cb_meeting_include_date);

        rbShowAll.setOnClickListener(this);
        rbShowToday.setOnClickListener(this);
        checkBox.setOnClickListener(this);

        radioButtonId = MeetingPreferences.getInstance().meetingRaceShowPref();

        if(rbShowAll.getId() == radioButtonId) {
            rbShowAll.setChecked(true);
            checkBox.setEnabled(true);
        } else {
            rbShowToday.setChecked(true);
            checkBox.setEnabled(false);
        }
        // Checkbox may currently be disabled, but previously was (enabled and) checked.
        if(MeetingPreferences.getInstance().meetingRaceShowDatePref()) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.id_rb_meeting_show_all:
                checkBox.setEnabled(true);
                break;
            case R.id.id_rb_meeting_show_today:
                checkBox.setEnabled(false);
                break;
        }
    }

    /*
      Set the meeting(s) to show preference (basically a manual persist).
    */
    private void saveMeetingPreference() {
        boolean notifyChange = false;
        radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);

        String newRbText = radioButton.getText().toString();
        String oldRbText = MeetingPreferences.getInstance().getDefaultSharedPreferences()
                .getString(MeetingConstants.RACE_SHOW_MEETINGS_PREF_VAL_KEY, null);

        SharedPreferences.Editor spe
                = MeetingPreferences.getInstance().getDefaultSharedPreferences().edit();

        if(!(oldRbText.equals(newRbText))) {
            // Only update if preference actually changed (i.e. a different one selected).
            spe.putInt(MeetingConstants.RACE_SHOW_MEETINGS_PREF_ID_KEY, radioButtonId).apply();
            spe.putString(MeetingConstants.RACE_SHOW_MEETINGS_PREF_VAL_KEY, newRbText).apply();
            notifyChange = true;
        }

        boolean cbOldVal = MeetingPreferences.getInstance().getDefaultSharedPreferences()
                .getBoolean(MeetingConstants.RACE_SHOW_MEETINGS_INCL_DATE_KEY, false);
        boolean cbNewVal = checkBox.isChecked();

        if(cbOldVal != cbNewVal) {
            spe.putBoolean(MeetingConstants.RACE_SHOW_MEETINGS_INCL_DATE_KEY, cbNewVal).apply();
            notifyChange = true;
        }

        if(notifyChange) { notifyChanged(); }
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
            // include the checkbox.
            spe.putBoolean(MeetingConstants.RACE_SHOW_MEETINGS_INCL_DATE_KEY,
                    MeetingConstants.RACE_SHOW_MEETINGS_INCL_DATE_DEFAULT_VAL);
        }
    }

    private CheckBox checkBox;         // the 'Include date' checkbox.
    private RadioGroup radioGroup;     //
    private RadioButton rbShowAll;     // the 'Show all' radio button.
    private RadioButton rbShowToday;   // the 'Show only today' radio button.
    private int radioButtonId;         // id of currently selected radio button.
}

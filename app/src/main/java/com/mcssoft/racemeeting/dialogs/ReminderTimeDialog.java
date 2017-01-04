package com.mcssoft.racemeeting.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.mcssoft.racemeeting.utility.MeetingConstants;

import mcssoft.com.racemeeting.R;

public class ReminderTimeDialog extends DialogPreference
    implements NumberPicker.OnValueChangeListener {

    public ReminderTimeDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        numberPicker = (NumberPicker) view.findViewById(R.id.id_prior_time_numberPicker);
        tvMinutes = (TextView) view.findViewById(R.id.tvMinutes);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setValue(npPrefVal);
        setLabel(npPrefVal);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            persistInt(numberPicker.getValue());
        }
    }

    @Override
    public void onValueChange(NumberPicker np, int oldVal, int newVal) {
        setLabel(newVal);
    }

    private void initialise(Context context) {
        setPersistent(true);
        setDialogLayoutResource(R.layout.dialog_prior_time);

        npPrefVal = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(MeetingConstants.REMINDER_PREF_KEY, MeetingConstants.INIT_DEFAULT);

        if(npPrefVal == MeetingConstants.INIT_DEFAULT) {
            npPrefVal = MeetingConstants.REMINDER_PREF_DEFAULT;
            SharedPreferences.Editor spe =
                    PreferenceManager.getDefaultSharedPreferences(context).edit();
            spe.putInt(MeetingConstants.REMINDER_PREF_KEY, npPrefVal).apply();
        }
    }

    private void setLabel(int value) {
        if(value == 0) {
            tvMinutes.setText("No reminder");
        } else if(value == 1) {
            tvMinutes.setText("minute");
        } else {
            tvMinutes.setText("minutes");
        }
    }

    private int npPrefVal;             // current value of the preference.
    private TextView tvMinutes;        // a textview to display 'No reminder','minute' or 'minutes'.
    private NumberPicker numberPicker; // this.
}

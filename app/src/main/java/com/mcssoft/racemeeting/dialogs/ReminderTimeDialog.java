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
        numberPicker.setValue(prefVal);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            persistInt(numberPicker.getValue());
        }
    }

    @Override
    public void onValueChange(NumberPicker np, int oldVal, int newVal) {

        if(newVal == 0) {
            tvMinutes.setText("No reminder");
        } else if(newVal == 1) {
            tvMinutes.setText("minute");
        } else {
            tvMinutes.setText("minutes");
        }
    }

    private void initialise(Context context) {
        setPersistent(true);
        setDialogLayoutResource(R.layout.dialog_prior_time);

        prefVal = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(MeetingConstants.TIME_PRIOR_PREF_KEY, MeetingConstants.INIT_DEFAULT);

        if(prefVal == MeetingConstants.INIT_DEFAULT) {
            prefVal = MeetingConstants.TIME_PRIOR_PREF_DEFAULT;
            SharedPreferences.Editor spe =
                    PreferenceManager.getDefaultSharedPreferences(context).edit();
            spe.putInt(MeetingConstants.TIME_PRIOR_PREF_KEY, prefVal).apply();
        }
    }

    private int prefVal;               // current value of the preference.
    private TextView tvMinutes;        // a textview to display 'No reminder','minute' or 'minutes'.
    private NumberPicker numberPicker; // this.
}

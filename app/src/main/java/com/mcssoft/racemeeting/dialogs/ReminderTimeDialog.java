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

import com.mcssoft.racemeeting.utility.MeetingResources;

import mcssoft.com.racemeeting.R;

public class ReminderTimeDialog extends DialogPreference
    implements NumberPicker.OnValueChangeListener {

    public ReminderTimeDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        numberPicker = (NumberPicker) view.findViewById(R.id.id_prior_time_numberPicker);
        tvMinutes = (TextView) view.findViewById(R.id.tvMinutes);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setMinValue(MeetingResources.getInstance()
                .getInteger(R.integer.reminder_min_value));
        numberPicker.setMaxValue(MeetingResources.getInstance()
                .getInteger(R.integer.reminder_max_value));
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

    private void initialise() {
        setPersistent(true);
        setDialogLayoutResource(R.layout.dialog_prior_time);
        checkReminderPreference();
    }

    /*
      Basically just a check that this custom preference exists. App may have been un-installed
      and then re-installed.
     */
    private void checkReminderPreference() {
        int init_default = MeetingResources.getInstance().getInteger(R.integer.init_default);
        npPrefVal = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getInt(MeetingResources.getInstance()
                        .getString(R.string.reminder_pref_key), init_default);

        if(npPrefVal == init_default) {
            npPrefVal = MeetingResources.getInstance().getInteger(R.integer.reminder_min_value);  // 0 or no reminder.
            SharedPreferences.Editor spe =
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            spe.putInt(MeetingResources.getInstance()
                    .getString(R.string.reminder_pref_key), npPrefVal).apply();
        }
    }

    private void setLabel(int value) {
        String[] reminder_labels = MeetingResources.getInstance()
                .getStringArray(R.array.reminder_pref_labels);
        if(value == 0) {
            tvMinutes.setText(reminder_labels[0]);
        } else if(value == 1) {
            tvMinutes.setText(reminder_labels[1]);
        } else {
            tvMinutes.setText(reminder_labels[2]);
        }
    }

    private int npPrefVal;             // current value of the preference.
    private TextView tvMinutes;        // a textview to display 'No reminder','minute' or 'minutes'.
    private NumberPicker numberPicker; // this.
}

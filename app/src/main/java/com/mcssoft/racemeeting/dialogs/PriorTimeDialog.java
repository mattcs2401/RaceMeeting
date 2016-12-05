package com.mcssoft.racemeeting.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import com.mcssoft.racemeeting.utility.MeetingConstants;

import mcssoft.com.racemeeting3.R;

public class PriorTimeDialog extends DialogPreference {

    public PriorTimeDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        numberPicker = (NumberPicker) view.findViewById(R.id.id_prior_time_numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);
        numberPicker.setValue(prefVal);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            persistInt(numberPicker.getValue());
        }
    }

    private void initialise(Context context) {
        setPersistent(true);
        setDialogLayoutResource(R.layout.dialog_prior_time);
        setDialogMessage("A value of 0 means no reminder,");

        prefVal = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(MeetingConstants.TIME_PRIOR_PREF_KEY, MeetingConstants.INIT_DEFAULT);

        if(prefVal == MeetingConstants.INIT_DEFAULT) {
            prefVal = MeetingConstants.TIME_PRIOR_PREF_DEFAULT;
            SharedPreferences.Editor spe =
                    PreferenceManager.getDefaultSharedPreferences(context).edit();
            spe.putInt(MeetingConstants.TIME_PRIOR_PREF_KEY, prefVal).apply();
        }
    }

    private int prefVal;
    private NumberPicker numberPicker;
}

package com.mcssoft.racemeeting.dialogs;

import android.content.Context;
import android.content.DialogInterface;
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
        this.context = context;

        setPersistent(true);
        setDialogLayoutResource(R.layout.dialog_prior_time);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        numberPicker = (NumberPicker) view.findViewById(R.id.id_prior_time_numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);

        int value = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(MeetingConstants.TIME_PRIOR_PREF_KEY , 0);
        numberPicker.setValue(value);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            int value = numberPicker.getValue();
            persistInt(value);
        }
    }

    private Context context;
    private NumberPicker numberPicker;
}

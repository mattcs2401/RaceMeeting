package com.mcssoft.racemeeting.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.app.DialogFragment;

import com.mcssoft.racemeeting.utility.MeetingConstants;

public class TimePickDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        boolean is24Hour = false;
        Bundle args = getArguments();

        if(args.getString(MeetingConstants.TIME_FORMAT).equals(MeetingConstants.TIME_FORMAT_PREF_24HR_KEY)) {
            is24Hour = true;
        }

        return new android.app.TimePickerDialog(
                getActivity(),
                (OnTimeSetListener) getFragmentManager().findFragmentByTag(MeetingConstants.DEFAULT_EDIT_FRAGMENT_TAG),
                args.getInt(MeetingConstants.HOUR),
                args.getInt(MeetingConstants.MINS),
                is24Hour);
    }

}

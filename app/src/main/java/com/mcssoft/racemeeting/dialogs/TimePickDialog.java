package com.mcssoft.racemeeting.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.app.DialogFragment;

import com.mcssoft.racemeeting.utility.MeetingResources;

import mcssoft.com.racemeeting.R;

public class TimePickDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        boolean is24Hour = false;
        Bundle args = getArguments();

        if(args.getString(MeetingResources.getInstance().getString(R.string.time_format))
                .equals(MeetingResources.getInstance()
                        .getString(R.string.time_format_pref_24hr_key))) {
            is24Hour = true;
        }

        return new android.app.TimePickerDialog(
                getActivity(),
                (OnTimeSetListener) getFragmentManager().findFragmentByTag(MeetingResources
                        .getInstance().getString(R.string.edit_fragment_tag)),
                args.getInt(MeetingResources.getInstance().getString(R.string.hour)),
                args.getInt(MeetingResources.getInstance().getString(R.string.mins)),
                is24Hour);
    }

}

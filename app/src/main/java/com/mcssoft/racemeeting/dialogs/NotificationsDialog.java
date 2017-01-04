package com.mcssoft.racemeeting.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.mcssoft.racemeeting.utility.MeetingConstants;

import java.util.Map;

import mcssoft.com.racemeeting.R;

public class NotificationsDialog extends DialogPreference
    implements NumberPicker.OnValueChangeListener {

    public NotificationsDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        soundPref = (Switch) view.findViewById(R.id.id_swDefaultSound);
        vibratePref = (Switch) view.findViewById(R.id.id_swVibrate);
        numberPicker = (NumberPicker) view.findViewById(R.id.id_vibrate_numberPicker);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(3);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setValue(npPrefVal);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            persistInt(numberPicker.getValue());
            persistBoolean(soundPref.isChecked());
            persistBoolean(vibratePref.isChecked());
        }
    }

    @Override
    public void onValueChange(NumberPicker np, int oldVal, int newVal) {
        npPrefVal = newVal;
    }

    private void initialise(Context context) {
        setPersistent(true);
        setDialogLayoutResource(R.layout.dialog_notifications);

        Map<String,?> prefsMap = PreferenceManager.getDefaultSharedPreferences(context).getAll();

        if(!prefsMap.containsKey(MeetingConstants.NOTIFY_PREF_KEY)) {
            npPrefVal = MeetingConstants.NOTIFY_PREF_DEFAULT;
            SharedPreferences.Editor spe =
                    PreferenceManager.getDefaultSharedPreferences(context).edit();
            spe.putInt(MeetingConstants.NOTIFY_PREF_KEY, npPrefVal).apply();
        }
        if(!prefsMap.containsKey(MeetingConstants.NOTIFY_SOUND_PREF_KEY)) {
            String bp = "";
        }
        if(!prefsMap.containsKey(MeetingConstants.NOTIFY_VIBRATE_PREF_KEY)) {
            String bp = "";
        }
    }

    private int npPrefVal;
    private Switch soundPref;
    private Switch vibratePref;
    private boolean soundPrefVal;
    private boolean vibratePrefVal;
    private NumberPicker numberPicker;
}

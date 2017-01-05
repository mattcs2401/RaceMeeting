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
        initialise();
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        numberPicker = (NumberPicker) view.findViewById(R.id.id_numberPicker_vibrate);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setMinValue(MeetingConstants.NOTIFY_VIBRATE_MIN_VAL);
        numberPicker.setMaxValue(MeetingConstants.NOTIFY_VIBRATE_MAX_VAL);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setValue(npPrefVal);

        if(vibratePrefVal) {
            numberPicker.setEnabled(true);
        } else {
            numberPicker.setEnabled(false);
        }

        soundPref = (Switch) view.findViewById(R.id.id_switch_defaultSound);
        if(soundPrefVal) {
            soundPref.setChecked(true);
        } else {
            soundPref.setChecked(false);
        }

        vibratePref = (Switch) view.findViewById(R.id.id_switch_vibrate);
        if(vibratePrefVal) {
            vibratePref.setChecked(true);
        } else {
            vibratePref.setChecked(false);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            setNotifyPreferences();
        }
    }

    @Override
    public void onValueChange(NumberPicker np, int oldVal, int newVal) {
        npPrefVal = newVal;
    }

    /*
      Set initial values.
     */
    private void initialise() {
        setDialogLayoutResource(R.layout.dialog_notifications);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        checkNotifyPreferences();
        getNotifyPreferences();
    }

    /*
      Basically just a check that these custom preferences exist. App may have been un-installed
      and then re-installed.
     */
    private void checkNotifyPreferences() {
        Map<String,?> prefsMap = sharedPreferences.getAll();
        SharedPreferences.Editor spe = sharedPreferences.edit();

        if(!prefsMap.containsKey(MeetingConstants.NOTIFY_SOUND_PREF_KEY)) {
            soundPrefVal = MeetingConstants.NOTIFY_SOUND_DEFAULT_PREF_VAL;
            spe.putBoolean(MeetingConstants.NOTIFY_SOUND_PREF_KEY, soundPrefVal).apply();
        }
        if(!prefsMap.containsKey(MeetingConstants.NOTIFY_VIBRATE_PREF_KEY)) {
            vibratePrefVal = MeetingConstants.NOTIFY_VIBRATE_DEFAULT_PREF_VAL;
            spe.putBoolean(MeetingConstants.NOTIFY_VIBRATE_PREF_KEY, vibratePrefVal).apply();
        }
        if(!prefsMap.containsKey(MeetingConstants.NOTIFY_PREF_KEY)) {
            npPrefVal = MeetingConstants.NOTIFY_DEFAULT_PREF_VAL;
            spe.putInt(MeetingConstants.NOTIFY_PREF_KEY, npPrefVal).apply();
        }
    }

    /*
      Set the notification preferences (basically a manual persist).
     */
    private void setNotifyPreferences() {
        SharedPreferences.Editor spe = sharedPreferences.edit();
        spe.putBoolean(MeetingConstants.NOTIFY_SOUND_PREF_KEY, soundPref.isChecked()).apply();
        spe.putBoolean(MeetingConstants.NOTIFY_VIBRATE_PREF_KEY, vibratePref.isChecked()).apply();
        spe.putInt(MeetingConstants.NOTIFY_PREF_KEY, npPrefVal).apply();
    }

    /*
      Get the notification preferences.
     */
    private void getNotifyPreferences() {
        soundPrefVal = sharedPreferences.getBoolean(MeetingConstants.NOTIFY_SOUND_PREF_KEY, false);
        vibratePrefVal = sharedPreferences.getBoolean(MeetingConstants.NOTIFY_VIBRATE_PREF_KEY, false);
        npPrefVal = sharedPreferences.getInt(MeetingConstants.NOTIFY_PREF_KEY,
                MeetingConstants.NOTIFY_DEFAULT_PREF_VAL);
    }

    private int npPrefVal;
    private Switch soundPref;
    private Switch vibratePref;
    private boolean soundPrefVal;
    private boolean vibratePrefVal;
    private NumberPicker numberPicker;
    private SharedPreferences sharedPreferences;
}

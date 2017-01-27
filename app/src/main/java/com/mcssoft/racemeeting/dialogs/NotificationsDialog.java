package com.mcssoft.racemeeting.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.mcssoft.racemeeting.utility.MeetingConstants;

import java.util.Map;

import mcssoft.com.racemeeting.R;

public class NotificationsDialog extends DialogPreference
    implements NumberPicker.OnValueChangeListener,
        CompoundButton.OnCheckedChangeListener {

    public NotificationsDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        numberPicker = (NumberPicker) view.findViewById(R.id.id_numberPicker_vibrate);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setMinValue(R.integer.notify_vibrate_min_val);
        numberPicker.setMaxValue(R.integer.notify_vibrate_max_val);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setValue(npPrefVal);

        soundPref = (Switch) view.findViewById(R.id.id_switch_defaultSound);
        if(soundPrefVal) {
            soundPref.setChecked(true);
        } else {
            soundPref.setChecked(false);
        }

        vibratePref = (Switch) view.findViewById(R.id.id_switch_vibrate);
        vibratePref.setOnCheckedChangeListener(this);
        if(vibratePrefVal) {
            vibratePref.setChecked(true);
            numberPicker.setEnabled(true);
        } else {
            vibratePref.setChecked(false);
            numberPicker.setEnabled(false);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if((buttonView.getId() == R.id.id_switch_vibrate) && isChecked) {
            numberPicker.setEnabled(true);
        } else {
            numberPicker.setEnabled(false);
        }
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
            npPrefVal = R.integer.notify_default_pref_val;
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
                R.integer.notify_default_pref_val);
    }

    private int npPrefVal;
    private Switch soundPref;
    private Switch vibratePref;
    private boolean soundPrefVal;
    private boolean vibratePrefVal;
    private NumberPicker numberPicker;
    private SharedPreferences sharedPreferences;
}

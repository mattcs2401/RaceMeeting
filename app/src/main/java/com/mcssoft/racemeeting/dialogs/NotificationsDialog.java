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
import com.mcssoft.racemeeting.utility.MeetingResources;

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

        numberPicker.setMinValue(MeetingResources.getInstance()
                .getInteger(R.integer.notify_vibrate_min_val));
        numberPicker.setMaxValue(MeetingResources.getInstance()
                .getInteger(R.integer.notify_vibrate_max_val));
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

        if(!prefsMap.containsKey(MeetingResources.getInstance()
                .getString(R.string.notify_sound_pref_key))) {
            soundPrefVal = MeetingResources.getInstance()
                    .getBoolean(R.bool.notify_sound_default_pref_val);
            spe.putBoolean(MeetingResources.getInstance()
                    .getString(R.string.notify_sound_pref_key), soundPrefVal).apply();
        }
        if(!prefsMap.containsKey(MeetingResources.getInstance().getString(R.string.notify_vibrate_pref_key))) {
            vibratePrefVal = MeetingResources.getInstance()
                    .getBoolean(R.bool.notify_vibrate_default_pref_val);
            spe.putBoolean(MeetingResources.getInstance()
                    .getString(R.string.notify_vibrate_pref_key), vibratePrefVal).apply();
        }
        if(!prefsMap.containsKey(MeetingResources.getInstance().getString(R.string.notify_pref_key))) {
            npPrefVal = MeetingResources.getInstance().getInteger(R.integer.notify_default_pref_val);
            spe.putInt(MeetingResources.getInstance()
                    .getString(R.string.notify_pref_key), npPrefVal).apply();
        }
    }

    /*
      Set the notification preferences (basically a manual persist).
     */
    private void setNotifyPreferences() {
        SharedPreferences.Editor spe = sharedPreferences.edit();
        spe.putBoolean(MeetingResources.getInstance()
                .getString(R.string.notify_sound_pref_key), soundPref.isChecked()).apply();
        spe.putBoolean(MeetingResources.getInstance()
                .getString(R.string.notify_vibrate_pref_key), vibratePref.isChecked()).apply();
        spe.putInt(MeetingResources.getInstance()
                .getString(R.string.notify_pref_key), npPrefVal).apply();
    }

    /*
      Get the notification preferences.
     */
    private void getNotifyPreferences() {
        soundPrefVal = sharedPreferences.getBoolean(MeetingResources.getInstance()
                .getString(R.string.notify_sound_pref_key), false);
        vibratePrefVal = sharedPreferences.getBoolean(MeetingResources.getInstance()
                .getString(R.string.notify_vibrate_pref_key), false);
        npPrefVal = sharedPreferences.getInt(MeetingResources.getInstance()
                .getString(R.string.notify_pref_key),
                MeetingResources.getInstance().getInteger(R.integer.notify_default_pref_val));
    }

    private int npPrefVal;
    private Switch soundPref;
    private Switch vibratePref;
    private boolean soundPrefVal;
    private boolean vibratePrefVal;
    private NumberPicker numberPicker;
    private SharedPreferences sharedPreferences;
}

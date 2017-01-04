package com.mcssoft.racemeeting.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Switch;

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
        soundPrefVal = (Switch) view.findViewById(R.id.id_swDefaultSound);
        vibratePrefVal = (Switch) view.findViewById(R.id.id_swVibrate);
        numberPicker = (NumberPicker) view.findViewById(R.id.id_vibrate_numberPicker);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setValue(npPrefVal);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            persistInt(numberPicker.getValue());
            //persistBoolean(soundPrefVal.get)
        }
    }

    @Override
    public void onValueChange(NumberPicker np, int oldVal, int newVal) {
        npPrefVal = newVal;
    }

    private void initialise() {
        setPersistent(true);
        setDialogLayoutResource(R.layout.dialog_notifications);

    }

    private int npPrefVal;
    private Switch soundPrefVal;
    private Switch vibratePrefVal;
    private NumberPicker numberPicker;
}

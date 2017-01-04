package com.mcssoft.racemeeting.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import mcssoft.com.racemeeting.R;

public class NotificationsDialog extends DialogPreference {

    public NotificationsDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
//            persistInt(numberPicker.getValue());
        }
    }

    private void initialise() {
        setPersistent(true);
        setDialogLayoutResource(R.layout.dialog_notifications);

    }
}

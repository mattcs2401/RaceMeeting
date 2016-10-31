package com.mcssoft.racemeeting.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MeetingEditText extends EditText {

    public MeetingEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        // If the user enters text but then presses the back button, clear the text fieeld. We want
        // the user to explicity use the done button on the soft keyboard.
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(this.getEditableText().length() > -1) {

                // user has pressed Back key so hide the keyboard.
                InputMethodManager mgr = (InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);

                // clear the text field.
                this.getEditableText().clear();
                return true;
            }
        }
        return false;
    }
}
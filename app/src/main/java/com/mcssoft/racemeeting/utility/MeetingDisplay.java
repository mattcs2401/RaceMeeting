package com.mcssoft.racemeeting.utility;

import android.content.Context;

import mcssoft.com.racemeeting3.R;

public class MeetingDisplay {

    private MeetingDisplay(Context context) {
        this.context = context;
    }

    /**
     * Get (initialise) an instance of MeetingDisplay.
     * @param context The current context.
     * @return An instance of MeetingDisplay.
     * @throws IllegalStateException if instance already initialised.
     */
    public static synchronized MeetingDisplay getInstance(Context context) {
        if(!instanceExists()) {
            instance = new MeetingDisplay(context);
            return instance;
        }
        throw new IllegalStateException(R.string.meeting_display_already_init + "");
    }

    /**
     * Get the current MeetingDisplay instance.
     * @return The current MeetingDisplay instance.
     * @throws IllegalStateException if instance is null.
     */
    public static synchronized MeetingDisplay getInstance() {
        if(instanceExists()) {
            return instance;
        }
        throw new IllegalStateException(R.string.meeting_display_not_init + "");
    }

    public static boolean instanceExists() {
        return instance != null ? true : false;
    }

    public String raceCodeToString(String code) {
        String r = "";
        if(code == null || code == "")
            return r;
        else {
            if(code.equals("R"))
                r = "Races";
            else if(code.equals("G"))
                r = "Greyhounds";
            else if(code.equals("T"))
                r = "Trots";
            else if(code.equals("S"))
                r = "Other";
        }
        return r;
    }

    private Context context;
    private static volatile MeetingDisplay instance = null;
}

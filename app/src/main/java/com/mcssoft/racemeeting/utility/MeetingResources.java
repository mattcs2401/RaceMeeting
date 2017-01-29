package com.mcssoft.racemeeting.utility;

import android.content.Context;

/**
 * Utility class to get resource values.
 */
public class MeetingResources {

    private MeetingResources(Context context) {
        this.context = context;
    }

    /**
     * Get (initialise) an instance of MeetingResources.
     * @param context The current context.
     * @return An instance of MeetingResources.
     */
    public static synchronized MeetingResources getInstance(Context context) {
        if(!instanceExists()) {
            instance = new MeetingResources(context);
        }
        return instance;
    }

    /**
     * Get the current MeetingResources instance.
     * @return The current MeetingResources instance.
     */
    public static synchronized MeetingResources getInstance() {
        return instance;
    }

    public static boolean instanceExists() {
        return instance != null ? true : false;
    }

    public int getInteger(int resId) {
        return context.getResources().getInteger(resId);
    }

    public boolean getBoolean(int resId) {
        return context.getResources().getBoolean(resId);
    }

    private Context context;

    private static volatile MeetingResources instance = null;
}

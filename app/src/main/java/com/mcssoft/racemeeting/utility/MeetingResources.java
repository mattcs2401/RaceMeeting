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

    /**
     * Check if this instance exists.
     * @return True if instance exists, else false.
     */
    public static boolean instanceExists() {
        return instance != null ? true : false;
    }

    /**
     * Get an integer resource.
     * @param resId The resource id.
     * @return The integer resource.
     */
    public int getInteger(int resId) {
        return context.getResources().getInteger(resId);
    }

    /**
     * Get an boolean resource.
     * @param resId The resource id.
     * @return The boolean resource.
     */
    public boolean getBoolean(int resId) {
        return context.getResources().getBoolean(resId);
    }

    public String getString(int resId) {
        return context.getResources().getString(resId);
    }

    public String[] getStringArray(int resId) {
        return context.getResources().getStringArray(resId);
    }

    /**
     * Ensure instance values are made NULL.
     */
    public void destroy() {
        context = null;
        instance = null;
    }

    private Context context;

    private static volatile MeetingResources instance = null;
}

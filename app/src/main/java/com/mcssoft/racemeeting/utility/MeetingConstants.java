package com.mcssoft.racemeeting.utility;

/**
 * General purpose class for app constants.
 **/
public class MeetingConstants {

    // Time/Date constants.
    public static final String HOUR = "hour";
    public static final int TIME_COMPONENT_HOUR = 0;
    public static final String MINS = "mins";
    public static final int TIME_COMPONENT_MINUTE = 1;
    public static final int TIME_COMPONENT_HOUR_MINUTE = 2;

    // Dialogs constants.
    public static final String FRAGMENT_ID = "dialog_id";             // generic keys.
    public static final String FRAGMENT_RB_VAL = "dialog_btn_val";    // "       "
    public static final int CITY_CODES_FRAGMENT_ID = 0;               // city codes dialog id.
    public static final String CITY_CODES_FRAGMENT_TAG = "CITY_CODES_FRAGMENT_TAG";    // frag trans tag, city codes.
    public static final int RACE_CODES_FRAGMENT_ID = 1;               // race codes dialog id.
    public static final String RACE_CODES_FRAGMENT_TAG = "RACE_CODES_FRAGMENT_TAG";    // frag trans tag, race codes.
    public static final String TIME_PICKER_TAG = "TIME_PICKER_TAG";   // frag trans tag, time picker.

    // Delete dialog constants.
    public static final String DELETE_DIALOG_ROWID = "delete_dialog_summary_key";

    // Calendar (time) formats.
    public static final String TIME_FORMAT = "time_format";     // generic 'key' value.
    public static final String TIME_FORMAT_12HR = "hh:mm aa";   // HH:MM am/pm
    public static final String TIME_FORMAT_24HR = "kk:mm";      // HH24:MI
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    // Calendar misc.
    public static final int ONE_MINUTE = 60000;       // i.e. 60000ms
    public static final int THIRTY_SECONDS = 30000;   // i.e. 30000ms

    // Fragment tags.
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "edit_fragment_tag";
    public static final String DEFAULT_DETAIL_FRAGMENT_TAG = "detail_fragment_tag";
    public static final String DEFAULT_LISTING_FRAGMENT_TAG = "listing_fragment_tag";
    public static final String DEFAULT_DELETE_DIALOG_FRAGMENT_TAG = "delete_dialog_fragment_tag";

    // Preferences (general)
    public static final String MEETING_SHOW_PREVIOUS = "P";

    // Race code preference.
    public static final String RACE_CODE_PREF_VAL_KEY = "race_code_pref_val_key"; // e.g. 'R'
    public static final String RACE_CODE_PREF_ID_KEY = "race_code_pref_id_key";   // button id.
    public static final String RACE_CODE_DEFAULT_VAL = "R";
    public static final String RACE_CODE_NONE = "None";

    // Meetings to show prference
    public static final String RACE_SHOW_MEETINGS_PREF_ID_KEY = "race_show_meetings_pref_id_key";
    public static final String RACE_SHOW_MEETINGS_PREF_VAL_KEY = "race_show_meetings_pref_val_key";
    public static final String RACE_SHOW_MEETINGS_DEFAULT_VAL = "Show only today";
    public static final String RACE_SHOW_MEETINGS_INCL_DATE_KEY = "race_show_meetings_incl_date_key";
    public static final boolean RACE_SHOW_MEETINGS_INCL_DATE_DEFAULT_VAL = false;
    public static final String RACE_PRIOR_MEETINGS_EXIST = "previous meetings exist";
    public static final String RACE_NO_MEETINGS = "0";

    // Reminder preferences (settings) defaults.
    public static final String REMINDER_PREF_KEY = "reminder_pref_key";
    public static final String[] REMINDER_LABELS = {"No reminder", "minute", "minutes"};
    public static final int REMINDER_MIN_VALUE = 0; // also default value (== no reminder).
    public static final int REMINDER_MAX_VALUE = 10;

    // Past time preferences (settings) defaults.
    public static final String TIME_PAST_PREF_KEY = "time_past_pref_key";

    // Meeting notification enable preference.
    public static final String NOTIFY_ENABLE_PREF_KEY = "notify_enable_pref_key";

    /* Meeting notification preference. */
    // (number picker prefs).
    public static final String NOTIFY_PREF_KEY = "notify_pref_key";
    public static final int NOTIFY_DEFAULT_PREF_VAL = 1;
    // (sound prefs)
    public static final String NOTIFY_SOUND_PREF_KEY = "notify_sound_pref_key";
    public static final boolean NOTIFY_SOUND_DEFAULT_PREF_VAL = false;
    // (vibrate prefs)
    public static final String NOTIFY_VIBRATE_PREF_KEY = "notify_vibrate_pref_key";
    public static final boolean NOTIFY_VIBRATE_DEFAULT_PREF_VAL = false;
    public static final int NOTIFY_VIBRATE_MIN_VAL = 1; // min number vibrations.
    public static final int NOTIFY_VIBRATE_MAX_VAL = 3; // max number vibrations.

    // Time format preferences.
    public static final String TIME_FORMAT_PREF_KEY = "time_format_pref_key";
    public static final String TIME_FORMAT_PREF_24HR = "24HR";
    public static final String TIME_FORMAT_PREF_24HR_KEY = "0";
    public static final String TIME_FORMAT_PREF_12HR = "12HR";
    public static final String TIME_FORMAT_PREF_12HR_KEY = "1";

    // Services / receivers.
    public static final int MSG_LISTING_SERVICE = 0x10;
    public static final int LISTING_SERVICE = 0x11;
    public static final int MSG_NOTIFY_SERVICE = 0x12;
    public static final int NOTIFY_SERVICE = 0x13;
    public static final String LISTING_SERVICE_HANDLER = "listing_service_handler";
    public static final String NOTIFY_SERVICE_HANDLER = "notify_service_handler";

    public static final String PAST_TIME_JOB_KEY = "past_time_job_key";
    public static final String PRIOR_TIME_JOB_KEY = "prior_time_job_key";

    // Misc.
    public static final int NEW_MEETING = 0x20;
    public static final int EDIT_MEETING = 0x21;
    public static final int COPY_MEETING = 0x22;
    public static final int SHOW_MEETING = 0x23;

    public static final int INIT_DEFAULT = -1; // arbitrary initialiser value.
    public static final int NOTIFY_REQUIRED = 0x30;
    public static final int MEETING_LOADER = 0x40;
    public static final String NOTIFY_REQUIRED_KEY = "notify_required_key";
    public static final int LISTING_CHANGE_REQUIRED = 0x31;   //
    public static final String CONTENT_TITLE = "Racemeeting nearing start time.";
    public static final String EDIT_EXISTING_OR_COPY = "edit_existing_or_copy";
    public static final String EDIT_ACTION_EXISTING = "edit_action_existing";
    public static final String EDIT_ACTION_NEW = "edit_action_new";
    public static final String EDIT_ACTION_COPY = "edit_action_copy";
    public static final String SHOW_SUMMARY = "show_summary";
}


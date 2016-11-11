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
    public static final String FRAGMENT_ID = "dialog_id";              // generic key.
    public static final String FRAGMENT_RB_ID = "dialog_rb_id";        // "       "
    public static final String FRAGMENT_RB_VAL = "dialog_btn_val";    // "       "
    public static final int CITY_CODES_FRAGMENT_ID = 0;                // city codes dialog id.
    public static final String CITY_CODES_FRAGMENT_TAG = "CITY_CODES_FRAGMENT_TAG";    // frag trans tag, city codes.
    public static final int RACE_CODES_FRAGMENT_ID = 1;                // race codes dialog id.
    public static final String RACE_CODES_FRAGMENT_TAG = "RACE_CODES_FRAGMENT_TAG";    // frag trans tag, race codes.
    public static final String TIME_PICKER_TAG = "TIME_PICKER_TAG";  // frag trans tag, time picker.

    // Delete dialog constants.
    public static final String DELETE_DIALOG_SELECT_POSITIVE = "delete_dialog_select_positive";
    public static final String DELETE_DIALOG_ROWID = "delete_dialog_summary_key";
    public static final String DELETE_ITEM_COUNT_KEY = "delete_item_count_key";

    // Calendar (time) formats.
    public static final String TIME_FORMAT = "time_format";     // generic 'key' value.
    public static final String TIME_FORMAT_12HR = "hh:mm aa";   // HH:MM am/pm
    public static final String TIME_FORMAT_24HR = "kk:mm";      // HH24:MI

    // Calendar misc.
    public static final int ONE_MINUTE = 60000;       // i.e. 60000ms
    public static final int THIRTY_SECONDS = 30000;   // i.e. 30000ms

    // Fragment tags.
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "edit_fragment_tag";
    public static final String DEFAULT_DETAIL_FRAGMENT_TAG = "detail_fragment_tag";
    public static final String DEFAULT_REVIEW_FRAGMENT_TAG = "review_fragment_tag";
    public static final String DEFAULT_LISTING_FRAGMENT_TAG = "listing_fragment_tag";
    public static final String DEFAULT_LISTING_FRAGMENT2_TAG = "listing_fragment2_tag";
    public static final String DEFAULT_DELETE_DIALOG_FRAGMENT_TAG = "delete_dialog_fragment_tag";
    public static final String DEFAULT_DELETE_FRAGMENT_TAG = "delete_fragment_tag";

    // Preferences (general)
    public static final String MEETING_NOTIFICATIONS_KEY = "pref_meeting_notifications_key";
    public static final String MEETING_SHOW_KEY = "pref_today_meeting_key";

    public static final String MEETING_PREFERENCES_KEY = "meeting_preferences_key";
    public static final int PREFERENCES_CHANGED = 1;
    public static final int PREFERENCES_ACTIVITY_REQUEST_CODE = 2;

    public static final String TIME_ACTIONS_PREFCAT_KEY = "time_actions_prefcat_key";
    //
    public static final String TIME_ACTIONS_PREF_KEY = "time_actions_pref_key";

    // Prior time preferences (settings) defaults.
    public static final String TIME_PRIOR_PREF_KEY = "time_prior_pref_key";
    public static final int TIME_PRIOR_PREF_DEFAULT = 0; // == the "No reminder." preference.

    // Past time preferences (settings) defaults.
    public static final String TIME_PAST_PREF_KEY = "time_past_pref_key";
    public static final int TIME_PAST_PREF_DEFAULT = 0; // == the "Take no action ..." preference.

    // Time format preferences.
    public static final String TIME_FORMAT_PREF_KEY = "time_format_pref_key";
    public static final String TIME_FORMAT_PREF_24HR = "24HR";
    public static final String TIME_FORMAT_PREF_24HR_KEY = "0";
    public static final String TIME_FORMAT_PREF_12HR = "12HR";
    public static final String TIME_FORMAT_PREF_12HR_KEY = "1";
    public static final String TIME_FORMAT_PREF_DEFAULT = TIME_FORMAT_PREF_24HR;

    // Default race code preferences.
    public static final String DEFAULT_RACE_CODE_PREF_KEY = "default_race_code_pref_key";

    // Services / receivers.
    public static final int MSG_LISTING_SERVICE = 0x10;    // arbitrary value.
    public static final int LISTING_SERVICE = 0;
    public static final int MSG_NOTIFY_SERVICE = 0x11;   // arbitrary value.
    public static final int NOTIFY_SERVICE = 1;
    public static final String LISTING_SERVICE_HANDLER = "listing_service_handler";
    public static final String LISTING_SERVICE_ACTION = "listing_service_action";
    public static final String NOTIFY_SERVICE_HANDLER = "notify_service_handler";
    public static final String NOTIFY_SERVICE_ACTION = "notify_service_action";

    public static final String PAST_TIME_JOB_KEY = "past_time_job_key";
    public static final String PRIOR_TIME_JOB_KEY = "prior_time_job_key";

    // Review.
    public static final String REVIEW_VALUES_KEY = "review_values_key";

    // Custom keyboard key codes.
    public static final int KEYCODE_CANCEL = -3;
    public static final int KEYCODE_DONE = -4;
    public static final int KEYCODE_DELETE = -5;
    //public static final int KEYCODE_CLEAR = 1001;
    //public static final int KEYCODE_COMMA = 1002;

    // Meeting race codes.
    public static final String RACE_CODE_ID_NONE = "0";  // maps to default race code array.
    public static final String RACE_CODE_ID_R = "1";     //
    public static final String RACE_CODE_ID_G = "2";     //
    public static final String RACE_CODE_ID_T = "3";     //
    public static final String RACE_CODE_ID_S = "4";     //
    public static final String RACE_CODE_R = "R";
    public static final String RACE_CODE_G = "G";
    public static final String RACE_CODE_T = "T";
    public static final String RACE_CODE_S = "S";

    // Misc.
    public static final long NEW_MEETING = 0x0;
    public static final long EDIT_MEETING = 0x01;
    public static final int EDIT_ACTIVITY_REQUEST_CODE = 0x03;
    public static final int EDIT_ACTIVITY_RESULT_SAVE = 0x04;
    public static final int INIT_DEFAULT = -1;              // arbitrary initialiser value.
    public static final int NOTIFY_REQUIRED = 0;
    public static final int MEETING_LOADER = 0;
    public static final String NOTIFY_REQUIRED_KEY = "notify_required_key";
    public static final int LISTING_CHANGE_REQUIRED = 1;   //
    public static final String CONTENT_TITLE = "Racemeeting nearing start time.";
    public static final String SAVE_VALUES_EXISTING = "save_values";
    public static final String SAVE_VALUES_NEW = "save_values_new";
    public static final String EDIT_EXISTING = "edit_existing";
    public static final String EDIT_NEW = "edit_new";
    public static final String EDIT_ACTION_EXISTING = "edit_action_existing";
    public static final String EDIT_ACTION_NEW = "edit_action_new";
    public static final String SELECTED_KEY = "selected_key";
    public static final String LIST_ROW_NUMBER = "list_row_number";
    public static final String SHOW_SUMMARY = "show_summary";
}


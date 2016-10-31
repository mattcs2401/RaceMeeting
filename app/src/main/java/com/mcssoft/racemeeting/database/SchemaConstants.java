package com.mcssoft.racemeeting.database;

public class SchemaConstants {

    // Content Provider Uri and Authority
    public static final String MEETING = "meeting";
    public static final String BASE = "com.mcssoft.racemeeting.";
    public static final String AUTHORITY = BASE + "database.MeetingProvider";
    public static final String CONTENT_URI = "content://" + AUTHORITY + "/" + MEETING;
    public static final String CURSOR_BASE_TYPE = "/" + BASE + MEETING;

    // UriMatcher stuff
    public static final int MEETING_TABLE = 0;
    public static final int MEETING_RECORD = 1;

    // Database columns
    public static final String COLUMN_ROWID = "_id";           // Note: Has to be like this (upper case _ID ?).
    public static final int COLUMN_ROWID_NDX = 0;
    public static final String COLUMN_CITY_CODE = "CITY_CODE";
    public static final int COLUMN_CITY_CODE_NDX = 1;
    public static final String COLUMN_RACE_CODE = "RACE_CODE";
    public static final int COLUMN_RACE_CODE_NDX = 2;
    public static final String COLUMN_RACE_NUM = "RACE_NUM";
    public static final int COLUMN_RACE_NUM_NDX = 3;
    public static final String COLUMN_RACE_SEL = "RACE_SEL";
    public static final int COLUMN_RACE_SEL_NDX = 4;
    public static final String COLUMN_DATE_TIME = "DATE_TIME";
    public static final int COLUMN_DATE_TIME_NDX = 5;
    // Generic field to indicate a display change is required.
    public static final String COLUMN_D_CHG_REQ = "D_CHG_REQ";
    public static final int COLUMN_D_CHG_REQ_NDX = 6;
    // Generic field that indicates if a notification set for the record.
    public static final String COLUMN_NOTIFIED = "NOTIFIED";
    public static final int COLUMN_NOTIFIED_NDX = 7;

    // Database version and names.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RACEMEETDB";
    public static final String DATABASE_TABLE = "RACEMEETTBL";

    // Database table create.
    public static final String DATABASE_CREATE = "CREATE TABLE "
            + DATABASE_TABLE   + " ("
            + COLUMN_ROWID     + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CITY_CODE + " TEXT NOT NULL, "
            + COLUMN_RACE_CODE + " TEXT NOT NULL, "
            + COLUMN_RACE_NUM  + " INTEGER NOT NULL, "
            + COLUMN_RACE_SEL  + " INTEGER NOT NULL, "
            + COLUMN_DATE_TIME + " INTEGER NOT NULL, "
            + COLUMN_D_CHG_REQ + " TEXT NOT NULL, "
            + COLUMN_NOTIFIED  + " TEXT NOT NULL)";

    public static final String SORT_ORDER = COLUMN_DATE_TIME + " ASC";

    // Partial select, get all records where a display change is required.
    public final static String SELECT_ALL_DCHANGE =
            "SELECT " + COLUMN_ROWID + "," +
                    COLUMN_D_CHG_REQ +
                    " FROM " + DATABASE_TABLE +
                    " WHERE " + COLUMN_D_CHG_REQ + "='N'" +
                    " AND " + COLUMN_DATE_TIME + " < ";

    // Select all.
    public static final String SELECT_ALL =
            "SELECT " +
            COLUMN_ROWID + "," +
            COLUMN_CITY_CODE + "," +
            COLUMN_RACE_CODE + "," +
            COLUMN_RACE_NUM + "," +
            COLUMN_RACE_SEL + "," +
            COLUMN_DATE_TIME + "," +
            COLUMN_D_CHG_REQ + "," +
            COLUMN_NOTIFIED +
            " FROM " + DATABASE_TABLE;

    // marries with DatabaseHelper.getMeetingListItemProjection.
    public static final String SELECT_ALL_MLI =
            "SELECT " +
             COLUMN_CITY_CODE + "," +
             COLUMN_RACE_CODE + "," +
             COLUMN_RACE_NUM + "," +
             COLUMN_RACE_SEL + "," +
             COLUMN_DATE_TIME + "," +
             COLUMN_D_CHG_REQ + "," +
             " FROM " + DATABASE_TABLE;

    // Select all records where a notificiation is required.
    public final static String SELECT_ALL_NOTIFY =
            SELECT_ALL +
            " WHERE " + COLUMN_D_CHG_REQ + "='N'" +
            " AND " + COLUMN_NOTIFIED + "='N'" +
            " AND " + COLUMN_DATE_TIME + " BETWEEN ";

    public static final String WHERE_CLAUSE_ROWID_EQUALS = SchemaConstants.COLUMN_ROWID + "=?";
}

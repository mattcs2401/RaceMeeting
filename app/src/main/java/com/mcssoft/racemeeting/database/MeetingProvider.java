package com.mcssoft.racemeeting.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MeetingProvider extends ContentProvider {

    //<editor-fold defaultstate="collapsed" desc="Region: Public static">
    public static final Uri contentUri = Uri.parse(SchemaConstants.CONTENT_URI);

    // MIME type - Table.
    public static final String MEETING_TABLE_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + SchemaConstants.CURSOR_BASE_TYPE;
    // MIME type - Record.
    public static final String MEETING_RECORD_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + SchemaConstants.CURSOR_BASE_TYPE;
    //</editor-fold>

    @Override
    public boolean onCreate() {
        //Log.d(LOG_TAG, "onCreate");
        initialise();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Use the UriMatcher to see what kind of query we have and format the db query accordingly.
        Cursor cursor;
        int urimatch = uriMatcher.match(uri);

        switch (urimatch) {
            case SchemaConstants.MEETING_TABLE:
                cursor = dB.query(SchemaConstants.DATABASE_TABLE, projection, selection, selectionArgs, null, null, SchemaConstants.SORT_ORDER);
                break;
            case SchemaConstants.MEETING_RECORD:
                cursor = dB.query(SchemaConstants.DATABASE_TABLE, projection, SchemaConstants.COLUMN_ROWID + "=?", selectionArgs, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // not sure why this is here.
        values.remove(SchemaConstants.COLUMN_ROWID);

        long id = dB.insertOrThrow(SchemaConstants.DATABASE_TABLE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String[] id = new String[] { Long.toString(ContentUris.parseId(uri)) };

        int count = dB.delete(SchemaConstants.DATABASE_TABLE, SchemaConstants.COLUMN_ROWID + "=?", id);

        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String[] id = new String[] { Long.toString(ContentUris.parseId(uri)) };

        int count = dB.update(SchemaConstants.DATABASE_TABLE, values, SchemaConstants.COLUMN_ROWID + "=?", id);

        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
//        Log.d(LOG_TAG, "getType");
        switch (uriMatcher.match(uri)) {
            case SchemaConstants.MEETING_TABLE:
                return MEETING_TABLE_TYPE;
            case SchemaConstants.MEETING_RECORD:
                return MEETING_RECORD_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(SchemaConstants.AUTHORITY, SchemaConstants.MEETING, SchemaConstants.MEETING_TABLE);
        // == com.mcssoft.racemeeting.database.MeetingProvider, meeting, 0

        matcher.addURI(SchemaConstants.AUTHORITY, SchemaConstants.MEETING + "/#", SchemaConstants.MEETING_RECORD);
        // == com.mcssoft.racemeeting.database.MeetingProvider, meeting/#, 1

        return matcher;
    }

    private void initialise() {
        uriMatcher = buildUriMatcher();
        dBHelper = new DatabaseHelper(getContext());
        dB = dBHelper.getWritableDatabase();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
    private SQLiteDatabase dB;
    private UriMatcher uriMatcher;
    private DatabaseHelper dBHelper;
    //private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

package com.mcssoft.racemeeting.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Database utility class.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, SchemaConstants.DATABASE_NAME, null, SchemaConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "onCreate");

        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + SchemaConstants.DATABASE_NAME + "." + SchemaConstants.DATABASE_TABLE + ";");
            db.execSQL(SchemaConstants.DATABASE_CREATE);
            db.setTransactionSuccessful();
        } catch(SQLException sqle) {
            Log.d(LOG_TAG, "Exception thrown on database create: " + sqle.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(this.getClass().getCanonicalName(), "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + SchemaConstants.DATABASE_NAME + "." + SchemaConstants.DATABASE_TABLE + ";");
    }

    public enum Projection {
        DatabaseSchema, MeetingListItem, DChange, Notify
    }

    public static String [] getProjection(Projection projection) {
        switch (projection) {
            case DatabaseSchema:
                return getDatabaseSchemaProjection();
            case MeetingListItem:
                return getMeetingListItemProjection();
            case DChange:
                return getDChangeProjection();
            case Notify:
                return getNotifyProjection();
        }
        return  null;
    }

    private static final String[] getDatabaseSchemaProjection() {
        return new String[] {
            SchemaConstants.COLUMN_ROWID,
            SchemaConstants.COLUMN_CITY_CODE,
            SchemaConstants.COLUMN_RACE_CODE,
            SchemaConstants.COLUMN_RACE_NUM,
            SchemaConstants.COLUMN_RACE_SEL,
            SchemaConstants.COLUMN_DATE_TIME,
            SchemaConstants.COLUMN_D_CHG_REQ,
            SchemaConstants.COLUMN_NOTIFIED
        };
    }

    private static final String[] getMeetingListItemProjection() {
        return new String [] {
            SchemaConstants.COLUMN_CITY_CODE,
            SchemaConstants.COLUMN_RACE_CODE,
            SchemaConstants.COLUMN_RACE_NUM,
            SchemaConstants.COLUMN_RACE_SEL,
            SchemaConstants.COLUMN_DATE_TIME,
            SchemaConstants.COLUMN_D_CHG_REQ,
            SchemaConstants.COLUMN_NOTIFIED
        };
    }

    private static final String [] getDChangeProjection () {
        return new String[] {
            SchemaConstants.COLUMN_ROWID,
            SchemaConstants.COLUMN_DATE_TIME,
            SchemaConstants.COLUMN_D_CHG_REQ
        };
    }

    private static final String[] getNotifyProjection() {
        return new String[] {
            SchemaConstants.COLUMN_ROWID,
            SchemaConstants.COLUMN_CITY_CODE,
            SchemaConstants.COLUMN_RACE_CODE,
            SchemaConstants.COLUMN_RACE_NUM,
            SchemaConstants.COLUMN_RACE_SEL,
            SchemaConstants.COLUMN_DATE_TIME
        };
    }

    private String LOG_TAG = this.getClass().getCanonicalName();
}
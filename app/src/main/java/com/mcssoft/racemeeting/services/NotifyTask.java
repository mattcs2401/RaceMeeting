package com.mcssoft.racemeeting.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mcssoft.racemeeting.database.DatabaseHelper;
import com.mcssoft.racemeeting.database.MeetingProvider;
import com.mcssoft.racemeeting.database.SchemaConstants;
import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingTime;

// https://androidresearch.wordpress.com/2013/05/10/dealing-with-asynctask-and-screen-orientation/

public class NotifyTask extends AsyncTask<JobParameters, Void, JobParameters> {

	public NotifyTask(NotifyService notifyService, int reminderTime) {
		Log.d(LOG_TAG, "constructor");
		this.notifyService = notifyService;
        this.reminderTime = reminderTime;
	}

	@Override
    /* Method just runs a query and returns any result in the extras. */
    protected JobParameters doInBackground(JobParameters... jParams) {

        JobParameters result = null;

        if (!isCancelled()) {
            Log.d(LOG_TAG, "doInBackground");

            for (JobParameters jp : jParams) {
                result = jp;

                cursor = doGetCursor();

                if (cursor.getCount() > 0) {
                    String [] row = null;

                    while (cursor.moveToNext()) {
                        if(doCriteriaCheck()) {
                            row = doCollateValues();
                            result.getExtras().putStringArray(row[0], row); // key is column id as string.
                            doUpdateCursor(row[0]); // set Notified flag.
                        }
                    }
                    if(row != null) {
                        result.getExtras().putInt(MeetingConstants.NOTIFY_REQUIRED_KEY,
                                                  MeetingConstants.NOTIFY_REQUIRED);
                    }
                }
                cursor.close();
            }
        }
        return result;
    }

	// Android dev: Runs on the UI thread after doInBackground(Params...).
	// The specified result is the value returned by doInBackground(Params...)
    @Override
    protected void onPostExecute(JobParameters result) {
    	Log.d(LOG_TAG, "onPostExecute");
        notifyService.jobFinished(result, false);
        notifyService.onStopJob(result);
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">

    // Note: These methods are just basically snippets of functionality to make the doInBackground
    // method easier to read/debug.

    private void doUpdateCursor(String rowId) {
        Uri uri = ContentUris.withAppendedId(MeetingProvider.contentUri, Long.parseLong(rowId));
        ContentValues cVals = new ContentValues();
        cVals.put(SchemaConstants.COLUMN_NOTIFIED, "Y");
        notifyService.getContentResolver().update(uri, cVals, null, null);
    }

    private String [] doCollateValues() {
        return new String [] {
                Integer.toString(cursor.getInt(cursor.getColumnIndex(SchemaConstants.COLUMN_ROWID))),
                cursor.getString(cursor.getColumnIndex(SchemaConstants.COLUMN_CITY_CODE)),
                cursor.getString(cursor.getColumnIndex(SchemaConstants.COLUMN_RACE_CODE)),
                Integer.toString(cursor.getInt(cursor.getColumnIndex(SchemaConstants.COLUMN_RACE_NUM))),
                Integer.toString(cursor.getInt(cursor.getColumnIndex(SchemaConstants.COLUMN_RACE_SEL))),
                Long.toString(cursor.getLong(cursor.getColumnIndex(SchemaConstants.COLUMN_DATE_TIME)))};
    }

    private boolean doCriteriaCheck() {
        long lRaceTime = cursor.getLong(cursor.getColumnIndex(SchemaConstants.COLUMN_DATE_TIME));
//        String sRaceTime = MeetingTime.getInstance().getFormattedTimeFromMillis(lRaceTime);      // testing
        long lNotifyTime = MeetingTime.getInstance().getTimeMinus(reminderTime);
//        String sNotifyTime = MeetingTime.getInstance().getFormattedTimeFromMillis(lNotifyTime);  // testing
        long lCurrentTime = MeetingTime.getInstance().getTimeInMillis();
//        String sCurrentTime = MeetingTime.getInstance().getFormattedTimeFromMillis(lCurrentTime);// testing

        return ((lCurrentTime > lNotifyTime) && (lCurrentTime < lRaceTime));
    }

    private Cursor doGetCursor() {
        return notifyService.getContentResolver()
                .query(MeetingProvider.contentUri,
                       DatabaseHelper.getNotifyProjection(),
                       SchemaConstants.WHERE_FOR_NOTIFY,
                       null,
                       null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
    private int reminderTime;     // prior reminder time (from preferences) in mSec.
    private Cursor cursor;
	private JobService notifyService;

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}
/*
Android dev: AsyncTask's generic types:

The three types used by an asynchronous task are the following:

Params;   the type of the parameters sent to the task upon execution.
Progress; the type of the progress units published during the background computation.
Result;   the type of the result of the background computation.

Not all types are always used by an asynchronous task. To mark a type as unused, simply use the type Void:

 private class MyTask extends AsyncTask<Void, Void, Void> { ... }
*/
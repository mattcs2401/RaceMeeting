package com.mcssoft.racemeeting.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
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

//	@Override
//	public void onPreExecute() { }

	@Override
    /* Method just runs a query and returns any result in the extras. */
    protected JobParameters doInBackground(JobParameters... jParams) {

        JobParameters result = null;

        if (!isCancelled()) {
            Log.d(LOG_TAG, "doInBackground");

            for (JobParameters jp : jParams) {
                result = jp;

                Cursor cursor = notifyService.getContentResolver()
                        .query(MeetingProvider.contentUri,
                                DatabaseHelper.getNotifyProjection(),
                                SchemaConstants.WHERE_FOR_NOTIFY,
                                null,
                                null);

                if (cursor.getCount() > 0) {

                    int cols = cursor.getColumnCount();
                    String[] row = null;

                    while (cursor.moveToNext()) {
                        long lRaceTime = cursor.getLong(cursor.getColumnIndex(SchemaConstants.COLUMN_DATE_TIME));
                        String sRaceTime = MeetingTime.getInstance().getFormattedTimeFromMillis(lRaceTime);     // testing
                        long lNotifyTime = MeetingTime.getInstance().getTimeMinus(reminderTime);
                        String sNotifyTime = MeetingTime.getInstance().getFormattedTimeFromMillis(lNotifyTime); // testing
                        long lCurrentTime = MeetingTime.getInstance().getTimeInMillis();
                        String sCurrentTime = MeetingTime.getInstance().getFormattedTimeFromMillis(lCurrentTime);// testing

                        if((lCurrentTime > lNotifyTime) && (lCurrentTime < lRaceTime)) {
                            row = new String[cols];

                            row[0] = Integer.toString(cursor.getInt(cursor.getColumnIndex(SchemaConstants.COLUMN_ROWID)));
                            row[1] = cursor.getString(cursor.getColumnIndex(SchemaConstants.COLUMN_CITY_CODE));
                            row[2] = cursor.getString(cursor.getColumnIndex(SchemaConstants.COLUMN_RACE_CODE));
                            row[3] = Integer.toString(cursor.getInt(cursor.getColumnIndex(SchemaConstants.COLUMN_RACE_NUM)));
                            row[4] = Integer.toString(cursor.getInt(cursor.getColumnIndex(SchemaConstants.COLUMN_RACE_SEL)));
                            row[5] = Long.toString(cursor.getLong(cursor.getColumnIndex(SchemaConstants.COLUMN_DATE_TIME)));

                            result.getExtras().putStringArray(row[0], row);     // key is column id as string.
                        } // end if
                    } // end while
                    if(row != null) {
                        result.getExtras().putInt(MeetingConstants.NOTIFY_REQUIRED_KEY, MeetingConstants.NOTIFY_REQUIRED);
                    }
                } else {
                    result.getExtras().putInt(MeetingConstants.NOTIFY_REQUIRED_KEY, 0); //MeetingConstants.INIT_DEFAULT);
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

    private int reminderTime;     // prior reminder time (from preferences) in mSec.
	private JobService notifyService;

    private String LOG_TAG = this.getClass().getCanonicalName();
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
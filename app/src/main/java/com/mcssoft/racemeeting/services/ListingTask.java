package com.mcssoft.racemeeting.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.mcssoft.racemeeting.database.DatabaseHelper;
import com.mcssoft.racemeeting.database.MeetingProvider;
import com.mcssoft.racemeeting.database.SchemaConstants;
import com.mcssoft.racemeeting.utility.MeetingResources;
import com.mcssoft.racemeeting.utility.MeetingTime;

import mcssoft.com.racemeeting.R;
// https://androidresearch.wordpress.com/2013/05/10/dealing-with-asynctask-and-screen-orientation/

public class ListingTask extends AsyncTask<JobParameters, Void, JobParameters> {

	public ListingTask(ListingService listingService) {
		Log.d(LOG_TAG, "constructor");
		this.listingService = listingService;
	}

//	@Override
//	public void onPreExecute() { }
	
	// Android dev:
	// "The specified parameters are the parameters passed to execute(Params...) by the caller of this task." 
	@Override
    protected JobParameters doInBackground(JobParameters... jParams) {

        JobParameters result = null;
        updated = false;

        if (!isCancelled()) {
            Log.d(LOG_TAG, "doInBackground");

//            int size = jParams.length;  // what if this is > 1 ?
            for (JobParameters jp : jParams) {
                result = jp;
                Cursor cursor = listingService.getContentResolver()
                        .query(MeetingProvider.contentUri,
                               DatabaseHelper.getProjection(DatabaseHelper.Projection.DChange),
                               SchemaConstants.WHERE_FOR_DCHANGE,
                               new String[] {"N", Long.toString(MeetingTime.getInstance().getTimeInMillis())},
                               null);

                if (cursor.getCount() > 0) {
                    int rowId;
                    while (cursor.moveToNext()) {
                        ContentValues cVals = new ContentValues();
                        cVals.put(SchemaConstants.COLUMN_D_CHG_REQ, "Y");
                        rowId = cursor.getInt(cursor.getColumnIndex(SchemaConstants.COLUMN_ROWID));
                        listingService.getContentResolver().update(ContentUris.withAppendedId(MeetingProvider.contentUri, rowId), cVals, null, null);

                        if(updated == false) {
                            updated = true;
                        }
                    }
                    cursor.close();

                    if(updated) {
                        result.getExtras().putInt(MeetingResources.getInstance()
                                .getString(R.string.past_time_job_key),
                                R.integer.listing_change_required);
                    } else {
                        result.getExtras().putInt(MeetingResources.getInstance()
                                .getString(R.string.past_time_job_key), 0);
                    }
                }
            }
        }
        return result;
    }

	// Android dev:
	// Runs on the UI thread after doInBackground(Params...).
	// The specified result is the value returned by doInBackground(Params...)
    @Override
    protected void onPostExecute(JobParameters result) {
    	Log.d(LOG_TAG, "onPostExecute");
        listingService.jobFinished(result, false);
        listingService.onStopJob(result);
    }

    private boolean updated;
	private JobService listingService;

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
package com.mcssoft.racemeeting.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;

// https://androidresearch.wordpress.com/2013/05/10/dealing-with-asynctask-and-screen-orientation/

public class ListingTask extends AsyncTask<JobParameters, Void, JobParameters> {

	public ListingTask(ListingService jobSvc) {
		Log.d(LOG_TAG, "constructor");
		this.jobSvc = jobSvc;
	}

	@Override
	public void onPreExecute() { }
	
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

                // Work around, having issues with doing this via content resolver. Content resolver
                // wants selection arguments as string; but then we have column_value (int) < string.

//                Cursor cursor = DatabaseHelper.rawQuery(SchemaConstants.SELECT_ALL_DCHANGE +
//                    MeetingTime.getInstance().getCurrentTimeInMillis(), null);
//
//                if (cursor.getCount() > 0) {
//                    int rowId;
//                    String dChange;
//
//                    while (cursor.moveToNext()) {
//                        dChange = cursor.getString(cursor.getColumnIndex(SchemaConstants.COLUMN_D_CHG_REQ));
//
//                        if(dChange.equals("N")) {
//                            ContentValues cVals = new ContentValues();
//                            cVals.put(SchemaConstants.COLUMN_D_CHG_REQ, "Y");
//                            rowId = cursor.getInt(cursor.getColumnIndex(SchemaConstants.COLUMN_ROWID));
//                            jobSvc.getContentResolver().update(ContentUris.withAppendedId(MeetingProvider.contentUri, rowId), cVals, null, null);
//
//                            result.getExtras().putInt(MeetingConstants.PAST_TIME_JOB_KEY, MeetingConstants.LISTING_CHANGE_REQUIRED);
//
////                            if(updated == false) {
////                                updated = true;
////                            }
//                        }
//                    }
//                    cursor.close();

//                    if(updated) {
//                        result.getExtras().putInt(MeetingConstants.PAST_TIME_JOB_KEY, MeetingConstants.LISTING_CHANGE_REQUIRED);
//                    } /*else {
//                        result.getExtras().putInt(MeetingConstants.PAST_TIME_JOB_KEY, 0);
//                    }*/
//                }
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
        jobSvc.jobFinished(result, false);
        jobSvc.onStopJob(result);
    }

    private boolean updated;
	private JobService jobSvc;

    private String LOG_TAG = this.getClass().getCanonicalName();
}

// Can't seem to get this to work.

//   Uri contentUri = Uri.parse(SchemaConstants.CONTENT_URI);
//   String[] schema = DatabaseHelper.getDatabaseSchemaProjection();
//   Cursor cursor = context.getContentResolver()
//                          .query(contentUri,
//                                 schema,
//                                 SchemaConstants.COLUMN_DATE_TIME + "<?", // + (int) MeetingTime.getInstance().getCurrentTimeInMillis(),
//                                 new String[] {Long.toString(MeetingTime.getInstance().getCurrentTimeInMillis())},
//                                 SchemaConstants.SORT_ORDER);


/*
Android dev: AsyncTask's generic types:

The three types used by an asynchronous task are the following:

Params;   the type of the parameters sent to the task upon execution.
Progress; the type of the progress units published during the background computation.
Result;   the type of the result of the background computation.

Not all types are always used by an asynchronous task. To mark a type as unused, simply use the type Void:

 private class MyTask extends AsyncTask<Void, Void, Void> { ... }
*/
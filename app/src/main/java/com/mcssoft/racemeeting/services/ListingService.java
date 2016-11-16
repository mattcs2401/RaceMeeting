package com.mcssoft.racemeeting.services;


import mcssoft.com.racemeeting3.R;

import com.mcssoft.racemeeting.fragment.MainFragment;
import com.mcssoft.racemeeting.utility.MeetingConstants;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

// http://developer.android.com/samples/JobScheduler/index.html

public class ListingService extends JobService {

//	public ListingService() { }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartComand");

        // the messenger object is effectively a reference to the handler in the MainFragment.
        Messenger messenger = intent.getParcelableExtra(MeetingConstants.LISTING_SERVICE_HANDLER);

        // Dev doco: ... the best way to get one of these is to call Message.obtain()
        Message message = Message.obtain();

        //Dev doco: User-defined message code so that the recipient can identify what this message is about.
        message.what = MeetingConstants.MSG_LISTING_SERVICE;

        // Dev doco: An arbitrary object to send to the recipient.
        message.obj = this;

        try {
            messenger.send(message);
        } catch (RemoteException e) {
            throw new IllegalStateException(R.string.listing_service_exception + "");
        }

        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters jobParams) {
        if(mainFragment == null) {
            return false;
        } else {
            Log.d(LOG_TAG, "onStartJob");
            listingTask = new ListingTask(this);
            mainFragment.onReceivedStartJob(jobParams.getExtras());
            listingTask.execute(jobParams);
            return true;
        }
    }

    @Override
    public boolean onStopJob(JobParameters result) {
        if (mainFragment == null) {
            return false;
        } else {
            Log.d(LOG_TAG, "onStopJob");
            mainFragment.onReceivedStopJob(result.getExtras());
            return true; // indicates to job manager to reschedule.
        }
    }

    /**
     * Send job to the job scheduler.
     * @param jobInfo The job details.
     * @return True if scheduled.
     */
    public boolean scheduleJob(JobInfo jobInfo) {
        Log.d(LOG_TAG, "scheduleJob");
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if(jobScheduler.schedule(jobInfo) == JobScheduler.RESULT_SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    public void setUiCallback(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    private ListingTask listingTask;
    private MainFragment mainFragment;

    private String LOG_TAG = this.getClass().getCanonicalName();

}

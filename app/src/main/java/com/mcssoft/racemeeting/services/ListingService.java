package com.mcssoft.racemeeting.services;


import mcssoft.com.racemeeting.R;

import com.mcssoft.racemeeting.fragment.MainFragment;
import com.mcssoft.racemeeting.utility.MeetingResources;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartComand");

        // the messenger object is effectively a reference to the handler in the MainFragment.
        Messenger messenger = intent.getParcelableExtra(MeetingResources.getInstance()
                .getString(R.string.listing_service_handler));
        // Dev doco: ... the best way to get one of these is to call Message.obtain()
        Message message = Message.obtain();
        //Dev doco: User-defined message code so that the recipient can identify what this message is about.
        message.what = R.integer.msg_listing_service;
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
            ListingTask listingTask = new ListingTask(this);
//            mainFragment.onReceivedStartJobListing(jobParams.getExtras());
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
            mainFragment.onReceivedStopJobListing(result.getExtras());
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
        return (jobScheduler.schedule(jobInfo) == JobScheduler.RESULT_SUCCESS);
    }

    public void setUiCallback(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    private MainFragment mainFragment;

    private String LOG_TAG = this.getClass().getCanonicalName();
}

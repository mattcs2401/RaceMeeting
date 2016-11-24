package com.mcssoft.racemeeting.services;


import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import mcssoft.com.racemeeting3.R;

import com.mcssoft.racemeeting.fragment.MainFragment;
import com.mcssoft.racemeeting.utility.MeetingConstants;

public class NotifyService extends JobService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartComand");

        // the messenger object is effectively a reference to the handler in the MainFragment.
        Messenger messenger = intent.getParcelableExtra(MeetingConstants.NOTIFY_SERVICE_HANDLER);

        // Dev doco: ... the best way to get one of these is to call Message.obtain()
        Message message = Message.obtain();

        //Dev doco: User-defined message code so that the recipient can identify what this message is about.
        message.what = MeetingConstants.MSG_NOTIFY_SERVICE;

        // Dev doco: An arbitrary object to send to the recipient.
        message.obj = this;

        try {
            messenger.send(message);
        } catch (RemoteException e) {
            throw new IllegalStateException(R.string.notify_service_exception + "");
        }

        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters jobParams) {
        boolean retVal = false;
        if(mainFragment != null) {
            Log.d(LOG_TAG, "onStartJob");

            int iReminder = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getInt(MeetingConstants.TIME_PRIOR_PREF_KEY, MeetingConstants.INIT_DEFAULT);

            notifyTask = new NotifyTask(this, (iReminder * MeetingConstants.ONE_MINUTE));
            mainFragment.onReceivedStartJobNotify(jobParams.getExtras());
            notifyTask.execute(jobParams);
            retVal = true;
        }
        return retVal;
    }

    public boolean scheduleJob(JobInfo jobInfo) {
        Log.d(LOG_TAG, "scheduleJob");
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if(jobScheduler.schedule(jobInfo) == JobScheduler.RESULT_SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParams) {
        if (mainFragment == null) {
            return false;
        } else {
            Log.d(LOG_TAG, "onStopJob");
            if(jobParams.getExtras().getInt(MeetingConstants.NOTIFY_REQUIRED_KEY) == MeetingConstants.NOTIFY_REQUIRED) {
                mainFragment.onReceivedStopJobNotify(jobParams.getExtras());
            }
            return true; // indicates to job manager to reschedule.
        }
    }

    public void setUiCallback(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    private NotifyTask notifyTask;
    private MainFragment mainFragment;

    private String LOG_TAG = this.getClass().getCanonicalName();

}

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
import android.util.Log;

import mcssoft.com.racemeeting3.R;

import com.mcssoft.racemeeting.utility.MeetingConstants;

public class NotifyService extends JobService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartComand");

        // the messenger object is effectively a reference to the handler in the ListingFragment.
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
//        if(lFrag != null) {
//            Log.d(LOG_TAG, "onStartJob");
//
//            int iReminder = MeetingConstants.INT_DEFAULT;
//            String sReminder = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
//                    .getString(MeetingConstants.TIME_PRIOR_PREF_KEY, null);
//
//            String[] mprt = getResources().getStringArray(R.array.meetingPriorReminderTime);
//
//            if(!sReminder.equals(mprt[0])) {
//                for(String s : mprt) {
//                    if(s.equals(sReminder)) {
//                        iReminder = Integer.parseInt(sReminder) * 1000;   // milli seconds.
//                        break;
//                    }
//                }
//
//                nTask = new NotifyTask(this, iReminder);
//                nTask.execute(jobParams);
//                retVal = true;
//            }
//        }
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
//        if (lFrag == null) {
//            return false;
//        } else {
//            Log.d(LOG_TAG, "onStopJob");
//            // TODO - if there is nothing to notify about, then don't call this.
//            if(jobParams.getExtras().getInt(MeetingConstants.NOTIFY_REQUIRED_KEY) == MeetingConstants.NOTIFY_REQUIRED) {
//                lFrag.onReceivedStopJob(jobParams.getExtras());
//            }
//            return true; // indicates to job manager to reschedule.
//        }
        return false;
    }

//    public void setUiCallback(ListingFragment lFrag) {
//        this.lFrag = lFrag;
//    }

//    private NotifyTask nTask;
//    private ListingFragment lFrag;

    private String LOG_TAG = this.getClass().getCanonicalName();

}
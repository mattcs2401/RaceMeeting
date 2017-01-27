package com.mcssoft.racemeeting.utility;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.util.Log;
import java.util.List;

import com.mcssoft.racemeeting.fragment.MainFragment;
import com.mcssoft.racemeeting.services.ListingService;
import com.mcssoft.racemeeting.services.NotifyService;

import mcssoft.com.racemeeting.R;

public class MeetingScheduler {

    public MeetingScheduler(Activity activity) {
        this.activity = activity;
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Default looper (for Services)">
    private final Handler lHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case R.integer.msg_listing_service:
                    Log.d(LOG_TAG, "handleMessage - listing service");
                    listingSvc = (ListingService) message.obj;
                    listingSvc.setUiCallback(getListingFragment());

                    if(!lsJobSchld) {
                        scheduleJob(R.integer.listing_service);
                    }
                    break;
                case R.integer.msg_notify_service:
                    Log.d(LOG_TAG, "handleMessage - notify service");
                    notifySvc = (NotifyService) message.obj;
                    notifySvc.setUiCallback(getListingFragment());

                    if(!nsJobSchld) {
                        scheduleJob(R.integer.notify_service);
                    }
                    break;
            }
        }
    };
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Service">
    public void startService(int svcId) { //}, int action) {
        switch(svcId) {
            case R.integer.listing_service:
                Log.d(LOG_TAG, "startListingService");
                listingSvcIntent = new Intent(activity, ListingService.class);
                listingSvcIntent.putExtra(MeetingConstants.LISTING_SERVICE_HANDLER, new Messenger(lHandler));
//                listingSvcIntent.putExtra(R.integer.listing_service_ACTION, action);
                activity.startService(listingSvcIntent);
                break;
            case R.integer.notify_service:
                Log.d(LOG_TAG, "startNotifyService");
                notifySvcIntent = new Intent(activity, NotifyService.class);
                notifySvcIntent.putExtra(MeetingConstants.NOTIFY_SERVICE_HANDLER, new Messenger(lHandler));
//                notifySvcIntent.putExtra(R.integer.notify_service_ACTION, action);
                activity.startService(notifySvcIntent);
                break;
        }
    }

    public void stopService(int svcId) {
        switch (svcId) {
            case R.integer.listing_service:
                if(listingSvcIntent != null) {
                    Log.d(LOG_TAG, "stopListingService");
                    activity.stopService(listingSvcIntent);
                    listingSvcIntent = null;
                    listingSvc = null;
                }
                break;
            case R.integer.notify_service:
                if(notifySvcIntent != null) {
                    Log.d(LOG_TAG, "stopNotifyService");
                    activity.stopService(notifySvcIntent);
                    notifySvcIntent = null;
                    notifySvc = null;
                }
                break;
        }
    }

    public boolean isSvcRunning(int svcId) {
        boolean retVal = false;
        switch(svcId) {
            case R.integer.listing_service:
                retVal = (listingSvc != null) ? true : false;
                break;
            case R.integer.notify_service:
                retVal = (notifySvc != null) ? true : false;
                break;
        }
        return retVal;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Task/Job">
    public void cancelStopAll() {
        Log.d(LOG_TAG, "cancelStopAll");
        if(isSvcRunning(R.integer.listing_service)) {
            cancelJobs(R.integer.listing_service);
            lsJobSchld = false;
            stopService(R.integer.listing_service);
        }
        if(isSvcRunning(R.integer.notify_service)) {
            cancelJobs(R.integer.notify_service);
            nsJobSchld = false;
            stopService(R.integer.notify_service);
        }
    }

    public void cancelJobs(int svcId) {
        JobScheduler js = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        List<JobInfo> lJobInfo = js.getAllPendingJobs();

        if(lJobInfo.size() > 0) {
            int id;
            String name = "";

            for(JobInfo ji : lJobInfo) {
                id = ji.getId();
                name = ji.getService().getClassName().toString();

                switch (svcId) {
                    case R.integer.listing_service:
                        if (name.contains("ListingService")) {
                            js.cancel(id);
                        }
                        break;
                    case R.integer.notify_service:
                        if (name.contains("NotifyService")) {
                            js.cancel(id);
                        }
                        break;
                }
            }
        }
    }

    public void scheduleJob(int svcId) {
        JobInfo.Builder builder = null;
        PersistableBundle bundle = null;

        switch (svcId) {
            case R.integer.listing_service:
                if(isSvcRunning(R.integer.listing_service)) {
                    Log.d(LOG_TAG, "scheduleListingJob");

                    builder = new JobInfo.Builder(lsJobId++, new ComponentName(activity, ListingService.class));
                    builder.setPeriodic(R.integer.thirty_seconds);

                    bundle = new PersistableBundle();
                    bundle.putInt(MeetingConstants.PAST_TIME_JOB_KEY, R.integer.init_default); // key/value

                    builder.setExtras(bundle);

                    if(listingSvc.scheduleJob(builder.build())) {
                        lsJobSchld = true;
                    } else {
                        lsJobSchld = false;
                    }
                }
                break;
            case R.integer.notify_service:
                if(isSvcRunning(R.integer.notify_service)) {
                    Log.d(LOG_TAG, "scheduleNotifyJob");

                    builder = new JobInfo.Builder(lsJobId++, new ComponentName(activity, NotifyService.class));
                    builder.setPeriodic(R.integer.thirty_seconds);

                    bundle = new PersistableBundle();
                    bundle.putInt(MeetingConstants.PRIOR_TIME_JOB_KEY, R.integer.init_default); // key/value
                    builder.setExtras(bundle);
                    JobInfo ji = builder.build();

                    if(notifySvc.scheduleJob(ji)) {
                        nsJobSchld = true;
                    } else {
                        nsJobSchld = false;
                    }
                }
                break;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private MainFragment getListingFragment() {
        return (MainFragment) activity.getFragmentManager()
                .findFragmentByTag(MeetingConstants.DEFAULT_LISTING_FRAGMENT_TAG);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
    private int lsJobId;               // arbitary listing job number, incremented.
    private boolean lsJobSchld;        // flag, listing job scheduled.
    private boolean nsJobSchld;        // flag, notify job scheduled.

    private Intent listingSvcIntent;   // listing service intent.
    private Intent notifySvcIntent;    // notify service intent.

    private Activity activity;         // the parent activity.

    private NotifyService notifySvc;   // the notify service.
    private ListingService listingSvc; // the listing service.

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

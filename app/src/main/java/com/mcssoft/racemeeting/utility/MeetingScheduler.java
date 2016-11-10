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

import com.mcssoft.racemeeting.adapter.MeetingAdapter;
import com.mcssoft.racemeeting.services.ListingService;
import com.mcssoft.racemeeting.services.NotifyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MeetingScheduler {

    public MeetingScheduler(Activity activity, Context context) {
        this.activity = activity;
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Default looper (for Services)">
    private final Handler lHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MeetingConstants.MSG_LISTING_SERVICE:
                    Log.d(LOG_TAG, "handleMessage - listing service");
                    listingSvc = (ListingService) message.obj;
//                    listingSvc.setUiCallback(MainFragment.this);

                    if(!lsJobSchld) {
                        scheduleJob(MeetingConstants.LISTING_SERVICE);
                    }
                    break;
                case MeetingConstants.MSG_NOTIFY_SERVICE:
                    Log.d(LOG_TAG, "handleMessage - notify service");
                    notifySvc = (NotifyService) message.obj;
//                    notifySvc.setUiCallback(MainFragment.this);

                    if(!nsJobSchld) {
                        scheduleJob(MeetingConstants.NOTIFY_SERVICE);
                    }
                    break;
            }
        }
    };
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Service">
    public void startService(int svcId, int action) {
        switch(svcId) {
            case MeetingConstants.LISTING_SERVICE:
                Log.d(LOG_TAG, "startListingService");
                listingSvcIntent = new Intent(activity, ListingService.class);
                listingSvcIntent.putExtra(MeetingConstants.LISTING_SERVICE_HANDLER, new Messenger(lHandler));
                listingSvcIntent.putExtra(MeetingConstants.LISTING_SERVICE_ACTION, action);
                activity.startService(listingSvcIntent);
                break;
            case MeetingConstants.NOTIFY_SERVICE:
                Log.d(LOG_TAG, "startNotifyService");
                notifySvcIntent = new Intent(activity, NotifyService.class);
                notifySvcIntent.putExtra(MeetingConstants.NOTIFY_SERVICE_HANDLER, new Messenger(lHandler));
                notifySvcIntent.putExtra(MeetingConstants.NOTIFY_SERVICE_ACTION, action);
                activity.startService(notifySvcIntent);
                break;
        }
    }

    public void stopService(int svcId) {
        switch (svcId) {
            case MeetingConstants.LISTING_SERVICE:
                if(listingSvcIntent != null) {
                    Log.d(LOG_TAG, "stopListingService");
                    activity.stopService(listingSvcIntent);
                    listingSvcIntent = null;
                    listingSvc = null;
                }
                break;
            case MeetingConstants.NOTIFY_SERVICE:
                if(notifySvcIntent != null) {
                    Log.d(LOG_TAG, "stopNotifyService");
                    activity.stopService(notifySvcIntent);
                    notifySvcIntent = null;
                    notifySvc = null;
                }
                break;
        }
    }

    private boolean isSvcRunning(int svcId) {
        boolean retVal = false;
        switch(svcId) {
            case MeetingConstants.LISTING_SERVICE:
                retVal = (listingSvc != null) ? true : false;
                break;
            case MeetingConstants.NOTIFY_SERVICE:
                retVal = (notifySvc != null) ? true : false;
                break;
        }
        return retVal;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Task/Job">
    private void cancelStopAll() {
        Log.d(LOG_TAG, "cancelStopAll");
        if(isSvcRunning(MeetingConstants.LISTING_SERVICE)) {
            cancelJobs(MeetingConstants.LISTING_SERVICE);
            lsJobSchld = false;
            stopService(MeetingConstants.LISTING_SERVICE);
        }
        if(isSvcRunning(MeetingConstants.NOTIFY_SERVICE)) {
            cancelJobs(MeetingConstants.NOTIFY_SERVICE);
            nsJobSchld = false;
            stopService(MeetingConstants.NOTIFY_SERVICE);
        }
    }

    private void cancelJobs(int svcId) {
        JobScheduler js = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        List<JobInfo> lJobInfo = js.getAllPendingJobs();

        if(lJobInfo.size() > 0) {
            int id;
            String name = "";

            for(JobInfo ji : lJobInfo) {
                id = ji.getId();
                name = ji.getService().getClassName().toString();

                switch (svcId) {
                    case MeetingConstants.LISTING_SERVICE:
                        if (name.contains("ListingService")) {
                            js.cancel(id);
                        }
                        break;
                    case MeetingConstants.NOTIFY_SERVICE:
                        if (name.contains("NotifyService")) {
                            js.cancel(id);
                        }
                        break;
                }
            }
        }
    }

    public void onReceivedStopJob(PersistableBundle results) {
        Log.d(LOG_TAG, "onReceivedStopJob");
        Set<String> keys = results.keySet();

        if(keys.contains(MeetingConstants.PAST_TIME_JOB_KEY)) {
            if(results.getInt(MeetingConstants.PAST_TIME_JOB_KEY) == MeetingConstants.LISTING_CHANGE_REQUIRED) {
//                // Do the actual work required.
                // TODO - interface.
//                getLoaderManager().restartLoader(0, null, this);
            }
        }

        if(keys.contains(MeetingConstants.PRIOR_TIME_JOB_KEY)) {
            if(results.getInt(MeetingConstants.NOTIFY_REQUIRED_KEY) == MeetingConstants.NOTIFY_REQUIRED) {

                // strip out the "non race value" keys.
                keys.remove(MeetingConstants.PRIOR_TIME_JOB_KEY);
                keys.remove(MeetingConstants.NOTIFY_REQUIRED_KEY);

                ArrayList<String[]> al = new ArrayList<>();

                for(String key : keys) {
                    al.add(results.getStringArray(key));
                }
                // TODO - interface.
//                ((INotifier) activity).onNotify(al);
            }
        }
    }

    public void scheduleJob(int svcId) {
        JobInfo.Builder builder = null;
        PersistableBundle bundle = null;

        switch (svcId) {
            case MeetingConstants.LISTING_SERVICE:
                if(isSvcRunning(MeetingConstants.LISTING_SERVICE)) {
                    Log.d(LOG_TAG, "scheduleListingJob");

                    builder = new JobInfo.Builder(lsJobId++, new ComponentName(activity, ListingService.class));
                    builder.setPeriodic(MeetingConstants.ONE_MINUTE);

                    bundle = new PersistableBundle();
                    bundle.putInt(MeetingConstants.PAST_TIME_JOB_KEY, MeetingConstants.INIT_DEFAULT); // key/value

                    builder.setExtras(bundle);

                    if(listingSvc.scheduleJob(builder.build())) {
                        lsJobSchld = true;
                    } else {
                        lsJobSchld = false;
                    }
                }
                break;
            case MeetingConstants.NOTIFY_SERVICE:
                if(isSvcRunning(MeetingConstants.NOTIFY_SERVICE)) {
                    Log.d(LOG_TAG, "scheduleNotifyJob");

                    builder = new JobInfo.Builder(lsJobId++, new ComponentName(activity, NotifyService.class));
                    builder.setPeriodic(MeetingConstants.THIRTY_SECONDS);

                    bundle = new PersistableBundle();
                    bundle.putInt(MeetingConstants.PRIOR_TIME_JOB_KEY, MeetingConstants.INIT_DEFAULT); // key/value
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

    private int lsJobId;                // arbitary listing job number, incremented.
    private int nsJobId;
    private long listItemPos;          // position in the listing.
    private boolean lsJobSchld;     // flag, listing job scheduled.
    private boolean nsJobSchld;

    private Intent listingSvcIntent;         // listing service intent.
    private Intent notifySvcIntent;
    private Activity activity;         // the parent activity.
    private MeetingAdapter mca;        // custom cursor adapter.
    private Bundle prefsState;       //
    private ListingService listingSvc;       // the listing service.
    private NotifyService notifySvc;

    private String LOG_TAG = this.getClass().getCanonicalName();
}

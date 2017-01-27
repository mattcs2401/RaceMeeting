package com.mcssoft.racemeeting.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mcssoft.racemeeting.adapter.MeetingAdapter;
import com.mcssoft.racemeeting.database.DatabaseHelper;
import com.mcssoft.racemeeting.database.MeetingProvider;
import com.mcssoft.racemeeting.database.SchemaConstants;
import com.mcssoft.racemeeting.interfaces.IEditMeeting;
import com.mcssoft.racemeeting.interfaces.IItemClickListener;
import com.mcssoft.racemeeting.interfaces.IDeleteMeeting;
import com.mcssoft.racemeeting.interfaces.IItemLongClickListener;
import com.mcssoft.racemeeting.interfaces.INotifier;
import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingPreferences;
import com.mcssoft.racemeeting.utility.MeetingScheduler;
import com.mcssoft.racemeeting.utility.MeetingTime;

import java.util.ArrayList;
import java.util.Set;

import mcssoft.com.racemeeting.R;

public class MainFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>,
               IItemClickListener,
               IItemLongClickListener,
               PopupMenu.OnMenuItemClickListener {

    //<editor-fold defaultstate="collapsed" desc="Region: LifeCycle">
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        meetingScheduler = new MeetingScheduler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.id_toolbar_frag_main);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setMeetingAdapter();
        activityCreated = true;
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();

        setRecyclerView(rootView);

        String records = checkRecordsExist();
        if(records.equals(MeetingConstants.RACE_PRIOR_MEETINGS_EXIST) ||
           records.equals(MeetingConstants.RACE_NO_MEETINGS)) {
            meetingAdapter.setEmptyView(true);
        } else if(Integer.parseInt(records) > 0) {
            meetingAdapter.setEmptyView(false);
            meetingAdapter.setHighliteReq(getHighlightReq());
            meetingAdapter.setShowToday(getShowToday());
            checkServicesRequired();
        }

        if(activityCreated) {
            activityCreated = false;
            getLoaderManager().initLoader(R.integer.meeting_loader, null, this);
        } else {
            getLoaderManager().restartLoader(R.integer.meeting_loader, null, this);
        }
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();
        meetingScheduler.cancelStopAll();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Job Start/Stop">
//    public void onReceivedStartJobListing(PersistableBundle bundle) { }

    public void onReceivedStopJobListing(PersistableBundle results) {
        // The listing service returns back to here when the listing task is done. ListingTask will
        // have set the D_CHG_REQ column to Y on applicable records.
        Set<String> keys = results.keySet();

        if(keys.contains(MeetingConstants.PAST_TIME_JOB_KEY)) {
            if(results.getInt(MeetingConstants.PAST_TIME_JOB_KEY) == R.integer.listing_change_required) {
                getLoaderManager().restartLoader(0, null, this);
            }
        }
    }

//    public void onReceivedStartJobNotify(PersistableBundle bundle) { }

    public void onReceivedStopJobNotify(PersistableBundle results) {
        // The notify service returns back to here when the notify task is done.
        Set<String> keys = results.keySet();

        if(keys.contains(MeetingConstants.PRIOR_TIME_JOB_KEY)) {
            if(results.getInt(MeetingConstants.NOTIFY_REQUIRED_KEY) == R.integer.notify_required) {

                // strip out the "non race value" keys.
                keys.remove(MeetingConstants.PRIOR_TIME_JOB_KEY);
                keys.remove(MeetingConstants.NOTIFY_REQUIRED_KEY);

                ArrayList<String[]> al = new ArrayList<>();

                for(String key : keys) {
                    al.add(results.getStringArray(key));
                }

                ((INotifier) getActivity()).onNotify(al);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Click handlers">
    @Override
    public void onItemClick(View view, int position) {
        this.position = position;
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.listing_context_menu);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        ((IDeleteMeeting) getActivity()).onDeleteMeeting(getDbRowId(position));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu_edit:
                ((IEditMeeting) getActivity()).onEditMeeting(R.integer.edit_meeting, getDbRowId(position));
                break;
            case R.id.context_menu_copy:
                ((IEditMeeting) getActivity()).onEditMeeting(R.integer.copy_meeting, getDbRowId(position));
                //Toast.makeText(getActivity(), "copy menu item clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.context_menu_details:
                ((IEditMeeting) getActivity()).onEditMeeting(R.integer.show_meeting, getDbRowId(position));
                break;
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Loader Callbacks">
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");
        return onCreateWhichLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished");
        meetingAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
        meetingAdapter.swapCursor(null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private void setRecyclerView(View view) {
        if(recyclerView == null) {
            // Will be null when app 1st starts.
            recyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_listing);
        }
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.scrollToPosition(0);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ListingDivider(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(meetingAdapter);
    }

    private void setMeetingAdapter() {
        meetingAdapter = new MeetingAdapter(getHighlightReq(), getShowToday());
        meetingAdapter.setOnItemClickListener(this);
        meetingAdapter.setOnItemLongClickListener(this);
    }

    private boolean getHighlightReq() {
        return MeetingPreferences.getInstance().meetingPastTimePref();
    }

    private boolean getShowToday() {
        // If the 'Show only today' preference is set, this will return true.
        return (MeetingPreferences.getInstance().meetingShowPref()
                .equals(MeetingConstants.RACE_SHOW_MEETINGS_DEFAULT_VAL));
    }

    private int getDbRowId(int position) {
        meetingAdapter.getItemId(position);
        Cursor cursor = meetingAdapter.getCursor();
        return  cursor.getInt(cursor.getColumnIndex(SchemaConstants.COLUMN_ROWID));
    }

    private String checkRecordsExist() {
        String records = MeetingConstants.RACE_NO_MEETINGS;     // simply a default value.
        int count;

        if(getShowToday()) {
            count = queryAllToday().getCount();
            if(count > 0) {
                // records from today exist.
                records = String.valueOf(count);
            } else {
                count = queryAll().getCount();
                if(count > 0) {
                    // previous records exist.
                    records = MeetingConstants.RACE_PRIOR_MEETINGS_EXIST;
                }
            }
        } else {
            count = queryAll().getCount();
            records = String.valueOf(count);
        }
        return  records;
    }

    private Cursor queryAll() {
            return getActivity().getContentResolver().query(Uri.parse(SchemaConstants.CONTENT_URI),
                    DatabaseHelper.getProjection(DatabaseHelper.Projection.DatabaseSchema),
                    null,
                    null,
                    SchemaConstants.SORT_ORDER);
    }

    private Cursor queryAllToday() {
        return getActivity().getContentResolver().query(Uri.parse(SchemaConstants.CONTENT_URI),
                DatabaseHelper.getProjection(DatabaseHelper.Projection.DatabaseSchema),
                SchemaConstants.WHERE_FOR_SHOW, // where column datetime > midnight
                new String[] {String.valueOf(MeetingTime.getInstance().getMidnight())},
                SchemaConstants.SORT_ORDER);
    }

    private void checkServicesRequired() {
        // If any other value than the 0 minutes default is selected.
        if (MeetingPreferences.getInstance().meetingReminderTimePref()
                != R.integer.reminder_min_value) {
            meetingScheduler.startService(R.integer.notify_service);
        } else {
            // 0 minutes default is selected.
            if (meetingScheduler.isSvcRunning(R.integer.notify_service)) {
                meetingScheduler.cancelJobs(R.integer.notify_service);
                meetingScheduler.stopService(R.integer.notify_service);
            }
        }

        // Meeting Past Race Time checkbox is selected.
        if (MeetingPreferences.getInstance().meetingPastTimePref()) {
            meetingScheduler.startService(R.integer.listing_service);
        } else {
            if (meetingScheduler.isSvcRunning(R.integer.listing_service)) {
                meetingScheduler.cancelJobs(R.integer.listing_service);
                meetingScheduler.stopService(R.integer.listing_service);
            }
        }
    }

    private CursorLoader onCreateWhichLoader() {
        if(!getShowToday()) {
            // For the 'Show all' preference.
            return new CursorLoader(getActivity(),
                    MeetingProvider.contentUri,
                    DatabaseHelper.getProjection(DatabaseHelper.Projection.DatabaseSchema),
                    null,
                    null,
                    null);
        } else {
            // For the "Show only today' preference.
            return new CursorLoader(getActivity(),
                    MeetingProvider.contentUri,
                    DatabaseHelper.getProjection(DatabaseHelper.Projection.DatabaseSchema),
                    SchemaConstants.WHERE_FOR_SHOW, // where column datetime > midnight
                    new String[] {String.valueOf(MeetingTime.getInstance().getMidnight())},
                    null);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private Vars">
    private boolean activityCreated;
    private int position;
    private View rootView;
    private RecyclerView recyclerView;
    private MeetingAdapter meetingAdapter;
    private MeetingScheduler meetingScheduler;

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

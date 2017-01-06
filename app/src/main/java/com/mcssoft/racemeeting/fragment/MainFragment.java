package com.mcssoft.racemeeting.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
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

        setMeetingAdapter();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MeetingConstants.MEETING_LOADER, null, this);
        onActivityCreated = true;
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();

        if(onActivityCreated) {
            onActivityCreated = false;
        } else {
            getLoaderManager().restartLoader(MeetingConstants.MEETING_LOADER, null, this);
        }

        setRecyclerView(rootView);
        meetingAdapter.setHighliteReq(getHighlightReq());
        meetingAdapter.setShowToday(getShowToday());
        checkServicesRequired();
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
            if(results.getInt(MeetingConstants.PAST_TIME_JOB_KEY) == MeetingConstants.LISTING_CHANGE_REQUIRED) {
                getLoaderManager().restartLoader(0, null, this);
            }
        }
    }

//    public void onReceivedStartJobNotify(PersistableBundle bundle) { }

    public void onReceivedStopJobNotify(PersistableBundle results) {
        // The notify service returns back to here when the notify task is done.
        Set<String> keys = results.keySet();

        if(keys.contains(MeetingConstants.PRIOR_TIME_JOB_KEY)) {
            if(results.getInt(MeetingConstants.NOTIFY_REQUIRED_KEY) == MeetingConstants.NOTIFY_REQUIRED) {

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
                ((IEditMeeting) getActivity()).onEditMeeting(MeetingConstants.EDIT_MEETING, getDbRowId(position));
                break;
            case R.id.context_menu_copy:
                ((IEditMeeting) getActivity()).onEditMeeting(MeetingConstants.COPY_MEETING, getDbRowId(position));
                //Toast.makeText(getActivity(), "copy menu item clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.context_menu_details:
                ((IEditMeeting) getActivity()).onEditMeeting(MeetingConstants.SHOW_MEETING, getDbRowId(position));
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
        recyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_listing);
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
        meetingAdapter = new MeetingAdapter();
//        meetingAdapter = new MeetingAdapter(getHighlightReq(), getShowToday());
        meetingAdapter.setOnItemClickListener(this);
        meetingAdapter.setOnItemLongClickListener(this);
    }

    private boolean getHighlightReq() {
        return MeetingPreferences.getInstance().meetingPastTimePref();
    }

    private boolean getShowToday() {
        return (Integer.parseInt(MeetingPreferences.getInstance().meetingShowPref()[0])
                == MeetingConstants.MEETING_SHOW_TODAY);
    }

    private int getDbRowId(int position) {
        meetingAdapter.getItemId(position);
        Cursor cursor = meetingAdapter.getCursor();
        return  cursor.getInt(cursor.getColumnIndex(SchemaConstants.COLUMN_ROWID));
    }

    private boolean recordsExist() {
        return (queryAll().getCount() > 0) ? true : false;
    }

    private Cursor queryAll() {
        ContentResolver cr = getActivity().getContentResolver();
        if(getShowToday()) {
            return cr.query(Uri.parse(SchemaConstants.CONTENT_URI),
                    DatabaseHelper.getProjection(DatabaseHelper.Projection.DatabaseSchema),
                    SchemaConstants.WHERE_FOR_SHOW, // where column datetime > midnight
                    new String[] {String.valueOf(MeetingTime.getInstance().getMidnight())},
                    SchemaConstants.SORT_ORDER);
        } else {
            return cr.query(Uri.parse(SchemaConstants.CONTENT_URI),
                    DatabaseHelper.getProjection(DatabaseHelper.Projection.DatabaseSchema),
                    null,
                    null,
                    SchemaConstants.SORT_ORDER);
        }
    }

    private void checkServicesRequired() {
        if(recordsExist()) {
            // If any other value than the 0 minutes default is selected.
            if (MeetingPreferences.getInstance().meetingReminderTimePref()
                    != MeetingConstants.REMINDER_MIN_VALUE) {
                meetingScheduler.startService(MeetingConstants.NOTIFY_SERVICE);
            } else {
                // 0 minutes default is selected.
                if (meetingScheduler.isSvcRunning(MeetingConstants.NOTIFY_SERVICE)) {
                    meetingScheduler.cancelJobs(MeetingConstants.NOTIFY_SERVICE);
                    meetingScheduler.stopService(MeetingConstants.NOTIFY_SERVICE);
                }
            }

            // Meeting Past Race Time checkbox is selected.
            if (MeetingPreferences.getInstance().meetingPastTimePref()) {
                meetingScheduler.startService(MeetingConstants.LISTING_SERVICE);
            } else {
                if (meetingScheduler.isSvcRunning(MeetingConstants.LISTING_SERVICE)) {
                    meetingScheduler.cancelJobs(MeetingConstants.LISTING_SERVICE);
                    meetingScheduler.stopService(MeetingConstants.LISTING_SERVICE);
                }
            }
        }
    }

    private CursorLoader onCreateWhichLoader() {
        if(Integer.parseInt(MeetingPreferences.getInstance().meetingShowPref()[0])
                == MeetingConstants.MEETING_SHOW_ALL) {
            // For the 'Show all meetings' preference.
            return new CursorLoader(getActivity(),
                    MeetingProvider.contentUri,
                    DatabaseHelper.getProjection(DatabaseHelper.Projection.DatabaseSchema),
                    null,
                    null,
                    null);
        } else {
            // For the "Show only today's meetings' preference.
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
    private boolean onActivityCreated;
    private int position;
    private View rootView;
    private RecyclerView recyclerView;
    private MeetingAdapter meetingAdapter;
    private MeetingScheduler meetingScheduler;

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

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
import android.widget.Toast;

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

import mcssoft.com.racemeeting3.R;

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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.id_toolbar_frag_main);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setRecyclerView(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MeetingConstants.MEETING_LOADER, null, this);
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();

        meetingPreferences = new MeetingPreferences(getActivity().getApplicationContext());
        preferences = meetingPreferences.getAllPreferences();

        // If the 'Meeting Time Actions' checkbox is ticked.
        if(preferences.getString(MeetingConstants.TIME_ACTIONS_PREF_KEY).equals("true")) {
            // If there are items listed.
            int action;
            if(recordsExist()) {

                action = Integer.parseInt(preferences.getString(MeetingConstants.TIME_PRIOR_PREF_KEY));

                // If anything other than the "No reminder" preference is selected.
                if (action != MeetingConstants.TIME_PRIOR_PREF_DEFAULT) {
                    meetingScheduler.startService(MeetingConstants.NOTIFY_SERVICE, action);
                } else {
                    // "No reminder." preference is selected.
                    if (meetingScheduler.isSvcRunning(MeetingConstants.NOTIFY_SERVICE)) {
                        meetingScheduler.cancelJobs(MeetingConstants.NOTIFY_SERVICE);
                    }
                }

                action = Integer.parseInt(preferences.getString(MeetingConstants.TIME_PAST_PREF_KEY));

                // If anything other than the "Take no action ..." preference is selected.
                if (action != MeetingConstants.TIME_PAST_PREF_DEFAULT) {
                    meetingScheduler.startService(MeetingConstants.LISTING_SERVICE, action);
                } else {
                    // "Take no action ..." preference is selected, cancel any jobs.
                    if (meetingScheduler.isSvcRunning(MeetingConstants.LISTING_SERVICE)) {
                        meetingScheduler.cancelJobs(MeetingConstants.LISTING_SERVICE);
                    }
                }
            }
        } else {
            // 'Meeting Time Actions' checkbox is not ticked.
            meetingScheduler.cancelStopAll();
        }
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();
        meetingScheduler.cancelStopAll();
        meetingPreferences.destroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    //</editor-fold>

    public void onReceivedStartJobListing(PersistableBundle bundle) {
        // TBA
        //Toast.makeText(getActivity(), "onReceivedStartJobListing", Toast.LENGTH_SHORT).show();
    }

    public void onReceivedStopJobListing(PersistableBundle results) {
        Toast.makeText(getActivity(), "onReceivedStopJobListing", Toast.LENGTH_SHORT).show();

        // The listing service returns back to here when the listing task is done.
        // ListingTask will have set the D_CHG_REQ column to Y on applicable records.

        Log.d(LOG_TAG, "onReceivedStopJob");
        Set<String> keys = results.keySet();

        if(keys.contains(MeetingConstants.PAST_TIME_JOB_KEY)) {
            if(results.getInt(MeetingConstants.PAST_TIME_JOB_KEY) == MeetingConstants.LISTING_CHANGE_REQUIRED) {
                getLoaderManager().restartLoader(0, null, this);
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

                ((INotifier) getActivity()).onNotify(al);
            }
        }
    }

    public void onReceivedStartJobNotify(PersistableBundle bundle) {
        // TBA
        //Toast.makeText(getActivity(), "onReceivedStartJobListing", Toast.LENGTH_SHORT).show();
    }

    public void onReceivedStopJobNotify(PersistableBundle bundle) {
        // The notify service returns back to here when the notify task is done.

        //Toast.makeText(getActivity(), "onReceivedStopJobNotify", Toast.LENGTH_SHORT).show();
    }

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
        return new CursorLoader(getActivity(),
                MeetingProvider.contentUri,
                DatabaseHelper.getDatabaseSchemaProjection(),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "onLoadFinished");
        meetingAdapter.swapCursor(data);
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

        meetingAdapter = new MeetingAdapter();
        meetingAdapter.setOnItemClickListener(this);
        meetingAdapter.setOnItemLongClickListener(this);
        recyclerView.setAdapter(meetingAdapter);
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
        return getActivity().getContentResolver()
                .query(Uri.parse(SchemaConstants.CONTENT_URI),
                        DatabaseHelper.getDatabaseSchemaProjection(),
                        null,
                        null,
                        SchemaConstants.SORT_ORDER);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private Vars">
    private int position;

    private Bundle preferences;
    private RecyclerView recyclerView;
    private MeetingAdapter meetingAdapter;
    private MeetingScheduler meetingScheduler;
    private MeetingPreferences meetingPreferences;

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

package com.mcssoft.racemeeting.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import com.mcssoft.racemeeting.interfaces.IShowMeeting;
import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingPreferences;
import com.mcssoft.racemeeting.utility.MeetingScheduler;

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
//        preferences = MeetingPreferences.getInstance().getAllPreferences();
        meetingPreferences = new MeetingPreferences(getActivity().getApplicationContext());
        preferences = meetingPreferences.getAllPreferences();
        int action;

        // If the 'Meeting Time Actions' checkbox is ticked.
        if(preferences.getString(MeetingConstants.TIME_ACTIONS_PREF_KEY).equals("true")) {
            // If there are items listed.
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

                // If anything other than the "Take no action ..." preference is selected, and records exist.
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        meetingPreferences.destroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    //</editor-fold>

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
                ((IEditMeeting) getActivity()).onEditMeeting(getDbRowId(position));
                break;
            case R.id.context_menu_copy:
                Toast.makeText(getActivity(), "copy menu item clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.context_menu_details:
                ((IShowMeeting) getActivity()).onShowMeeting(getDbRowId(position));
                break;
        }
        return false;
    }

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
//        updateEmptyView();
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

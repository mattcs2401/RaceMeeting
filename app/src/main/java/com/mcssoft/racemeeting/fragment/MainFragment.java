package com.mcssoft.racemeeting.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcssoft.racemeeting.adapter.MeetingAdapter;
import com.mcssoft.racemeeting.database.DatabaseHelper;
import com.mcssoft.racemeeting.database.MeetingProvider;
import com.mcssoft.racemeeting.database.SchemaConstants;
import com.mcssoft.racemeeting.interfaces.IClickListener;
import com.mcssoft.racemeeting.interfaces.IDeleteMeeting;
import com.mcssoft.racemeeting.interfaces.IShowMeeting;
import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingDisplay;
import com.mcssoft.racemeeting.utility.MeetingPreferences;
import com.mcssoft.racemeeting.utility.MeetingTime;
import com.mcssoft.racemeeting.listener.RecyclerTouchListener;

import mcssoft.com.racemeeting3.R;

public class MainFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    //<editor-fold defaultstate="collapsed" desc="Region: LifeCycle">
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // Set the meeting preferences component.
//        if(!MeetingPreferences.instanceExists()) {
//            MeetingPreferences.getInstance(getActivity().getApplicationContext());
//        }

        prefsState = getArguments();

//        // Set the meeting time component.
//        if(!MeetingTime.instanceExists()) {
//            MeetingTime.getInstance(getActivity().getApplicationContext());
//        }
//        // Set the meeting display helper.
//        if(!MeetingDisplay.instanceExists()) {
//            MeetingDisplay.getInstance(getActivity().getApplicationContext());
//        }
//        setHasOptionsMenu(true);
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
    public void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
//        getLoaderManager().restartLoader(MeetingConstants.MEETING_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
//        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
        meetingAdapter.swapCursor(null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private Methods">
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
        recyclerView.setAdapter(meetingAdapter);

        RecyclerTouchListener rtl = new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new IClickListener() {
            @Override
            public void onClick(View view, int position) {
                ((IShowMeeting) getActivity()).onShowMeeting(getDbRowId(position));
            }
            @Override
            public void onLongClick(View view, int position) {
                ((IDeleteMeeting) getActivity()).onDeleteMeeting(getDbRowId(position));
            }
        });

        recyclerView.addOnItemTouchListener(rtl);
    }

    private int getDbRowId(int position) {
        meetingAdapter.getItemId(position);
        Cursor cursor = meetingAdapter.getCursor();
        return  cursor.getInt(cursor.getColumnIndex(SchemaConstants.COLUMN_ROWID));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private Vars">
    private Bundle prefsState;
    private RecyclerView recyclerView;
    private MeetingAdapter meetingAdapter;

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

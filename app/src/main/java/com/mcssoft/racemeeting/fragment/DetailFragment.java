package com.mcssoft.racemeeting.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
;import com.mcssoft.racemeeting.database.DatabaseHelper;
import com.mcssoft.racemeeting.database.MeetingProvider;
import com.mcssoft.racemeeting.database.SchemaConstants;
import com.mcssoft.racemeeting.interfaces.IEditMeeting;
import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingDisplay;
import com.mcssoft.racemeeting.utility.MeetingTime;

import mcssoft.com.racemeeting3.R;

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    //<editor-fold defaultstate="collapsed" desc="Region: LifeCycle">
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        onCreateInitialise(rootView);

        Intent intent = getActivity().getIntent();
        Bundle args = new Bundle();
        rowId = intent.getLongExtra(MeetingConstants.SHOW_SUMMARY, MeetingConstants.INIT_DEFAULT);

        if(rowId != MeetingConstants.INIT_DEFAULT) {
            args.putLong(MeetingConstants.SHOW_SUMMARY, rowId);
            getLoaderManager().initLoader(0, args, this);
        } else {
            // TBA
        }

        return rootView;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Listeners">
    @Override
    public void onClick(View view) {
        Log.d(LOG_TAG, "onClick");
        switch(view.getId()) {
            case R.id.btnEdit:
                ((IEditMeeting) getActivity()).onEditMeeting(rowId);
//                Toast.makeText(getActivity(), "edit buttn click", Toast.LENGTH_SHORT).show();
                break;
            }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Loader Callbacks">
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");

        long rowId = args.getLong(MeetingConstants.SHOW_SUMMARY);
        Uri contentUri = ContentUris.withAppendedId(MeetingProvider.contentUri, rowId);
        return new CursorLoader(getActivity(),
                contentUri,
                DatabaseHelper.getMeetingListItemProjection(),
                SchemaConstants.SELECT_ALL_MLI,
                new String[] {Long.toString(rowId)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished");

        // Get the values from the database.
        tvRaceCity.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_CITY_CODE)));

        String raceCode = cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_CODE));
        tvRaceCode.setText(MeetingDisplay.getInstance().raceCodeToString(raceCode));

        tvRaceNoText.setText("Race:");
        tvRaceNo.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_NUM)));

        tvRaceSelText.setText("Selection:");
        tvRaceSel.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_SEL)));

        long timeInMillis = cursor.getLong(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_DATE_TIME));
        String time = MeetingTime.getInstance().getTimeFromMillis(timeInMillis,false);
        tvRaceTime.setText(time.replace("am","").replace("pm",""));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private void onCreateInitialise(View view) {
        tvRaceCity = (TextView) view.findViewById(R.id.tvRaceCity);
        tvRaceCode = (TextView) view.findViewById(R.id.tvRaceCode);
        tvRaceNo = (TextView) view.findViewById(R.id.tvRaceNo);
        tvRaceNoText = (TextView) view.findViewById(R.id.tvRaceNoText);
        tvRaceSel = (TextView) view.findViewById(R.id.tvRaceSel);
        tvRaceSelText = (TextView) view.findViewById(R.id.tvRaceSelText);
        tvRaceTime = (TextView) view.findViewById(R.id.tvRaceTime);
        btnEdit = (Button) view.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private Vars">
    private TextView tvRaceCity;
    private TextView tvRaceCode;
    private TextView tvRaceNo;
    private TextView tvRaceNoText;
    private TextView tvRaceSel;
    private TextView tvRaceSelText;
    private TextView tvRaceTime;
    private Button btnEdit;
    private long rowId;

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

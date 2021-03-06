package com.mcssoft.racemeeting.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.mcssoft.racemeeting.utility.MeetingResources;
import com.mcssoft.racemeeting.utility.MeetingTime;

import mcssoft.com.racemeeting.R;

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    //<editor-fold defaultstate="collapsed" desc="Region: LifeCycle">
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.id_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.app_name_review);

        onCreateInitialise(rootView);

        Bundle args = getActivity().getIntent().getExtras();
        rowId = ((long[])args.get(MeetingResources.getInstance().getString(R.string.show_summary)))[0];

        if(rowId != R.integer.init_default) {
            args.putLong(MeetingResources.getInstance().getString(R.string.show_summary), rowId);
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
                ((IEditMeeting) getActivity()).onEditMeeting(R.integer.show_meeting, rowId);
                break;
            }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Loader Callbacks">
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");

        long rowId = args.getLong(MeetingResources.getInstance().getString(R.string.show_summary));
        Uri contentUri = ContentUris.withAppendedId(MeetingProvider.contentUri, rowId);
        return new CursorLoader(getActivity(),
                contentUri,
                DatabaseHelper.getProjection(DatabaseHelper.Projection.MeetingListItem),
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
        tvRaceCode.setText(raceCodeToString(raceCode));

        tvRaceNoText.setText("Race:");
        tvRaceNo.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_NUM)));

        tvRaceSelText.setText("Selection:");
        tvRaceSel.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_SEL)));

        long timeInMillis = cursor.getLong(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_DATE_TIME));
        String time = MeetingTime.getInstance().getFormattedTimeFromMillis(timeInMillis);
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

    public String raceCodeToString(String code) {
        String r = "";
        if(code == null || code == "")
            return r;
        else {
            if(code.equals("R"))
                r = "Races";
            else if(code.equals("G"))
                r = "Greyhounds";
            else if(code.equals("T"))
                r = "Trots";
            else if(code.equals("S"))
                r = "Other";
        }
        return r;
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

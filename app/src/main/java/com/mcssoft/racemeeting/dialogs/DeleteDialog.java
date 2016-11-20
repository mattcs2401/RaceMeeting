package com.mcssoft.racemeeting.dialogs;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mcssoft.racemeeting.database.DatabaseHelper;
import com.mcssoft.racemeeting.database.MeetingProvider;
import com.mcssoft.racemeeting.database.SchemaConstants;
import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingTime;

import mcssoft.com.racemeeting3.R;

public class DeleteDialog extends DialogFragment implements View.OnClickListener {

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        rowId = getArguments().getLong(MeetingConstants.DELETE_DIALOG_ROWID);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete, container, false);

        initialise(view);
        return view;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Listener">
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch(viewId) {
            case R.id.btn_cancel:
                break;
            case R.id.btn_delete:
                getActivity().getContentResolver().
                        delete(ContentUris.withAppendedId(MeetingProvider.contentUri, rowId), null, null);
                break;
        }
        dismiss();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private void initialise(View view) {
        enableComponents(view);
        initialiseComponents();
    }

    private void initialiseComponents() {
        Uri contentUri = ContentUris.withAppendedId(MeetingProvider.contentUri, rowId);
        Cursor cursor =  getActivity().getContentResolver()
                .query(contentUri, DatabaseHelper.getMeetingListItemProjection(),
                       SchemaConstants.SELECT_ALL_MLI, new String[] {Long.toString(rowId)}, null);

        String cityCode = cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_CITY_CODE));
        String raceCode = cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_CODE));
        tvRaceCodes.setText(cityCode + " " + raceCode);

        tvRaceNum.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_NUM)));
        tvRaceSel.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_SEL)));

        long timeInMillis = cursor.getLong(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_DATE_TIME));
        String time = MeetingTime.getInstance().getFormattedTimeFromMillis(timeInMillis);
        tvRaceTime.setText(time.replace("am","").replace("pm",""));

        btnDDDelete.setOnClickListener(this);
        btnDDCancel.setOnClickListener(this);
    }

    private void enableComponents(View view) {
        tvRaceCodes = (TextView) view.findViewById(R.id.tvRaceCodes);

        tvRaceNum = (TextView) view.findViewById(R.id.tvRaceNo);
        tvRaceSel = (TextView) view.findViewById(R.id.tvRaceSel);

        tvRaceTime = (TextView) view.findViewById(R.id.tvRaceTime);

        btnDDDelete = (Button) view.findViewById(R.id.btn_delete);
        btnDDCancel = (Button) view.findViewById(R.id.btn_cancel);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private Vars">
    private long rowId;
    private TextView tvRaceCodes;
    private TextView tvRaceNum;
    private TextView tvRaceSel;
    private TextView tvRaceTime;
    private Button btnDDDelete;
    private Button btnDDCancel;
    //</editor-fold>
}

package com.mcssoft.racemeeting.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.mcssoft.racemeeting.database.DatabaseHelper;
import com.mcssoft.racemeeting.database.MeetingProvider;
import com.mcssoft.racemeeting.database.SchemaConstants;
import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingTime;

import mcssoft.com.racemeeting.R;

public class DeleteDialog extends DialogFragment implements  DialogInterface.OnClickListener {

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rowId = getArguments().getLong(MeetingConstants.DELETE_DIALOG_ROWID);
        raceDetails = collateRaceDetails();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        ab.setIcon(R.drawable.ic_action_warning) // holo_light, xhdpi size.
          .setTitle(R.string.lbl_delete_dialog_title)
          .setMessage(raceDetails)
          .setPositiveButton(R.string.lbl_delete, this)
          .setNegativeButton(R.string.lbl_cancel, this);
        return ab.create();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Listener">
    @Override
    public void onClick(DialogInterface dialog, int whichButton) {
        switch (whichButton) {
            case Dialog.BUTTON_POSITIVE:
                getActivity().getContentResolver().
                        delete(ContentUris.withAppendedId(MeetingProvider.contentUri, rowId), null, null);
                break;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private String collateRaceDetails() {
        Uri contentUri = ContentUris.withAppendedId(MeetingProvider.contentUri, rowId);
        Cursor cursor =  getActivity().getContentResolver()
                .query(contentUri, DatabaseHelper.getProjection(DatabaseHelper.Projection.MeetingListItem),
                       SchemaConstants.SELECT_ALL_MLI, new String[] {Long.toString(rowId)}, null);

        StringBuilder sb = new StringBuilder();
        sb.append(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_CITY_CODE)));
        sb.append(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_CODE)));
        sb.append(" ");
        sb.append(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_NUM)));
        sb.append(" ");
        sb.append("Sel");
        sb.append(" ");
        sb.append(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_SEL)));
        sb.append(" ");
        long timeInMillis = cursor.getLong(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_DATE_TIME));
        String time = MeetingTime.getInstance().getFormattedTimeFromMillis(timeInMillis);
        sb.append(time.replace("am","").replace("pm",""));
        return sb.toString();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private Vars">
    private long rowId;
    private String raceDetails;
    //</editor-fold>
}

package com.mcssoft.racemeeting.dialogs;


import mcssoft.com.racemeeting3.R;

import com.mcssoft.racemeeting.database.DatabaseHelper;
import com.mcssoft.racemeeting.database.MeetingProvider;
import com.mcssoft.racemeeting.database.SchemaConstants;
import com.mcssoft.racemeeting.interfaces.IDeleteDialog;
import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DeleteDialog extends DialogFragment
        implements View.OnClickListener {

    public DeleteDialog() {}

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        activity = getActivity();
        rowId = getArguments().getLong(MeetingConstants.DELETE_DIALOG_ROWID);
    }

    @Override
    public Dialog onCreateDialog(Bundle state) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(R.layout.dialog_delete)
                .setCustomTitle(setDialogTitleView())
                .setPositiveButton(null, null)
                .setNegativeButton(null, null);
        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        initialise();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch(viewId) {
            case R.id.btn_cancel:
                break;
            case R.id.btn_delete:
                deleteItem();
                break;
        }
        dialog.dismiss();
    }

    //@SuppressLint("InflateParams")
    private View setDialogTitleView() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_delete_title, null);

        ImageView iv = (ImageView) view.findViewById(R.id.iv_dialog_delete_title);
        iv.setImageResource(R.drawable.ic_warning_black_24dp);

        TextView tv = (TextView) view.findViewById(R.id.tv_dialog_delete_title);
        tv.setText(R.string.lbl_delete_dialog_header);

        return view;
    }

    private void deleteItem() {
        Bundle vals = new Bundle();
        vals.putString(MeetingConstants.DELETE_DIALOG_SELECT_POSITIVE, null);

        int count = getActivity().getContentResolver().delete(ContentUris.withAppendedId(MeetingProvider.contentUri, rowId), null, null);

        if(count == 1) {
            vals.putInt(MeetingConstants.DELETE_ITEM_COUNT_KEY, count);
        }
        ((IDeleteDialog) getActivity()).onDeleteDialog(vals);
    }

    private void initialise() {
        enableComponents();
        initialiseComponents();
    }

    private void initialiseComponents() {
        Cursor cursor = getCursor();

        tvCityCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_CITY_CODE)));
        tvRaceCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_CODE)));
        tvRaceNum.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_NUM)));
        tvRaceSel.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_SEL)));

        long timeInMillis = cursor.getLong(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_DATE_TIME));
        String time = MeetingTime.getInstance().getTimeFromMillis(timeInMillis,false);
        tvRaceTime.setText(time.replace("am","").replace("pm",""));

        btnDDCancel.setOnClickListener(this);
        btnDDDelete.setOnClickListener(this);
    }

    private void enableComponents() {
        tvCityCode = (TextView) dialog.findViewById(R.id.tv_city_code);
        tvRaceCode = (TextView) dialog.findViewById(R.id.tv_race_code);
        tvRaceNum = (TextView) dialog.findViewById(R.id.tv_race_no);
        tvRaceSel = (TextView) dialog.findViewById(R.id.tv_race_sel);
        tvRaceTime = (TextView) dialog.findViewById(R.id.tv_race_time);
        btnDDCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btnDDDelete = (Button) dialog.findViewById(R.id.btn_delete);
    }

    private Cursor getCursor() {
        Uri contentUri = ContentUris.withAppendedId(MeetingProvider.contentUri, rowId);
        return getActivity().getContentResolver()
                .query(contentUri,
                        DatabaseHelper.getMeetingListItemProjection(),
                        SchemaConstants.SELECT_ALL_MLI,
                        new String[] {Long.toString(rowId)},
                        null);
    }

    private long rowId;
    private Dialog dialog;
    private Activity activity;

    TextView tvCityCode;
    TextView tvRaceCode;
    TextView tvRaceNum;
    TextView tvRaceSel;
    TextView tvRaceTime;
    Button btnDDCancel;
    Button btnDDDelete;

}

// See http://developer.android.com/guide/topics/ui/dialogs.html


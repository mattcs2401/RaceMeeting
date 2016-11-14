package com.mcssoft.racemeeting.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcssoft.racemeeting.database.SchemaConstants;
import com.mcssoft.racemeeting.interfaces.IItemClickListener;
import com.mcssoft.racemeeting.utility.MeetingTime;

import mcssoft.com.racemeeting3.R;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingViewHolder> {

    public MeetingAdapter() { }

    @Override
    public MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "onCreateViewHolder");
        if ( parent instanceof RecyclerView ) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.racemeet_row, parent, false);
            view.setFocusable(true);
            return new MeetingViewHolder(view, meetingCliclListener); // don't need to keep a local copy, framework now supplies.
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(MeetingViewHolder holder, int position) {
        Log.d(LOG_TAG, "onBindViewHolder");
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Couldn't move cursor to position: " + position);
        }
        adapaterOnBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        Log.d(LOG_TAG, "getItemId");
        cursor.moveToPosition(position);
        return cursor.getLong(idColNdx);
    }

    public void setOnItemClickListener(IItemClickListener listener) {
        this.meetingCliclListener = listener;
    }

    public void swapCursor(Cursor newCursor) {
        Log.d(LOG_TAG, "swapCursor");
        cursor = newCursor;

        if(cursor != null) {
            cursor.moveToFirst();
            idColNdx = cursor.getColumnIndex(SchemaConstants.COLUMN_ROWID);
            cityCodeColNdx = cursor.getColumnIndex(SchemaConstants.COLUMN_CITY_CODE);
            raceCodeColNdx = cursor.getColumnIndex(SchemaConstants.COLUMN_RACE_CODE);
            raceNumColNdx = cursor.getColumnIndex(SchemaConstants.COLUMN_RACE_NUM);
            raceSelColNdx = cursor.getColumnIndex(SchemaConstants.COLUMN_RACE_SEL);
            dateTimeColNdx = cursor.getColumnIndex(SchemaConstants.COLUMN_DATE_TIME);
            chgReqColNdx = cursor.getColumnIndex(SchemaConstants.COLUMN_D_CHG_REQ);
            notifyDataSetChanged();
        }
    }

    public Cursor getCursor() {
        return cursor;
    }

    private void adapaterOnBindViewHolder(MeetingViewHolder holder, int position) {
        cursor.moveToPosition(position);

        holder.getCityCode().setText(cursor.getString(cityCodeColNdx));
        holder.getRaceCode().setText(cursor.getString(raceCodeColNdx));
        holder.getRaceNo().setText(cursor.getString(raceNumColNdx));
        holder.getRaceSel().setText(cursor.getString(raceSelColNdx));

        String raceTime = MeetingTime.getInstance().getTimeFromMillis(cursor.getLong(dateTimeColNdx),true);
        holder.getRaceTime().setText(raceTime);

        String dChangeReq = cursor.getString(chgReqColNdx);
        holder.getDChange().setText(dChangeReq);

        if (dChangeReq.equals("Y")) {
            view.setBackgroundResource(R.drawable.et_basic);
        } else {
            view.setBackgroundResource(0);
        }
    }

    private View view;
    private Cursor cursor;
    private IItemClickListener meetingCliclListener;

    private int idColNdx;
    private int cityCodeColNdx;
    private int raceCodeColNdx;
    private int raceNumColNdx;
    private int raceSelColNdx;
    private int dateTimeColNdx;
    private int chgReqColNdx;

    private String LOG_TAG = this.getClass().getCanonicalName();
}
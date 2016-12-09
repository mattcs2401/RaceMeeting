package com.mcssoft.racemeeting.adapter;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcssoft.racemeeting.database.SchemaConstants;
import com.mcssoft.racemeeting.interfaces.IItemClickListener;
import com.mcssoft.racemeeting.interfaces.IItemLongClickListener;
import com.mcssoft.racemeeting.utility.MeetingTime;

import mcssoft.com.racemeeting3.R;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingViewHolder> {

    public MeetingAdapter(boolean highliteChgReq) {
        this.highliteReq = highliteChgReq;
    }

    @Override
    public MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "onCreateViewHolder");
        if ( parent instanceof RecyclerView ) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.racemeet_row, parent, false);
            view.setFocusable(true);
            // don't need to keep a local copy, framework now supplies.
            return new MeetingViewHolder(view, itemClickListener, itemLongClickListener);
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
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(IItemLongClickListener listener) {
        this.itemLongClickListener = listener;
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

    public void setHighliteReqFromPrefs(boolean highliteReq) {
        this.highliteReq = highliteReq;
    }

    private void adapaterOnBindViewHolder(MeetingViewHolder holder, int position) {
        cursor.moveToPosition(position);

        holder.getCityCode().setText(cursor.getString(cityCodeColNdx));
        holder.getRaceCode().setText(cursor.getString(raceCodeColNdx));
        holder.getRaceNo().setText(cursor.getString(raceNumColNdx));
        holder.getRaceSel().setText(cursor.getString(raceSelColNdx));

        String raceTime = MeetingTime.getInstance().getFormattedTimeFromMillis(cursor.getLong(dateTimeColNdx));
        holder.getRaceTime().setText(raceTime);

        if(cursor.getString(chgReqColNdx).equals("Y") && (!highliteReq)) {
            holder.getCityCode().setTextColor(Color.RED);
            holder.getRaceCode().setTextColor(Color.RED);
            holder.getRaceNo().setTextColor(Color.RED);
            holder.getRaceSel().setTextColor(Color.RED);
            holder.getRaceTime().setTextColor(Color.RED);

        } else {
            holder.getCityCode().setTextColor(Color.BLACK);
            holder.getRaceCode().setTextColor(Color.BLACK);
            holder.getRaceNo().setTextColor(Color.BLACK);
            holder.getRaceSel().setTextColor(Color.BLACK);
            holder.getRaceTime().setTextColor(Color.BLACK);
        }
    }

    private View view;
    private Cursor cursor;
    private boolean highliteReq;

    private int idColNdx;
    private int cityCodeColNdx;
    private int raceCodeColNdx;
    private int raceNumColNdx;
    private int raceSelColNdx;
    private int dateTimeColNdx;
    private int chgReqColNdx;

    private IItemClickListener itemClickListener;
    private IItemLongClickListener itemLongClickListener;

    private String LOG_TAG = this.getClass().getCanonicalName();
}
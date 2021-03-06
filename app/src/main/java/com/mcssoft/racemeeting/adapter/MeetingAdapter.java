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
import com.mcssoft.racemeeting.utility.MeetingPreferences;
import com.mcssoft.racemeeting.utility.MeetingResources;
import com.mcssoft.racemeeting.utility.MeetingTime;

import mcssoft.com.racemeeting.R;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingViewHolder> {

    public MeetingAdapter(boolean highliteChgReq, boolean showToday) {
        this.highliteReq = highliteChgReq;
        this.showToday = showToday;
    }

    @Override
    public MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "onCreateViewHolder");
        // Note: don't need to keep a local copy of MeetingViewHolder, framework now supplies.
        if ( parent instanceof RecyclerView ) {
            if(!emptyView) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.racemeet_row, parent, false);
                view.setFocusable(true);
                // don't need to keep a local copy, framework now supplies.
                return new MeetingViewHolder(view, itemClickListener, itemLongClickListener);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.racemeet_empty, parent, false);
                return new MeetingViewHolder(view);
            }
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(MeetingViewHolder holder, int position) {
        Log.d(LOG_TAG, "onBindViewHolder");
        if(!emptyView) {
            adapaterOnBindViewHolder(holder, position);
        } else {
            holder.getEmptyText().getText().toString();
        }
    }

    @Override
    public int getItemCount() {
        if(emptyView) {
            return  1; // need to do this so the onCreateViewHolder fires.
        } else {
            if(cursor != null) {
                return cursor.getCount();
            } else {
                return 0;
            }
        }
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
        // set current listing data.
        cursor = newCursor;

        // set column indexes for use by the (adapter)OnBindViewHolder.
        if((cursor != null) && !(emptyView)) {
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

    public void setHighliteReq(boolean highliteReq) {
        this.highliteReq = highliteReq;
    }

    public void setShowToday(boolean showToday) { this.showToday = showToday; }

    public void setEmptyView(boolean emptyView) { this.emptyView = emptyView; }

    private void adapaterOnBindViewHolder(MeetingViewHolder holder, int position) {
        cursor.moveToPosition(position);

        holder.getCityCode().setText(cursor.getString(cityCodeColNdx));
        holder.getRaceCode().setText(cursor.getString(raceCodeColNdx));
        holder.getRaceNo().setText(cursor.getString(raceNumColNdx));
        holder.getRaceSel().setText(cursor.getString(raceSelColNdx));

        long lRaceTime = cursor.getLong(dateTimeColNdx);
        String sRaceTime = MeetingTime.getInstance().getFormattedTimeFromMillis(lRaceTime);

        holder.getRaceTime().setText(sRaceTime);

        if(cursor.getString(chgReqColNdx).equals("Y") && (highliteReq)) {
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

        if(showToday == false) {
            // 'Show all' preference selected.
            if(MeetingPreferences.getInstance().meetingRaceShowDatePref()) {
                // the 'Include date' preference is selected
                String date = MeetingTime.getInstance().getFormattedDateFromMillis(lRaceTime);
                holder.getRaceDay().setText(date);
            } else {
                if (MeetingTime.getInstance().isTimeToday(lRaceTime)) {
                    holder.getRaceDay().setBackgroundResource(0);
                    holder.getRaceDay().setText(null);
                } else {
                    holder.getRaceDay().setBackgroundResource(R.drawable.tv_day_outline);
                    holder.getRaceDay().setText(MeetingResources.getInstance()
                            .getString(R.string.meeting_show_previous));
                }
            }
        }
    }

    private View view;
    private Cursor cursor;
    private boolean showToday;
    private boolean highliteReq;
    private boolean emptyView;

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
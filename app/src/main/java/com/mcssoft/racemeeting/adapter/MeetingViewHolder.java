package com.mcssoft.racemeeting.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import mcssoft.com.racemeeting3.R;

public class MeetingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public MeetingViewHolder(View view) {
        super(view);
//        this.view = view;
        setViewHolderComponents(view);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int adapterPosition = getAdapterPosition();
//        mCursor.moveToPosition(adapterPosition);
//        int dateColumnIndex = mCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
//        mClickHandler.onClick(mCursor.getLong(dateColumnIndex), this);
    }

    public TextView getCityCode() {
        return tvCityCode;
    }

    public TextView getRaceCode() {
        return tvRaceCode;
    }

    public TextView getRaceNo() {
        return tvRaceNo;
    }

    public TextView getRaceSel() {
        return tvRaceSel;
    }

    public TextView getRaceTime() {
        return tvRaceTime;
    }

    public TextView getDChange() {
        return tvDChange;
    }

    private void setViewHolderComponents(View view) {
        tvCityCode = (TextView) view.findViewById(R.id.tv_city_code);
        tvRaceCode = (TextView) view.findViewById(R.id.tv_race_code);
        tvRaceNo = (TextView) view.findViewById(R.id.tv_race_no);
        tvRaceSel = (TextView) view.findViewById(R.id.tv_race_sel);
        tvRaceTime = (TextView) view.findViewById(R.id.tv_race_time);
        tvDChange = (TextView) view.findViewById(R.id.tv_dchange_testing);
    }

//    private View view;
    private TextView tvCityCode;
    private TextView tvRaceCode;
    private TextView tvRaceNo;
    private TextView tvRaceSel;
    private TextView tvRaceTime;
    private TextView tvDChange;
}


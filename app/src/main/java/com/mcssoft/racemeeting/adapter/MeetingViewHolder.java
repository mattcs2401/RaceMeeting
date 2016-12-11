package com.mcssoft.racemeeting.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mcssoft.racemeeting.interfaces.IItemClickListener;
import com.mcssoft.racemeeting.interfaces.IItemLongClickListener;

import mcssoft.com.racemeeting3.R;

public class MeetingViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener,
                   View.OnLongClickListener {

    public MeetingViewHolder(View view, IItemClickListener listener, IItemLongClickListener longListener) {
        super(view);
        // Set the ViewHolder components.
        tvCityCode = (TextView) view.findViewById(R.id.tv_city_code);
        tvRaceCode = (TextView) view.findViewById(R.id.tv_race_code);
        tvRaceNo = (TextView) view.findViewById(R.id.tv_race_no);
        tvRaceSel = (TextView) view.findViewById(R.id.tv_race_sel);
        tvRaceTime = (TextView) view.findViewById(R.id.tv_race_time);
        tvDChange = (TextView) view.findViewById(R.id.tv_race_day);

        // Set the listeners.
        itemClickListener = listener;
        view.setOnClickListener(this);
        itemLongClickListener = longListener;
        view.setOnLongClickListener(this);
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Listeners">
    @Override
    public void onClick(View view) {
        if(itemClickListener != null){
            itemClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(itemLongClickListener != null){
            itemLongClickListener.onItemLongClick(view, getAdapterPosition());
            return true;
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Accessors">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
    private TextView tvCityCode;
    private TextView tvRaceCode;
    private TextView tvRaceNo;
    private TextView tvRaceSel;
    private TextView tvRaceTime;
    private TextView tvDChange;

    private IItemClickListener itemClickListener;
    private IItemLongClickListener itemLongClickListener;
    //</editor-fold>
}


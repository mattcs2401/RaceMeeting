package com.mcssoft.racemeeting.interfaces;

import android.view.View;

/**
 * Interface between the RecyclerTouchListener and the MainFragment (for recycler view);
 */
public interface IClickListener {
    void onClick(View view,int position);
    void onLongClick(View view, int position);
}
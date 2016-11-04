package com.mcssoft.racemeeting.interfaces;

import android.view.View;

/**
 * Interface between the RecyclerTouchListener and the ListingFragment (for recycler view);
 */
public interface IClickListener {
    void onClick(View view,int position);
    void onLongClick(View view, int position);
}
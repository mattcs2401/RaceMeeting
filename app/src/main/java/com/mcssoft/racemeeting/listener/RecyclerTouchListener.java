package com.mcssoft.racemeeting.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.mcssoft.racemeeting.interfaces.IClickListener;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private IClickListener clicklistener;
    private GestureDetector gestureDetector;
    private RecyclerView recyclerView;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final IClickListener clicklistener){
        this.recyclerView = recyclerView;
        this.clicklistener = clicklistener;

        gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(child != null && clicklistener != null){
                    clicklistener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent me) {
        View child = rv.findChildViewUnder(me.getX(),me.getY());
        if(child != null && clicklistener != null && gestureDetector.onTouchEvent(me)){
            clicklistener.onClick(child, rv.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent me) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
}


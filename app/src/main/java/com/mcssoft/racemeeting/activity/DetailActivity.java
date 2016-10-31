package com.mcssoft.racemeeting.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mcssoft.racemeeting.fragment.DetailFragment;
import com.mcssoft.racemeeting.interfaces.IEditMeeting;
import com.mcssoft.racemeeting.utility.MeetingConstants;

import mcssoft.com.racemeeting3.R;

public class DetailActivity extends Activity implements IEditMeeting {

    //<editor-fold defaultstate="collapsed" desc="Region: LifeCycle">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail); // <-- simple FrameLayout

        Bundle args = getIntent().getExtras();
        detailFragment = new DetailFragment();

        if (savedInstanceState == null) {
            detailFragment.setArguments(args);

            getFragmentManager().beginTransaction()
                    .add(R.id.detail_container, detailFragment, MeetingConstants.DEFAULT_DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            detailFragment = (DetailFragment) getFragmentManager()
                    .getFragment(savedInstanceState, MeetingConstants.DEFAULT_DETAIL_FRAGMENT_TAG);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - IEditMeeting">
    /**
     * Start the Edit activity.
     * @param id Database row id: value==0 new meeting, else the database row id used in a select query.
     */
    @Override
    public void onEditMeeting(long id) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(MeetingConstants.EDIT_EXISTING, id);
        intent.setAction(MeetingConstants.EDIT_ACTION_EXISTING);
        startActivity(intent);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private Vars">
    private DetailFragment detailFragment;

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>

}

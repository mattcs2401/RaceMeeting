package com.mcssoft.racemeeting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mcssoft.racemeeting.fragment.DetailFragment;
import com.mcssoft.racemeeting.interfaces.IEditMeeting;
import com.mcssoft.racemeeting.utility.MeetingResources;

import mcssoft.com.racemeeting.R;

public class DetailActivity extends AppCompatActivity implements IEditMeeting {

    //<editor-fold defaultstate="collapsed" desc="Region: LifeCycle">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail); // <-- simple FrameLayout

        Bundle args = getIntent().getExtras();
        if(args.isEmpty()) { } // trial and error ?? required if this is called from a Notification.

        detailFragment = new DetailFragment();

        if (savedInstanceState == null) {
            detailFragment.setArguments(args);

            getFragmentManager().beginTransaction()
                    .add(R.id.detail_container, detailFragment,
                            MeetingResources.getInstance().getString(R.string.detail_fragment_tag))
                    .commit();
        } else {
            detailFragment = (DetailFragment) getFragmentManager()
                    .getFragment(savedInstanceState,
                            MeetingResources.getInstance().getString(R.string.detail_fragment_tag));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - IEditMeeting">
    @Override
    public void onEditMeeting(int editType, long... dbRowId) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(MeetingResources.getInstance().getString(R.string.edit_existing_or_copy), dbRowId);
        intent.setAction(MeetingResources.getInstance().getString(R.string.edit_action_existing));
        startActivity(intent);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private Vars">
    private DetailFragment detailFragment;

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>

}

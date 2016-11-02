package com.mcssoft.racemeeting.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import com.mcssoft.racemeeting.fragment.DeleteFragment;
import com.mcssoft.racemeeting.utility.MeetingConstants;

import mcssoft.com.racemeeting3.R;

public class DeleteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete); // <-- simple FrameLayout

        Bundle args = getIntent().getExtras();
        deleteFragment = new DeleteFragment();
        fragmentManager = getFragmentManager();

        if (savedInstanceState == null) {
            deleteFragment.setArguments(args);

            fragmentManager.beginTransaction()
                    .add(R.id.delete_container, deleteFragment, MeetingConstants.DEFAULT_DELETE_FRAGMENT_TAG)
                    .commit();
        } else {
            deleteFragment = (DeleteFragment) fragmentManager
                    .getFragment(savedInstanceState, MeetingConstants.DEFAULT_EDIT_FRAGMENT_TAG);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fragmentManager.putFragment(outState, MeetingConstants.DEFAULT_EDIT_FRAGMENT_TAG, deleteFragment);
    }

//    @Override
//    public void onBackPressed() {
//        // Note: Implemented to try an overcome the issue where the back button is pressed in one of
//        // the codes fragments and the navigation jumped back to the main listing.
//        int count = fragmentManager.getBackStackEntryCount();
//
//        if (count == 0) {
//            super.onBackPressed();
//        } else {
//            fragmentManager.popBackStack();
//        }
//    }

    private DeleteFragment deleteFragment;
    private FragmentManager fragmentManager;

    private String LOG_TAG = this.getClass().getCanonicalName();
}

package com.mcssoft.racemeeting.activity;

import android.app.FragmentTransaction;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mcssoft.racemeeting.dialogs.DeleteDialog;
import com.mcssoft.racemeeting.fragment.ListingFragment;
import com.mcssoft.racemeeting.interfaces.IDeleteMeeting;
import com.mcssoft.racemeeting.interfaces.IEditMeeting;
import com.mcssoft.racemeeting.interfaces.IShowMeeting;
import com.mcssoft.racemeeting.utility.MeetingConstants;

import mcssoft.com.racemeeting3.R;

public class MainActivity extends AppCompatActivity
    implements IEditMeeting, IShowMeeting, IDeleteMeeting {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            listingFragment = new ListingFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.listing_container, listingFragment, null)
                    .commit();
        } else {
            // TBA
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listing_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {
            case R.id.preference_settings:
                Intent paIntent = new Intent(this, PreferencesActivity.class);
                startActivityForResult(paIntent, MeetingConstants.PREFERENCES_ACTIVITY_REQUEST_CODE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - IEditMeeting">
    @Override
    public void onEditMeeting(long id) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(MeetingConstants.EDIT_NEW, id);
        intent.setAction(MeetingConstants.EDIT_ACTION_NEW);
        startActivity(intent);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - IShowMeeting">
    @Override
    public void onShowMeeting(long id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MeetingConstants.SHOW_SUMMARY, id);
        startActivity(intent);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - IDeleteMeeting">
    public void onDeleteMeeting(long rowId) {
        DialogFragment df = new DeleteDialog();

        Bundle args = new Bundle();
        args.putLong(MeetingConstants.DELETE_DIALOG_ROWID, rowId);
        df.setArguments(args);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        df.show(ft, MeetingConstants.DEFAULT_DELETE_DIALOG_FRAGMENT_TAG);
    }
    //</editor-fold>


    private ListingFragment listingFragment;
    private String LOG_TAG = this.getClass().getCanonicalName();
}

package com.mcssoft.racemeeting.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mcssoft.racemeeting.dialogs.DeleteDialog;
import com.mcssoft.racemeeting.fragment.MainFragment;
import com.mcssoft.racemeeting.interfaces.IDeleteMeeting;
import com.mcssoft.racemeeting.interfaces.IEditMeeting;
import com.mcssoft.racemeeting.interfaces.IShowMeeting;
import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingTime;

import mcssoft.com.racemeeting3.R;

public class MainActivity extends AppCompatActivity
    implements IEditMeeting, IShowMeeting, IDeleteMeeting {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialise();

        if(savedInstanceState == null) {
            fragmentManager.beginTransaction()
                           .replace(R.id.listing_container, mainFragment, null)
                           .addToBackStack(null)
                           .commit();
        } else {
            // TBA
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listing_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {
            case R.id.toolbar_menu_insert:
                onEditMeeting(0, MeetingConstants.NEW_MEETING);
                break;
            case R.id.toolbar_preference_settings:
                Intent paIntent = new Intent(this, PreferencesActivity.class);
                startActivityForResult(paIntent, MeetingConstants.PREFERENCES_ACTIVITY_REQUEST_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - IEditMeeting">
    @Override
    public void onEditMeeting(int editType, long dbRowId) {
        Intent intent = new Intent(this, EditActivity.class);
        switch (editType) {
            case MeetingConstants.NEW_MEETING:
                intent.setAction(MeetingConstants.EDIT_ACTION_NEW);
                break;
            case MeetingConstants.EDIT_MEETING:
                intent.putExtra(MeetingConstants.EDIT_EXISTING, dbRowId);
                intent.setAction(MeetingConstants.EDIT_ACTION_EXISTING);
                break;
            case MeetingConstants.COPY_MEETING:
                intent.putExtra(MeetingConstants.EDIT_COPY, dbRowId);
                intent.setAction(MeetingConstants.EDIT_ACTION_COPY);
                break;
        }
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

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - Utility">
    private void initialise() {
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        mainFragment = new MainFragment();

        if(!MeetingTime.instanceExists()) {
            MeetingTime.getInstance(getApplicationContext());
        }
    }
    //</editor-fold>

    private MainFragment mainFragment;
    private FragmentManager fragmentManager;
    private String LOG_TAG = this.getClass().getCanonicalName();
}

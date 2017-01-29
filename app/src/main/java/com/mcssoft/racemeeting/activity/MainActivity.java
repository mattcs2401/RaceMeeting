package com.mcssoft.racemeeting.activity;

import android.app.FragmentTransaction;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mcssoft.racemeeting.dialogs.DeleteDialog;
import com.mcssoft.racemeeting.fragment.MainFragment;
import com.mcssoft.racemeeting.interfaces.IDeleteMeeting;
import com.mcssoft.racemeeting.interfaces.IEditMeeting;
import com.mcssoft.racemeeting.interfaces.INotifier;
import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingPreferences;
import com.mcssoft.racemeeting.utility.MeetingResources;
import com.mcssoft.racemeeting.utility.MeetingTime;

import java.util.ArrayList;

import mcssoft.com.racemeeting.R;

public class MainActivity extends AppCompatActivity
    implements IEditMeeting, IDeleteMeeting, INotifier {

    //<editor-fold defaultstate="collapsed" desc="Region: Lifecycle">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialise();

        if(savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                                .replace(R.id.listing_container, mainFragment,
                                        MeetingConstants.DEFAULT_LISTING_FRAGMENT_TAG)
                                .addToBackStack(null)
                                .commit();
        } else {
            // TBA ...
            mainFragment = (MainFragment) getFragmentManager()
                    .getFragment(savedInstanceState, MeetingConstants.DEFAULT_LISTING_FRAGMENT_TAG);
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
                onEditMeeting(R.integer.new_meeting);
                break;
            case R.id.toolbar_preference_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MeetingTime.getInstance().destroy();
        MeetingPreferences.getInstance().destroy();
        MeetingResources.getInstance().destroy();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - IEditMeeting">
    @Override
    public void onEditMeeting(int editType, long... dbRowId) {
        Intent intent = new Intent(this, EditActivity.class);
        switch (editType) {
            case R.integer.new_meeting:
                intent.setAction(MeetingConstants.EDIT_ACTION_NEW);
                break;
            case R.integer.edit_meeting:
                intent.putExtra(MeetingConstants.EDIT_EXISTING_OR_COPY, dbRowId);
                intent.setAction(MeetingConstants.EDIT_ACTION_EXISTING);
                break;
            case R.integer.copy_meeting:
                intent.putExtra(MeetingConstants.EDIT_EXISTING_OR_COPY, dbRowId);
                intent.setAction(MeetingConstants.EDIT_ACTION_COPY);
                break;
            case R.integer.show_meeting:
                intent = new Intent(this, DetailActivity.class);
                intent.putExtra(MeetingConstants.SHOW_SUMMARY, dbRowId);
                break;
        }
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

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - INotifier">
    public void onNotify(ArrayList<String[]> notifyValues) {
        Notification notification = new Notification.Builder(this)
                .setContentTitle(MeetingConstants.CONTENT_TITLE)
                .setContentText(notificationContentText(notifyValues))
                .setSmallIcon(R.drawable.r_icon_24dp)
                .setLargeIcon(getBitmap(R.drawable.r_icon_32dp))  // testing API 22 doesn't do icon
                .addAction(getNotificationAction(notifyValues))
                .setVibrate(new long [] {1000,1000,1000,1000}) // wait 1 sec, vibrate 1 sec (twice).
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - Utility">
    private void initialise() {
        setContentView(R.layout.activity_main);
        mainFragment = new MainFragment();
        MeetingResources.getInstance(this);
        MeetingPreferences.getInstance(this);
        MeetingTime.getInstance(this);
    }

    private String notificationContentText(ArrayList<String[]> notifyValues) {
        String [] contents = notifyValues.get(0);
        StringBuilder contentText = new StringBuilder();

        contentText.append(contents[1]);  // city code
        contentText.append(contents[2]);  // race code
        contentText.append(" ");
        contentText.append(contents[3]);  // race number
        contentText.append(" ");
        contentText.append(contents[4]);  // race selection
        contentText.append(" ");
        long timeMillis = Long.parseLong(contents[5]);  // race time
        contentText.append(MeetingTime.getInstance().getFormattedTimeFromMillis(timeMillis));
        return contentText.toString();
    }

    // https://developer.android.com/reference/android/graphics/BitmapFactory.html
    // https://developer.android.com/reference/android/graphics/BitmapFactory.Options.html
    private Bitmap getBitmap(int image) {
        return BitmapFactory.decodeResource(getResources(), image); //, options);
    }

    // https://developer.android.com/reference/android/app/Notification.Action.Builder.html
    private Notification.Action getNotificationAction(ArrayList<String[]> notifyValues) {

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MeetingConstants.SHOW_SUMMARY, Long.parseLong((notifyValues.get(0)[0])));

        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Notification.Action.Builder builder = new Notification.Action.Builder(0, "Detail", pIntent);

        return builder.build();
    }
    //</editor-fold>

    private MainFragment mainFragment;
    private String LOG_TAG = this.getClass().getCanonicalName();
}

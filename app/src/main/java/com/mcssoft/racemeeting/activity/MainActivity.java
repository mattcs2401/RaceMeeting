package com.mcssoft.racemeeting.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.mcssoft.racemeeting.utility.MeetingTime;

import java.util.ArrayList;

import mcssoft.com.racemeeting3.R;

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
    }
    //</editor-fold>

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
            case MeetingConstants.SHOW_MEETING:
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
        // TODO - rework this for potential multiple notifications.
        // TODO - content title and text values.
        // TODO - option to go to a "review" screen ? or edit ?

        Notification notification = new Notification.Builder(this)
                .setContentTitle(MeetingConstants.CONTENT_TITLE)
                .setContentText(notificationContentText(notifyValues))
                .setSmallIcon(R.drawable.r_icon_24dp)
                .setLargeIcon(getBitmap(R.drawable.r_icon_32dp))  // testing API 22 doesn't do icon
                .addAction(getNotificationAction(notifyValues))
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - Utility">
    private void initialise() {
        setContentView(R.layout.activity_main);
        mainFragment = new MainFragment();
        MeetingPreferences.getInstance(this);
        MeetingTime.getInstance(this);
    }

    private String notificationContentText(ArrayList<String[]> notifyValues) {
        //0-id, 1-city, 2-code, 3-num, 4-sel, 5-time
        // TODO - this needs work, testing only.
        String [] contents = notifyValues.get(0);
        StringBuilder contentText = new StringBuilder();

        contentText.append(contents[1]);
        contentText.append(contents[2]);
        contentText.append(" ");
        contentText.append(contents[3]);
        contentText.append(" ");
        contentText.append(contents[4]);
        contentText.append(" ");
        //int timeMillis = Integer.parseInt(contents[5]);
        long timeMillis = Long.parseLong(contents[5]);
        contentText.append(MeetingTime.getInstance().getFormattedTimeFromMillis(timeMillis));
        return contentText.toString();
    }

    // https://developer.android.com/reference/android/graphics/BitmapFactory.html
    // https://developer.android.com/reference/android/graphics/BitmapFactory.Options.html
    private Bitmap getBitmap(int image) {
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        //BitmapFactory.decodeResource(getResources(), image, options);
        //int imageHeight = options.outHeight;
        //int imageWidth = options.outWidth;
        //String imageType = options.outMimeType;
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

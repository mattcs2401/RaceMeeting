package com.mcssoft.racemeeting.fragment;


import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mcssoft.racemeeting.database.DatabaseHelper;
import com.mcssoft.racemeeting.database.MeetingProvider;
import com.mcssoft.racemeeting.database.SchemaConstants;
import com.mcssoft.racemeeting.dialogs.TimePickDialog;
import com.mcssoft.racemeeting.interfaces.IShowCodes;
import com.mcssoft.racemeeting.utility.MeetingEditText;
import com.mcssoft.racemeeting.utility.MeetingConstants;
import com.mcssoft.racemeeting.utility.MeetingTime;

import mcssoft.com.racemeeting3.R;

public class EditFragment extends Fragment
    implements View.OnClickListener,
               View.OnTouchListener,
               TimePickerDialog.OnTimeSetListener,
               TextView.OnEditorActionListener,
               LoaderManager.LoaderCallbacks<Cursor> {

    //<editor-fold defaultstate="collapsed" desc="Region: LifeCycle">
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.id_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        onCreateInitialise(rootView);

        Intent intent = getActivity().getIntent();
        launchAction = intent.getAction();

        if(launchAction.equals(MeetingConstants.EDIT_ACTION_NEW)) {
            actionBar.setTitle(R.string.app_name_new);
            updateBackground(MeetingConstants.NEW_MEETING);
            timeInMillis = MeetingTime.getInstance().getCurrentTimeInMillis();
            updateRaceTime(timeInMillis);

            if(checkUseRaceCodePreference()) {
                setRaceCodeFromPreference();
            }
        } else if(launchAction.equals(MeetingConstants.EDIT_ACTION_EXISTING)) {
            actionBar.setTitle(R.string.app_name_edit);
            updateBackground(MeetingConstants.EDIT_MEETING);

            Bundle args = new Bundle();
            long rowId = intent.getLongExtra(MeetingConstants.EDIT_EXISTING, MeetingConstants.INIT_DEFAULT);

            if(rowId != MeetingConstants.INIT_DEFAULT) {
                args.putLong(MeetingConstants.EDIT_EXISTING, rowId);
                getLoaderManager().initLoader(0, args, this);
            } else {
                // TBA
            }
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            // Restore the frament's state here.
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the fragment's state here.
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Listeners">
    @Override
    public void onClick(View view) {
        Log.d(LOG_TAG, "onClick");
        switch(view.getId()) {
            case R.id.btnSave:
                if(checkFields()) {
                    // save values and tell activity to finish.
                    saveValuesForResult();
                    ((IShowCodes) getActivity()).onFinish();
                    break;
                }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d(LOG_TAG, "onTouch");
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            IShowCodes isc = (IShowCodes) getActivity(); // callback to EditActivity.

            switch (view.getId()) {
                case R.id.etCityCode:
                    isc.onShowCodes(MeetingConstants.CITY_CODES_FRAGMENT_ID, view);
                    break;
                case R.id.etRaceCode:
                    isc.onShowCodes(MeetingConstants.RACE_CODES_FRAGMENT_ID, view);
                    break;
                case R.id.etRaceTime:
                    isc = null;
                    showTimePicker();
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        // Note: this doesn't pick up a keyboard dismiss if done not pressed.
        boolean handled = false;
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                // must call this, doesn't hide by default.
                hideSoftKeyboard(view);

                // check field contains something.
                if(view.getEditableText().length() > 0) {
                    view.setBackgroundResource(R.drawable.et_basic_green_outline);
                }
                handled = true;
                break;
        }
        return handled;
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        int [] time = new int[] {hour, minute};
        timeInMillis = MeetingTime.getInstance().getMillisFromTimeComponent(time);
        updateRaceTime(timeInMillis);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Loader Callbacks">
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");

        long rowId = args.getLong(MeetingConstants.EDIT_EXISTING);
        Uri contentUri = ContentUris.withAppendedId(MeetingProvider.contentUri, rowId);
        return new CursorLoader(getActivity(),
                contentUri,
                DatabaseHelper.getMeetingListItemProjection(),
                SchemaConstants.SELECT_ALL_MLI,
                new String[] {Long.toString(rowId)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished");

        // Get the values from the database.
        etCityCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_CITY_CODE)));
        etRaceCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_CODE)));
        etRaceNum.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_NUM)));
        etRaceSel.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_RACE_SEL)));

        timeInMillis = cursor.getLong(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_DATE_TIME));
        String time = MeetingTime.getInstance().getTimeFromMillis(timeInMillis,false);
        etRaceTime.setText(time.replace("am","").replace("pm",""));

        dChange = cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_D_CHG_REQ));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Dialog">
    /**
     * Show the time picker dialog.
     */
    private void showTimePicker() {
//        if(meetKbd.isVisible()) meetKbd.hide();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment df = new TimePickDialog();

        Bundle args = new Bundle();
        int[] hourMin;

        hourMin = MeetingTime.getInstance().getTimeComponent(timeInMillis,MeetingConstants.TIME_COMPONENT_HOUR_MINUTE);

        args.putInt(MeetingConstants.HOUR, hourMin[0]);
        args.putInt(MeetingConstants.MINS, hourMin[1]);

        args.putString(MeetingConstants.TIME_FORMAT, MeetingTime.getInstance().getTimeFormatPrefKey());

        df.setArguments(args);
        df.show(ft, MeetingConstants.TIME_PICKER_TAG);
    }
    //</editor-fold>

    ///<editor-fold defaultstate="collapsed" desc="Region: Utility">
    /**
     * Save the edit values to the database.
     * @return Intent with either:
     * 1) For new entry; key==EDIT_ACTION_NEW, value==row id in database.
     * 2) For existing entry:  key==EDIT_ACTION_EXISTING, value==row id in database.
     */
    private Intent saveValuesForResult() {
        //Log.d(LOG_TAG, "saveValuesForResult");
        Intent intent = new Intent();
        long dbRowId = MeetingConstants.INIT_DEFAULT;
        ContentValues contentValues = new ContentValues();

        contentValues.put(SchemaConstants.COLUMN_CITY_CODE, etCityCode.getText().toString());
        contentValues.put(SchemaConstants.COLUMN_RACE_CODE, etRaceCode.getText().toString());
        contentValues.put(SchemaConstants.COLUMN_RACE_NUM, etRaceNum.getText().toString());
        contentValues.put(SchemaConstants.COLUMN_RACE_SEL, etRaceSel.getText().toString());
        contentValues.put(SchemaConstants.COLUMN_DATE_TIME, timeInMillis);

        if(launchAction.equals(MeetingConstants.EDIT_ACTION_NEW)) {
            contentValues.put(SchemaConstants.COLUMN_D_CHG_REQ, "N");
            contentValues.put(SchemaConstants.COLUMN_NOTIFIED, "N");

            Uri itemUri = getActivity().getContentResolver().insert(MeetingProvider.contentUri, contentValues);

            dbRowId = ContentUris.parseId(itemUri);
            if(dbRowId < 1) {
                throw new IllegalStateException("Unable to update; path=" + itemUri.toString());
            } else {
                intent.putExtra(launchAction, dbRowId);
            }
        } else if(launchAction.equals(MeetingConstants.EDIT_ACTION_EXISTING)) {
            if ((timeInMillis > MeetingTime.getInstance().getCurrentTimeInMillis()) && (dChange.equals("Y"))) {
                contentValues.put(SchemaConstants.COLUMN_D_CHG_REQ, "N");
            }
            dbRowId = getArguments().getLong(MeetingConstants.EDIT_EXISTING);
            int count = getActivity().getContentResolver()
                    .update(ContentUris.withAppendedId(MeetingProvider.contentUri, dbRowId), contentValues, null, null);
            if (count != 1) {
                throw new IllegalStateException("Unable to update RaceMeeting: more than one rows match rowId: " + dbRowId);
            }
        }
        intent.putExtra(launchAction, dbRowId);
        return intent;
    }

    /**
     * Update the text displayed on the (race) time field.
     */
    private void updateRaceTime(long timeInMillis) {
        etRaceTime.setText(MeetingTime.getInstance().getTimeFromMillis(timeInMillis,false));
    }

    /**
     * Map local values from the layout.
     */
    private void onCreateInitialise(View view) {
        etCityCode = (EditText) view.findViewById(R.id.etCityCode);
        etCityCode.setRawInputType(InputType.TYPE_NULL);
        etCityCode.setOnTouchListener(this);

        etRaceCode = (EditText) view.findViewById(R.id.etRaceCode);
        etRaceCode.setRawInputType(InputType.TYPE_NULL);
        etRaceCode.setOnTouchListener(this);

//        etRaceNum = (EditText) view.findViewById(R.id.etRaceNum);
        etRaceNum = (MeetingEditText) view.findViewById(R.id.etRaceNum);
//        meetKbd.register(R.id.etRaceNum);
        etRaceNum.setOnEditorActionListener(this);

        etRaceSel = (EditText) view.findViewById(R.id.etRaceSel);
//        meetKbd.register(R.id.etRaceSel);
        etRaceSel.setOnEditorActionListener(this);

        etRaceTime = (EditText) view.findViewById(R.id.etRaceTime);
        etRaceTime.setRawInputType(InputType.TYPE_NULL);
        etRaceTime.setOnTouchListener(this);
        etRaceTime.setBackgroundResource(R.drawable.et_basic_black_outline);

        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

//        meetKbd.prepareOnKeyboard(new int [] {R.id.btnSave});
    }

    /**
     * Set the backround of the components.
     * @param state
     */
    private void updateBackground(long state) {
        if(state == MeetingConstants.NEW_MEETING) {
            etCityCode.setBackgroundResource(R.drawable.et_basic_red_outline);

            if (checkUseRaceCodePreference()) {
                etRaceCode.setBackgroundResource(R.drawable.et_basic_green_outline);
            } else {
                etRaceCode.setBackgroundResource(R.drawable.et_basic_red_outline);
            }

            etRaceNum.setBackgroundResource(R.drawable.et_basic_red_outline);
            etRaceSel.setBackgroundResource(R.drawable.et_basic_red_outline);

        } else if(state == MeetingConstants.EDIT_MEETING) {
            etCityCode.setBackgroundResource(R.drawable.et_basic_green_outline);
            etRaceCode.setBackgroundResource(R.drawable.et_basic_green_outline);
            etRaceNum.setBackgroundResource(R.drawable.et_basic_green_outline);
            etRaceSel.setBackgroundResource(R.drawable.et_basic_green_outline);
        }
    }

    /**
     * Simple sanity check there is something in all the fields.
     * @return True if values exist in all fields.
     */
    private boolean checkFields() {
        if ((etCityCode.getText().length() > 0) &&
                (etRaceCode.getText().length() > 0) &&
                (etRaceNum.getText().length() > 0) &&
                (etRaceSel.getText().length() > 0) &&
                (etRaceTime.getText().length() > 0)) {
            return true;
        } else {
            Toast.makeText(getActivity(), "All fields are required.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Check whether the preference to use a default race code is set.
     * @return True if a default race code set, else false.
     */
    private boolean checkUseRaceCodePreference() {
        if(getRaceCodePreference().equals(MeetingConstants.RACE_CODE_ID_NONE)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Get the default race code preference value.
     * @return The race code preference value.
     */
    private String getRaceCodePreference() {
        String dspk = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getString(MeetingConstants.DEFAULT_RACE_CODE_PREF_KEY, null);
        if(dspk == null) {
            dspk = MeetingConstants.RACE_CODE_ID_NONE;
        }
        return dspk;
    }

    /**
     * Set the default race code preference value into the component.
     */
    private void setRaceCodeFromPreference() {

        String rcPref = getRaceCodePreference();

        if (rcPref.equals(MeetingConstants.RACE_CODE_ID_R)) {
            // race code 'R'
            etRaceCode.setText(MeetingConstants.RACE_CODE_R);
        } else if (rcPref.equals(MeetingConstants.RACE_CODE_ID_G)) {
            // race code 'G'
            etRaceCode.setText(MeetingConstants.RACE_CODE_G);
        } else if (rcPref.equals(MeetingConstants.RACE_CODE_ID_T)) {
            // race code 'T'
            etRaceCode.setText(MeetingConstants.RACE_CODE_T);
        } else if (rcPref.equals(MeetingConstants.RACE_CODE_ID_S)) {
            // race code 'S'
            etRaceCode.setText(MeetingConstants.RACE_CODE_S);
        }
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
    // This fragment's components as local variables.
    private ActionBar actionBar;
    private EditText etCityCode;
    private EditText etRaceCode;
    private EditText etRaceNum;
    private MeetingEditText etRaceNumCust;
    private EditText etRaceSel;
    private EditText etRaceTime;
    private Button btnSave;
    private long timeInMillis;         // time in milli sec.
    private String dChange;            // display change required flag.
    private String launchAction;       // activity action tag.

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

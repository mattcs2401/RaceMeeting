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
import com.mcssoft.racemeeting.utility.MeetingPreferences;
import com.mcssoft.racemeeting.utility.MeetingResources;
import com.mcssoft.racemeeting.utility.MeetingTime;

import mcssoft.com.racemeeting.R;

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
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        onCreateInitialise(rootView);

        intent = getActivity().getIntent();
        String action = intent.getAction();

        if(action.equals(MeetingResources.getInstance().getString(R.string.edit_action_new))) {
            editAction = R.string.edit_action_new;
        } else if (action.equals(MeetingResources.getInstance().getString(R.string.edit_action_existing))) {
            editAction = R.string.edit_action_existing;
        } else if (action.equals(MeetingResources.getInstance().getString(R.string.edit_action_copy))) {
            editAction = R.string.edit_action_copy;
        }

        doEditAction();

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
                if(checkFieldLengths() && checkAgainstCache()) {
                    // save values and tell activity to finish.
                    saveValues();
                    ((IShowCodes) getActivity()).onFinish();
                    break;
                }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d(LOG_TAG, "onTouch");
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            switch (view.getId()) {
                case R.id.etCityCode:
                    ((IShowCodes) getActivity())
                            .onShowCodes(R.integer.city_codes_fragment_id, view);
                    break;
                case R.id.etRaceCode:
                    ((IShowCodes) getActivity())
                            .onShowCodes(R.integer.race_codes_fragment_id, view);
                    break;
                case R.id.etRaceTime:
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
        long dbRowId = R.integer.init_default;  // simply an initialiser else complains.

        if((editAction == R.string.edit_action_existing) ||
                (editAction == R.string.edit_action_copy)) {
            dbRowId = args.getLong(MeetingResources.getInstance().getString(R.string.edit_existing_or_copy));
        }

        Uri contentUri = ContentUris.withAppendedId(MeetingProvider.contentUri, dbRowId);

        return new CursorLoader(getActivity(),
                contentUri,
                DatabaseHelper.getProjection(DatabaseHelper.Projection.MeetingListItem),
                SchemaConstants.SELECT_ALL_MLI,
                new String[] {Long.toString(dbRowId)},
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
        String time = MeetingTime.getInstance().getFormattedTimeFromMillis(timeInMillis);
        etRaceTime.setText(time.replace("am","").replace("pm",""));

        dispChgReq = cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_D_CHG_REQ));
        notified = cursor.getString(cursor.getColumnIndexOrThrow(SchemaConstants.COLUMN_NOTIFIED));

        if(editAction == R.string.edit_action_copy) {
            setCache();
        }
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
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment df = new TimePickDialog();

        Bundle args = new Bundle();
        int[] hourMin;

        hourMin = MeetingTime.getInstance().getTimeComponent(timeInMillis,R.integer.time_component_hour_minute);

        args.putInt(MeetingResources.getInstance().getString(R.string.hour), hourMin[0]);
        args.putInt(MeetingResources.getInstance().getString(R.string.mins), hourMin[1]);

        args.putString(MeetingResources.getInstance().getString(R.string.time_format),
                MeetingTime.getInstance().getTimeFormatPrefKey());

        df.setArguments(args);
        df.show(ft, MeetingResources.getInstance().getString(R.string.time_picker_tag));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private void doEditAction() {
        Bundle args = new Bundle();
        boolean getLoader = false;
        switch(editAction) {
            case R.string.edit_action_new:
                actionBar.setTitle(R.string.app_name_new);
                updateBackground(R.integer.new_meeting);
                timeInMillis = MeetingTime.getInstance().getTimeInMillis();
                updateRaceTime(timeInMillis);

                if(checkUseRaceCodePreference()) {
                    etRaceCode.setText(getRaceCodePreference());
                }
                break;
            case R.string.edit_action_existing:
                actionBar.setTitle(R.string.app_name_edit);
                getLoader = true;
                break;
            case R.string.edit_action_copy:
                actionBar.setTitle(R.string.app_name_copy);
                getLoader = true;
                break;
        }
        if(getLoader) {
            updateBackground(R.integer.edit_meeting);
            args.putLong(MeetingResources.getInstance()
                    .getString(R.string.edit_existing_or_copy), getRowIdFromArgs());
            getLoaderManager().initLoader(0, args, this);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Utility - Validation">
    /**
     * Simple sanity check there is something in all the fields.
     * @return True if values exist in all fields.
     */
    private boolean checkFieldLengths() {
        if ((etCityCode.getText().length() > 0) &&
                (etRaceCode.getText().length() > 0) &&
                (etRaceNum.getText().length() > 0) &&
                (etRaceSel.getText().length() > 0) &&
                (etRaceTime.getText().length() > 0)) {
            return true;
        }
        Toast.makeText(getActivity(), "All fields are required.", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean checkAgainstCache() {
        boolean isValid = true;
        if(editAction == R.string.edit_action_copy) {
            if(etCityCodeCache.equals(etCityCode.getText().toString()) &&
                    etRaceCodeCache.equals(etRaceCode.getText().toString()) &&
                    etRaceNumCache.equals(etRaceNum.getText().toString()) &&
                    etRaceSelCache.equals(etRaceSel.getText().toString()) &&
                    etRaceTimeCache.equals(etRaceTime.getText().toString())) {
                // nothing chnaged.
                Toast.makeText(getActivity(), "Fields are identical.", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }
        return isValid;
    }
    //</editor-fold>

    /**
     * Save the edit values to the database.
     * @return Intent with either:
     * 1) For new entry; key==EDIT_ACTION_NEW, value==row id in database.
     * 2) For existing entry:  key==EDIT_ACTION_EXISTING, value==row id in database.
     */
    private void saveValues() {
        //Log.d(LOG_TAG, "saveValues");
        long dbRowId;
        ContentValues contentValues = new ContentValues();

        contentValues.put(SchemaConstants.COLUMN_CITY_CODE, etCityCode.getText().toString());
        contentValues.put(SchemaConstants.COLUMN_RACE_CODE, etRaceCode.getText().toString());
        contentValues.put(SchemaConstants.COLUMN_RACE_NUM, etRaceNum.getText().toString());
        contentValues.put(SchemaConstants.COLUMN_RACE_SEL, etRaceSel.getText().toString());
        contentValues.put(SchemaConstants.COLUMN_DATE_TIME, timeInMillis);

        if((editAction == R.string.edit_action_new) || (editAction == R.string.edit_action_copy)) {

            contentValues.put(SchemaConstants.COLUMN_D_CHG_REQ, "N");
            contentValues.put(SchemaConstants.COLUMN_NOTIFIED, "N");

            Uri itemUri = getActivity().getContentResolver().insert(MeetingProvider.contentUri, contentValues);

            dbRowId = ContentUris.parseId(itemUri);
            if(dbRowId < 1) {
                throw new IllegalStateException(R.string.error_path + itemUri.toString());
            }
        } else if(editAction == R.string.edit_action_existing) {

            // Reset display change required.
            if ((timeInMillis > MeetingTime.getInstance().getTimeInMillis()) && (dispChgReq.equals("Y"))) {
                contentValues.put(SchemaConstants.COLUMN_D_CHG_REQ, "N");
            }

            // Reset notify required.
            if ((timeInMillis > MeetingTime.getInstance().getTimeInMillis()) && (notified.equals("Y"))) {
                contentValues.put(SchemaConstants.COLUMN_NOTIFIED, "N");
            }

            dbRowId = getRowIdFromArgs();
            int count = getActivity().getContentResolver()
                    .update(ContentUris.withAppendedId(MeetingProvider.contentUri, dbRowId), contentValues, null, null);
            if (count != 1) {
                throw new IllegalStateException(R.string.error_multi_rows + String.valueOf(dbRowId));
            }
        }
    }

    /**
     * Update the text displayed on the (race) time field.
     */
    private void updateRaceTime(long timeInMillis) {
        etRaceTime.setText(MeetingTime.getInstance().getFormattedTimeFromMillis(timeInMillis));
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

        etRaceNum = (MeetingEditText) view.findViewById(R.id.etRaceNum);
        etRaceNum.setOnEditorActionListener(this);

        etRaceSel = (EditText) view.findViewById(R.id.etRaceSel);
        etRaceSel.setOnEditorActionListener(this);

        etRaceTime = (EditText) view.findViewById(R.id.etRaceTime);
        etRaceTime.setRawInputType(InputType.TYPE_NULL);
        etRaceTime.setOnTouchListener(this);
        etRaceTime.setBackgroundResource(R.drawable.et_basic_black_outline);

        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
    }

    /**
     * Set the backround of the components.
     * @param state
     */
    private void updateBackground(int state) {
        switch (state) {
            case R.integer.new_meeting:
                etCityCode.setBackgroundResource(R.drawable.et_basic_red_outline);

                if (checkUseRaceCodePreference()) {
                    etRaceCode.setBackgroundResource(R.drawable.et_basic_green_outline);
                } else {
                    etRaceCode.setBackgroundResource(R.drawable.et_basic_red_outline);
                }

                etRaceNum.setBackgroundResource(R.drawable.et_basic_red_outline);
                etRaceSel.setBackgroundResource(R.drawable.et_basic_red_outline);
                break;
            case R.integer.edit_meeting:
                etCityCode.setBackgroundResource(R.drawable.et_basic_green_outline);
                etRaceCode.setBackgroundResource(R.drawable.et_basic_green_outline);
                etRaceNum.setBackgroundResource(R.drawable.et_basic_green_outline);
                etRaceSel.setBackgroundResource(R.drawable.et_basic_green_outline);
                break;
        }
    }

    /**
     * Check whether the preference to use a default race code is set.
     * @return True if a default race code set, else false.
     */
    private boolean checkUseRaceCodePreference() {
        return (!getRaceCodePreference().equals(MeetingResources.getInstance()
                .getString(R.string.race_code_none)));
    }

    /**
     * Get the default race code preference value.
     * @return The race code preference value.
     */
    private String getRaceCodePreference() {
        return MeetingPreferences.getInstance().meetingRaceCodePref();
    }

    private void hideSoftKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setCache() {
        etCityCodeCache = etCityCode.getText().toString();
        etRaceCodeCache = etRaceCode.getText().toString();
        etRaceNumCache = etRaceNum.getText().toString();
        etRaceSelCache = etRaceSel.getText().toString();
        etRaceTimeCache = etRaceTime.getText().toString();
    }

    private long getRowIdFromArgs() {
        // Note: array is only one element.
        return  ((long[])intent.getExtras().get(MeetingResources.getInstance()
                .getString(R.string.edit_existing_or_copy)))[0];
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private vars">
    // This fragment's components as local variables.
    private EditText etCityCode;
    private String etCityCodeCache;
    private EditText etRaceCode;
    private String etRaceCodeCache;
    private EditText etRaceNum;
    private String  etRaceNumCache;
    private EditText etRaceSel;
    private String  etRaceSelCache;
    private EditText etRaceTime;
    private String  etRaceTimeCache;
    private Button btnSave;

    private Intent intent;
    private ActionBar actionBar;

    private long timeInMillis;         // time in milli sec.
    private String dispChgReq;            // display change required flag.
    private String notified;
    private int editAction;       // activity action tag.

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

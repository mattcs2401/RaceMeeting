package com.mcssoft.racemeeting.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.mcssoft.racemeeting.fragment.CityCodesFragment;
import com.mcssoft.racemeeting.fragment.EditFragment;
import com.mcssoft.racemeeting.fragment.RaceCodesFragment;
import com.mcssoft.racemeeting.interfaces.IShowCodes;
import com.mcssoft.racemeeting.utility.MeetingConstants;

import mcssoft.com.racemeeting3.R;

public class EditActivity extends AppCompatActivity
        implements IShowCodes {

    //<editor-fold defaultstate="collapsed" desc="Region: LifeCycle">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        initialise();

        if (savedInstanceState == null) {
            editFragment.setArguments(getIntent().getExtras());

            fragmentManager.beginTransaction()
                    .add(R.id.edit_detail_container, editFragment, MeetingConstants.DEFAULT_EDIT_FRAGMENT_TAG)
                    .commit();
        } else {
            editFragment = (EditFragment) fragmentManager
                    .getFragment(savedInstanceState, MeetingConstants.DEFAULT_EDIT_FRAGMENT_TAG);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fragmentManager.putFragment(outState, MeetingConstants.DEFAULT_EDIT_FRAGMENT_TAG, editFragment);
    }

    @Override
    public void onBackPressed() {
        // Note: Implemented to try an overcome the issue where the back button is pressed in one of
        // the codes fragments and the navigation jumped back to the main listing.
        int count = fragmentManager.getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            fragmentManager.popBackStack();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Interface - IShowCodes">
    public void onShowCodes(int fragId, View view) {
        Log.d(LOG_TAG, "onShowCodes");
        setCodesFragment(fragId, view);
    }

    public void onFinishCodes(Bundle args) {
        Log.d(LOG_TAG, "onFinishCodes");
        String rbText = args.getString(MeetingConstants.FRAGMENT_RB_VAL);
        int fragId = args.getInt(MeetingConstants.FRAGMENT_ID);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.edit_detail_container);

        switch (fragId) {
            case MeetingConstants.CITY_CODES_FRAGMENT_ID:
                frameLayout.removeView(findViewById(R.id.id_city_codes_detail));
                EditText cc = (EditText) findViewById(R.id.etCityCode);
                cc.setText(rbText);
                cc.setBackgroundResource(R.drawable.et_basic_green_outline);
                cityCodesFragment = null;
                break;
            case MeetingConstants.RACE_CODES_FRAGMENT_ID:
                frameLayout.removeView(findViewById(R.id.id_race_codes));
                EditText rc = (EditText) findViewById(R.id.etRaceCode);
                rc.setText(rbText);
                rc.setBackgroundResource(R.drawable.et_basic_green_outline);
                raceCodesFragment = null;
                break;
        }

        getFragmentManager().popBackStack();
    }

    public void onFinish() {
        Log.d(LOG_TAG, "onFinish");
        cityCodesFragment = null;
        raceCodesFragment = null;
        editFragment = null;
        this.finish();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private void setCodesFragment(int fragId, View view) {
        Bundle args = new Bundle();
        args.putInt(MeetingConstants.FRAGMENT_ID, fragId);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        String etValue;

        switch (fragId) {
            case MeetingConstants.CITY_CODES_FRAGMENT_ID:
                etValue = ((EditText) view.findViewById(R.id.etCityCode)).getText().toString();
                args.putString(MeetingConstants.FRAGMENT_RB_VAL, etValue);
                cityCodesFragment = new CityCodesFragment();
                cityCodesFragment.setArguments(args);
                ft.add(R.id.edit_detail_container, cityCodesFragment, MeetingConstants.CITY_CODES_FRAGMENT_TAG);
                break;
            case MeetingConstants.RACE_CODES_FRAGMENT_ID:
                etValue = ((EditText) view.findViewById(R.id.etRaceCode)).getText().toString();
                args.putString(MeetingConstants.FRAGMENT_RB_VAL, etValue);
                raceCodesFragment = new RaceCodesFragment();
                raceCodesFragment.setArguments(args);
                ft.add(R.id.edit_detail_container, raceCodesFragment, MeetingConstants.RACE_CODES_FRAGMENT_TAG);
                break;
        }

        ft.addToBackStack(null);
        ft.hide(editFragment);
        ft.commit();
    }

    private void initialise() {
        setContentView(R.layout.activity_edit); // <-- simple FrameLayout
        editFragment = new EditFragment();
        fragmentManager = getFragmentManager();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private Vars">
    private EditFragment editFragment;
    private FragmentManager fragmentManager;
    private CityCodesFragment cityCodesFragment;
    private RaceCodesFragment raceCodesFragment;

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

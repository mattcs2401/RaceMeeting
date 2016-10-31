package com.mcssoft.racemeeting.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mcssoft.racemeeting.interfaces.IShowCodes;
import com.mcssoft.racemeeting.utility.MeetingConstants;

import mcssoft.com.racemeeting3.R;

public class RaceCodesFragment extends Fragment
        implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    //<editor-fold defaultstate="collapsed" desc="Region: LifeCycle">
    @Override
    public void onCreate(Bundle state) {
        Log.d(LOG_TAG,"onCreate");
        super.onCreate(state);
        fragId = getArguments().getInt(MeetingConstants.FRAGMENT_ID);
        rbText = getArguments().getString(MeetingConstants.FRAGMENT_RB_VAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.race_codes, container, false);

        initialise(view);

        return view;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Listeners">
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.id_fab_race_codes) {
            ((IShowCodes) getActivity()).onFinishCodes(args);
        }
    }

    // Radio group listener.
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        RadioButton rb = (RadioButton) group.findViewById(checkedId);

        if ((rb != null) && (checkedId > -1)) {
            rbText = rb.getText().toString();

            args = new Bundle();
            args.putInt(MeetingConstants.FRAGMENT_ID, fragId);
            args.putString(MeetingConstants.FRAGMENT_RB_VAL, rbText);

            Toast.makeText(getActivity(), "Race Code " + rbText + " selected.", Toast.LENGTH_SHORT).show();
            fab.setVisibility(FloatingActionButton.VISIBLE);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    private void initialise(View view) {
        rgRC = (RadioGroup) view.findViewById(R.id.rg_race_codes);
        rbId = getRbId();

        fab = (FloatingActionButton) view.findViewById(R.id.id_fab_race_codes);
        fab.setOnClickListener(this);

        if(rbId == MeetingConstants.INIT_DEFAULT) {
            // editing for new entry.
            rgRC.clearCheck();
        } else {
            rgRC.check(rbId);
        }
        rgRC.setOnCheckedChangeListener(this);
    }

    private int getRbId() {
        RadioButton rb = null;
        int id = MeetingConstants.INIT_DEFAULT;
        int count = rgRC.getChildCount();

        for (int ndx = 0; ndx < count; ndx++) {
            rb = (RadioButton) rgRC.getChildAt(ndx);
            if(rb.getText().equals(rbText)) {
                id = rb.getId();
                break;
            }
        }
        return id;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Private Vars">
    private int rbId;                 // radiobutton id.
    private int fragId;               // fragment unique id.
    private Bundle args;              // fragment 'return' values.
    private String rbText;            // text of selected radio button.
    private RadioGroup rgRC;          // radio button group.
    private FloatingActionButton fab; //

    private String LOG_TAG = this.getClass().getCanonicalName();
    //</editor-fold>
}

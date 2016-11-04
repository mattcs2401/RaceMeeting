package com.mcssoft.racemeeting.interfaces;

import android.os.Bundle;
import android.view.View;

/**
 * Interface between the EditFragment and EditActivity to show the codes fragments
 */
public interface IShowCodes {
    /**
     * Call back to xxx.
     * @param fragId The codes fragment identifier.
     * @param view Used to instantiate a particular view (EditText).
     */
    void onShowCodes(int fragId, View view);

    /**
     * Call back to return the selected code from the particular codes fragment.
     * @param args Key/Value pair of the selected code.
     */
    void onFinishCodes(Bundle args);

    /**
     *
     */
    void onFinish();
}

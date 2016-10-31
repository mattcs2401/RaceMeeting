package com.mcssoft.racemeeting.interfaces;

import android.os.Bundle;
import android.view.View;

/**
 * Interface between the EditFragment and EditActivity to show the codes fragments
 */
public interface IShowCodes {
    /**
     *
     * @param dId
     * @param view
     */
    public void onShowCodes(int dId, View view);

    /**
     *
     * @param args
     */
    public void onFinishCodes(Bundle args);


    public void onFinish();
}

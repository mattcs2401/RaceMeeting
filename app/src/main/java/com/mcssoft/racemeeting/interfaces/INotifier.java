package com.mcssoft.racemeeting.interfaces;

import java.util.ArrayList;

/**
 * Interface between MqainFragment and MainActivity.
 * Returns a listing of values to be used in the notification text elements.
 */
public interface INotifier {
    public void onNotify(ArrayList<String[]> notifyValues);
}
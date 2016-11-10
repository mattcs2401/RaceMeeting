package com.mcssoft.racemeeting.interfaces;

/**
 * Interface between the MainFragment and the MainActivity.
 */
public interface IShowMeeting {
    /**
     * Callback to display an item from the listing utilising the DetailActivity.
     * @param id The database row id value.
     */
    void onShowMeeting(long id);
}

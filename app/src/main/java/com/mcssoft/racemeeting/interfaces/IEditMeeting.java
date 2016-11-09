package com.mcssoft.racemeeting.interfaces;

public interface IEditMeeting {
    /**
     * Replace the ListingFragment with the EditFragment (called in MainActivity).
     * Replace the DetailFragment with the EditFragment (called in DetailActivity)
     * @param dbRowId The database row id corredsponding to the selected item in the listing, or a
     *                value of 0 to indicate a new meeting.
     *  Bundle extras also provided to indicate editing an existing entry, or a new one.
     */
    void onEditMeeting(long dbRowId);
}

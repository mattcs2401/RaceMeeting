package com.mcssoft.racemeeting.interfaces;

public interface IEditMeeting {
    /**
     * Replace the ListingFragment with the EditFragment.
     * @param dbRowId The database row id corredsponding to the selected item in the listing, or a
     *                value of 0 to indicate a new meeting..
     */
    void onEditMeeting(long dbRowId);
}

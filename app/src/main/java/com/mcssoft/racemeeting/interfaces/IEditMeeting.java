package com.mcssoft.racemeeting.interfaces;

public interface IEditMeeting {
    /**
     *
     * @param dbRowId
     * @param editType
     */
    void onEditMeeting(int editType, long dbRowId );
}

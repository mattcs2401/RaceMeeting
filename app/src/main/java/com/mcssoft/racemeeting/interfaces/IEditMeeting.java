package com.mcssoft.racemeeting.interfaces;

public interface IEditMeeting {
    /**
     *
     * @param dbRowId
     * @param editType
     */
    void onEditMeeting(long dbRowId, int editType);
}

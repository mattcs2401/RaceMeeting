package com.mcssoft.racemeeting.interfaces;

import android.os.Bundle;

/**
 * Interface between the DeleteDialog and the ListingFragment.
 * Provides a message that meeting was deleted (or not).
 */
public interface IDeleteDialog {
    void onDeleteDialog(Bundle args);
}


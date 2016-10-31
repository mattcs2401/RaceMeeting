package com.mcssoft.racemeeting.interfaces;

/**
 * Interface between the custom keyboard and EditFragment.
 * Returns: the component id, keycode of the key pressed.
 */
public interface IKeyboard {
    void onFinishKeyboard(int compId, int keyCode);
}


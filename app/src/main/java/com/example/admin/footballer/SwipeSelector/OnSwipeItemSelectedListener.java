package com.example.admin.footballer.SwipeSelector;

/**
 * Created by admin on 3/18/17.
 */


public interface OnSwipeItemSelectedListener {
    /**
     * The method being called when currently visible {@link SwipeItem} changes.
     * This listener won't be fired until the user changes the selected item the
     * first time. So you won't get this event when you're just initialized the
     * SwipeSelector.
     *
     */
    void onItemSelected(int position);
}
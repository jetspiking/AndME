package com.dustinhendriks.andme.interfaces;

import android.content.Context;

/**
 * Listen to item clicked events by implementing the OnItemClickedListener interface.
 */
public interface OnItemClickedListener {
    /**
     * For a regular performed click on an item.
     * @param context Context of clicked item.
     * @param object Clicked item.
     * @param holder Holder of the item.
     */
    void clickedItem(Context context, Object object, Object holder);
    /**
     * For a longer performed click on an item.
     * @param context Context of clicked item.
     * @param object Clicked item.
     * @param holder Holder of the item.
     */
    void longClickedItem(Context context, Object object, Object holder);
}

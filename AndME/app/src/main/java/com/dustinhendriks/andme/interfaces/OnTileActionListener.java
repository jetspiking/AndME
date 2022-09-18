package com.dustinhendriks.andme.interfaces;

import android.content.Context;

/**
 * Provides a specific implementation for the OnItemClickedListener interface.
 */
public interface OnTileActionListener extends OnItemClickedListener {
    /**
     * For a regular performed click on an item.
     * @param context Context of clicked item.
     * @param object Clicked item.
     * @param holder Holder of the item.
     */
    @Override
    void clickedItem(Context context, Object object, Object holder);
    /**
     * For a longer performed click on an item.
     * @param context Context of clicked item.
     * @param object Clicked item.
     * @param holder Holder of the item.
     */
    @Override
    void longClickedItem(Context context, Object object, Object holder);
}

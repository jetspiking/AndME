package com.dustinhendriks.andme.interfaces;

import android.content.Context;

public interface OnTileActionListener extends OnItemClickedListener {
    @Override
    void clickedItem(Context context, Object object, int position);
    @Override
    void longClickedItem(Context context, Object object, int position);
    void clickedDragger(Context context, Object object, int position);
    void clickedUnpin(Context context, Object object, int position);
    void clickedResize(Context context, Object object, int position);
}

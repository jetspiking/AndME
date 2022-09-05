package com.dustinhendriks.andme.interfaces;

import android.content.Context;

public interface OnItemClickedListener {
    void clickedItem(Context context, Object object, int position);
    void longClickedItem(Context context, Object object, int position);
}

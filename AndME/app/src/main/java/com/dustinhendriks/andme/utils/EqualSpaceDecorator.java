package com.dustinhendriks.andme.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;

/**
 * Apply a margin as an ItemDecoration that is suitable for usage in a recycler view through the "addItemDecoration" function.
 */
public class EqualSpaceDecorator extends ItemDecoration {
    private final int margin;

    public EqualSpaceDecorator(int margin) {
        this.margin = margin;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = this.margin;
        outRect.right = this.margin;
        outRect.bottom = this.margin;
        outRect.top = this.margin;
    }

}

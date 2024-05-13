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
    private final int mMargin;

    /**
     * Construct based on margin that should be applied.
     * @param margin Space between tiles.
     */
    public EqualSpaceDecorator(int margin) {
        this.mMargin = margin;
    }

    /**
     * Retrieve the offsets based on the margin.
     * @param outRect Rect to receive the output.
     * @param view    The child view to decorate
     * @param parent  RecyclerView this ItemDecoration is decorating
     * @param state   The current state of RecyclerView.
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = this.mMargin;
        outRect.right = this.mMargin;
        outRect.bottom = this.mMargin;
        outRect.top = this.mMargin;
    }

}

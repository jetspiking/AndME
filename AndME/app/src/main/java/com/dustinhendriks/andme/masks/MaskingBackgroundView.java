package com.dustinhendriks.andme.masks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Used in combination with a background for achieving the unique Windows Phone tile look where a tile seems to be a window to the background.
 */
public class MaskingBackgroundView extends View {
    private List<Rect> mTileRects = new ArrayList<Rect>();
    private Paint mMaskPaint;
    private Paint mClearPaint;
    private int mColor;

    /**
     * Create the view.
     * @param context Application context.
     * @param color Color for block.
     */
    public MaskingBackgroundView(Context context, int color) {
        super(context);
        this.mColor = color;
        initPaints();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    /**
     * Initialize paint.
     */
    private void initPaints() {
        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMaskPaint.setColor(this.mColor);

        mClearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    /**
     * Update tile rectangles.
     * @param rects To update / draw.
     */
    public void updateTileRects(List<Rect> rects) {
        mTileRects = rects;
        invalidate();
    }

    /**
     * To update / draw.
     * @param canvas Canvas.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPaint(mMaskPaint);

        for (Rect rect : mTileRects) {
            canvas.drawRect(rect, mClearPaint);
        }
    }
}

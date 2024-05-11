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

public class MaskingBackgroundView extends View {
    private List<Rect> mTileRects = new ArrayList<Rect>();
    private Paint mMaskPaint;
    private Paint mClearPaint;
    private int mColor;

    public MaskingBackgroundView(Context context, int color) {
        super(context);
        this.mColor = color;
        initPaints();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);  // Use software layer to handle Xfermodes
    }

    private void initPaints() {
        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMaskPaint.setColor(this.mColor);  // Background mask color

        mClearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

    }

    public void updateTileRects(List<Rect> rects) {
        mTileRects = rects;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPaint(mMaskPaint);  // Apply the mask paint over the entire canvas

        // Draw clear rectangles over the tiles
        for (Rect rect : mTileRects) {
            canvas.drawRect(rect, mClearPaint);
        }
    }
}

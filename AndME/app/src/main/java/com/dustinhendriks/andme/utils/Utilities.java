package com.dustinhendriks.andme.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Contains a set of general functions that are too generic for encapsulation in a regular class.
 */
public class Utilities {

    /**
     * Convert a Drawable to a Bitmap object.
     * @param drawable Drawable to convert.
     * @return Bitmap representation of Drawable argument.
     */
    public static Bitmap drawableToBitmap (Drawable drawable){
        if (drawable instanceof BitmapDrawable) return ((BitmapDrawable)drawable).getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Convert a bitmap to a Drawable object.
     * @param context Application context.
     * @param bitmap Bitmap to convert.
     * @return Drawable representation of Bitmap argument.
     */
    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }
}

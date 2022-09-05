package com.dustinhendriks.andme.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Utilities {
    private static final String TAG = "Utilities";
    public static Bitmap getBitmap(Bitmap bitmap, int newHeight, int newWidth)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float floatWidth = ((float) newWidth) / width;
        float floatHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(floatWidth, floatHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static Bitmap drawableToBitmap (Drawable drawable){
        if (drawable instanceof BitmapDrawable) return ((BitmapDrawable)drawable).getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static void printLongString(String string) {
        int maxLogSize = 500;
        for(int i = 0; i <= string.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > string.length() ? string.toString().length() : end;
            Log.d(TAG, string.substring(start, end));
        }
    }
}

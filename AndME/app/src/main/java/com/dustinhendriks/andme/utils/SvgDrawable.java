package com.dustinhendriks.andme.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.caverock.androidsvg.SVG;

import java.io.InputStream;

public class SvgDrawable {
    /**
     * Load an svg from disk represented as a Drawable object.
     * @param context Application context.
     * @param uri Path to svg.
     * @param color Color for svg.
     * @return Vector drawable.
     */
    public static Drawable getSvgAsDrawable(Context context, String uri, int color) {
        try {
            InputStream inputStream = context.getAssets().open(uri);
            SVG svg = SVG.getFromInputStream(inputStream);
            inputStream.close();

            Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            svg.renderToCanvas(canvas);

            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
            bitmapDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            return bitmapDrawable;
        } catch (Exception ignored) {
        }
        return null;
    }
}

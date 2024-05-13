package com.dustinhendriks.andme.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.caverock.androidsvg.SVG;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles mapping of application packages to icons.
 */
public class IconPackUtils {
    public static final String ICON_PACK_DEFAULT = "Default";
    public static final String ICON_PACK_OPENMIX = "Openmix";

    /**
     * Retrieve the list of available icon packs.
     * @return List of icon packs.
     */
    public static List<String> getAvailableIconPacks() {
        List<String> iconPacks = new ArrayList<>();
        iconPacks.add(ICON_PACK_DEFAULT);
        iconPacks.add(ICON_PACK_OPENMIX);
        return iconPacks;
    }

    /**
     * Load an icon based on package name.
     * @param context Application context.
     * @param appPackageName Application package name.
     * @param iconPackPackageName Icon pack name.
     * @return Drawable representation of icon.
     */
    public static Drawable loadIconFromPack(Context context, String appPackageName, String iconPackPackageName) {
        String iconName = getDiskIconName(context, appPackageName, iconPackPackageName);
        if (iconName != null) {
            return getDiskIconByName(context, iconName, iconPackPackageName);
        }
        return null;
    }

    /**
     * Retrieve the icon name from the disk for a specific app package name (mapped).
     * @param context Application context.
     * @param appPackageName Application to retrieve icon name for.
     * @param iconPackPackageName Icon pack name.
     * @return String containing icon name.
     */
    private static String getDiskIconName(Context context, String appPackageName, String iconPackPackageName) {
        try {
            String path = "iconpacks/" + iconPackPackageName.toLowerCase();
            String[] iconNames = context.getAssets().list(path);
            if (iconNames != null) {
                for (String iconName : iconNames) {
                    if (iconName.contains(appPackageName))
                        return iconName;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Retrieve an icon pack image by name.
     * @param context Application context.
     * @param iconName Icon name.
     * @param iconPackPackageName Application pack name.
     * @return Drawable representation of icon.
     */
    public static Drawable getDiskIconByName(Context context, String iconName, String iconPackPackageName) {
        try {
            InputStream inputStream = context.getAssets().open("iconpacks/" + iconPackPackageName.toLowerCase() + "/" + iconName);
            SVG svg = SVG.getFromInputStream(inputStream);
            inputStream.close();

            Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            svg.renderToCanvas(canvas);

            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
            bitmapDrawable.setColorFilter(AppMiscDefaults.TEXT_COLOR, PorterDuff.Mode.SRC_ATOP);
            return bitmapDrawable;
        } catch (Exception ignored) {
        }
        return null;
    }
}

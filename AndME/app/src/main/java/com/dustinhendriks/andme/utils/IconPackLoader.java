package com.dustinhendriks.andme.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.dustinhendriks.andme.R;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles mapping of application packages to icons.
 */
public class IconPackLoader {
    public static final String ICON_PACK_DEFAULT = "Default";
    public static final String ICON_PACK_INCLUDED = "Metro";

    /**
     * Retrieve the list of available icon packs.
     * @return List of icon packs.
     */
    public static List<String> getAvailableIconPacks() {
        List<String> iconPacks = new ArrayList<>();
        iconPacks.add(ICON_PACK_DEFAULT);
        iconPacks.add(ICON_PACK_INCLUDED);
        return iconPacks;
    }

    /**
     * Load an icon based on package name.
     * @param context Application context.
     * @param appPackageName Application package name.
     * @param iconPackJson Icon pack object string representation containing bindings.
     * @return Drawable representation of icon.
     */
    public static Drawable loadIconFromPack(Context context, String appPackageName, String iconPackName, String iconPackJson) {
        if (iconPackName.isEmpty() || iconPackName.equals("Default")) return null;
        if (iconPackJson == null || iconPackJson.isEmpty()) return null;

        IconPack iconPack = null;
        try {
            Gson gson = new Gson();
            iconPack = gson.fromJson(iconPackJson, IconPack.class);
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.settings_invalid_theme_data), Toast.LENGTH_SHORT).show();
        }
        if (iconPack == null) return null;

        for (IconBinding binding : iconPack.bindings) {
            if (binding.packageName.equalsIgnoreCase(appPackageName)) {
                return getDiskIconByName(context, binding.fileName, iconPackName);
            }
        }
        return null;
    }

    /**
     * Retrieve an icon pack image by name.
     * @param context Application context.
     * @param iconName Icon name.
     * @param iconPackName Icon pack name.
     * @return Drawable representation of icon.
     */
    public static Drawable getDiskIconByName(Context context, String iconName, String iconPackName) {
        String uri = "iconpacks/" + iconPackName.toLowerCase() + "/" + iconName;
        return SvgDrawable.getSvgAsDrawable(context, uri, AppMiscDefaults.TEXT_COLOR);
    }
}
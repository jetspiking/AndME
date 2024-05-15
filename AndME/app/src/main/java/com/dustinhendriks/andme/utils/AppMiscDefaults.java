package com.dustinhendriks.andme.utils;

import android.graphics.Color;

import com.dustinhendriks.andme.models.AppSerializableData;

/**
 * Default application settings.
 */
public class AppMiscDefaults {
    public static String WEB_URL = "https://github.com/jetspiking/andme";
    public static int TILE_SPAN_COUNT = 3;
    public static int TILE_BORDER_MARGIN = 10;
    public static int TILE_LIST_MARGIN = 10;
    public static int OPACITY=0;
    public static boolean SHOW_ICONS_IN_APPS_LIST = true;
    public static boolean SHOW_NAVIGATION_BAR = true;
    public static boolean SHOW_SYSTEM_WALLPAPER = false;
    public static int BACKGROUND_COLOR = Color.BLACK;
    public static int ACCENT_COLOR = Color.parseColor("#FF00ABA9");
    public static int TEXT_COLOR = Color.WHITE;
    public static String APPLIED_ICON_PACK_NAME = "Default";
    public static String APPLIED_ICON_PACK_BINDING;

    /**
     * Write application data to memory.
     * @param appSerializableData Data to apply to memory.
     */
    public static void RestoreFromSerializedData(AppSerializableData appSerializableData) {
        TEXT_COLOR = appSerializableData.TEXT_COLOR;
        ACCENT_COLOR = appSerializableData.ACCENT_COLOR;
        BACKGROUND_COLOR = appSerializableData.BACKGROUND_COLOR;
        SHOW_ICONS_IN_APPS_LIST = appSerializableData.SHOW_ICONS_IN_APPS_LIST == 1;
        SHOW_NAVIGATION_BAR = appSerializableData.SHOW_NAVIGATION_BAR == 1;
        SHOW_SYSTEM_WALLPAPER = appSerializableData.SHOW_SYSTEM_WALLPAPER == 1;
        OPACITY = appSerializableData.OPACITY;
        TILE_LIST_MARGIN = appSerializableData.TILE_LIST_MARGIN;
        TILE_BORDER_MARGIN = appSerializableData.TILE_BORDER_MARGIN;
        TILE_SPAN_COUNT = appSerializableData.TILE_SPAN_COUNT;
        APPLIED_ICON_PACK_NAME = appSerializableData.APPLIED_ICON_PACK_NAME;
        APPLIED_ICON_PACK_BINDING = appSerializableData.APPLIED_ICON_PACK_BINDING;
    }
}

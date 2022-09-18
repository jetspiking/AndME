package com.dustinhendriks.andme.models;

import com.dustinhendriks.andme.utils.AppMiscDefaults;

import java.io.Serializable;
import java.util.ArrayList;

public class AppSerializableData implements Serializable {
    public static final int NOT_UPDATED = -404;
    private ArrayList<Tile> tiles = new ArrayList<>();
    public int TILE_SPAN_COUNT=NOT_UPDATED;
    public int TILE_BORDER_MARGIN=NOT_UPDATED;
    public int TILE_LIST_MARGIN=NOT_UPDATED;
    public int OPACITY=NOT_UPDATED;
    public int BACKGROUND_COLOR = NOT_UPDATED;
    public int ACCENT_COLOR = NOT_UPDATED;
    public int TEXT_COLOR = NOT_UPDATED;
    public int SHOW_NAVIGATION_BAR = NOT_UPDATED;
    public int SHOW_ICONS_IN_APPS_LIST = NOT_UPDATED;

    public void update(ArrayList<Tile> tiles) {
        this.tiles.clear();
        this.tiles.addAll(tiles);
        TILE_SPAN_COUNT = AppMiscDefaults.TILE_SPAN_COUNT;
        TILE_BORDER_MARGIN = AppMiscDefaults.TILE_BORDER_MARGIN;
        TILE_LIST_MARGIN = AppMiscDefaults.TILE_LIST_MARGIN;
        OPACITY = AppMiscDefaults.OPACITY;
        BACKGROUND_COLOR = AppMiscDefaults.BACKGROUND_COLOR;
        ACCENT_COLOR = AppMiscDefaults.ACCENT_COLOR;
        TEXT_COLOR = AppMiscDefaults.TEXT_COLOR;
        SHOW_NAVIGATION_BAR = AppMiscDefaults.SHOW_NAVIGATION_BAR ? 1 : 0;
        SHOW_ICONS_IN_APPS_LIST = AppMiscDefaults.SHOW_ICONS_IN_APPS_LIST ? 1 : 0;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }
}

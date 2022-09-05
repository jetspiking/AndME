package com.dustinhendriks.andme.models;

import com.dustinhendriks.andme.utils.AppDefaults;

import java.io.Serializable;
import java.util.ArrayList;

public class AppSerializableData implements Serializable {
    public static final int NOT_UPDATED = -404;
    private ArrayList<Tile> tiles = new ArrayList<>();
    public int DEFAULT_TILE_GRID_BACKGROUND_COLOR=NOT_UPDATED;
    public int THEME_ACCENT_COLOR=NOT_UPDATED;
    public int TILE_SPAN_COUNT=NOT_UPDATED;
    public int TILE_BORDER_MARGIN=NOT_UPDATED;
    public int SETTINGS_COLOR_BORDER_MARGIN=NOT_UPDATED;
    public int TILE_LIST_MARGIN=NOT_UPDATED;
    public int OPACITY=NOT_UPDATED;

    public void update(ArrayList<Tile> tiles) {
        this.tiles.clear();
        this.tiles.addAll(tiles);
        THEME_ACCENT_COLOR = AppDefaults.THEME_ACCENT_COLOR;
        TILE_SPAN_COUNT = AppDefaults.TILE_SPAN_COUNT;
        TILE_BORDER_MARGIN = AppDefaults.TILE_BORDER_MARGIN;
        SETTINGS_COLOR_BORDER_MARGIN = AppDefaults.SETTINGS_COLOR_BORDER_MARGIN;
        TILE_LIST_MARGIN = AppDefaults.TILE_LIST_MARGIN;
        OPACITY = AppDefaults.OPACITY;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    @Override
    public String toString() {
        return "AppSerializableData{" +
                "\ntiles=" + tiles +
                ",\nDEFAULT_TILE_GRID_BACKGROUND_COLOR=" + DEFAULT_TILE_GRID_BACKGROUND_COLOR +
                ",\nTHEME_ACCENT_COLOR=" + THEME_ACCENT_COLOR +
                ",\nTILE_SPAN_COUNT=" + TILE_SPAN_COUNT +
                ",\nTILE_BORDER_MARGIN=" + TILE_BORDER_MARGIN +
                ",\nSETTINGS_COLOR_BORDER_MARGIN=" + SETTINGS_COLOR_BORDER_MARGIN +
                ",\nTILE_LIST_MARGIN=" + TILE_LIST_MARGIN +
                ",\nOPACITY=" + OPACITY +
                '}';
    }
}

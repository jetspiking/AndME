package com.dustinhendriks.andme.models;

import java.io.Serializable;

/**
 * Used for decorative purposes and represents a wrapped application in a "Tile", to display on the start menu.
 */
public abstract class Tile implements Serializable {
    private String name;
    private int width;
    private int height;

    /**
     * Create tile.
     * @param name Tile name.
     * @param width Tile width.
     * @param height Tile height.
     */
    public Tile(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    /**
     * Retrieve the tile name.
     * @return Tile name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the tile name.
     * @param name Tile name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the tile width.
     * @return Tile width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the tile width.
     * @param width Tile width.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Get the tile height.
     * @return Tile height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the tile height.
     * @param height Tile height.
     */
    public void setHeight(int height) {
        this.height = height;
    }
}

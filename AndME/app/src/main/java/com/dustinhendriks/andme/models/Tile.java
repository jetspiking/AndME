package com.dustinhendriks.andme.models;

import java.io.Serializable;

public abstract class Tile implements Serializable {
    private String name;
    private int width;
    private int height;
    private int backgroundColor;
    private int iconResource;
    private int alertResource;

    protected Tile(Builder<?> builder){
        name = builder.name;
        width = builder.width;
        height = builder.height;
        backgroundColor = builder.backgroundColor;
        iconResource = builder.iconResource;
        alertResource = builder.alertResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public int getAlertResource() {
        return alertResource;
    }

    public void setAlertResource(int alertResource) {
        this.alertResource = alertResource;
    }

    public void checkWidth(int currentTileWidth) {
        if (width>currentTileWidth) {
            width=1;
        }
    }

    public abstract static class Builder<T extends Builder<T>> {
        private String name;
        private int width;
        private int height;
        private int backgroundColor;
        private int iconResource;
        private int alertResource;

        public Builder(String name) {
            this.name = name;
        }

        public abstract T getThis();

        public T withWidth(int width) {
            this.width = width;
            return getThis();
        }

        public T withHeight(int height) {
            this.height = height;
            return getThis();
        }

        public T withBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return getThis();
        }

        public T withIconResource(int iconResource) {
            this.iconResource = iconResource;
            return getThis();
        }

        public T withAlertResource(int alertResource) {
            this.alertResource = alertResource;
            return getThis();
        }
    }

    @Override
    public String toString() {
        return "\nTile{" +
                "name='" + name + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", backgroundColor=" + backgroundColor +
                ", iconResource=" + iconResource +
                ", alertResource=" + alertResource +
                '}';
    }
}

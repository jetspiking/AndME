package com.dustinhendriks.andme.interfaces;

/**
 * Allows implementing the launch function.
 * @param <T> Generic parameter type depending on usage.
 */
public interface LaunchableTile<T> {
    /**
     * Launch application from Tile.
     * @param parameter Generic parameter type depending on usage.
     */
    void launch(T parameter);
}

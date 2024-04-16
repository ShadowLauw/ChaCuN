package ch.epfl.chacun.gui;

import ch.epfl.chacun.PlayerColor;
import javafx.scene.paint.Color;

/**
 * A utility class for mapping player colors to colors.
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class ColorMap {
    /**
     * The brightness of the stroke color compared to the fill color.
     */
    private static final double REDUCED_BRIGHTNESS = 0.60; // 40% lighter is 60% of the brightness.

    /**
     * Private constructor to prevent instantiation.
     */
    private ColorMap() {
    }

    /**
     * Returns the fill color for a given player color.
     *
     * @param color the player color
     * @return the fill color
     */
    public static Color fillColor(PlayerColor color) {
        return switch (color) {
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case GREEN -> Color.LIME;
            case YELLOW -> Color.YELLOW;
            case PURPLE -> Color.PURPLE;
        };
    }

    /**
     * Returns the stroke color for a given player color.
     *
     * @param color the player color
     * @return the stroke color
     */
    public static Color strokeColor(PlayerColor color) {
        return switch (color) {
            case RED, BLUE, PURPLE -> Color.WHITE;
            case GREEN, YELLOW -> fillColor(color).deriveColor(1, 1, REDUCED_BRIGHTNESS, 1);
        };
    }
}

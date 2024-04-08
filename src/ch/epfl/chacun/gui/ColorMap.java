package ch.epfl.chacun.gui;

import ch.epfl.chacun.PlayerColor;
import javafx.scene.paint.Color;


public class ColorMap {
    /**
     * The brightness of the stroke color compared to the fill color.
     */
    static private final double BRIGHTNESS = 0.60; // 40% lighter is 60% of the brightness.

    /**
     * Returns the fill color for a given player color.
     * @param color the player color
     * @return the fill color
     */
    public static Color fillColor (PlayerColor color) {
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
     * @param color the player color
     * @return the stroke color
     */
    public static Color strokeColor (PlayerColor color) {
        return switch (color) {
            case RED -> Color.WHITE;
            case BLUE -> Color.WHITE;
            case GREEN -> fillColor(color).deriveColor(1, 1, BRIGHTNESS, 1);
            case YELLOW -> fillColor(color).deriveColor(1, 1, BRIGHTNESS, 1);
            case PURPLE -> Color.WHITE;
        };
    }
}

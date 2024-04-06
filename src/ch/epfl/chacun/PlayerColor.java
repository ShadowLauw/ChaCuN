package ch.epfl.chacun;

import java.util.List;

/**
 * Represents the different colors of the players
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public enum PlayerColor {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    PURPLE;

    /**
     * List of all the possible colors
     */
    public static final List<PlayerColor> ALL = List.of(values());
}
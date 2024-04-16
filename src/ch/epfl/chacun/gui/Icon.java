package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;

import static ch.epfl.chacun.gui.ColorMap.fillColor;
import static ch.epfl.chacun.gui.ColorMap.strokeColor;

/**
 * A utility class for creating icons for the board.
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class Icon {
    /**
     * the svg path for the pawn
     */
    private static final String PATH_PAWN = "M -10 10 H -4 L 0 2 L 6 10 H 12 L 5 0 L 12 -2 L 12 -4 L 6 -6L 6 -10 L 0 -10 L -2 -4 L -6 -2 L -8 -10 L -12 -10 L -8 6 Z";

    /**
     * the svg path for the hut
     */
    private static final String PATH_HUT = "M -8 10 H 8 V 2 H 12 L 0 -10 L -12 2 H -8 Z";

    /**
     * Private constructor to prevent instantiation.
     */
    private Icon() {
    }

    /**
     * Creates a new node for the given player color and occupant kind.
     *
     * @param color    the player color
     * @param occupant the occupant kind
     * @return the new node
     */
    public static Node newFor(PlayerColor color, Occupant.Kind occupant) {
        SVGPath svg = new SVGPath();
        svg.setFill(fillColor(color));
        svg.setStroke(strokeColor(color));
        switch (occupant) {
            case PAWN -> svg.setContent(PATH_PAWN);
            case HUT -> svg.setContent(PATH_HUT);
        }
        ;
        return svg;
    }
}

package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;

import static ch.epfl.chacun.gui.ColorMap.fillColor;
import static ch.epfl.chacun.gui.ColorMap.strokeColor;

public abstract class Icon {

    /**
     * the svg path for the pawn
     */
    private final String PATHPAWN = "M -10 10 H -4 L 0 2 L 6 10 H 12 L 5 0 L 12 -2 L 12 -4 L 6 -6L 6 -10 L 0 -10 L -2 -4 L -6 -2 L -8 -10 L -12 -10 L -8 6 Z";
    /**
     * the svg path for the hut
     */
    private final String PATHHUT = "M -8 10 H 8 V 2 H 12 L 0 -10 L -12 2 H -8 Z";

    /**
     * Creates a new node for the given player color and occupant kind.
     * @param color the player color
     * @param occupant the occupant kind
     * @return the new node
     */
    public Node newFor (PlayerColor color, Occupant.Kind occupant) {
        SVGPath svg = new SVGPath();
        switch (occupant) {
            case PAWN -> {
                svg.setContent(PATHPAWN);
                svg.setFill(fillColor(color));
                svg.setStroke(strokeColor(color));
            }
            case HUT -> {
                svg.setContent(PATHHUT);
                svg.setFill(fillColor(color));
                svg.setStroke(strokeColor(color));
            }
        };
        return svg;
    }
}

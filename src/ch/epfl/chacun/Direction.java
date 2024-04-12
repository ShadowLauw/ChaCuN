package ch.epfl.chacun;

import java.util.List;

/**
 * Represents the different directions of the board
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public enum Direction {
    N,
    E,
    S,
    W;

    /**
     * List of all the possible directions
     */
    public final static List<Direction> ALL = List.of(values());

    /**
     * Number of possible directions
     */
    public final static int COUNT = ALL.size();

    /**
     * Return the direction facing when rotated.
     *
     * @param rotation The rotation you want to apply.
     * @return The direction facing after the rotation.
     */
    public Direction rotated(Rotation rotation) {
        return ALL.get((this.ordinal() + rotation.quarterTurnsCW()) % COUNT);
    }

    /**
     * Return the opposite direction of the current direction
     *
     * @return the opposite direction of the current direction
     */
    public Direction opposite() {
        return rotated(Rotation.HALF_TURN);
    }
}

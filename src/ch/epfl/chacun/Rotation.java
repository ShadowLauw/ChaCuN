package ch.epfl.chacun;

import java.util.List;

/**
 * Represents the four possible rotations of a tile.
 *
 * @author Emmanuel Omont  (372632
 * @author Laura Paraboschi (364161)
 */
public enum Rotation {
    NONE,
    RIGHT,
    HALF_TURN,
    LEFT;

    /**
     * The list of all possible rotations.
     */
    public static final List<Rotation> ALL = List.of(values());

    /**
     * The number of possible rotations.
     */
    public static final int COUNT = ALL.size();

    /**
     * Returns the rotation obtained by adding the given rotation to the current one.
     *
     * @param that the rotation to add
     * @return the rotation obtained by adding the given rotation to the current one
     */
    public Rotation add(Rotation that) {
        int position = (this.ordinal() + that.ordinal()) % COUNT;
        return ALL.get(position);
    }

    /**
     * Returns the rotation that, with the current one, produce the null Rotation.
     *
     * @return the rotation obtained by subtracting the given rotation from the current one
     */
    public Rotation negated() {
        int position = (COUNT - this.ordinal()) % COUNT;
        return ALL.get(position);
    }

    /**
     * Returns the number of quarter turns required to obtain the current rotation
     *
     * @return the number of quarter turns required to obtain the current rotation
     */
    public int quarterTurnsCW() {
        return this.ordinal();
    }

    /**
     * Returns the number of degrees required to obtain the current rotation
     *
     * @return the number of degrees required to obtain the current rotation
     */
    public int degreesCW() {
        // The number of degrees in a quarter turn
        int QUARTER_TURN_DEGREES = 90;
        return this.ordinal() * QUARTER_TURN_DEGREES;
    }

    ;
}
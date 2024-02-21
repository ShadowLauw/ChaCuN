package ch.epfl.chacun;

import java.util.List;

/**
 * Represents the four possible rotations of a tile.
 *
 * @author Emmanuel Omont  (372632
 * @ author Laura Paraboschi (364161)
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
     * Returns the rotation obtained by adding the given rotation to this one.
     *
     * @param that the rotation to add
     * @return the rotation obtained by adding the given rotation to this one
     */
    public Rotation add(Rotation that) {
        int position = (this.ordinal() + that.ordinal())%COUNT;
        return ALL.get(position);
    };
    /**
     * Returns the rotation that, with this one, produce the null Rotation.
     *
     * @return the rotation obtained by subtracting the given rotation from this one
     */
    public Rotation negated() {
        int position = (COUNT - this.ordinal()) % COUNT;
        return ALL.get(position);
    };
    /**
     * Returns the number of quarter turns required to obtain this rotation from the null rotation.
     *
     * @return the number of quarter turns required to obtain this rotation from the null rotation
     */
    public int quarterTurnsCW() {
        return this.ordinal();
    };
    /**
     * Returns the number of degrees required to obtain this rotation from the null rotation.
     *
     * @return the number of degrees required to obtain this rotation from the null rotation
     */
    public int degreesCW() {
        return this.ordinal()*90;
    };
}
package ch.epfl.chacun;

import java.util.List;

public enum Direction {
    N,
    E,
    S,
    W;
    public static final List<Direction> ALL = List.of(values());
    public final static int COUNT = ALL.size();
    public Direction rotated(Rotation rotation) {
        return ALL.get((this.ordinal() + rotation.quarterTurnsCW()) % COUNT);
    }

    public Direction opposite() {
        return rotated(Rotation.HALF_TURN);
    }
}

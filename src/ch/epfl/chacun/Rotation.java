package ch.epfl.chacun;

import java.util.List;

public enum Rotation {
    NONE,
    RIGHT,
    HALF_TURN,
    LEFT;
    public static final List<Rotation> ALL = List.of(values());
    public static final int COUNT = ALL.size();
    public Rotation add(Rotation that) {
        int position = (this.ordinal() + that.ordinal())%COUNT;
        return ALL.get(position);
    };
    public Rotation negated() {
        int position = (COUNT-this.ordinal());
        return ALL.get(position);
    };
    public int quarterTurnsCW() {
        return this.ordinal();
    };
    public int degreesCW() {
        return this.ordinal()*90;
    };
}
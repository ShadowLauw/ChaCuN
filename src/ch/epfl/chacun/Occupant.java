package ch.epfl.chacun;

import static ch.epfl.chacun.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public record Occupant(Kind kind, int zoneId) {

    private static final int PAWN_COUNT = 5;
    private static final int HUT_COUNT = 3;

    public enum Kind {PAWN, HUT}
    public Occupant {
        requireNonNull(kind);
        checkArgument(zoneId > 0);
    }

    public static int occupantsCount(Kind kind) {
        return switch (kind) {
            case PAWN -> PAWN_COUNT;
            case HUT -> HUT_COUNT;
        };
    }
}

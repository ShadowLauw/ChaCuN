package ch.epfl.chacun;

import static ch.epfl.chacun.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents the different occupants of the board
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 *
 * @param kind The kind of the occupant
 * @param zoneId The ID of the zone the occupant is in
 */
public record Occupant(Kind kind, int zoneId) {

    /**
     * The count of the pawn a player has at the start of the game
     */
    private static final int PAWN_COUNT = 5;
    /**
     * The count of the hut a player has at the start of the game
     */
    private static final int HUT_COUNT = 3;

    /**
     * Represents the different kinds of occupants
     */
    public enum Kind {PAWN, HUT}
    /**
     * Creates a new occupant with the given kind and zoneID
     *
     * @param kind The kind of the occupant
     * @param zoneId The ID of the zone the occupant is in
     *
     * @throws NullPointerException if the kind is null
     * @throws IllegalArgumentException if the zoneId is not positive
     */
    public Occupant {
        requireNonNull(kind);
        checkArgument(zoneId > 0);
    }

    /**
     * Returns the count of the given kind of occupant
     *
     * @param kind The kind of the occupant
     *
     * @return the count of the given kind of occupant
     */
    public static int occupantsCount(Kind kind) {
        return switch (kind) {
            case PAWN -> PAWN_COUNT;
            case HUT -> HUT_COUNT;
        };
    }
}

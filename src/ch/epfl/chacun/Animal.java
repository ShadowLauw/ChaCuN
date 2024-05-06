package ch.epfl.chacun;

/**
 * Represents an animal in the game.
 *
 * @param id   the unique identifier of the animal
 * @param kind the kind of the animal
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public record Animal(int id, Kind kind) {
    /**
     *  The divider to get the zone ID from the animal ID
     */
    private static final int ZONE_ID_DIVIDER = 10;

    /**
     * Represents the different kinds of animals
     */
    public enum Kind {MAMMOTH, AUROCHS, DEER, TIGER}

    /**
     * Returns the tileID the animal is on
     *
     * @return the tileID the animal is on
     */
    public int tileId() {
        return Zone.tileId(id / ZONE_ID_DIVIDER);
    }
}

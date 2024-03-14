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
     * Constant to get the tileID from the animal ID
     */
    static final int TILE_ID_DIVIDER = 100;

    /**
     * Represents the different kinds of animals
     */
    public enum Kind {MAMMOTH, AUROCHS, DEER, TIGER}

    /**
     * Returns the tileID the animal is on
     *
     * @return (int) the tileID the animal is on
     */
    public int tileId() {
        return id / TILE_ID_DIVIDER;
    }
}

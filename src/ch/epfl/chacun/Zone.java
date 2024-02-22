package ch.epfl.chacun;

import java.util.List;
/**
 * Represents the different zones of the board
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 *
 * @implSpec This interface is sealed and cannot be implemented outside of this file.
 */
public sealed interface Zone {
    /**
     * Constant to get the tileID from the zone ID
     */
    int TILE_ID_DIVIDER = 10;
    /**
     * Represents the different special powers
     */
    enum SpecialPower {
        SHAMAN,
        LOGBOAT,
        HUNTING_TRAP,
        PIT_TRAP,
        WILD_FIRE,
        RAFT
    }
    /**
     * Returns the tileID the given zone is on
     *
     * @param zoneId (int) the ID of the zone
     * @return (int) the tileID the zone is on
     */
    static int tileId(int zoneId) {
        return zoneId / TILE_ID_DIVIDER;
    }
    /**
     * Returns the localID of the given zone
     *
     * @param zoneId (int) the ID of the zone
     * @return (int) the localID of the zone
     */
    static int localId(int zoneId) {
        return zoneId % TILE_ID_DIVIDER;
    }
    /**
     * Returns the ID of the current zone
     * @return (int) the ID of the current zone
     */
    int id();
    /**
     * Returns the tileID the current zone is on
     * @return (int) the tileID the current zone is on
     */
    default int tileId() {
        return id() / TILE_ID_DIVIDER;
    }
    /**
     * Returns the localID of the current zone
     * @return (int) the localID of the current zone
     */
    default int localId() {
        return id() % TILE_ID_DIVIDER;
    }
    /**
     * Returns the special power of the current zone
     * @return (SpecialPower) the special power of the current zone
     */
    default SpecialPower specialPower() {
        return null;
    }

    /**
     * Represents the different kinds of forests
     *
     * @param id (int) the unique identifier of the forest
     * @param kind (Kind) the kind of the forest
     */
    record Forest(int id, Kind kind) implements Zone {
        /**
         * The different kinds of forests
         */
        public enum Kind {PLAIN, WITH_MENHIR, WITH_MUSHROOMS}
    }
    /**
     * Represents the different kinds of meadows
     *
     * @param id (int) the unique identifier of the meadow
     * @param animals (List<Animal>) the animals in the meadow
     * @param specialPower (SpecialPower) the special power of the meadow
     */
    record Meadow(int id, List<Animal> animals, SpecialPower specialPower) implements Zone {
        /**
         * Creates a new meadow with the given animals and special power
         *
         * @param id (int) the unique identifier of the meadow
         * @param animals (List<Animal>) the animals in the meadow
         * @param specialPower (SpecialPower) the special power of the meadow
         *
         * @implSpec Ensure that the animals list is not null and unmodifiable
         */
        public Meadow {
            animals = List.copyOf(animals);
        }
    }

    /**
     * An interface representing a water zone.
     * @implSpec This interface is sealed and cannot be implemented outside of this file.
     */
    sealed interface Water extends Zone {
        /**
         * Returns the number of fish in the water
         * @return (int) the number of fish in the water
         */
        int fishCount();
    }
    /**
     * Represents the different kinds of lakes
     *
     * @param id (int) the unique identifier of the lake
     * @param fishCount (int) the number of fish in the lake
     * @param specialPower (SpecialPower) the special power of the lake
     */
    record Lake(int id, int fishCount, SpecialPower specialPower) implements Water {
    }

    /**
     * Represents the different kinds of rivers
     *
     * @param id (int) the unique identifier of the river
     * @param fishCount (int) the number of fish in the river
     * @param lake (Lake) the lake the river is connected to
     */
    record River(int id, int fishCount, Lake lake) implements Water {

        /**
         * Checks if the river is connected to a lake
         * @return (boolean) true if the river is connected to a lake, false otherwise
         */
        public boolean hasLake() {
            return lake != null;
        }
    }
}

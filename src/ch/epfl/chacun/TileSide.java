package ch.epfl.chacun;

import java.util.List;

/**
 * Represents the different sides of the tiles
 *
 * @author Emmanuel Omont (372632)
 * @author Laura Paraboschi (364161)
 */
public sealed interface TileSide {
    /**
     * Returns the zones of the current side
     *
     * @return a list of Zones representing the zones of the current side
     */
    List<Zone> zones();

    /**
     * Returns true if the given side is of the same kind as the current one
     *
     * @param that the side to compare
     * @return true if the given side is of the same kind as the current one
     */
    boolean isSameKindAs(TileSide that);

    /**
     * Public record representing the forest tile side
     *
     * @param forest the forest zone
     */
    record Forest(Zone.Forest forest) implements TileSide {
        /**
         * Returns the zones of the current side
         *
         * @return a list of Zones representing the zones of the current side
         */
        @Override
        public List<Zone> zones() {
            return List.of(forest);
        }

        /**
         * Returns true if the given side is of the same kind as the current one
         *
         * @param that the side to compare
         * @return true if the given side is of the same kind as the current one
         */
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof Forest;
        }
    }

    /**
     * Public record representing the meadow tile side
     *
     * @param meadow the meadow zone
     */
    record Meadow(Zone.Meadow meadow) implements TileSide {
        /**
         * Returns the zones of the current side
         *
         * @return (List < Zones >) a list of Zones representing the zones of the current side
         */
        @Override
        public List<Zone> zones() {
            return List.of(meadow);
        }

        /**
         * Returns true if the given side is of the same kind as the current one
         *
         * @param that the side to compare
         * @return true if the given side is of the same kind as the current one
         */
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof Meadow;
        }
    }

    /**
     * Public record representing the river tile side
     *
     * @param meadow1 the first meadow zone
     * @param river   the river zone
     * @param meadow2 the second meadow zone
     */
    record River(Zone.Meadow meadow1, Zone.River river, Zone.Meadow meadow2) implements TileSide {
        /**
         * Returns the zones of the current side
         *
         * @return a list of Zones representing the zones of the current side
         */
        @Override
        public List<Zone> zones() {
            return List.of(meadow1, river, meadow2);
        }

        /**
         * Returns true if the given side is of the same kind as the current one
         *
         * @param that the side to compare
         * @return true if the given side is of the same kind as the current one
         */
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof River;
        }
    }
}

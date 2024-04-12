package ch.epfl.chacun;

import java.util.List;
import java.util.Set;

/**
 * Represents the different zone partitions of the board
 *
 * @param forests      the forest zone partition
 * @param meadows      the meadow zone partition
 * @param rivers       the river zone partition
 * @param riverSystems the river system zone partition
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public record ZonePartitions(
        ZonePartition<Zone.Forest> forests,
        ZonePartition<Zone.Meadow> meadows,
        ZonePartition<Zone.River> rivers,
        ZonePartition<Zone.Water> riverSystems
) {
    /**
     * Number of sides of a tile
     */
    private final static int NUMBER_OF_SIDES = 4;

    /**
     * Max number of zones of a tile
     */
    private final static int NUMBER_OF_ZONES = 10;

    /**
     * Represents the empty zone partitions
     */
    public final static ZonePartitions EMPTY = new ZonePartitions(
            new ZonePartition<>(Set.of()),
            new ZonePartition<>(Set.of()),
            new ZonePartition<>(Set.of()),
            new ZonePartition<>(Set.of())
    );

    /**
     * Builder of ZonePartitions
     */
    public static final class Builder {
        private final ZonePartition.Builder<Zone.Forest> forests;
        private final ZonePartition.Builder<Zone.Meadow> meadows;
        private final ZonePartition.Builder<Zone.River> rivers;
        private final ZonePartition.Builder<Zone.Water> riverSystems;

        /**
         * Constructs a ZonePartitions builder with the given initial zone partitions
         *
         * @param initial the initial zone partitions
         */
        public Builder(ZonePartitions initial) {
            forests = new ZonePartition.Builder<>(initial.forests());
            meadows = new ZonePartition.Builder<>(initial.meadows());
            rivers = new ZonePartition.Builder<>(initial.rivers());
            riverSystems = new ZonePartition.Builder<>(initial.riverSystems());
        }

        /**
         * Adds all the zones of the given tile to the zone partitions
         *
         * @param tile the tile containing the zones to add
         */
        public void addTile(Tile tile) {
            int[] openConnections = new int[NUMBER_OF_ZONES];
            List<TileSide> sides = tile.sides();
            for (int i = 0; i < NUMBER_OF_SIDES; i++) {
                for (Zone zone : sides.get(i).zones()) {
                    openConnections[zone.localId()]++;
                    if (zone instanceof Zone.River river && river.hasLake()) {
                        openConnections[river.lake().localId()]++;
                        openConnections[river.localId()]++;
                    }
                }
            }
            Set<Zone> zones = tile.zones();
            for (Zone zone : zones) {
                int openConnectionsCount = openConnections[zone.localId()];
                switch (zone) {
                    case Zone.Forest forest -> forests.addSingleton(forest, openConnectionsCount);
                    case Zone.Meadow meadow -> meadows.addSingleton(meadow, openConnectionsCount);
                    case Zone.River river -> {
                        rivers.addSingleton(river, river.hasLake() ? openConnectionsCount - 1 : openConnectionsCount);
                        riverSystems.addSingleton(river, openConnectionsCount);
                    }
                    case Zone.Lake lake -> riverSystems.addSingleton(lake, openConnectionsCount);
                }
            }

            for (Zone zone : zones) {
                if (zone instanceof Zone.River river && river.hasLake()) {
                    riverSystems.union(river, river.lake());
                }
            }
        }

        /**
         * Connects the given tile sides in the zone partitions
         *
         * @param s1 the first side to connect
         * @param s2 the second side to connect
         * @throws IllegalArgumentException if the given sides are not of the same kind, or if the zones
         *                                  are not in the partition
         */
        public void connectSides(TileSide s1, TileSide s2) {
            switch (s1) {
                case TileSide.Meadow(Zone.Meadow m1) when s2 instanceof TileSide.Meadow(Zone.Meadow m2) ->
                        meadows.union(m1, m2);
                case TileSide.Forest(Zone.Forest f1) when s2 instanceof TileSide.Forest(Zone.Forest f2) ->
                        forests.union(f1, f2);
                case TileSide.River(Zone.Meadow m1_1, Zone.River r1, Zone.Meadow m1_2)
                        when s2 instanceof TileSide.River(Zone.Meadow m2_1, Zone.River r2, Zone.Meadow m2_2) -> {
                    meadows.union(m1_1, m2_2);
                    meadows.union(m1_2, m2_1);
                    rivers.union(r1, r2);
                    riverSystems.union(r1, r2);
                }
                default -> throw new IllegalArgumentException();
            }
        }

        /**
         * Adds the given initial occupant to the zone partitions
         *
         * @param player       the player of the initial occupant
         * @param occupantKind the kind of the initial occupant
         * @param occupiedZone the zone where the initial occupant has to be added
         * @throws IllegalArgumentException if the given occupant is the wrong kind for the zone or if
         *                                  the area of the zone is already occupied, or if the zone is not in the partition
         */
        public void addInitialOccupant(PlayerColor player, Occupant.Kind occupantKind, Zone occupiedZone) {
            switch (occupiedZone) {
                case Zone.Forest forest when occupantKind.equals(Occupant.Kind.PAWN) ->
                        forests.addInitialOccupant(forest, player);
                case Zone.Meadow meadow when occupantKind.equals(Occupant.Kind.PAWN) ->
                        meadows.addInitialOccupant(meadow, player);
                case Zone.River river when occupantKind.equals(Occupant.Kind.PAWN) ->
                        rivers.addInitialOccupant(river, player);
                case Zone.Water water when occupantKind.equals(Occupant.Kind.HUT) ->
                        riverSystems.addInitialOccupant(water, player);
                default -> throw new IllegalArgumentException();
            }
        }

        /**
         * Removes the given pawn from the zone given
         *
         * @param player       the player of the pawn to remove
         * @param occupiedZone the zone where the pawn has to be removed
         * @throws IllegalArgumentException if the zone is a lake or if the area of the zone
         *                                  does not contain a pawn of the given player, or if the zone is not in the partition
         */
        public void removePawn(PlayerColor player, Zone occupiedZone) {
            switch (occupiedZone) {
                case Zone.Forest forest -> forests.removeOccupant(forest, player);
                case Zone.Meadow meadow -> meadows.removeOccupant(meadow, player);
                case Zone.River river -> rivers.removeOccupant(river, player);
                default -> throw new IllegalArgumentException();
            }
        }

        /**
         * Clears the pawns from the given forest
         *
         * @param forest the forest to clear
         * @throws IllegalArgumentException if the zone is not in the partition
         */
        public void clearGatherers(Area<Zone.Forest> forest) {
            forests.removeAllOccupantsOf(forest);
        }

        /**
         * Clears the pawns from the given river
         *
         * @param river the river to clear
         * @throws IllegalArgumentException if the zone is not in the partition
         */
        public void clearFishers(Area<Zone.River> river) {
            rivers.removeAllOccupantsOf(river);
        }

        /**
         * Builds the ZonePartitions
         *
         * @return the built ZonePartitions
         */
        public ZonePartitions build() {
            return new ZonePartitions(forests.build(), meadows.build(), rivers.build(), riverSystems.build());
        }
    }
}

package ch.epfl.chacun;

import java.util.Set;

public record ZonePartitions (ZonePartition<Zone.Forest> forests, ZonePartition<Zone.Meadow> meadows, ZonePartition<Zone.River> rivers, ZonePartition<Zone.Water> riverSystems){

    public final static ZonePartitions EMPTY = new ZonePartitions(new ZonePartition<>(Set.of()), new ZonePartition<>(Set.of()), new ZonePartition<>(Set.of()), new ZonePartition<>(Set.of()));

    public final class Builder {
        private ZonePartition.Builder<Zone.Forest> forests;
        private ZonePartition.Builder<Zone.Meadow> meadows;
        private ZonePartition.Builder<Zone.River> rivers;
        private ZonePartition.Builder<Zone.Water> riverSystems;

        Builder (ZonePartitions initial) {

        }


        public void addTile (Tile tile){

        }
        public void connectSide (TileSide s1, TileSide s2){

        }

        public void addInitialOccupant (PlayerColor player, Occupant.Kind occupantKind, Zone occupiedZone){

        }

        public void removePawn(PlayerColor player, Zone occupiedZone) {

        }

        public void clearGatherers(Area<Zone.Forest> forest) {

        }

        public void clearFishers(Area<Zone.River> river) {

        }

        public ZonePartitions build() {
            return null;
        }
    }
}

package ch.epfl.chacun;

import java.util.List;
import java.util.Set;

public record ZonePartition<Z extends Zone> (Set<Area<Z>> zones){
    public ZonePartition(Set<Area<Z>> zones) {
        this.zones = Set.copyOf(zones);
    }
    public ZonePartition() {
        this(Set.of());
    }
    public Area<Z> areaContaining (int zoneId) {
        return null;
    }

    class Builder {
        public Builder addArea(Area<Z> area) {
            return null;
        }
        public void addSingleton (Z zone, int OpenConnections) {

        }
        public void addInitialOccupant (Z zone, PlayerColor color) {

        }
        public void removeOccupant (Z zone, PlayerColor color) {

        }
        public void removeAllOccupantsOf (Area<Z> area) {

        }
        public void union (Z zone1, Z zone2) {

        }
        public ZonePartition<Z> build () {
            return null;
        }
    }

}

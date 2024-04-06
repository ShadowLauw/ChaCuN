package ch.epfl.chacun;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the different zones of the board
 *
 * @param <Z>   the type of the zones
 * @param areas the areas of the zone partition
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public record ZonePartition<Z extends Zone>(Set<Area<Z>> areas) {
    /**
     * Constructs a zone partition with the given areas
     *
     * @param areas the areas of the zone partition
     */
    public ZonePartition {
        areas = Set.copyOf(areas);
    }

    /**
     * Constructs a zone partition with no areas
     */
    public ZonePartition() {
        this(Set.of());
    }

    /**
     * Returns the area containing the given zone
     *
     * @param zone the zone to check
     * @return the area containing the given zone
     * @throws IllegalArgumentException if the zone is not in any area of the partition
     */
    public Area<Z> areaContaining(Z zone) {
        return staticAreaContaining(zone, areas);
    }

    /**
     * Returns the area containing the given zone
     *
     * @param <Z>   the type of the zones
     * @param zone  the zone to check
     * @param areas the areas to check
     * @return the area containing the given zone
     * @throws IllegalArgumentException if the zone is not in any area of the partition
     */
    private static <Z extends Zone> Area<Z> staticAreaContaining(Z zone, Set<Area<Z>> areas) {
        for (Area<Z> area : areas) {
            if (area.zones().contains(zone)) {
                return area;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Builder for ZonePartition
     *
     * @param <Z> the type of the zones
     */
    public static final class Builder<Z extends Zone> {
        private final Set<Area<Z>> areas;

        /**
         * Constructs a builder of ZonePartition with the given zone partition
         *
         * @param zonePartition the zone partition
         */
        public Builder(ZonePartition<Z> zonePartition) {
            areas = new HashSet<>(zonePartition.areas());
        }

        /**
         * Add a new area to the partition with the single given zone and open connections
         *
         * @param zone the zone of the area
         */
        public void addSingleton(Z zone, int openConnections) {
            areas.add(new Area<>(Set.of(zone), List.of(), openConnections));
        }

        /**
         * Add an occupant to the area in the partition of the given zone
         *
         * @param zone  the zone of the area
         * @param color the color of the occupant
         * @throws IllegalArgumentException if the area is already occupied or if the area of the zone is not
         *                                  in the partition
         */
        public void addInitialOccupant(Z zone, PlayerColor color) {
            Area<Z> areaContainingZone = staticAreaContaining(zone, areas);

            //throws IllegalArgumentException if the area is already occupied
            areas.add(areaContainingZone.withInitialOccupant(color));
            areas.remove(areaContainingZone);
        }

        /**
         * Remove an occupant of a certain color from the area in the partition of the given zone
         *
         * @param zone  the zone of the area
         * @param color the color of the occupant
         * @throws IllegalArgumentException if the area doesn't contain an occupant of the color given or if the
         *                                  area of the zone is not in the partition
         */
        public void removeOccupant(Z zone, PlayerColor color) {
            Area<Z> areaContainingZone = staticAreaContaining(zone, areas);

            //throws IllegalArgumentException if the area doesn't contain an occupant of the color given
            areas.add(areaContainingZone.withoutOccupant(color));
            areas.remove(areaContainingZone);
        }

        /**
         * Remove all the occupants from the area (if in the partition)
         *
         * @param area the area which needs its occupants to be removed
         * @throws IllegalArgumentException if the area is not in the partition
         */
        public void removeAllOccupantsOf(Area<Z> area) {
            Preconditions.checkArgument(areas.contains(area));

            areas.remove(area);
            areas.add(area.withoutOccupants());
        }

        /**
         * Connect the two areas of the given zones (if in the partition)
         *
         * @param zone1 the first zone
         * @param zone2 the second zone
         * @throws IllegalArgumentException if the areas of the zone are not in the partition
         */
        public void union(Z zone1, Z zone2) {
            Area<Z> areaContainingZone1 = staticAreaContaining(zone1, areas);
            Area<Z> areaContainingZone2 = staticAreaContaining(zone2, areas);

            if (areaContainingZone1.equals(areaContainingZone2))
                areas.remove(areaContainingZone1);
            else {
                areas.remove(areaContainingZone1);
                areas.remove(areaContainingZone2);
            }
            areas.add(areaContainingZone1.connectTo(areaContainingZone2));
        }

        /**
         * Builds the zone partition
         *
         * @return the built zone partition
         */
        public ZonePartition<Z> build() {
            return new ZonePartition<>(areas);
        }
    }
}

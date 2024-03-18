package ch.epfl.chacun;

import java.util.*;

/**
 * Represents the different areas of the board
 *
 * @param zones           the zones of the area
 * @param occupants       the occupants of the area
 * @param openConnections the open connections of the area
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public record Area<Z extends Zone>(Set<Z> zones, List<PlayerColor> occupants, int openConnections) {
    /**
     * Constructs an area with the given zones, occupants and open connections
     *
     * @param zones           the zones of the area
     * @param occupants       the occupants of the area
     * @param openConnections the open connections of the area
     * @throws IllegalArgumentException if the number of openConnections is negative
     */
    public Area {
        Preconditions.checkArgument(openConnections >= 0);

        zones = Set.copyOf(zones);

        List<PlayerColor> occupantToSort = new ArrayList<>(occupants);
        Collections.sort(occupantToSort);
        occupants =
                // Use Collections.unmodifiableList instead of List.copyOf to avoid a list copy
                // Area is still immutable because occupantToSort doesn't exist outside the constructor, so
                // it can't be modified
                Collections.unmodifiableList(occupantToSort);
    }

    /**
     * Returns true if the area has a menhir
     *
     * @param forest the area to check
     * @return true if the area has a menhir
     */
    public static boolean hasMenhir(Area<Zone.Forest> forest) {
        for (Zone.Forest zone : forest.zones()) {
            if (zone.kind() == Zone.Forest.Kind.WITH_MENHIR)
                return true;
        }
        return false;
    }

    /**
     * Returns the number of zones with mushrooms in the given area
     *
     * @param forest the area to check
     * @return the number of zones with mushrooms in the given area
     */
    public static int mushroomGroupCount(Area<Zone.Forest> forest) {
        int number = 0;
        for (Zone.Forest zone : forest.zones()) {
            if (zone.kind() == Zone.Forest.Kind.WITH_MUSHROOMS)
                number++;
        }
        return number;
    }

    /**
     * Return the list of animals present in the given area
     *
     * @param meadow           the area to check
     * @param cancelledAnimals the animals to exclude from the list
     * @return the set of animals present in the given area
     */
    public static Set<Animal> animals(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animalsList = new HashSet<>();
        for (Zone.Meadow zone : meadow.zones()) {
            for (Animal animal : zone.animals()) {
                if (!cancelledAnimals.contains(animal)) {
                    animalsList.add(animal);
                }
            }
        }
        return animalsList;
    }

    /**
     * Returns the number of fish in the given river
     *
     * @param river the river to check
     * @return the number of fish in the given river
     */
    public static int riverFishCount(Area<Zone.River> river) {
        int fishCount = 0;
        Set<Zone.Lake> lakes = new HashSet<>();
        for (Zone.River zone : river.zones()) {
            fishCount += zone.fishCount();
            if (zone.hasLake() && lakes.add(zone.lake())) {
                fishCount += zone.lake().fishCount();
            }
        }
        return fishCount;
    }

    /**
     * Returns the number of fish in the given river system
     *
     * @param riverSystem the river system to check
     * @return the number of fish in the given river system
     */
    public static int riverSystemFishCount(Area<Zone.Water> riverSystem) {
        int fishCount = 0;
        for (Zone.Water zone : riverSystem.zones()) {
            fishCount += zone.fishCount();
        }
        return fishCount;
    }

    /**
     * Returns the number of lakes in the given river system
     *
     * @param riverSystem the river system to check
     * @return the number of lakes in the given river system
     */
    public static int lakeCount(Area<Zone.Water> riverSystem) {
        int lakeCount = 0;
        for (Zone.Water zone : riverSystem.zones()) {
            if (zone instanceof Zone.Lake)
                lakeCount++;
        }
        return lakeCount;
    }

    /**
     * Returns a boolean for if a given area is closed
     *
     * @return true if the area is closed
     */
    public boolean isClosed() {
        return openConnections == 0;
    }

    /**
     * Returns a boolean for if a given area is occupied
     *
     * @return true if the area is occupied
     */
    public boolean isOccupied() {
        return !occupants.isEmpty();
    }

    /**
     * Returns the majority occupants of a given area
     *
     * @return the majority occupants of the area
     */
    public Set<PlayerColor> majorityOccupants() {
        int[] count = new int[PlayerColor.ALL.size()];
        int max = 0;
        for (PlayerColor occupant : occupants) {
            count[occupant.ordinal()]++;
            if (count[occupant.ordinal()] > max)
                max = count[occupant.ordinal()];
        }
        Set<PlayerColor> majority = new HashSet<>();
        if (max == 0)
            return majority;
        else {
            for (PlayerColor playerColor : PlayerColor.ALL) {
                if (count[playerColor.ordinal()] == max)
                    majority.add(playerColor);
            }
        }
        return majority;
    }

    /**
     * Return a new Area with the given area connected to the current area
     *
     * @param that the area to connect to
     * @return the new area
     */
    public Area<Z> connectTo(Area<Z> that) {
        int newOpenConnections = openConnections - 2;
        Set<Z> zones = new HashSet<>(this.zones);
        List<PlayerColor> newOccupants = new ArrayList<>(this.occupants);

        if (!that.equals(this)) {
            newOccupants.addAll(that.occupants());
            zones.addAll(that.zones());
            newOpenConnections += that.openConnections();
        }
        return new Area<>(zones, newOccupants, newOpenConnections);
    }

    /**
     * Return a new area with the given occupant added
     *
     * @param occupant the occupant to add
     * @return the new area
     * @throws IllegalArgumentException if the area is already occupied
     */
    public Area<Z> withInitialOccupant(PlayerColor occupant) {
        Preconditions.checkArgument(!isOccupied());
        return new Area<>(zones, List.of(occupant), openConnections);
    }

    /**
     * Return a new area with the given occupant removed
     *
     * @param occupant the occupant to remove
     * @return the new area
     * @throws IllegalArgumentException if the area doesn't contain an occupant of the given color
     */
    public Area<Z> withoutOccupant(PlayerColor occupant) {
        Preconditions.checkArgument(occupants.contains(occupant));
        List<PlayerColor> newOccupant = new ArrayList<>(occupants);
        newOccupant.remove(occupant);
        return new Area<>(zones, newOccupant, openConnections);
    }

    /**
     * Return a new area with no occupants
     *
     * @return the new area
     */
    public Area<Z> withoutOccupants() {
        return new Area<>(zones, List.of(), openConnections);
    }

    /**
     * Return the list of the tileIds of the zones in the area
     *
     * @return the set of tileIds
     */
    public Set<Integer> tileIds() {
        Set<Integer> tileIds = new HashSet<>();
        for (Z zone : zones) {
            tileIds.add(zone.tileId());
        }
        return tileIds;
    }

    /**
     * Return the zone with the given special power
     *
     * @param specialPower the special power to look for
     * @return the zone with the given special power or null if there is none
     */
    public Zone zoneWithSpecialPower(Zone.SpecialPower specialPower) {
        for (Z zone : zones) {
            if (zone.specialPower() == specialPower)
                return zone;
        }
        return null;
    }

}

package ch.epfl.chacun;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Represents the different areas of the board
 *
 * @param <Z> the type of the zones in the area
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public record Area <Z extends Zone> (Set<Z> zones, List<PlayerColor> occupants, int openConnection) {
    /**
     * Constructs an area with the given zones, occupants and open connections
     *
     * @param zones (Set<Z>) the zones of the area
     * @param occupants (List<PlayerColor>) the occupants of the area
     * @param openConnection (int) the open connections of the area
     */
    public Area(Set<Z> zones, List<PlayerColor> occupants, int openConnection) {
        Preconditions.checkArgument(openConnection >= 0);
        this.zones = Set.copyOf(zones);
        this.occupants = List.copyOf(occupants);
        //pourquoi pas faire une variable intermédiaire
        Collections.sort(this.occupants);
        this.openConnection = openConnection;
    }
    /**
     * Returns true if the area has a menhir
     *
     * @param forest (Area<Zone.Forest>) the area to check
     * @return (boolean) true if the area has a menhir
     */
    static boolean hasMenhir (Area<Zone.Forest> forest) {
        for (Zone.Forest zone : forest.zones) {
            if (zone.kind() == Zone.Forest.Kind.WITH_MENHIR) return true;
        }
        return false;
    }
    /**
     * Returns the number of zones with mushrooms in the given area
     *
     * @param forest (Area<Zone.Forest>) the area to check
     * @return (int) the number of zones with mushrooms in the given area
     */
    static int mushroomGroup (Area<Zone.Forest> forest) {
        int number = 0;
        for (Zone.Forest zone : forest.zones) {
            if (zone.kind() == Zone.Forest.Kind.WITH_MUSHROOMS) number+=1;
        }
        return number;
    }

    /**
     * Return the list of aniumals present in the given area
     *
     * @param meadow
     * @param cancelledAnimals
     * @return the list of animals present in the given area
     */
    static Set<Animal> animals (Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animalsList = new HashSet<>();
        for (Zone.Meadow zone : meadow.zones) {
            for (Animal animal : zone.animals()) {
                if (isInList(animal, cancelledAnimals)) {
                    animalsList.add(animal);
                }
            }
        }
        return animalsList;
    }
    /**
     * Returns true if the given element is in the given list
     *
     * @param element (T) the element to check
     * @param list (Set<T>) the list to check
     * @return (boolean) true if the given element is in the given list
     */
    static private<T> boolean isInList (T element, Set<T> list) {
        for (T e : list) {
            if (e == element) return true;
        }
        return false;
    }
    /**
     * Returns the number of fish in the given river
     *
     * @param river (Area<Zone.River>) the river to check
     * @return (int) the number of fish in the given river
     */
    static int riverFishCount (Area<Zone.River> river) {
        int fishCount = 0;
        Set<Zone.Lake> lakes = new HashSet<>();
        for (Zone.River zone : river.zones) {
            fishCount += zone.fishCount();
            if(zone.hasLake() && lakes.add(zone.lake())){
                fishCount += zone.lake().fishCount();
            }
        }
        return fishCount;
    }
    /**
     * Returns the number of fish in the given river system
     *
     * @param riverSystem (Area<Zone.Water>) the river system to check
     * @return (int) the number of fish in the given river system
     */
    static int riverSystemFishCount (Area<Zone.Water> riverSystem) {
        int fishCount = 0;
        for (Zone.Water zone : riverSystem.zones) {
            fishCount += zone.fishCount();
        }
        return fishCount;
    }
    /**
     * Returns the number of lakes in the given river system
     *
     * @param riverSystem (Area<Zone.Water>) the river system to check
     * @return (int) the number of lakes in the given river system
     */
    static int lakeCount (Area<Zone.Water> riverSystem) {
        int lakeCount = 0;
        for (Zone.Water zone : riverSystem.zones) {
            if (zone instanceof Zone.Lake) lakeCount += 1;
        }
        return lakeCount;
    }

    /**
     * Returns a boolean for if a given area is closed
     * @return (boolean) true if the area is closed
     */
    public boolean isClosed() {
        return openConnection == 0;
    }
    /**
     * Returns a boolean for if a given area is occupied
     * @return (boolean) true if the area is occupied
     */
    public boolean isOccupied() {
        return !occupants.isEmpty();
    }
    /**
     * Returns the majority occupants of a given area
     * @return (Set<PlayerColor>) the majority occupants of the area
     */
    public Set<PlayerColor> majorityOccupants() {
        int[] count = new int[PlayerColor.values().length];
        for (PlayerColor occupant : occupants) {
            count[occupant.ordinal()] += 1;
        }
        int max = 0;
        for (int i = 0; i < count.length; i++) {
            if (count[i] > count[max]) max = i;
        }
        Set<PlayerColor> majority = new HashSet<>();
        for (int i = 0; i < count.length; i++) {
            if (count[i] == count[max]) majority.add(PlayerColor.values()[i]);
        }
        return majority;
    }

    /**
     * Return a new Area with the given area added
     * @param that (Area<Z>) the area to connect to
     * @return (Area<Z>) the new area
     */
    public Area<Z> connectTo(Area<Z> that) {
        int newOpenConnections = this.openConnection + that.openConnection;
        Set<Z> zones = new HashSet<>(this.zones);
        List<PlayerColor> newOccupants = List.of();
        newOccupants.addAll(this.occupants);
        newOccupants.addAll(that.occupants);

        for (Z zone : that.zones){
            if (zones.contains(zone)) {
                newOpenConnections -= 2;
            } else {
                zones.add(zone);
                newOpenConnections -= 2;
            }
        }
        return new Area<>(zones, newOccupants, newOpenConnections);
    }

    /**
     * Return a new area with the given occupant added
     * @param occupant (PlayerColor) the occupant to add
     * @return (Area<Z>) the new area
     */
    public Area<Z> withInitialOccupant (PlayerColor occupant) {
        Preconditions.checkArgument(isOccupied());
        return new Area<>(zones, List.of(occupant), openConnection);
    }
    /**
     * Return a new area with the given occupant removed
     * @param occupant (PlayerColor) the occupant to remove
     * @return (Area<Z>) the new area
     */
    public Area<Z> withoutOccupant (PlayerColor occupant) {
        Preconditions.checkArgument(occupants.contains(occupant));
        occupants.remove(occupant);
        return new Area<>(zones, occupants, openConnection);
    }
    /**
     * Return a new area with no occupants
     * @return (Area<Z>) the new area
     */
    public Area<Z> withoutOccupant () {
        return new Area<>(zones, Collections.emptyList(), openConnection);
    }

    /**
     * Return the list of the tileIds of the zones in the area
     * @return (Set<Integer>) the list of tileIds
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
     * @param specialPower (Zone.SpecialPower) the special power to look for
     * @return (Zone) the zone with the given special power
     */
    public Zone zoneWithSpecialPower (Zone.SpecialPower specialPower) {
        for (Z zone : zones) {
            if (zone.specialPower() == specialPower) return zone;
        }
        return null;
    }

}

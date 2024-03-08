package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LauraAreaTest {

    @Test
    void areaThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Area<>(Set.of(), List.of(), -1));
    }

    @Test
    void isImmutable() {
        Set<Zone.Forest> set = new HashSet<>();
        set.add(new Zone.Forest(1, Zone.Forest.Kind.PLAIN));
        Area<Zone.Forest> forest = new Area<>(set, List.of(), 0);
        set.add(new Zone.Forest(2, Zone.Forest.Kind.WITH_MUSHROOMS));
        assertNotEquals(forest.zones(), set);
        List<PlayerColor> list = new ArrayList<>(List.of(PlayerColor.BLUE));
        forest = new Area<>(set, list, 0);
        list.add(PlayerColor.RED);
        assertNotEquals(forest.occupants(), list);
    }

    @Test
    void isColorSorted() {
        List<PlayerColor> list = new ArrayList<>(List.of(PlayerColor.PURPLE, PlayerColor.BLUE, PlayerColor.GREEN));
        Area<Zone.Forest> forest = new Area<>(Set.of(), list, 0);
        assertEquals(List.of(PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.PURPLE), forest.occupants());
    }
    @Test
    void hasMenhirWorks() {
        Set<Zone.Forest> set = new HashSet<>();
        set.add(new Zone.Forest(1, Zone.Forest.Kind.PLAIN));
        set.add(new Zone.Forest(2, Zone.Forest.Kind.WITH_MUSHROOMS));
        Area<Zone.Forest> forest = new Area<>(set, List.of(), 0);
        assertFalse(Area.hasMenhir(forest));
        set.add(new Zone.Forest(3, Zone.Forest.Kind.WITH_MENHIR));
        forest = new Area<>(set, List.of(), 0);
        assertTrue(Area.hasMenhir(forest));
        forest = new Area<>(Set.of(), List.of(), 0);
        assertFalse(Area.hasMenhir(forest));
    }
    @Test
    void mushroomGroupCountWorks() {
        Set<Zone.Forest> set = new HashSet<>();
        set.add(new Zone.Forest(1, Zone.Forest.Kind.PLAIN));
        set.add(new Zone.Forest(2, Zone.Forest.Kind.WITH_MUSHROOMS));
        Area<Zone.Forest> forest = new Area<>(set, List.of(), 0);
        assertEquals(1, Area.mushroomGroupCount(forest));
        set.add(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS));
        forest = new Area<>(set, List.of(), 0);
        assertEquals(2, Area.mushroomGroupCount(forest));
        forest = new Area<>(Set.of(), List.of(), 0);
        assertEquals(0, Area.mushroomGroupCount(forest));
    }
    @Test
    void animalsWorks() {
        Set<Animal> animals = new HashSet<>();
        animals.add(new Animal(1, Animal.Kind.DEER));
        animals.add(new Animal(2, Animal.Kind.AUROCHS));
        Zone.Meadow zoneMeadow = new Zone.Meadow(1, List.copyOf(animals), null);
        Area<Zone.Meadow> meadow = new Area<>(Set.of(zoneMeadow),List.of(), 0);
        Set<Animal> cancelledAnimals = new HashSet<>();
        Set<Animal> animalsNotCancelled = new HashSet<>(animals);

        cancelledAnimals.add(new Animal(1, Animal.Kind.DEER));
        animalsNotCancelled.remove(new Animal(1, Animal.Kind.DEER));
        assertEquals(animalsNotCancelled, Area.animals(meadow, cancelledAnimals));

        cancelledAnimals.add(new Animal(2, Animal.Kind.AUROCHS));
        animalsNotCancelled.remove(new Animal(2, Animal.Kind.AUROCHS));
        assertEquals(animalsNotCancelled, Area.animals(meadow, cancelledAnimals));

        animals.add(new Animal(3, Animal.Kind.TIGER));
        animals.add(new Animal(4, Animal.Kind.MAMMOTH));
        cancelledAnimals = Set.of(new Animal(2, Animal.Kind.TIGER));
        zoneMeadow = new Zone.Meadow(1, List.copyOf(animals), null);
        meadow = new Area<>(Set.of(zoneMeadow),List.of(), 0);
        assertEquals(animals, Area.animals(meadow, cancelledAnimals));

        Set<Animal> animals2 = new HashSet<>();
        animals2.add(new Animal(1, Animal.Kind.DEER));
        animals2.add(new Animal(2, Animal.Kind.AUROCHS));
        animalsNotCancelled.clear();
        animalsNotCancelled.addAll(animals);
        animalsNotCancelled.addAll(animals2);
        animalsNotCancelled.remove(new Animal(1, Animal.Kind.DEER));
        cancelledAnimals = Set.of(new Animal(1, Animal.Kind.DEER));
        Zone.Meadow zoneMeadow2 = new Zone.Meadow(2, List.copyOf(animals2), null);
        meadow = new Area<>(Set.of(zoneMeadow, zoneMeadow2),List.of(), 0);
        assertEquals(animalsNotCancelled, Area.animals(meadow, cancelledAnimals));

        zoneMeadow2 = new Zone.Meadow(0, List.of(), null);
        cancelledAnimals = Set.of(new Animal(1, Animal.Kind.DEER), new Animal(2, Animal.Kind.AUROCHS));
        meadow = new Area<>(Set.of(zoneMeadow2),List.of(), 0);
        assertEquals(Set.of(), Area.animals(meadow, cancelledAnimals));
    }

    @Test
    void riverFishCountWorks() {
        Set<Zone.River> set = new HashSet<>();
        set.add(new Zone.River(1, 1, null));
        set.add(new Zone.River(2, 2, null));
        Area<Zone.River> river = new Area<>(set, List.of(), 0);
        assertEquals(3, Area.riverFishCount(river));
        set.add(new Zone.River(3, 3, new Zone.Lake(1, 3, null)));
        river = new Area<>(set, List.of(), 0);
        assertEquals(9, Area.riverFishCount(river));
        river = new Area<>(Set.of(), List.of(), 0);
        assertEquals(0, Area.riverFishCount(river));
    }

    @Test
    void riverSystemFishCountWorks() {
        Set<Zone.Water> set = new HashSet<>();
        set.add(new Zone.River(1, 1, null));
        set.add(new Zone.River(2, 2, null));
        set.add(new Zone.Lake(2, 3, null));
        Area<Zone.Water> waterArea = new Area<>(set, List.of(), 0);
        assertEquals(6, Area.riverSystemFishCount(waterArea));

        set.clear();
        waterArea = new Area<>(set, List.of(), 0);
        assertEquals(0, Area.riverSystemFishCount(waterArea));
    }

    @Test
    void lakeCountWorks() {
        Set<Zone.Water> set = new HashSet<>();
        set.add(new Zone.River(1, 1, null));
        set.add(new Zone.River(2, 2, null));
        set.add(new Zone.Lake(2, 3, null));
        Area<Zone.Water> waterArea = new Area<>(set, List.of(), 0);
        assertEquals(1, Area.lakeCount(waterArea));

        set.clear();
        waterArea = new Area<>(set, List.of(), 0);
        assertEquals(0, Area.lakeCount(waterArea));
    }

    @Test
    void isClosedWorks() {
        Set<Zone.Water> set = new HashSet<>();
        set.add(new Zone.River(1, 1, null));
        set.add(new Zone.River(2, 2, null));
        set.add(new Zone.Lake(2, 3, null));
        Area<Zone.Water> waterArea = new Area<>(set, List.of(), 0);
        assertTrue(waterArea.isClosed());

        set.clear();
        set.add(new Zone.River(1, 1, null));
        waterArea = new Area<>(set, List.of(), 1);
        assertFalse(waterArea.isClosed());
    }

    @Test
    void isOccupiedWorks() {
        Area<Zone.Water> waterArea = new Area<>(Set.of(), List.of(), 0);
        assertFalse(waterArea.isOccupied());
        waterArea = new Area<>(Set.of(), List.of(PlayerColor.RED), 0);
        assertTrue(waterArea.isOccupied());
    }

    @Test
    void majorityOccupantsWorks() {
        Area<Zone.Water> waterArea = new Area<>(Set.of(), List.of(), 0);
        assertEquals(Set.of(), waterArea.majorityOccupants());
        waterArea = new Area<>(Set.of(), List.of(PlayerColor.RED), 0);
        assertEquals(Set.of(PlayerColor.RED), waterArea.majorityOccupants());
        waterArea = new Area<>(Set.of(), List.of(PlayerColor.RED, PlayerColor.RED, PlayerColor.BLUE), 0);
        assertEquals(Set.of(PlayerColor.RED), waterArea.majorityOccupants());
        waterArea = new Area<>(Set.of(), List.of(PlayerColor.RED, PlayerColor.RED, PlayerColor.BLUE, PlayerColor.BLUE, PlayerColor.GREEN), 0);
        assertEquals(Set.of(PlayerColor.RED, PlayerColor.BLUE), waterArea.majorityOccupants());
    }

    @Test
    void connectToWorks() {
        Set<Zone.Water> set = new HashSet<>();
        set.add(new Zone.River(1, 1, null));
        set.add(new Zone.River(2, 2, null));
        set.add(new Zone.Lake(2, 3, null));
        Area<Zone.Water> waterArea = new Area<>(set, List.of(PlayerColor.RED, PlayerColor.GREEN), 2);
        Set<Zone.Water> set2 = new HashSet<>();
        set2.add(new Zone.River(3, 1, null));
        set2.add(new Zone.River(4, 2, null));
        set2.add(new Zone.Lake(4, 3, null));
        Area<Zone.Water> waterArea2 = new Area<>(set2, List.of(PlayerColor.RED), 2);
        Set<Zone.Water> set3 = new HashSet<>(set);
        set3.addAll(set2);
        assertEquals(waterArea.connectTo(waterArea2), new Area<>(set3, List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.RED), 2));
        Area<Zone.Water> waterArea3 = waterArea.connectTo(waterArea2);
        assertEquals(waterArea3.connectTo(waterArea3), new Area<>(set3, List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.RED), 0));
    }

    @Test
    void withInitialOccupantWorks() {
        Set<Zone.Water> set = new HashSet<>();
        set.add(new Zone.River(1, 1, null));
        set.add(new Zone.River(2, 2, null));
        set.add(new Zone.Lake(2, 3, null));
        Area<Zone.Water> waterArea = new Area<>(set, List.of(), 2);
        assertEquals(waterArea.withInitialOccupant(PlayerColor.RED), new Area<>(set, List.of(PlayerColor.RED), 2));
    }

    @Test
    void withInitialOccupantThrows() {
        Set<Zone.Water> set = new HashSet<>();
        set.add(new Zone.River(1, 1, null));
        set.add(new Zone.River(2, 2, null));
        set.add(new Zone.Lake(2, 3, null));
        Area<Zone.Water> waterArea = new Area<>(set, List.of(PlayerColor.PURPLE), 0);
        assertThrows(IllegalArgumentException.class, () -> waterArea.withInitialOccupant(PlayerColor.RED));
    }

    @Test
    void withoutOccupantThrows() {
        Set<Zone.Water> set = new HashSet<>();
        set.add(new Zone.River(1, 1, null));
        set.add(new Zone.River(2, 2, null));
        set.add(new Zone.Lake(2, 3, null));
        Area<Zone.Water> waterArea = new Area<>(set, List.of(PlayerColor.PURPLE), 0);
        assertThrows(IllegalArgumentException.class, () -> waterArea.withoutOccupant(PlayerColor.RED));
        Area<Zone.Water> waterArea2 = new Area<>(set, List.of(), 0);
        assertThrows(IllegalArgumentException.class, () -> waterArea2.withoutOccupant(PlayerColor.RED));
    }

    @Test
    void withoutOccupantWorks() {
        Set<Zone.Water> set = new HashSet<>();
        set.add(new Zone.River(1, 1, null));
        set.add(new Zone.River(2, 2, null));
        set.add(new Zone.Lake(2, 3, null));
        Area<Zone.Water> waterArea = new Area<>(set, List.of(PlayerColor.PURPLE), 0);
        assertEquals(waterArea.withoutOccupant(PlayerColor.PURPLE), new Area<>(set, List.of(), 0));
        waterArea = new Area<>(set, List.of(PlayerColor.PURPLE, PlayerColor.RED), 0);
        assertEquals(waterArea.withoutOccupant(PlayerColor.PURPLE), new Area<>(set, List.of(PlayerColor.RED), 0));
        waterArea = new Area<>(set, List.of(PlayerColor.PURPLE, PlayerColor.PURPLE), 2);
        assertEquals(waterArea.withoutOccupant(PlayerColor.PURPLE), new Area<>(set, List.of(PlayerColor.PURPLE), 2));
    }

    @Test
    void withoutOccupantsWorks() {
        Set<Zone.Water> set = new HashSet<>();
        set.add(new Zone.River(1, 1, null));
        set.add(new Zone.River(2, 2, null));
        set.add(new Zone.Lake(2, 3, null));
        Area<Zone.Water> waterArea = new Area<>(set, List.of(PlayerColor.PURPLE), 0);
        assertEquals(waterArea.withoutOccupants(), new Area<>(set, List.of(), 0));
        waterArea = new Area<>(set, List.of(), 0);
        assertEquals(waterArea.withoutOccupants(), new Area<>(set, List.of(), 0));
    }

    @Test
    void tileIdsWorks() {
        Set<Zone.Water> set = new HashSet<>();
        set.add(new Zone.River(561, 1, null));
        set.add(new Zone.River(372, 2, null));
        set.add(new Zone.Lake(202, 3, null));
        Area<Zone.Water> waterArea = new Area<>(set, List.of(), 0);
        assertEquals(Set.of(56, 37, 20), waterArea.tileIds());
        set.add(new Zone.River(371, 1, null));
        waterArea = new Area<>(set, List.of(), 0);
        assertEquals(Set.of(56, 37, 20), waterArea.tileIds());
        assertEquals(Set.of(), new Area<>(Set.of(), List.of(), 0).tileIds());
    }

    @Test
    void zoneWithSpecialPowerWorks() {
        Set<Zone.Water> set = new HashSet<>();
        set.add(new Zone.River(561, 1, null));
        set.add(new Zone.River(372, 2, null));
        set.add(new Zone.Lake(202, 3, null));
        assertNull(new Area<>(Set.of(), List.of(), 0).zoneWithSpecialPower(Zone.SpecialPower.RAFT));
        set.add(new Zone.Lake(35, 5, Zone.SpecialPower.LOGBOAT));
        Area<Zone.Water> waterArea = new Area<>(set, List.of(), 0);;
        assertEquals(new Zone.Lake(35, 5, Zone.SpecialPower.LOGBOAT), waterArea.zoneWithSpecialPower(Zone.SpecialPower.LOGBOAT));
        assertNull(waterArea.zoneWithSpecialPower(Zone.SpecialPower.RAFT));
    }
}

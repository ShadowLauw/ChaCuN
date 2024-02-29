package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LauraTileSideTest {
    @Test
    void zonesWorksForForest() {
        var forest = new TileSide.Forest(new Zone.Forest(0, Zone.Forest.Kind.PLAIN));
        assertEquals(List.of(new Zone.Forest(0, Zone.Forest.Kind.PLAIN)), forest.zones());
    }

    @Test
    void isSameKindAsWorksForForest() {
        var forest = new TileSide.Forest(new Zone.Forest(0, Zone.Forest.Kind.PLAIN));
        var forest2 = new TileSide.Forest(new Zone.Forest(333, Zone.Forest.Kind.WITH_MENHIR));
        assertTrue(forest2.isSameKindAs(forest));
    }

    @Test
    void zoneWorksForMeadow() {
        var meadow = new TileSide.Meadow(new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null));
        assertEquals(List.of(new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null)), meadow.zones());
    }

    @Test
    void isSameKindAsWorksForMeadow() {
        var meadow = new TileSide.Meadow(new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null));
        var meadow2 = new TileSide.Meadow(new Zone.Meadow(333, List.of(new Animal(10, Animal.Kind.AUROCHS)), null));
        assertTrue(meadow2.isSameKindAs(meadow));
    }

    @Test
    void zoneWorksForRiver() {
        var river = new TileSide.River(new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null), new Zone.River(0, 0, null), new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null));
        assertEquals(List.of(new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null), new Zone.River(0, 0, null), new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null)), river.zones());
    }

    @Test
    void isSameKindAsWorksForRiver() {
        var river = new TileSide.River(new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null), new Zone.River(0, 0, null), new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null));
        var river2 = new TileSide.River(new Zone.Meadow(333, List.of(new Animal(10, Animal.Kind.AUROCHS)), null), new Zone.River(333, 0, null), new Zone.Meadow(333, List.of(new Animal(10, Animal.Kind.AUROCHS)), null));
        assertTrue(river2.isSameKindAs(river));
    }
}

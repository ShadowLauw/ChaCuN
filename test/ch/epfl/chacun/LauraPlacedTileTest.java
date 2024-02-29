package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LauraPlacedTileTest {

    @Test
    void placedTileConstructorThrowsIAEOnNullTile() {
        assertThrows(NullPointerException.class, () -> new PlacedTile(null, PlayerColor.RED, Rotation.HALF_TURN, new Pos(0, 0)));
    }

    @Test
    void placedTileConstructorThrowsIAEOnNullRotation() {
        assertThrows(NullPointerException.class, () -> new PlacedTile(new Tile(0, null, null, null, null, null), PlayerColor.RED, null, new Pos(0, 0)));
    }

    @Test
    void placedTileConstructorThrowsIAEOnNullPos() {
        assertThrows(NullPointerException.class, () -> new PlacedTile(new Tile(0, null, null, null, null, null), PlayerColor.RED, Rotation.HALF_TURN, null));
    }

    @Test
    void idWorks() {
        for (int i = 0; i < 95; i++) {
            var tile = new Tile(i, null, null, null, null, null);
            var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.HALF_TURN, new Pos(0, 0));
            assertEquals(placedTile.id(), i);
        }
    }

    @Test
    void tileKindWorks() {
        var tile = new Tile(0, Tile.Kind.START, null, null, null, null);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.HALF_TURN, new Pos(0, 0));
        assertEquals(placedTile.kind(), Tile.Kind.START);
        var tile2 = new Tile(0, Tile.Kind.NORMAL, null, null, null, null);
        var placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.HALF_TURN, new Pos(0, 0));
        assertEquals(placedTile2.kind(), Tile.Kind.NORMAL);
        var tile3 = new Tile(0, Tile.Kind.MENHIR, null, null, null, null);
        var placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.HALF_TURN, new Pos(0, 0));
        assertEquals(placedTile3.kind(), Tile.Kind.MENHIR);
    }

    @Test
    void sideWorksForMix() {
        var zoneMeadow1 = new Zone.Meadow(3, List.of(new Animal(1, Animal.Kind.DEER)), null);
        var zoneMeadow2 = new Zone.Meadow(5, List.of(new Animal(1, Animal.Kind.MAMMOTH)), null);
        var meadow1 = new TileSide.Meadow(zoneMeadow1);
        var meadow2 = new TileSide.Meadow(zoneMeadow2);
        var river = new TileSide.River(zoneMeadow1, new Zone.River(35, 3, null), zoneMeadow2);
        var forest = new TileSide.Forest(new Zone.Forest(27, Zone.Forest.Kind.PLAIN));

        var tile = getTile();
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(meadow1, placedTile.side(Direction.N));
        assertEquals(river, placedTile.side(Direction.E));
        assertEquals(meadow2, placedTile.side(Direction.S));
        assertEquals(forest, placedTile.side(Direction.W));
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.LEFT, new Pos(0, 0));
        assertEquals(forest, placedTile.side(Direction.N));
        assertEquals(meadow1, placedTile.side(Direction.E));
        assertEquals(river, placedTile.side(Direction.S));
        assertEquals(meadow2, placedTile.side(Direction.W));
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.RIGHT, new Pos(0, 0));
        assertEquals(river, placedTile.side(Direction.N));
        assertEquals(meadow2, placedTile.side(Direction.E));
        assertEquals(forest, placedTile.side(Direction.S));
        assertEquals(meadow1, placedTile.side(Direction.W));
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.HALF_TURN, new Pos(0, 0));
        assertEquals(meadow2, placedTile.side(Direction.N));
        assertEquals(forest, placedTile.side(Direction.E));
        assertEquals(meadow1, placedTile.side(Direction.S));
        assertEquals(river, placedTile.side(Direction.W));
    }

    @Test
    void zoneWithIdThrowsIAEOnInexistantId() {
        var tile = getTile();

        assertThrows(IllegalArgumentException.class, () -> new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0)).zoneWithId(0));
        assertThrows(IllegalArgumentException.class, () -> new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0)).zoneWithId(1));
        assertThrows(IllegalArgumentException.class, () -> new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0)).zoneWithId(2));
        assertThrows(IllegalArgumentException.class, () -> new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0)).zoneWithId(746));
        assertThrows(IllegalArgumentException.class, () -> new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0)).zoneWithId(37));
    }

    @Test
    void zoneWithIdWorks() {
        var zoneMeadow1 = new Zone.Meadow(3, List.of(new Animal(1, Animal.Kind.DEER)), null);
        var zoneMeadow2 = new Zone.Meadow(5, List.of(new Animal(1, Animal.Kind.MAMMOTH)), null);
        var zoneLake = new Zone.Lake(7, 2, null);
        var zoneRiver = new Zone.River(35, 3, zoneLake);
        var zoneForest = new Zone.Forest(27, Zone.Forest.Kind.PLAIN);

        var tile = getTile();
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(zoneMeadow1, placedTile.zoneWithId(3));
        assertEquals(zoneMeadow2, placedTile.zoneWithId(5));
        assertEquals(zoneRiver, placedTile.zoneWithId(35));
        assertEquals(zoneForest, placedTile.zoneWithId(27));
        assertEquals(zoneLake, placedTile.zoneWithId(7));
    }

    @Test
    void specialPowerZoneWorks() {
        var zoneMeadow1 = new Zone.Meadow(3, List.of(new Animal(1, Animal.Kind.DEER)), Zone.SpecialPower.RAFT);
        var zoneMeadow2 = new Zone.Meadow(5, List.of(new Animal(1, Animal.Kind.MAMMOTH)), null);
        var zoneMeadow3 = new Zone.Meadow(8, List.of(new Animal(1, Animal.Kind.MAMMOTH)), null);
        var zoneRiver = new Zone.River(35, 3, null);
        var zoneForest = new Zone.Forest(27, Zone.Forest.Kind.PLAIN);
        var meadow1 = new TileSide.Meadow(zoneMeadow1);
        var meadow2 = new TileSide.Meadow(zoneMeadow2);
        var meadow3 = new TileSide.Meadow(zoneMeadow3);
        var river = new TileSide.River(zoneMeadow3, zoneRiver, zoneMeadow2);
        var forest = new TileSide.Forest(zoneForest);
        var tile = new Tile(33, Tile.Kind.START, meadow1, river, meadow2, forest);

        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(zoneMeadow1, placedTile.specialPowerZone());

        tile = new Tile(33, Tile.Kind.START, meadow2, river, meadow1, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(zoneMeadow1, placedTile.specialPowerZone());

        tile = new Tile(33, Tile.Kind.START, meadow3, river, meadow2, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertNull(placedTile.specialPowerZone());

        var zoneLake = new Zone.Lake(7, 2, Zone.SpecialPower.LOGBOAT);
        zoneRiver = new Zone.River(35, 3, zoneLake);
        river = new TileSide.River(zoneMeadow3, zoneRiver, zoneMeadow2);
        tile = new Tile(33, Tile.Kind.START, meadow3, river, meadow2, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(zoneLake, placedTile.specialPowerZone());

        river = new TileSide.River(zoneMeadow1, zoneRiver, zoneMeadow2);
        tile = new Tile(33, Tile.Kind.START, meadow3, river, meadow2, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(zoneMeadow1, placedTile.specialPowerZone());

        river = new TileSide.River(zoneMeadow3, zoneRiver, zoneMeadow1);
        tile = new Tile(33, Tile.Kind.START, meadow3, river, meadow2, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(zoneMeadow1, placedTile.specialPowerZone());
    }

    @Test
    void forestZonesWorks() {
        var zoneMeadow1 = new Zone.Meadow(3, List.of(new Animal(1, Animal.Kind.DEER)), null);
        var zoneMeadow2 = new Zone.Meadow(5, List.of(new Animal(1, Animal.Kind.MAMMOTH)), null);
        var zoneLake = new Zone.Lake(7, 2, null);
        var zoneRiver = new Zone.River(35, 3, zoneLake);
        var zoneForest = new Zone.Forest(27, Zone.Forest.Kind.PLAIN);
        var meadow1 = new TileSide.Meadow(zoneMeadow1);
        var meadow2 = new TileSide.Meadow(zoneMeadow2);
        var river = new TileSide.River(zoneMeadow1, zoneRiver, zoneMeadow2);
        var forest = new TileSide.Forest(zoneForest);
        var tile = new Tile(33, Tile.Kind.START, meadow1, river, meadow2, forest);

        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(zoneForest), placedTile.forestZones());

        var zoneForest2 = new Zone.Forest(27, Zone.Forest.Kind.WITH_MENHIR);
        var forest2 = new TileSide.Forest(zoneForest2);
        tile = new Tile(33, Tile.Kind.START, meadow1, forest2, meadow2, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(zoneForest, zoneForest2), placedTile.forestZones());

        var zoneForest3 = new Zone.Forest(27, Zone.Forest.Kind.WITH_MUSHROOMS);
        var forest3 = new TileSide.Forest(zoneForest3);
        tile = new Tile(33, Tile.Kind.START, forest3, forest2, meadow2, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(zoneForest, zoneForest3, zoneForest2), placedTile.forestZones());

        tile = new Tile(33, Tile.Kind.START, meadow1, river, meadow2, meadow1);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(new HashSet<>(), placedTile.forestZones());
    }

    @Test
    void meadowZonesWorks() {
        var zoneMeadow1 = new Zone.Meadow(3, List.of(new Animal(1, Animal.Kind.DEER)), null);
        var zoneMeadow2 = new Zone.Meadow(5, List.of(new Animal(1, Animal.Kind.MAMMOTH)), null);
        var zoneLake = new Zone.Lake(7, 2, null);
        var zoneRiver = new Zone.River(35, 3, zoneLake);
        var zoneForest = new Zone.Forest(27, Zone.Forest.Kind.PLAIN);
        var meadow1 = new TileSide.Meadow(zoneMeadow1);
        var meadow2 = new TileSide.Meadow(zoneMeadow2);
        var forest = new TileSide.Forest(zoneForest);
        var tile = new Tile(33, Tile.Kind.START, meadow1, forest, meadow2, forest);

        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(zoneMeadow1, zoneMeadow2), placedTile.meadowZones());

        var zoneMeadow3 = new Zone.Meadow(8, List.of(new Animal(1, Animal.Kind.MAMMOTH)), null);
        var meadow3 = new TileSide.Meadow(zoneMeadow3);
        var river = new TileSide.River(zoneMeadow3, zoneRiver, zoneMeadow2);
        tile = new Tile(33, Tile.Kind.START, meadow1, river, meadow2, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(zoneMeadow1, zoneMeadow3, zoneMeadow2), placedTile.meadowZones());

        river = new TileSide.River(zoneMeadow1, zoneRiver, zoneMeadow1);
        tile = new Tile(33, Tile.Kind.START, forest, river, forest, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(zoneMeadow1), placedTile.meadowZones());

        tile = new Tile(33, Tile.Kind.START, forest, forest, forest, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(new HashSet<>(), placedTile.meadowZones());
    }

    @Test
    void riverZonesWorks() {
        var zoneMeadow1 = new Zone.Meadow(3, List.of(new Animal(1, Animal.Kind.DEER)), null);
        var zoneMeadow2 = new Zone.Meadow(5, List.of(new Animal(1, Animal.Kind.MAMMOTH)), null);
        var zoneLake = new Zone.Lake(7, 2, null);
        var zoneRiver = new Zone.River(35, 3, zoneLake);
        var zoneForest = new Zone.Forest(27, Zone.Forest.Kind.PLAIN);
        var meadow1 = new TileSide.Meadow(zoneMeadow1);
        var meadow2 = new TileSide.Meadow(zoneMeadow2);
        var forest = new TileSide.Forest(zoneForest);
        var river = new TileSide.River(zoneMeadow1, zoneRiver, zoneMeadow2);

        var tile = new Tile(33, Tile.Kind.START, meadow1, forest, meadow2, forest);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(new HashSet<>(), placedTile.riverZones());

        var zoneRiver2 = new Zone.River(25, 3, null);
        var river2 = new TileSide.River(zoneMeadow1, zoneRiver2, zoneMeadow2);
        tile = new Tile(33, Tile.Kind.START, river, river2, meadow2, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(zoneRiver, zoneRiver2), placedTile.riverZones());

        tile = new Tile(33, Tile.Kind.START, forest, river, forest, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(zoneRiver), placedTile.riverZones());
    }

    @Test
    void potentialOccupantsWorks() {
        var zoneMeadow1 = new Zone.Meadow(3, List.of(new Animal(1, Animal.Kind.DEER)), null);
        var zoneMeadow2 = new Zone.Meadow(5, List.of(new Animal(1, Animal.Kind.MAMMOTH)), null);
        var zoneLake = new Zone.Lake(7, 2, null);
        var zoneRiverWithLake = new Zone.River(35, 3, zoneLake);
        var zoneRiverWithoutLake = new Zone.River(33, 3, null);
        var zoneForest = new Zone.Forest(27, Zone.Forest.Kind.PLAIN);
        var meadow1 = new TileSide.Meadow(zoneMeadow1);
        var occupantMeadow1 = new Occupant(Occupant.Kind.PAWN, 3);
        var meadow2 = new TileSide.Meadow(zoneMeadow2);
        var occupantMeadow2 = new Occupant(Occupant.Kind.PAWN, 5);
        var forest = new TileSide.Forest(zoneForest);
        var occupantForest = new Occupant(Occupant.Kind.PAWN, 27);
        var riverWithLake = new TileSide.River(zoneMeadow1, zoneRiverWithLake, zoneMeadow2);
        var occupantRiverWithLake = new Occupant(Occupant.Kind.PAWN, 35);
        var occupantLake = new Occupant(Occupant.Kind.HUT, 7);
        var riverWithoutLake = new TileSide.River(zoneMeadow1, zoneRiverWithoutLake, zoneMeadow2);
        var occupantPawnRiverWithoutLake = new Occupant(Occupant.Kind.PAWN, 33);
        var occupantHutRiverWithoutLake = new Occupant(Occupant.Kind.HUT, 33);

        var tile = new Tile(33, Tile.Kind.START, meadow1, riverWithoutLake, meadow2, forest);
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(occupantMeadow1, occupantMeadow2, occupantForest, occupantHutRiverWithoutLake, occupantPawnRiverWithoutLake), placedTile.potentialOccupants());

        tile = new Tile(33, Tile.Kind.START, meadow1, riverWithLake, meadow2, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(occupantMeadow1, occupantMeadow2, occupantForest, occupantLake, occupantRiverWithLake), placedTile.potentialOccupants());

        tile = new Tile(33, Tile.Kind.START, meadow1, meadow1, meadow1, forest);
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(Set.of(occupantMeadow1, occupantForest), placedTile.potentialOccupants());
    }

    @Test
    void withOccupantWorks() {
        var tile = getTile();
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        var occupant = new Occupant(Occupant.Kind.PAWN, 3);
        var placedTileWithOccupant = placedTile.withOccupant(occupant);
        assertEquals(occupant, placedTileWithOccupant.occupant());
    }

    @Test
    void withOccupantThrowsIAEOnNonNullOccupant() {
        var tile = getTile();
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.PAWN, 3));
        assertThrows(IllegalArgumentException.class, () -> placedTile.withOccupant(null));
    }

    @Test
    void withNoOccupantWorks() {
        var tile = getTile();
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.PAWN, 3));
        var placedTileWithNoOccupant = placedTile.withNoOccupant();
        assertNull(placedTileWithNoOccupant.occupant());
    }

    @Test
    void idOfZoneOccupiedByWorks() {
        var tile = getTile();
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.PAWN, 3));
        assertEquals(3, placedTile.idOfZoneOccupiedBy(Occupant.Kind.PAWN));
        placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.HUT, 35));
        assertEquals(35, placedTile.idOfZoneOccupiedBy(Occupant.Kind.HUT));
    }

    @Test
    void idOfZoneOccupiedByReturnMinusOne() {
        var tile = getTile();
        var placedTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        assertEquals(-1, placedTile.idOfZoneOccupiedBy(Occupant.Kind.PAWN));
        var placedTile2 = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.HUT, 35));
        assertEquals(-1, placedTile2.idOfZoneOccupiedBy(Occupant.Kind.PAWN));
    }

    private static Tile getTile() {
        var zoneMeadow1 = new Zone.Meadow(3, List.of(new Animal(1, Animal.Kind.DEER)), null);
        var zoneMeadow2 = new Zone.Meadow(5, List.of(new Animal(1, Animal.Kind.MAMMOTH)), null);
        var zoneLake = new Zone.Lake(7, 2, null);
        var zoneRiver = new Zone.River(35, 3, zoneLake);
        var zoneForest = new Zone.Forest(27, Zone.Forest.Kind.PLAIN);
        var meadow1 = new TileSide.Meadow(zoneMeadow1);
        var meadow2 = new TileSide.Meadow(zoneMeadow2);
        var river = new TileSide.River(zoneMeadow1, zoneRiver, zoneMeadow2);
        var forest = new TileSide.Forest(zoneForest);

        return new Tile(33, Tile.Kind.START, meadow1, river, meadow2, forest);
    }
}

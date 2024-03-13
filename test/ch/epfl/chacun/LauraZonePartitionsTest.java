package ch.epfl.chacun;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ch.epfl.chacun.TileReader;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LauraZonePartitionsTest {
    @Test
    void emptyZonePartition() {
        ZonePartition<Zone.Forest> zonePartitionForest = new ZonePartition<>();
        ZonePartition<Zone.Meadow> zonePartitionMeadow = new ZonePartition<>();
        ZonePartition<Zone.River> zonePartitionRiver = new ZonePartition<>();
        ZonePartition<Zone.Water> zonePartitionWater = new ZonePartition<>();
        assertEquals(new ZonePartitions(zonePartitionForest, zonePartitionMeadow, zonePartitionRiver, zonePartitionWater), ZonePartitions.EMPTY);
    }

    @Test
    void addTileWorks56() throws IOException {
        ZonePartitions.Builder builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        Tile tile56 = TileReader.readTileFromCSV(56);
        builder.addTile(tile56);

        Zone.Forest zoneForest = new Zone.Forest(561, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Meadow zoneMeadow1 = new Zone.Meadow(560, List.of(new Animal(5600, Animal.Kind.AUROCHS)), null);
        Zone.Meadow zoneMeadow2 = new Zone.Meadow(562, List.of(), null);
        Zone.River zoneRiver = new Zone.River(563, 0, new Zone.Lake(568, 1, null));


        ZonePartition.Builder<Zone.Forest> zonePartitionForest = new ZonePartition.Builder<>(new ZonePartition<>());
        zonePartitionForest.addSingleton(zoneForest, 2);
        ZonePartition.Builder<Zone.Meadow> zonePartitionMeadow = new ZonePartition.Builder<>(new ZonePartition<>());
        zonePartitionMeadow.addSingleton(zoneMeadow1, 2);
        zonePartitionMeadow.addSingleton(zoneMeadow2, 1);
        ZonePartition.Builder<Zone.River> zonePartitionRiver = new ZonePartition.Builder<>(new ZonePartition<>());
        zonePartitionRiver.addSingleton(zoneRiver, 1);
        ZonePartition.Builder<Zone.Water> zonePartitionWater = new ZonePartition.Builder<>(new ZonePartition<>());
        zonePartitionWater.addSingleton(zoneRiver.lake(), 1);
        zonePartitionWater.addSingleton(zoneRiver, 2);
        zonePartitionWater.union(zoneRiver.lake(), zoneRiver);

        assertEquals(builder.build().forests(), zonePartitionForest.build());
        assertEquals(builder.build().meadows(), zonePartitionMeadow.build());
        assertEquals(builder.build().rivers(), zonePartitionRiver.build());
        assertEquals(builder.build().riverSystems(), zonePartitionWater.build());
    }

    @Test
    void addTileWork20() throws IOException {
        ZonePartitions.Builder builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        Tile tile20 = TileReader.readTileFromCSV(20);
        builder.addTile(tile20);

        Zone.Meadow zoneMeadow0 = (Zone.Meadow)(tile20.n().zones().getFirst());
        Zone.Meadow zoneMeadow1 = (Zone.Meadow)(tile20.e().zones().getFirst());
        Zone.Meadow zoneMeadow2 = (Zone.Meadow)(tile20.e().zones().getLast());
        Zone.River zoneRiver = (Zone.River)(tile20.n().zones().get(1));
        Zone.Forest zoneForest = (Zone.Forest)(tile20.s().zones().getFirst());

        ZonePartition.Builder<Zone.Forest> zonePartitionForest = new ZonePartition.Builder<>(new ZonePartition<>());
        zonePartitionForest.addSingleton(zoneForest, 1);
        ZonePartition.Builder<Zone.Meadow> zonePartitionMeadow = new ZonePartition.Builder<>(new ZonePartition<>());
        zonePartitionMeadow.addSingleton(zoneMeadow0, 2);
        zonePartitionMeadow.addSingleton(zoneMeadow1, 2);
        zonePartitionMeadow.addSingleton(zoneMeadow2, 1);
        ZonePartition.Builder<Zone.River> zonePartitionRiver = new ZonePartition.Builder<>(new ZonePartition<>());
        zonePartitionRiver.addSingleton(zoneRiver, 2);
        ZonePartition.Builder<Zone.Water> zonePartitionWater = new ZonePartition.Builder<>(new ZonePartition<>());
        zonePartitionWater.addSingleton(zoneRiver, 2);

        assertEquals(builder.build().forests(), zonePartitionForest.build());
        assertEquals(builder.build().meadows(), zonePartitionMeadow.build());
        assertEquals(builder.build().rivers(), zonePartitionRiver.build());
        assertEquals(builder.build().riverSystems(), zonePartitionWater.build());
    }

    @Test
    void connectSidesThrows() throws IOException {
        ZonePartitions.Builder builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        Tile tile56 = TileReader.readTileFromCSV(56);
        Tile tile20 = TileReader.readTileFromCSV(20);
        builder.addTile(tile20);
        builder.addTile(tile56);
        assertThrows(IllegalArgumentException.class, () -> builder.connectSides(tile20.e(), tile56.e()));
        assertThrows(IllegalArgumentException.class, () -> builder.connectSides(tile20.n(), tile56.e()));
        assertThrows(IllegalArgumentException.class, () -> builder.connectSides(tile20.e(), tile56.n()));
    }

    @Test
    void connectSidesWorks() throws IOException {
        ZonePartitions.Builder builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        Tile tile56 = TileReader.readTileFromCSV(56);
        Tile tile20 = TileReader.readTileFromCSV(20);
        builder.addTile(tile20);
        builder.addTile(tile56);
        builder.connectSides(tile20.s(), tile56.e());
        Area<Zone.Forest> forestArea = new Area<>(Set.of((Zone.Forest)tile20.s().zones().getFirst(), (Zone.Forest)tile56.e().zones().getFirst()), List.of(), 1);
        assertEquals(builder.build().forests(), new ZonePartition<>(Set.of(forestArea)));
    }

    @Test
    void connectSidesWorksOnRiver() throws IOException {
        ZonePartitions.Builder builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        Tile tile56 = TileReader.readTileFromCSV(56);
        Tile tile20 = TileReader.readTileFromCSV(20);
        builder.addTile(tile20);
        builder.addTile(tile56);
        builder.connectSides(tile20.n(), tile56.w());
        Area<Zone.River> riverArea = new Area<>(Set.of((Zone.River)tile20.n().zones().get(1), (Zone.River)tile56.w().zones().get(1)), List.of(), 1);
        Area<Zone.Water> waterArea = new Area<>(Set.of((Zone.Water)tile20.n().zones().get(1), (Zone.Water)tile56.w().zones().get(1), ((Zone.River) tile56.w().zones().get(1)).lake()), List.of(), 1);
        assertEquals(builder.build().rivers(), new ZonePartition<>(Set.of(riverArea)));
        assertEquals(builder.build().riverSystems(), new ZonePartition<>(Set.of(waterArea)));
    }

    @Test
    void addInitialOccupantWorksOnPawn() throws IOException {
        ZonePartitions.Builder builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        Tile tile56 = TileReader.readTileFromCSV(56);
        builder.addTile(tile56);
        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.PAWN, tile56.s().zones().getFirst());
        Area<Zone.Forest> forestArea = new Area<>(Set.of((Zone.Forest)tile56.s().zones().getFirst()), List.of(PlayerColor.RED), 2);
        assertEquals(builder.build().forests(), new ZonePartition<>(Set.of(forestArea)));
    }
    @Test
    void addInitialOccupantWorksOnHut() throws IOException {
        ZonePartitions.Builder builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        Tile tile56 = TileReader.readTileFromCSV(56);
        builder.addTile(tile56);
//        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.TOTORO, tile56.s().zones().getFirst());
        Area<Zone.Forest> forestArea = new Area<>(Set.of((Zone.Forest)tile56.s().zones().getFirst()), List.of(PlayerColor.RED), 2);
        assertEquals(builder.build().forests(), new ZonePartition<>(Set.of(forestArea)));
    }
}


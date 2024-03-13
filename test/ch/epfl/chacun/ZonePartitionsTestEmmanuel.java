package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ZonePartitionsTestEmmanuel {
    @Test
    void ZonePartitionConstructorWorks() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Area<Zone.Forest> area = new Area<>(Set.of(forest1), List.of(PlayerColor.BLUE), 2);
        ZonePartition<Zone.Forest> forest = new ZonePartition<>(Set.of(area));
        Zone.River river1 = new Zone.River(0, 0, null);
        Area<Zone.River> area1 = new Area<>(Set.of(river1), List.of(PlayerColor.RED), 3);
        ZonePartition<Zone.River> river = new ZonePartition<>(Set.of(area1));
        Zone.Lake lake = new Zone.Lake(0, 0, null);
        Zone.Water water1 = new Zone.River(0, 0, lake);
        Area<Zone.Water> area2 = new Area<>(Set.of(water1, lake), List.of(PlayerColor.GREEN, PlayerColor.BLUE), 4);
        ZonePartition<Zone.Water> water = new ZonePartition<>(Set.of(area2));
        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null);
        Area<Zone.Meadow> area3 = new Area<>(Set.of(meadow1), List.of(PlayerColor.YELLOW), 5);
        ZonePartition<Zone.Meadow> meadow = new ZonePartition<>(Set.of(area3));

        ZonePartitions zonePartitions = new ZonePartitions(forest, meadow, river, water);

        assertEquals(zonePartitions.forests(), forest);
        assertEquals(zonePartitions.meadows(), meadow);
        assertEquals(zonePartitions.rivers(), river);
        assertEquals(zonePartitions.riverSystems(), water);
    }

    @Test
    void ZonePartitionConstructorWorksEmpty() {
        ZonePartitions zonePartitions = ZonePartitions.EMPTY;
        assertEquals(zonePartitions.forests(), new ZonePartition<>(Set.of()));
        assertEquals(zonePartitions.meadows(), new ZonePartition<>(Set.of()));
        assertEquals(zonePartitions.rivers(), new ZonePartition<>(Set.of()));
        assertEquals(zonePartitions.riverSystems(), new ZonePartition<>(Set.of()));
    }

    @Test
    void BuilderBuildWorks() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Area<Zone.Forest> area = new Area<>(Set.of(forest1), List.of(PlayerColor.BLUE), 2);
        ZonePartition<Zone.Forest> forest = new ZonePartition<>(Set.of(area));
        Zone.River river1 = new Zone.River(0, 0, null);
        Area<Zone.River> area1 = new Area<>(Set.of(river1), List.of(PlayerColor.RED), 3);
        ZonePartition<Zone.River> river = new ZonePartition<>(Set.of(area1));
        Zone.Lake lake = new Zone.Lake(0, 0, null);
        Zone.Water water1 = new Zone.River(0, 0, lake);
        Area<Zone.Water> area2 = new Area<>(Set.of(water1, lake), List.of(PlayerColor.GREEN, PlayerColor.BLUE), 4);
        ZonePartition<Zone.Water> water = new ZonePartition<>(Set.of(area2));
        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null);
        Area<Zone.Meadow> area3 = new Area<>(Set.of(meadow1), List.of(PlayerColor.YELLOW), 5);
        ZonePartition<Zone.Meadow> meadow = new ZonePartition<>(Set.of(area3));

        ZonePartitions zonePartitions = new ZonePartitions(forest, meadow, river, water);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);
        assertEquals(builder.build(), zonePartitions);
    }

    @Test
    void BuilderAddTileWorks() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Area<Zone.Forest> areaForest = new Area<>(Set.of(forest1), List.of(PlayerColor.BLUE), 2);
        ZonePartition<Zone.Forest> forest = new ZonePartition<>(Set.of(areaForest));

        Zone.River river1 = new Zone.River(0, 0, null);
        Area<Zone.River> areaRiver = new Area<>(Set.of(river1), List.of(PlayerColor.RED), 3);
        ZonePartition<Zone.River> river = new ZonePartition<>(Set.of(areaRiver));

        Zone.Lake lake = new Zone.Lake(0, 0, null);
        Zone.Water river1bis = new Zone.River(0, 0, lake);
        Area<Zone.Water> areaWater = new Area<>(Set.of(river1bis, river1, lake), List.of(PlayerColor.GREEN, PlayerColor.BLUE), 4);
        ZonePartition<Zone.Water> water = new ZonePartition<>(Set.of(areaWater));

        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null);
        Area<Zone.Meadow> areaMeadow = new Area<>(Set.of(meadow1), List.of(PlayerColor.YELLOW), 5);
        ZonePartition<Zone.Meadow> meadow = new ZonePartition<>(Set.of(areaMeadow));

        ZonePartitions zonePartitions = new ZonePartitions(forest, meadow, river, water);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);

        Zone.Forest forest2 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Lake lake2 = new Zone.Lake(100, 0, null);
        Zone.River river2 = new Zone.River(101, 0, lake2);
        Zone.Meadow meadow2 = new Zone.Meadow(102, List.of(new Animal(1001, Animal.Kind.DEER)), null);
        Zone.Meadow meadow3 = new Zone.Meadow(103, List.of(new Animal(1002, Animal.Kind.DEER)), null);
        builder.addTile(new Tile(0, Tile.Kind.NORMAL, new TileSide.Forest(forest2), new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow3), new TileSide.River(meadow1, river2, meadow2)));

        //alors je suis pas sûr de mon coup ici, je sais pas trop si les zones sont enregistrées dans une nouvelle aire à part pour chacune mais on va dire que non
        Area<Zone.Forest> areaForest2 = new Area<>(Set.of(forest2), List.of(), 0);
        Area<Zone.Meadow> areaMeadow2 = new Area<>(Set.of(meadow3), List.of(), 0);
        Area<Zone.River> areaRiver2 = new Area<>(Set.of(river2), List.of(), 0);
        Area<Zone.Water> areaWater2 = new Area<>(Set.of(river2, lake2), List.of(), 0);
        ZonePartitions zonePartitions2 = new ZonePartitions(new ZonePartition<>(Set.of(areaForest, areaForest2)), new ZonePartition<>(Set.of(areaMeadow, areaMeadow2)), new ZonePartition<>(Set.of(areaRiver, areaRiver2)), new ZonePartition<>(Set.of(areaWater, areaWater2)));
        assertEquals(builder.build(), zonePartitions2);
    }

    @Test
    void builderAddTileWorksTest() {

        Tile startTile = TileReader.readTileFromCSV(56);
        Tile tile17 = TileReader.readTileFromCSV(17);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);
        builder.addTile(startTile);
        Set<Area<Zone.Forest>> forestsArea = Set.of(new Area<>(Set.of((Zone.Forest)startTile.e().zones().getFirst()), List.of(), 2));
        Set<Area<Zone.Meadow>> meadowsArea = Set.of(new Area<>(Set.of((Zone.Meadow)startTile.w().zones().getFirst()), List.of(), 1), new Area<>(Set.of((Zone.Meadow)startTile.w().zones().getLast()), List.of(), 2));
        Set<Area<Zone.River>> riversArea = Set.of(new Area<>(Set.of((Zone.River)startTile.w().zones().get(1)), List.of(), 1));
        Set<Area<Zone.Water>> watersArea = Set.of(new Area<>(startTile.zones().stream().filter(zone -> zone instanceof Zone.Water).map(zone -> (Zone.Water) zone).collect(Collectors.toSet()), List.of(), 1));
        ZonePartitions zonePartitionsValid = new ZonePartitions(
                new ZonePartition<>(forestsArea),
                new ZonePartition<>(meadowsArea),
                new ZonePartition<>(riversArea),
                new ZonePartition<>(watersArea)
        );
        assertEquals(zonePartitionsValid, builder.build());

        builder.addTile(tile17);
        Set<Area<Zone.Meadow>> meadowsArea2 = Set.of(
                new Area<>(Set.of((Zone.Meadow)tile17.e().zones().getFirst()), List.of(), 1),
                new Area<>(Set.of((Zone.Meadow)tile17.e().zones().getLast()), List.of(), 2),
                new Area<>(Set.of((Zone.Meadow)tile17.w().zones().getFirst()), List.of(), 3),
                new Area<>(Set.of((Zone.Meadow)tile17.w().zones().getLast()), List.of(), 4)
        );
        Set<Area<Zone.River>> riversArea2 = Set.of(new Area<>(Set.of((Zone.River)tile17.w().zones().get(1)), List.of(), 0));
        Set<Area<Zone.Water>> watersArea2 = Set.of(new Area<>(tile17.zones().stream().filter(zone -> zone instanceof Zone.Water).map(zone -> (Zone.Water) zone).collect(Collectors.toSet()), List.of(), 0));
        riversArea2.add(new Area<>(Set.of((Zone.River)tile17.w().zones().get(1)), List.of(), 0));
        watersArea2.add(new Area<>(tile17.zones().stream().filter(zone -> zone instanceof Zone.Water).map(zone -> (Zone.Water) zone).collect(Collectors.toSet()), List.of(), 0));


    }

    @Test
    void BuilderConnectSideWorks() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Area<Zone.Forest> areaForest = new Area<>(Set.of(forest1), List.of(PlayerColor.BLUE), 2);
        ZonePartition<Zone.Forest> forest = new ZonePartition<>(Set.of(areaForest));

        Zone.River river1 = new Zone.River(0, 0, null);
        Area<Zone.River> areaRiver = new Area<>(Set.of(river1), List.of(PlayerColor.RED), 3);
        ZonePartition<Zone.River> river = new ZonePartition<>(Set.of(areaRiver));

        Zone.Lake lake = new Zone.Lake(0, 0, null);
        Zone.Water river1bis = new Zone.River(0, 0, lake);
        Area<Zone.Water> areaWater = new Area<>(Set.of(river1bis, river1, lake), List.of(PlayerColor.GREEN, PlayerColor.BLUE), 4);
        ZonePartition<Zone.Water> water = new ZonePartition<>(Set.of(areaWater));

        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null);
        Area<Zone.Meadow> areaMeadow = new Area<>(Set.of(meadow1), List.of(PlayerColor.YELLOW), 5);
        ZonePartition<Zone.Meadow> meadow = new ZonePartition<>(Set.of(areaMeadow));

        ZonePartitions zonePartitions = new ZonePartitions(forest, meadow, river, water);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);

        Zone.Forest forest2 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Lake lake2 = new Zone.Lake(100, 0, null);
        Zone.River river2 = new Zone.River(101, 0, lake2);
        Zone.Meadow meadow2 = new Zone.Meadow(102, List.of(new Animal(1001, Animal.Kind.DEER)), null);
        Zone.Meadow meadow3 = new Zone.Meadow(103, List.of(new Animal(1002, Animal.Kind.DEER)), null);
        builder.addTile(new Tile(0, Tile.Kind.NORMAL, new TileSide.Forest(forest2), new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow3), new TileSide.River(meadow1, river2, meadow2)));
        builder.connectSides(new TileSide.Forest(forest1), new TileSide.Forest(forest2));

        //alors je suis pas sûr de mon coup ici, je sais pas trop si les zones sont enregistrées dans une nouvelle aire à part pour chacune mais on va dire que non
        Area<Zone.Forest> areaForest2 = new Area<>(Set.of(forest1, forest2), List.of(), 2); //peut être 0, dépend si on sconsidère qu'on enlève 1...
        Area<Zone.Meadow> areaMeadow2 = new Area<>(Set.of(meadow3), List.of(), 3);
        Area<Zone.River> areaRiver2 = new Area<>(Set.of(river2), List.of(), 4);
        Area<Zone.Water> areaWater2 = new Area<>(Set.of(river2, lake2), List.of(), 5);
        ZonePartitions zonePartitions2 = new ZonePartitions(new ZonePartition<>(Set.of(areaForest2)), new ZonePartition<>(Set.of(areaMeadow, areaMeadow2)), new ZonePartition<>(Set.of(areaRiver, areaRiver2)), new ZonePartition<>(Set.of(areaWater, areaWater2)));
        assertEquals(builder.build(), zonePartitions2);

        assertThrows(IllegalArgumentException.class, () -> builder.connectSides(new TileSide.Forest(forest1), new TileSide.Meadow(meadow2)));
    }

    @Test
    void BuilderAddInitialOccupantWorks () {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Area<Zone.Forest> areaForest = new Area<>(Set.of(forest1), List.of(), 2);
        ZonePartition<Zone.Forest> forest = new ZonePartition<>(Set.of(areaForest));

        Zone.River river1 = new Zone.River(0, 0, null);
        Area<Zone.River> areaRiver = new Area<>(Set.of(river1), List.of(PlayerColor.RED), 3);
        ZonePartition<Zone.River> river = new ZonePartition<>(Set.of(areaRiver));

        Zone.Lake lake = new Zone.Lake(0, 0, null);
        Zone.Water river1bis = new Zone.River(0, 0, lake);
        Area<Zone.Water> areaWater = new Area<>(Set.of(river1bis, river1, lake), List.of(PlayerColor.GREEN, PlayerColor.BLUE), 4);
        ZonePartition<Zone.Water> water = new ZonePartition<>(Set.of(areaWater));

        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null);
        Area<Zone.Meadow> areaMeadow = new Area<>(Set.of(meadow1), List.of(PlayerColor.YELLOW), 5);
        ZonePartition<Zone.Meadow> meadow = new ZonePartition<>(Set.of(areaMeadow));

        ZonePartitions zonePartitions = new ZonePartitions(forest, meadow, river, water);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);
        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.PAWN, forest1);

        Zone.Forest forest2 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Lake lake2 = new Zone.Lake(100, 0, null);
        Zone.River river2 = new Zone.River(101, 0, lake2);
        Zone.Meadow meadow2 = new Zone.Meadow(102, List.of(new Animal(1001, Animal.Kind.DEER)), null);
        Zone.Meadow meadow3 = new Zone.Meadow(103, List.of(new Animal(1002, Animal.Kind.DEER)), null);

        Area<Zone.Forest> areaForest2 = new Area<>(Set.of(forest2), List.of(PlayerColor.RED), 0);
        ZonePartitions zonePartitions2 = new ZonePartitions(new ZonePartition<>(Set.of(areaForest2)), meadow, river, water);

        assertEquals(builder.build(), zonePartitions2);

        assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.HUT, forest2));
        assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.HUT, meadow1));
    }

    @Test
    void removePawn () {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Area<Zone.Forest> areaForest = new Area<>(Set.of(forest1), List.of(PlayerColor.BLUE, PlayerColor.RED), 2);
        ZonePartition<Zone.Forest> forest = new ZonePartition<>(Set.of(areaForest));

        Zone.River river1 = new Zone.River(0, 0, null);
        Area<Zone.River> areaRiver = new Area<>(Set.of(river1), List.of(PlayerColor.RED), 3);
        ZonePartition<Zone.River> river = new ZonePartition<>(Set.of(areaRiver));

        Zone.Lake lake = new Zone.Lake(0, 0, null);
        Zone.Water river1bis = new Zone.River(0, 0, lake);
        Area<Zone.Water> areaWater = new Area<>(Set.of(river1bis, river1, lake), List.of(PlayerColor.GREEN, PlayerColor.BLUE), 4);
        ZonePartition<Zone.Water> water = new ZonePartition<>(Set.of(areaWater));

        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null);
        Area<Zone.Meadow> areaMeadow = new Area<>(Set.of(meadow1), List.of(PlayerColor.YELLOW), 5);
        ZonePartition<Zone.Meadow> meadow = new ZonePartition<>(Set.of(areaMeadow));

        ZonePartitions zonePartitions = new ZonePartitions(forest, meadow, river, water);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);

        Zone.Forest forest2 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Lake lake2 = new Zone.Lake(100, 0, null);
        Zone.River river2 = new Zone.River(101, 0, lake2);
        Zone.Meadow meadow2 = new Zone.Meadow(102, List.of(new Animal(1001, Animal.Kind.DEER)), null);
        Zone.Meadow meadow3 = new Zone.Meadow(103, List.of(new Animal(1002, Animal.Kind.DEER)), null);

        builder.removePawn(PlayerColor.BLUE, forest1);

        //alors je suis pas sûr de mon coup ici, je sais pas trop si les zones sont enregistrées dans une nouvelle aire à part pour chacune mais on va dire que non
        Area<Zone.Forest> areaForest2 = new Area<>(Set.of(forest1), List.of(PlayerColor.RED), 0);
        ZonePartitions zonePartitions2 = new ZonePartitions(new ZonePartition<>(Set.of(areaForest2)), meadow, river, water);
        assertEquals(builder.build(), zonePartitions2);

        assertThrows(IllegalArgumentException.class, () -> builder.removePawn(PlayerColor.RED, lake));
    }

    @Test
    void clearGatherersWorks () {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Area<Zone.Forest> areaForest = new Area<>(Set.of(forest1), List.of(PlayerColor.BLUE, PlayerColor.RED), 2);
        ZonePartition<Zone.Forest> forest = new ZonePartition<>(Set.of(areaForest));

        Zone.River river1 = new Zone.River(0, 0, null);
        Area<Zone.River> areaRiver = new Area<>(Set.of(river1), List.of(PlayerColor.RED), 3);
        ZonePartition<Zone.River> river = new ZonePartition<>(Set.of(areaRiver));

        Zone.Lake lake = new Zone.Lake(0, 0, null);
        Zone.Water river1bis = new Zone.River(0, 0, lake);
        Area<Zone.Water> areaWater = new Area<>(Set.of(river1bis, river1, lake), List.of(PlayerColor.GREEN, PlayerColor.BLUE), 4);
        ZonePartition<Zone.Water> water = new ZonePartition<>(Set.of(areaWater));

        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null);
        Area<Zone.Meadow> areaMeadow = new Area<>(Set.of(meadow1), List.of(PlayerColor.YELLOW), 5);
        ZonePartition<Zone.Meadow> meadow = new ZonePartition<>(Set.of(areaMeadow));

        ZonePartitions zonePartitions = new ZonePartitions(forest, meadow, river, water);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);

        Zone.Forest forest2 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Lake lake2 = new Zone.Lake(100, 0, null);
        Zone.River river2 = new Zone.River(101, 0, lake2);
        Zone.Meadow meadow2 = new Zone.Meadow(102, List.of(new Animal(1001, Animal.Kind.DEER)), null);
        Zone.Meadow meadow3 = new Zone.Meadow(103, List.of(new Animal(1002, Animal.Kind.DEER)), null);

        builder.clearGatherers(areaForest);

        Area<Zone.Forest> areaForest2 = new Area<>(Set.of(forest1), List.of(), 2);
        ZonePartitions zonePartitions2 = new ZonePartitions(new ZonePartition<>(Set.of(areaForest2)), meadow, river, water);
        assertEquals(builder.build(), zonePartitions2);
    }

    @Test
    void clearFishersWork () {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Area<Zone.Forest> areaForest = new Area<>(Set.of(forest1), List.of(PlayerColor.BLUE, PlayerColor.RED), 2);
        ZonePartition<Zone.Forest> forest = new ZonePartition<>(Set.of(areaForest));

        Zone.River river1 = new Zone.River(0, 0, null);
        Area<Zone.River> areaRiver = new Area<>(Set.of(river1), List.of(PlayerColor.RED, PlayerColor.YELLOW), 3);
        ZonePartition<Zone.River> river = new ZonePartition<>(Set.of(areaRiver));

        Zone.Lake lake = new Zone.Lake(0, 0, null);
        Zone.Water river1bis = new Zone.River(0, 0, lake);
        Area<Zone.Water> areaWater = new Area<>(Set.of(river1bis, river1, lake), List.of(PlayerColor.GREEN, PlayerColor.BLUE), 4);
        ZonePartition<Zone.Water> water = new ZonePartition<>(Set.of(areaWater));

        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null);
        Area<Zone.Meadow> areaMeadow = new Area<>(Set.of(meadow1), List.of(PlayerColor.YELLOW), 5);
        ZonePartition<Zone.Meadow> meadow = new ZonePartition<>(Set.of(areaMeadow));

        ZonePartitions zonePartitions = new ZonePartitions(forest, meadow, river, water);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);

        Zone.Forest forest2 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Lake lake2 = new Zone.Lake(100, 0, null);
        Zone.River river2 = new Zone.River(101, 0, lake2);
        Zone.Meadow meadow2 = new Zone.Meadow(102, List.of(new Animal(1001, Animal.Kind.DEER)), null);
        Zone.Meadow meadow3 = new Zone.Meadow(103, List.of(new Animal(1002, Animal.Kind.DEER)), null);

        builder.clearFishers(areaRiver);

        //alors je suis pas sûr de mon coup ici, je sais pas trop si les zones sont enregistrées dans une nouvelle aire à part pour chacune mais on va dire que non
        Area<Zone.River> areaRiver2 = new Area<>(Set.of(river1), List.of(), 3);
        ZonePartitions zonePartitions2 = new ZonePartitions(forest, meadow, new ZonePartition<>(Set.of(areaRiver2)), water);
        assertEquals(builder.build(), zonePartitions2);
    }
}

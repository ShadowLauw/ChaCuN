package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ZonePartitionTestEmmanuel {
    @Test
    void zonePartitionConstructorWorks() {
        ZonePartition<Zone.River> zonePartition = new ZonePartition<>();
        assertTrue(zonePartition.areas().isEmpty());

        //initialisation et gros foutoir
        Set<Area<Zone.River>> areaList = new HashSet<>();
        List<PlayerColor> players = List.of(PlayerColor.GREEN, PlayerColor.RED);
        List<Animal> animals = List.of(new Animal(2, Animal.Kind.DEER), new Animal(3, Animal.Kind.AUROCHS));

        Zone.Lake lake = new Zone.Lake(3000, 0, null);
        Zone.River river1 = new Zone.River(1000, 2, null);
        Zone.River river2 = new Zone.River(1001, 4, null);
        Zone.River river3 = new Zone.River(1003, 5, null);
        Zone.River river4 = new Zone.River(1004, 5, lake);
        Zone.River river5 = new Zone.River(1005, 5, null);
        Zone.Forest forest = new Zone.Forest(2000, Zone.Forest.Kind.PLAIN);
        Zone.Meadow meadow = new Zone.Meadow(3000, animals, null);


        Set<Zone.River> zonesRiver1 = new HashSet<>();
        zonesRiver1.add(river1);
        zonesRiver1.add(river2);

        Set<Zone.River> zonesRiver2 = new HashSet<>();
        zonesRiver2.add(river3);
        zonesRiver2.add(river4);


        Area<Zone.River> area1 = new Area<>(zonesRiver1, players, 0);
        areaList.add(area1);
        ZonePartition<Zone.River> zonePartition2 = new ZonePartition<>(areaList);

        //Prions ensemble pour que ce test fonctionne et soit pas trop bancal MDR

        assertEquals(zonePartition2.areas(), areaList);
        areaList.clear();
        assertNotEquals(zonePartition2.areas(), areaList);

        assertThrows(IllegalArgumentException.class, () -> zonePartition.areaContaining(river5));
    }
@Test
void zonePartitionConstructorWorksForForest() {
    ZonePartition<Zone.Forest> zonePartition = new ZonePartition<>();
    assertTrue(zonePartition.areas().isEmpty());

    Set<Area<Zone.Forest>> areaList = new HashSet<>();
    List<PlayerColor> players = List.of(PlayerColor.GREEN, PlayerColor.RED);

    Zone.Forest forest1 = new Zone.Forest(1000, Zone.Forest.Kind.PLAIN);
    Zone.Forest forest2 = new Zone.Forest(1001, Zone.Forest.Kind.PLAIN);

    Set<Zone.Forest> zonesForest = new HashSet<>();
    zonesForest.add(forest1);
    zonesForest.add(forest2);

    Area<Zone.Forest> area1 = new Area<>(zonesForest, players, 0);
    areaList.add(area1);
    ZonePartition<Zone.Forest> zonePartition2 = new ZonePartition<>(areaList);

    assertEquals(zonePartition2.areas(), areaList);
    areaList.clear();
    assertNotEquals(zonePartition2.areas(), areaList);

    assertThrows(IllegalArgumentException.class, () -> zonePartition.areaContaining(forest2));
}

@Test
void zonePartitionConstructorWorksForMeadow() {
    ZonePartition<Zone.Meadow> zonePartition = new ZonePartition<>();
    assertTrue(zonePartition.areas().isEmpty());

    Set<Area<Zone.Meadow>> areaList = new HashSet<>();
    List<PlayerColor> players = List.of(PlayerColor.GREEN, PlayerColor.RED);
    List<Animal> animals = List.of(new Animal(2, Animal.Kind.DEER), new Animal(3, Animal.Kind.AUROCHS));

    Zone.Meadow meadow1 = new Zone.Meadow(1000, animals, null);
    Zone.Meadow meadow2 = new Zone.Meadow(1001, animals, null);

    Set<Zone.Meadow> zonesMeadow = new HashSet<>();
    zonesMeadow.add(meadow1);
    zonesMeadow.add(meadow2);

    Area<Zone.Meadow> area1 = new Area<>(zonesMeadow, players, 0);
    areaList.add(area1);
    ZonePartition<Zone.Meadow> zonePartition2 = new ZonePartition<>(areaList);

    assertEquals(zonePartition2.areas(), areaList);
    areaList.clear();
    assertNotEquals(zonePartition2.areas(), areaList);

    assertThrows(IllegalArgumentException.class, () -> zonePartition.areaContaining(meadow2));
}

    @Test
    void zonePartitionBuilderBuild() {


        Set<Zone.Forest> zones1 = new HashSet<>();
        Set<Zone.Forest> zones2 = new HashSet<>();
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest3 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest4 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest5 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        zones1.add(forest1);
        zones1.add(forest2);
        zones1.add(forest3);
        zones2.add(forest4);
        zones2.add(forest5);
        Area<Zone.Forest> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
        Area<Zone.Forest> area2 = new Area<>(zones2, List.of(PlayerColor.BLUE), 5);


        Set<Area<Zone.Forest>> areaList = new HashSet<>();
        areaList.add(area1);
        areaList.add(area2);

        ZonePartition<Zone.Forest> zonePartition = new ZonePartition<>(areaList);

        ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);



        ZonePartition<Zone.Forest> zonePartitionCorrect1 = new ZonePartition<>(areaList);
        assertEquals(builder.build(), zonePartitionCorrect1);
    }

    @Test
void zonePartitionBuilderBuildForMeadows() {
    Set<Zone.Meadow> zones1 = new HashSet<>();
    Set<Zone.Meadow> zones2 = new HashSet<>();
    List<Animal> animals = List.of(new Animal(2, Animal.Kind.DEER), new Animal(3, Animal.Kind.AUROCHS));
    Zone.Meadow meadow1 = new Zone.Meadow(0, animals, null);
    Zone.Meadow meadow2 = new Zone.Meadow(1, animals, null);
    Zone.Meadow meadow3 = new Zone.Meadow(2, animals, null);
    Zone.Meadow meadow4 = new Zone.Meadow(3, animals, null);
    Zone.Meadow meadow5 = new Zone.Meadow(4, animals, null);
    zones1.add(meadow1);
    zones1.add(meadow2);
    zones1.add(meadow3);
    zones2.add(meadow4);
    zones2.add(meadow5);
    Area<Zone.Meadow> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
    Area<Zone.Meadow> area2 = new Area<>(zones2, List.of(PlayerColor.BLUE), 5);

    Set<Area<Zone.Meadow>> areaList = new HashSet<>();
    areaList.add(area1);
    areaList.add(area2);

    ZonePartition<Zone.Meadow> zonePartition = new ZonePartition<>(areaList);

    ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

    ZonePartition<Zone.Meadow> zonePartitionCorrect1 = new ZonePartition<>(areaList);
    assertEquals(builder.build(), zonePartitionCorrect1);
}

@Test
void zonePartitionBuilderBuildForRivers() {
    Set<Zone.River> zones1 = new HashSet<>();
    Set<Zone.River> zones2 = new HashSet<>();
    Zone.River river1 = new Zone.River(0, 2, null);
    Zone.River river2 = new Zone.River(1, 3, null);
    Zone.River river3 = new Zone.River(2, 4, null);
    Zone.River river4 = new Zone.River(3, 5, null);
    Zone.River river5 = new Zone.River(4, 6, null);
    zones1.add(river1);
    zones1.add(river2);
    zones1.add(river3);
    zones2.add(river4);
    zones2.add(river5);
    Area<Zone.River> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
    Area<Zone.River> area2 = new Area<>(zones2, List.of(PlayerColor.BLUE), 5);

    Set<Area<Zone.River>> areaList = new HashSet<>();
    areaList.add(area1);
    areaList.add(area2);

    ZonePartition<Zone.River> zonePartition = new ZonePartition<>(areaList);

    ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

    ZonePartition<Zone.River> zonePartitionCorrect1 = new ZonePartition<>(areaList);
    assertEquals(builder.build(), zonePartitionCorrect1);
}


    @Test
    void ZonePartitionBuilderAddSingleton() {

        Set<Zone.Forest> zones1 = new HashSet<>();
        Set<Zone.Forest> zones2 = new HashSet<>();
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest3 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest4 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest5 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        zones1.add(forest1);
        zones1.add(forest2);
        zones1.add(forest3);
        zones2.add(forest4);

        Area<Zone.Forest> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
        Area<Zone.Forest> area2 = new Area<>(zones2, List.of(), 2);





        Set<Area<Zone.Forest>> areaList = new HashSet<>();
        areaList.add(area1);
        ZonePartition<Zone.Forest> zonePartition = new ZonePartition<>(areaList);

        ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);
        builder.addSingleton(forest4, 2);

        areaList.add(area2);

        ZonePartition<Zone.Forest> zoneParitionCorrect = new ZonePartition<>(areaList);
        assertEquals(builder.build(), zoneParitionCorrect);
    }

    @Test
void ZonePartitionBuilderAddSingletonForRivers() {
    Set<Zone.River> zones1 = new HashSet<>();
    Zone.River river1 = new Zone.River(0, 2, null);
    Zone.River river2 = new Zone.River(1, 3, null);
    zones1.add(river1);

    Area<Zone.River> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
    Area<Zone.River> area2 = new Area<>(Set.of(river2), List.of(), 2);

    Set<Area<Zone.River>> areaList = new HashSet<>();
    areaList.add(area1);
    ZonePartition<Zone.River> zonePartition = new ZonePartition<>(areaList);

    ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);
    builder.addSingleton(river2, 2);

    areaList.add(area2);

    ZonePartition<Zone.River> zonePartitionCorrect = new ZonePartition<>(areaList);
    assertEquals(builder.build(), zonePartitionCorrect);
}

@Test
void ZonePartitionBuilderAddSingletonForMeadows() {
    Set<Zone.Meadow> zones1 = new HashSet<>();
    List<Animal> animals = List.of(new Animal(2, Animal.Kind.DEER), new Animal(3, Animal.Kind.AUROCHS));
    Zone.Meadow meadow1 = new Zone.Meadow(0, animals, null);
    Zone.Meadow meadow2 = new Zone.Meadow(1, animals, null);
    zones1.add(meadow1);

    Area<Zone.Meadow> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
    Area<Zone.Meadow> area2 = new Area<>(Set.of(meadow2), List.of(), 2);

    Set<Area<Zone.Meadow>> areaList = new HashSet<>();
    areaList.add(area1);
    ZonePartition<Zone.Meadow> zonePartition = new ZonePartition<>(areaList);

    ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);
    builder.addSingleton(meadow2, 2);

    areaList.add(area2);

    ZonePartition<Zone.Meadow> zonePartitionCorrect = new ZonePartition<>(areaList);
    assertEquals(builder.build(), zonePartitionCorrect);
}
    @Test
    void zonePartitionAddInitialOccupantTest() {

        Set<Zone.Forest> zones1 = new HashSet<>();
        Set<Zone.Forest> zones2 = new HashSet<>();
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest3 = new Zone.Forest(2, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest4 = new Zone.Forest(3, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest5 = new Zone.Forest(4, Zone.Forest.Kind.PLAIN);
        zones1.add(forest1);
        zones1.add(forest2);
        zones1.add(forest3);
        Area<Zone.Forest> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
        Area<Zone.Forest> area2 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.BLUE), 2);
        Set<Area<Zone.Forest>> areaList = new HashSet<>();
        areaList.add(area1);


        ZonePartition<Zone.Forest> zonePartition = new ZonePartition<>(areaList);

        ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

        builder.addInitialOccupant(forest1, PlayerColor.BLUE);
        builder.addInitialOccupant(forest2, PlayerColor.GREEN);
        builder.addInitialOccupant(forest3, PlayerColor.BLUE);

        Set<Area<Zone.Forest>> areaList2 = new HashSet<>();
        areaList2.add(area2);
        ZonePartition<Zone.Forest> zonePartitionGood = new ZonePartition<>(areaList2);

        assertEquals(builder.build(), zonePartitionGood);

        assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(forest5, PlayerColor.BLUE));
    }

    @Test
void zonePartitionAddInitialOccupantTestForRivers() {
    Set<Zone.River> zones1 = new HashSet<>();
    Zone.River river1 = new Zone.River(0, 2, null);
    Zone.River river2 = new Zone.River(1, 3, null);
    Zone.River river3 = new Zone.River(2, 4, null);
    Zone.River river4 = new Zone.River(3, 5, null);
    Zone.River river5 = new Zone.River(4, 6, null);
    zones1.add(river1);
    zones1.add(river2);
    zones1.add(river3);
    Area<Zone.River> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
    Area<Zone.River> area2 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.BLUE), 2);
    Set<Area<Zone.River>> areaList = new HashSet<>();
    areaList.add(area1);

    ZonePartition<Zone.River> zonePartition = new ZonePartition<>(areaList);

    ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

    builder.addInitialOccupant(river1, PlayerColor.BLUE);
    builder.addInitialOccupant(river2, PlayerColor.GREEN);
    builder.addInitialOccupant(river3, PlayerColor.BLUE);

    Set<Area<Zone.River>> areaList2 = new HashSet<>();
    areaList2.add(area2);
    ZonePartition<Zone.River> zonePartitionGood = new ZonePartition<>(areaList2);

    assertEquals(builder.build(), zonePartitionGood);

    assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(river5, PlayerColor.BLUE));
}

@Test
void zonePartitionAddInitialOccupantTestForMeadows() {
    Set<Zone.Meadow> zones1 = new HashSet<>();
    List<Animal> animals = List.of(new Animal(2, Animal.Kind.DEER), new Animal(3, Animal.Kind.AUROCHS));
    Zone.Meadow meadow1 = new Zone.Meadow(0, animals, null);
    Zone.Meadow meadow2 = new Zone.Meadow(1, animals, null);
    Zone.Meadow meadow3 = new Zone.Meadow(2, animals, null);
    Zone.Meadow meadow4 = new Zone.Meadow(3, animals, null);
    Zone.Meadow meadow5 = new Zone.Meadow(4, animals, null);
    zones1.add(meadow1);
    zones1.add(meadow2);
    zones1.add(meadow3);
    Area<Zone.Meadow> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
    Area<Zone.Meadow> area2 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.BLUE), 2);
    Set<Area<Zone.Meadow>> areaList = new HashSet<>();
    areaList.add(area1);

    ZonePartition<Zone.Meadow> zonePartition = new ZonePartition<>(areaList);

    ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

    builder.addInitialOccupant(meadow1, PlayerColor.BLUE);
    builder.addInitialOccupant(meadow2, PlayerColor.GREEN);
    builder.addInitialOccupant(meadow3, PlayerColor.BLUE);

    Set<Area<Zone.Meadow>> areaList2 = new HashSet<>();
    areaList2.add(area2);
    ZonePartition<Zone.Meadow> zonePartitionGood = new ZonePartition<>(areaList2);

    assertEquals(builder.build(), zonePartitionGood);

    assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(meadow5, PlayerColor.BLUE));
}
    @Test
    void ZonePartitionBuilderRemoveOccupantTest() {

        Set<Zone.Forest> zones1 = new HashSet<>();
        Set<Zone.Forest> zones2 = new HashSet<>();
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest3 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest4 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest5 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        zones1.add(forest1);
        zones1.add(forest2);
        zones1.add(forest3);
        Area<Zone.Forest> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
        zones1.add(forest4);
        Area<Zone.Forest> area2 = new Area<>(zones1, List.of(PlayerColor.RED), 2);

        Set<Area<Zone.Forest>> areaList = new HashSet<>();
        areaList.add(area1);


        ZonePartition<Zone.Forest> zonePartition = new ZonePartition<>(areaList);

        ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

        builder.removeOccupant(forest1, PlayerColor.GREEN);

        Set<Area<Zone.Forest>> areaList2 = new HashSet<>();
        areaList2.add(area2);
        ZonePartition<Zone.Forest> zonePartitionGood = new ZonePartition<>(areaList2);

        assertEquals(builder.build(), zonePartitionGood);

        assertThrows(IllegalArgumentException.class, () -> builder.removeOccupant(forest5, PlayerColor.BLUE));
    }

    @Test
void ZonePartitionBuilderRemoveOccupantTestForRivers() {
    Set<Zone.River> zones1 = new HashSet<>();
    Zone.River river1 = new Zone.River(0, 2, null);
    Zone.River river2 = new Zone.River(1, 3, null);
    Zone.River river3 = new Zone.River(2, 4, null);
    Zone.River river4 = new Zone.River(3, 5, null);
    Zone.River river5 = new Zone.River(4, 6, null);
    zones1.add(river1);
    zones1.add(river2);
    zones1.add(river3);
    Area<Zone.River> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
    zones1.add(river4);
    Area<Zone.River> area2 = new Area<>(zones1, List.of(PlayerColor.RED), 2);

    Set<Area<Zone.River>> areaList = new HashSet<>();
    areaList.add(area1);

    ZonePartition<Zone.River> zonePartition = new ZonePartition<>(areaList);

    ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

    builder.removeOccupant(river1, PlayerColor.GREEN);

    Set<Area<Zone.River>> areaList2 = new HashSet<>();
    areaList2.add(area2);
    ZonePartition<Zone.River> zonePartitionGood = new ZonePartition<>(areaList2);

    assertEquals(builder.build(), zonePartitionGood);

    assertThrows(IllegalArgumentException.class, () -> builder.removeOccupant(river5, PlayerColor.BLUE));
}

@Test
void ZonePartitionBuilderRemoveOccupantTestForMeadows() {
    Set<Zone.Meadow> zones1 = new HashSet<>();
    List<Animal> animals = List.of(new Animal(2, Animal.Kind.DEER), new Animal(3, Animal.Kind.AUROCHS));
    Zone.Meadow meadow1 = new Zone.Meadow(0, animals, null);
    Zone.Meadow meadow2 = new Zone.Meadow(1, animals, null);
    Zone.Meadow meadow3 = new Zone.Meadow(2, animals, null);
    Zone.Meadow meadow4 = new Zone.Meadow(3, animals, null);
    Zone.Meadow meadow5 = new Zone.Meadow(4, animals, null);
    zones1.add(meadow1);
    zones1.add(meadow2);
    zones1.add(meadow3);
    Area<Zone.Meadow> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
    zones1.add(meadow4);
    Area<Zone.Meadow> area2 = new Area<>(zones1, List.of(PlayerColor.RED), 2);

    Set<Area<Zone.Meadow>> areaList = new HashSet<>();
    areaList.add(area1);

    ZonePartition<Zone.Meadow> zonePartition = new ZonePartition<>(areaList);

    ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

    builder.removeOccupant(meadow1, PlayerColor.GREEN);

    Set<Area<Zone.Meadow>> areaList2 = new HashSet<>();
    areaList2.add(area2);
    ZonePartition<Zone.Meadow> zonePartitionGood = new ZonePartition<>(areaList2);

    assertEquals(builder.build(), zonePartitionGood);

    assertThrows(IllegalArgumentException.class, () -> builder.removeOccupant(meadow5, PlayerColor.BLUE));
}
    @Test
    void ZonePartitionRemoveAllOccupantsOfTest () {

        Set<Zone.Forest> zones1 = new HashSet<>();
        Set<Zone.Forest> zones2 = new HashSet<>();
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest3 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest4 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest5 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        zones1.add(forest1);
        zones1.add(forest2);
        zones1.add(forest3);
        Area<Zone.Forest> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
        zones1.add(forest4);
        Area<Zone.Forest> area2 = new Area<>(zones1, List.of(), 2);

        Set<Area<Zone.Forest>> areaList = new HashSet<>();
        areaList.add(area1);


        ZonePartition<Zone.Forest> zonePartition = new ZonePartition<>(areaList);

        ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

        builder.removeAllOccupantsOf(area1);

        Set<Area<Zone.Forest>> areaList2 = new HashSet<>();
        areaList2.add(area2);
        ZonePartition<Zone.Forest> zonePartitionGood = new ZonePartition<>(areaList2);

        assertEquals(builder.build(), zonePartitionGood);

        assertThrows(IllegalArgumentException.class, () -> builder.removeOccupant(forest5, PlayerColor.BLUE));
    }

    @Test
void ZonePartitionRemoveAllOccupantsOfTestForRivers() {
    Set<Zone.River> zones1 = new HashSet<>();
    Zone.River river1 = new Zone.River(0, 2, null);
    Zone.River river2 = new Zone.River(1, 3, null);
    Zone.River river3 = new Zone.River(2, 4, null);
    Zone.River river4 = new Zone.River(3, 5, null);
    Zone.River river5 = new Zone.River(4, 6, null);
    zones1.add(river1);
    zones1.add(river2);
    zones1.add(river3);
    Area<Zone.River> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
    zones1.add(river4);
    Area<Zone.River> area2 = new Area<>(zones1, List.of(), 2);

    Set<Area<Zone.River>> areaList = new HashSet<>();
    areaList.add(area1);

    ZonePartition<Zone.River> zonePartition = new ZonePartition<>(areaList);

    ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

    builder.removeAllOccupantsOf(area1);

    Set<Area<Zone.River>> areaList2 = new HashSet<>();
    areaList2.add(area2);
    ZonePartition<Zone.River> zonePartitionGood = new ZonePartition<>(areaList2);

    assertEquals(builder.build(), zonePartitionGood);

    assertThrows(IllegalArgumentException.class, () -> builder.removeOccupant(river5, PlayerColor.BLUE));
}

@Test
void ZonePartitionRemoveAllOccupantsOfTestForMeadows() {
    Set<Zone.Meadow> zones1 = new HashSet<>();
    List<Animal> animals = List.of(new Animal(2, Animal.Kind.DEER), new Animal(3, Animal.Kind.AUROCHS));
    Zone.Meadow meadow1 = new Zone.Meadow(0, animals, null);
    Zone.Meadow meadow2 = new Zone.Meadow(1, animals, null);
    Zone.Meadow meadow3 = new Zone.Meadow(2, animals, null);
    Zone.Meadow meadow4 = new Zone.Meadow(3, animals, null);
    Zone.Meadow meadow5 = new Zone.Meadow(4, animals, null);
    zones1.add(meadow1);
    zones1.add(meadow2);
    zones1.add(meadow3);
    Area<Zone.Meadow> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
    zones1.add(meadow4);
    Area<Zone.Meadow> area2 = new Area<>(zones1, List.of(), 2);

    Set<Area<Zone.Meadow>> areaList = new HashSet<>();
    areaList.add(area1);

    ZonePartition<Zone.Meadow> zonePartition = new ZonePartition<>(areaList);

    ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

    builder.removeAllOccupantsOf(area1);

    Set<Area<Zone.Meadow>> areaList2 = new HashSet<>();
    areaList2.add(area2);
    ZonePartition<Zone.Meadow> zonePartitionGood = new ZonePartition<>(areaList2);

    assertEquals(builder.build(), zonePartitionGood);

    assertThrows(IllegalArgumentException.class, () -> builder.removeOccupant(meadow5, PlayerColor.BLUE));
}
    @Test
    void ZonePartitionUnionTest () {

        Set<Zone.Forest> zones1 = new HashSet<>();
        Set<Zone.Forest> zones2 = new HashSet<>();
        Set<Zone.Forest> zones3 = new HashSet<>();
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest3 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest4 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest5 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        zones1.add(forest1);
        zones1.add(forest2);
        zones1.add(forest3);
        zones2.add(forest4);
        zones2.add(forest5);

        zones3.add(forest1);
        zones3.add(forest2);
        zones3.add(forest3);
        zones3.add(forest4);
        zones3.add(forest5);

        Area<Zone.Forest> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
        Area<Zone.Forest> area2 = new Area<>(zones2, List.of(PlayerColor.BLUE), 0);
        Area<Zone.Forest> area3 = new Area<>(zones3, List.of(PlayerColor.GREEN, PlayerColor.BLUE), 2);

        Set<Area<Zone.Forest>> areaList = new HashSet<>();
        areaList.add(area1);
        areaList.add(area2);


        ZonePartition<Zone.Forest> zonePartition = new ZonePartition<>(areaList);

        ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

        builder.union(forest2, forest2);

        assertEquals(builder.build(), new ZonePartition<>(areaList));

        areaList.clear();
        areaList.add(area3);
        builder.union(forest2, forest5);
        ZonePartition<Zone.Forest> zonePartitionGood = new ZonePartition<>(areaList);
        assertEquals(builder.build(), zonePartitionGood);
    }

    @Test
void ZonePartitionUnionTestForRivers() {
    Set<Zone.River> zones1 = new HashSet<>();
    Set<Zone.River> zones2 = new HashSet<>();
    Set<Zone.River> zones3 = new HashSet<>();
    Zone.River river1 = new Zone.River(0, 2, null);
    Zone.River river2 = new Zone.River(1, 3, null);
    Zone.River river3 = new Zone.River(2, 4, null);
    Zone.River river4 = new Zone.River(3, 5, null);
    Zone.River river5 = new Zone.River(4, 6, null);
    zones1.add(river1);
    zones1.add(river2);
    zones1.add(river3);
    zones2.add(river4);
    zones2.add(river5);

    zones3.add(river1);
    zones3.add(river2);
    zones3.add(river3);
    zones3.add(river4);
    zones3.add(river5);

    Area<Zone.River> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
    Area<Zone.River> area2 = new Area<>(zones2, List.of(PlayerColor.BLUE), 0);
    Area<Zone.River> area3 = new Area<>(zones3, List.of(PlayerColor.GREEN, PlayerColor.BLUE), 2);

    Set<Area<Zone.River>> areaList = new HashSet<>();
    areaList.add(area1);
    areaList.add(area2);

    ZonePartition<Zone.River> zonePartition = new ZonePartition<>(areaList);

    ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

    builder.union(river2, river2);

    assertEquals(builder.build(), new ZonePartition<>(areaList));

    areaList.clear();
    areaList.add(area3);
    builder.union(river2, river5);
    ZonePartition<Zone.River> zonePartitionGood = new ZonePartition<>(areaList);
    assertEquals(builder.build(), zonePartitionGood);
}

@Test
void ZonePartitionUnionTestForMeadows() {
    Set<Zone.Meadow> zones1 = new HashSet<>();
    Set<Zone.Meadow> zones2 = new HashSet<>();
    Set<Zone.Meadow> zones3 = new HashSet<>();
    List<Animal> animals = List.of(new Animal(2, Animal.Kind.DEER), new Animal(3, Animal.Kind.AUROCHS));
    Zone.Meadow meadow1 = new Zone.Meadow(0, animals, null);
    Zone.Meadow meadow2 = new Zone.Meadow(1, animals, null);
    Zone.Meadow meadow3 = new Zone.Meadow(2, animals, null);
    Zone.Meadow meadow4 = new Zone.Meadow(3, animals, null);
    Zone.Meadow meadow5 = new Zone.Meadow(4, animals, null);
    zones1.add(meadow1);
    zones1.add(meadow2);
    zones1.add(meadow3);
    zones2.add(meadow4);
    zones2.add(meadow5);

    zones3.add(meadow1);
    zones3.add(meadow2);
    zones3.add(meadow3);
    zones3.add(meadow4);
    zones3.add(meadow5);

    Area<Zone.Meadow> area1 = new Area<>(zones1, List.of(PlayerColor.GREEN, PlayerColor.RED), 2);
    Area<Zone.Meadow> area2 = new Area<>(zones2, List.of(PlayerColor.BLUE), 0);
    Area<Zone.Meadow> area3 = new Area<>(zones3, List.of(PlayerColor.GREEN, PlayerColor.BLUE), 2);

    Set<Area<Zone.Meadow>> areaList = new HashSet<>();
    areaList.add(area1);
    areaList.add(area2);

    ZonePartition<Zone.Meadow> zonePartition = new ZonePartition<>(areaList);

    ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

    builder.union(meadow2, meadow2);

    assertEquals(builder.build(), new ZonePartition<>(areaList));

    areaList.clear();
    areaList.add(area3);
    builder.union(meadow2, meadow5);
    ZonePartition<Zone.Meadow> zonePartitionGood = new ZonePartition<>(areaList);
    assertEquals(builder.build(), zonePartitionGood);
}
}

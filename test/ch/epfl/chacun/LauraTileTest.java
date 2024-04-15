package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.epfl.chacun.GenerateTileSide.*;

import static org.junit.jupiter.api.Assertions.*;

public class LauraTileTest {

    @Test
    void sideWorksForAMix() {
        TileSide.River river = generateTileSideRiverWithoutLake();
        TileSide.River riverWithLake = generateTileSideRiverWithLake();
        TileSide.Forest forest = generateTileSideForest();
        TileSide.Meadow meadow = generateTileSideMeadow();

        assertEquals(List.of(river, riverWithLake, forest, meadow), new Tile(0, Tile.Kind.NORMAL, river, riverWithLake, forest, meadow).sides());
    }

    @Test
    void sideZonesWorksForAMix() {
        TileSide.River river = generateTileSideRiverWithoutLake();
        TileSide.River riverWithLake = generateTileSideRiverWithLake();
        TileSide.Forest forest = generateTileSideForest();
        TileSide.Meadow meadow = generateTileSideMeadow();

        Set<Zone> expected = new HashSet<>();
        expected.addAll(river.zones());
        expected.addAll(riverWithLake.zones());
        expected.addAll(forest.zones());
        expected.addAll(meadow.zones());

        assertEquals(expected, new Tile(0, Tile.Kind.NORMAL, river, riverWithLake, forest, meadow).sideZones());
    }

    @Test
    void zonesWorksForAMix() {
        TileSide.River river = generateTileSideRiverWithoutLake();
        TileSide.River riverWithLake = generateTileSideRiverWithLake();
        TileSide.Forest forest = generateTileSideForest();
        TileSide.Meadow meadow = generateTileSideMeadow();

        Set<Zone> expected = new HashSet<>();
        List<Zone> riverZones = riverWithLake.zones();
        expected.addAll(river.zones());
        expected.addAll(riverWithLake.zones());
        expected.addAll(forest.zones());
        expected.addAll(meadow.zones());
        expected.add(((Zone.River)riverZones.get(1)).lake());

        assertEquals(expected, new Tile(0, Tile.Kind.NORMAL, river, riverWithLake, forest, meadow).zones());
    }
}

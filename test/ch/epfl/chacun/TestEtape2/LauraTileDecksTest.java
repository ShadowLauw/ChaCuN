package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.chacun.GenerateTileSide.*;
import static org.junit.jupiter.api.Assertions.*;

public class LauraTileDecksTest {
    @Test
    void tileDecksIsImmutable() {
        List<Tile> startTiles = List.of(generateTile(0, "RFLM", Tile.Kind.START));
        List<Tile> normalTiles = List.of(generateTile(1, "MFLM", Tile.Kind.NORMAL));
        List<Tile> menhirTiles = List.of(generateTile(2, "LRMM", Tile.Kind.MENHIR));
        var mutableStartTile = new ArrayList<>(startTiles);
        var mutableNormalTile = new ArrayList<>(normalTiles);
        var mutableMenhirTile = new ArrayList<>(menhirTiles);
        TileDecks tileDecks = new TileDecks(mutableStartTile, mutableNormalTile, mutableMenhirTile);
        mutableStartTile.clear();
        mutableNormalTile.clear();
        mutableMenhirTile.clear();
        assertEquals(startTiles, tileDecks.startTiles());
        assertEquals(normalTiles, tileDecks.normalTiles());
        assertEquals(menhirTiles, tileDecks.menhirTiles());
    }

    @Test
    void deckSizeTest() {
        List<Tile> startTiles = List.of(generateTile(0, "RFLM", Tile.Kind.START), generateTile(1, "MFLM", Tile.Kind.START));
        List<Tile> normalTiles = List.of(generateTile(1, "MFLM", Tile.Kind.NORMAL));
        List<Tile> menhirTiles = List.of(generateTile(2, "LRMM", Tile.Kind.MENHIR), generateTile(2, "LRMM", Tile.Kind.MENHIR), generateTile(2, "LRMM", Tile.Kind.MENHIR));
        TileDecks tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertEquals(2, tileDecks.deckSize(Tile.Kind.START));
        assertEquals(1, tileDecks.deckSize(Tile.Kind.NORMAL));
        assertEquals(3, tileDecks.deckSize(Tile.Kind.MENHIR));
    }

    @Test
    void topTileTest() {
        List<Tile> startTiles = List.of(generateTile(0, "RFLM", Tile.Kind.START), generateTile(1, "MFLM", Tile.Kind.START));
        List<Tile> normalTiles = List.of(generateTile(1, "MFLM", Tile.Kind.NORMAL));
        List<Tile> menhirTiles = List.of(generateTile(2, "LRMM", Tile.Kind.MENHIR), generateTile(2, "LRLM", Tile.Kind.MENHIR), generateTile(2, "MRMM", Tile.Kind.MENHIR));
        TileDecks tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertEquals(generateTile(0, "RFLM", Tile.Kind.START), tileDecks.topTile(Tile.Kind.START));
        assertEquals(generateTile(1, "MFLM", Tile.Kind.NORMAL), tileDecks.topTile(Tile.Kind.NORMAL));
        assertEquals(generateTile(2, "LRMM", Tile.Kind.MENHIR), tileDecks.topTile(Tile.Kind.MENHIR));
    }

    @Test
    void topTileTestEmptyDeck() {
        List<Tile> startTiles = List.of();
        List<Tile> normalTiles = List.of();
        List<Tile> menhirTiles = List.of();
        TileDecks tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertNull(tileDecks.topTile(Tile.Kind.START));
        assertNull(tileDecks.topTile(Tile.Kind.NORMAL));
        assertNull(tileDecks.topTile(Tile.Kind.MENHIR));
    }

    @Test
    void withTopTileDrawnTest() {
        List<Tile> startTiles = List.of(generateTile(0, "RFLM", Tile.Kind.START), generateTile(1, "MFLM", Tile.Kind.START));
        List<Tile> normalTiles = List.of(generateTile(1, "MFLM", Tile.Kind.NORMAL));
        List<Tile> menhirTiles = List.of(generateTile(2, "LRMM", Tile.Kind.MENHIR), generateTile(46, "LRLM", Tile.Kind.MENHIR), generateTile(24, "MRMM", Tile.Kind.MENHIR));
        List<Tile> startTilesExpected = List.of(generateTile(1, "MFLM", Tile.Kind.START));
        List<Tile> normalTilesExpected = List.of();
        List<Tile> menhirTilesExpected = List.of(generateTile(46, "LRLM", Tile.Kind.MENHIR), generateTile(24, "MRMM", Tile.Kind.MENHIR));
        TileDecks tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertEquals(new TileDecks(startTilesExpected, normalTiles, menhirTiles), tileDecks.withTopTileDrawn(Tile.Kind.START));
        assertEquals(new TileDecks(startTiles, normalTilesExpected, menhirTiles), tileDecks.withTopTileDrawn(Tile.Kind.NORMAL));
        assertEquals(new TileDecks(startTiles, normalTiles, menhirTilesExpected), tileDecks.withTopTileDrawn(Tile.Kind.MENHIR));
    }

    @Test
    void withTopTileDrawnTestThrows() {
        List<Tile> startTiles = List.of();
        List<Tile> normalTiles = List.of();
        List<Tile> menhirTiles = List.of();
        TileDecks tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertThrows(IllegalArgumentException.class, () -> tileDecks.withTopTileDrawn(Tile.Kind.START));
        assertThrows(IllegalArgumentException.class, () -> tileDecks.withTopTileDrawn(Tile.Kind.NORMAL));
        assertThrows(IllegalArgumentException.class, () -> tileDecks.withTopTileDrawn(Tile.Kind.MENHIR));
    }

    @Test
    void withTopTileDrawnUntilTest() {
        List<Tile> startTiles = List.of(generateTile(0, "RFLM", Tile.Kind.START), generateTile(1, "MFLM", Tile.Kind.START), generateTile(10, "LRMM", Tile.Kind.START));
        List<Tile> normalTiles = List.of(generateTile(1, "MFMM", Tile.Kind.NORMAL), generateTile(2, "RRMM", Tile.Kind.NORMAL));
        List<Tile> menhirTiles = List.of(generateTile(2, "LFMM", Tile.Kind.MENHIR), generateTile(46, "LRLM", Tile.Kind.MENHIR), generateTile(24, "MRMM", Tile.Kind.MENHIR));
        List<Tile> startTilesExpected = List.of(generateTile(10, "LRMM", Tile.Kind.START));
        List<Tile> normalTilesExpected = List.of();
        List<Tile> menhirTilesExpected = List.of(generateTile(46, "LRLM", Tile.Kind.MENHIR), generateTile(24, "MRMM", Tile.Kind.MENHIR));
        TileDecks tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertEquals(new TileDecks(startTilesExpected, normalTiles, menhirTiles), tileDecks.withTopTileDrawnUntil(Tile.Kind.START, tile -> tile.id() > 2));
        assertEquals(new TileDecks(startTiles, normalTilesExpected, menhirTiles), tileDecks.withTopTileDrawnUntil(Tile.Kind.NORMAL, tile -> tile.zones().size() > 2));
        assertEquals(new TileDecks(startTiles, normalTiles, menhirTilesExpected), tileDecks.withTopTileDrawnUntil(Tile.Kind.MENHIR, tile -> tile.zones().contains(generateZoneRiverWithoutLake())));
    }
    /**
     * Generates a tile with the given parameters
     * @param id
     * @param ts
     * @param kind
     * @return Tile
     */
    Tile generateTile(int id, String ts, Tile.Kind kind) {
        TileSide[] tileSides = new TileSide[4];
        for (int i = 0; i < 4; i++) {
            switch (ts.charAt(i)) {
                case 'R' -> tileSides[i] = generateTileSideRiverWithoutLake();
                case 'F' -> tileSides[i] = generateTileSideForest();
                case 'M' -> tileSides[i] = generateTileSideMeadow();
                case 'L' -> tileSides[i] = generateTileSideRiverWithLake();
            }
        }
        return new Tile(id, kind, tileSides[0], tileSides[1], tileSides[2], tileSides[3]);
    }
}

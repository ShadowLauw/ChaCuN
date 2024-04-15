package ch.epfl.chacun;
import javafx.scene.paint.Color;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTestEmmanuel {
    @Test
    void javafixisInstalled () {
        Color c = Color.RED;
    }
    @Test
    void canAddTileTestEmmanuel () {
        Board board = Board.EMPTY;

        Tile startTile = TileReader.readTileFromCSV(56);
        assertFalse(board.couldPlaceTile(startTile));
        assertFalse(board.canAddTile(new PlacedTile(startTile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0))));

        board = board.withNewTile(new PlacedTile(startTile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0)));

        Tile tile38 = TileReader.readTileFromCSV(38);
        assertTrue(board.couldPlaceTile(tile38));
        assertTrue(board.canAddTile(new PlacedTile(tile38, PlayerColor.RED, Rotation.HALF_TURN, new Pos(1, 0))));
        assertFalse(board.canAddTile(new PlacedTile(tile38, PlayerColor.RED, Rotation.NONE, new Pos(-1, 0))));

        board = board.withNewTile(new PlacedTile(tile38, PlayerColor.RED, Rotation.NONE, new Pos(1, 0)));

        Tile tile02  = TileReader.readTileFromCSV(2);
        assertTrue(board.couldPlaceTile(tile02));
        assertTrue(board.canAddTile(new PlacedTile(tile02, PlayerColor.RED, Rotation.NONE, new Pos(-1, 0))));
        assertFalse(board.canAddTile(new PlacedTile(tile02, PlayerColor.RED, Rotation.NONE, new Pos(0, 1))));

        board = board.withNewTile(new PlacedTile(tile02, PlayerColor.RED, Rotation.NONE, new Pos(-1, 0)));

        Tile tile03 = TileReader.readTileFromCSV(13);
        assertFalse(board.couldPlaceTile(tile03));

    }

    @Test
    void couldPlaceTileTestEmmanuel() {
        //voir au-dessus

        //les tests sont pour le moment assez simple et testent les cas plutôt généraux.
    }

    @Test
    void withNewTileTestEmmanuel() {
        Board board = Board.EMPTY;

        Tile startTile = TileReader.readTileFromCSV(56);
        PlacedTile placedStartTile = new PlacedTile(startTile, null, Rotation.NONE, new Pos(0, 0));

        board = board.withNewTile(placedStartTile);

        assertEquals(board.tileAt(new Pos(0, 0)), placedStartTile);

        Tile tile38 = TileReader.readTileFromCSV(38);

        PlacedTile placedTile38 = new PlacedTile(tile38, null, Rotation.NONE, new Pos(1, 0));
        board = board.withNewTile(placedTile38);
        assertEquals(board.tileAt(new Pos(1, 0)), placedTile38);

        final Board board2 = board;
        assertThrows(IllegalArgumentException.class, () -> board2.withNewTile(new PlacedTile(tile38, null, Rotation.NONE, new Pos(13, 0))));
        assertThrows(IllegalArgumentException.class, () -> board2.withNewTile(new PlacedTile(tile38, null, Rotation.NONE, new Pos(0, 0))));
//      Gérer quand on passe un paramètre null peut être ? Pas forcément demandé en vrai donc je met ici
//        assertThrows(IllegalArgumentException.class, () -> board2.withNewTile(null));
        assertThrows(IllegalArgumentException.class, () -> board2.withNewTile(new PlacedTile(TileReader.readTileFromCSV(13), null, Rotation.NONE, new Pos(2, 0))));
    }

    @Test
    void withOccupantTestEmmanuel() {
        Board board = Board.EMPTY;

        Tile startTile = TileReader.readTileFromCSV(56);
        PlacedTile placedStartTile = new PlacedTile(startTile, null, Rotation.NONE, new Pos(0, 0));
        board = board.withNewTile(placedStartTile);

        Tile tile38 = TileReader.readTileFromCSV(38);

        PlacedTile placedTile38 = new PlacedTile(tile38, PlayerColor.RED, Rotation.NONE, new Pos(1, 0));
        board = board.withNewTile(placedTile38);
        Zone.Forest zoneForest = (Zone.Forest) board.lastPlacedTile().tile().e().zones().getFirst();
        Occupant pawn1 = new Occupant(Occupant.Kind.PAWN, zoneForest.id());
        board = board.withOccupant(pawn1);


        assertEquals(Set.of(pawn1), board.occupants());

        board = board.withNewTile(new PlacedTile(TileReader.readTileFromCSV(2), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0)));
        Zone.River zoneRiver = (Zone.River) board.lastPlacedTile().tile().e().zones().get(1);
        Occupant pawn2 = new Occupant(Occupant.Kind.PAWN, zoneRiver.id());
        board = board.withOccupant(pawn2);

        assertEquals(Set.of(pawn1, pawn2), board.occupants());

        board = board.withoutOccupant(pawn1);
        assertEquals(Set.of(pawn2), board.occupants());

        board = board.withoutOccupant(pawn2);
        assertEquals(Set.of(), board.occupants());

        Occupant pawn3 = new Occupant(Occupant.Kind.PAWN, board.lastPlacedTile().tile().e().zones().getFirst().id());
        board = board.withOccupant(pawn2);
        board = board.withOccupant(pawn1);

        Board board2 = board;

        assertThrows(IllegalArgumentException.class, () -> board2.withOccupant(pawn3));


        board = board.withoutGatherersOrFishersIn(Set.of(board.forestArea(zoneForest)), Set.of(board.riverArea(zoneRiver)));

        assertEquals(Set.of(), board.occupants());

        board = board.withOccupant(pawn1).withOccupant(pawn2);

        board = board.withoutGatherersOrFishersIn(Set.of(board.forestArea(zoneForest)), Set.of());

        assertEquals(Set.of(pawn2), board.occupants());
    }

    @Test
    void withoutOccupantTestEmmanuel() {
        //voir plus haut, mais ça marche très bien
    }

    @Test
    void withoutGatherersOrFishersInTestEmmanuel() {
        //voir plus haut
    }

    @Test
    void withMoreCancelledAnimalsTestEmmanuel() {
        Board board = Board.EMPTY;

        Tile startTile = TileReader.readTileFromCSV(56);
        PlacedTile placedStartTile = new PlacedTile(startTile, null, Rotation.NONE, new Pos(0, 0));
        board = board.withNewTile(placedStartTile);

        Tile tile38 = TileReader.readTileFromCSV(38);
        PlacedTile placedTile38 = new PlacedTile(tile38, PlayerColor.RED, Rotation.NONE, new Pos(1, 0));
        board = board.withNewTile(placedTile38);

        Zone.Meadow meadow1 = (Zone.Meadow) startTile.n().zones().get(0);
        board = board.withMoreCancelledAnimals(Set.of(meadow1.animals().get(0)));

        assertEquals(Set.of(meadow1.animals().get(0)), board.cancelledAnimals());
    }

    //ce test là est aussi bon
    @Test
    void equalsTestEmmanuel() {
        Board board1 = Board.EMPTY;
        Board board2 = Board.EMPTY;

        assertEquals(board1, board2);

        Zone.Forest forest1 = new Zone.Forest(101, Zone.Forest.Kind.PLAIN);
        PlacedTile tile = new PlacedTile(new Tile(56, Tile.Kind.START, new TileSide.Forest(forest1), new TileSide.Forest(forest1), new TileSide.Forest(forest1), new TileSide.Forest(forest1)), PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.PAWN, 101));
        board1 = board1.withNewTile(tile);
        assertNotEquals(board1, board2);

        board2 = board2.withNewTile(tile);
        assertEquals(board1, board2);

        Zone.Forest forest2 = new Zone.Forest(102, Zone.Forest.Kind.PLAIN);
        PlacedTile tile2 = new PlacedTile(new Tile(56, Tile.Kind.NORMAL, new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2)), PlayerColor.RED, Rotation.NONE, new Pos(0, 1), new Occupant(Occupant.Kind.PAWN, 102));
        board1 = board1.withNewTile(tile2);
        assertNotEquals(board1, board2);

        board2 = board2.withNewTile(tile2);
        assertEquals(board1, board2);
    }

    //ce test là est bon
    @Test
    void hashCodeTestEmmanuel() {
        Board board1 = Board.EMPTY;
        Board board2 = Board.EMPTY;

        assertEquals(board1.hashCode(), board2.hashCode());

        Zone.Forest forest1 = new Zone.Forest(101, Zone.Forest.Kind.PLAIN);
        PlacedTile tile = new PlacedTile(new Tile(56, Tile.Kind.START, new TileSide.Forest(forest1), new TileSide.Forest(forest1), new TileSide.Forest(forest1), new TileSide.Forest(forest1)), PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.PAWN, 101));
        board1 = board1.withNewTile(tile);
        assertNotEquals(board1.hashCode(), board2.hashCode());

        board2 = board2.withNewTile(tile);
        assertEquals(board1.hashCode(), board2.hashCode());
//
//        Zone.Forest forest2 = new Zone.Forest(102, Zone.Forest.Kind.PLAIN);
//        PlacedTile tile2 = new PlacedTile(new Tile(56, Tile.Kind.NORMAL, new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2)), PlayerColor.RED, Rotation.NONE, new Pos(0, 1), new Occupant(Occupant.Kind.PAWN, 102));
//        board1 = board1.withNewTile(tile2);
//        assertNotEquals(board1.hashCode(), board2.hashCode());
//
//        board2 = board2.withNewTile(tile2);
//        assertEquals(board1.hashCode(), board2.hashCode());
    }

}

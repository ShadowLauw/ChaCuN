package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTestEmmanuel {
    @Test
    void canAddTileTestEmmanuel () {
        Board board = Board.EMPTY;
        Zone.Forest forest1 = new Zone.Forest(101, Zone.Forest.Kind.PLAIN);
        PlacedTile tile = new PlacedTile(new Tile(56, Tile.Kind.START, new TileSide.Forest(forest1), new TileSide.Forest(forest1), new TileSide.Forest(forest1), new TileSide.Forest(forest1)), PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.PAWN, 101));
        assertTrue(board.canAddTile(tile));
        board = board.withNewTile(tile);

        Zone.Forest forest2 = new Zone.Forest(102, Zone.Forest.Kind.PLAIN);
        PlacedTile tile2 = new PlacedTile(new Tile(56, Tile.Kind.NORMAL, new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2)), PlayerColor.RED, Rotation.NONE, new Pos(0, 1), new Occupant(Occupant.Kind.PAWN, 102));
        assertTrue(board.canAddTile(tile2));
        board = board.withNewTile(tile2);

        Zone.Forest forest3 = new Zone.Forest(103, Zone.Forest.Kind.PLAIN);
        PlacedTile tile3 = new PlacedTile(new Tile(56, Tile.Kind.NORMAL, new TileSide.Forest(forest3), new TileSide.Forest(forest3), new TileSide.Forest(forest3), new TileSide.Forest(forest3)), PlayerColor.RED, Rotation.NONE, new Pos(1, 2), new Occupant(Occupant.Kind.PAWN, 103));
        assertFalse(board.canAddTile(tile3));
        final Board board2 = board;
        assertThrows(IllegalArgumentException.class, () -> board2.withNewTile(tile3));

        Zone.Forest forest4 = new Zone.Forest(104, Zone.Forest.Kind.PLAIN);
        Zone.Meadow meadow1 = new Zone.Meadow(105, List.of(), null);
        PlacedTile tile4 = new PlacedTile(new Tile(56, Tile.Kind.NORMAL, new TileSide.Forest(forest4), new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1)), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0), new Occupant(Occupant.Kind.PAWN, 104));
        assertFalse(board.canAddTile(tile4));


        Zone.Forest forest5 = new Zone.Forest(106, Zone.Forest.Kind.PLAIN);
        Zone.Meadow meadow2 = new Zone.Meadow(107, List.of(), null);
        PlacedTile tile5 = new PlacedTile(new Tile(56, Tile.Kind.NORMAL, new TileSide.Forest(forest5), new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow2)), PlayerColor.RED, Rotation.RIGHT, new Pos(-1, 0), new Occupant(Occupant.Kind.PAWN, 106));
        assertTrue(board.canAddTile(tile5));
        board = board.withNewTile(tile5);
    }

    @Test
    void couldPlaceTileTestEmmanuel() {
        Board board = Board.EMPTY;
        Zone.Forest forest1 = new Zone.Forest(101, Zone.Forest.Kind.PLAIN);
        Tile tile = new Tile(56, Tile.Kind.START, new TileSide.Forest(forest1), new TileSide.Forest(forest1), new TileSide.Forest(forest1), new TileSide.Forest(forest1));
        assertTrue(board.couldPlaceTile(tile));
        board = board.withNewTile(new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.PAWN, 101)));

        Zone.Forest forest2 = new Zone.Forest(102, Zone.Forest.Kind.PLAIN);
        Tile tile2 = new Tile(56, Tile.Kind.NORMAL, new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2));
        assertTrue(board.couldPlaceTile(tile2));
        board = board.withNewTile(new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(0, 1), new Occupant(Occupant.Kind.PAWN, 102)));

        Zone.Forest forest4 = new Zone.Forest(104, Zone.Forest.Kind.PLAIN);
        Zone.Meadow meadow1 = new Zone.Meadow(105, List.of(), null);
        Tile tile4 = new Tile(56, Tile.Kind.NORMAL, new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1));
        assertFalse(board.couldPlaceTile(tile4));

        Zone.Forest forest5 = new Zone.Forest(106, Zone.Forest.Kind.PLAIN);
        Zone.Meadow meadow2 = new Zone.Meadow(107, List.of(), null);
        Tile tile5 = new Tile(56, Tile.Kind.NORMAL, new TileSide.Forest(forest5), new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow2));
        assertTrue(board.couldPlaceTile(tile5));

        //les tests sont pour le moment assez simple et testent les cas plutôt généraux.
    }

    @Test
    void withNewTileTestEmmanuel() {
        Zone.Forest forest1 = new Zone.Forest(101, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(102, Zone.Forest.Kind.PLAIN);
        Zone.Meadow meadow1 = new Zone.Meadow(103, List.of(new Animal(1001, Animal.Kind.DEER)), null);
        Zone.Meadow meadow2 = new Zone.Meadow(104, List.of(new Animal(1002, Animal.Kind.MAMMOTH)), null);

        Area<Zone.Forest> area1 = new Area<>(Set.of(forest1, forest2), List.of(PlayerColor.RED), 2);
        Area<Zone.Meadow> area2 = new Area<>(Set.of(meadow1, meadow2), List.of(PlayerColor.GREEN, PlayerColor.YELLOW), 2);

        ZonePartitions zonePartitions = new ZonePartitions(new ZonePartition<>(Set.of(area1)), new ZonePartition<>(Set.of(area2)), new ZonePartition<>(Set.of()), new ZonePartition<>(Set.of()));

        PlacedTile tile1 = new PlacedTile(new Tile(56, Tile.Kind.START, new TileSide.Forest(forest1), new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1), new TileSide.Forest(forest1)), PlayerColor.RED, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile tile1Bis = new PlacedTile(new Tile(56, Tile.Kind.START, new TileSide.Forest(forest1), new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1), new TileSide.Forest(forest1)), PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.PAWN, 101));
        PlacedTile tile2 = new PlacedTile(new Tile(57, Tile.Kind.NORMAL, new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2)), PlayerColor.BLUE, Rotation.NONE, new Pos(0, 1), new Occupant(Occupant.Kind.PAWN, 102));
        PlacedTile tile3 = new PlacedTile(new Tile(58, Tile.Kind.NORMAL, new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1)), PlayerColor.GREEN, Rotation.NONE, new Pos(1, 0), new Occupant(Occupant.Kind.PAWN, 103));
        PlacedTile tile4 = new PlacedTile(new Tile(59, Tile.Kind.NORMAL, new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow2)), PlayerColor.YELLOW, Rotation.NONE, new Pos(2, 0), new Occupant(Occupant.Kind.PAWN, 104));

        Board board = Board.EMPTY;
        board = board.withNewTile(tile1);
        board = board.withNewTile(tile2);
        board = board.withNewTile(tile3);
        board = board.withNewTile(tile4);

        PlacedTile[] placedTiles = new PlacedTile[625];
        placedTiles[312] = tile1;
        placedTiles[287] = tile2;
        placedTiles[313] = tile3;
        placedTiles[314] = tile4;
        Board board2 = new Board(placedTiles, new int[0], zonePartitions, Set.of());

        assertEquals(board, board2);

        board = board.withMoreCancelledAnimals(Set.of(new Animal(1001, Animal.Kind.DEER)));
        board2 = new Board(placedTiles, new int[0], zonePartitions, Set.of(new Animal(1001, Animal.Kind.DEER)));

        assertEquals(board, board2);

        Zone.River river1 = new Zone.River(105, 0, null);
        Zone.River river2 = new Zone.River(106, 0, null);
        Area<Zone.River> area3 = new Area<>(Set.of(river1), List.of(PlayerColor.RED), 2);
        Area<Zone.River> area4 = new Area<>(Set.of(river2), List.of(), 2);
        ZonePartitions zonePartitions2 = new ZonePartitions(new ZonePartition<>(Set.of(area1)), new ZonePartition<>(Set.of(area2)), new ZonePartition<>(Set.of(area3, area4)), new ZonePartition<>(Set.of()));

        PlacedTile tile5 = new PlacedTile(new Tile(60, Tile.Kind.NORMAL, new TileSide.River(meadow1, river1, meadow2), new TileSide.River(meadow1, river2, meadow2), new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow1)), PlayerColor.RED, Rotation.NONE, new Pos(3, 0), new Occupant(Occupant.Kind.PAWN, 105));



        //test de withOccupant et withoutOccupant
        Board board4 = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 101));

        Area<Zone.Forest> area1bis = new Area<>(Set.of(forest1, forest2), List.of(PlayerColor.RED, PlayerColor.RED), 2);
        Board board4Valid = Board.EMPTY;
        board4Valid = board4Valid.withNewTile(tile1Bis);
        board4Valid = board4Valid.withNewTile(tile2);
        board4Valid = board4Valid.withNewTile(tile3);
        board4Valid = board4Valid.withNewTile(tile4);

        assertEquals(board4, board4Valid);
        board4 = board.withoutOccupant(new Occupant(Occupant.Kind.PAWN, 102));
        assertEquals(board, board4);
        board = board.withoutGatherersOrFishersIn(Set.of(area1), Set.of(area3, area4));

        PlacedTile tile2Bis = new PlacedTile(new Tile(57, Tile.Kind.NORMAL, new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2)), PlayerColor.BLUE, Rotation.NONE, new Pos(0, 1), null);
        PlacedTile tile3Bis = new PlacedTile(new Tile(58, Tile.Kind.NORMAL, new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1), new TileSide.Meadow(meadow1)), PlayerColor.GREEN, Rotation.NONE, new Pos(1, 0), null);
        PlacedTile tile4Bis = new PlacedTile(new Tile(59, Tile.Kind.NORMAL, new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow2), new TileSide.Meadow(meadow2)), PlayerColor.YELLOW, Rotation.NONE, new Pos(2, 0), null);

        Board Board5 = Board.EMPTY;
        Board5 = Board5.withNewTile(tile1Bis);
        Board5 = Board5.withNewTile(tile2Bis);
        Board5 = Board5.withNewTile(tile3Bis);
        Board5 = Board5.withNewTile(tile4Bis);

        final Board finalBoard = Board5;

        assertEquals(board, Board5);
        assertThrows(IllegalArgumentException.class, () -> finalBoard.withoutOccupant(new Occupant(Occupant.Kind.PAWN, 101)));


        final Board finalBoard2 = Board5.withOccupant(new Occupant(Occupant.Kind.PAWN, 101));
        assertThrows(IllegalArgumentException.class, () -> finalBoard2.withOccupant(new Occupant(Occupant.Kind.PAWN, 101)));

        //en vrai possibilité que ça merde mais c tellement la galère à tester cette merde c'est just infâme.
    }

    @Test
    void withOccupantTestEmmanuel() {
        //voir plus haut
    }

    @Test
    void withoutOccupantTestEmmanuel() {
        //voir plus h
    }

    @Test
    void withoutGatherersOrFishersInTestEmmanuel() {
        //voir plus haut
    }

    @Test
    void withMoreCancelledAnimalsTestEmmanuel() {
        //voir plus haut
    }

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

        Zone.Forest forest2 = new Zone.Forest(102, Zone.Forest.Kind.PLAIN);
        PlacedTile tile2 = new PlacedTile(new Tile(56, Tile.Kind.NORMAL, new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2), new TileSide.Forest(forest2)), PlayerColor.RED, Rotation.NONE, new Pos(0, 1), new Occupant(Occupant.Kind.PAWN, 102));
        board1 = board1.withNewTile(tile2);
        assertNotEquals(board1.hashCode(), board2.hashCode());

        board2 = board2.withNewTile(tile2);
        assertEquals(board1.hashCode(), board2.hashCode());
    }

}

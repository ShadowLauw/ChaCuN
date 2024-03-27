package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class EmmanuelLauraGameStateTest {
    Board fullBoard() {
        PlacedTile startTile = new PlacedTile(TileReader.readTileFromCSV(56), PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        Board board = Board.EMPTY.withNewTile(startTile);
        PlacedTile tile38 = new PlacedTile(TileReader.readTileFromCSV(38), PlayerColor.BLUE, Rotation.NONE, new Pos(1, 0));
        board = board.withNewTile(tile38);
        PlacedTile tile41 = new PlacedTile(TileReader.readTileFromCSV(41), PlayerColor.GREEN, Rotation.NONE, new Pos(2, 0));
        board = board.withNewTile(tile41);
        PlacedTile tile35 = new PlacedTile(TileReader.readTileFromCSV(35), PlayerColor.YELLOW, Rotation.HALF_TURN, new Pos(3, 0));
        board = board.withNewTile(tile35);
        PlacedTile tile67 = new PlacedTile(TileReader.readTileFromCSV(67), PlayerColor.PURPLE, Rotation.NONE, new Pos(0, 1));
        board = board.withNewTile(tile67);
        PlacedTile tile51 = new PlacedTile(TileReader.readTileFromCSV(51), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        board = board.withNewTile(tile51);
        PlacedTile tile1 = new PlacedTile(TileReader.readTileFromCSV(1), PlayerColor.BLUE, Rotation.NONE, new Pos(-2, 0));

        PlacedTile tile16 = new PlacedTile(TileReader.readTileFromCSV(16), PlayerColor.GREEN, Rotation.RIGHT, new Pos(-1, 1));
        board = board.withNewTile(tile16);
        PlacedTile tile22 = new PlacedTile(TileReader.readTileFromCSV(22), PlayerColor.BLUE, Rotation.HALF_TURN, new Pos(-2, 1));
        board = board.withNewTile(tile22);
        PlacedTile tile24 = new PlacedTile(TileReader.readTileFromCSV(24), PlayerColor.PURPLE, Rotation.NONE, new Pos(-3, 1));
        board = board.withNewTile(tile24);
        PlacedTile tile17 = new PlacedTile(TileReader.readTileFromCSV(17), PlayerColor.GREEN, Rotation.NONE, new Pos(-3, 0));
        board = board.withNewTile(tile17);
        PlacedTile tile21 = new PlacedTile(TileReader.readTileFromCSV(21), PlayerColor.YELLOW, Rotation.NONE, new Pos(-3, -1));
        board = board.withNewTile(tile21);
        PlacedTile tile37 = new PlacedTile(TileReader.readTileFromCSV(37), PlayerColor.PURPLE, Rotation.NONE, new Pos(0, -1));
        board = board.withNewTile(tile37);
        PlacedTile tile25 = new PlacedTile(TileReader.readTileFromCSV(25), PlayerColor.RED, Rotation.NONE, new Pos(0, -2));
        board = board.withNewTile(tile25);
        board = board.withNewTile(tile1);

        Zone.Meadow meadow560 = (Zone.Meadow) startTile.zoneWithId(560);
        Zone.Meadow meadow562 = (Zone.Meadow) startTile.zoneWithId(562);

        Zone.Meadow meadow380 = (Zone.Meadow) tile38.zoneWithId(380);
        Zone.Meadow meadow382 = (Zone.Meadow) tile38.zoneWithId(382);
        Zone.Meadow meadow410 = (Zone.Meadow) tile41.zoneWithId(410);
        Zone.Meadow meadow350 = (Zone.Meadow) tile35.zoneWithId(350);
        Zone.Meadow meadow672 = (Zone.Meadow) tile67.zoneWithId(672);
        Zone.Meadow meadow510 = (Zone.Meadow) tile51.zoneWithId(510);
        Zone.Meadow meadow512 = (Zone.Meadow) tile51.zoneWithId(512);
        Zone.Meadow meadow10 = (Zone.Meadow) tile1.zoneWithId(10);
        Zone.Meadow meadow12 = (Zone.Meadow) tile1.zoneWithId(12);
        Zone.Meadow meadow14 = (Zone.Meadow) tile1.zoneWithId(14);
        Zone.Meadow meadow170 = (Zone.Meadow) tile17.zoneWithId(170);
        Zone.Meadow meadow172 = (Zone.Meadow) tile17.zoneWithId(172);
        Zone.Meadow meadow174 = (Zone.Meadow) tile17.zoneWithId(174);
        Zone.Meadow meadow210 = (Zone.Meadow) tile21.zoneWithId(210);
        Zone.Meadow meadow212 = (Zone.Meadow) tile21.zoneWithId(212);
        Zone.Meadow meadow371 = (Zone.Meadow) tile37.zoneWithId(371);
        Zone.Meadow meadow250 = (Zone.Meadow) tile25.zoneWithId(250);
        Zone.Meadow meadow252 = (Zone.Meadow) tile25.zoneWithId(252);
        Zone.Meadow meadow220 = (Zone.Meadow) tile22.zoneWithId(220);
        Zone.Meadow meadow222 = (Zone.Meadow) tile22.zoneWithId(222);

        return board;
    }
    @Test
    void ConstructeurThrows () {
        Board board = Board.EMPTY;

        Tile startTile = TileReader.readTileFromCSV(56);
        Tile tile38 = TileReader.readTileFromCSV(38);
        Tile tile23 = TileReader.readTileFromCSV(23);
        board = board.withNewTile(new PlacedTile(startTile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0)));
        board = board.withNewTile(new PlacedTile(tile38, null, Rotation.NONE, new Pos(1, 0)));
        board = board.withNewTile(new PlacedTile(tile23, null, Rotation.NONE, new Pos(-1, 0)));
        final Board board2 = board;
        TileDecks tileDecks = new TileDecks(List.of(startTile), List.of(tile23, tile38), List.of());

        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.PURPLE);
        MessageBoard messageBoard = new MessageBoard(new LauraTextMakerClass(), List.of());

        //à tester :

        //on peut pas lancer avec moins de deux joueurs
        assertThrows(IllegalArgumentException.class, () -> {
            new GameState(
                    List.of(PlayerColor.YELLOW),
                    tileDecks,
                    startTile,
                    board2,
                    GameState.Action.OCCUPY_TILE,
                    messageBoard
            );
        });

        //on peut pas ne pas avoir de tile à placer quand il faut en avoir une
        assertThrows(IllegalArgumentException.class, () -> {
            new GameState(
                    players,
                    tileDecks,
                    null,
                    board2,
                    GameState.Action.PLACE_TILE,
                    messageBoard
            );
        });

        //on ne peut pas avoir de tuile à poser dans startgame
        assertThrows(IllegalArgumentException.class, () -> {
            new GameState(
                    players,
                    tileDecks,
                    startTile,
                    board2,
                    GameState.Action.START_GAME,
                    messageBoard
            );
        });

        //on ne peut pas avoir tileDecks null
        assertThrows(IllegalArgumentException.class, () -> {
            new GameState(
                    players,
                    null,
                    startTile,
                    board2,
                    GameState.Action.OCCUPY_TILE,
                    messageBoard
            );
        });

        //on ne peut pas avoir board null
        assertThrows(IllegalArgumentException.class, () -> {
            new GameState(
                    players,
                    tileDecks,
                    startTile,
                    null,
                    GameState.Action.OCCUPY_TILE,
                    messageBoard
            );
        });

        //on ne peut pas avoir nextAction null
        assertThrows(IllegalArgumentException.class, () -> {
            new GameState(
                    players,
                    tileDecks,
                    startTile,
                    board2,
                    null,
                    messageBoard
            );
        });

        //on ne ptu pas avoir messageBoard null
        assertThrows(IllegalArgumentException.class, () -> {
            new GameState(
                    players,
                    tileDecks,
                    startTile,
                    board2,
                    GameState.Action.OCCUPY_TILE,
                    null
            );
        });
    }
    @Test
    void constructeurImmuable () {
        //vérifier que les listes passés en paramètre ne sont pas modifiables
        List<PlayerColor> players = new ArrayList<>(List.of(PlayerColor.RED, PlayerColor.BLUE));
        TileDecks tileDecks = new TileDecks(List.of(), List.of(), List.of());
        GameState gameState = GameState.initial(players, tileDecks, new LauraTextMakerClass());
        players.add(PlayerColor.YELLOW);
        assertNotEquals(players, gameState.players());
    }
    @Test
    void gameStateInitialTest () {
        //tuile à placer nulle, action start game, plateau vide, tableau d'affichage soit vide

        Tile startTile = TileReader.readTileFromCSV(56);
        Tile tile38 = TileReader.readTileFromCSV(38);
        Tile tile23 = TileReader.readTileFromCSV(23);
        TileDecks tileDecks = new TileDecks(List.of(startTile), List.of(tile23, tile38), List.of());
        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.PURPLE);

        GameState gameState =  GameState.initial(players, tileDecks, new LauraTextMakerClass());
        assertEquals(GameState.Action.START_GAME, gameState.nextAction());
        assertEquals(Board.EMPTY, gameState.board());
        assertEquals(0, gameState.messageBoard().messages().size());
        assertEquals(tileDecks, gameState.tileDecks());
        assertNull(gameState.tileToPlace());
    }

    @Test
    void currentPlayerTest () {
        // retourner le bon joueur courant, où null si la prochaine action est start Game ou end Game
        List<PlayerColor> players = new ArrayList<>(List.of(PlayerColor.BLUE, PlayerColor.YELLOW));
        TileDecks tileDecks = new TileDecks(List.of(TileReader.readTileFromCSV(56)), List.of(TileReader.readTileFromCSV(32)), List.of());
        MessageBoard messageBoard = new MessageBoard(new LauraTextMakerClass(), List.of());

        GameState gameState = GameState.initial(players, tileDecks, new LauraTextMakerClass());
        assertNull(gameState.currentPlayer());
        GameState gameState2 = new GameState(players, tileDecks, null, Board.EMPTY, GameState.Action.END_GAME, messageBoard);
        assertNull(gameState2.currentPlayer());

        gameState = gameState.withStartingTilePlaced();
        assertEquals(PlayerColor.BLUE, gameState.currentPlayer());
    }

    @Test
    void freeOccupantsCountTest () {
        Board board = fullBoard();
        TileDecks tileDecks = new TileDecks(List.of(), List.of(), List.of());
        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.PURPLE);
        MessageBoard messageBoard = new MessageBoard(new LauraTextMakerClass(), List.of());

        GameState gameState = new GameState(players, tileDecks, null, board, GameState.Action.START_GAME, messageBoard);
        assertEquals(5, gameState.freeOccupantsCount(PlayerColor.RED, Occupant.Kind.PAWN));
        board = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 510));
        board = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 251));


        gameState = new GameState(players, tileDecks, null, board, GameState.Action.START_GAME, messageBoard);
        assertEquals(3, gameState.freeOccupantsCount(PlayerColor.RED, Occupant.Kind.PAWN));
        assertEquals(3, gameState.freeOccupantsCount(PlayerColor.RED, Occupant.Kind.HUT));

        board = board.withOccupant(new Occupant(Occupant.Kind.HUT, 171));
        gameState = new GameState(players, tileDecks, null, board, GameState.Action.START_GAME, messageBoard);
        assertEquals(2, gameState.freeOccupantsCount(PlayerColor.GREEN, Occupant.Kind.HUT));
    }
    @Test
    void lastTilePotentialOccupant () {
        //retourne le potentiel occupant de la prochaine tuile, Attention zones incluses et nombre la main incluses aussi
        Board board = fullBoard();
        TileDecks tileDecks = new TileDecks(List.of(), List.of(), List.of());
        List<PlayerColor> players = List.of(PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.PURPLE);
        MessageBoard messageBoard = new MessageBoard(new LauraTextMakerClass(), List.of());
        GameState gameState = new GameState(players, tileDecks, null, board, GameState.Action.OCCUPY_TILE, messageBoard);
        Set<Occupant> occupantsPotentiels = new HashSet<>();
        occupantsPotentiels.add(new Occupant(Occupant.Kind.PAWN, 10));
        occupantsPotentiels.add(new Occupant(Occupant.Kind.PAWN, 12));
        occupantsPotentiels.add(new Occupant(Occupant.Kind.PAWN, 13));
        occupantsPotentiels.add(new Occupant(Occupant.Kind.PAWN, 14));
        occupantsPotentiels.add(new Occupant(Occupant.Kind.PAWN, 11));
        occupantsPotentiels.add(new Occupant(Occupant.Kind.PAWN, 15));
        occupantsPotentiels.add(new Occupant(Occupant.Kind.HUT, 18));
        assertEquals(occupantsPotentiels, gameState.lastTilePotentialOccupants());
        board = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 510));
        board = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 223));
        board = board.withOccupant(new Occupant(Occupant.Kind.HUT, 211));
        occupantsPotentiels.remove(new Occupant(Occupant.Kind.HUT, 18));
        occupantsPotentiels.remove(new Occupant(Occupant.Kind.PAWN, 13));
        occupantsPotentiels.remove(new Occupant(Occupant.Kind.PAWN, 10));
        gameState = new GameState(players, tileDecks, null, board, GameState.Action.OCCUPY_TILE, messageBoard);
        assertEquals(occupantsPotentiels, gameState.lastTilePotentialOccupants());
        board = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 563));
        occupantsPotentiels.remove(new Occupant(Occupant.Kind.PAWN, 11));
        gameState = new GameState(players, tileDecks, null, board, GameState.Action.OCCUPY_TILE, messageBoard);
        assertEquals(occupantsPotentiels, gameState.lastTilePotentialOccupants());

        //Sans la main PAWN
        PlacedTile startTile = new PlacedTile(TileReader.readTileFromCSV(56), PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        board = Board.EMPTY.withNewTile(startTile);
        PlacedTile tile38 = new PlacedTile(TileReader.readTileFromCSV(38), PlayerColor.BLUE, Rotation.NONE, new Pos(1, 0));
        board = board.withNewTile(tile38);
        PlacedTile tile41 = new PlacedTile(TileReader.readTileFromCSV(41), PlayerColor.BLUE, Rotation.NONE, new Pos(2, 0));
        board = board.withNewTile(tile41);
        PlacedTile tile35 = new PlacedTile(TileReader.readTileFromCSV(35), PlayerColor.BLUE, Rotation.HALF_TURN, new Pos(3, 0));
        board = board.withNewTile(tile35);
        PlacedTile tile67 = new PlacedTile(TileReader.readTileFromCSV(67), PlayerColor.PURPLE, Rotation.NONE, new Pos(0, 1));
        board = board.withNewTile(tile67);
        PlacedTile tile51 = new PlacedTile(TileReader.readTileFromCSV(51), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        board = board.withNewTile(tile51);
        PlacedTile tile1 = new PlacedTile(TileReader.readTileFromCSV(1), PlayerColor.BLUE, Rotation.NONE, new Pos(-2, 0));

        PlacedTile tile16 = new PlacedTile(TileReader.readTileFromCSV(16), PlayerColor.GREEN, Rotation.RIGHT, new Pos(-1, 1));
        board = board.withNewTile(tile16);
        PlacedTile tile22 = new PlacedTile(TileReader.readTileFromCSV(22), PlayerColor.BLUE, Rotation.HALF_TURN, new Pos(-2, 1));
        board = board.withNewTile(tile22);
        PlacedTile tile24 = new PlacedTile(TileReader.readTileFromCSV(24), PlayerColor.PURPLE, Rotation.NONE, new Pos(-3, 1));
        board = board.withNewTile(tile24);
        PlacedTile tile17 = new PlacedTile(TileReader.readTileFromCSV(17), PlayerColor.GREEN, Rotation.NONE, new Pos(-3, 0));
        board = board.withNewTile(tile17);
        PlacedTile tile21 = new PlacedTile(TileReader.readTileFromCSV(21), PlayerColor.YELLOW, Rotation.NONE, new Pos(-3, -1));
        board = board.withNewTile(tile21);
        PlacedTile tile37 = new PlacedTile(TileReader.readTileFromCSV(37), PlayerColor.BLUE, Rotation.NONE, new Pos(0, -1));
        board = board.withNewTile(tile37);
        PlacedTile tile25 = new PlacedTile(TileReader.readTileFromCSV(25), PlayerColor.BLUE, Rotation.NONE, new Pos(0, -2));
        board = board.withNewTile(tile25);
        board = board.withNewTile(tile1);
        board = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 410));
        board = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 351));
        board = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 380));
        board = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 250));
        board = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 370));
        occupantsPotentiels.clear();
        occupantsPotentiels.add(new Occupant(Occupant.Kind.HUT, 18));
        gameState = new GameState(players, tileDecks, null, board, GameState.Action.OCCUPY_TILE, messageBoard);
        assertEquals(occupantsPotentiels, gameState.lastTilePotentialOccupants());

        //Sans la main hutte
        startTile = new PlacedTile(TileReader.readTileFromCSV(56), PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        board = Board.EMPTY.withNewTile(startTile);
        tile38 = new PlacedTile(TileReader.readTileFromCSV(38), PlayerColor.BLUE, Rotation.NONE, new Pos(1, 0));
        board = board.withNewTile(tile38);
        tile41 = new PlacedTile(TileReader.readTileFromCSV(41), PlayerColor.GREEN, Rotation.NONE, new Pos(2, 0));
        board = board.withNewTile(tile41);
        tile35 = new PlacedTile(TileReader.readTileFromCSV(35), PlayerColor.YELLOW, Rotation.HALF_TURN, new Pos(3, 0));
        board = board.withNewTile(tile35);
        tile67 = new PlacedTile(TileReader.readTileFromCSV(67), PlayerColor.PURPLE, Rotation.NONE, new Pos(0, 1));
        board = board.withNewTile(tile67);
        tile51 = new PlacedTile(TileReader.readTileFromCSV(51), PlayerColor.RED, Rotation.NONE, new Pos(-1, 0));
        board = board.withNewTile(tile51);
        tile1 = new PlacedTile(TileReader.readTileFromCSV(1), PlayerColor.BLUE, Rotation.NONE, new Pos(-2, 0));

        tile16 = new PlacedTile(TileReader.readTileFromCSV(16), PlayerColor.BLUE, Rotation.RIGHT, new Pos(-1, 1));
        board = board.withNewTile(tile16);
        tile22 = new PlacedTile(TileReader.readTileFromCSV(22), PlayerColor.BLUE, Rotation.HALF_TURN, new Pos(-2, 1));
        board = board.withNewTile(tile22);
        tile24 = new PlacedTile(TileReader.readTileFromCSV(24), PlayerColor.BLUE, Rotation.NONE, new Pos(-3, 1));
        board = board.withNewTile(tile24);
        tile17 = new PlacedTile(TileReader.readTileFromCSV(17), PlayerColor.GREEN, Rotation.NONE, new Pos(-3, 0));
        board = board.withNewTile(tile17);
        tile21 = new PlacedTile(TileReader.readTileFromCSV(21), PlayerColor.YELLOW, Rotation.NONE, new Pos(-3, -1));
        board = board.withNewTile(tile21);
        tile37 = new PlacedTile(TileReader.readTileFromCSV(37), PlayerColor.PURPLE, Rotation.NONE, new Pos(0, -1));
        board = board.withNewTile(tile37);
        tile25 = new PlacedTile(TileReader.readTileFromCSV(25), PlayerColor.BLUE, Rotation.NONE, new Pos(0, -2));
        board = board.withNewTile(tile25);
        board = board.withNewTile(tile1);

        board = board.withOccupant(new Occupant(Occupant.Kind.HUT, 251));
        board = board.withOccupant(new Occupant(Occupant.Kind.HUT, 248));
        board = board.withOccupant(new Occupant(Occupant.Kind.HUT, 161));
        occupantsPotentiels.clear();
        occupantsPotentiels.add(new Occupant(Occupant.Kind.PAWN, 10));
        occupantsPotentiels.add(new Occupant(Occupant.Kind.PAWN, 12));
        occupantsPotentiels.add(new Occupant(Occupant.Kind.PAWN, 13));
        occupantsPotentiels.add(new Occupant(Occupant.Kind.PAWN, 14));
        occupantsPotentiels.add(new Occupant(Occupant.Kind.PAWN, 11));
        occupantsPotentiels.add(new Occupant(Occupant.Kind.PAWN, 15));


        gameState = new GameState(players, tileDecks, null, board, GameState.Action.OCCUPY_TILE, messageBoard);
        assertEquals(occupantsPotentiels, gameState.lastTilePotentialOccupants());
    }
    @Test
    void lastTilePotentialOccupantThrows () {
        //si le plateau est vide il aime pas
        List<PlayerColor> players = new ArrayList<>(List.of(PlayerColor.BLUE, PlayerColor.YELLOW));
        TileDecks tileDecks = new TileDecks(List.of(), List.of(), List.of());
        GameState gameState = GameState.initial(players, tileDecks, new LauraTextMakerClass());
        assertThrows(IllegalArgumentException.class, gameState::lastTilePotentialOccupants);
        gameState = gameState.withStartingTilePlaced();
        assertDoesNotThrow(gameState::lastTilePotentialOccupants);
    }

    @Test
    void startingTiledPlacedThrows () {
        //si la prochaine action n'est pas startgame.
        Board board = Board.EMPTY;
        Tile startTile = TileReader.readTileFromCSV(56);
        TileDecks tileDecks = new TileDecks(List.of(), List.of(), List.of());
        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.PURPLE);
        MessageBoard messageBoard = new MessageBoard(new LauraTextMakerClass(), List.of());

        GameState gameState = new GameState(players, tileDecks, startTile, board, GameState.Action.OCCUPY_TILE, messageBoard);

        assertThrows(IllegalArgumentException.class, gameState::withStartingTilePlaced);
    }

    @Test
    void startingTiledPlacedWorks () {
        //faut vérifier que la tuile de départ est au centre du plateau, que le tileDeck start est vide, que le tile deck normal a une carte en moins, et que la tiledtoplace est la premère carte du deck normal
        //faut que l'action soit placedTile
        Board board = Board.EMPTY;
        Tile startTile = TileReader.readTileFromCSV(56);
        Tile tile38 = TileReader.readTileFromCSV(38);
        Tile tile29 = TileReader.readTileFromCSV(29);
        TileDecks tileDecks = new TileDecks(List.of(startTile), List.of(tile38, tile29), List.of());
        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.PURPLE);
        MessageBoard messageBoard = new MessageBoard(new LauraTextMakerClass(), List.of());

        GameState gameState = new GameState(players, tileDecks, startTile, board, GameState.Action.START_GAME, messageBoard);
        gameState = gameState.withStartingTilePlaced();
        board = board.withNewTile(new PlacedTile(startTile, null, Rotation.NONE, new Pos(0, 0), null));
        assertEquals(GameState.Action.PLACE_TILE, gameState.nextAction());
        //assertEquals(gameState.tile(new Position(0, 0)), startTile);
        assertTrue(gameState.tileDecks().startTiles().isEmpty());
        assertEquals(1, gameState.tileDecks().deckSize(Tile.Kind.NORMAL));
        assertEquals(board, gameState.board());
        assertEquals(tile38, gameState.tileToPlace());
    }

    @Test
    void withPlacedTileThrows () {
        //prochaine action pas placed tile, et tuile passée déjà occupée

        Board board = fullBoard();
        TileDecks tileDecks = new TileDecks(List.of(), List.of(), List.of());
        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.PURPLE);
        MessageBoard messageBoard = new MessageBoard(new LauraTextMakerClass(), List.of());
        Tile tile19 = TileReader.readTileFromCSV(19);
        final GameState gameState = new GameState(players, tileDecks, tile19, board, GameState.Action.OCCUPY_TILE, messageBoard);
        assertThrows(IllegalArgumentException.class, () -> gameState.withPlacedTile(new PlacedTile(tile19, PlayerColor.RED, Rotation.NONE, new Pos(-2, 0), null)));

        GameState gameState2 = new GameState(players, tileDecks, tile19, board, GameState.Action.PLACE_TILE, messageBoard);
        assertThrows(IllegalArgumentException.class, () -> gameState2.withPlacedTile(new PlacedTile(tile19, PlayerColor.RED, Rotation.NONE, new Pos(-2, 0), new Occupant(Occupant.Kind.PAWN, 191))));
    }

    @Test
    void withPlacedTileWorks () {
        //la tuile doit être bien placée sur le plateau,
        //il doit y avoir les différents super-pvr dans le message board, (>4 différents)
        //faut vérifier que ce soit les bons animaux annulé dans le pré
        //que la prochaine action soit occupytile ou remove pawn (deux cas : soit on peut remove, soit c de nouveau occupytile quand opp remove, soit on peut même pas occuper la tuile du chaman et c fin de tour merci au-revoir)

        //plein d'autres trucs (tester tt les withTurnFinish a gogo)

        //BON BAH ICI C LA MERDE

        //Test du placement d'une tuile générique :

        Board board = fullBoard();

        Tile shamanTile = TileReader.readTileFromCSV(88);
        Tile pitTrapTile = TileReader.readTileFromCSV(94);
        Tile loagBoatTile = TileReader.readTileFromCSV(93);

        TileDecks tileDecks = new TileDecks(List.of(), List.of(), List.of());
        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.PURPLE);
        MessageBoard messageBoard = new MessageBoard(new LauraTextMakerClass(), List.of());
        Tile tile19 = TileReader.readTileFromCSV(18);
        GameState gameState = new GameState(players, tileDecks, tile19, board, GameState.Action.PLACE_TILE, messageBoard);

        gameState = gameState.withPlacedTile(new PlacedTile(tile19, PlayerColor.RED, Rotation.NONE, new Pos(0, 2), null));
        assertEquals(GameState.Action.OCCUPY_TILE, gameState.nextAction());
        assertEquals(board.withNewTile(new PlacedTile(tile19, PlayerColor.RED, Rotation.NONE, new Pos(0, 2), null)), gameState.board());
        assertEquals(messageBoard, gameState.messageBoard());

        gameState = new GameState(players, tileDecks, shamanTile, board, GameState.Action.PLACE_TILE, messageBoard);

        gameState = gameState.withPlacedTile(new PlacedTile(shamanTile, PlayerColor.RED, Rotation.RIGHT, new Pos(0, 2), null));

        //ça switch sur OCCUPY_TILE vu que il a pas de pion le pauvre
        assertEquals(GameState.Action.OCCUPY_TILE, gameState.nextAction());
        assertEquals(board.withNewTile(new PlacedTile(shamanTile, PlayerColor.RED, Rotation.RIGHT, new Pos(0, 2), null)), gameState.board());

        gameState = new GameState(players, tileDecks, pitTrapTile, board, GameState.Action.PLACE_TILE, messageBoard);

        gameState = gameState.withPlacedTile(new PlacedTile(pitTrapTile, PlayerColor.RED, Rotation.RIGHT, new Pos(0, 2), null));

        assertEquals(GameState.Action.OCCUPY_TILE, gameState.nextAction());

        //tout ça juste pour récupérer l'aire avec le champ mdr
        Area<Zone.Meadow> zones = gameState.board().adjacentMeadow(new Pos(0, 2), (Zone.Meadow) pitTrapTile.s().zones().getFirst());
        assertEquals(messageBoard.withScoredHuntingTrap(PlayerColor.RED, zones), gameState.messageBoard());

        gameState = new GameState(players, tileDecks, loagBoatTile, board, GameState.Action.PLACE_TILE, messageBoard);

        gameState = gameState.withPlacedTile(new PlacedTile(loagBoatTile, PlayerColor.RED, Rotation.RIGHT, new Pos(-1, 2), null));

        assertEquals(GameState.Action.OCCUPY_TILE, gameState.nextAction());
        assertEquals(board.withNewTile(new PlacedTile(loagBoatTile, PlayerColor.RED, Rotation.RIGHT, new Pos(-1, 2), null)), gameState.board());

        final int zoneId2 = loagBoatTile.n().zones().get(1).id();
        List<Area<Zone.Water>> filteredWarerAreas = gameState.board().riverSystemAreas().stream()
                .filter(area -> area.zones().stream().anyMatch(zone -> zone.id() == zoneId2))
                .collect(Collectors.toList());

        assertEquals(messageBoard.withScoredLogboat(PlayerColor.RED, filteredWarerAreas.getFirst()), gameState.messageBoard());

        //le moment où le joueur peut pas poser de pions (le plus simple c de prendre le cas où il a déjà posé ses 5 pions)

        board = fullBoard();
        board = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 511));
        gameState = new GameState(players, tileDecks, shamanTile, board, GameState.Action.PLACE_TILE, messageBoard);

        gameState = gameState.withPlacedTile(new PlacedTile(shamanTile, PlayerColor.RED, Rotation.RIGHT, new Pos(0, 2), null));

        assertEquals(GameState.Action.RETAKE_PAWN, gameState.nextAction());
    }

    @Test
    void withTurnFinishedWorks() {

    }

    @Test
    void withOccupantRemovedThrows () {
        //IAE si prochaine action n'est pas retake pawn
        //ou si l'occupant donné n'est ni null ni un pion.

        Board board = fullBoard();
        Tile startTile = TileReader.readTileFromCSV(56);
        TileDecks tileDecks = new TileDecks(List.of(), List.of(), List.of());
        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.PURPLE);
        MessageBoard messageBoard = new MessageBoard(new LauraTextMakerClass(), List.of());

        GameState gameState = new GameState(players, tileDecks, null, board, GameState.Action.OCCUPY_TILE, messageBoard);

        assertThrows(IllegalArgumentException.class, () -> gameState.withOccupantRemoved(new Occupant(Occupant.Kind.PAWN, 560)));

        GameState gameState2 = new GameState(players, tileDecks, null, board, GameState.Action.RETAKE_PAWN, messageBoard);
        assertThrows(IllegalArgumentException.class, () -> gameState2.withOccupantRemoved(new Occupant(Occupant.Kind.HUT, 560)));
    }

    @Test
    void withOccupantRemovedWorks () {
        //vérifier que l'occupant est bien supprimé, ou pas supprimé si le joueur le veut pas.
    }

    @Test
    void withNewOccupantThrows () {
        //throws si la prochaine action n'est pas occupytile

        Board board = fullBoard();
        Tile startTile = TileReader.readTileFromCSV(19);
        TileDecks tileDecks = new TileDecks(List.of(), List.of(), List.of());
        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.PURPLE);
        MessageBoard messageBoard = new MessageBoard(new LauraTextMakerClass(), List.of());

        GameState gameState = new GameState(players, tileDecks, startTile, board, GameState.Action.PLACE_TILE, messageBoard);

        assertThrows(IllegalArgumentException.class, () -> gameState.withNewOccupant(new Occupant(Occupant.Kind.PAWN, 560)));
    }

    @Test
    void withNewOccupantWorks () {
        // vérifier si il a bien ajouté l'occupant à la dernière tuile
        //vérifier qu'il fait rien quand ça vaut null
    }

    @Test
    void finDuJeuWorks () {
        //vérifier que la fin du jeu se comporte correctement.
    }
}

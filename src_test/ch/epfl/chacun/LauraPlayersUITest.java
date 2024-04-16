package ch.epfl.chacun;

import ch.epfl.chacun.gui.PlayersUI;
import ch.epfl.chacun.tile.Tiles;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LauraPlayersUITest extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        var playerNames = Map.of(PlayerColor.YELLOW, "Rose",
                PlayerColor.GREEN, "Bernard");
        var playerColors = playerNames.keySet().stream()
                .sorted()
                .toList();

        var tilesByKind = Tiles.TILES.stream()
                .collect(Collectors.groupingBy(Tile::kind));
        var tileDecks =
                new TileDecks(tilesByKind.get(Tile.Kind.START),
                        tilesByKind.get(Tile.Kind.NORMAL),
                        tilesByKind.get(Tile.Kind.MENHIR));

        var textMaker = new TextMakerFr(playerNames);

        PlacedTile startTile = new PlacedTile(Tiles.TILES.get(56), PlayerColor.YELLOW, Rotation.NONE, new Pos(0, 0));
        Board board = Board.EMPTY.withNewTile(startTile);
        PlacedTile tile38 = new PlacedTile(Tiles.TILES.get(38), PlayerColor.YELLOW, Rotation.NONE, new Pos(1, 0));
        board = board.withNewTile(tile38);
        PlacedTile tile41 = new PlacedTile(Tiles.TILES.get(41), PlayerColor.GREEN, Rotation.NONE, new Pos(2, 0));
        board = board.withNewTile(tile41);
        PlacedTile tile35 = new PlacedTile(Tiles.TILES.get(35), PlayerColor.GREEN, Rotation.HALF_TURN, new Pos(3, 0));
        board = board.withNewTile(tile35);
        PlacedTile tile67 = new PlacedTile(Tiles.TILES.get(67), PlayerColor.YELLOW, Rotation.NONE, new Pos(0, 1));
        board = board.withNewTile(tile67);
        PlacedTile tile51 = new PlacedTile(Tiles.TILES.get(51), PlayerColor.YELLOW, Rotation.NONE, new Pos(-1, 0));
        board = board.withNewTile(tile51);

        board = board.withOccupant(new Occupant(Occupant.Kind.PAWN, 410));
        board = board.withOccupant(new Occupant(Occupant.Kind.HUT, 568));

        MessageBoard messageBoard = new MessageBoard(new TextMakerFr(playerNames), List.of());

        GameState gameState = new GameState(playerColors, tileDecks, null, board, GameState.Action.OCCUPY_TILE, messageBoard);

        /**var gameState =
                GameState.initial(playerColors,
                        tileDecks,
                        textMaker).withStartingTilePlaced();**/

        var gameStateO = new SimpleObjectProperty<>(gameState);

        var playersNode = PlayersUI.create(gameStateO, textMaker);
        var rootNode = new BorderPane(playersNode);
        primaryStage.setScene(new Scene(rootNode));

        primaryStage.setTitle("ChaCuN test");
        primaryStage.show();
    }
}

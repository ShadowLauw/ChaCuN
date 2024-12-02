package ch.epfl.chacun;

import ch.epfl.chacun.gui.PlayersUI;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
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

        var tilesByKind = ch.epfl.chacun.Tiles.TILES.stream()
                .collect(Collectors.groupingBy(Tile::kind));
        var tileDecks =
                new TileDecks(tilesByKind.get(Tile.Kind.START),
                        tilesByKind.get(Tile.Kind.NORMAL),
                        tilesByKind.get(Tile.Kind.MENHIR));

        var textMaker = new TextMakerFr(playerNames);

        GameState gameState = GameState.initial(playerColors, tileDecks, textMaker);

        var gameStateO = new SimpleObjectProperty<>(gameState);

        var playersNode = PlayersUI.create(gameStateO, textMaker);
        var rootNode = new BorderPane(playersNode);
        primaryStage.setScene(new Scene(rootNode));

        primaryStage.setTitle("ChaCuN test");
        primaryStage.show();

        gameStateO.setValue(gameStateO.get().withStartingTilePlaced());
    }
}

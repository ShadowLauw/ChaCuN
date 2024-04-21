package ch.epfl.chacun;

import ch.epfl.chacun.gui.DecksUI;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class EmmanuelDecksUITest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        MessageBoard messageBoard = new MessageBoard(new TextMakerFr(Map.of(PlayerColor.PURPLE, "Rose", PlayerColor.GREEN, "Bernard")), List.of());
        Area<Zone.Forest> forest = new Area<>(Set.of((Zone.Forest)TileReader.readTileFromCSV(56).e().zones().getFirst(), (Zone.Forest)TileReader.readTileFromCSV(20).s().zones().getFirst(), (Zone.Forest)TileReader.readTileFromCSV(87).e().zones().getFirst()), List.of(PlayerColor.GREEN, PlayerColor.PURPLE, PlayerColor.PURPLE), 0);
        messageBoard = messageBoard.withScoredForest(forest);

        var tile = new SimpleObjectProperty<>(TileReader.readTileFromCSV(56));
        var deckSize = new SimpleObjectProperty<>(60);
        var menhirSize = new SimpleObjectProperty<>(5);
        var text = new SimpleObjectProperty<>("");
        var messageNode = DecksUI.create(tile, deckSize, menhirSize, text, (e) -> System.out.println("Hello World!"));

        var rootNode = new BorderPane(messageNode);
        primaryStage.setScene(new Scene(rootNode));
        primaryStage.setTitle("Message Board UI");
        primaryStage.show();

    }
}

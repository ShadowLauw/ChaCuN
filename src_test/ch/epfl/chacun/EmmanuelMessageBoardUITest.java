package ch.epfl.chacun;

import ch.epfl.chacun.gui.MessageBoardUI;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static javafx.application.Application.launch;

public class EmmanuelMessageBoardUITest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        MessageBoard messageBoard = new MessageBoard(new TextMakerFr(Map.of(PlayerColor.PURPLE, "Rose", PlayerColor.GREEN, "Bernard")), List.of());
        Area<Zone.Forest> forest = new Area<>(Set.of((Zone.Forest)TileReader.readTileFromCSV(56).e().zones().getFirst(), (Zone.Forest)TileReader.readTileFromCSV(20).s().zones().getFirst(), (Zone.Forest)TileReader.readTileFromCSV(87).e().zones().getFirst()), List.of(PlayerColor.GREEN, PlayerColor.PURPLE, PlayerColor.PURPLE), 0);
        messageBoard = messageBoard.withScoredForest(forest);

        var messages = new SimpleObjectProperty<>(messageBoard.messages());
        var tilesId = new SimpleObjectProperty<>(Set.of(56, 20, 87));
        var messageNode = MessageBoardUI.create(messages, tilesId);

        var rootNode = new BorderPane(messageNode);
        primaryStage.setScene(new Scene(rootNode));
        primaryStage.setTitle("Message Board UI");
        primaryStage.show();

    }
}

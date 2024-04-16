package ch.epfl.chacun.gui;

import ch.epfl.chacun.MessageBoard;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Set;

import static javafx.application.Platform.runLater;

/**
 * Represents the message board of the game with JavaFX
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class MessageBoardUI {
    /**
     * Private constructor to prevent instantiation.
     */
    private MessageBoardUI(){};

    /**
     * A static method to create a Node for the message board
     * @param messages the list of messages to display
     * @param tilesId the id of the tiles to display
     * @return a Node representing the message board
     */
    public static Node create(ObservableValue<List<MessageBoard.Message>> messages, ObjectProperty<Set<Integer>> tilesId) {
        //je sais pas si c vrm autorisé de faire ça, mais autrement je vois vrm pas comment faire pour ajouter dynamiquement les messages
        VBox messagesBox = new VBox();
        ScrollPane scrollPane = new ScrollPane(messagesBox);

        messages.addListener((obs, oldV, newV) -> {
            newV.removeAll(oldV);
            for (MessageBoard.Message message : newV) {
                Text text = new Text(message.toString());
                text.setOnMouseEntered(e -> tilesId.setValue(message.tileIds()));
                text.setOnMouseExited(e -> tilesId.setValue(Set.of()));
                text.setWrappingWidth(ImageLoader.LARGE_TILE_FIT_SIZE);
                messagesBox.getChildren().add(text);
                runLater(() -> scrollPane.setVvalue(1));
            }
        });

        VBox vBox = new VBox(scrollPane);

        vBox.getStylesheets().add("message-board.css");
        vBox.setId("message-board");

        return vBox;
    }


}

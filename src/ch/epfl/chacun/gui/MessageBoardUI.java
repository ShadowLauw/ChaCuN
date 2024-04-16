package ch.epfl.chacun.gui;

import ch.epfl.chacun.MessageBoard;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
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

    private static final String MESSAGE_BOARD_CSS = "message-board.css";
    private static final String UI_ID = "message-board";
    /**
     * Private constructor to prevent instantiation.
     */
    private MessageBoardUI(){}

    /**
     * A static method to create a Node for the message board
     * @param messages the list of messages to display
     * @param tilesId the id of the tiles to display
     * @return a Node representing the message board
     */
    public static Node create(ObservableValue<List<MessageBoard.Message>> messages, ObjectProperty<Set<Integer>> tilesId) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId(UI_ID);
        scrollPane.getStylesheets().add(MESSAGE_BOARD_CSS);

        VBox messagesBox = new VBox();
        messagesBox.setAlignment(Pos.CENTER);
        scrollPane.setContent(messagesBox);

        messages.addListener((obs, oldV, newV) -> {
            for (int i = oldV.size(); i < newV.size(); ++i) {
                MessageBoard.Message message = newV.get(i);
                Text text = new Text(message.text());
                text.setOnMouseEntered(e -> tilesId.setValue(message.tileIds()));
                text.setOnMouseExited(e -> tilesId.setValue(Set.of()));
                text.setWrappingWidth(ImageLoader.LARGE_TILE_FIT_SIZE);
                messagesBox.getChildren().add(text);
            }
            runLater(() -> scrollPane.setVvalue(1));
        });

        return scrollPane;
    }

}

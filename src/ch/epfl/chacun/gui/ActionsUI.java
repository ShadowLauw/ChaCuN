package ch.epfl.chacun.gui;

import ch.epfl.chacun.Base32;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Consumer;

/**
 * Displays the actions part of the interface of the game
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class ActionsUI {
    /**
     * The path to the CSS file for the actions UI.
     */
    private static final String ACTIONS_CSS = "actions.css";

    /**
     * ID of the base node of the actions UI.
     */
    private static final String ACTIONS_ID = "actions";

    /**
     * ID of the text field of the actions UI.
     */
    private static final String FIELD_ID = "action-field";

    /**
     * Number of actions to display.
     */
    private static final int NUMBER_OF_ACTIONS_TO_DISPLAY = 4;

    /**
     * Private constructor to prevent instantiation.
     */
    private ActionsUI() {}

    /**
     * Creates a Node of the actions display
     *
     * @param actions the actions to display
     * @param actionConsumer the consumer to call when an action is entered
     * @return a node displaying the actions
     */
    public static Node create(ObservableValue<List<String>> actions, Consumer<String> actionConsumer) {
        HBox hbox = new HBox();
        hbox.setId(ACTIONS_ID);
        hbox.getStylesheets().add(ACTIONS_CSS);

        Text text = new Text();
        text.textProperty().bind(actions.map(ActionsUI::actionListLastFourBuilder));
        TextField textField = new TextField();
        textField.setId(FIELD_ID);

        textField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                actionConsumer.accept(textField.getText());
                textField.clear();
            }
        });

        textField.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(textFormatter(change.getText()));
            return change;
        }));

        hbox.getChildren().addAll(text, textField);

        return hbox;
    }

    private static String actionListLastFourBuilder(List<String> list) {
        if (list.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int lastIndex = list.size() - 1;
        int firstIndex = Math.max(0, lastIndex - NUMBER_OF_ACTIONS_TO_DISPLAY);
        for (int i = firstIndex; i <= lastIndex; ++i) {
            sb.append(i + 1).append(':').append(list.get(i));
            if (i != lastIndex) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    private static String textFormatter(String text) {
        return text.toUpperCase()
                .chars()
                .filter(c -> Base32.isValid(Character.toString(c)))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}

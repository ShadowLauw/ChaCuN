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
 * Displays the actions part of the interface of the game, and allow to have multiplayer mode
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public class ActionsUI {
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
     * Private constructor to prevent instantiation.
     */
    private ActionsUI() {};

    /**
     * Creates a Node of the actions display
     *
     * @param actions the actions to display
     * @param consumer the consumer to call when an action is entered
     * @return a node displaying the actions
     */
    public static Node create(ObservableValue<List<String>> actions, Consumer<String> consumer) {
        HBox hbox = new HBox();
        hbox.setId(ACTIONS_ID);
        hbox.getStylesheets().add(ACTIONS_CSS);

        Text text = new Text();
        TextField textField = new TextField();
        textField.setId(FIELD_ID);
        text.textProperty().bind(
                actions.map(list -> {
                    StringBuilder sb = new StringBuilder();
                    for (int i = list.size() - 1; i >= 0; --i) {
                        sb.append(i).append(':').append(list.get(i));
                        if (i != 0 || i != list.size() - 4) {
                            sb.append(", ");
                        } else {
                            return sb.toString();
                        }
                    }
                    return sb.toString();
                })
        );

        textField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                consumer.accept(textField.getText());
                textField.clear();
            }
        });

        textField.setTextFormatter(new TextFormatter<>(change -> {
            String fieldText = change.getText().chars()
                    .filter(c -> Base32.ALPHABET.contains(String.valueOf((char) Character.toUpperCase(c))))
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            change.setText(fieldText);
            return change;
        }));

        hbox.getChildren().addAll(text, textField);

        return hbox;
    }
}

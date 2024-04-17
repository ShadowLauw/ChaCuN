package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.Tile;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Displays the decks part and next tile/action of the interface of the game
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class DecksUI {
    /**
     * The path to the CSS file for the decks UI.
     */
    private static final String DECKS_CSS = "decks.css";
    /**
     * ID of the next tile node.
     */
    private static final String NEXT_TILE_ID = "next-tile";
    /**
     * ID of the decks' node.
     */
    private static final String DECKS_ID = "decks";
    /**
     * ID of the normal deck node.
     */
    private static final String NORMAL_DECK_ID = "NORMAL";
    /**
     * ID of the menhir deck node.
     */
    private static final String MENHIR_DECK_ID = "MENHIR";
    /**
     * The percentage of the image size to use for the text display.
     */
    private static final double TEXT_PERCENT = 0.8;

    /**
     * Private constructor to prevent instantiation.
     */
    private DecksUI() {
    }

    /**
     * A static method to create a Node for the decks display
     *
     * @param tileToPlace    the tile to place
     * @param normalDeckSize the size of the normal deck
     * @param menhirDeckSize the size of the menhir deck
     * @param textToDisplay  the text to display
     * @param eventHandler   the event handler for the next tile
     * @return a Node displaying the decks
     */
    public static Node create(
            ObservableValue<Tile> tileToPlace,
            ObservableValue<Integer> normalDeckSize,
            ObservableValue<Integer> menhirDeckSize,
            ObservableValue<String> textToDisplay,
            Consumer<Occupant> eventHandler
    ) {
        VBox decksUi = new VBox();
        decksUi.getStylesheets().add(DECKS_CSS);

        //------------Constructing the tile decks--------------------
        HBox decks = new HBox();
        decks.setId(DECKS_ID);
        decks.getChildren().add(constructImageStackPane(NORMAL_DECK_ID, normalDeckSize));
        decks.getChildren().add(constructImageStackPane(MENHIR_DECK_ID, menhirDeckSize));

        //------------Constructing the view of the tile to place--------------------
        StackPane stackPaneNextTile = new StackPane();
        stackPaneNextTile.setId(NEXT_TILE_ID);

        ImageView imageViewNextTile = new ImageView();
        Text textNextTile = new Text();
        imageViewNextTile.imageProperty().bind(tileToPlace.map(t -> ImageLoader.largeImageForTile(t.id())));
        imageViewNextTile.visibleProperty().bind(textToDisplay.map(String::isEmpty));
        imageViewNextTile.setFitWidth(ImageLoader.LARGE_TILE_FIT_SIZE);
        imageViewNextTile.setFitHeight(ImageLoader.LARGE_TILE_FIT_SIZE);

        textNextTile.textProperty().bind(textToDisplay);
        textNextTile.visibleProperty().bind(textToDisplay.map(s -> !s.isEmpty()));
        textNextTile.setWrappingWidth(ImageLoader.LARGE_TILE_FIT_SIZE * TEXT_PERCENT);
        textNextTile.setOnMouseClicked(e -> {
            if (textNextTile.isVisible()) eventHandler.accept(null);
        });

        stackPaneNextTile.getChildren().addAll(imageViewNextTile, textNextTile);
        decksUi.getChildren().addAll(decks, stackPaneNextTile);

        return decksUi;
    }

    /**
     * Constructs a stack pane with an image and a text displaying the deck size.
     *
     * @param deckId   the id of the deck
     * @param deckSize the size of the deck
     * @return the stack pane
     */
    private static StackPane constructImageStackPane(String deckId, ObservableValue<Integer> deckSize) {
        StackPane stackPane = new StackPane();
        ImageView imageView = new ImageView();
        imageView.setId(deckId);
        imageView.setFitHeight(ImageLoader.NORMAL_TILE_FIT_SIZE);
        imageView.setFitWidth(ImageLoader.NORMAL_TILE_FIT_SIZE);
        Text deckText = new Text();
        deckText.textProperty().bind(deckSize.map(Objects::toString));
        stackPane.getChildren().addAll(imageView, deckText);

        return stackPane;
    }
}

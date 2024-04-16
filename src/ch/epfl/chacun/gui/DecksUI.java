package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.Tile;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.function.Consumer;

//note : c'est peut être pas les bonnes constantes partout.

/**
 * Represents the different decks of the game with JavaFX
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class DecksUI {

    private static final String DECKS_CSS = "decks.css";
    private static final String NEXT_TILE_ID = "next-tile";
    private static final String DECKS_ID = "decks";
    private static final String NORMAL_DECK_ID = "NORMAL";
    private static final String MENHIR_DECK_ID = "MENHIR";
    private static final double TEXT_PERCENT = 0.8;

    /**
     * Private constructor to prevent instantiation.
     */
    private DecksUI() {
    }

    /**
     * A static method to create a Node for a Deck size
     * @param tileToPlace the tile to display
     * @param normalDeckSize the size of the normal deck (number of remaining cards)
     * @param menhirDeckSize the size of the menhir deck (number of remaining cards)
     * @param textToDisplay the text to display
     * @param eventHandler the event handler
     * @return a Node representing the deck
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
        decksUi.getChildren().add(decks);

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
        textNextTile.setOnMouseClicked(e -> {if (textNextTile.isVisible()) eventHandler.accept(null);});

        stackPaneNextTile.getChildren().add(imageViewNextTile);
        stackPaneNextTile.getChildren().add(textNextTile);

        decksUi.getChildren().add(stackPaneNextTile);

        return decksUi;
    }

    private static StackPane constructImageStackPane(String deckId, ObservableValue<Integer> deckSize) {
        StackPane stackPane = new StackPane();
        stackPane.setId(deckId);

        ImageView imageViewNormal = new ImageView();
        /**imageViewNormal.setImage(deckId.equals(NORMAL_DECK_ID)
                ? ImageLoader.NORMAL_NORMAL_TILE_BACK
                : ImageLoader.NORMAL_MENHIR_TILE_BACK);**/
        imageViewNormal.setFitHeight(ImageLoader.NORMAL_TILE_FIT_SIZE);
        imageViewNormal.setFitWidth(ImageLoader.NORMAL_TILE_FIT_SIZE);
        stackPane.getChildren().add(imageViewNormal);

        Text deckText = new Text();
        deckText.textProperty().bind(deckSize.map(Objects::toString));
        stackPane.getChildren().add(deckText);

        return stackPane;
    }
}

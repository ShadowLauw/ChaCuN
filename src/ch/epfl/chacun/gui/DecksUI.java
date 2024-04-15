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
import javafx.scene.text.Text;

import java.util.function.Consumer;

//note : c'est peut être pas les bonnes constantes partout.

/**
 * Represents the different decks of the game with JavaFX
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class DecksUI {
    private static final double TEXT_PERCENT = 0.8;

    /**
     * Private constructor to prevent instantiation.
     */
    private DecksUI() {
    }

    /**
     * A static method to create a Node for a Deck size
     * @param tile the tile to display
     * @param deckSize the size of the normal deck (number of remaining cards)
     * @param menhirDeckSize the size of the menhir deck (number of remaining cards)
     * @param textToDisplay the text to display
     * @param eventHandler the event handler
     * @return a Node representing the deck
     */
    public static Node create(ObservableValue<Tile> tile, ObservableValue<Integer> deckSize, ObservableValue<Integer> menhirDeckSize, ObservableValue<String> textToDisplay, Consumer<Occupant> eventHandler) {
        VBox vBox = new VBox();
        vBox.getStylesheets().add("decks.css");

        //------------Constructing the first child of VBox--------------------
        HBox hBox = new HBox();
        hBox.setId("decks");

        //constructing the first child of HBOX
        StackPane stackPaneNormal = new StackPane();
        ImageView imageViewNormal = new ImageView();
        Text textNormal = new Text();
        imageViewNormal.imageProperty().bind(tile.map(t -> ImageLoader.largeImageForTile("normal")));
        imageViewNormal.setFitWidth(NORMAL_TILE_FIT_SIZE);
        imageViewNormal.setFitHeight(NORMAL_TILE_FIT_SIZE);
        textNormal.textProperty().bind(deckSize.map(Object::toString));

        stackPaneNormal.getChildren().add(imageViewNormal);
        stackPaneNormal.getChildren().add(textNormal);
        hBox.getChildren().add(stackPaneNormal);
        //end

        //constructing the second child of HBOX
        StackPane stackPaneMenhir = new StackPane();
        ImageView imageViewMenhir = new ImageView();
        Text textMenhir = new Text();
        imageViewMenhir.imageProperty().bind(tile.map(t -> ImageLoader.largeImageForTile("menhir")));
        imageViewMenhir.setFitWidth(NORMAL_TILE_FIT_SIZE);
        imageViewMenhir.setFitHeight(NORMAL_TILE_FIT_SIZE);
        textMenhir.textProperty().bind(menhirDeckSize.map(Object::toString));

        stackPaneMenhir.getChildren().add(imageViewMenhir);
        stackPaneMenhir.getChildren().add(textMenhir);
        hBox.getChildren().add(stackPaneMenhir);
        //end

        vBox.getChildren().add(hBox);
        //------------End of the construction of the first child of VBox--------------------

        //------------Constructing the second child of VBox--------------------
        StackPane stackPaneNextTile = new StackPane();
        stackPaneNextTile.setId("next-tile");

        ImageView imageViewNextTile = new ImageView();
        Text textNextTile = new Text();
        imageViewNextTile.imageProperty().bind(tile.map(t -> ImageLoader.largeImageForTile(t.id())));
        imageViewNextTile.setFitWidth(LARGE_TILE_FIT_SIZE);
        imageViewNextTile.setFitHeight(LARGE_TILE_FIT_SIZE);
        textNextTile.textProperty().bind(textToDisplay);
        textNextTile.visibleProperty().bind(textToDisplay.map(s -> !s.isEmpty()));
        textNextTile.setWrappingWidth(LARGE_TILE_FIT_SIZE*TEXT_PERCENT);
        textNextTile.setOnMouseClicked(e -> {if (textNextTile.isVisible()) eventHandler.accept(null);});


        stackPaneNextTile.getChildren().add(imageViewNextTile);
        stackPaneNextTile.getChildren().add(textNextTile);

        vBox.getChildren().add(stackPaneNextTile);
        //----------End of the construction of the second child of VBox--------------------


        return vBox;
    }
}

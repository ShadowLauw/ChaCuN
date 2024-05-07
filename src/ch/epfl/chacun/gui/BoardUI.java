package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static ch.epfl.chacun.gui.Icon.newFor;

/**
 * Displays the board part of the interface of the game
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class BoardUI {
    /**
     * The path to the CSS file for the board UI.
     */
    private static final String BOARD_CSS = "board.css";

    /**
     * ID of the scroll pane of the board UI.
     */
    private static final String SCROLL_PANE_ID = "board-scroll-pane";

    /**
     * ID of the grid pane of the board UI.
     */
    private static final String GRID_ID = "board-grid";

    /**
     * Prefix for the pawns IDs.
     */
    private static final String PAWN_PREFIX = "pawn_";

    /**
     * Prefix for the huts IDs.
     */
    private static final String HUT_PREFIX = "hut_";

    /**
     * Prefix for the markers IDs.
     */
    private static final String MARKER_PREFIX = "marker_";

    /**
     * The style class for the markers.
     */
    private static final String MARKER_CLASS = "marker";

    /**
     * The opacity of the veil.
     */
    private static final double OPACITY_VEIL = 0.5;

    /**
     * The position of the middle of the window.
     */
    private static final double MID_WINDOW = 0.5;

    /**
     * The empty tile image.
     */
    private static final WritableImage EMPTY_TILE_IMAGE = createEmptyTileImage();

    /**
     * The gray color of the empty tile.
     */
    private static final double EMPTY_GRAY_COLOR = 0.98;

    /**
     * The cached images.
     */
    private static final Map<Integer, Image> cachedImages = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private BoardUI() {
    }

    /**
     * Creates a Node of the board display
     *
     * @param range            the range of the board
     * @param gameState        the observable value of the game state
     * @param rotationOfTile   the observable value of the rotation of the tile
     * @param visibleOccupants the observable value of the visible occupants
     * @param highlightedTiles the observable value of the highlighted tiles
     * @param rotationConsumer  the consumer to call when a rotation is clicked
     * @param posConsumer  the consumer to call when a tile is chosen
     * @param occupantConsumer   the consumer to call when an occupant is chosen
     * @return a node displaying the board
     */
    public static Node create(int range,
                              ObservableValue<GameState> gameState,
                              ObservableValue<Rotation> rotationOfTile,
                              ObservableValue<Set<Occupant>> visibleOccupants,
                              ObservableValue<Set<Integer>> highlightedTiles,
                              Consumer<Rotation> rotationConsumer,
                              Consumer<Pos> posConsumer,
                              Consumer<Occupant> occupantConsumer
    ) {
        // Create the base node
        ScrollPane baseNode = new ScrollPane();
        baseNode.setId(SCROLL_PANE_ID);
        baseNode.getStylesheets().add(BOARD_CSS);
        baseNode.setVvalue(MID_WINDOW);
        baseNode.setHvalue(MID_WINDOW);
        GridPane grid = new GridPane();
        grid.setId(GRID_ID);
        baseNode.setContent(grid);

        // Create observable values of the gameState components
        ObservableValue<Board> board = gameState.map(GameState::board);
        ObservableValue<Set<Animal>> cancelledAnimals = board.map(Board::cancelledAnimals);
        ObservableValue<GameState.Action> currentAction = gameState.map(GameState::nextAction);

        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                Group tileGroup = new Group();

                // Create the observable values of the tile components
                Pos posOfTile = new Pos(x, y);
                ObservableValue<PlacedTile> tileAtPos = board.map(b -> b.tileAt(posOfTile));
                ObservableValue<Boolean> isInsertionAndDisplayed = Bindings.createObjectBinding(
                        () -> board.getValue().insertionPositions().contains(posOfTile)
                                && currentAction.getValue() == GameState.Action.PLACE_TILE,
                        board,
                        currentAction
                );

                // Create the tile image view
                ImageView tileImageView = new ImageView();
                tileImageView.setFitHeight(ImageLoader.NORMAL_TILE_FIT_SIZE);
                tileImageView.setFitWidth(ImageLoader.NORMAL_TILE_FIT_SIZE);
                tileGroup.getChildren().add(tileImageView);

                // Create the observable value of the cell data
                ObservableValue<CellData> observableTile = Bindings.createObjectBinding(() -> {
                            PlacedTile tile = tileAtPos.getValue();
                            //A tile is placed
                            if (tile != null) {
                                Image tileImage = cachedImages.computeIfAbsent(tile.id(), ImageLoader::normalImageForTile);
                                Set<Integer> highlightedTilesValue = highlightedTiles.getValue();
                                //Check if it has to be not highlighted -> Black veil if it has
                                if (!highlightedTilesValue.isEmpty() && !highlightedTilesValue.contains(tile.id()))
                                    return new CellData(tileImage, tile.rotation().degreesCW(), Color.BLACK);

                                //No veil otherwise, the image of the tile is displayed
                                return new CellData(tileImage, tile.rotation().degreesCW());
                            } else if (isInsertionAndDisplayed.getValue()) {
                                PlayerColor currentPlayer = gameState.getValue().currentPlayer();
                                //If the mouse is over the cell
                                if (tileGroup.hoverProperty().getValue()) {
                                    Tile nextTileToPlace = gameState.getValue().tileToPlace();
                                    int id = nextTileToPlace.id();
                                    Image tileImage = cachedImages.computeIfAbsent(id, ImageLoader::normalImageForTile);
                                    PlacedTile tileToPlace = new PlacedTile(
                                            nextTileToPlace,
                                            null,
                                            rotationOfTile.getValue(),
                                            posOfTile
                                    );
                                    //Check if the tile can be placed on the board or not -> White veil if it can't
                                    if (!board.getValue().canAddTile(tileToPlace)) {
                                        return new CellData(tileImage, rotationOfTile.getValue().degreesCW(), Color.WHITE);
                                    }

                                    //No veil otherwise, the image of the tile to place is displayed
                                    return new CellData(tileImage, rotationOfTile.getValue().degreesCW());
                                }/* If the mouse is not over the cell, but it is part of the insertion positions
                                     and the action is to place a tile -> Player color veil, empty image */ else if (currentPlayer != null) {
                                    return new CellData(rotationOfTile.getValue().degreesCW(),
                                            ColorMap.fillColor(currentPlayer)
                                    );
                                }
                            }

                            return new CellData();
                        },
                        tileAtPos,
                        rotationOfTile,
                        tileGroup.hoverProperty(),
                        isInsertionAndDisplayed,
                        highlightedTiles,
                        gameState,
                        board
                );

                tileImageView.imageProperty().bind(observableTile.map(t -> t.tileImage));

                //Click effect management
                tileGroup.setOnMouseClicked(e -> {
                    MouseButton button = e.getButton();
                    if (button == MouseButton.PRIMARY) {
                        posConsumer.accept(posOfTile);
                    } else if (button == MouseButton.SECONDARY) {
                        if (e.isAltDown()) {
                            rotationConsumer.accept(Rotation.RIGHT);
                        } else {
                            rotationConsumer.accept(Rotation.LEFT);
                        }
                    }
                });

                //Add the markers if the tile is placed
                tileAtPos.addListener((_, _, newTile) -> {
                    if (newTile != null) {
                        for (Zone.Meadow meadow : newTile.meadowZones()) {
                            for (Animal animal : meadow.animals()) {
                                ImageView markerAnimal = new ImageView();
                                markerAnimal.setId(MARKER_PREFIX + animal.id());
                                markerAnimal.setFitHeight(ImageLoader.MARKER_FIT_SIZE);
                                markerAnimal.setFitWidth(ImageLoader.MARKER_FIT_SIZE);
                                markerAnimal.getStyleClass().add(MARKER_CLASS);
                                markerAnimal.visibleProperty().bind(cancelledAnimals.map(s -> s.contains(animal)));
                                tileGroup.getChildren().add(markerAnimal);
                            }
                        }

                        for (Occupant occupant : newTile.potentialOccupants()) {
                            Node markerOccupant = newFor(newTile.placer(), occupant.kind());
                            if (occupant.kind() == Occupant.Kind.HUT)
                                markerOccupant.setId(HUT_PREFIX + occupant.zoneId());
                            else
                                markerOccupant.setId(PAWN_PREFIX + occupant.zoneId());

                            markerOccupant.visibleProperty().bind(visibleOccupants.map(s -> s.contains(occupant)));
                            markerOccupant.rotateProperty().bind(observableTile.map(t -> -t.rotation));
                            markerOccupant.setOnMouseClicked(_ -> occupantConsumer.accept(occupant));
                            tileGroup.getChildren().add(markerOccupant);
                        }
                    }
                });

                //Others effects management
                tileGroup.rotateProperty().bind(observableTile.map(t -> t.rotation));
                ColorInput veilInput = new ColorInput(
                        0,
                        0,
                        ImageLoader.NORMAL_TILE_FIT_SIZE,
                        ImageLoader.NORMAL_TILE_FIT_SIZE,
                        Color.TRANSPARENT
                );
                veilInput.paintProperty().bind(observableTile.map(t ->
                        t.veilColor != null
                                ? t.veilColor.deriveColor(0, 1, 1, OPACITY_VEIL)
                                : Color.TRANSPARENT
                ));

                tileGroup.setEffect(new Blend(BlendMode.SRC_OVER, null, veilInput));

                grid.add(tileGroup, x + range, y + range);
            }
        }

        return baseNode;
    }

    /**
     * Represents the data of a cell of the board
     *
     * @param tileImage the image of the tile
     * @param rotation  the rotation of the tile in degrees
     * @param veilColor the color of the veil
     */
    private record CellData(Image tileImage,
                            int rotation,
                            Color veilColor
    ) {
        /**
         * Creates a cell data with the empty tile image, no rotation and no veil color
         */
        public CellData() {
            this(EMPTY_TILE_IMAGE, 0, null);
        }

        /**
         * Creates a cell data with the given tile image, rotation and no veil color
         *
         * @param tileImage the image of the tile
         * @param rotation  the rotation of the tile in degrees
         */
        public CellData(Image tileImage, int rotation) {
            this(tileImage, rotation, null);
        }

        /**
         * Creates a cell data with the empty tile image, the given rotation and veil color
         *
         * @param rotation  the rotation of the tile in degrees
         * @param veilColor the color of the veil
         */
        public CellData(int rotation, Color veilColor) {
            this(EMPTY_TILE_IMAGE, rotation, veilColor);
        }
    }

    private static WritableImage createEmptyTileImage() {
        WritableImage image = new WritableImage(1, 1);
        image.getPixelWriter().setColor(0, 0, Color.gray(EMPTY_GRAY_COLOR));
        return image;
    }
}

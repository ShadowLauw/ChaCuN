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
     * The empty tile image.
     */
    private static final WritableImage emptyTileImage = new WritableImage(1, 1);
    /**
     * The cached images.
     */
    private static final Map<Integer, Image> cachedImages = new HashMap<>();
    /**
     * Private constructor to prevent instantiation.
     */
    private BoardUI() {}

    /**
     * Creates a Node of the board display
     *
     * @param range the range of the board
     * @param gameState the observable value of the game state
     * @param rotationOfTile the observable value of the rotation of the tile
     * @param visibleOccupants the observable value of the visible occupants
     * @param highlightedTiles the observable value of the highlighted tiles
     * @param rotationOnClick the consumer to call when a rotation is clicked
     * @param posOfTileChosen the consumer to call when a tile is chosen
     * @param occupantChosen the consumer to call when an occupant is chosen
     * @return a node displaying the board
     */
    public static Node create(int range,
                       ObservableValue<GameState> gameState,
                       ObservableValue<Rotation> rotationOfTile,
                       ObservableValue<Set<Occupant>> visibleOccupants,
                       ObservableValue<Set<Integer>> highlightedTiles,
                       Consumer<Rotation> rotationOnClick,
                       Consumer<Pos> posOfTileChosen,
                       Consumer<Occupant> occupantChosen
                       ) {

        ScrollPane baseNode = new ScrollPane();
        baseNode.setId(SCROLL_PANE_ID);
        baseNode.getStylesheets().add(BOARD_CSS);
        GridPane grid = new GridPane();
        grid.setId(GRID_ID);
        baseNode.setContent(grid);

        emptyTileImage.getPixelWriter().setColor(0, 0, Color.gray(0.98));

        ObservableValue<Board> board = gameState.map(GameState::board);
        ObservableValue<Set<Animal>> cancelledAnimals = board.map(Board::cancelledAnimals);
        ObservableValue<PlayerColor> currentPlayer = gameState.map(GameState::currentPlayer);
        ObservableValue<GameState.Action> currentAction = gameState.map(GameState::nextAction);
        ObservableValue<Tile> nextTileToPlace = gameState.map(GameState::tileToPlace);

        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                Group tileGroup = new Group();

                Pos posOfTile = new Pos(x, y);
                ObservableValue<Boolean> isMouseOver = tileGroup.hoverProperty();
                ObservableValue<Boolean> isInsertionPosition = board.map(b -> b.insertionPositions().contains(posOfTile));
                ObservableValue<PlacedTile> tileAtPos = board.map(b -> b.tileAt(posOfTile));

                ImageView tileImageView = new ImageView();
                tileImageView.setFitHeight(ImageLoader.NORMAL_TILE_FIT_SIZE);
                tileImageView.setFitWidth(ImageLoader.NORMAL_TILE_FIT_SIZE);
                tileGroup.getChildren().add(tileImageView);

                ObservableValue<CellData> observableTile = Bindings.createObjectBinding(() -> {
                    PlacedTile tile = tileAtPos.getValue();
                    Set<Integer> highlightedTilesValue = highlightedTiles.getValue();

                    if (tile != null) {
                         Image tileImage = cachedImages.computeIfAbsent(tile.id(), ImageLoader::normalImageForTile);
                         if (!highlightedTilesValue.isEmpty() && !highlightedTilesValue.contains(tile.id()))
                            return new CellData(tileImage, tile.rotation().degreesCW(), Color.BLACK);

                         return new CellData(tileImage, tile.rotation().degreesCW());
                    } else if (isInsertionPosition.getValue()
                            && currentAction.getValue() == GameState.Action.PLACE_TILE
                    ) {
                        if (isMouseOver.getValue()) {
                            int id = nextTileToPlace.getValue().id();
                            Image tileImage = cachedImages.computeIfAbsent(id, ImageLoader::normalImageForTile);
                            PlacedTile tileToPlace = new PlacedTile(
                                    nextTileToPlace.getValue(),
                                    null,
                                    rotationOfTile.getValue(),
                                    posOfTile
                            );
                            if (!board.getValue().canAddTile(tileToPlace)) {
                                return new CellData(tileImage, rotationOfTile.getValue().degreesCW(), Color.WHITE);
                            }

                            return new CellData(tileImage, rotationOfTile.getValue().degreesCW());
                        } else if (currentPlayer.getValue() != null) {
                            return new CellData(rotationOfTile.getValue().degreesCW(),
                                    ColorMap.fillColor(currentPlayer.getValue())
                            );
                        }
                    }

                    return new CellData();
                    },
                        tileAtPos,
                        rotationOfTile,
                        isMouseOver,
                        isInsertionPosition,
                        currentAction,
                        highlightedTiles,
                        nextTileToPlace,
                        board,
                        currentPlayer
                );

                tileImageView.imageProperty().bind(observableTile.map(t -> t.tileImage));

                tileGroup.setOnMouseClicked(e -> {
                    if (currentAction.getValue() == GameState.Action.PLACE_TILE && isInsertionPosition.getValue()) {
                        MouseButton button = e.getButton();
                        if (button == MouseButton.PRIMARY) {
                            posOfTileChosen.accept(posOfTile);
                        } else if (button == MouseButton.SECONDARY) {
                            if (e.isAltDown()) {
                                rotationOnClick.accept(Rotation.RIGHT);
                            } else {
                                rotationOnClick.accept(Rotation.LEFT);
                            }
                        }
                    }
                });

                tileAtPos.addListener((_, _, newTile) -> {
                    if (newTile != null) {
                        for (Zone.Meadow meadow : newTile.meadowZones()) {
                            for (Animal animal : meadow.animals()) {
                                ImageView markerAnimal = new ImageView();
                                markerAnimal.setId(MARKER_PREFIX + animal.id());
                                markerAnimal.getStyleClass().add(MARKER_CLASS);
                                markerAnimal.visibleProperty().bind(cancelledAnimals.map(s -> s.contains(animal)));
                                tileGroup.getChildren().add(markerAnimal);
                            }
                        }

                        for (Occupant occupant : newTile.potentialOccupants()) {
                            ImageView markerOccupant = new ImageView();
                            if (occupant.kind() == Occupant.Kind.HUT)
                                markerOccupant.setId(HUT_PREFIX + occupant.zoneId());
                            else
                                markerOccupant.setId(PAWN_PREFIX + occupant.zoneId());

                            markerOccupant.visibleProperty().bind(visibleOccupants.map(s -> s.contains(occupant)));
                            markerOccupant.rotateProperty().bind(observableTile.map(t -> -t.rotation));
                            markerOccupant.setOnMouseClicked(_ -> occupantChosen.accept(occupant));
                            tileGroup.getChildren().add(markerOccupant);
                        }
                    }
                });


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

    private record CellData(Image tileImage,
                            int rotation,
                            Color veilColor
    ) {
        public CellData () {
            this(emptyTileImage, 0, null);
        }

        public CellData (Image tileImage, int rotation) {
            this(tileImage, rotation, null);
        }

        public CellData (int rotation, Color veilColor) {
            this(emptyTileImage, rotation, veilColor);
        }
    }
}

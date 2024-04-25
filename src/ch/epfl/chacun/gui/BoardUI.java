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

public final class BoardUI {

    private static final String BOARD_CSS = "board.css";
    private static final String SCROLL_PANE_ID = "board-scroll-pane";
    private static final String GRID_ID = "board-grid";

    private static final String PAWN_PREFIX = "pawn_";
    private static final String HUT_PREFIX = "hut_";
    private static final String MARKER_PREFIX = "marker_";
    private static final String MARKER_CLASS = "marker";

    private static final double OPACITY_VEIL = 0.5;

    private static final WritableImage emptyTileImage = new WritableImage(1, 1);

    private static final Map<Integer, Image> cachedImages = new HashMap<>();

    private BoardUI() {}

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
        ObservableValue<Set<Pos>> insertionPositions = board.map(Board::insertionPositions);
        ObservableValue<PlayerColor> currentPlayer = gameState.map(GameState::currentPlayer);
        ObservableValue<GameState.Action> currentAction = gameState.map(GameState::nextAction);

        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                Group tileGroup = new Group();

                Pos posOfTile = new Pos(x, y);
                ObservableValue<Boolean> isMouseOver = tileGroup.hoverProperty();
                ObservableValue<Boolean> isInsertionPosition = insertionPositions.map(s -> s.contains(posOfTile));

                ObservableValue<PlacedTile> tileAtPos = board.map(b -> b.tileAt(posOfTile));
                ObservableValue<Image> tileImage = Bindings.createObjectBinding(() -> {
                    PlacedTile tile = tileAtPos.getValue();
                    if (tile != null) {
                        return cachedImages.computeIfAbsent(tile.id(), ImageLoader::normalImageForTile);
                    } else if (isInsertionPosition.getValue()
                            && currentAction.getValue() == GameState.Action.PLACE_TILE
                            && isMouseOver.getValue()
                    ) {
                        int id = gameState.getValue().tileToPlace().id();
                        return cachedImages.computeIfAbsent(id, ImageLoader::normalImageForTile);
                    } else {
                        return emptyTileImage;
                    }
                }, tileAtPos, isMouseOver, isInsertionPosition, currentAction, gameState);

                ImageView tileImageView = new ImageView();
                tileImageView.setFitHeight(ImageLoader.NORMAL_TILE_FIT_SIZE);
                tileImageView.setFitWidth(ImageLoader.NORMAL_TILE_FIT_SIZE);
                tileImageView.imageProperty().bind(tileImage);

                tileGroup.getChildren().add(tileImageView);

                ObservableValue<Color> veilColor = Bindings.createObjectBinding(
                        () -> {
                            PlacedTile tile = tileAtPos.getValue();
                            Set<Integer> highlightedTilesValue = highlightedTiles.getValue();
                            if (tile != null
                                    && !highlightedTilesValue.isEmpty()
                                    && !highlightedTilesValue.contains(tile.id())
                            ) {
                                return Color.BLACK;
                            } else if (currentAction.getValue() == GameState.Action.PLACE_TILE
                                    && isInsertionPosition.getValue()
                            ) {
                                if (!isMouseOver.getValue())
                                    return ColorMap.fillColor(currentPlayer.getValue());
                                else {
                                    PlacedTile tileToPlace = new PlacedTile(gameState.getValue().tileToPlace(),
                                            null,
                                            rotationOfTile.getValue(),
                                            posOfTile
                                    );
                                    if (!board.getValue().canAddTile(tileToPlace))
                                        return Color.WHITE;

                                    return null;
                                }
                            } else
                                return null;
                        },
                        isInsertionPosition,
                        highlightedTiles,
                        tileAtPos,
                        isMouseOver,
                        rotationOfTile,
                        currentPlayer,
                        gameState,
                        board
                );

                ObservableValue<CellData> observableTile = Bindings.createObjectBinding(
                        () -> new CellData(tileImage.getValue(), rotationOfTile.getValue(), veilColor.getValue()),
                        tileImage,
                        rotationOfTile,
                        veilColor
                );

                tileGroup.setOnMouseClicked(e -> {
                    if (currentAction.getValue() == GameState.Action.PLACE_TILE && isInsertionPosition.getValue()) {
                        if (e.getButton() == MouseButton.PRIMARY) {
                            posOfTileChosen.accept(posOfTile);
                        } else if (e.getButton() == MouseButton.SECONDARY) {
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
                            markerOccupant.rotateProperty().bind(observableTile.map(t -> t.rotation.negated().degreesCW()));
                            markerOccupant.setOnMouseClicked(_ -> occupantChosen.accept(occupant));
                            tileGroup.getChildren().add(markerOccupant);
                        }
                    }
                });


                tileGroup.rotateProperty().bind(observableTile.map(t -> t.rotation.degreesCW()));
                tileGroup.effectProperty().bind(observableTile.map(
                        t -> t.veilColor == null
                        ? null
                        : new Blend(BlendMode.SRC_OVER,
                                tileImageView.getEffect(),
                                new ColorInput(0,
                                        0,
                                        ImageLoader.NORMAL_TILE_FIT_SIZE,
                                        ImageLoader.NORMAL_TILE_FIT_SIZE,
                                        t.veilColor.deriveColor(0,
                                                1,
                                                1,
                                                OPACITY_VEIL)
                                )
                        )
                ));
                grid.add(tileGroup, x + range, y + range);
            }
        }

        return baseNode;
    }

    private record CellData(Image tileImage,
                            Rotation rotation,
                            Color veilColor
    ) {}
}

package ch.epfl.chacun.gui;

import ch.epfl.chacun.GameState;
import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.Pos;
import ch.epfl.chacun.Rotation;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import java.util.Set;
import java.util.function.Consumer;

public final class BoardUI {

    private static final String BOARD_CSS = "board.css";
    private static final String SCROLL_PANE_ID = "board-scroll-pane";
    private static final String GRID_ID = "board-grid";

    private BoardUI() {}

    public Node create(int range,
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

        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                
            }
        }


    }

    private record CellData() {
        Image
    }
}

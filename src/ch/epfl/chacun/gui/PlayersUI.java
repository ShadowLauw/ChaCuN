package ch.epfl.chacun.gui;

import ch.epfl.chacun.GameState;
import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import ch.epfl.chacun.TextMaker;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.epfl.chacun.gui.ColorMap.fillColor;
import static ch.epfl.chacun.gui.Icon.newFor;

/**
 * Displays the players infos part of the interface of the game
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class PlayersUI {
    /**
     * The opacity of the pawns and huts when they are available.
     */
    private static final double NORMAL_OPACITY = 1.0;

    /**
     * The opacity of the pawns and huts when they are not available.
     */
    private static final double LOW_OPACITY = 0.1;

    /**
     * ID of the base node of the players UI.
     */
    private static final String UI_ID = "players";

    /**
     * The path to the CSS file for the players UI.
     */
    private static final String PLAYERS_CSS = "players.css";

    /**
     * The style class for the players nodes.
     */
    private static final String PLAYER_STYLE_CLASS = "player";

    /**
     * The style class for the current player node.
     */
    private static final String CURRENT_PLAYER_STYLE_CLASS = "current";

    /**
     * The separator between the player's name and points.
     */
    private static final Text SEPARATOR = new Text("   ");

    /**
     * Private constructor to prevent instantiation.
     */
    private PlayersUI() {
    }

    /**
     * Creates a Node of the players infos display
     *
     * @param gameState the game state
     * @param textMaker the text maker
     * @return a node displaying the players infos
     */
    public static Node create(ObservableValue<GameState> gameState, TextMaker textMaker) {
        //Root node
        VBox playersUI = new VBox();
        playersUI.setId(UI_ID);
        playersUI.getStylesheets().add(PLAYERS_CSS);
        playersUI.setAlignment(Pos.CENTER);

        ObservableValue<Map<PlayerColor, Integer>> points = gameState.map(g -> g.messageBoard().points());
        ObservableValue<PlayerColor> currentPlayer = gameState.map(GameState::currentPlayer);

        for (PlayerColor player : gameState.getValue().players()) {
            String playerName = textMaker.playerName(player);
            //Player node
            TextFlow playerInfos = new TextFlow();
            playerInfos.getStyleClass().add(PLAYER_STYLE_CLASS);
            playerInfos.setId(player.toString());

            //Player's color circle
            Circle circle = new Circle(5, fillColor(player));

            //Player's points
            ObservableValue<String> pointsText0 = points.map(p ->
                    STR." \{playerName} : \{textMaker.points(p.getOrDefault(player, 0))}\n"
            );
            Text pointsText = new Text();
            pointsText.textProperty().bind(pointsText0);

            //Player's pawns and huts
            List<Node> pawns = new ArrayList<>();
            List<Node> huts = new ArrayList<>();
            ObservableValue<Integer> nbOfPawn = gameState.map(g -> g.freeOccupantsCount(player, Occupant.Kind.PAWN));
            ObservableValue<Integer> nbOfHut = gameState.map(g -> g.freeOccupantsCount(player, Occupant.Kind.HUT));
            for (int i = 0; i < Occupant.occupantsCount(Occupant.Kind.PAWN); ++i) {
                int index = i;
                ObservableValue<Double> opacity = nbOfPawn.map(n -> index < n ? NORMAL_OPACITY : LOW_OPACITY);
                Node pawn = newFor(player, Occupant.Kind.PAWN);
                pawn.opacityProperty().bind(opacity);
                pawns.add(pawn);
            }
            for (int i = 0; i < Occupant.occupantsCount(Occupant.Kind.HUT); ++i) {
                int index = i;
                ObservableValue<Double> opacity = nbOfHut.map(n -> index < n ? NORMAL_OPACITY : LOW_OPACITY);
                Node hut = newFor(player, Occupant.Kind.HUT);
                hut.opacityProperty().bind(opacity);
                huts.add(hut);
            }

            playerInfos.getChildren().addAll(circle, pointsText);
            playerInfos.getChildren().addAll(huts);
            playerInfos.getChildren().add(SEPARATOR);
            playerInfos.getChildren().addAll(pawns);

            //Current player style management
            currentPlayer.addListener((_, oldPlayer, newPlayer) -> {
                if (newPlayer == player) {
                    playerInfos.getStyleClass().add(CURRENT_PLAYER_STYLE_CLASS);
                } else if (oldPlayer == player) {
                    playerInfos.getStyleClass().remove(CURRENT_PLAYER_STYLE_CLASS);
                }
            });

            playersUI.getChildren().add(playerInfos);
        }

        return playersUI;
    }
}

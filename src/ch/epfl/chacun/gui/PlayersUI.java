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

public final class PlayersUI {

    private final static double NORMAL_OPACITY = 1.0;
    private final static double LOW_OPACITY = 0.1;
    private final static String UI_ID = "players";
    private final static String PLAYERS_CSS = "players.css";
    private final static String PLAYER_STYLE_CLASS = "player";
    private final static String CURRENT_PLAYER_STYLE_CLASS = "current";

    private PlayersUI() {}

    public static Node create(ObservableValue<GameState> gameState, TextMaker textMaker) {
        VBox playersUI = new VBox();
        playersUI.setId(UI_ID);
        playersUI.getStylesheets().add(PLAYERS_CSS);
        playersUI.setAlignment(Pos.CENTER_LEFT);

        ObservableValue<Map<PlayerColor, Integer>> points = gameState.map(g -> g.messageBoard().points());

        for (PlayerColor player : PlayerColor.ALL) {
            String playerName = textMaker.playerName(player);
            if (playerName != null) {
                TextFlow playerUI = new TextFlow();
                playerUI.getStyleClass().add(PLAYER_STYLE_CLASS);
                playerUI.setId(player.toString());

                Circle circle = new Circle(5, fillColor(player));
                playerUI.getChildren().add(circle);

                ObservableValue<String> pointsText0 =
                        points.map(p -> STR." \{playerName} : \{textMaker.points(p.getOrDefault(player, 0))}\n");
                Text pointsText = new Text();
                pointsText.textProperty().bind(pointsText0);
                playerUI.getChildren().add(pointsText);

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
                playerUI.getChildren().addAll(huts);
                playerUI.getChildren().add(new Text("   "));
                playerUI.getChildren().addAll(pawns);

                playersUI.getChildren().add(playerUI);
            }
        }

        ObservableValue<PlayerColor> currentPlayer = gameState.map(GameState::currentPlayer);
        currentPlayer.addListener((o, oldPlayer, newPlayer) -> {
            if (oldPlayer != null) {
                playersUI.lookup(STR."#\{oldPlayer}").getStyleClass().remove(CURRENT_PLAYER_STYLE_CLASS);
            }
            if (newPlayer != null) {
                playersUI.lookup(STR."#\{newPlayer}").getStyleClass().add(CURRENT_PLAYER_STYLE_CLASS);
            }
        });

        return playersUI;
    }
}

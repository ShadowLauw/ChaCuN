package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import ch.epfl.chacun.Tiles;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;

import static java.lang.Long.parseUnsignedLong;

/**
 * The main class of the ChaCuN GUI, which creates the game window and starts the game.
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public class Main extends Application {
    private static final String SEED_KEY = "seed";
    private static final double WINDOW_HEIGHT = 1080;
    private static final double WINDOW_WIDTH = 1440;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parameters param = getParameters();
        List<String> playerNames = param.getUnnamed();
        Map<String, String> seedMap = param.getNamed();
        Map<PlayerColor, String> players = getPlayers(playerNames);

        TileDecks myTileDecks = getShuffledTiles(seedMap);
        TextMakerFr textMaker = new TextMakerFr(players);
        GameState gameState = GameState.initial(players.keySet().stream().sorted().toList(), myTileDecks, textMaker);
        SimpleObjectProperty<GameState> gameStateO = new SimpleObjectProperty<>(gameState);

        SimpleObjectProperty<Rotation> rotationTileToPlace = new SimpleObjectProperty<>(Rotation.NONE);
        ObservableValue<Set<Occupant>> visibleOccupants = gameStateO.map(g -> {
                    Set<Occupant> visibleOccupantsSet = new HashSet<>(g.board().occupants());
                    if (g.nextAction() == GameState.Action.OCCUPY_TILE) {
                        visibleOccupantsSet.addAll(g.lastTilePotentialOccupants());
                    }
                    return visibleOccupantsSet;
                }
        );
        SimpleObjectProperty<Set<Integer>> highlightedTiles = new SimpleObjectProperty<>(Set.of());
        SimpleObjectProperty<List<String>> actions = new SimpleObjectProperty<>(List.of());
        ObservableValue<List<MessageBoard.Message>> messages = gameStateO.map(g -> g.messageBoard().messages());
        ObservableValue<Tile> tileToPlace = gameStateO.map(GameState::tileToPlace);
        ObservableValue<Integer> normalDeckSize = gameStateO.map(g -> g.tileDecks().deckSize(Tile.Kind.NORMAL));
        ObservableValue<Integer> menhirDeckSize = gameStateO.map(g -> g.tileDecks().deckSize(Tile.Kind.MENHIR));
        ObservableValue<String> textToDisplayDeck = gameStateO.map(g -> switch (g.nextAction()) {
            case OCCUPY_TILE -> textMaker.clickToOccupy();
            case RETAKE_PAWN -> textMaker.clickToUnoccupy();
            default -> "";
        });

        BorderPane rootNode = new BorderPane();
        Node board = BoardUI.create(
                Board.REACH,
                gameStateO,
                rotationTileToPlace,
                visibleOccupants,
                highlightedTiles,
                rotationTileToPlace(rotationTileToPlace),
                posOfTileChosen(gameStateO, actions,rotationTileToPlace),
                occupantChosen(gameStateO, actions)
        );
        BorderPane rightPane = new BorderPane();
        rootNode.setCenter(board);
        rootNode.setRight(rightPane);

        Node playersNode = PlayersUI.create(gameStateO, textMaker);
        Node messageBoardNode = MessageBoardUI.create(messages, highlightedTiles);
        VBox deckBox = new VBox();
        Node actionsNode = ActionsUI.create(actions, actionEntered(gameStateO, actions));
        Node deckNode = DecksUI.create(
                tileToPlace,
                normalDeckSize,
                menhirDeckSize,
                textToDisplayDeck,
                noPawnEventHandler(gameStateO, actions)
        );
        deckBox.getChildren().addAll(actionsNode, deckNode);
        rightPane.setTop(playersNode);
        rightPane.setCenter(messageBoardNode);
        rightPane.setBottom(deckBox);

        primaryStage.setScene(new Scene(rootNode));
        primaryStage.setTitle("ChaCuN");
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.show();

        gameStateO.setValue(gameStateO.getValue().withStartingTilePlaced());

    }

    private static Map<PlayerColor, String> getPlayers(List<String> playerNames) {
        Preconditions.checkArgument(playerNames.size() >= 2 && playerNames.size() <= 5);
        Map<PlayerColor, String> players = new HashMap<>();
        for (int i = 0; i < playerNames.size(); i++) {
            players.put(PlayerColor.ALL.get(i), playerNames.get(i));
        }

        return players;
    }

    private static TileDecks getShuffledTiles(Map<String, String> seedMap) {
        String seedString = seedMap.get(SEED_KEY);
        Long seed = seedString == null ? null : parseUnsignedLong(seedString);
        RandomGeneratorFactory<RandomGenerator> randomGenFactory = RandomGeneratorFactory.getDefault();
        RandomGenerator randomGen = seed == null ? randomGenFactory.create() : randomGenFactory.create(seed);

        List<Tile> myTiles = new ArrayList<>(Tiles.TILES);
        Collections.shuffle(myTiles, randomGen);
        Map<Tile.Kind, List<Tile>> tilesByKind = myTiles.stream().collect(Collectors.groupingBy(Tile::kind));

        return new TileDecks(
                tilesByKind.get(Tile.Kind.START),
                tilesByKind.get(Tile.Kind.NORMAL),
                tilesByKind.get(Tile.Kind.MENHIR)
        );
    }

    private static boolean placerIsCurrentPlayer(GameState state, int zoneId) {
        return state.board().tileWithId(Zone.tileId(zoneId)).placer() == state.currentPlayer();
    }

    private static void updateGameAndActions(SimpleObjectProperty<GameState> gameState,
                                             SimpleObjectProperty<List<String>> actions,
                                             ActionEncoder.StateAction stateAction
    ) {
        if (stateAction != null) {
            gameState.setValue(stateAction.state());
            List<String> actionList = new ArrayList<>(actions.getValue());
            actionList.add(stateAction.action());
            actions.setValue(actionList);
        }
    }

    private Consumer<Rotation> rotationTileToPlace(SimpleObjectProperty<Rotation> rotationTileToPlace) {
        return rotation -> rotationTileToPlace.setValue(rotationTileToPlace.getValue().add(rotation));
    }

    private Consumer<Pos> posOfTileChosen(SimpleObjectProperty<GameState> gameState,
                                          SimpleObjectProperty<List<String>> actions,
                                          SimpleObjectProperty<Rotation> rotationTileToPlace
    ) {
        return pos -> {
            GameState currentGameState = gameState.getValue();
            PlacedTile tileToAdd = new PlacedTile(
                    currentGameState.tileToPlace(),
                    currentGameState.currentPlayer(),
                    rotationTileToPlace.getValue(),
                    pos
            );
            if (currentGameState.board().canAddTile(tileToAdd)) {
                ActionEncoder.StateAction stateAction = ActionEncoder.withPlacedTile(currentGameState, tileToAdd);
                updateGameAndActions(gameState, actions, stateAction);
                rotationTileToPlace.setValue(Rotation.NONE);
            }
        };
    }

    private Consumer<Occupant> occupantChosen(SimpleObjectProperty<GameState> gameState,  SimpleObjectProperty<List<String >> actions) {
        return occupant -> {
            GameState currentGameState = gameState.getValue();
            if (currentGameState.nextAction() == GameState.Action.OCCUPY_TILE
                    && currentGameState.lastTilePotentialOccupants().contains(occupant)
            ) {
                ActionEncoder.StateAction stateAction = ActionEncoder.withNewOccupant(currentGameState, occupant);
                updateGameAndActions(gameState, actions, stateAction);
            } else if (currentGameState.nextAction() == GameState.Action.RETAKE_PAWN
                    && occupant.kind() == Occupant.Kind.PAWN
                    && placerIsCurrentPlayer(currentGameState, occupant.zoneId())
            ) {
                ActionEncoder.StateAction stateAction = ActionEncoder.withOccupantRemoved(currentGameState, occupant);
                updateGameAndActions(gameState, actions, stateAction);
            }
        };
    }

    private Consumer<String> actionEntered(SimpleObjectProperty<GameState> gameState, SimpleObjectProperty<List<String >> actions) {
        return action -> {
            if (Base32.isValid(action)) {
                ActionEncoder.StateAction stateAction = ActionEncoder.decodeAndApply(gameState.getValue(), action);
                updateGameAndActions(gameState, actions, stateAction);
            }
        };
    }

    private Consumer<Occupant> noPawnEventHandler(SimpleObjectProperty<GameState> gameState, SimpleObjectProperty<List<String >> actions) {
        return occupant -> {
            GameState currentGameState = gameState.getValue();
            ActionEncoder.StateAction stateAction = switch (currentGameState.nextAction()) {
                case OCCUPY_TILE -> ActionEncoder.withNewOccupant(currentGameState, occupant);
                case RETAKE_PAWN -> ActionEncoder.withOccupantRemoved(currentGameState, occupant);
                default -> null;
            };
            updateGameAndActions(gameState, actions, stateAction);
        };
    }

}

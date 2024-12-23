package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import ch.epfl.chacun.Tiles;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
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
    /**
     * The key for the seed parameter.
     */
    private static final String SEED_KEY = "seed";

    /**
     * The title of the window
     */
    private static final String WINDOW_TITLE = "ChaCuN";

    /**
     * The height of the window.
     */
    private static final double WINDOW_HEIGHT = 1080;

    /**
     * The width of the window.
     */
    private static final double WINDOW_WIDTH = 1440;

    /**
     * The minimum number of players
     */
    private static final int MIN_PLAYERS = 2;

    /**
     * The maximum number of players
     */
    private static final int MAX_PLAYERS = 5;

    /**
     * The main method of the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the game.
     *
     * @param primaryStage the primary stage
     * @throws Exception if an exception occurs
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parameters & Players management
        Parameters param = getParameters();
        List<String> playerNames = param.getUnnamed();
        Map<String, String> seedMap = param.getNamed();
        Map<PlayerColor, String> players = getPlayers(playerNames);

        //Initial game state management
        TileDecks myTileDecks = getShuffledTiles(seedMap);
        TextMakerFr textMaker = new TextMakerFr(players);
        GameState initialGameState = GameState.initial(players.keySet().stream().sorted().toList(), myTileDecks, textMaker);
        ObjectProperty<GameState> gameStateO = new SimpleObjectProperty<>(initialGameState);

        //Parameters for the GUI creation
        ObjectProperty<Rotation> rotationTileToPlace = new SimpleObjectProperty<>(Rotation.NONE);
        ObjectProperty<Set<Integer>> highlightedTiles = new SimpleObjectProperty<>(Set.of());
        ObjectProperty<List<String>> actions = new SimpleObjectProperty<>(List.of());
        ObservableValue<String> textToDisplayDeck = gameStateO.map(g -> switch (g.nextAction()) {
            case OCCUPY_TILE -> textMaker.clickToOccupy();
            case RETAKE_PAWN -> textMaker.clickToUnoccupy();
            default -> "";
        });

        //Root node creation (Board, Players, MessageBoard, Actions, Decks)
        BorderPane rootNode = new BorderPane();
        Node board = BoardUI.create(
                Board.REACH,
                gameStateO,
                rotationTileToPlace,
                gameStateO.map(getVisibleOccupants()),
                highlightedTiles,
                rotationConsumer(rotationTileToPlace),
                posConsumer(gameStateO, actions, rotationTileToPlace),
                occcupantConsumer(gameStateO, actions)
        );
        BorderPane rightPane = new BorderPane();
        rootNode.setCenter(board);
        rootNode.setRight(rightPane);

        //Players, MessageBoard, Actions, Decks nodes creation
        Node playersNode = PlayersUI.create(gameStateO, textMaker);
        Node messageBoardNode = MessageBoardUI.create(
                gameStateO.map(g -> g.messageBoard().messages()),
                highlightedTiles
        );
        VBox deckBox = new VBox();
        Node actionsNode = ActionUI.create(actions, actionConsumer(gameStateO, actions));
        Node deckNode = DecksUI.create(
                gameStateO.map(GameState::tileToPlace),
                gameStateO.map(g -> g.tileDecks().deckSize(Tile.Kind.NORMAL)),
                gameStateO.map(g -> g.tileDecks().deckSize(Tile.Kind.MENHIR)),
                textToDisplayDeck,
                noPawnEventHandler(gameStateO, actions)
        );
        deckBox.getChildren().addAll(actionsNode, deckNode);
        rightPane.setTop(playersNode);
        rightPane.setCenter(messageBoardNode);
        rightPane.setBottom(deckBox);

        //Scene setup
        primaryStage.setScene(new Scene(rootNode));
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.show();

        //Start the game
        gameStateO.setValue(gameStateO.getValue().withStartingTilePlaced());
    }

    /**
     * Returns a function that returns the visible occupants given a game state.
     *
     * @return a function that returns the visible occupants given a game state
     */
    private static Function<GameState, Set<Occupant>> getVisibleOccupants() {
        return g -> {
            Set<Occupant> visibleOccupantsSet = new HashSet<>(g.board().occupants());
            if (g.nextAction() == GameState.Action.OCCUPY_TILE) {
                visibleOccupantsSet.addAll(g.lastTilePotentialOccupants());
            }
            return visibleOccupantsSet;
        };
    }

    /**
     * Returns a map of players with their respective colors.
     *
     * @param playerNames the list of player names
     * @return a map of players with their respective colors
     */
    private static Map<PlayerColor, String> getPlayers(List<String> playerNames) {
        Preconditions.checkArgument(playerNames.size() >= MIN_PLAYERS && playerNames.size() <= MAX_PLAYERS);
        Map<PlayerColor, String> players = new HashMap<>();
        for (int i = 0; i < playerNames.size(); i++) {
            players.put(PlayerColor.ALL.get(i), playerNames.get(i));
        }

        return players;
    }

    /**
     * Returns a shuffled deck of tiles.
     *
     * @param seedMap the seed map
     * @return a shuffled deck of tiles
     * @throws NullPointerException if there aren't start tile or normal tile in the Tiles
     */
    private static TileDecks getShuffledTiles(Map<String, String> seedMap) {
        String seedString = seedMap.get(SEED_KEY);
        Long seed = seedString == null ? null : parseUnsignedLong(seedString);
        RandomGeneratorFactory<RandomGenerator> randomGenFactory = RandomGeneratorFactory.getDefault();
        RandomGenerator randomGen = seed == null ? randomGenFactory.create() : randomGenFactory.create(seed);

        List<Tile> myTiles = new ArrayList<>(Tiles.TILES);
        Collections.shuffle(myTiles, randomGen);
        Map<Tile.Kind, List<Tile>> tilesByKind = myTiles.stream().collect(Collectors.groupingBy(Tile::kind));

        // We make sure that the start tile is present and at least one normal tile is present
        return new TileDecks(
                tilesByKind.get(Tile.Kind.START),
                tilesByKind.get(Tile.Kind.NORMAL),
                tilesByKind.getOrDefault(Tile.Kind.MENHIR, List.of())
        );
    }

    /**
     * Returns whether the placer of the tile is the current player.
     *
     * @param state  the game state
     * @param zoneId the zone id
     * @return whether the placer of the tile is the current player
     */
    private static boolean placerIsCurrentPlayer(GameState state, int zoneId) {
        return state.board().tileWithId(Zone.tileId(zoneId)).placer() == state.currentPlayer();
    }

    /**
     * Returns whether the game state is in place tile mode.
     *
     * @param state the game state
     * @return whether the game state is in place tile mode
     */
    private static boolean isGameInGoodMode(GameState state, GameState.Action action) {
        return state.nextAction() == action;
    }

    /**
     * Updates the game state and the list of actions.
     *
     * @param gameState   the game state
     * @param actions     the list of actions
     * @param stateAction the state action
     */
    private static void updateGameAndActions(
            ObjectProperty<GameState> gameState,
            ObjectProperty<List<String>> actions,
            ActionEncoder.StateAction stateAction
    ) {
        if (stateAction != null) {
            gameState.setValue(stateAction.state());
            List<String> actionList = new ArrayList<>(actions.getValue());
            actionList.add(stateAction.action());
            actions.setValue(List.copyOf(actionList));
        }
    }

    /**
     * Returns a consumer that updates the rotation of the tile to place.
     *
     * @param rotationTileToPlace the rotation of the tile to place
     * @return a consumer that updates the rotation of the tile to place
     */
    private static Consumer<Rotation> rotationConsumer(ObjectProperty<Rotation> rotationTileToPlace) {
        // We already checked that we have clicked on an insertion position and the game is in place tile mode in BoardUI
        // as we have no info of the position of the mouse in this consumer. We could have checked if the game is in place
        // tile mode here, but it is not necessary as the variable in BoardUI that checks if we clicked on an insertion
        // position also checks that the game is in place tile mode. (Creating a new variable in BoardUI to only check
        // if we are on an insertion position would be redundant, considering other places where we need to check that
        // need to check that the game is in place tile mode as well)
        return rotation -> rotationTileToPlace.setValue(rotationTileToPlace.getValue().add(rotation));
    }

    /**
     * Returns a consumer that updates the position of the tile to place.
     *
     * @param gameState           the game state
     * @param actions             the list of actions
     * @param rotationTileToPlace the rotation of the tile to place
     * @return a consumer that updates the position of the tile to place
     */
    private static Consumer<Pos> posConsumer(
            ObjectProperty<GameState> gameState,
            ObjectProperty<List<String>> actions,
            ObjectProperty<Rotation> rotationTileToPlace
    ) {
        return pos -> {
            GameState currentGameState = gameState.getValue();
            if (isGameInGoodMode(currentGameState, GameState.Action.PLACE_TILE)) {
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
            }
        };
    }

    /**
     * Returns a consumer that updates the occupant of a tile (put or remove)
     *
     * @param gameState the game state
     * @param actions   the list of actions
     * @return a consumer that updates the occupant of a tile
     */
    private static Consumer<Occupant> occcupantConsumer(
            ObjectProperty<GameState> gameState,
            ObjectProperty<List<String>> actions
    ) {
        return occupant -> {
            GameState currentGameState = gameState.getValue();
            ActionEncoder.StateAction stateAction = null;
            if (isGameInGoodMode(currentGameState, GameState.Action.OCCUPY_TILE)
                    && currentGameState.lastTilePotentialOccupants().contains(occupant)
            ) {
                stateAction = ActionEncoder.withNewOccupant(currentGameState, occupant);
            } else if (isGameInGoodMode(currentGameState, GameState.Action.RETAKE_PAWN)
                    && occupant.kind() == Occupant.Kind.PAWN
                    && placerIsCurrentPlayer(currentGameState, occupant.zoneId())
            ) {
                stateAction = ActionEncoder.withOccupantRemoved(currentGameState, occupant);
            }
            updateGameAndActions(gameState, actions, stateAction);
        };
    }

    /**
     * Returns a consumer that updates the game state and the list of actions.
     *
     * @param gameState the game state
     * @param actions   the list of actions
     * @return a consumer that updates the game state and the list of actions
     */
    private static Consumer<String> actionConsumer(
            ObjectProperty<GameState> gameState,
            ObjectProperty<List<String>> actions
    ) {
        return action -> {
            if (Base32.isValid(action)) {
                ActionEncoder.StateAction stateAction = ActionEncoder.decodeAndApply(gameState.getValue(), action);
                updateGameAndActions(gameState, actions, stateAction);
            }
        };
    }

    /**
     * Returns a consumer that updates the game state and the list of actions when no pawn is selected.
     *
     * @param gameState the game state
     * @param actions   the list of actions
     * @return a consumer that updates the game state and the list of actions
     */
    private static Consumer<Occupant> noPawnEventHandler(
            ObjectProperty<GameState> gameState,
            ObjectProperty<List<String>> actions
    ) {
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
package ch.epfl.chacun;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Represents the state of the game
 *
 * @param players the players of the game
 * @param tileDecks the tile decks of the game
 * @param tileToPlace the tile to place
 * @param board the board of the game
 * @param nextAction the next action to do
 * @param messageBoard the message board of the game
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public record GameState(
        List<PlayerColor> players,
        TileDecks tileDecks,
        Tile tileToPlace,
        Board board,
        Action nextAction,
        MessageBoard messageBoard
) {
    /**
     * Represents the different actions that can be done in the game
     */
    public enum Action {
        START_GAME,
        PLACE_TILE,
        RETAKE_PAWN,
        OCCUPY_TILE,
        END_GAME
    }

    /**
     * Constructs a game state with the given players, tile decks, tile to place, board, next action and message board
     *
     * @param players      the players
     * @param tileDecks    the tile decks
     * @param tileToPlace  the tile to place
     * @param board        the board
     * @param nextAction   the next action
     * @param messageBoard the message board
     * @throws NullPointerException     if the players, the tile decks, the board, the next action
     *                                  or the message board are null
     * @throws IllegalArgumentException if there is less than 2 players or if the next action is PLACE_TILE
     *                                  and the tile to place is null
     */
    public GameState {
        Preconditions.checkArgument(players.size() >= 2);
        Preconditions.checkArgument(tileToPlace == null ^ nextAction == Action.PLACE_TILE);
        requireNonNull(tileDecks);
        requireNonNull(board);
        requireNonNull(nextAction);
        requireNonNull(messageBoard);
        players = List.copyOf(players);
    }

    /**
     * Returns the initial GameState with the given players and tile decks, an empty board,
     * the next action being START_GAME, and an empty message board
     *
     * @return the initial GameState with the given players and tile decks, and an empty board,
     * the next action being START_GAME, and an empty message board
     */
    public static GameState initial(List<PlayerColor> players, TileDecks tileDecks, TextMaker textMaker) {
        return new GameState(
                players,
                tileDecks,
                null,
                Board.EMPTY,
                Action.START_GAME,
                new MessageBoard(textMaker, List.of())
        );
    }

    /**
     * Returns the player whose turn it is
     *
     * @return the player whose turn it is
     */
    public PlayerColor currentPlayer() {
        return nextAction == Action.START_GAME || nextAction == Action.END_GAME ? null : players.getFirst();
    }

    /**
     * Returns the number of free occupants of the given player of the given kind
     *
     * @param player the color of the player
     * @param kind   the kind of the occupant
     * @return the number of free occupants of the given player of the given kind
     */
    public int freeOccupantsCount(PlayerColor player, Occupant.Kind kind) {
        return Occupant.occupantsCount(kind) - board.occupantCount(player, kind);
    }

    /**
     * Returns the set of the potential occupants of the last placed tile
     *
     * @return the set of the potential occupants of the last placed tile
     * @throws IllegalArgumentException if the board is empty
     */
    public Set<Occupant> lastTilePotentialOccupants() {
        PlacedTile lastPlacedTile = board.lastPlacedTile();
        Preconditions.checkArgument(lastPlacedTile != null);

        Set<Occupant> potentialOccupants = new HashSet<>(lastPlacedTile.potentialOccupants());
        potentialOccupants.removeIf(occupant ->
                freeOccupantsCount(currentPlayer(), occupant.kind()) == 0
                        || switch (lastPlacedTile.zoneWithId(occupant.zoneId())) {
                    case Zone.Forest forest -> board.forestArea(forest).isOccupied();
                    case Zone.Meadow meadow -> board.meadowArea(meadow).isOccupied();
                    case Zone.River river when occupant.kind() == Occupant.Kind.PAWN ->
                            board.riverArea(river).isOccupied();
                    case Zone.Water water -> board.riverSystemArea(water).isOccupied();
                }
        );

        return potentialOccupants;
    }

    /**
     * Returns the GameState with the starting tile placed
     *
     * @return the GameState with the starting tile placed
     * @throws IllegalArgumentException if the next action is not START_GAME
     */
    public GameState withStartingTilePlaced() {
        Preconditions.checkArgument(nextAction == Action.START_GAME);

        return new GameState(
                players,
                tileDecks.withTopTileDrawn(Tile.Kind.START).withTopTileDrawn(Tile.Kind.NORMAL),
                tileDecks.topTile(Tile.Kind.NORMAL),
                board.withNewTile(
                        new PlacedTile(
                                tileDecks.topTile(Tile.Kind.START),
                                null,
                                Rotation.NONE,
                                Pos.ORIGIN
                        )
                ),
                Action.PLACE_TILE,
                messageBoard
        );
    }

    /**
     * Returns the GameState with the given tile placed, points for immediate scoring added, and the next action to do
     *
     * @param placedTile the tile to place
     * @return the GameState with the given tile placed, points for immediate scoring added, and the next action to do
     * @throws IllegalArgumentException if the next action is not PLACE_TILE or if the tile is already occupied
     */
    public GameState withPlacedTile(PlacedTile placedTile) {
        Preconditions.checkArgument(nextAction == Action.PLACE_TILE && placedTile.occupant() == null);

        Board newBoard = board.withNewTile(placedTile);
        MessageBoard newMessageBoard = messageBoard;

        Zone specialPowerZone = placedTile.specialPowerZone();
        switch (specialPowerZone) {
            case Zone z when z.specialPower() == Zone.SpecialPower.SHAMAN -> {
                boolean canRemoveOccupant = newBoard.occupantCount(currentPlayer(), Occupant.Kind.PAWN) > 0;
                GameState newGameState = new GameState(
                        players,
                        tileDecks,
                        null,
                        newBoard,
                        canRemoveOccupant ? Action.RETAKE_PAWN : Action.OCCUPY_TILE,
                        messageBoard
                );
                return canRemoveOccupant ? newGameState : newGameState.withTurnFinishedIfOccupationImpossible();
            }
            case Zone z when z.specialPower() == Zone.SpecialPower.LOGBOAT ->
                    newMessageBoard = newMessageBoard.withScoredLogboat(
                            currentPlayer(),
                            newBoard.riverSystemArea((Zone.Water) specialPowerZone)
                    );
            case Zone z when z.specialPower() == Zone.SpecialPower.HUNTING_TRAP -> {
                Area<Zone.Meadow> adjacentMeadow = newBoard.adjacentMeadow(
                        placedTile.pos(),
                        (Zone.Meadow) specialPowerZone
                );
                Set<Animal> deersToCancel = getSimpleCancelledDeers(adjacentMeadow);
                newMessageBoard = newMessageBoard.withScoredHuntingTrap(currentPlayer(), adjacentMeadow, deersToCancel);
                newBoard = newBoard.withMoreCancelledAnimals(Area.animals(adjacentMeadow, Set.of()));
            }
            case null, default -> {
            }
        }

        return new GameState(
                players,
                tileDecks,
                null,
                newBoard,
                Action.OCCUPY_TILE,
                newMessageBoard
        ).withTurnFinishedIfOccupationImpossible();
    }

    /**
     * Returns the GameState with the given occupant removed if the occupant isn't null
     *
     * @param occupant the occupant to remove
     * @return the GameState with the given occupant removed if the occupant isn't null
     */
    public GameState withOccupantRemoved(Occupant occupant) {
        Preconditions.checkArgument(nextAction == Action.RETAKE_PAWN
                && (occupant == null || occupant.kind() == Occupant.Kind.PAWN)
        );

        return new GameState(
                players,
                tileDecks,
                null,
                occupant == null ? board : board.withoutOccupant(occupant),
                Action.OCCUPY_TILE,
                messageBoard
        ).withTurnFinishedIfOccupationImpossible();
    }

    /**
     * Returns the GameState with the given occupant added
     *
     * @param occupant the occupant to add
     * @return the GameState with the given occupant added
     */
    public GameState withNewOccupant(Occupant occupant) {
        Preconditions.checkArgument(nextAction == Action.OCCUPY_TILE);

        return new GameState(
                players,
                tileDecks,
                null,
                occupant == null ? board : board.withOccupant(occupant),
                nextAction,
                messageBoard
        ).withTurnFinished();
    }

    /**
     * Check if the occupation is possible, and if not finish the turn
     *
     * @return the GameState with the turn finished or the next action being OCCUPY_TILE
     */
    private GameState withTurnFinishedIfOccupationImpossible() {
        return lastTilePotentialOccupants().isEmpty() ? withTurnFinished() : this;
    }

    /**
     * Returns the GameState with the turn finished
     *
     * @return the GameState with the turn finished
     */
    private GameState withTurnFinished() {
        MessageBoard newMessageBoard = messageBoard;

        //Points of closed rivers
        Set<Area<Zone.River>> riversArea = board.riversClosedByLastTile();
        for (Area<Zone.River> river : riversArea) {
            newMessageBoard = newMessageBoard.withScoredRiver(river);
        }

        //Points of closed forests
        Set<Area<Zone.Forest>> forestsArea = board.forestsClosedByLastTile();
        Area<Zone.Forest> forestAreaWithMenhir = null;
        for (Area<Zone.Forest> forestArea : forestsArea) {
            newMessageBoard = newMessageBoard.withScoredForest(forestArea);
            if (Area.hasMenhir(forestArea)) {
                forestAreaWithMenhir = forestArea;
            }
        }
        Board newBoard = board.withoutGatherersOrFishersIn(forestsArea, riversArea);

        //Second turn management
        boolean playerCanPlaySecondTurn = forestAreaWithMenhir != null
                && newBoard.lastPlacedTile().kind() == Tile.Kind.NORMAL;

        TileDecks newTileDecks = tileDecks.withTopTileDrawnUntil(
                playerCanPlaySecondTurn ? Tile.Kind.MENHIR : Tile.Kind.NORMAL,
                newBoard::couldPlaceTile
        );
        if (playerCanPlaySecondTurn && newTileDecks.deckSize(Tile.Kind.MENHIR) == 0) {
            newTileDecks = newTileDecks.withTopTileDrawnUntil(Tile.Kind.NORMAL, newBoard::couldPlaceTile);
            playerCanPlaySecondTurn = false;
        }

        //Next turn & end of the game management
        List<PlayerColor> newPlayers = new LinkedList<>(players);
        Tile tileToPlay;
        if (playerCanPlaySecondTurn) {
            newMessageBoard = newMessageBoard.withClosedForestWithMenhir(
                    currentPlayer(),
                    forestAreaWithMenhir
            );
            tileToPlay = newTileDecks.topTile(Tile.Kind.MENHIR);
            newTileDecks = newTileDecks.withTopTileDrawn(Tile.Kind.MENHIR);
        } else {
            tileToPlay = newTileDecks.topTile(Tile.Kind.NORMAL);
            if (tileToPlay == null)
                return new GameState(
                        newPlayers,
                        newTileDecks,
                        null,
                        newBoard,
                        Action.END_GAME,
                        newMessageBoard
                ).withFinalPointsCounted();
            newTileDecks = newTileDecks.withTopTileDrawn(Tile.Kind.NORMAL);
            Collections.rotate(newPlayers, -1);
        }

        return new GameState(
                newPlayers,
                newTileDecks,
                tileToPlay,
                newBoard,
                Action.PLACE_TILE,
                newMessageBoard
        );
    }

    /**
     * Returns the GameState with the final points counted and messages added
     *
     * @return the GameState with the final points counted and messages added
     */
    private GameState withFinalPointsCounted() {
        Board newBoard = board;
        MessageBoard newMessageBoard = messageBoard;

        //Points of the meadows
        for (Area<Zone.Meadow> meadowArea : board.meadowAreas()) {
            Zone.Meadow zonePitTrap = (Zone.Meadow) meadowArea.zoneWithSpecialPower(Zone.SpecialPower.PIT_TRAP);
            boolean isThereFire = meadowArea.zoneWithSpecialPower(Zone.SpecialPower.WILD_FIRE) != null;
            if (!isThereFire) {
                Set<Animal> cancelledAnimals = zonePitTrap == null
                        ? getSimpleCancelledDeers(meadowArea)
                        : getFurthestCancelledDeers(zonePitTrap);
                newBoard = newBoard.withMoreCancelledAnimals(cancelledAnimals);
            }
            newMessageBoard = newMessageBoard.withScoredMeadow(meadowArea, newBoard.cancelledAnimals());
            if (zonePitTrap != null) {
                Pos posPitTrap = newBoard.tileWithId(zonePitTrap.tileId()).pos();
                Area<Zone.Meadow> area = newBoard.adjacentMeadow(posPitTrap, zonePitTrap);
                newMessageBoard = newMessageBoard.withScoredPitTrap(area, newBoard.cancelledAnimals());
            }
        }

        //Points of the river systems
        for (Area<Zone.Water> riverSystemArea : board.riverSystemAreas()) {
            Zone.Water zoneWithRaft = (Zone.Water) riverSystemArea.zoneWithSpecialPower(Zone.SpecialPower.RAFT);
            newMessageBoard = newMessageBoard.withScoredRiverSystem(riverSystemArea);
            if (zoneWithRaft != null) {
                newMessageBoard = newMessageBoard.withScoredRaft(riverSystemArea);
            }
        }

        //Winners management
        int maxPoints = newMessageBoard.points().values().stream().max(Integer::compare).orElse(0);
        Set<PlayerColor> winners = newMessageBoard.points().entrySet().stream()
                .filter(entry -> entry.getValue() == maxPoints)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        newMessageBoard = newMessageBoard.withWinners(winners, maxPoints);

        return new GameState(
                players,
                tileDecks,
                null,
                newBoard,
                nextAction,
                newMessageBoard
        );
    }

    /**
     * Returns the set of the deers to cancel in the given area without taking into account the distance of the deers
     *
     * @param meadowArea the meadow area to check
     * @return the set of the deers to cancel in the given area without taking into account the distance of the deers
     */
    private Set<Animal> getSimpleCancelledDeers(Area<Zone.Meadow> meadowArea) {
        Set<Animal> areaNotCancelledAnimals = Area.animals(meadowArea, board.cancelledAnimals());
        int numberOfSmilodons = getAnimalSet(areaNotCancelledAnimals, Animal.Kind.TIGER).size();

        return getAnimalSet(areaNotCancelledAnimals, Animal.Kind.DEER).stream()
                .limit(numberOfSmilodons).collect(Collectors.toSet());
    }

    /**
     * Returns the set of the furthest deers from the given meadow to cancel in the meadow area
     *
     * @param pitTrapMeadow the pit trap meadow
     * @return the set of the furthest deers from the given meadow to cancel in the meadow area
     */
    private Set<Animal> getFurthestCancelledDeers(Zone.Meadow pitTrapMeadow) {
        Area<Zone.Meadow> meadowArea = board.meadowArea(pitTrapMeadow);
        Pos posOfCentralMeadow = board.tileWithId(pitTrapMeadow.tileId()).pos();
        Area<Zone.Meadow> adjacentMeadowArea = board.adjacentMeadow(posOfCentralMeadow, pitTrapMeadow);

        Set<Animal> areaNotCancelledAnimals = Area.animals(meadowArea, board.cancelledAnimals());
        Set<Animal> insideAreaNotCancelledAnimals = Area.animals(adjacentMeadowArea, board.cancelledAnimals());

        int numberOfSmilodons = getAnimalSet(areaNotCancelledAnimals, Animal.Kind.TIGER).size();
        Set<Animal> deersInAdjacentArea = getAnimalSet(insideAreaNotCancelledAnimals, Animal.Kind.DEER);
        areaNotCancelledAnimals.removeAll(deersInAdjacentArea);
        Set<Animal> deersOutsideAdjacentArea = getAnimalSet(areaNotCancelledAnimals, Animal.Kind.DEER);

        return Stream.concat(deersOutsideAdjacentArea.stream(), deersInAdjacentArea.stream()).limit(numberOfSmilodons)
                .collect(Collectors.toSet());
    }

    /**
     * Returns the set of the animals of the given kind in the given set of animals
     *
     * @param animals the set of animals
     * @param kind    the kind of the animals
     * @return the set of the animals of the given kind in the given set of animals
     */
    private static Set<Animal> getAnimalSet(Set<Animal> animals, Animal.Kind kind) {
        return animals.stream().filter(animal -> animal.kind() == kind).collect(Collectors.toSet());
    }
}

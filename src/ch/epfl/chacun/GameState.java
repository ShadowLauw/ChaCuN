package ch.epfl.chacun;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Represents the state of the game
 * @param players
 * @param tileDecks
 * @param tileToPlace
 * @param board
 * @param nextAction
 * @param messageBoard
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public record GameState(List<PlayerColor> players, TileDecks tileDecks, Tile tileToPlace, Board board, Action nextAction, MessageBoard messageBoard) {

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
     * @throws NullPointerException if the players, the tile decks, the board, the next action or the message board are null
     * @throws IllegalArgumentException if the players list is empty
     */
    public GameState {
        Preconditions.checkArgument(players.size() > 1);
        //use of a linkedList because we are going to remove the first player and add him at the end of the list
        players = new LinkedList<>(players);
        requireNonNull(tileDecks);
        requireNonNull(board);
        requireNonNull(nextAction);
        requireNonNull(messageBoard);
    }

    /**
     * Returns the GameState with the starting tile placed
     *
     * @return the GameState with the starting tile placed
     */
    public static GameState initial(List<PlayerColor> players, TileDecks tileDecks, TextMaker textMaker) {
        return new GameState(players, tileDecks, null, Board.EMPTY, Action.START_GAME, new MessageBoard(textMaker, List.of()));
    }

    /**
     * Returns the player whose turn it is
     *
     * @return the player whose turn it is
     */
    public PlayerColor currentPlayer() {
        return players.get(0);
    }
    /**
     * Returns the number of free occupants of the given kind and color
     *
     * @param player the color of the player
     * @param kind   the kind of the occupant
     * @return the number of free occupants of the given kind and color
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
        Preconditions.checkArgument(board.equals(Board.EMPTY));
        return board.lastPlacedTile().potentialOccupants();
    }

    /**
     * Returns the GameState with the starting tile placed
     *
     * @return the GameState with the starting tile placed
     */
    public GameState withStartingTilePlaced() {
        return new GameState(
                players,
                tileDecks.withTopTileDrawn(Tile.Kind.START),
                //there is no problem, as all the tiles can be placed around the starting tile
                tileDecks.topTile(Tile.Kind.NORMAL),
                //temporary solution for the start tile...
                board.withNewTile(new PlacedTile(tileDecks.topTile(Tile.Kind.START), null, Rotation.NONE, new Pos(0, 0))),
                Action.PLACE_TILE,
                messageBoard
        );
    }

    public GameState withPlacedTile(PlacedTile placedTile) {

        //VAUT MIEUX S'OCCUPER DANS LA METHODE PRIVEE DE LA FIN DE GAME JUSTE



        //TO DO LAURA
        //(je crois qu'elle est bien fat celle là, genre c la pire de toutes)

        //JE VAIS ESSAYER DE FAIRE DES COMMENTAIRES POUR ECLAICIR LE TRUC

        //D'abord, ajouter la tuile au plateau

        //Ensuite, on vérifie les différentes zones qui se sont fermées
        //avec des vieux if else dégueulasse mais pas trop le choix

        //si une forêt est fermée :
        // - vérifier si elle a pas de menhir, si oui la prochaine action est de placer une tuile
        // - vérifier si c'était pas une tuile avec un pouvoir spécial, auquel cas faut gérer

        // - vérifier les différentes zones fermées, et agir en fonction, notamment
        // -en affichant un message
        // - en restituant les pions aux joueurs concernés

        // - Ensuite, vérifier si c'est la fin du tour

        // - Enfin, on retourne le nouveau GameState

        // Tout est plus ou moins bien expliqué dans la partie 4 des conseils de programmation.

        //Ah aussi faut vérifier que la prochaine tuile est jouable LOL.
        return null;
    }

    /**
     * Returns the GameState with the given occupant removed
     *
     * @param occupant the occupant to remove
     * @return the GameState with the given occupant removed
     */
    public GameState withOccupantRemoved(Occupant occupant) {
        Preconditions.checkArgument(nextAction == Action.RETAKE_PAWN);
        Preconditions.checkArgument(occupant == null || occupant.kind().equals(Occupant.Kind.PAWN));
        if(occupant == null) return this;

        //we go directly to the occupy tile action, and inside we will check if the turn is finished because there is nothing to do
        return new GameState(
                players,
                tileDecks,
                tileToPlace,
                board.withoutOccupant(occupant),
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
        if(occupant == null) return this;
        return new GameState(
                players,
                tileDecks,
                tileToPlace,
                board.withOccupant(occupant),
                Action.OCCUPY_TILE,
                messageBoard
        ).withTurnFinishedIfOccupationImpossible();
    }

    /**
     * Check if the occupation is possible, and if not finish the turn
     *
     * @return the GameState with the turn finished
     */
    private GameState withTurnFinishedIfOccupationImpossible() {
        //ça dépend de comment tu choisis d'implémenter PlaceTile, mais je pense que

        //no possibility of placing any occupant
        if(board.lastPlacedTile().potentialOccupants().isEmpty()) return this.withTurnFinished();
        if(freeOccupantsCount(currentPlayer(), Occupant.Kind.PAWN) == 0 && freeOccupantsCount(currentPlayer(), Occupant.Kind.HUT) == 0) return this.withTurnFinished();

        return this;
    }

    private GameState withTurnFinished() {
        return null;
    }
}

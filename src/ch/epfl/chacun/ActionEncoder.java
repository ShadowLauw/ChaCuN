package ch.epfl.chacun;

import java.util.*;

/**
 * Utility class to encode and decode actions in Base32, and apply them to the game state.
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class ActionEncoder {
    /**
     * The number of bits to shift to encode the position of the tile.
     */
    private static final int POS_TILE_SHIFT = 2;

    /**
     * The number of bits to shift to encode the kind of occupant.
     */
    private static final int OCCUPANT_KIND_SHIFT = 4;

    /**
     * The value to represent no occupant.
     */
    private static final int NO_OCCUPANT = 0b11111;

    /**
     * The mask to get the rotation of the tile.
     */
    private static final int MASK_ROTATION = 0b11;

    /**
     * The mask to get the zone local ID of the occupant.
     */
    private static final int MASK_ZONE_OCCUPANT = 0b1111;

    /**
     * The length of the action to place a tile.
     */
    private static final int LENGTH_PLACE_TILE = 2;

    /**
     * The length of the action to occupy a tile.
     */
    private static final int LENGTH_OCCUPY_TILE = 1;

    /**
     * The length of the action to retake a pawn.
     */
    private static final int LENGTH_RETAKE_PAWN = 1;

    /**
     * Private constructor to prevent instantiation.
     */
    private ActionEncoder() {
    }

    /**
     * Encodes the action of placing a tile, and updates the game state accordingly.
     *
     * @param state the game state
     * @param tile  the tile to place
     * @return a StateAction containing the new game state and the encoded action
     */
    public static StateAction withPlacedTile(GameState state, PlacedTile tile) {
        int encodedAction = (posSorter(state).indexOf(tile.pos()) << POS_TILE_SHIFT) | tile.rotation().ordinal();

        return new StateAction(state.withPlacedTile(tile), Base32.encodeBits10(encodedAction));
    }

    /**
     * Encodes the action of occupying a tile, and updates the game state accordingly. If the occupant is null, then it
     * is assumed that no occupant will be placed.
     *
     * @param state    the game state
     * @param occupant the occupant to place
     * @return a StateAction containing the new game state and the encoded action
     */
    public static StateAction withNewOccupant(GameState state, Occupant occupant) {
        int encodedAction = occupant == null
                ? NO_OCCUPANT
                : occupant.kind().ordinal() << OCCUPANT_KIND_SHIFT | Zone.localId(occupant.zoneId());

        return new StateAction(state.withNewOccupant(occupant), Base32.encodeBits5(encodedAction));
    }

    /**
     * Encodes the action of retaking a pawn, and updates the game state accordingly. If the occupant is null, then it
     * is assumed that no occupant will be removed.
     *
     * @param state    the game state
     * @param occupant the occupant to remove
     * @return a StateAction containing the new game state and the encoded action
     */
    public static StateAction withOccupantRemoved(GameState state, Occupant occupant) {
        int encodedAction = occupant == null ? NO_OCCUPANT : pawnsSorter(state).indexOf(occupant);

        return new StateAction(state.withOccupantRemoved(occupant), Base32.encodeBits5(encodedAction));
    }

    /**
     * Decodes an action and applies it to the game state.
     *
     * @param state  the game state
     * @param action the action to decode and apply
     * @return a StateAction containing the new game state and the decoded action, or null if the action is invalid
     */
    public static StateAction decodeAndApply(GameState state, String action) {
        try {
            return dAAHandler(state, action);
        } catch (ActionException e) {
            return null;
        }
    }

    /**
     * Decodes an action and applies it to the game state.
     *
     * @param state  the game state
     * @param action the action to decode and apply
     * @return a StateAction containing the new game state and the decoded action
     * @throws IllegalArgumentException if the action is invalid
     */
    private static StateAction dAAHandler(GameState state, String action) throws ActionException {
        if (!Base32.isValid(action)) throw new ActionException();

        int decoded = Base32.decode(action);

        return switch (state.nextAction()) {
            case PLACE_TILE -> {
                int indexTile = decoded >> POS_TILE_SHIFT;
                int rotation = decoded & MASK_ROTATION;
                List<Pos> insertionPositions = posSorter(state);

                checkStringLength(action, LENGTH_PLACE_TILE);
                checkListSize(insertionPositions, indexTile);

                PlacedTile tile = new PlacedTile(
                        state.tileToPlace(),
                        state.currentPlayer(),
                        Rotation.ALL.get(rotation),
                        insertionPositions.get(indexTile)
                );
                checkCanAddTile(state, tile);

                yield new StateAction(state.withPlacedTile(tile), action);
            }
            case OCCUPY_TILE -> {
                if (decoded == NO_OCCUPANT) {
                    yield new StateAction(state.withNewOccupant(null), action);
                }
                int zoneLocalId = decoded & MASK_ZONE_OCCUPANT;
                int kind = decoded >> OCCUPANT_KIND_SHIFT;

                checkStringLength(action, LENGTH_OCCUPY_TILE);

                Occupant occupant = state.lastTilePotentialOccupants().stream()
                        .filter(o -> o.kind().ordinal() == kind)
                        .filter(o -> Zone.localId(o.zoneId()) == zoneLocalId)
                        .findFirst()
                        .orElseThrow(ActionException::new);

                yield new StateAction(state.withNewOccupant(occupant), action);
            }
            case RETAKE_PAWN -> {
                if (decoded == NO_OCCUPANT) {
                    yield new StateAction(state.withOccupantRemoved(null), action);
                }
                List<Occupant> pawns = pawnsSorter(state);

                checkStringLength(action, LENGTH_RETAKE_PAWN);
                checkListSize(pawns, decoded);

                Occupant pawn = pawns.get(decoded);
                checkOccupantIsOwnedByCurrentPlayer(state, pawn);

                yield new StateAction(state.withOccupantRemoved(pawn), action);
            }
            default -> throw new ActionException();
        };
    }

    /**
     * Checks if the length of a string is equal to a given length.
     *
     * @param action the string to check
     * @param length the length to compare
     * @throws ActionException if the length is not equal to the given length
     */
    private static void checkStringLength(String action, int length) throws ActionException {
        if (action.length() != length) throw new ActionException();
    }

    /**
     * Checks if the index is within the bounds of a list.
     *
     * @param list  the list to check
     * @param index the index to check
     * @throws ActionException if the index is out of bounds
     */
    private static void checkListSize(List<?> list, int index) throws ActionException {
        if (index >= list.size()) throw new ActionException();
    }

    /**
     * Checks if the occupant is owned by the current player.
     *
     * @param state    the game state
     * @param occupant the occupant to check
     * @throws ActionException if the occupant is not owned by the current player
     */
    private static void checkOccupantIsOwnedByCurrentPlayer(GameState state, Occupant occupant) throws ActionException {
        if (state.currentPlayer() != state.board().tileWithId(Zone.tileId(occupant.zoneId())).placer())
            throw new ActionException();
    }

    /**
     * Checks if a tile can be added to the board.
     *
     * @param state the game state
     * @param tile  the tile to add
     * @throws IllegalArgumentException if the tile cannot be added
     */
    private static void checkCanAddTile(GameState state, PlacedTile tile) throws ActionException {
        if (!state.board().canAddTile(tile)) throw new ActionException();
    }

    /**
     * Sorts the insertion positions of a game state, first by x, then by y in ascending order.
     *
     * @param state the game state
     * @return a sorted list of insertion positions
     */
    private static List<Pos> posSorter(GameState state) {
        List<Pos> insertionPositions = new ArrayList<>(state.board().insertionPositions());
        insertionPositions.sort((p1, p2) -> p1.x() == p2.x()
                ? Integer.compare(p1.y(), p2.y())
                : Integer.compare(p1.x(), p2.x())
        );
        return insertionPositions;
    }

    /**
     * Sorts the pawns of a game state by zone ID in ascending order.
     *
     * @param state the game state
     * @return a sorted list of pawn occupants
     */
    private static List<Occupant> pawnsSorter(GameState state) {
        List<Occupant> occupants = new ArrayList<>(state.board().occupants());
        return occupants.stream()
                .filter(o -> o.kind() == Occupant.Kind.PAWN)
                .sorted(Comparator.comparingInt(Occupant::zoneId))
                .toList();
    }

    /**
     * Represents a pair of a state and a Base32 encoded action.
     *
     * @param state  the game state
     * @param action the Base32 encoded action
     */
    public record StateAction(GameState state, String action) {
    }

    /**
     * Represents an exception thrown when an action is invalid.
     */
    private static class ActionException extends Exception {}
}

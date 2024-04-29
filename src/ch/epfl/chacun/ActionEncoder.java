package ch.epfl.chacun;

import java.util.*;

public final class ActionEncoder {

    private static final int POS_TILE_SHIFT = 2;
    private static final int OCCUPANT_KIND_SHIFT = 4;
    private static final int NO_OCCUPANT = 0b11111;
    private static final int MASK_ROTATION = 0b11;
    private static final int MASK_ZONE_OCCUPANT = 0b1111;
    private static final int LENGTH_PLACE_TILE = 2;
    private static final int LENGTH_OCCUPY_TILE = 1;
    private static final int LENGTH_RETAKE_PAWN = 1;
    private static final int ZONE_LOCAL_ID_DIVIDER = 10;

    private ActionEncoder() {}

    public static StateAction withPlacedTile(GameState state, PlacedTile tile) {
        int encodedAction = (posSorter(state).indexOf(tile.pos()) << POS_TILE_SHIFT) | tile.rotation().ordinal();

        return new StateAction(state.withPlacedTile(tile), Base32.encodeBits10(encodedAction));
    }

    public static StateAction withNewOccupant(GameState state, Occupant occupant) {
        int encodedAction = occupant == null
                ? NO_OCCUPANT
                : occupant.kind().ordinal() << OCCUPANT_KIND_SHIFT | occupant.zoneId();

        return new StateAction(state.withNewOccupant(occupant), Base32.encodeBits5(encodedAction));
    }

    public static StateAction withOccupantRemoved(GameState state, Occupant occupant) {
        int encodedAction = occupant == null ? NO_OCCUPANT : occupantsSorter(state).indexOf(occupant);

        return new StateAction(state.withOccupantRemoved(occupant), Base32.encodeBits5(encodedAction));
    }

    public static StateAction decodeAndApply(GameState state, String action) {
        try {
            return dAAHandler(state, action);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static StateAction dAAHandler(GameState state, String action) {
        if (!Base32.isValid(action)) throw new IllegalArgumentException();

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
                if (state.board().canAddTile(tile))
                    yield new StateAction(state.withPlacedTile(tile), action);

                throw new IllegalArgumentException();
            }
            case OCCUPY_TILE -> {
                if (decoded == NO_OCCUPANT) {
                    yield new StateAction(state.withOccupantRemoved(null), action);
                }
                int zoneLocalId = decoded & MASK_ZONE_OCCUPANT;
                int kind = decoded >> OCCUPANT_KIND_SHIFT;

                checkStringLength(action, LENGTH_OCCUPY_TILE);

                Occupant occupant = state.lastTilePotentialOccupants().stream()
                        .filter(o -> o.kind().ordinal() == kind)
                        .filter(o -> o.zoneId() % ZONE_LOCAL_ID_DIVIDER == zoneLocalId)
                        .findFirst()
                        .orElseThrow(IllegalArgumentException::new);

                yield new StateAction(state.withNewOccupant(occupant), action);
            }
            case RETAKE_PAWN -> {
                if (decoded == NO_OCCUPANT) {
                    yield new StateAction(state.withOccupantRemoved(null), action);
                }
                List<Occupant> occupants = occupantsSorter(state);

                checkStringLength(action, LENGTH_RETAKE_PAWN);
                checkListSize(occupants, decoded);

                Occupant occupant = occupants.get(decoded);
                yield new StateAction(state.withOccupantRemoved(occupant), action);
            }
            default -> throw new IllegalArgumentException();
        };
    }

    private static void checkStringLength(String action, int length) {
        if (action.length() != length) throw new IllegalArgumentException();
    }

    private static void checkListSize(List<?> list, int index) {
        if (index >= list.size()) throw new IllegalArgumentException();
    }

    private static List<Pos> posSorter(GameState state) {
        List<Pos> insertionPositions = new ArrayList<>(state.board().insertionPositions());
        insertionPositions.sort((p1, p2) -> p1.x() == p2.x()
                ? Integer.compare(p1.y(), p2.y())
                : Integer.compare(p1.x(), p2.x())
        );
        return insertionPositions;
    }

    private static List<Occupant> occupantsSorter(GameState state) {
        List<Occupant> occupants = new ArrayList<>(state.board().occupants());
        occupants.sort(Comparator.comparingInt(Occupant::zoneId));
        return occupants;
    }

    public record StateAction(GameState state, String action) {}

}


package ch.epfl.chacun;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the board of the game
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */

public final class Board {
    /**
     * The reach of the board
     */
    public static final int REACH = 12;

    /**
     * The maximum size of the board
     */
    private static final int BOARD_MAX_SIZE = 625;

    /**
     * The empty board
     */
    public static final Board EMPTY = new Board(
            new PlacedTile[BOARD_MAX_SIZE],
            new int[0],
            ZonePartitions.EMPTY,
            Set.of()
    );

    /**
     * The width of the board
     */
    private static final int WIDTH = 25;

    /**
     * The Array of the placed tiles
     */
    private final PlacedTile[] placedTiles;

    /**
     * The Array of the index of the tiles (in the order they were placed)
     */
    private final int[] tileIndex;

    /**
     * The zone partitions of the board
     */
    private final ZonePartitions zonePartitions;

    /**
     * The set of the animals that have been cancelled
     */
    private final Set<Animal> cancelledAnimals;

    /**
     * Constructs a board with the given placed tiles, tile index, zone partitions and cancelled animals
     *
     * @param placedTiles      the placed tiles
     * @param tileIndex        the index of the tiles
     * @param zonePartitions   the zone partitions
     * @param cancelledAnimals the cancelled animals
     */
    private Board(PlacedTile[] placedTiles, int[] tileIndex, ZonePartitions zonePartitions, Set<Animal> cancelledAnimals) {
        this.placedTiles = placedTiles;
        this.tileIndex = tileIndex;
        this.zonePartitions = zonePartitions;
        this.cancelledAnimals = cancelledAnimals;
    }

    /**
     * Gives the PlacedTile at the position pos on the board
     *
     * @param pos the position of the tile
     * @return the tile at the position pos on the board or null if there is no tile at this position
     * @throws IllegalArgumentException if the position is not in the board perimeter
     */
    public PlacedTile tileAt(Pos pos) {
        return isPosInBoard(pos) ? placedTiles[getIndexOfTile(pos)] : null;
    }

    /**
     * Gives the PlacedTile with the given id
     *
     * @param tileId the id of the tile
     * @return the tile with the given id
     * @throws IllegalArgumentException if there is no tile with the given id
     */
    public PlacedTile tileWithId(int tileId) {
        for (int i : tileIndex) {
            PlacedTile tile = placedTiles[i];
            if (tile.id() == tileId) {
                return tile;
            }
        }

        throw new IllegalArgumentException();
    }

    /**
     * Gives the set of cancelled Animals
     *
     * @return the set of cancelled animals
     */
    public Set<Animal> cancelledAnimals() {
        return cancelledAnimals;
    }

    /**
     * Gives the set of occupants on the board
     *
     * @return the set of occupants on the board
     */
    public Set<Occupant> occupants() {
        Set<Occupant> occupants = new HashSet<>();
        for (int i : tileIndex) {
            Occupant occupant = placedTiles[i].occupant();
            if (occupant != null) {
                occupants.add(occupant);
            }
        }
        return occupants;
    }

    /**
     * Gives the Area where the given forest is
     *
     * @param forest the forest
     * @return the area where the forest is
     * @throws IllegalArgumentException if the forest is not in any area of the partition
     */
    public Area<Zone.Forest> forestArea(Zone.Forest forest) {
        return zonePartitions.forests().areaContaining(forest);
    }

    /**
     * Gives the Area where the given meadow is
     * Area<> area = board
     *
     * @param meadow the meadow
     * @return the area where the meadow is
     * @throws IllegalArgumentException if the meadow is not in any area of the partition
     */
    public Area<Zone.Meadow> meadowArea(Zone.Meadow meadow) {
        return zonePartitions.meadows().areaContaining(meadow);
    }

    /**
     * Gives the Area where the given river is
     *
     * @param river the river
     * @return the area where the river is
     * @throws IllegalArgumentException if the river is not in any area of the partition
     */
    public Area<Zone.River> riverArea(Zone.River river) {
        return zonePartitions.rivers().areaContaining(river);
    }

    /**
     * Gives the Area where the given water zone is
     *
     * @param water the water
     * @return the area where the water zone is
     * @throws IllegalArgumentException if the water zone is not in any area of the partition
     */
    public Area<Zone.Water> riverSystemArea(Zone.Water water) {
        return zonePartitions.riverSystems().areaContaining(water);
    }

    /**
     * Gives all the meadow areas of the board
     *
     * @return the set of all the meadow areas of the board
     */
    public Set<Area<Zone.Meadow>> meadowAreas() {
        return zonePartitions.meadows().areas();
    }

    /**
     * Gives all the water areas of the board
     *
     * @return the set of all the water areas of the board
     */
    public Set<Area<Zone.Water>> riverSystemAreas() {
        return zonePartitions.riverSystems().areas();
    }

    /**
     * Gives the adjacent meadows of the given meadow
     *
     * @param pos        the position fof the given meadow
     * @param meadowZone the meadow zone
     * @return the area of the adjacent meadows
     */
    public Area<Zone.Meadow> adjacentMeadow(Pos pos, Zone.Meadow meadowZone) {
        Area<Zone.Meadow> areaOfTheZone = meadowArea(meadowZone);
        Set<Zone.Meadow> meadowsInArea = areaOfTheZone.zones();
        Set<Zone.Meadow> adjacentZones = new HashSet<>();

        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                PlacedTile tile = tileAt(pos.translated(dx, dy));
                if (tile != null) {
                    tile.meadowZones().stream()
                            .filter(meadowsInArea::contains)
                            .forEach(adjacentZones::add);
                }
            }
        }

        return new Area<>(adjacentZones, areaOfTheZone.occupants(), 0);
    }

    /**
     * Gives the number of the given occupant placed by the given player on the board
     *
     * @param player       the color of the player
     * @param occupantKind the kind of the occupant
     * @return the number of the given occupant kind placed by the player on the board
     */
    public int occupantCount(PlayerColor player, Occupant.Kind occupantKind) {
        return (int) occupants().stream()
                .filter(occupant -> tileFromOccupant(occupant).placer() == player && occupant.kind() == occupantKind)
                .count();
    }

    /**
     * Gives the set of the positions where a tile can be placed
     *
     * @return the set of the positions where a tile can be placed
     */
    public Set<Pos> insertionPositions() {
        Set<Pos> positions = new HashSet<>();
        for (int index : tileIndex) {
            Pos pos = placedTiles[index].pos();
            for (Direction direction : Direction.ALL) {
                Pos posToTest = pos.neighbor(direction);
                if (isPosInBoard(posToTest) && tileAt(posToTest) == null) {
                    positions.add(posToTest);
                }
            }
        }

        return positions;
    }

    /**
     * Gives the last placed Tile
     *
     * @return the last placed tile or null if there is no tile on the board
     */
    public PlacedTile lastPlacedTile() {
        return tileIndex.length == 0 ? null : placedTiles[tileIndex[tileIndex.length - 1]];
    }

    /**
     * Gives the set of the forests that are closed by the last placed tile
     *
     * @return the set of the forests that are closed by the last placed tile
     */
    public Set<Area<Zone.Forest>> forestsClosedByLastTile() {
        PlacedTile lastPlacedTile = lastPlacedTile();
        return lastPlacedTile == null
                ? Set.of()
                : lastPlacedTile.forestZones().stream()
                .map(this::forestArea)
                .filter(Area::isClosed)
                .collect(Collectors.toSet());
    }

    /**
     * Gives the set of the rivers that are closed by the last placed tile
     *
     * @return the set of the rivers that are closed by the last placed tile
     */
    public Set<Area<Zone.River>> riversClosedByLastTile() {
        PlacedTile lastPlacedTile = lastPlacedTile();
        return lastPlacedTile == null
                ? Set.of()
                : lastPlacedTile.riverZones().stream()
                .map(this::riverArea)
                .filter(Area::isClosed)
                .collect(Collectors.toSet());
    }

    /**
     * Verify if the placed tile can be added to the board
     *
     * @param tile the placed tile to test
     * @return true if the tile can be added to the board, false otherwise
     */
    public boolean canAddTile(PlacedTile tile) {
        return insertionPositions().contains(tile.pos())
                && Direction.ALL.stream()
                .allMatch(direction -> {
                    PlacedTile neighborTile = tileAt(tile.pos().neighbor(direction));
                    return neighborTile == null
                            || tile.side(direction).isSameKindAs(neighborTile.side(direction.opposite()));
                });
    }

    /**
     * Verify if the tile could be placed on the board
     *
     * @param tile the tile to test
     * @return true if the tile could be placed on the board, false otherwise
     */
    public boolean couldPlaceTile(Tile tile) {
        for (Pos pos : insertionPositions()) {
            for (Rotation rotation : Rotation.ALL) {
                if (canAddTile(new PlacedTile(tile, null, rotation, pos))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gives a new board with the given tile added
     *
     * @param tile the tile to add
     * @return a new board with the given tile added
     * @throws IllegalArgumentException if the tile cannot be added to the board
     */
    public Board withNewTile(PlacedTile tile) {
        Preconditions.checkArgument(tileIndex.length == 0 || canAddTile(tile));

        PlacedTile[] newPlacedTiles = placedTiles.clone();
        int index = getIndexOfTile(tile.pos());
        newPlacedTiles[index] = tile;
        int[] newTileIndex = Arrays.copyOf(tileIndex, tileIndex.length + 1);
        newTileIndex[newTileIndex.length - 1] = index;

        ZonePartitions.Builder newZonePartitionsBuilder = new ZonePartitions.Builder(zonePartitions);
        newZonePartitionsBuilder.addTile(tile.tile());
        for (Direction direction : Direction.ALL) {
            PlacedTile neighborTile = tileAt(tile.pos().neighbor(direction));
            if (neighborTile != null) {
                newZonePartitionsBuilder.connectSides(tile.side(direction), neighborTile.side(direction.opposite()));
            }
        }

        return new Board(newPlacedTiles, newTileIndex, newZonePartitionsBuilder.build(), cancelledAnimals);
    }

    /**
     * Gives a new board with the given occupant added
     *
     * @param occupant the occupant to add
     * @return a new board with the given occupant added
     * @throws IllegalArgumentException if the tile where the occupant is supposed to be is already occupied or the area
     *                                  of the zone is already occupied or if the tile is not in the board
     */
    public Board withOccupant(Occupant occupant) {
        PlacedTile tile = tileFromOccupant(occupant);
        PlacedTile[] newPlacedTiles = placedTiles.clone();
        newPlacedTiles[getIndexOfTile(tile.pos())] = tile.withOccupant(occupant);

        ZonePartitions.Builder newZonePartitionsBuilder = new ZonePartitions.Builder(zonePartitions);
        newZonePartitionsBuilder.addInitialOccupant(tile.placer(), occupant.kind(), zoneFromOccupant(tile, occupant));

        return new Board(newPlacedTiles, tileIndex, newZonePartitionsBuilder.build(), cancelledAnimals);
    }

    /**
     * Gives a new board with the given occupant removed
     *
     * @param occupant the occupant to remove
     * @return a new board with the given occupant removed
     * @throws IllegalArgumentException if the tile where the occupant is supposed to be is not in the board
     */
    public Board withoutOccupant(Occupant occupant) {
        PlacedTile tile = tileWithId(Zone.tileId(occupant.zoneId()));
        PlacedTile[] newPlacedTiles = placedTiles.clone();
        newPlacedTiles[getIndexOfTile(tile.pos())] = tile.withNoOccupant();

        ZonePartitions.Builder newZonePartitionsBuilder = new ZonePartitions.Builder(zonePartitions);
        newZonePartitionsBuilder.removePawn(tile.placer(), zoneFromOccupant(tile, occupant));

        return new Board(newPlacedTiles, tileIndex, newZonePartitionsBuilder.build(), cancelledAnimals);
    }

    /**
     * Gives a new board without gatherers or fishers in the given forests or rivers
     *
     * @param forests the forests to clear
     * @param rivers  the rivers to clear
     * @return a new board without gatherers or fishers in the given forests or rivers
     */
    public Board withoutGatherersOrFishersIn(Set<Area<Zone.Forest>> forests, Set<Area<Zone.River>> rivers) {
        PlacedTile[] newPlacedTiles = placedTiles.clone();
        ZonePartitions.Builder newZonePartitionsBuilder = new ZonePartitions.Builder(zonePartitions);

        Set<Integer> tileIdsToClear = new HashSet<>();
        Set<Integer> zoneIdsToClear = new HashSet<>();

        for (Area<Zone.Forest> forest : forests) {
            newZonePartitionsBuilder.clearGatherers(forest);
            tileIdsToClear.addAll(forest.tileIds());
            forest.zones().forEach(zone -> zoneIdsToClear.add(zone.id()));
        }

        for (Area<Zone.River> river : rivers) {
            newZonePartitionsBuilder.clearFishers(river);
            tileIdsToClear.addAll(river.tileIds());
            river.zones().forEach(zone -> zoneIdsToClear.add(zone.id()));
        }

        for (int id : tileIdsToClear) {
            PlacedTile tile = tileWithId(id);
            Occupant occupant = tile.occupant();
            if (occupant != null
                    && zoneIdsToClear.contains(occupant.zoneId())
                    && occupant.kind() == Occupant.Kind.PAWN
            ) {
                newPlacedTiles[getIndexOfTile(tile.pos())] = tile.withNoOccupant();
            }
        }

        return new Board(newPlacedTiles, tileIndex, newZonePartitionsBuilder.build(), cancelledAnimals);
    }

    /**
     * Gives a new board with the given animals cancelled added in the list of cancelled animals
     *
     * @param newlyCancelledAnimals the animals to add to the list of cancelled animals
     * @return a new board with the given animals cancelled added in the list of cancelled animals
     */
    public Board withMoreCancelledAnimals(Set<Animal> newlyCancelledAnimals) {
        Set<Animal> newCancelledAnimals = new HashSet<>(cancelledAnimals);
        newCancelledAnimals.addAll(newlyCancelledAnimals);

        return new Board(placedTiles, tileIndex, zonePartitions, Collections.unmodifiableSet(newCancelledAnimals));
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof Board thatBoard) {
            return Arrays.equals(placedTiles, thatBoard.placedTiles)
                    && Arrays.equals(tileIndex, thatBoard.tileIndex)
                    && zonePartitions.equals(thatBoard.zonePartitions)
                    && cancelledAnimals.equals(thatBoard.cancelledAnimals);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(placedTiles), Arrays.hashCode(tileIndex), zonePartitions, cancelledAnimals);
    }

    /**
     * Verify if the given position is in the board perimeter
     *
     * @param pos the position to test
     * @return true if the position is in the board perimeter, false otherwise
     */
    private static boolean isPosInBoard(Pos pos) {
        return Math.abs(pos.x()) <= REACH && Math.abs(pos.y()) <= REACH;
    }

    /**
     * Gives the index of the tile in placedTiles at the given position
     *
     * @param pos the position of the tile
     * @return the index of the tile in placedTiles at the given position
     */
    private static int getIndexOfTile(Pos pos) {
        return pos.x() + REACH + (pos.y() + REACH) * WIDTH;
    }

    /**
     * Returns the zone which the occupant is on
     *
     * @param tile     the tile which the occupant is on
     * @param occupant the occupant
     * @return the zone which the occupant is on
     */
    private static Zone zoneFromOccupant(PlacedTile tile, Occupant occupant) {
        return tile.zoneWithId(occupant.zoneId());
    }

    /**
     * Returns the tile which the occupant is on
     *
     * @param occupant the occupant
     * @return the tile which the occupant is on
     */
    private PlacedTile tileFromOccupant(Occupant occupant) {
        return tileWithId(Zone.tileId(occupant.zoneId()));
    }
}

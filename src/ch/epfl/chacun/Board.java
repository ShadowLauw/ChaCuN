package ch.epfl.chacun;

import java.util.*;

/**
 * Represents the board of the game
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */

public final class Board {

    /**
     * The maximum size of the board
     */
    private static final int BOARD_MAX_SIZE = 625;

    /**
     * The index of the origin (0,0) tile
     */
    private static final int INDEX_ORIGIN_TILE = 312;
    /**
     * The reach of the board
     */
    public static final int REACH = 12;
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
     * The Array of the index of the tiles (in the order there were placed)
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
     * @return the tile at the position pos
     */
    public PlacedTile tileAt(Pos pos) {
        return isPosInBoard(pos) ? placedTiles[getIndexOfTile(pos)] : null;
    }
    /**
     * Verify if the given position is in the board perimeter
     * @param pos the position to test
     * @return true if the position is in the board perimeter, false otherwise
     */
    private boolean isPosInBoard(Pos pos) {
        return pos.x() <= REACH && pos.x() >= -REACH && pos.y() <= REACH && pos.y() >= -REACH;
    }
    /**
     * Gives the index of the tile in placedTiles at the given position
     * @param pos the position of the tile
     * @return the index of the tile in placedTiles at the given position
     */
    private int getIndexOfTile(Pos pos) {
        return INDEX_ORIGIN_TILE + pos.x() + pos.y() * WIDTH;
    }

    /**
     * Gives the PlacedTile with the given id
     *
     * @param tileId the id of the tile
     * @return the tile with the given id
     * @throws IllegalArgumentException if there is no tile with the given id
     */
    public PlacedTile tileWithId(int tileId) {
        for (int index : tileIndex) {
            PlacedTile tile = placedTiles[index];
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
        return Collections.unmodifiableSet(cancelledAnimals);
    }

    /**
     * Gives the set of occupants on the board
     *
     * @return the set of occupants on the board
     */
    public Set<Occupant> occupants() {
        Set<Occupant> occupants = new HashSet<>();
        for (int index : tileIndex) {
            PlacedTile tile = placedTiles[index];
            Occupant occupant = tile.occupant();
            if (occupant != null) {
                occupants.add(tile.occupant());
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
     * @return the set of all the meadow areas of the board
     */
    public Set<Area<Zone.Meadow>> meadowAreas() {
        return zonePartitions.meadows().areas();
    }
    /**
     * Gives all the water areas of the board
     * @return the set of all the water areas of the board
     */
    public Set<Area<Zone.Water>> riverSystemAreas() {
        return zonePartitions.riverSystems().areas();
    }

    /**
     * Gives the adjacent meadows of the given meadow
     * @param pos the position fof the given meadow
     * @param meadowZone the meadow zone
     * @return the area of the adjacent meadows
     */
    public Area<Zone.Meadow> adjacentMeadow(Pos pos, Zone.Meadow meadowZone) {
        Area<Zone.Meadow> areaOfTheZone = meadowArea(meadowZone);
        Set<Zone.Meadow> adjacentZones = new HashSet<>();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                Pos posTranslated = pos.translated(dx, dy);
                PlacedTile tile = tileAt(posTranslated);
                if (tile != null) {
                    for (Zone.Meadow zone : tile.meadowZones()) {
                        if (areaOfTheZone.zones().contains(zone)) {
                            adjacentZones.add(zone);
                        }
                    }
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
        int count = 0;
        for (Occupant occupant : occupants()) {
            if (tileWithId(Zone.tileId(occupant.zoneId())).placer().equals(player)
                    && occupant.kind().equals(occupantKind))
            {
                count++;
            }
        }
        return count;
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
            for (Direction direction : Direction.values()) {
                Pos posToTest = pos.neighbor(direction);
                if (isPosInBoard(posToTest) && tileAt(posToTest) == null) {
                    positions.add(posToTest);
                }
            }
        }
        return Set.copyOf(positions);
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
        Set<Area<Zone.Forest>> forests = new HashSet<>();
        PlacedTile lastPlacedTile = lastPlacedTile();
        if (lastPlacedTile != null) {
            for (Zone.Forest forest : lastPlacedTile.forestZones()) {
                Area<Zone.Forest> forestArea = forestArea(forest);
                if (forestArea.isClosed()) {
                    forests.add(forestArea);
                }
            }
        }
        return Set.copyOf(forests);
    }

    /**
     * Gives the set of the rivers that are closed by the last placed tile
     *
     * @return the set of the rivers that are closed by the last placed tile
     */
    public Set<Area<Zone.River>> riversClosedByLastTile() {
        Set<Area<Zone.River>> rivers = new HashSet<>();
        PlacedTile lastPlacedTile = lastPlacedTile();
        if (lastPlacedTile != null) {
            for (Zone.River river : lastPlacedTile.riverZones()) {
                Area<Zone.River> riverArea = riverArea(river);
                if (riverArea.isClosed()) {
                    rivers.add(riverArea);
                }
            }
        }
        return Set.copyOf(rivers);
    }

    /**
     * Verify if the placed tile can be added to the board
     * @param tile the placed tile to test
     * @return true if the tile can be added to the board, false otherwise
     */
    public boolean canAddTile(PlacedTile tile) {
        if (insertionPositions().contains(tile.pos())) {
            for (Direction direction : Direction.values()) {
                PlacedTile neighborTile = tileAt(tile.pos().neighbor(direction));
                if (neighborTile != null) {
                    if (!tile.side(direction).isSameKindAs(neighborTile.side(direction.opposite()))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Verify if the tile could be placed on the board
     * @param tile the tile to test
     * @return true if the tile could be placed on the board, false otherwise
     */
    public boolean couldPlaceTile(Tile tile) {
        for (Pos pos : insertionPositions()) {
            for (Rotation rotation : Rotation.values()) {
                if (canAddTile(new PlacedTile(tile, null, rotation, pos))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gives a new board with the given tile added
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
        for (Direction direction : Direction.values()) {
            Pos pos = tile.pos().neighbor(direction);
            PlacedTile neighborTile = tileAt(pos);
            if (neighborTile != null) {
                newZonePartitionsBuilder.connectSides(tile.side(direction), neighborTile.side(direction.opposite()));
            }
        }

        return new Board(newPlacedTiles, newTileIndex, newZonePartitionsBuilder.build(), cancelledAnimals());
    }

    /**
     * Gives a new board with the given occupant added
     * @param occupant the occupant to add
     * @return a new board with the given occupant added
     * @throws IllegalArgumentException if the tile where the occupant is supposed to be is already occupied or the area
     *                                 of the zone is already occupied
     */
    public Board withOccupant(Occupant occupant) {
        PlacedTile tile = tileWithId(Zone.tileId(occupant.zoneId()));
        PlacedTile[] newPlacedTiles = placedTiles.clone();
        newPlacedTiles[getIndexOfTile(tile.pos())] = tile.withOccupant(occupant);

        ZonePartitions.Builder newZonePartitionsBuilder = new ZonePartitions.Builder(zonePartitions);
        newZonePartitionsBuilder.addInitialOccupant(tile.placer(), occupant.kind(), tile.zoneWithId(occupant.zoneId()));

        return new Board(newPlacedTiles, tileIndex.clone(), newZonePartitionsBuilder.build(), cancelledAnimals());
    }

    /**
     * Gives a new board with the given occupant removed
     * @param occupant the occupant to remove
     * @return a new board with the given occupant removed
     */
    public Board withoutOccupant(Occupant occupant) {
        PlacedTile tile = tileWithId(Zone.tileId(occupant.zoneId()));
        PlacedTile[] newPlacedTiles = placedTiles.clone();
        newPlacedTiles[getIndexOfTile(tile.pos())] = tile.withNoOccupant();

        ZonePartitions.Builder newZonePartitionsBuilder = new ZonePartitions.Builder(zonePartitions);
        newZonePartitionsBuilder.removePawn(tile.placer(), tile.zoneWithId(occupant.zoneId()));

        return new Board(newPlacedTiles, tileIndex.clone(), newZonePartitionsBuilder.build(), cancelledAnimals());
    }

    /**
     * Gives a new board without gatherers or fishers in the given forests or rivers
     * @param forests the forests to clear
     * @param rivers the rivers to clear
     * @return a new board without gatherers or fishers in the given forests or rivers
     */
    public Board withoutGatherersOrFishersIn(Set<Area<Zone.Forest>> forests, Set<Area<Zone.River>> rivers) {
        PlacedTile[] newPlacedTiles = placedTiles.clone();
        ZonePartitions.Builder newZonePartitionsBuilder = new ZonePartitions.Builder(zonePartitions);

        for (Area<Zone.Forest> forest : forests) {
            newZonePartitionsBuilder.clearGatherers(forest);
            for (int id : forest.tileIds()) {
                PlacedTile tile = tileWithId(id);
                if (tile.occupant() != null && tile.occupant().kind() == Occupant.Kind.PAWN)
                    newPlacedTiles[getIndexOfTile(tile.pos())] = tile.withNoOccupant();
            }
        }
        for (Area<Zone.River> river : rivers) {
            newZonePartitionsBuilder.clearFishers(river);
            for (int id : river.tileIds()) {
                PlacedTile tile = tileWithId(id);
                if (tile.occupant() != null && tile.occupant().kind() == Occupant.Kind.PAWN)
                    newPlacedTiles[getIndexOfTile(tile.pos())] = tile.withNoOccupant();
            }
        }

        return new Board(newPlacedTiles, tileIndex.clone(), newZonePartitionsBuilder.build(), cancelledAnimals());
    }

    /**
     * Gives a new board with the given animals cancelled added in the list of cancelled animals
     * @param newlyCancelledAnimals the animals to add to the list of cancelled animals
     * @return a new board with the given animals cancelled added in the list of cancelled animals
     */
    public Board withMoreCancelledAnimals(Set<Animal> newlyCancelledAnimals) {
        Set<Animal> newCancelledAnimals = new HashSet<>(cancelledAnimals);
        newCancelledAnimals.addAll(newlyCancelledAnimals);

        return new Board(placedTiles.clone(), tileIndex.clone(), zonePartitions, newCancelledAnimals);
    }
    
    @Override
    public boolean equals(Object that) {
        if (that == null) {
            return false;
        } else if (that.getClass() == getClass()) {
            Board thatBoard = (Board)that;
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
}

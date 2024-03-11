package ch.epfl.chacun;

import java.util.*;

/**
 * Represents the board of the game
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 *
 */

public class Board {

    /**
     * The reach of the board
     */
    public static final int REACH = 12;
    /**
     * The empty board
     */
    public static final Board EMPTY = new Board(new PlacedTile[625], new int[0], new ZonePartitions(new ZonePartition<Zone.Forest>(), new ZonePartition<Zone.Meadow>(), new ZonePartition<Zone.River>(), new ZonePartition<Zone.Water>()), Set.of());
    /**
     * The width of the board
     */
    private static final int WIDTH = 25;
    /**
     * The Array of the placed tiles
     */
    private PlacedTile[] placedTiles;
    /**
     * The Array of the index of the tiles (in the order there were placed)
     */
    private int[] tileIndex;
    /**
     * The zone partitions of the board
     */
    private ZonePartitions zonePartitions;
    /**
     * The set of the animals that have been cancelled
     */
    private Set<Animal> cancelledAnimals;

    /**
     * Constructs a board with the given placed tiles, tile index, zone partitions and cancelled animals
     *
     * @param placedTiles (PlacedTile[]) the placed tiles
     * @param tileIndex (int[]) the index of the tiles
     * @param zonePartitions (ZonePartitions) the zone partitions
     * @param cancelledAnimals (Set<Animal>) the cancelled animals
     */
    private Board (PlacedTile[] placedTiles, int[] tileIndex, ZonePartitions zonePartitions, Set<Animal> cancelledAnimals) {
        this.placedTiles = placedTiles;
        this.tileIndex = tileIndex;
        this.zonePartitions = zonePartitions;
        this.cancelledAnimals = cancelledAnimals;
    }

    /**
     * Gives the PlacedTile at the position pos on the board
     *
     * @param pos (Pos) the position of the tile
     * @return (PlacedTile) the tile at the position pos
     */
    public PlacedTile tileAt(Pos pos) {
        //index of the tile at 0,0, and then we compute the index of the tile at pos by adding the x and y coordinates
        return placedTiles[312+pos.y()*WIDTH+pos.x()];
    }

    /**
     * Give the PlacedTile with the given id
     *
     * @param id (int) the id of the tile
     * @return (PlacedTile) the tile with the given id
     *
     * @throws IllegalArgumentException if there is no tile with the given id
     */
    public PlacedTile tileWithId(int id) {
        for (PlacedTile tile : placedTiles) {
            if (tile.id() == id) {
                return tile;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Gives the set of cancelled Animals (make a defensive copy)
     * @return (Set<Animal>) the set of cancelled animals
     */
    //copie défensive de cancelledAnimals
    public Set<Animal> cancelledAnimals() {
        return Set.copyOf(cancelledAnimals);
    }

    /**
     * Gives the set of occupants on the board
     * @return (Set<Occupant>) the set of occupants
     */
    public Set<Occupant> occupants() {
        List<Occupant> occupants = new ArrayList<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null) {
                if (tile.occupant() != null) {
                    occupants.add(tile.occupant());
                }
            }
        }
        return Set.copyOf(occupants);
    }

    /**
     * Gives the Area where the given forest is
     * @param forest (Zone.Forest) the forest
     * @return (Area<Zone.Forest>) the area where the forest is
     */
    public Area<Zone.Forest> forestArea(Zone.Forest forest) {
        return (zonePartitions.forests().areaContaining(forest));
    }

    /**
     * Gives the Area where the given meadow is
     * @param meadow (Zone.Meadow) the meadow
     * @return (Area<Zone.Meadow>) the area where the meadow is
     */
    public Area<Zone.Meadow> meadowArea(Zone.Meadow meadow) {
        return (zonePartitions.meadows().areaContaining(meadow));
    }

    /**
     * Gives the Area where the given river is
     * @param river (Zone.River) the river
     * @return (Area<Zone.River>) the area where the river is
     */
    public Area<Zone.River> riverAreas(Zone.River river) {
        return (zonePartitions.rivers().areaContaining(river));
    }

    /**
     * Gives the Area where the given water is
     * @param water (Zone.Water) the water
     * @return (Area<Zone.Water>) the area where the water is
     */
    public Area<Zone.Water> riverSystemAreas(Zone.Water water) {
        return (zonePartitions.riverSystems().areaContaining(water));
    }

    /**
     * Gives the number of the given occupant on the board
     * @param player (PlayerColor) the color of the player
     * @param occupantKind (Occupant.Kind) the kind of the occupant
     * @return (int) the number of the given occupant
     */
    public int occupantCount(PlayerColor player, Occupant.Kind occupantKind) {
        int count = 0;
        for (PlacedTile tile : placedTiles) {
            if (tile != null) {
                if (tile.occupant() != null) {
                    //the pawn that is on the tile has to be of the same color as the placer, because only the placer can place a pawn on a tile
                    if (tile.occupant().kind() == occupantKind && tile.placer() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Gives the set of the positions where a tile can be placed
     * @return (Set<Pos>) the set of the positions where a tile can be placed
     */
    public Set<Pos> insertionPositions() {
        Set<Pos> positions = new HashSet<>();
        for (int index : tileIndex) {
            int x = placedTiles[index].pos().x();
            int y = placedTiles[index].pos().y();

            //we check if one of the tiles that are around is empty, and if yes, we add it to the set
            if(tileAt(new Pos(x+1, y)) == null) {
                positions.add(new Pos(x+1, y));
            }
            if(tileAt(new Pos(x-1, y)) == null) {
                positions.add(new Pos(x-1, y));
            }
            if(tileAt(new Pos(x, y+1)) == null) {
                positions.add(new Pos(x, y+1));
            }
            if(tileAt(new Pos(x, y-1)) == null) {
                positions.add(new Pos(x, y-1));
            }
        }
        return positions;
    }

    /**
     * Gives the last placed Tile
     * @return (PlacedTile) the last placed tile
     */
    public PlacedTile lastPlacedTile() {
        return placedTiles[tileIndex[tileIndex.length-1]];
    }

    /**
     * Gives the set of the forests that are closed by the last placed tile
     * @return (Set<Area<Zone.Forest>>) the set of the forests that are closed by the last placed tile
     */
    public Set<Area<Zone.Forest>> forestsClosedByLastTile() {
        Set<Area<Zone.Forest>> forests = new HashSet<>();
        for (Zone.Forest forest : lastPlacedTile().forestZones()) {
            if (forestArea(forest).isClosed()) {
                forests.add(forestArea(forest));
            }
        }
        return forests;
    }

    /**
     * Gives the set of the rivers that are closed by the last placed tile
     * @return (Set<Area<Zone.River>>) the set of the rivers that are closed by the last placed tile
     */
    public Set<Area<Zone.River>> riversClosedByLastTile() {
        Set<Area<Zone.River>> rivers = new HashSet<>();
        for (Zone.River river : lastPlacedTile().riverZones()) {
            if (riverAreas(river).isClosed()) {
                rivers.add(riverAreas(river));
            }
        }
        return rivers;
    }

    //A partir de ici c'est la partie de Laura
    public boolean canAddTile(PlacedTile tile) {
        return false;
    }

    public boolean couldPlaceTile(Tile tile) {
        return false;
    }

    public Board withNewTile(PlacedTile tile) {
        return null;
    }

    public Board withOccupant(Occupant occupant) {
        return null;
    }

    public Board withoutOccupant(Occupant occupant) {
        return null;
    }

    public Board withoutGatherersOrFisherIn(Set<Area<Zone.Forest>> forests, Set<Area<Zone.River>> rivers) {
        return null;
    }

    public Board withMoreCancelledAnimals(Set<Animal> newlyCancelledAnimals) {
        return null;
    }

    @Override
    public boolean equals (Object that) {
        return false;
    }

    @Override
    public int hashCode () {
      return 0;
    }



}

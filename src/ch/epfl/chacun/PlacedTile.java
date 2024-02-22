package ch.epfl.chacun;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a tile placed on the board
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 *
 * @param tile (Tile) the tile placed
 * @param placer (PlayerColor) the player who placed the tile
 * @param rotation (Rotation) the rotation of the tile
 * @param pos (Pos) the position of the tile
 * @param occupant (Occupant) the occupant of the tile
 */
public record PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos, Occupant occupant) {
    /**
     * Constructs a placed tile (with verification of the parameters)
     * @param tile (Tile) the tile placed
     * @param placer (PlayerColor) the player who placed the tile
     * @param rotation (Rotation) the rotation of the tile
     * @param pos (Pos) the position of the tile
     * @param occupant (Occupant) the occupant of the tile (null if there is no occupant)
     *
     * @throws IllegalArgumentException if the tile, the rotation or the position are null
     */
    public PlacedTile {
        Preconditions.checkArgument(tile != null && rotation != null && pos != null);
    }
    /**
     * Constructs a placed tile without an occupant (with verification of the parameters)
     * @param tile (Tile) the tile placed
     * @param placer (PlayerColor) the player who placed the tile
     * @param rotation (Rotation) the rotation of the tile
     * @param pos (Pos) the position of the tile
     *
     * @throws IllegalArgumentException if the tile, the rotation or the position are null
     */
    public PlacedTile (Tile tile, PlayerColor placer, Rotation rotation, Pos pos) {
        this(tile, placer, rotation, pos, null);
    }
    /**
     * Returns the id of the tile
     * @return (int) the id of the tile
     */
    public int id() {
        return tile.id();
    }
    /**
     * Returns the kind of the tile
     * @return (Tile.Kind) the kind of the tile
     */
    public Tile.Kind kind() {
        return tile.kind();
    }
    /**
     * Returns the side of the tile in the given direction
     * @param direction (Direction) the direction of the side
     * @return (TileSide) the side of the tile in the given direction
     */
    //ici en vrai de vrai c'est un peu bancal mais je vois pas d'autres moyen de le faire autrement malheureusement...
    public TileSide side(Direction direction) {
        Direction dir = direction.rotated(this.rotation);
        switch (dir) {
            case E -> {
                return tile.e();
            }
            case S -> {
                return tile.s();
            }
            case W -> {
                return tile.w();
            }
            case N -> {
                return tile.n();
            }
            default -> {
                throw new IllegalArgumentException();
            }
        }
    }
    /**
     * Returns the zone of the tile corresponding with the given id
     * @param id (int) the id of the zone to search
     * @return (Set<Zone>) the zones of the tile
     * @throws IllegalArgumentException if the zone with the given id is not found
     */
    public Zone zoneWithId(int id) {
        for (Zone zone : tile.zones()) {
            if(zone.id() == id) {
                return zone;
            }
        };
        throw new IllegalArgumentException();
    }

    /**
     * Returns, if there is one, the zone with a special power of the Tile
     * @return (Zone) the zone with a special power of the Tile (if there is one)
     */
    public Zone specialPowerZone() {
        for (Zone zone : tile.zones()) {
            if(zone.specialPower() != null){
                return zone;
            }
        }
        return null;
    }

    /**
     * Return the forest zones of the tile
     * @return (Set<Zone.Forest>) the forests zone of the tile
     */
    public Set<Zone.Forest> forestZones() {
        Set<Zone.Forest> forests = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if(zone instanceof Zone.Forest forest) {
                forests.add(forest);
            }
        }
        return forests;
    }
    /**
     * Return the meadow zones of the tile
     * @return (Set<Zone.Meadow>) the meadow zone of the tile
     */
    public Set<Zone.Meadow> meadowZones() {
        Set<Zone.Meadow> meadows = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if(zone instanceof Zone.Meadow meadow) {
                meadows.add(meadow);
            }
        }
        return meadows;
    }
    /**
     * Return the river zone of the tile
     * @return (Set<Zone.River>) the river zone of the tile
     */
    public Set<Zone.River> riverZones() {
        Set<Zone.River> rivers = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if(zone instanceof Zone.River river) {
                rivers.add(river);
            }
        }
        return rivers;
    }
    /**
     * Return the potential occupants of the tile
     * @return (Set<Occupant>) the list of the potential occupants of the tile
     */
    public Set<Occupant> potentialOccupants() {
        Set<Occupant> occupants = new HashSet<>();
        if(this.placer == null) {
            return occupants;
        } else {
            for (Zone zone : tile.zones()) {
                //the player can place a hut on the lake if there is one
                if (zone instanceof Zone.Lake lake)
                    occupants.add(new Occupant(Occupant.Kind.HUT, lake.id()));
                else {
                    //the player can place a pawn on each sideZone of the tile
                    occupants.add(new Occupant(Occupant.Kind.PAWN, zone.id()));
                    //the player can place a hut on each river if it is not connected to a lake
                    if (zone instanceof Zone.River river && !river.hasLake())
                        occupants.add(new Occupant(Occupant.Kind.HUT, river.id()));
                }
            }
        }
        return occupants;
    }
    /**
     * Returns the same tile but with a given occupant
     * @param occupant (Occupant) the occupant to add
     * @return (PlacedTile) the tile with the given occupant
     * @throws IllegalArgumentException if the tile already has an occupant
     */
    public PlacedTile withOccupant(Occupant occupant) {
        Preconditions.checkArgument(this.occupant != null);
        return new PlacedTile(tile, placer, rotation, pos, occupant);
    }
    /**
     * Returns the same tile but with no occupant
     * @return (PlacedTile) the tile with no occupant
     */
    public PlacedTile withNoOccupant() {
        return new PlacedTile(tile, placer, rotation, pos, null);
    }
    /**
     * Returns the id of the zone occupied by the given occupant kind
     * @param occupantKind (Occupant.Kind) the kind of the occupant
     * @return (int) the id of the zone occupied by the given occupant kind (or -1 if there is no such zone)
     */
    public int idOfZoneOccupiedBy(Occupant.Kind occupantKind) {
        if (this.occupant == null || this.occupant.kind() != occupantKind) 
            return -1;
        return this.occupant.zoneId();
    }
}

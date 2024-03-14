package ch.epfl.chacun;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Represents a tile placed on the board
 *
 * @param tile     the tile to place
 * @param placer   the player who placed the tile
 * @param rotation the rotation of the tile
 * @param pos      the position of the tile
 * @param occupant (Occupant) the occupant of the tile
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public record PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos, Occupant occupant) {
    /**
     * Constructs a placed tile (with verification of the parameters)
     *
     * @param tile     the tile placed
     * @param placer   the player who placed the tile
     * @param rotation the rotation of the tile
     * @param pos      the position of the tile
     * @param occupant the occupant of the tile (null if there is no occupant)
     * @throws NullPointerException if the tile, the rotation or the position are null
     */
    public PlacedTile {
        requireNonNull(tile);
        requireNonNull(rotation);
        requireNonNull(pos);
    }

    /**
     * Constructs a placed tile without an occupant (with verification of the parameters)
     *
     * @param tile     the tile placed
     * @param placer   the player who placed the tile
     * @param rotation the rotation of the tile
     * @param pos      the position of the tile
     * @throws NullPointerException if the tile, the rotation or the position are null
     */
    public PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos) {
        this(tile, placer, rotation, pos, null);
    }

    /**
     * Returns the id of the tile
     *
     * @return the id of the tile
     */
    public int id() {
        return tile.id();
    }

    /**
     * Returns the kind of the tile
     *
     * @return the kind of the tile
     */
    public Tile.Kind kind() {
        return tile.kind();
    }

    /**
     * Returns the TileSide in the given direction
     *
     * @param direction the direction of the side
     * @return the TileSide in the given direction
     */
    public TileSide side(Direction direction) {
        Direction dir = direction.rotated(this.rotation.negated());
        return switch (dir) {
            case E -> tile.e();
            case S -> tile.s();
            case W -> tile.w();
            case N -> tile.n();
        };
    }

    /**
     * Returns the zone of the tile corresponding with the given id
     *
     * @param id the id of the zone to search
     * @return the zones of the tile
     * @throws IllegalArgumentException if the zone with the given id is not found
     */
    public Zone zoneWithId(int id) {
        for (Zone zone : tile.zones()) {
            if (zone.id() == id)
                return zone;
        }
        ;
        throw new IllegalArgumentException();
    }

    /**
     * Returns, if there is one, the zone with a special power of the Tile
     *
     * @return the zone with a special power of the Tile (if there is one) otherwise null
     */
    public Zone specialPowerZone() {
        for (Zone zone : tile.zones()) {
            if (zone.specialPower() != null)
                return zone;

        }
        return null;
    }

    /**
     * Return the forest zones of the tile
     *
     * @return the forests zone of the tile
     */
    public Set<Zone.Forest> forestZones() {
        Set<Zone.Forest> forests = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if (zone instanceof Zone.Forest forest)
                forests.add(forest);
        }
        return forests;
    }

    /**
     * Return the meadow zones of the tile
     *
     * @return the meadow zone of the tile
     */
    public Set<Zone.Meadow> meadowZones() {
        Set<Zone.Meadow> meadows = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if (zone instanceof Zone.Meadow meadow)
                meadows.add(meadow);
        }
        return meadows;
    }

    /**
     * Return the river zone of the tile
     *
     * @return the river zone of the tile
     */
    public Set<Zone.River> riverZones() {
        Set<Zone.River> rivers = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if (zone instanceof Zone.River river)
                rivers.add(river);
        }
        return rivers;
    }

    /**
     * Return the potential occupants of the tile
     *
     * @return the set of the potential occupants of the tile
     */
    public Set<Occupant> potentialOccupants() {
        Set<Occupant> occupants = new HashSet<>();
        if (this.placer == null) {
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
     *
     * @param occupant the occupant to add
     * @return the tile with the given occupant
     * @throws IllegalArgumentException if the tile already has an occupant
     */
    public PlacedTile withOccupant(Occupant occupant) {
        Preconditions.checkArgument(this.occupant == null);
        return new PlacedTile(tile, placer, rotation, pos, occupant);
    }

    /**
     * Returns the same tile but with no occupant
     *
     * @return the tile with no occupant
     */
    public PlacedTile withNoOccupant() {
        return new PlacedTile(tile, placer, rotation, pos, null);
    }

    /**
     * Returns the id of the zone occupied by the given occupant kind
     *
     * @param occupantKind the kind of the occupant
     * @return the id of the zone occupied by the given occupant kind (or -1 if there is no such zone)
     */
    public int idOfZoneOccupiedBy(Occupant.Kind occupantKind) {
        if (this.occupant == null || this.occupant.kind() != occupantKind)
            return -1;
        return this.occupant.zoneId();
    }
}

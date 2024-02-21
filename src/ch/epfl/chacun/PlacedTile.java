package ch.epfl.chacun;

import java.util.HashSet;
import java.util.Set;

public record PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos, Occupant occupant) {
    public PlacedTile {
        Preconditions.checkArgument(tile != null && rotation != null && pos != null );
    }
    public PlacedTile (Tile tile, PlayerColor placer, Rotation rotation, Pos pos) {
        this(tile, placer, rotation, pos, null);
    }
    public int id() {
        return tile.id();
    }
    public Tile.Kind kind() {
        return tile.kind();
    }
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
    public Zone zoneWithId(int id) {
        for (Zone zone : tile.zones()) {
            if(zone.id() == id) {
                return zone;
            }
        };
        throw new IllegalArgumentException();
    }
    public  Zone specialPowerZone() {
        for (Zone zone : tile.zones()) {
            if(zone.specialPower() != null){
                return zone;
            }
        }
        return null;
    }
    public Set<Zone.Forest> forestZones() {
        Set<Zone.Forest> forests = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if(zone instanceof Zone.Forest forest) {
                forests.add(forest);
            }
        }
        return forests;
    }
    public Set<Zone.Meadow> meadowZones() {
        Set<Zone.Meadow> meadows = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if(zone instanceof Zone.Meadow meadow) {
                meadows.add(meadow);
            }
        }
        return meadows;
    }
    public Set<Zone.River> riverZones() {
        Set<Zone.River> rivers = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if(zone instanceof Zone.River river) {
                rivers.add(river);
            }
        }
        return rivers;
    }
    public Set<Occupant> potentialOccupants() {
        Set<Occupant> occupants = new HashSet<>();
        if(this.placer == null) {
            return occupants;
        } else {
            for (Zone zone : tile.sideZones()) {
                occupants.add(new Occupant(Occupant.Kind.PAWN, zone.id()));
            }
            for (Zone zone : tile.zones()) {
                switch(zone) {
                    case Zone.River river -> {
                        if(!river.hasLake()) {
                            occupants.add(new Occupant(Occupant.Kind.PAWN, river.id()));

                        }
                    }
                    case Zone.Lake lake -> {
                        occupants.add(new Occupant(Occupant.Kind.HUT, lake.id()));
                    }
                    default -> {}
                }
            }
            return occupants;
        }
    }
    public PlacedTile withOccupant(Occupant occupant) {
        Preconditions.checkArgument(this.occupant != null);
        return new PlacedTile(tile, placer, rotation, pos, occupant);
    }
    public PlacedTile withNoOccupant() {
        return new PlacedTile(tile, placer, rotation, pos, null);
    }
    //ici il faut voir si tester la condition de nullité à chaque fois c'est redondant ou si ça passe.
    public int idOfZoneOccupiedBy(Occupant.Kind occupantKind) {
        for (Zone zone : tile.zones()) {
            if (this.occupant != null && this.occupant.kind() == occupantKind) {
                return this.occupant.zoneId();
            }
        }
        return -1;
    }
}

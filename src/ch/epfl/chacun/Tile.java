package ch.epfl.chacun;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the different tiles of the board
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 *
 * @param id (int) The unique identifier of the tile
 * @param kind (Kind) The kind of the tile
 * @param n (TileSide) The north side of the tile
 * @param e (TileSide) The east side of the tile
 * @param s (TileSide) The south side of the tile
 * @param w (TileSide) The west side of the tile
 */
public record Tile(int id, Kind kind, TileSide n, TileSide e, TileSide s, TileSide w) {
    /**
     * Represents the different kinds of tiles
     */
    public enum Kind {
        START,
        NORMAL,
        MENHIR;
    }
    /**
     * Returns the sides of the current tile
     * @return (List<TileSide>) a list of TileSide representing the sides of the current tile
     */
    public List<TileSide> sides() {
        return List.of(n, e, s, w);
    }
    /**
     * Returns the zones of the current tile
     * @return (Set<Zone>) a set of Zone representing the zones of the current tile
     */
    public Set<Zone> sideZones() {
        Set<Zone> sideZones = new HashSet<>();
        sideZones.addAll(n.zones());
        sideZones.addAll(e.zones());
        sideZones.addAll(s.zones());
        sideZones.addAll(w.zones());
        return sideZones;
    }
    /**
     * Returns the zones of the current tile
     * @return (Set<Zone>) a set of Zone representing the zones of the current tile
     */
    public Set<Zone> zones() {
        Set<Zone> zones = sideZones();
        Set<Zone> result = new HashSet<>(zones);
        for (Zone zone : zones) {
            if (zone instanceof Zone.River river) {
                if (river.hasLake())
                    result.add(river.lake());
            }
        }
        return result;
    }
}

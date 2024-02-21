package ch.epfl.chacun;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record Tile(int id, Kind kind, TileSide n, TileSide e, TileSide s, TileSide w) {
    public enum Kind {
        START,
        NORMAL,
        MENHIR;
    }

    public List<TileSide> sides() {
        return List.of(n, e, s, w);
    }
    public Set<Zone> sideZones() {
        Set<Zone> sideZones = new HashSet<>();
        sideZones.addAll(n.zones());
        sideZones.addAll(e.zones());
        sideZones.addAll(s.zones());
        sideZones.addAll(w.zones());
        return sideZones;
    }
    public Set<Zone> zones() {
        Set<Zone> zones = sideZones();
        for (Zone zone: sideZones()) {
            if(zone instanceof Zone.River river){
                zones.add(river.lake());
            }
        }
        return zones;
    }
}

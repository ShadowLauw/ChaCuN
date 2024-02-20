package ch.epfl.chacun;

import java.util.List;

public sealed interface Zone {
    int TILE_ID_DIVIDER = 10;
    enum SpecialPower {
        SHAMAN,
        LOGBOAT,
        HUNTING_TRAP,
        PIT_TRAP,
        WILD_FIRE,
        RAFT
    }

    static int tileId(int zoneId) {
        return zoneId / TILE_ID_DIVIDER;
    }
    static int localId(int zoneId) {
        return zoneId % TILE_ID_DIVIDER;
    }
    int id();
    default int tileId() {
        return id() / TILE_ID_DIVIDER;
    }
    default int localId() {
        return id() % TILE_ID_DIVIDER;
    }
    default SpecialPower specialPower() {
        return null;
    }

    record Forest(int id, Kind kind) implements Zone {
        public enum Kind {PLAIN, WITH_MENHIR, WITH_MUSHROOMS}
    }

    record Meadow(int id, List<Animal> animals, SpecialPower specialPower) implements Zone {
        public Meadow {
            animals = List.copyOf(animals);
        }
    }

    sealed interface Water extends Zone {
        int fishCount();
    }

    record Lake(int id, int fishCount, SpecialPower specialPower) implements Water {
    }

    record River(int id, int fishCount, Lake lake) implements Water {
        public boolean hasLake() {
            return lake != null;
        }
    }
}

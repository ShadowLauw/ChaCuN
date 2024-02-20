package ch.epfl.chacun;

public record Animal(int id, Kind kind) {

    static final int TILE_ID_DIVIDER = 100;

    public enum Kind {MAMMOTH, AUROCHS, DEER, TIGER}

    public int tileId() {
        return id / TILE_ID_DIVIDER;
    }
}

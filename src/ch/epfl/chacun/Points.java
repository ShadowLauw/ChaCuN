package ch.epfl.chacun;

public final class Points {
    private Points() {}

    public int forClosedForest (int tileCount, int mushroomGroupCount) {
        if(tileCount < 1 || mushroomGroupCount < 0)
            throw new IllegalArgumentException();
    };

    public int forClosedRiver (int tileCount, int fishCount) {
        if(tileCount < 1 || fishCount < 0)
            throw new IllegalArgumentException();
    };

    public int forMeadow (int mammothCount, int aurochsCount, int deerCount) {
        if(mammothCount < 0 || aurochsCount < 0 || deerCount < 0)
            throw new IllegalArgumentException();
    };
    public int forRiverSystem (int fishCount) {
        if(fishCount < 0)
            throw new IllegalArgumentException();
    };
    public int forLogboat (int lakeCount) {
        if(lakeCount < 0)
            throw new IllegalArgumentException();
    };
    public int forRaft (int lakeCount) {
        if(lakeCount < 0)
            throw new IllegalArgumentException();
    };
}

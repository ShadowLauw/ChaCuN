package ch.epfl.chacun;

import static ch.epfl.chacun.Preconditions.checkArgument;

public final class Points {
    private Points() {}
    public static int forClosedForest (int tileCount, int mushroomGroupCount) {
        checkArgument(tileCount > 1 && mushroomGroupCount >= 0);
        return (2*tileCount + 3*mushroomGroupCount);
    };

    public static int forClosedRiver (int tileCount, int fishCount) {
        checkArgument(tileCount > 1 && fishCount >= 0);
        return (tileCount + fishCount);
    };

    public static int forMeadow (int mammothCount, int aurochsCount, int deerCount) {
        checkArgument(mammothCount >= 0 && aurochsCount >= 0 && deerCount >= 0);
        return (3*mammothCount + 2*aurochsCount + deerCount);
    };
    public static int forRiverSystem (int fishCount) {
        checkArgument(fishCount >= 0);
        return (fishCount);
    };
    public static int forLogboat (int lakeCount) {
        checkArgument(lakeCount > 0);
        return (2*lakeCount);
    };
    public static int forRaft (int lakeCount) {
        checkArgument(lakeCount > 0);
        return (lakeCount);
    };
}

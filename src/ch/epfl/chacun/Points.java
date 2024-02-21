package ch.epfl.chacun;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * immutable class for computing the points for some events during a game.
 */
public final class Points {
    /**
     * Private constructor to prevent instantiation.
     */
    private Points() {}

    /**
     * Returns the number of points for a closed Forest.
     * @param tileCount the number a tiles that constitue the forest
     * @param mushroomGroupCount the number of mushroomGroups present in the forest
     * @return the number of points for the given number of tiles and mushroom groups in a closed forest
     */
    public static int forClosedForest (int tileCount, int mushroomGroupCount) {
        checkArgument(tileCount > 1 && mushroomGroupCount >= 0);
        return (2*tileCount + 3*mushroomGroupCount);
    };

    /**
     * Returns the number of points for a closed River.
     * @param tileCount the numbers a tiles that constitue the river
     * @param fishCount the number of fish present in the river (or the lacs at the extreities)
     * @return the number of points for the given number of tiles and fish in a closed river
     */
    public static int forClosedRiver (int tileCount, int fishCount) {
        checkArgument(tileCount > 1 && fishCount >= 0);
        return (tileCount + fishCount);
    };

    /**
     * Returns the number of points for a closed Meadow.
     * @param mammothCount
     * @param aurochsCount
     * @param deerCount
     * @return the number of points for the given number of mammoths, aurochs and deers in a closed meadow
     */
    public static int forMeadow (int mammothCount, int aurochsCount, int deerCount) {
        checkArgument(mammothCount >= 0 && aurochsCount >= 0 && deerCount >= 0);
        return (3*mammothCount + 2*aurochsCount + deerCount);
    };
    /**
     * Returns the number of points for a closed river System.
     * @param fishCount the number of fish present in the river System
     * @return the number of points for the given number of fish in the river System
     */
    public static int forRiverSystem (int fishCount) {
        checkArgument(fishCount >= 0);
        return (fishCount);
    };
    /**
     * Returns the number of points for the event Logboat.
     * @param lakeCount the number of lakes present in the river system where the Logboat is placed
     * @return the number of points for the number of lakes present in the river system where the Logboat is placed
     */
    public static int forLogboat (int lakeCount) {
        checkArgument(lakeCount > 0);
        return (2*lakeCount);
    };
    /**
     * Returns the number of points for the event Raft.
     * @param lakeCount the number of lakes present in the river system where the Raft is placed
     * @return the number of points for the number of lakes present in the river system where the Raft is placed
     */
    public static int forRaft (int lakeCount) {
        checkArgument(lakeCount > 0);
        return (lakeCount);
    };
}

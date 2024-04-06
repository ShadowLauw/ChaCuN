package ch.epfl.chacun;

/**
 * Computes the points for some events during a game.
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class Points {
    /**
     * Private constructor to prevent instantiation.
     */
    private Points() {
    }

    /**
     * Returns the number of points for a closed Forest.
     *
     * @param tileCount          the number of tiles that constitute the forest
     * @param mushroomGroupCount the number of mushroomGroups present in the forest
     * @return the number of points for the given number of tiles and mushroom groups in a closed forest
     * @throws IllegalArgumentException if the tileCount is not strictly superior to 1 or if the mushroomGroupCount is negative
     */
    public static int forClosedForest(int tileCount, int mushroomGroupCount) {
        Preconditions.checkArgument(tileCount > 1 && mushroomGroupCount >= 0);

        return 2 * tileCount + 3 * mushroomGroupCount;
    }

    /**
     * Returns the number of points for a closed River.
     *
     * @param tileCount the numbers a tiles that constitute the river
     * @param fishCount the number of fish present in the river
     * @return the number of points for the given number of tiles and fish in a closed river
     * @throws IllegalArgumentException if the tileCount is not strictly superior to 1 or if the fishCount is negative
     */
    public static int forClosedRiver(int tileCount, int fishCount) {
        Preconditions.checkArgument(tileCount > 1 && fishCount >= 0);

        return tileCount + fishCount;
    }

    /**
     * Returns the number of points for a meadow.
     *
     * @param mammothCount the number of mammoths present in the meadow
     * @param aurochsCount the number of aurochs present in the meadow
     * @param deerCount    the number of deers present in the meadow
     * @return the number of points for the given number of mammoths, aurochs and deers in the meadow
     * @throws IllegalArgumentException if the mammothCount, aurochsCount or deerCount is negative
     */
    public static int forMeadow(int mammothCount, int aurochsCount, int deerCount) {
        Preconditions.checkArgument(mammothCount >= 0 && aurochsCount >= 0 && deerCount >= 0);

        return 3 * mammothCount + 2 * aurochsCount + deerCount;
    }

    /**
     * Returns the number of points for a closed river System.
     *
     * @param fishCount the number of fish present in the river System
     * @return the number of points for the given number of fish in the river System
     * @throws IllegalArgumentException if the fishCount is negative
     */
    public static int forRiverSystem(int fishCount) {
        Preconditions.checkArgument(fishCount >= 0);

        return fishCount;
    }

    /**
     * Returns the number of points for the event Logboat.
     *
     * @param lakeCount the number of lakes present in the river system where the Logboat is placed
     * @return the number of points for the number of lakes present in the river system where the Logboat is placed
     * @throws IllegalArgumentException if the lakeCount is not strictly superior to 0
     */
    public static int forLogboat(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);

        return 2 * lakeCount;
    }

    /**
     * Returns the number of points for the event Raft.
     *
     * @param lakeCount the number of lakes present in the river system where the Raft is placed
     * @return the number of points for the number of lakes present in the river system where the Raft is placed
     * @throws IllegalArgumentException if the lakeCount is not strictly superior to 0
     */
    public static int forRaft(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);

        return lakeCount;
    }
}

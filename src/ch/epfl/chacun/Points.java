package ch.epfl.chacun;

public final class Points {
    private Points() {}

    public int forClosedForest (int tileCount, int mushroomGroupCount) {
        checkArgument(1, tileCount, mushroomGroupCount);
        return (2*tileCount + 3*mushroomGroupCount);
    };

    public int forClosedRiver (int tileCount, int fishCount) {
        checkArgument(1, tileCount, fishCount);
        return (tileCount + fishCount);
    };

    public int forMeadow (int mammothCount, int aurochsCount, int deerCount) {
        checkArgument(0, mammothCount, aurochsCount, deerCount);
        return (3*mammothCount + 2*aurochsCount + deerCount);
    };
    public int forRiverSystem (int fishCount) {
        checkArgument(0, fishCount);
        return (fishCount);
    };
    public int forLogboat (int lakeCount) {
        checkArgument(0, lakeCount);
        return (2*lakeCount);
    };
    public int forRaft (int lakeCount) {
        checkArgument(0, lakeCount);
        return (lakeCount);
    };
    /**
     * This is a method to check if the arguments of the Point class are valid
     * @param value the value to check for the first argument
     * @param arguments the list of argument, the first argument is going to be checked against the value, and the other must be positive (or null)
     */
    private void checkArgument (int value, int... arguments) {
        if(arguments.length < 1 || arguments[0] < value)
            throw new IllegalArgumentException();
        for(int i = 1; i < arguments.length; i++) {
            if(arguments[i] < 0)
                throw new IllegalArgumentException();
        }
    }
}

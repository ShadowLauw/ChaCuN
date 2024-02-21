package ch.epfl.chacun;

/**
 * Utility immutable class for checking preconditions.
 *
 * @author Emmanuel Omont  (372632)
 * @ author Laura Paraboschi (364161)
 */
public final class Preconditions {
    /**
     * Private constructor to prevent instantiation.
     */
    private Preconditions() {}
    /**
     * Throws an IllegalArgumentException if the given boolean is false.
     *
     * @param shouldBeTrue the boolean to check
     * @throws IllegalArgumentException if the given boolean is false
     */
    public static void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue)
            throw new IllegalArgumentException();
    }
}

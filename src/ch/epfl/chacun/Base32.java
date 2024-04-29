package ch.epfl.chacun;

public final class Base32 {

    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int BIT_PER_CHAR = 5;
    private static final int MASK_5BITS = 0b11111;
    private static final int MAX_NUMBER_OF_CHAR = 2;

    private Base32() {}

    public static boolean isValid(String stringToTest) {
        return stringToTest.chars().allMatch(c -> ALPHABET.indexOf(c) != -1);
    }

    public static String encodeBits5(int value) {
        return String.valueOf(ALPHABET.charAt(value & MASK_5BITS));
    }

    public static String encodeBits10(int value) {
        return encodeBits5(value >> BIT_PER_CHAR) + encodeBits5(value);
    }

    public static int decode(String encoded) {
        Preconditions.checkArgument(!encoded.isEmpty() && encoded.length() <= MAX_NUMBER_OF_CHAR);
        int decoded = 0;
        for (int i = 0; i < encoded.length(); ++i) {
            decoded = (decoded << BIT_PER_CHAR) | ALPHABET.indexOf(encoded.charAt(i));
        }

        return decoded;
    }
}

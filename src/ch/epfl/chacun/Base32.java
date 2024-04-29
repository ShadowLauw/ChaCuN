package ch.epfl.chacun;

/**
 * Represents a base 32 encoding/decoding utility
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class Base32 {
    /**
     * The Base32 alphabet used for encoding/decoding
     */
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    /**
     * The number of bits per character in Base32
     */
    private static final int BITS_PER_CHAR = 5;
    /**
     * The mask to get the 5 least significant bits of a number
     */
    private static final int MASK_5BITS = 0b11111;
    /**
     * The maximum number of characters in our Base32 strings
     */
    private static final int MAX_NUMBER_OF_CHAR = 2;

    /**
     * Private constructor to prevent instantiation
     */
    private Base32() {
    }

    /**
     * Checks if a string is a valid Base32 string
     *
     * @param stringToTest the string to test
     * @return true if the string is a valid Base32 string, false otherwise
     */
    public static boolean isValid(String stringToTest) {
        return stringToTest.chars().allMatch(c -> ALPHABET.indexOf((char) c) != -1);
    }

    /**
     * Encodes the 5 least significant bits of a number into a Base32 character
     *
     * @param value the number to encode
     * @return the Base32 String representation of the 5 least significant bits of the number
     */
    public static String encodeBits5(int value) {
        return String.valueOf(ALPHABET.charAt(value & MASK_5BITS));
    }

    /**
     * Encodes the 10 least significant bits of a number into a Base32 string
     *
     * @param value the number to encode
     * @return the Base32 String representation of the 10 least significant bits of the number
     */
    public static String encodeBits10(int value) {
        return encodeBits5(value >> BITS_PER_CHAR) + encodeBits5(value);
    }

    /**
     * Decodes a Base32 string of length 1 or 2 into a number
     *
     * @param encoded the Base32 string to decode
     * @return the number represented by the Base32 string
     * @throws IllegalArgumentException if the string is empty or has more than 2 characters
     */
    public static int decode(String encoded) {
        Preconditions.checkArgument(!encoded.isEmpty() && encoded.length() <= MAX_NUMBER_OF_CHAR);
        int decoded = 0;
        for (int i = 0; i < encoded.length(); ++i) {
            decoded = (decoded << BITS_PER_CHAR) | ALPHABET.indexOf(encoded.charAt(i));
        }

        return decoded;
    }
}
